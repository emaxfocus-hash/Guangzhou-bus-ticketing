// Notifications and SMS endpoints for the mock backend

const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { v4: uuidv4 } = require('uuid');

let firebaseAdmin;
let twilioClient;
try {
  // Optional: initialize firebase-admin if service account JSON is provided in env
  if (process.env.FIREBASE_SERVICE_ACCOUNT_JSON) {
    const admin = require('firebase-admin');
    const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT_JSON);
    admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });
    firebaseAdmin = admin;
    console.log('Firebase Admin initialized (from env JSON).');
  }
} catch (e) {
  console.warn('Firebase Admin not initialized:', e.message);
}

try {
  if (process.env.TWILIO_ACCOUNT_SID && process.env.TWILIO_AUTH_TOKEN) {
    const twilio = require('twilio');
    twilioClient = twilio(process.env.TWILIO_ACCOUNT_SID, process.env.TWILIO_AUTH_TOKEN);
    console.log('Twilio client prepared.');
  }
} catch (e) {
  console.warn('Twilio not configured:', e.message);
}

const app = express();
app.use(cors());
app.use(bodyParser.json());

const devices = {}; // token -> { token, phone }
const payments = {};

app.post('/devices/register', (req, res) => {
  const { token, user_phone } = req.body;
  if (!token) return res.status(400).json({ ok: false, message: 'token required' });
  devices[token] = { token, user_phone };
  console.log('Registered device', token, 'phone=', user_phone);
  res.json({ ok: true });
});

app.post('/notifications/send', async (req, res) => {
  const { title, body, tokens } = req.body;
  if (!tokens || !Array.isArray(tokens) || tokens.length === 0) return res.status(400).json({ ok: false, message: 'tokens required' });

  if (firebaseAdmin) {
    try {
      const message = {
        notification: { title, body },
        tokens
      };
      const response = await firebaseAdmin.messaging().sendMulticast(message);
      console.log('FCM send response', response);
      return res.json({ ok: true, message: 'sent', result: response });
    } catch (e) {
      console.warn('FCM send failed', e.message);
      // fallthrough to log
    }
  }

  // Fallback: log and pretend success
  console.log('Sending push (mock) to tokens:', tokens, 'title=', title, 'body=', body);
  res.json({ ok: true, message: 'mock_sent' });
});

app.post('/sms/send', async (req, res) => {
  const { phone, message } = req.body;
  if (!phone || !message) return res.status(400).json({ ok: false, message: 'phone and message required' });

  if (twilioClient && process.env.TWILIO_FROM) {
    try {
      const result = await twilioClient.messages.create({ body: message, from: process.env.TWILIO_FROM, to: phone });
      console.log('Twilio sent', result.sid);
      return res.json({ ok: true, message: 'sent', sid: result.sid });
    } catch (e) {
      console.warn('Twilio send failed', e.message);
      return res.status(500).json({ ok: false, message: e.message });
    }
  }

  // Fallback: log
  console.log('SMS (mock) to', phone, 'message=', message);
  res.json({ ok: true, message: 'mock_sent' });
});

// keep existing routes (/routes and /payments) from earlier file

const routeCatalog = [
  { from: 'Dar es Salaam', to: 'Morogoro', operator: 'Dala 94 Express', departure: '08:00 AM', price: 32000, seats: 12 },
  { from: 'Dar es Salaam', to: 'Morogoro', operator: 'Gani Bus', departure: '10:30 AM', price: 35000, seats: 7 },
  { from: 'Dar es Salaam', to: 'Dodoma', operator: 'Northern Link', departure: '07:15 AM', price: 28000, seats: 16 },
  { from: 'Dar es Salaam', to: 'Arusha', operator: 'Safari Coach', departure: '09:15 AM', price: 42000, seats: 10 }
];

app.get('/routes', (req, res) => {
  res.json(routeCatalog);
});

app.post('/payments/start', (req, res) => {
  const payment_id = uuidv4();
  payments[payment_id] = { status: 'pending', ...req.body };
  setTimeout(() => { payments[payment_id].status = 'paid'; }, 5000);
  res.json({ payment_id, status: 'pending' });
});

app.get('/payments/:id/status', (req, res) => {
  const id = req.params.id;
  if (!payments[id]) return res.status(404).json({ error: 'not_found' });
  res.json({ payment_id: id, status: payments[id].status });
});

const port = process.env.PORT || 3000;
app.listen(port, () => console.log('Backend with notifications ready on', port));
