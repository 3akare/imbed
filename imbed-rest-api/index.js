const express = require('express');
const imbed = require('redis'); // Uses redis-client

const app = express();
app.use(express.json());

// Imbed client setup
const client = imbed.createClient({
  socket: {
    host: '127.0.0.1',
    port: 6379,
  }
});

client.on('error', (err) => console.error('Imbed  Client Error', err));

(async () => {
  try {
    await client.connect();
    console.log('✅ Connected to Imbed');
  } catch (err) {
    console.error('❌ Failed to connect to Imbed:', err.message);
    process.exit(1);
  }
})();

// Get value by key
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

// List all keys
app.get('/keys', async (_req, res) => {
  try {
    const keys = await client.keys('*');
    res.json({ keys });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Ping Redis
app.get('/ping', async (_req, res) => {
  try {
    const pong = await client.ping();
    res.json({ pong });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Get TTL for a key
app.get('/ttl/:key', async (req, res) => {
  try {
    const ttl = await client.ttl(req.params.key);
    if (ttl === -2) {
      return res.status(404).json({ error: 'Key not found' });
    }
    res.json({ key: req.params.key, ttl });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Set a key (with optional expiration)
app.post('/set', async (req, res) => {
  try {
    const { key, value, ex } = req.body;
    if (!key || value === undefined) {
      return res.status(400).json({ error: 'Missing key or value' });
    }
    if (ex) {
      await client.set(key, value, { EX: ex });
    } else {
      await client.set(key, value);
    }
    res.json({ message: 'OK' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Expire a key
app.post('/expire', async (req, res) => {
  try {
    const { key, ex } = req.body;
    if (!key || !ex) {
      return res.status(400).json({ error: 'Missing key or expiration time' });
    }
    const result = await client.expire(key, ex);
    if (result === 0) {
      return res.status(404).json({ error: 'Key not found or could not set expiration' });
    }
    res.json({ message: 'Expiration set' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Echo message
app.post('/echo', async (req, res) => {
  try {
    const { message } = req.body;
    if (!message) {
      return res.status(400).json({ error: 'Missing message' });
    }
    const echo = await client.echo(message);
    res.json({ echo });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Delete a key
app.delete('/del/:key', async (req, res) => {
  try {
    const result = await client.del(req.params.key);
    if (result === 0) {
      return res.status(404).json({ error: 'Key not found' });
    }
    res.json({ deleted: req.params.key });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`🚀 REST API listening on http://localhost:${PORT}`);
});
