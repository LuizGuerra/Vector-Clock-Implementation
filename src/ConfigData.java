import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigData {
    final String index;
    final String pathString;
    
    final Set<String> ids;
    final List<String> sortedIds;
    final List<Configuration> configurations;
    final String thisId;
    final String host;
    final Integer port;
    final Float chance;
    final Integer event;
    final Integer minDelay;
    final Integer maxDelay;

    private Configuration thisConfiguration;

    static private final Random random = new Random();

    public ConfigData(String pathString, String index) throws Exception {
        // Set atributes
        this.pathString = pathString;
        this.index = index;
        // Read and close file
        File file = new File(pathString);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> list = reader.lines()
            .collect(Collectors.toList());
        reader.close();
        // Set final atributes
        this.ids = list.stream()
            .map(x -> x.substring(0, 1))
            .collect(Collectors.toSet());
        this.configurations = list.stream()
            .map(x -> new Configuration(x))
            .collect(Collectors.toList());
        this.sortedIds = ids.stream().sorted().collect(Collectors.toList());
        final String[] thisOne = list.stream()
            .filter(x -> x.substring(0, 1).equals(index))
            .collect(Collectors.toList())
            .get(0).split("\\s");
        this.thisId = thisOne[0];
        ids.remove(thisId);
        this.host = thisOne[1];
        this.port = Integer.parseInt(thisOne[2]);
        this.chance = Float.parseFloat(thisOne[3]);
        this.event = Integer.parseInt(thisOne[4]);
        this.minDelay = Integer.parseInt(thisOne[5]);
        this.maxDelay = Integer.parseInt(thisOne[6]);
    }

    public String getRandomId() {
        int index = random.nextInt(ids.size());
        int counter = 0;
        for (String id : ids) {
            if (counter == index) { return id; }
            index++;
        }
        return sortedIds.get(0);
    }

    public Configuration thisConfiguration() {
        if (thisConfiguration == null) {
            thisConfiguration = findById(thisId);
        }
        return thisConfiguration;
    }

    public Configuration findById(String id) {
        for (Configuration c : configurations) {
            if(c.id.equals(id)) { return c; }
        }
        return null;
    }

    public Configuration randomConfiguration() {
        return findById(getRandomId());
    }

    @Override
    public String toString() {
        return "ConfigData{" +
                "index='" + index + '\'' +
                ", pathString='" + pathString + '\'' +
                ", ids=" + ids +
                ", thisId='" + thisId + '\'' +
                ", port='" + port + '\'' +
                ", host='" + host + '\'' +
                ", chance=" + chance +
                ", event=" + event +
                ", minDelay=" + minDelay +
                ", maxDelay=" + maxDelay +
                '}';
    }
}
