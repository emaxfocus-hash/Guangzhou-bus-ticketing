# Notifications & SMS

This project includes support for both push notifications (FCM) and SMS reminders.

Push notifications (FCM)
- Android app uses Firebase Cloud Messaging (firebase-messaging).
- Add your google-services.json to app/ to enable Firebase.
- Optionally provide a Firebase service account JSON in the environment variable `FIREBASE_SERVICE_ACCOUNT_JSON` (stringified JSON) to enable backend server push via firebase-admin.

SMS reminders
- The backend can send SMS via Twilio if you set these environment variables:
  - TWILIO_ACCOUNT_SID
  - TWILIO_AUTH_TOKEN
  - TWILIO_FROM (the sending phone number)
- If Twilio credentials are not set, the backend will log SMS messages (mock mode).

API endpoints added
- POST /devices/register  { token, user_phone }
- POST /notifications/send { title, body, tokens }
- POST /sms/send { phone, message }

Local testing
1. cd backend && npm install
2. (optional) set env variables for Twilio and Firebase service account JSON
3. npm start
4. Run the Android app on emulator, Firebase will create a token and the app will POST to /devices/register
