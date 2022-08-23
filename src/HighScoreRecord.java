import java.time.LocalDate;

public class HighScoreRecord implements Comparable<HighScoreRecord> {
    public final String name;
    public final LocalDate date;
    public final long time;
    public final int tries;

    public HighScoreRecord(String name, LocalDate date, long time, int tries) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.tries = tries;
    }

    public int compareTo(HighScoreRecord other) {
        if (time == other.time) {
            if (tries == other.tries) {
                return 0;
            } else if (tries > other.tries) {
                return 1;
            } else {
                return -1;
            }
        } else if (time > other.time) {
            return 1;
        } else {
            return -1;
        }
    }
}
