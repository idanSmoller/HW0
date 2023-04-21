import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);    // delete to what scanner equal when finish
    public static Random rnd;

    static final int N_GUESS_N_SUB = 0;
    static final int N_GUESS_Y_SUB = 1;
    static final int Y_GUESS_N_SUB = 2;
    static final int Y_GUESS_Y_SUB = 3;

    static final char INPUT_DIVIDER = 'X';
    static final String INPUT_DIVIDER_COORDINATE = ", ";

    static final int SUB_INDEX_X = 0;
    static final int SUB_INDEX_Y = 1;
    static final int SUB_INDEX_ORIENTATION = 2;
    static final int ORIENTATION_HORIZONTAL = 0;
    static final int ORIENTATION_VERTICAL = 1;

    static final int BOARD_INDEX_PLAYER = 0;
    static final int BOARD_INDEX_COMP = 1;


    /**
     * parse string input in format "num1Xnum2" into int array in format [num1, num2]
     * @param str the string input
     * @return the int array
     */
    public static int[] parseNumXNum(String str) {
        int i = 0;
        for (i = 0; i < str.length(); i++) {
            if (str.charAt(i) == INPUT_DIVIDER) {
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

    public static int[] parseCoordinateOrientation(String str) {
        int i = 0;
        int found = 0;
        int ret[] = new int[3];
        int prev = 0;

        while (found < 2){
            if (str.substring(i, i + 2).equals(INPUT_DIVIDER_COORDINATE)) {
                ret[found++] = Integer.parseInt(str.substring(prev, i));
                prev = i + 2;
            }

            i++;
        }

        ret[found] = Integer.parseInt(str.substring(prev, str.length()));

        return ret;
    }

    public static int[] inputAndParseBoardSize() {
        System.out.println("Enter the board size");
        String sizeAsString = scanner.nextLine();
        return parseNumXNum(sizeAsString);
    }

    public static int getSubSizesArrLength(int[] boardSize) {
        return boardSize[0] > boardSize[1] ? boardSize[0] : boardSize[1];
    }

    public static int[] inputAndParseSubSizes(int[] boardSize) {
        int[] subSizes = new int[getSubSizesArrLength(boardSize)];
        System.out.println("Enter the battleship sizes");
        String subSizesString = scanner.nextLine();
        int wordStart = 0;
        for(int i = 0; i < subSizesString.length(); i++) {
            char currentChar = subSizesString.charAt(i);
            if(currentChar == ' ') {
                int[] parseNumXNumValue = parseNumXNum(subSizesString.substring(wordStart, i));
                subSizes[parseNumXNumValue[1]] = parseNumXNumValue[0];
                wordStart = i + 1;
            }
        }
        int[] parseNumXNumValue = parseNumXNum(subSizesString.substring(wordStart, subSizesString.length()));
        subSizes[parseNumXNumValue[1]] = parseNumXNumValue[0];
        return subSizes;
    }

    public static int[] inputAndParseCoordinatesOrientation(int size) {
        System.out.println("Enter location and orientation for battleship of size " + size);
        return parseCoordinateOrientation(scanner.nextLine());
    }

    public static boolean checkValidSub(int[][] board, int[] sub, int size) {
        if (sub[SUB_INDEX_ORIENTATION] != ORIENTATION_HORIZONTAL && sub[SUB_INDEX_ORIENTATION] != ORIENTATION_VERTICAL) {
            System.out.println("Illegal orientation, try again!");
            return false;
        }
        if (sub[SUB_INDEX_X] >= board[0].length || sub[SUB_INDEX_X] < 0 ||
                sub[SUB_INDEX_Y] >= board.length || sub[SUB_INDEX_Y] < 0) {
            System.out.println("Illegal tile, try again!");
            return false;
        }
        if (sub[sub[SUB_INDEX_ORIENTATION] ^ 1] + size >
                (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_HORIZONTAL ? board[0].length : board.length)) {
            System.out.println("Battleship exceeds the boundaries of the board, try again!");
        }
        //TODO: not on or near another sub

        return true;
    }

    public static void placeSub(int[][] board, int[] sub) {
        //TODO: the function
    }

    public static void inputSubs(int[][][] board, int[] subSizes) {
        int[] sub = new int[3];
        for (int i = 1; i < subSizes.length; i++) {
            while (subSizes[i] != 0) {
                do {
                    sub = inputAndParseCoordinatesOrientation(i);
                } while (!checkValidSub(board[BOARD_INDEX_PLAYER], sub, i));

                placeSub(board[BOARD_INDEX_PLAYER], sub);
                subSizes[i]--;
            }
        }
    }

    public static int[][][] initBoard() {
        int[] boardSize = inputAndParseBoardSize();
        int[][][] board = new int[2][boardSize[0]][boardSize[1]];
        int[] subSizes = inputAndParseSubSizes(boardSize);

        inputSubs(board, subSizes);

        return board;
    }

    public static void battleshipGame() {
        int[][][] board = initBoard();
    }

    public static void main(String[] args) {
        int[] arr = parseCoordinateOrientation("10, 2, 0");
        for (int i = 0; i < 3; i++) {
            System.out.println(arr[i]);
        }
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