import java.io.IOException;
import java.nio.file.Path;

public class Node {
    static final int port = 5000;
    static final String group = "224.0.0.1";

    MulticastController controller;

    public Node(String path, String index) throws Exception {        
        ConfigData cd = new ConfigData(path, index);
        System.out.println(cd.toString());
        // controller = new MulticastController(name, group, port);
    }

    public void run() {
        try {
            controller.send("HELLO");
        } catch (Exception ignored) {}
        while(true){
            try {
                System.out.println(controller.receive());
            } catch (Exception ignored) {}
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
