import java.util.List;
import java.util.Map;

public class CommandExecutor {
    private final Map<String, ImbedValue> store;

    public CommandExecutor(Map<String, ImbedValue> store) {
        this.store = store;
    }

    public String execute(List<String> args) {
        if (args.isEmpty()) {
            return "-ERR empty command\r\n";
        }

        String cmd = args.get(0).toLowerCase();
        long now = System.currentTimeMillis();

        switch (cmd) {
            case "ping":
                return "+PONG\r\n";

            case "echo":
                if (args.size() < 2) return "-ERR wrong number of arguments\r\n";
                return "$" + args.get(1).length() + "\r\n" + args.get(1) + "\r\n";

            case "set": {
                if (args.size() < 3) return "-ERR wrong number of arguments\r\n";
                String key = args.get(1);
                String value = args.get(2);
                long ttl = -1L;

                if (args.size() > 4) {
                    String optType = args.get(3).toLowerCase();
                    if (optType.equals("ex")) {
                        ttl = now + (Long.parseLong(args.get(4)) * 1000);
                    } else if (optType.equals("px")) {
                        ttl = now + Long.parseLong(args.get(4));
                    }
                }

                store.put(key, new ImbedValue(ttl, value));
                return "+OK\r\n";
            }

            case "get": {
                if (args.size() < 2) return "-ERR wrong number of arguments\r\n";
                String key = args.get(1);
                ImbedValue val = store.get(key);

                if (val == null) return "$-1\r\n";
                if (val.isExpired()) {
                    store.remove(key);
                    return "$-1\r\n";
                }
                return "$" + val.value.length() + "\r\n" + val.value + "\r\n";
            }

            case "keys": {
                StringBuilder resp = new StringBuilder();
                resp.append("*").append(store.size()).append("\r\n");
                for (String key : store.keySet()) {
                    resp.append("$").append(key.length()).append("\r\n").append(key).append("\r\n");
                }
                return resp.toString();
            }

            default:
                return "-ERR unknown command\r\n";
        }
    }
}