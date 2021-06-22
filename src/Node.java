import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Node {
    static final int port = 5000;
    static final String group = "224.0.0.1";

    MulticastController controller;
    ConfigData configData;
    Random random;
    Map<String, Integer> vectorClock;
    
    public Node(String path, String index) throws Exception {        
        configData = new ConfigData(path, index);
        System.out.println(configData.toString());
        controller = new MulticastController(configData.thisId, group, port);
        random = new Random();
        vectorClock = new HashMap<>();
        for (String id : configData.ids) { vectorClock.put(id, 0); }
        vectorClock.put(configData.thisId, 0);
    }

    // TODO: Mandar singlecast, RECEBER single casts e atualizar clock local
    public void run() {
        waitOtherNodes();
        final String id = configData.thisId;
        final int maxEvents = configData.event;
        final float chance = configData.chance;
        final int intervalSize = configData.maxDelay - configData.minDelay;
        final int minDelay = configData.minDelay;
        for (int i = 0; i < maxEvents; i++) {
            try {
                Thread.sleep(random.nextInt(intervalSize) + minDelay);
            } catch (Exception ignored) {}
            if(random.nextDouble() < chance) {
                // Send message to another process
                String randomId = configData.getRandomId();
                // TODO: Prepare singlecast packet
                String message = vectorClockToString();
                // TODO: Send singlecast packet
            } else {
                // Local event
                this.vectorClock.put(id, this.vectorClock.get(id) + 1);
            }
        }

    }

    private String vectorClockToString() {
        return vectorClock.keySet().stream()
            .sorted()
            .map(k -> String.valueOf(vectorClock.get(k)))
            .reduce("", (a, b) -> a + " " + b)
            .substring(1);
    }

    private void waitOtherNodes() {
        int counter = configData.ids.size() + 1;
        try {
            controller.send("HELLO");
        } catch (Exception ignored) {}
        while(true){
            try {
                String message = controller.receive();
                if(message.split("\\s")[0].equals("START")) { break; }
                System.out.println(message);
                counter--;
            } catch (Exception ignored) {}
            if (counter == 0) {
                try {
                    controller.send("START");
                } catch (IOException e) {e.printStackTrace();}
                break;
            }
        }
        try {
            controller.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Usage: java Node <config path> <config index>");
            System.exit(1);
        }
        
        try {
            Node node = new Node(args[0], args[1]);
            // node.run();
        } catch (Exception ignored) {}
    }
}
