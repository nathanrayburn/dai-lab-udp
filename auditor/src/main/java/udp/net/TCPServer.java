package udp.net;

import udp.Auditor;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class TCPServer {
    private final int SERVER_PORT;
    private final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Logger LOG = Logger.getLogger(Auditor.class.getName());

    public TCPServer(int portTcp) {
        SERVER_PORT = portTcp;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException ex) {
            LOG.severe("ServerSocket: " + ex.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET))) {
            Collection<JsonObject> items = getActiveMusicians();
            output.write(items + "\n");
            output.flush();
            LOG.info(items.toString());
        } catch (IOException ex) {
            LOG.severe("Socket: " + ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                LOG.severe("Error closing socket: " + ex.getMessage());
            }
        }
        LOG.info("Waiting for new connection...");
    }

    private Collection<JsonObject> getActiveMusicians() {
        Collection<JsonObject> items = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        Vector<String> keys = new Vector<>();

        if (!Auditor.orchestra.isEmpty()) {
            Auditor.orchestra.forEach((key, hashMap) -> hashMap.forEach((key2, value) -> {
                if (value + 5000 < currentTime) {
                    keys.add(key);
                } else {
                    JsonObject item = new JsonObject();
                    item.addProperty("uuid", key);
                    item.addProperty("instrument", key2);
                    item.addProperty("lastActivity", value);
                    items.add(item);
                }
            }));
        }

        for (String k : keys) {
            Auditor.orchestra.remove(k);
        }

        return items;
    }
}