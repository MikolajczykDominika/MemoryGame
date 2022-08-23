public class HardGame extends Game {
    public HardGame() {
        super(15, 8, 2);
    }

    @Override
    public String chooseField(int numberOfWords) {
        return Console.getUserInput("Choose field from A1 to B8", "[abAB][1-8]", "Incorrect choice. Choose field from A1 to B8 ");
    }
}
