## Local development

1. Start the mock backend:
   cd backend
   npm install
   npm start

2. Open the Android project in Android Studio.
3. Run the app on an emulator. The app is configured to call the mock backend at http://10.0.2.2:3000/

Notes:
- Mobile Money payments are simulated by the backend; after starting a payment the backend will mark it as paid automatically after a short delay.
- For real integration replace the backend with your provider's API and secure webhooks.
