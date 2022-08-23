public class MemoryGame {

    private static boolean checkIfRestartGame() {
        String choice = Console.getUserInput("Would you like to start a new game [y/n]", "[yYnN]", "Incorrect choice. Type 'y' or 'n'");
        return choice.equalsIgnoreCase("y");
    }

    public static Game createGame(){
        Game game;
        if (Game.chooseLevel() == 1) {
            game = new EasyGame();
        } else {
            game = new HardGame();
        }
        return game;
    }

    public static void main(String[] args) {
        do {
            Game game = createGame();
            game.run();
        } while (checkIfRestartGame());
    }
}
