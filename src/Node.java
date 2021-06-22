import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
        for (String id : configData.sortedIds) { vectorClock.put(id, 0); }
        System.out.println(vectorClock);
        vectorClock.put("0", 100);
        vectorClock.put("1", 111);
        vectorClock.put("2", 222);
        System.out.println(vectorClock);
        try {
            updateVectorClockFrom("222 100 666");
        } catch (Exception ignored) {}
        System.out.println(vectorClock);
    }

    // TODO: Mandar singlecast, RECEBER single casts e atualizar clock local
    public void run() {
        waitOtherNodes();
        startProcesses();
    }

    private void startProcesses() {
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
                byte[] byteMessage = message.getBytes();
                // TODO: Send singlecast packet
            } else {
                // Local event
                this.vectorClock.put(id, this.vectorClock.get(id) + 1);
            }
        }
    }

    private void updateVectorClockFrom(String message) throws Exception {
        List<Integer> values = Arrays.asList(message.split("\\s"))
            .stream().map(x -> Integer.parseInt(x))
            .collect(Collectors.toList());
        List<String> keys = configData.sortedIds;
        if(values.size() != keys.size()) {
            throw new Exception("Received Value list (of lenght " + values.size() +
                " have a different lenght from the Keys list (lenght " + keys.size() + ")."
            );
        }
        for(int i = 0; i < keys.size(); i++) {
            final String key = keys.get(i);
            final int value = values.get(i);
            if(vectorClock.get(key) < value) {
                vectorClock.put(key, value);
            }
        }
        vectorClock.put(configData.thisId, vectorClock.get(configData.thisId) + 1);
    }

    private String vectorClockToString() {
        return configData.sortedIds.stream()
            .map(k -> String.valueOf(vectorClock.get(k)))
            .reduce("", (a, b) -> a + " " + b)
            .substring(1);
    }

    private void waitOtherNodes() {
        int counter = configData.sortedIds.size();
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
            node.run();
        } catch (Exception ignored) {}
    }
}
