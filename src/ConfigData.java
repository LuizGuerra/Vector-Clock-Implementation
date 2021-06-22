import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ConfigData {
    final String index;
    final String pathString;
    
    final Set<String> ids;
    final String thisId;
    final String host;
    final Integer port;
    final Float chance;
    final Integer event;
    final Integer minDelay;
    final Integer maxDelay;

    public ConfigData(String pathString, String index) throws Exception {
        // Set data
        this.pathString = pathString;
        this.index = index;
        // Parse data
        File file = new File(pathString);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> list = reader.lines()
            .collect(Collectors.toList());
        reader.close();
            this.ids = new HashSet<>(
            list.stream()
            .map(x -> x.substring(0, 1))
            .collect(Collectors.toSet())
        );
        final String[] thisOne = list.stream()
            .filter(x -> x.substring(0, 1).equals(index))
            .collect(Collectors.toList())
            .get(0).split("\\s");
        this.thisId = thisOne[0];
        this.host = thisOne[1];
        this.port = Integer.parseInt(thisOne[2]);
        this.chance = Float.parseFloat(thisOne[3]);
        this.event = Integer.parseInt(thisOne[4]);
        this.minDelay = Integer.parseInt(thisOne[5]);
        this.maxDelay = Integer.parseInt(thisOne[6]);
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
