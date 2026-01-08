package multithreadedserver.readersandwriters;

public class ConsoleReader {

    private static final BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));

    private ConsoleReader() {}

    public static String readLine(String prompt) {
        while(true){
            try {
                System.out.println(prompt);
                return reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read from console", e);
            }
        }
    }

    public static String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from console", e);
        }
    }

}

