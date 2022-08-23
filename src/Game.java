import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;


public abstract class Game {
    int chancesCount;
    int matrixColCount;
    int matrixRowCount;

    HighScoreDatabase db;

    public Game(int chancesCount, int matrixColCount, int matrixRowCount) {
        this.chancesCount = chancesCount;
        this.matrixColCount = matrixColCount;
        this.matrixRowCount = matrixRowCount;
        this.db = new HighScoreDatabase();
    }

    public abstract String chooseField(int numberOfWords);

    public static int chooseLevel() {
        String levelStr = Console.getUserInput("Choose level: 1-Easy, 2-Hard", "[12]", "Incorrect choice. Type '1' or '2'");
        return Integer.parseInt(levelStr);
    }

    private static String formatInterval(final long l) {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }

    private static boolean writeResult() {
        String choice = Console.getUserInput("Would you like to save your result [y/n]", "[yYnN]", "Incorrect choice. Type 'y' or 'n'");
        return choice.equalsIgnoreCase("y");
    }

    private static int convertFieldToRandomWordRow(String field) {
        int row;
        String rowField = field.substring(0, 1);
        if (rowField.matches("[aA]")) row = 0;
        else row = 1;
        return row;
    }

    private static int convertFieldToRandomWordCol(String field) {
        int col;
        String colField = field.substring(1, 2);
        col = Integer.parseInt(colField);
        return col - 1;
    }

    private String getRandomLine(String path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Random random = new Random();
        return lines.get(random.nextInt(lines.size()));
    }

    private String[][] initializeRandomMatix() {
        String path = new File("Words.txt").getAbsolutePath();
        List<String> listNumbers = new ArrayList<>();
        int counter = 0;
        while (counter < matrixColCount) {
            String randomLine = getRandomLine(path);
            if (!listNumbers.contains(randomLine)) {
                listNumbers.add(randomLine);
                counter++;
            }
        }

        listNumbers.addAll(listNumbers);
        Collections.shuffle(listNumbers);
        String randomMatrix[][] = new String[2][matrixColCount];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < matrixColCount; j++) {
                randomMatrix[i][j] = listNumbers.get(0);
                listNumbers.remove(0);
            }
        }
        return randomMatrix;
    }

    private String[][] initializeBaseMatrix() {
        String[][] baseMatrix = new String[2][matrixColCount];
        Arrays.stream(baseMatrix).forEach(a -> Arrays.fill(a, String.valueOf('X')));
        return baseMatrix;
    }

    private String addPadding(String input, Integer size) {
        int numberOfSpaces = (size - input.length()) / 2;
        String out = String.format("%" + numberOfSpaces + "s", "") + input + String.format("%" + numberOfSpaces + "s", "");
        if (out.length() < size) out += " ";
        return out;
    }

    public int findMaxSize(String[][] baseMatrix) {
        int max = 0;
        for (String[] row : baseMatrix) {
            for (String item : row) {
                max = Math.max(item.length(), max);
            }
        }
        return max;
    }

    public void display(String[][] randomMatrix, String[][] baseMatrix) {
        Console.clearScreen();
        System.out.println("You have " + chancesCount + " chances");

        Integer rows = matrixRowCount + 1;
        Integer cols = matrixColCount + 1;
        int size = findMaxSize(randomMatrix) + 2;

        for (int i = 0; i < rows; i++) {
            for (int a = 0; a < size * cols + cols; a++) System.out.print("-");
            System.out.println("");

            for (int j = 0; j < cols; j++) {
                if (i == 0) {
                    if (j == 0) {
                        System.out.print("|");
                        System.out.print(addPadding("+", size));
                    } else {
                        System.out.print(addPadding(Integer.toString(j), size));
                    }
                    System.out.print("|");
                } else if (j == 0) {
                    System.out.print("|");
                    if (i != 0) {
                        System.out.print(addPadding("" + (char) (i + 64), size));
                    }
                    System.out.print("|");
                } else {
                    System.out.print(addPadding(baseMatrix[i - 1][j - 1], size));
                    System.out.print("|");
                }
            }
            System.out.println("");
        }
        for (int a = 0; a < size * cols + cols; a++) System.out.print("-");
        System.out.println("");

    }


    public void displayHighScoreBoard() {
        Console.clearScreen();
        System.out.println("High score board\n");
        System.out.println("Name - Date - Time - Tries");
        for (HighScoreRecord entry : db.getHighScoreRecords()) {
            System.out.println(entry.name + " - " + entry.date + " - " + formatInterval(entry.time) + " - " + entry.tries);
        }
    }

    public void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int[] chooseXYField(String[][] baseMatrix) {
        String chosenFirstField = chooseField(matrixColCount);
        int row = convertFieldToRandomWordRow(chosenFirstField);
        int col = convertFieldToRandomWordCol(chosenFirstField);
        while (!(baseMatrix[row][col].equals(String.valueOf('X')))) {
            System.out.println("This field is uncovered, choose other field");
            chosenFirstField = chooseField(matrixColCount);
            row = convertFieldToRandomWordRow(chosenFirstField);
            col = convertFieldToRandomWordCol(chosenFirstField);
        }
        return new int[]{row, col};
    }

    public void run() {
        String[][] randomMatrix = initializeRandomMatix();
        String[][] baseMatrix = initializeBaseMatrix();
        int numberOfAction = 0;
        long millisActualTime = System.currentTimeMillis(); // początkowy czas w milisekundach.

        System.out.println("START GAME");

        do {
            display(randomMatrix, baseMatrix);
            //choose first field
            int[] firstPair = chooseXYField(baseMatrix);
            int tempFirstRow = firstPair[0];
            int tempFirstCol = firstPair[1];

            //uncover first field
            baseMatrix[tempFirstRow][tempFirstCol] = randomMatrix[tempFirstRow][tempFirstCol];

            display(randomMatrix, baseMatrix);

            //choose second field
            int[] secondPair = chooseXYField(baseMatrix);
            int tempSecondRow = secondPair[0];
            int tempSecondCol = secondPair[1];

            //uncover second field and check if words are the same
            baseMatrix[tempSecondRow][tempSecondCol] = randomMatrix[tempSecondRow][tempSecondCol];

            display(randomMatrix, baseMatrix);

            chancesCount--;
            numberOfAction++;

            if (baseMatrix[tempFirstRow][tempFirstCol] != baseMatrix[tempSecondRow][tempSecondCol]) {
                baseMatrix[tempFirstRow][tempFirstCol] = String.valueOf('X');
                baseMatrix[tempSecondRow][tempSecondCol] = String.valueOf('X');
            }

            sleep();

            // check win or lose
            int counterX = 0;
            for (int i = 0; i < matrixRowCount; i++) {
                for (int j = 0; j < matrixColCount; j++) {
                    if (baseMatrix[i][j].equals(String.valueOf('X'))) counterX++;
                }
            }

            if (counterX == 0 && chancesCount >= 0) {
                long executionTime = System.currentTimeMillis() - millisActualTime; // czas wykonania programu w milisekundach.
                System.out.println("Congratulations, you win, you needed " + numberOfAction + " tries");
                System.out.println("Game duration: " + formatInterval(executionTime));
                if (db.isTop10Result(executionTime, numberOfAction) && writeResult()) {
                    System.out.println("Give your name");
                    Scanner scan = new Scanner(System.in);
                    String name = scan.nextLine();
                    System.out.println(name);

                    int pos = db.addToDatabase(new HighScoreRecord(name, LocalDate.now(), executionTime, numberOfAction));
                    System.out.println("You took " + pos + " place in high score board");
                }
                displayHighScoreBoard();
                break;
            }
            System.out.println();
            System.out.println("You have " + chancesCount + " chances");
            if (chancesCount == 0) {
                long executionTime = System.currentTimeMillis() - millisActualTime; // czas wykonania programu w milisekundach.
                System.out.println("You lost");
                System.out.println("Game duration: " + formatInterval(executionTime));
                displayHighScoreBoard();
                break;
            }
        } while (chancesCount > 0);

    }
}
