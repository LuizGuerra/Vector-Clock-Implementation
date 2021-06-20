import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastController {
    final int port;
    final InetAddress group;
    final MulticastSocket socket;
    final String name;

    public MulticastController(String name, String group, int port) throws IOException {
        this.name = name;
        this.port = port;
        this.socket = new MulticastSocket(port);
        this.group = InetAddress.getByName(group);
        socket.joinGroup(this.group);
    }

    public void send(String request) throws IOException {
        byte[] message = (request + " " + name).getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, group, port);
        socket.send(packet);
    }

    public void send(String request, String body) throws IOException {
        byte[] message = (request + " " + name + " " + body).getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, group, port);
        socket.send(packet);
    }

    public void send(MulticastMessageFormat mmf) throws IOException {
        send(mmf.request, mmf.body);
    }

    public void send(String request, Long time) throws IOException {
        send(request, time.toString());
    }

    public String receive() throws IOException {
        // Read
        byte[] entry = new byte[1024]; // 2^14 bytes
        DatagramPacket packet = new DatagramPacket(entry, entry.length);
        socket.setSoTimeout(100);
        socket.receive(packet);
        // Parse
        return new String(packet.getData(), 0, packet.getLength());
    }

    public void end() throws IOException {
        socket.leaveGroup(group);
        socket.close();
        System.gc();
    }
}
