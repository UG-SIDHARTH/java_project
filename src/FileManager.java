import java.io.*;
import java.util.*;

// Module 5: Data Storage
public class FileManager {
    private static final String DATA_DIR = "data/";

    static {
        new File(DATA_DIR).mkdirs();
    }

    public static void saveLines(String filename, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
            for (String line : lines) {
                pw.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    public static List<String> loadLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(DATA_DIR + filename);
        if (!file.exists()) return lines;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }
}
