const express = require('express');
const redis = require('redis');

const app = express();
app.use(express.json());

const client = redis.createClient({
  socket: {
    host: '127.0.0.1',
    port: 6379,
  }
});

client.connect().catch(console.error);

app.get('/get/:key', async (req, res) => {
  try {
    const value = await client.get(req.params.key);
    if (value === null) {
      return res.status(404).json({ error: 'Key not found' });
    }
    res.json({ key: req.params.key, value });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/set', async (req, res) => {
  try {
    const { key, value, ex } = req.body;
    if (!key || !value) {
      return res.status(400).json({ error: 'Missing key or value' });
    }
    if (ex) {
      await client.set(key, value, { EX: ex });
    }
    else {
      await client.set(key, value);
    }
    res.json({ message: 'OK' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/keys', async (_req, res) => {
  try {
    const keys = await client.keys('*');
    res.json({ keys });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/ping', async (_req, res) => {
  try {
    const pong = await client.ping();
    res.json({ pong });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/echo', async (req, res) => {
  const { message } = req.body;
  try {
    const echo = await client.echo(message);
    res.json({ echo });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`REST API proxy listening on http://localhost:${PORT}`);
});
