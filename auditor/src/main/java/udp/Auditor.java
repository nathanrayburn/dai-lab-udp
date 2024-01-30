package udp;

import java.util.HashMap;
import java.util.logging.Logger;
import udp.net.*;

public class Auditor {
    private static final int PORT_TCP = 2205;
    private static final Logger LOG = Logger.getLogger(Auditor.class.getName());
    public static HashMap<String, HashMap<String, Long>> orchestra = new HashMap<>();

    public static void main(String[] args) {
        new UDPService().start();
        new TCPServer(PORT_TCP).start();
    }
}