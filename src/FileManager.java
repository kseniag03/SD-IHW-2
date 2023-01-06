import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Class for working with files and directories.
 */
public class FileManager {
    /**
     * Max nesting level (for appropriate recursion work)
     */
    private final int NESTING_LEVEL = 10;

    /**
     * Default path to the directory with files (if user enters the wrong one).
     */
    private final String FILE_PATH = System.getProperty("user.dir");

    /**
     * Constant file name for output (in current project directory).
     */
    private final String OUTPUT_FILE_PATH = "output.txt";

    /**
     * Map of files and their indexes in the graph (files "ids").
     */
    private final Map<Integer, File> fileId;

    /**
     * Map of files and the list of files it depends on.
     */
    private final Map<File, ArrayList<File>> dependencies;

    /**
     * Counter for file id.
     */
    private int count = 0;

    /**
     * Current root directory (from input or directory of the project).
     */
    File root;

    /**
     * Graph for checking cycles and topological sorting.
     */
    Graph graph;

    /**
     * Constructor for FileManager class.
     * Creates root directory, initializes maps and clears output file.
     * @param directoryPath path to the root directory
     */
    FileManager(String directoryPath) {
        root = new File(directoryPath);
        if (!root.exists()) {
            root = new File(FILE_PATH);
        }
        File output = new File(OUTPUT_FILE_PATH);
        if (output.exists()) {
            try {
                FileWriter fileWriter = new FileWriter(output);
                fileWriter.write("");
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        fileId = new Hashtable<>();
        dependencies = new Hashtable<>();
    }

    /**
     * Gets index of the file in the graph.
     * There is a bijection between graph vertex and file,
     * so we can get key by value too.
     * @param map map of files with integer keys
     * @param value file to find
     * @return index of the file in the graph
     */
    private Integer getKeyByValue(Map<Integer, File> map, File value) {
        for (var entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Finds the file path of the required file in current line.
     * @param words array of words in the current line
     * @return file path of the required file
     */
    private String checkRequire(String[] words) {
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("require") && i + 1 < words.length) {
                StringBuilder nextLine = new StringBuilder(words[i + 1]);
                while (nextLine.charAt(nextLine.length() - 1) != '\'' && ++i < words.length) {
                    nextLine.append(" ").append(words[i + 1]);
                }
                String nextFileName = nextLine.toString().split("'")[1];
                var fileParts = nextFileName.split("/");
                StringBuilder filePath = new StringBuilder(root.getAbsolutePath() + File.separator); // if nextLine is null ?
                for (int j = 0; j < fileParts.length - 1; j++) {
                    filePath.append(fileParts[j]).append(File.separator);
                }
                filePath.append(fileParts[fileParts.length - 1]);
                System.out.println("filePath: " + filePath);
                return filePath.toString();
            }
        }
        return null;
    }

    /**
     * Reads current file and find its vertex and dependencies.
     * @param fileName current file name
     */
    private void readFile(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                ArrayList<File> fileDependencies;
                if (getKeyByValue(fileId, file) == null) {
                    count++;
                    fileId.put(count, file);
                }
                if (dependencies.get(file) == null) {
                    fileDependencies = new ArrayList<>();
                } else {
                    fileDependencies = dependencies.get(file);
                }
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String[] words = scanner.nextLine().split(" ");
                    String filePath = checkRequire(words);
                    if (filePath != null) {
                        var curFile = new File(filePath);
                        if (curFile.exists()) {
                            if (getKeyByValue(fileId, curFile) == null) {
                                count++;
                                fileId.put(count, curFile);
                            }
                            fileDependencies.add(curFile);
                        } else {
                            System.out.println("Required file '" + curFile.getName() + "' doesn't exist.");
                        }
                    }
                }
                dependencies.put(file, fileDependencies);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Reads all files in the directory recursively.
     * @param directoryPath current directory path
     * @param nestingLevel current nesting level (does not exceed NESTING_LEVEL)
     */
    private void readDirectory(String directoryPath, int nestingLevel) {
        if (nestingLevel > NESTING_LEVEL) {
            return;
        }
        File root = new File(directoryPath);
        if (root.exists()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        readDirectory(file.getAbsolutePath(), nestingLevel + 1);
                    } else {
                        readFile(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Writes file's content to the output file.
     * @param file current file
     */
    private void writeFileContent(File file) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(OUTPUT_FILE_PATH, true);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                fileWriter.write(line + "\n");
                System.out.println(line);
            }
            fileWriter.write("\n");
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Initializes the graph, adds all edges, checks for cycles,
     * launches topological sort and writes the result.
     * @param directoryPath current directory path
     */
    public void doManagerWork(String directoryPath) {
        readDirectory(directoryPath, 0);
        //System.out.println("count = " + count);
        graph = new Graph(count);
        for (Map.Entry<File, ArrayList<File>> entry : dependencies.entrySet()) {
            var curFile = entry.getKey();
            var edgeCurFile = getKeyByValue(fileId, curFile);
            if (edgeCurFile != null) {
                //System.out.println("File: " + entry.getKey().getName());
                for (File file : entry.getValue()) {
                    //System.out.println("Dependency: " + file.getName());
                    var edgeDependentFile = getKeyByValue(fileId, file);
                    if (edgeDependentFile != null) {
                        graph.addEdge(edgeDependentFile - 1, edgeCurFile - 1);
                    } else {
                        System.out.println("Error: edge of dependent file is null");
                    }
                }
            } else {
                System.out.println("Error: edge of current file is null");
            }
        }
        System.out.println();
        graph.printGraph();
        if (graph.hasCycle()) {
            System.out.println("Cycle detected\n");
        } else {
            System.out.println("No cycle detected\n");
            var order = graph.topologicalSort();
            while (!order.empty()) {
                var key = order.pop() + 1;
                if (fileId.get(key) != null) {
                    System.out.println(fileId.get(key).getName());
                    writeFileContent(fileId.get(key));
                }
                System.out.println();
            }
        }
    }
}
