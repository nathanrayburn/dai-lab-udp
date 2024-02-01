package udp.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import udp.Auditor;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * The UDPService class extends Thread and is designed to continuously receive and process UDP messages.
 * It uses a MulticastService to listen for messages sent to a multicast group, interprets the received messages,
 * and updates a shared resource with information about musical instruments and their last active timestamp.
 */
public class UDPService extends Thread {
    /**
     * Logger instance for logging activities and errors.
     */
    private static final Logger LOG = Logger.getLogger(Auditor.class.getName());

    /**
     * A mapping of sound identifiers to musical instrument names.
     */
    private final HashMap<String, String> sounds = new HashMap<>();

    /**
     * The multicast service used to receive messages.
     */
    private MulticastService multicastReceiver;

    /**
     * Constructs a UDPService. Initializes the mapping of sounds to instruments and sets up the multicast receiver.
     */
    public UDPService() {
        sounds.put("ti-ta-ti", "piano");
        sounds.put("pouet", "trumpet");
        sounds.put("trulu", "flute");
        sounds.put("gzi-gzi", "violin");
        sounds.put("boum-boum", "drum");
        multicastReceiver = new MulticastService();
    }

    /**
     * The main execution method for the thread. Continuously receives and processes UDP messages.
     */
    @Override
    public void run() {
        while (true) {
            try {
                processUDPMessage();
            } catch (Exception ex) {
                LOG.severe("Error processing UDP message: " + ex.getMessage());
            }
        }
    }

    /**
     * Processes a single UDP message. Converts the message from JSON to an object, identifies the instrument
     * based on the sound, and updates the shared resource with the instrument's last active timestamp.
     */
    private void processUDPMessage() {
        String json = multicastReceiver.receive();
        if (json != null) {
            LOG.info(json);
            JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
            String instrument = sounds.get(jsonObject.get("sound").getAsString());
            String uuid = jsonObject.get("uuid").getAsString();
            HashMap<String, Long> innerHashMap = new HashMap<>();

            if (!Auditor.orchestra.containsKey(uuid)) {
                innerHashMap.put(instrument, System.currentTimeMillis());
                Auditor.orchestra.put(uuid, innerHashMap);
            } else {
                innerHashMap.put(instrument, System.currentTimeMillis());
                Auditor.orchestra.replace(uuid, innerHashMap);
            }
            LOG.info(Auditor.orchestra.toString());
        }
    }
}
