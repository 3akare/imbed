import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Main {
    public static void main(String[] args) {
        int port = 6379;
        Map<String, ImbedValue> store = new ConcurrentHashMap<>();
        CommandExecutor executor = new CommandExecutor(store);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket, executor)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket, CommandExecutor executor) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                if (!line.startsWith("*")) {
                    List<String> args = CommandParser.parse(line);
                    String response = executor.execute(args);
                    writer.write(response);
                    writer.flush();
                    continue;
                }
                int numArgs = Integer.parseInt(line.substring(1));
                List<String> args = new ArrayList<>();
                for (int i = 0; i < numArgs; i++) {
                    String lenLine = reader.readLine();
                    if (lenLine == null || !lenLine.startsWith("$")) {
                        break;
                    }
                    int len = Integer.parseInt(lenLine.substring(1));
                    char[] buf = new char[len];
                    int readCount = reader.read(buf, 0, len);

                    reader.readLine();
                    args.add(new String(buf, 0, readCount));
                }

                String response = executor.execute(args);
                writer.write(response);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

}
