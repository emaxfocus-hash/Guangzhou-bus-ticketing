# Development backend

This is a simple Node.js mock backend that simulates payment flows for local development.

Run in the repo root:

cd backend
npm install
npm start

The mock server listens on port 3000 and exposes:
- GET /trips
- POST /payments/start
- GET /payments/:id/status
- POST /payments/webhook
- POST /bookings

The /payments/start endpoint simulates an asynchronous mobile-money provider by marking the payment as paid after 6 seconds. Use the Android emulator and point the app to http://10.0.2.2:3000/ (ApiClient.kt is already configured for this).
