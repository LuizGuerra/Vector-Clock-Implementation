import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final public class MulticastMessageFormat {
    public final String request;
    public final String sender;
    public final String body;
    public final String originalMessage;

    public MulticastMessageFormat(String str) {
        String[] vars = str.split("\\s");
        if(vars.length < 2) {
            throw new IllegalArgumentException("MulticastMessageFormat constructor bad entry:\n" + str);
        }
        request = vars[0];
        sender = vars[1];
        if(vars.length >= 3) {
            body = str.substring(request.length() + sender.length() + 2);
        } else {
            body = "";
        }
        originalMessage = str;
    }

    public Long bodyToTime() {
        try {
            return Long.parseLong(body);
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public String toString() {
        return "MulticastMessageFormat{" +
                "request='" + request + '\'' +
                ", sender='" + sender + '\'' +
                ", body='" + body + '\'' +
                ", originalMessage='" + originalMessage + '\'' +
                '}';
    }
}
