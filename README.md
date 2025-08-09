# Imbed
**Imbed** is a lightweight in-memory key-value store (inspired by Redis) built in Java. Uses RESP for compatibility with existing Redis clients. No persistence.

## Features
Currently implemented features:
- TCP server that listens for connections on a specified port (default: **6379**).
- Thread-safe storage using `ConcurrentHashMap`.
- Basic command execution via a `CommandExecutor` (see supported commands in the Commands section).
- Multiple clients can connect concurrently.
- Lightweight, minimal dependencies.

## Run with Docker
### 1. Build the Docker image
From the root of the project:

```bash
docker build -t imbed .
```

### 2. Run the container
```bash
docker run -d -p 6379:6379 --name imbed-server imbed
```
This will:
* Start the `imbed` server in a container.
* Map container port `6379` to host port `6379`.

### 3. Stop the container
```bash
docker stop imbed-server
```

### 4. Remove the container
```bash
docker rm imbed-server
```

## Commands
Example supported commands (will vary based on your `CommandExecutor` implementation):

| Command         | Description                  | Example          |
| --------------- | ---------------------------- | ---------------- |
| `SET key value` | Stores a value under a key   | `SET name David` |
| `GET key`       | Retrieves the value of a key | `GET name`       |
| `DEL key`       | Deletes a key-value pair     | `DEL name`       |
| `PING`          | Tests connection             | `PING`           |

## ⚙️ Development Notes

* Default port is **6379**.
* Server can be configured by editing the `main` method in `Main.java`.
* Each client runs in its own thread to allow concurrent access.

---