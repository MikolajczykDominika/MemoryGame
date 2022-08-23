import java.util.Scanner;

public class Console {
    public static String getUserInput(String message, String regex, String errorMessage) {
        System.out.println(message);
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        while (!input.matches(regex)) {
            System.out.println(errorMessage);
            System.out.println(message);
            input = scan.nextLine();
        }
        return input;
    }

    public static void clearScreen() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}