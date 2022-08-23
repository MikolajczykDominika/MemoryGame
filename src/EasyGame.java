public class EasyGame extends Game {
    public EasyGame() {
        super(10, 4, 2);
    }

    @Override
    public String chooseField(int numberOfWords) {
        return Console.getUserInput("Choose field from A1 to B4", "[abAB][1-4]", "Incorrect choice. Choose field from A1 to B4");
    }
}
