package udp.net;
import java.net.*;
public class MulticastService {
    final static String MULTI_ADDR = "239.255.22.5";
    final static int PORT = 9904;
    final static int BUFFER_SIZE = 1024;
    MulticastSocket socket;
    NetworkInterface netif;
    InetSocketAddress group_address;

    public MulticastService() {
        try {
            socket = new MulticastSocket(PORT);
            netif = NetworkInterface.getByName("eth0");
            group_address = new InetSocketAddress(MULTI_ADDR, PORT);
            socket.joinGroup(group_address, netif);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    public String receive() {
        try {
            // Set up a buffer for receiving messages
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
            // Receive the multicast message
            socket.receive(packet);
            System.out.println("Received packet");
            return new String(packet.getData(), 0, packet.getLength());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
