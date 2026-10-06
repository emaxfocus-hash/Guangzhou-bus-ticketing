#!/usr/bin/env bash
set -euo pipefail

# Local build & signing helper for Guangzhou-bus-ticketing
# Usage:
#   KEYSTORE_PATH=./my-release-key.jks KEYSTORE_PASSWORD=... KEY_ALIAS=gzbuskey KEY_PASSWORD=... ./scripts/local_sign_and_build.sh [--apk|--aab|--both] [--skip-verify]
#
# Options:
#   --apk, -a        Produce only APK (assembleRelease)
#   --aab, -b        Produce only AAB (bundleRelease)
#   --both, -c       Produce both APK and AAB (default)
#   --skip-verify, -s  Skip signature verification and fallback signing
#   --help, -h       Show this help message

show_help() {
  sed -n '1,220p' "$0"
  exit 0
}

# Default: build both
BUILD_APK=true
BUILD_AAB=true
SKIP_VERIFY=false

# Parse args
while [ $# -gt 0 ]; do
  case "$1" in
    --apk|-a)
      BUILD_APK=true
      BUILD_AAB=false
      shift
      ;;
    --aab|-b)
      BUILD_APK=false
      BUILD_AAB=true
      shift
      ;;
    --both|-c)
      BUILD_APK=true
      BUILD_AAB=true
      shift
      ;;
    --skip-verify|-s)
      SKIP_VERIFY=true
      shift
      ;;
    --help|-h)
      show_help
      ;;
    *)
      echo "Unknown option: $1"
      echo "Run with --help to see usage"
      exit 1
      ;;
  esac
done

# Helper: prompt for value if not set in environment
prompt_if_empty() {
  local var_name="$1"
  local prompt="$2"
  if [ -z "${!var_name:-}" ]; then
    read -rp "$prompt: " val
    export "$var_name"="$val"
  fi
}

# Ensure we're in repo root (script assumes repo root)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$SCRIPT_DIR/.."
cd "$REPO_ROOT"

# Gather required signing env vars (allow passing them in environment)
if [ -z "${KEYSTORE_PATH:-}" ]; then
  echo "KEYSTORE_PATH not set. Defaulting to ./my-release-key.jks"
  export KEYSTORE_PATH="$PWD/my-release-key.jks"
fi

prompt_if_empty KEYSTORE_PASSWORD "Enter KEYSTORE_PASSWORD"
prompt_if_empty KEY_ALIAS "Enter KEY_ALIAS"
prompt_if_empty KEY_PASSWORD "Enter KEY_PASSWORD"

# Export them for Gradle signingConfig (app/build.gradle uses System.getenv)
export KEYSTORE_PATH
export KEYSTORE_PASSWORD
export KEY_ALIAS
export KEY_PASSWORD

echo "Using keystore: $KEYSTORE_PATH"
if [ ! -f "$KEYSTORE_PATH" ]; then
  echo "ERROR: keystore file not found at $KEYSTORE_PATH"
  exit 1
fi

# Determine gradle tasks to run
GRADLE_TASKS=()
if [ "$BUILD_APK" = true ]; then
  GRADLE_TASKS+=("assembleRelease")
fi
if [ "$BUILD_AAB" = true ]; then
  GRADLE_TASKS+=("bundleRelease")
fi

echo "Running Gradle release build: ${GRADLE_TASKS[*]} ..."
./gradlew clean "${GRADLE_TASKS[@]}" --no-daemon --stacktrace

APK="app/build/outputs/apk/release/app-release.apk"
UNSIGNED_APK="app/build/outputs/apk/release/app-release-unsigned.apk"
ALIGNED_APK="app/build/outputs/apk/release/app-release-aligned.apk"
SIGNED_APK="app/build/outputs/apk/release/app-release-signed.apk"
AAB="app/build/outputs/bundle/release/app-release.aab"

# Find apksigner & zipalign
APKSIGNER=""
ZIPALIGN=""
if command -v apksigner >/dev/null 2>&1; then
  APKSIGNER="$(command -v apksigner)"
fi
if command -v zipalign >/dev/null 2>&1; then
  ZIPALIGN="$(command -v zipalign)"
fi

# Try ANDROID_SDK_ROOT build-tools if not in PATH
if [ -z "$APKSIGNER" ] || [ -z "$ZIPALIGN" ]; then
  if [ -n "${ANDROID_SDK_ROOT:-}" ] && [ -d "${ANDROID_SDK_ROOT}/build-tools" ]; then
    LATEST_BUILD_TOOLS="$(ls -1 "${ANDROID_SDK_ROOT}/build-tools" | sort -V | tail -n 1 || true)"
    if [ -n "$LATEST_BUILD_TOOLS" ]; then
      BT_DIR="${ANDROID_SDK_ROOT}/build-tools/$LATEST_BUILD_TOOLS"
      if [ -x "$BT_DIR/apksigner" ]; then
        APKSIGNER="$BT_DIR/apksigner"
      fi
      if [ -x "$BT_DIR/zipalign" ]; then
        ZIPALIGN="$BT_DIR/zipalign"
      fi
    fi
  fi
fi

echo "apksigner: ${APKSIGNER:-not found}"
echo "zipalign: ${ZIPALIGN:-not found}"
echo "skip verify: ${SKIP_VERIFY}"

# Verify function helpers
verify_with_apksigner() {
  local file="$1"
  if [ -z "${APKSIGNER:-}" ]; then
    echo "apksigner not available"
    return 2
  fi
  echo "Verifying with apksigner: $file"
  "$APKSIGNER" verify --print-certs "$file"
  return $?
}

verify_with_jarsigner() {
  local file="$1"
  if ! command -v jarsigner >/dev/null 2>&1; then
    echo "jarsigner not available"
    return 2
  fi
  echo "Verifying with jarsigner: $file"
  jarsigner -verify -verbose -certs "$file" | tee "${file##*/}.verify.log"
  if grep -q "jar verified." "${file##*/}.verify.log"; then
    return 0
  fi
  return 1
}

# APK handling
if [ "$BUILD_APK" = true ]; then
  if [ -f "$APK" ]; then
    echo "Found APK: $APK"
    if [ "$SKIP_VERIFY" = true ]; then
      echo "Skipping APK verification as requested (--skip-verify)."
    else
      if verify_with_apksigner "$APK"; then
        echo "APK signature OK (apksigner)."
      else
        echo "apksigner verification failed or not available. Trying jarsigner..."
        if verify_with_jarsigner "$APK"; then
          echo "APK signature OK (jarsigner)."
        else
          echo "APK is not signed or verification failed."
          # Try fallback: sign if unsigned APK exists
          if [ -f "$UNSIGNED_APK" ]; then
            echo "Found unsigned APK: $UNSIGNED_APK"
            if [ -n "${ZIPALIGN:-}" ]; then
              echo "Aligning unsigned APK -> $ALIGNED_APK"
              "$ZIPALIGN" -v -p 4 "$UNSIGNED_APK" "$ALIGNED_APK"
              echo "Signing aligned APK -> $SIGNED_APK"
              if [ -n "${APKSIGNER:-}" ]; then
                "$APKSIGNER" sign --ks "$KEYSTORE_PATH" --ks-key-alias "$KEY_ALIAS" --ks-pass pass:"$KEYSTORE_PASSWORD" --key-pass pass:"$KEY_PASSWORD" --out "$SIGNED_APK" "$ALIGNED_APK"
                echo "Signed APK created: $SIGNED_APK"
                if verify_with_apksigner "$SIGNED_APK"; then
                  echo "Signed APK verification OK"
                  mv "$SIGNED_APK" "$APK"
                else
                  echo "Signed APK verification failed"
                  exit 1
                fi
              else
                echo "apksigner not available to sign the aligned APK. Install Android build-tools."
                exit 1
              fi
            else
              echo "zipalign not available to align the APK. Install Android build-tools."
              exit 1
            fi
          else
            echo "No unsigned APK to sign. Ensure your Gradle signingConfig is configured or provide the unsigned APK."
            exit 1
          fi
        fi
      fi
    fi
  else
    echo "APK not found at $APK"
    if [ "$BUILD_AAB" = false ]; then
      # user requested only APK but not found -> error
      echo "Requested APK build, but APK not present. Exiting."
      exit 1
    fi
  fi
fi

# AAB handling: verify with apksigner (if supported) or jarsigner
if [ "$BUILD_AAB" = true ]; then
  if [ -f "$AAB" ]; then
    echo "Found AAB: $AAB"
    if [ "$SKIP_VERIFY" = true ]; then
      echo "Skipping AAB verification as requested (--skip-verify)."
    else
      if verify_with_apksigner "$AAB"; then
        echo "AAB signature OK (apksigner)."
      else
        echo "apksigner verify failed or not available. Trying jarsigner for AAB..."
        if verify_with_jarsigner "$AAB"; then
          echo "AAB signature OK (jarsigner)."
        else
          echo "AAB is not signed or verification failed."
          echo "Attempting to sign AAB with jarsigner..."
          if command -v jarsigner >/dev/null 2>&1; then
            jarsigner -keystore "$KEYSTORE_PATH" -storepass "$KEYSTORE_PASSWORD" -keypass "$KEY_PASSWORD" "$AAB" "$KEY_ALIAS"
            echo "Signed AAB with jarsigner (note: Google Play uses its own signing keys; ensure this matches your Play upload key workflow)."
            if verify_with_jarsigner "$AAB"; then
              echo "AAB signature verified after jarsigner."
            else
              echo "AAB signature verification still failed"
              exit 1
            fi
          else
            echo "jarsigner not available; cannot sign AAB locally."
            exit 1
          fi
        fi
      fi
    fi
  else
    echo "AAB not found at $AAB"
    if [ "$BUILD_APK" = false ]; then
      # user requested only AAB but not found -> error
      echo "Requested AAB build, but AAB not present. Exiting."
      exit 1
    fi
  fi
fi

echo "Build completed."
echo "Artifacts:"
[ -f "$APK" ] && echo "  APK: $APK"
[ -f "$AAB" ] && echo "  AAB: $AAB"
