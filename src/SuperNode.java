import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SuperNode {

    // Node information
    String name;

    // Multicasts
    MulticastController superNodeController;
    MulticastController clientsNodeController;

    // Node communication
    static final String HELLO = "HELLO";
    static final String EXIT = "EXIT";
    static final int superNodePort = 5000;
    Set<String> superNodes;
    String superNodeGroup = "224.0.2.1";

    // Client and super node communication
    static final String CLIENT_HELLO = "CLIENT_HELLO";
    static final String CLIENT_EXIT = "CLIENT_EXIT";
    static final String CLIENT_ALIVE = "CLIENT_ALIVE";
    static final String REQUEST_RESOURCES = "REQUEST_RESOURCES";
    static final String COMMIT_RESOURCES = "COMMIT_RESOURCES";
    static final String HEARTBEAT = "HEARTBEAT";
    static final String SYSTEM_EXIT = "SYSTEM_EXIT";
    static final String FILE_REQUEST = "FILE_REQUEST";

    int clientNodesPort;
    String clientNodeGroup = "224.0.2.2";
    Timer timer;

    public SuperNode(String name, int port) throws IOException {
        this.superNodeController = new MulticastController(name, superNodeGroup, superNodePort);
        this.clientsNodeController = new MulticastController(name, clientNodeGroup, port);
        this.superNodes = new HashSet<>();
        this.name = name;
        this.clientNodesPort = port;
        timer = new Timer();
    }

    public void run() throws IOException {
        System.out.println("Send EXIT to exit.\n");
        System.out.println("Sending hello...");
        superNodeController.send(HELLO);
        System.out.println("Hello sent.\n");
        Scanner scanner = new Scanner(System.in);
        String input;

        // Is waiting super node resources or request from other supernode
        String askedForResources = "";
        StringBuilder resourceStrings = new StringBuilder();
        int counter = 0;

        while (true) {
            // If can send resources back to client
            if (!askedForResources.isEmpty() && counter == superNodes.size()) {
                System.out.println("\nReceived all resources. Sending package...");
                clientsNodeController.send(COMMIT_RESOURCES, resourceStrings.toString());
                askedForResources = "";
                resourceStrings = new StringBuilder();
                counter = 0;
                System.out.println("Resources package sent to all connected clients.\n");
            }
            
            if (System.in.available() > 0) {
                input = scanner.nextLine().trim().toUpperCase();
                if (EXIT.equals(input)) {
                    superNodeController.send(EXIT);
                    break;
                }
            }
        }
        superNodeController.end();
        scanner.close();
        timer.cancel();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java ClientNode <name> <unique port>");
            System.out.println("<name>: user name");
            System.out.println("<unique port>: integer client communication port");
            System.exit(1);
        }
        int clientPort = Integer.parseInt(args[1]);
        SuperNode node = new SuperNode(args[0], clientPort);
        node.run();
    }
}
