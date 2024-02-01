package udp.net;

import java.net.*;

/**
 * The MulticastService class provides functionality for joining a multicast group and receiving multicast messages.
 * It encapsulates the details for setting up a multicast socket on a specified port and network interface,
 * and provides a method for receiving messages sent to the multicast group.
 */
public class MulticastService {
    /**
     * The multicast address used by this service.
     */
    final static String MULTI_ADDR = "239.255.22.5";

    /**
     * The port number on which the multicast service listens for messages.
     */
    final static int PORT = 9904;

    /**
     * The size of the buffer used for receiving multicast messages.
     */
    final static int BUFFER_SIZE = 1024;

    /**
     * The multicast socket used for network communication.
     */
    MulticastSocket socket;

    /**
     * The network interface used by the multicast socket.
     */
    NetworkInterface netif;

    /**
     * The group address (IP address and port) used for the multicast service.
     */
    InetSocketAddress group_address;

    /**
     * Constructs a MulticastService object and initializes the multicast socket,
     * network interface, and group address. Automatically joins the multicast group.
     */
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

    /**
     * Receives a multicast message.
     *
     * @return A String containing the data from the received multicast packet, or null if an exception occurs.
     */
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
