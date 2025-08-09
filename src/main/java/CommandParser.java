import java.util.ArrayList;
import java.util.List;

public class CommandParser {
    public static List<String> parse(String raw) {
        String[] lines = raw.split("\r\n");
        List<String> args = new ArrayList<>();
        for (String line : lines) {
            if (!line.isEmpty() && !line.startsWith("*") && !line.startsWith("$")) {
                args.add(line);
            }
        }
        return args;
    }
}
