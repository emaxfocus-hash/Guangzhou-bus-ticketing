// Simple Node.js mock backend for development/testing

const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { v4: uuidv4 } = require('uuid');

const app = express();
app.use(cors());
app.use(bodyParser.json());

const payments = {};
const bookings = {};

app.get('/trips', (req, res) => {
  res.json([
    { id: 't1', route: 'Dar es Salaam - Morogoro', departure: '08:00', seats: 12, price: 32000 },
    { id: 't2', route: 'Dar es Salaam - Morogoro', departure: '10:30', seats: 7, price: 35000 }
  ]);
});

app.post('/payments/start', (req, res) => {
  const payment_id = uuidv4();
  payments[payment_id] = { status: 'pending', ...req.body };

  // Simulate provider: after 6 seconds, mark payment as paid
  setTimeout(() => {
    payments[payment_id].status = 'paid';
    console.log('Payment', payment_id, 'marked as paid (simulated)');
  }, 6000);

  res.json({ payment_id, status: 'pending' });
});

app.get('/payments/:id/status', (req, res) => {
  const id = req.params.id;
  if (!payments[id]) return res.status(404).json({ error: 'not_found' });
  res.json({ payment_id: id, status: payments[id].status });
});

app.post('/payments/webhook', (req, res) => {
  const { payment_id, status } = req.body;
  if (!payments[payment_id]) return res.status(404).json({ error: 'not_found' });
  payments[payment_id].status = status;
  res.json({ ok: true });
});

app.post('/bookings', (req, res) => {
  const booking_id = uuidv4();
  bookings[booking_id] = { id: booking_id, ...req.body };
  res.json({ booking_id });
});

const port = process.env.PORT || 3000;
app.listen(port, () => console.log('Mock backend listening on', port));
