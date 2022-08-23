import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;

public class HighScoreDatabase {
    private ArrayList<HighScoreRecord> highScoreRecords = new ArrayList<>();
    private static final String DATABASE_FILE_NAME = "highscores.txt";

    public HighScoreDatabase() {
        loadDatabase();
        trimDatabase();
    }

    public boolean isTop10Result(long time, int tries) {
        boolean result = false;
        if (highScoreRecords.size() < 10) {
            return true;
        }

        for (HighScoreRecord highScoreRecord : highScoreRecords) {
            if (time < highScoreRecord.time ||
                    (time == highScoreRecord.time && tries < highScoreRecord.tries)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void trimDatabase() {
        Collections.sort(highScoreRecords);
        while (highScoreRecords.size() > 10) {
            highScoreRecords.remove(10);
        }
    }

    public int addToDatabase(HighScoreRecord newRecord) {
        highScoreRecords.add(newRecord);
        saveDatabase();

        return highScoreRecords.indexOf(newRecord);
    }

    private void saveDatabase() {
        trimDatabase();

        try (FileWriter fWriter = new FileWriter(DATABASE_FILE_NAME)) {
            for (HighScoreRecord i : highScoreRecords) {
                String str = i.name + "|" + i.date.toString() + "|" + i.time + "|" + i.tries + "\n";
                fWriter.write(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDatabase() {
        try {
            File myFile = new File(DATABASE_FILE_NAME);
            if(!myFile.exists() && !myFile.createNewFile()) {
                System.out.println("An error occurred. Unable to create high score file");
            }

            try {
                Scanner myReader = new Scanner(myFile);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    String[] tokens = data.split("\\|");
                    if (tokens.length == 4) {
                        highScoreRecords.add(new HighScoreRecord(tokens[0], LocalDate.parse(tokens[1]), Long.parseLong(tokens[2]), Integer.parseInt(tokens[3])));
                    }
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred. Database file doesn't exist");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("An error occurred. ");
            e.printStackTrace();
        }
    }

    public List<HighScoreRecord> getHighScoreRecords() {
        return highScoreRecords;
    }
}