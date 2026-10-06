const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { v4: uuidv4 } = require('uuid');

const app = express();
app.use(cors());
app.use(bodyParser.json());

const cities = [
  'Dar es Salaam', 'Morogoro', 'Dodoma', 'Arusha', 'Moshi', 'Mbeya', 'Tanga',
  'Zanzibar', 'Mwanza', 'Kigoma', 'Iringa', 'Songea', 'Tabora', 'Bukoba', 'Shinyanga', 'Lindi'
];

const routeCatalog = [
  { from: 'Dar es Salaam', to: 'Morogoro', operator: 'Dala 94 Express', departure: '08:00 AM', price: 32000, seats: 12 },
  { from: 'Dar es Salaam', to: 'Morogoro', operator: 'Gani Bus', departure: '10:30 AM', price: 35000, seats: 7 },
  { from: 'Dar es Salaam', to: 'Dodoma', operator: 'Northern Link', departure: '07:15 AM', price: 28000, seats: 16 },
  { from: 'Dar es Salaam', to: 'Arusha', operator: 'Safari Coach', departure: '09:15 AM', price: 42000, seats: 10 },
  { from: 'Dar es Salaam', to: 'Mbeya', operator: 'Top Route', departure: '18:30 PM', price: 61000, seats: 9 },
  { from: 'Dar es Salaam', to: 'Zanzibar', operator: 'Coastal Star', departure: '06:45 AM', price: 25000, seats: 20 },
  { from: 'Morogoro', to: 'Dodoma', operator: 'Hillway Express', departure: '06:00 AM', price: 22000, seats: 14 },
  { from: 'Morogoro', to: 'Arusha', operator: 'Mountain Movers', departure: '11:00 AM', price: 39000, seats: 11 },
  { from: 'Dodoma', to: 'Mbeya', operator: 'Central Transit', departure: '08:45 AM', price: 43000, seats: 13 },
  { from: 'Arusha', to: 'Moshi', operator: 'Kilimanjaro Line', departure: '07:35 AM', price: 17000, seats: 21 },
  { from: 'Arusha', to: 'Mwanza', operator: 'Lake Route', departure: '15:15 PM', price: 52000, seats: 8 },
  { from: 'Mwanza', to: 'Kigoma', operator: 'Lake Express', departure: '06:30 AM', price: 33000, seats: 12 },
  { from: 'Mbeya', to: 'Iringa', operator: 'Southern Hills', departure: '09:40 AM', price: 18000, seats: 25 },
  { from: 'Mbeya', to: 'Songea', operator: 'Southern Link', departure: '13:00 PM', price: 24000, seats: 18 },
  { from: 'Tanga', to: 'Dar es Salaam', operator: 'Coastal Express', departure: '08:10 AM', price: 26000, seats: 19 },
  { from: 'Zanzibar', to: 'Dar es Salaam', operator: 'Bora Ferry Coach', departure: '07:00 AM', price: 23000, seats: 22 },
  { from: 'Tabora', to: 'Dodoma', operator: 'Central Shuttle', departure: '09:05 AM', price: 25500, seats: 14 },
  { from: 'Bukoba', to: 'Mwanza', operator: 'Lake Transit', departure: '10:25 AM', price: 21000, seats: 17 },
  { from: 'Lindi', to: 'Dar es Salaam', operator: 'Coastway', departure: '06:50 AM', price: 29000, seats: 15 }
];

const payments = {};

app.get('/cities', (req, res) => res.json(cities));

app.get('/routes', (req, res) => {
  const { from, to } = req.query;
  const filtered = routeCatalog.filter(route => {
    const fromMatch = !from || route.from.toLowerCase().includes(String(from).toLowerCase());
    const toMatch = !to || route.to.toLowerCase().includes(String(to).toLowerCase());
    return fromMatch && toMatch;
  });
  res.json(filtered);
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
app.listen(port, () => console.log('Tanzania route backend ready on', port));
