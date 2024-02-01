package udp.net;

import udp.Auditor;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

/**
 * The TCPServer class provides functionality for starting a TCP server that listens on a specified port.
 * It accepts client connections, processes requests, and sends back information about active musicians.
 * This class uses java.util.logging.Logger for logging information and errors.
 */
public class TCPServer {
    /**
     * The port number on which the server listens for incoming TCP connections.
     */
    private final int SERVER_PORT;

    /**
     * The character set used for encoding/decoding the streams. Defaults to UTF-8.
     */
    private final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * Logger instance for logging server activities and errors.
     */
    private static final Logger LOG = Logger.getLogger(Auditor.class.getName());

    /**
     * Constructs a TCPServer with a specified port for TCP connections.
     *
     * @param portTcp The port number on which the server will listen for connections.
     */
    public TCPServer(int portTcp) {
        SERVER_PORT = portTcp;
    }

    /**
     * Starts the server to accept client connections and process requests.
     * For each client connection, a new thread is spawned to handle communication.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            LOG.info("Server started on port " + SERVER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException ex) {
            LOG.severe("ServerSocket: " + ex.getMessage());
        }
    }

    /**
     * Handles client connections by sending a list of active musicians.
     * It reads data from an internal source and writes it to the client's socket output stream.
     *
     * @param socket The client socket connection to handle.
     */
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

    /**
     * Retrieves a collection of active musicians as JsonObjects.
     * A musician is considered active if their last activity timestamp is within a certain threshold.
     *
     * @return A collection of JsonObjects each representing an active musician.
     */
    private Collection<JsonObject> getActiveMusicians() {
        Collection<JsonObject> items = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        Vector<String> keys = new Vector<>();

        // Filter out inactive musicians and prepare JsonObject for active ones
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

        // Remove inactive musicians from the collection
        for (String k : keys) {
            Auditor.orchestra.remove(k);
        }

        return items;
    }
}
