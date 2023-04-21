import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);    // delete to what scanner equal when finish
    public static Random rnd;

    static final int N_GUESS_N_SUB = 0;
    static final int N_GUESS_Y_SUB = 1;
    static final int Y_GUESS_N_SUB = 2;
    static final int Y_GUESS_Y_SUB = 3;

    static final char INPUT_DEVIDER = 'X';


    /**
     * parse string input in format "num1Xnum2" into int array in format [num1, num2]
     * @param str the string input
     * @return the int array
     */
    public static int[] parseInput(String str) {
        int i = 0;
        for (i = 0; i < str.length(); i++) {
            if (str.charAt(i) == INPUT_DEVIDER) {
                break;
            }
        }

        String firstNumAsString = str.substring(0, i);
        String secondNumAsString = str.substring(i + 1, str.length());

        int[] ret = new int[2];
        ret[0] = Integer.parseInt(firstNumAsString);
        ret[1] = Integer.parseInt(secondNumAsString);

        return ret;
    }

    public static int[] inputAndParseBoardSize() {
        System.out.println("Enter the board size");
        String sizeAsString = scanner.nextLine();
        return parseInput(sizeAsString);
    }

    public static int getBiggestInArray(int[] arr) {
        int max = arr[0];
        for(int i = 1; i < arr.length; i++) {
            if(arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    public static int getSubSizesArrLength(int[] boardSize) {
        return getBiggestInArray(boardSize) + 1;
    }

    public static int[] inputAndParseSubSizes(int[] boardSize) {
        int[] subSizes = new int[getSubSizesArrLength(boardSize)];
        System.out.println("Enter the battleship sizes");
        String subSizesString = scanner.nextLine();
        int wordStart = 0;
        for(int i = 0; i < subSizesString.length(); i++) {
            char currentChar = subSizesString.charAt(i);
            if(currentChar == ' ') {
                System.out.println("i: " + i);
                int[] parseInputValue = parseInput(subSizesString.substring(wordStart, i));
                subSizes[parseInputValue[1]] = parseInputValue[0];
                wordStart = i + 1;
            }
        }
        int[] parseInputValue = parseInput(subSizesString.substring(wordStart, subSizesString.length()));
        subSizes[parseInputValue[1]] = parseInputValue[0];
        return subSizes;
    }

    public static int[][][] initBoard() {
        int[] boardSize = inputAndParseBoardSize();
        int[] subSizes = inputAndParseSubSizes(boardSize);
        for(int i = 0; i < subSizes.length; i++) {
            System.out.println(subSizes[i] + " subs in size " + i);
        }
        int[][][] check = new int[1][1][1];
        return check;
    }

    public static void battleshipGame() {
        int[][][] board = initBoard();
    }

    public static void main(String[] args) {
        initBoard();
    }

    /*public static void main(String[] args) throws IOException {
        String path = args[0];
        scanner = new Scanner(new File(path));
        int numberOfGames = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Total of " + numberOfGames + " games.");

        for (int i = 1; i <= numberOfGames; i++) {
            scanner.nextLine();
            int seed = scanner.nextInt();
            rnd = new Random(seed);
            scanner.nextLine();
            System.out.println("Game number " + i + " starts.");
            battleshipGame();
            System.out.println("Game number " + i + " is over.");
            System.out.println("------------------------------------------------------------");
        }
        System.out.println("All games are over.");
    }

     */
}