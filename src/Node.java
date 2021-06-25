import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Node {
    static final int port = 4500;
    static final String group = "224.0.0.1";
    
    Random random;
    ConcurrentHashMap<String, Integer> vectorClock;

    MulticastController controller;
    ConfigData configData;
    ReceiveUnicast receiver;
    
    public Node(String path, String index) throws Exception {        
        configData = new ConfigData(path, index);
        // System.out.println(configData.toString());
        controller = new MulticastController(configData.thisId, group, port);
        random = new Random();
        receiver = new ReceiveUnicast();
        receiver.start();
        vectorClock = new ConcurrentHashMap<>();
        for (String id : configData.sortedIds) { vectorClock.put(id, 0); }
    }

    private class ReceiveUnicast extends Thread {  // Formatar o recebimento de um evento
        @Override
        public void run() {
            while (true) {
                try {
                    DatagramSocket socket = new DatagramSocket(configData.port);
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String[] received = new String(packet.getData(), 0, packet.getLength()).split(";");
                    updateVectorClockFrom(received[1]);
                    System.out.println("Received event from @" + received[0] +
                    "\t" + vectorClockToString());
                    System.out.println(received[0] + "\t" + received[1] + "\t" + "R");
                    socket.close();
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    private class SendUnicast extends Thread {
        private InetAddress address;
        private Integer port;
        private String message;
        public SendUnicast(String address, Integer port, String message) throws UnknownHostException {
            this.address = InetAddress.getByName(address);
            this.port = port;
            this.message = message;
        }
        @Override
        public void run() {
            try {
                DatagramSocket socket = new DatagramSocket();
                byte[] byteMessage = (configData.thisId + ";" + message).getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(
                    byteMessage, byteMessage.length, this.address, this.port);
                socket.send(datagramPacket);
                socket.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void run() {
        waitOtherNodes();
        startProcesses();
        //endProcess();
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
            if(random.nextDouble() < chance) {  // formatar o envio de um evento para outro nodo
                // Send message to another process
                Configuration c = configData.randomConfiguration();
                System.out.println("Sending event to @" + c.id + "\t" + vectorClockToString());
                System.out.println(c.id + "\t" + vectorClockToString() + "\t" + "S");
                try {
                    SendUnicast sender = new SendUnicast(c.host, c.port, vectorClockToString());
                    sender.start();
                } catch (Exception e) { e.printStackTrace(); };
            } else {                            // formatar o envio de um evento local
                // Local event
                this.vectorClock.put(id, this.vectorClock.get(id) + 1);
                System.out.println("Local event\t\t" + vectorClockToString());
                System.out.println(id + "\t" + vectorClockToString() + "\t" + "L");
            }
            System.gc();
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

    // private String localEvent() {
    //     // i [c,c,c] i d s t
    // }

    // private String sendMessage() {
    //     // i [c,c,c] i d s t
    // }

    // private String receiveMessage() {
    //     // i [c,c,c] i d s t
    // }

    private void waitOtherNodes() {
        int counter = configData.sortedIds.size();
        try {
            controller.send("HELLO");
        } catch (Exception ignored) {}
        while(true){
            try {
                String[] message = controller.receive().split("\\s");
                if(message[0].equals("START")) { break; }
                System.out.println("Received " + message[0] + " from " + message[1]);
                counter--;
            } catch (Exception ignored) {}
            if (counter == 0) {
                try {
                    controller.send("START");
                } catch (IOException e) { e.printStackTrace(); }
                break;
            }
        }
        System.out.println("Everyone is ready, starting program...\n");
    }

    private void endProcess() {
        int counter = configData.sortedIds.size() - 1;
        try {
            controller.send("CLOSING " + configData.thisId);
        } catch (Exception ignored) {}
        while(true){
            try {
                String[] message = controller.receive().split("\\s");
                if(message[0].equals("EXIT")) { break; }
                if(message[1].equals(configData.thisId)) { continue; }
                counter--;
                System.out.println("Process @" + message[1] + 
                    " is ending. " + counter + " more to end.");
                if (counter == 0) {
                    controller.send("EXIT");
                    break;
                }
            } catch (Exception ignored) {}
        }
        try {
            controller.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiver.interrupt();
        System.gc();
        System.exit(0);
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Usage: java Node <config path> <config index>");
            System.exit(1);
        }

        System.out.println("Running..");
        
        try {
            Node node = new Node(args[0], args[1]);
            node.run();
        } catch (Exception ignored) {ignored.printStackTrace();}
    }
}
