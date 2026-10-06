# Release instructions for Guangzhou Bus Ticketing

This document describes the steps to produce a release build (AAB/APK), verify signatures, and upload builds to Google Play.

1) Prepare a release keystore

Generate the keystore (one-time):

```bash
keytool -genkeypair -v \
  -keystore my-release-key.jks \
  -alias gzbuskey \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

Keep the keystore safe and offline. DO NOT commit it to source control.

2) Add GitHub secrets (for CI)

From the repo settings: add these Actions secrets:

- KEYSTORE_BASE64 — base64-encoded contents of my-release-key.jks (single line)
- KEYSTORE_PASSWORD — your keystore password
- KEY_ALIAS        — gzbuskey (or the alias you chose)
- KEY_PASSWORD     — the key password

To generate KEYSTORE_BASE64 locally:

```bash
base64 --wrap=0 my-release-key.jks
```

Use the output as the secret value.

3) Build locally (signed)

Make sure the signing configuration in `app/build.gradle` reads the environment variables KEYSTORE_PATH, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD.

Export env vars and build only APK:

```bash
export KEYSTORE_PATH="$PWD/my-release-key.jks"
export KEYSTORE_PASSWORD="your_keystore_password"
export KEY_ALIAS="gzbuskey"
export KEY_PASSWORD="your_key_password"

./gradlew clean assembleRelease
```

Or use the included helper script:

```bash
chmod +x scripts/local_sign_and_build.sh
KEYSTORE_PATH=./my-release-key.jks KEYSTORE_PASSWORD=... KEY_ALIAS=... KEY_PASSWORD=... ./scripts/local_sign_and_build.sh --apk
```

Artifacts:
- APK: app/build/outputs/apk/release/app-release.apk
- AAB: app/build/outputs/bundle/release/app-release.aab

4) Verify signatures

Preferred check (apksigner):

```bash
$ANDROID_SDK_ROOT/build-tools/<version>/apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```

Fallback (jarsigner):

```bash
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk
```

5) Mapping files and ProGuard/R8

Mapping file location after a release build:
- app/build/outputs/mapping/release/mapping.txt

Upload the mapping.txt to Google Play (or keep it safe) to enable crash deobfuscation.

6) Upload to Google Play

Manual (Play Console):
- Create an app in Play Console with package name `com.guangzhou.busticketing`.
- Upload the AAB to an internal test track for QA.
- Fill out store listing, screenshots, privacy policy, content rating, and target countries.
- Roll out to production when ready.

Automated (CI):
- Use `r0adkll/upload-google-play` or `google-github-actions/deploy-action` with a service account JSON stored in the secret `PLAY_STORE_JSON`.
- Ensure the service account has `Release Manager` permissions for the Play Console app.

7) Release checklist

- [ ] Keystore and secrets added to CI
- [ ] google-services.json (Firebase) added to app/ if using Firebase services
- [ ] Adaptive launcher icons and splash screen set
- [ ] Privacy policy URL ready and added to Play listing
- [ ] Mapping file uploaded to Play after release
- [ ] Smoke test on physical devices


