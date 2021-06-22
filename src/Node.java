import java.io.IOException;

public class Node {
    static final int port = 5000;
    static final String group = "224.0.0.1";

    MulticastController controller;
    ConfigData cd;
    
    public Node(String path, String index) throws Exception {        
        cd = new ConfigData(path, index);
        System.out.println(cd.toString());
        controller = new MulticastController(cd.thisId, group, port);
    }

    public void run() {
        waitOtherNodes();
    }

    private void waitOtherNodes() {
        int counter = cd.ids.size() - 1;
        try {
            controller.send("HELLO");
        } catch (Exception ignored) {}
        while(true){
            try {
                String message = controller.receive();
                System.out.println(message);
                counter--;
            } catch (Exception ignored) {}
            if (counter == 0) {
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
