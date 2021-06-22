public class Configuration {
    public final String id;
    public final String host;
    public final Integer port;
    public Configuration(String line) {
        String[] vars = line.split("\\s");
        id = vars[0];
        host = vars[1];
        port = Integer.parseInt(vars[2]);
    }
    @Override
    public String toString() {
        return "Id: " + id + ", " +
            "Host: " + host +  ", " +
            "Port: " + port + ".";
    }
}
