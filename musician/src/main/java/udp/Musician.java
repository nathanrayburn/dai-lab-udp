package udp;

import com.google.gson.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Enum representing different types of musical instruments, each associated with a specific sound.
 */
enum Instrument {
    PIANO("ti-ta-ti"),
    TRUMPET("pouet"),
    FLUTE("trulu"),
    VIOLIN("gzi-gzi"),
    DRUM("boum-boum");

    private final String sound;

    Instrument(String sound) {
        this.sound = sound;
    }

    /**
     * Gets the sound associated with the instrument.
     * @return The sound of the instrument.
     */
    public String getSound() {
        return sound;
    }

    /**
     * Converts a string to an Instrument enum.
     * @param instrumentName The name of the instrument to convert.
     * @return The corresponding Instrument enum, or null if no match is found.
     */
    public static Instrument fromString(String instrumentName) {
        for (Instrument instrument : Instrument.values()) {
            if (instrument.name().equalsIgnoreCase(instrumentName)) {
                return instrument;
            }
        }
        return null;
    }
}

/**
 * The Musician class simulates a musician who plays an instrument and sends its sound
 * to a multicast group at regular intervals.
 */
class Musician {
    final static String IP_ADDR = "239.255.22.5";
    final static int PORT = 9904;
    private static final Logger LOG = Logger.getLogger(Musician.class.getName());

    /**
     * The main method that validates input arguments, creates a JSON object representing
     * the musician's UUID and instrument sound, and sends it to a multicast group.
     * @param args Command-line arguments specifying the instrument to play.
     */
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 1) {
            LOG.severe("Not the right number of arguments, you need at least 1");
            System.exit(1);
        }

        // Check if the specified instrument is supported
        Instrument instrument = Instrument.fromString(args[0]);
        if (instrument == null) {
            LOG.severe("Not the right instrument, this application have to be launched with one of these instruments : piano, trumpet, flute, violin or drum");
            System.exit(1);
        }

        // Generate a UUID for the session
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();

        // Create a JSON object containing UUID and instrument sound information
        JsonObject jsonObject = createJsonObject(uuidAsString, instrument.getSound());

        try (DatagramSocket socket = new DatagramSocket()) {
            // Continuously send information to the multicast group
            while (true) {
                byte[] payload = jsonObject.toString().getBytes(UTF_8);
                InetSocketAddress dest_address = new InetSocketAddress(IP_ADDR, PORT);
                var packet = new DatagramPacket(payload, payload.length, dest_address);
                socket.send(packet);
                LOG.info(jsonObject.toString());
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (IOException ex) {
            LOG.severe(ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a JSON object with UUID and sound properties.
     * @param uuid The UUID of the musician as a string.
     * @param sound The sound of the instrument.
     * @return A JsonObject with UUID and sound information.
     */
    private static JsonObject createJsonObject(String uuid, String sound) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", uuid);
        jsonObject.addProperty("sound", sound);
        return jsonObject;
    }
}
