package udp.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import udp.Auditor;

import java.util.HashMap;
import java.util.logging.Logger;

public class UDPService extends Thread {
    private static final Logger LOG = Logger.getLogger(Auditor.class.getName());
    private final HashMap<String, String> sounds = new HashMap<>();
    private MulticastService multicastReceiver;

    public UDPService() {
        sounds.put("ti-ta-ti", "piano");
        sounds.put("pouet", "trumpet");
        sounds.put("trulu", "flute");
        sounds.put("gzi-gzi", "violin");
        sounds.put("boum-boum", "drum");
        multicastReceiver = new MulticastService();
    }

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