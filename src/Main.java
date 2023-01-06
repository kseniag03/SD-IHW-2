import java.util.*;

/**
 * Entry point to the program.
 */
public class Main {
    /**
     * Gets the path to the root directory from user
     * and creates FileManager object.
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Enter the path to the root directory: ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        FileManager fileManager = new FileManager(path != null ? path : "");
        fileManager.doManagerWork();
    }
}
