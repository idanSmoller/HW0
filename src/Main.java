import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner;
    public static Random rnd;

    static final int N_GUESS_N_SUB = 0;
    static final int N_GUESS_Y_SUB = 1;
    static final int Y_GUESS_N_SUB = 2;
    static final int Y_GUESS_Y_SUB = 3;

    static final char INPUT_DIVIDER = 'X';
    static final String INPUT_DIVIDER_COORDINATE = ", ";

    static final int SUB_INDEX_ROW = 0;
    static final int SUB_INDEX_COL = 1;
    static final int SUB_INDEX_ORIENTATION = 2;
    static final int ORIENTATION_HORIZONTAL = 0;
    static final int ORIENTATION_VERTICAL = 1;

    static final int BOARD_INDEX_PLAYER = 0;
    static final int BOARD_INDEX_COMP = 1;


    /**
     * parse string input in format "num1Xnum2" into int array in format [num1, num2]
     *
     * @param str the string input
     * @return the int array
     */
    public static int[] parseNumXNum(String str) {
        int i;
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

    /**
     * parse string input in format "x, y, orientation" into an int array in format [x, y, orientation]
     *
     * @param str the string input
     * @return the formatted int array
     */
    public static int[] parseCoordinateOrientation(String str) {
        int i = 0;
        int found = 0;
        int[] ret = new int[3];
        int prev = 0;

        while (found < 2) {
            if (str.substring(i, i + 2).equals(INPUT_DIVIDER_COORDINATE)) {
                ret[found++] = Integer.parseInt(str.substring(prev, i));
                prev = i + 2;
            }

            i++;
        }

        ret[found] = Integer.parseInt(str.substring(prev, str.length()));

        return ret;
    }

    /**
     * parse string input in format "x, y" into an int array in format [x, y]
     *
     * @param str the string input
     * @return the formatted int array
     */
    public static int[] parseCoordinate(String str) {
        int i = 0;
        int found = 0;
        int[] ret = new int[2];
        int prev = 0;

        while (found < 1) {
            if (str.substring(i, i + 2).equals(INPUT_DIVIDER_COORDINATE)) {
                ret[found++] = Integer.parseInt(str.substring(prev, i));
                prev = i + 2;
            }

            i++;
        }

        ret[found] = Integer.parseInt(str.substring(prev, str.length()));

        return ret;
    }

    /**
     * input and parse the board size
     *
     * @return the board size as an int array of size 2
     */
    public static int[] inputAndParseBoardSize() {
        System.out.println("Enter the board size");
        String sizeAsString = scanner.nextLine();
        return parseNumXNum(sizeAsString);
    }

    /**
     * get the maximum possible size of a sub (the largest side of the board)
     *
     * @param boardSize the board size
     * @return the maximum size for a sub
     */
    public static int getSubSizesArrLength(int[] boardSize) {
        return boardSize[0] > boardSize[1] ? boardSize[0] : boardSize[1];
    }

    /**
     * input and parse the sub sizes
     *
     * @param boardSize the board size
     * @return the sub sizes as a histogram (value x in the nth spot in the array means x n sized subs will be in the game
     */
    public static int[] inputAndParseSubSizes(int[] boardSize) {
        int[] subSizes = new int[getSubSizesArrLength(boardSize)];
        System.out.println("Enter the battleships sizes");
        String subSizesString = scanner.nextLine();
        int wordStart = 0;
        for (int i = 0; i < subSizesString.length(); i++) {
            char currentChar = subSizesString.charAt(i);
            if (currentChar == ' ') {
                int[] parseNumXNumValue = parseNumXNum(subSizesString.substring(wordStart, i));
                subSizes[parseNumXNumValue[1]] = parseNumXNumValue[0];
                wordStart = i + 1;
            }
        }
        int[] parseNumXNumValue = parseNumXNum(subSizesString.substring(wordStart, subSizesString.length()));
        subSizes[parseNumXNumValue[1]] = parseNumXNumValue[0];
        return subSizes;
    }

    /**
     * input and parse the location of a guess
     *
     * @param mute_ask true if shouldn't be printed message asking for input. false otherwise.
     * @return the sub as an array of size 2: [x, y]
     */
    public static int[] inputAndParseCoordinates(boolean mute_ask) {
        if(!mute_ask){
            System.out.println("Enter a tile to attack");
        }
        return parseCoordinate(scanner.nextLine());
    }

    /**
     * input and parse the location and the orientation of a sub
     *
     * @param mute_ask true if shouldn't be printed message asking for input. false otherwise.
     * @param size the size of the sub
     * @return the sub as an array of size 3: [row, column, orientation] (row and column of the top left corner of the sub)
     */
    public static int[] inputAndParseCoordinatesOrientation(int size, boolean mute_ask) {
        if(!mute_ask){
            System.out.println("Enter location and orientation for battleship of size " + size);
        }
        return parseCoordinateOrientation(scanner.nextLine());
    }

    /**
     * check if the tile (row, col) in the board is currently empty
     *
     * @param board the board
     * @param row     the tile's row
     * @param col     the tile's column
     * @return whether the spot is available or not
     */
    public static boolean notOverlapping(int[][] board, int row, int col) {
        return board[row][col] == N_GUESS_N_SUB;
    }

    /**
     * check if the surrounding of a given tile is empty
     *
     * @param board the board
     * @param row     the tile's row
     * @param col     the tile's column
     * @return whether the tile's surrounding is valid for sub placement or not
     */
    public static boolean validSurrounding(int[][] board, int row, int col) {
        boolean rightEdge = (col == board[0].length - 1);
        boolean leftEdge = (col == 0);
        boolean bottomEdge = (row == board.length - 1);
        boolean topEdge = (row == 0);
        int startRow = topEdge ? row : row - 1;
        int endRow = bottomEdge ? row : row + 1;
        int startCol = leftEdge ? col : col - 1;
        int endCol = rightEdge ? col : col + 1;
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                if (board[i][j] == N_GUESS_Y_SUB) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * returns whether a tile (int) was guessed or not
     *
     * @param tile int to check if was guessed.
     * @return true if the tile was guessed, false otherwise.
     */
    public static boolean guessed(int tile) {
        return tile == Y_GUESS_N_SUB || tile == Y_GUESS_Y_SUB;
    }

    /**
     * check and return whether a tile is valid to attack. if not, print the appropriate error (if not muted).
     *
     * @param board the boar game
     * @param row the row of the desired tile to attack
     * @param col the column of the desired tile to attack
     * @param mute true if shouldn't print any message, false if should print errors.
     * @return boolean. true if it is valid to attack the given tile, false otherwise.
     */
    public static boolean checkValidAttackTile(int[][] board, int row, int col, boolean mute) {
        if (!(row >= 0 && row < board.length && col >= 0 && col < board[0].length)) {
            if (!mute) {
                System.out.println("Illegal tile, try again!");
            }
            return false;
        }
        if (guessed(board[row][col])) {
            if (!mute) {
                System.out.println("Tile already attacked, try again!");
            }
                return false;
        }

        return true;
    }

    /**
     * check if a sub can be placed on the board
     *
     * @param board the board
     * @param sub   the sub which the player wants to place
     * @param size  the sub size
     * @param mute  whether to print the error messages or not
     * @return whether the sub can be legally placed in the wanted place
     */
    public static boolean checkValidSub(int[][] board, int[] sub, int size, boolean mute) {
        // check illegal orientation
        if (sub[SUB_INDEX_ORIENTATION] != ORIENTATION_HORIZONTAL &&
                sub[SUB_INDEX_ORIENTATION] != ORIENTATION_VERTICAL) {
            if (!mute) {
                System.out.println("Illegal orientation, try again!");
            }
            return false;
        }

        // check illegal tile
        if (sub[SUB_INDEX_ROW] >= board.length || sub[SUB_INDEX_ROW] < 0 ||
                sub[SUB_INDEX_COL] >= board[0].length || sub[SUB_INDEX_COL] < 0) {
            if (!mute) {
                System.out.println("Illegal tile, try again!");
            }
            return false;
        }

        // check sub in boundaries
        if (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_HORIZONTAL) {
            if (sub[SUB_INDEX_COL] + size > board[0].length) {
                if (!mute) {
                    System.out.println("Battleship exceeds the boundaries of the board, try again!");
                }
                return false;
            }
        } else {
            if (sub[SUB_INDEX_ROW] + size > board.length) {
                if (!mute) {
                    System.out.println("Battleship exceeds the boundaries of the board, try again!");
                }
                return false;
            }
        }

        // check battleship overlaps another battleships
        for (int i = 0; i < size; i++) {
            int currLocationRow = sub[SUB_INDEX_ROW] + (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_VERTICAL ? i : 0);
            int currLocationCol = sub[SUB_INDEX_COL] + (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_HORIZONTAL ? i : 0);

            // check overlaps
            if (!notOverlapping(board, currLocationRow, currLocationCol)) {
                if (!mute) {
                    System.out.println("Battleship overlaps another battleship, try again!");
                }
                return false;
            }
        }

        // check battleship adjacent to another battleships
        for (int i = 0; i < size; i++) {
            int currLocationRow = sub[SUB_INDEX_ROW] + (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_VERTICAL ? i : 0);
            int currLocationCol = sub[SUB_INDEX_COL] + (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_HORIZONTAL ? i : 0);
            if (!validSurrounding(board, currLocationRow, currLocationCol)) {
                if (!mute) {
                    System.out.println("Adjacent battleship detected, try again!");
                }
                return false;
            }
        }

        return true;
    }

    /**
     * place a sub on the board (we assume that the spot for the sub is valid
     *
     * @param board the board
     * @param sub   the sub we wish to place
     * @param size  the sub size
     */
    public static void placeSub(int[][] board, int[] sub, int size) {
        for (int i = 0; i < size; i++) {
            if (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_VERTICAL) {
                board[sub[SUB_INDEX_ROW] + i][sub[SUB_INDEX_COL]] = N_GUESS_Y_SUB;
            } else {
                board[sub[SUB_INDEX_ROW]][sub[SUB_INDEX_COL] + i] = N_GUESS_Y_SUB;
            }
        }
    }

    /**
     * get a valid sub for the computer player (random)
     *
     * @param board the computer's board
     * @param size  the size of the generated sub
     * @return the created sub
     */
    public static int[] getComputerSub(int[][] board, int size) {
        int[] sub = new int[3];
        int row = board.length;
        int col = board[0].length;

        do {
            sub[0] = rnd.nextInt(row);
            sub[1] = rnd.nextInt(col);
            sub[2] = rnd.nextInt(2);
        } while (!checkValidSub(board, sub, size, true));

        return sub;
    }

    /**
     * get a valid sub for the human player (input)
     *
     * @param board the computer's board
     * @param size  the size of the generated sub
     * @return the created sub
     */
    public static int[] getPlayerSub(int[][] board, int size) {
        int[] sub = inputAndParseCoordinatesOrientation(size, false);
        while (!checkValidSub(board, sub, size, false)) {
            sub = inputAndParseCoordinatesOrientation(size, true);
        }
        return sub;
    }

    /**
     * input subs and place them on the board
     *
     * @param board    the board
     * @param subSizes the sub sizes in the current game
     */
    public static void inputSubs(int[][][] board, int[] subSizes) {
        int[] playerSub;
        int[] computerSub;
        System.out.println("Your current game board:");
        printBoard(board[BOARD_INDEX_PLAYER], true);

        for (int i = 1; i < subSizes.length; i++) {
            while (subSizes[i] != 0) {
                playerSub = getPlayerSub(board[BOARD_INDEX_PLAYER], i);
                computerSub = getComputerSub(board[BOARD_INDEX_COMP], i);

                placeSub(board[BOARD_INDEX_PLAYER], playerSub, i);
                placeSub(board[BOARD_INDEX_COMP], computerSub, i);
                subSizes[i]--;
                System.out.println("Your current game board:");
                printBoard(board[BOARD_INDEX_PLAYER], true);
            }
        }
    }

    /**
     * count number of subs for each player and place it in subNums
     *
     * @param subSizes histogram: value x in index i means there are x subs of size i for each player
     * @param subNums an array to place the sizes in them
     */
    public static void countSubs(int[] subSizes, int[] subNums) {
        int sum = 0;
        for (int size : subSizes) {
            sum += size;
        }
        subNums[0] = sum;
        subNums[1] = sum;
    }

    /**
     * initialize the boards for the player and the computer - input subs and place them on the board.
     * Also place the number of subs for each player in subNums
     *
     * @param subNums array in which updates the number of subs for each player
     * @return the created board
     */
    public static int[][][] initBoard(int[] subNums) {
        int[] boardSize = inputAndParseBoardSize();
        int[][][] board = new int[2][boardSize[0]][boardSize[1]];
        int[] subSizes = inputAndParseSubSizes(boardSize);

        countSubs(subSizes, subNums);
        inputSubs(board, subSizes);

        return board;
    }

    /**
     * turns a tile in the board into a printable character
     *
     * @param tile   the tile
     * @param player true if to show the player board, false if to show the comp board
     * @return the tile as a character to print
     */
    public static char tileToChar(int tile, boolean player) {
        switch (tile) {
            case N_GUESS_N_SUB:
                return '–';
            case N_GUESS_Y_SUB:
                return player ? '#' : '–';
            case Y_GUESS_N_SUB:
                return player ? '–' : 'X';
            case Y_GUESS_Y_SUB:
                return player ? 'X' : 'V';
            default:
                return 0;
        }
    }

    /**
     * count how many digits are in a positive integer
     *
     * @param num the integer to check
     * @return how many digits are in num
     */
    public static int countDigit(int num) {
        if (num == 0) {
            return 1;
        }
        int counter = 0;
        while (num > 0) {
            counter++;
            num /= 10;
        }
        return counter;
    }

    /**
     * print spaces (without down line). it does this 'times' times
     *
     * @param times how many times to print space
     */
    public static void printNumSpaces(int times){
        for(int i = 0; i < times; i++){
            System.out.print(" ");
        }
    }

    /**
     * print the current state pf the given board (also depends on the player the board belonged to)
     *
     * @param board the board to print
     * @param player the player the board belongs to
     */
    public static void printBoard(int[][] board, boolean player) {
        // todo: double digit shit
        int digitNumRow = countDigit(board.length - 1);
        int digitNumCol = countDigit(board[0].length - 1);
        // print the first row of indexes
        printNumSpaces(digitNumRow);
        for (int i = 0; i < board[0].length; i++) {
            printNumSpaces(digitNumCol - countDigit(i) + 1);
            System.out.print(i);
        }
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            printNumSpaces(digitNumRow - countDigit(i));
            System.out.print(i);
            for (int j = 0; j < board[0].length; j++) {
                printNumSpaces(digitNumCol);
                System.out.print(tileToChar(board[i][j], player));
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * check if a sub has a continuation in a specific direction from a specific tile
     *
     * @param board current board
     * @param row row of the tile to check
     * @param col column of the tile to check
     * @param incRow 1 if to check to the right, -1 if to check to the left, 0 otherwise
     * @param incCol 1 if to check to the bottom, -1 if to check to the top, 0 otherwise
     * @return true if there is a continuation in that direction, false otherwise
     */
    public static boolean drownedOneDirection(int[][] board, int row, int col, int incRow, int incCol) {
        while (!(row < 0 || row >= board.length || col < 0 || col >= board[0].length)) {
            if (board[row][col] == N_GUESS_N_SUB || board[row][col] == Y_GUESS_N_SUB) {
                return true;
            }
            if (board[row][col] == N_GUESS_Y_SUB) {
                return false;
            }

            row += incRow;
            col += incCol;
        }

        return true;
    }

    /**
     * check if a sub which the given tile is part of, was drowned
     *
     * @param board current board
     * @param row row of tile to check
     * @param col column of tile to check
     * @return true if sub was drowned, false otherwise
     */
    public static boolean drowned(int[][] board, int row, int col) {
        boolean down = drownedOneDirection(board, row, col, 1, 0);
        boolean up = drownedOneDirection(board, row, col, -1, 0);
        boolean left = drownedOneDirection(board, row, col, 0, -1);
        boolean right = drownedOneDirection(board, row, col, 0, 1);

        return right && left && up && down;
    }

    /**
     * play an attack (print and change board), assuming the play is valid.
     *
     * @param board current board
     * @param row row of tile to attack
     * @param col column of tile to attack
     * @param subNums an array that contains how many subs where left fot each player
     * @param player true if human is attacking, false if computer
     */
    public static void attackTile(int[][] board, int row, int col, int[] subNums, boolean player) {
        if (board[row][col] == N_GUESS_N_SUB) {
            System.out.println("That is a miss!");
            board[row][col] = Y_GUESS_N_SUB;
        } else {
            int playerIndex = player ? BOARD_INDEX_COMP : BOARD_INDEX_PLAYER;
            System.out.println("That is a hit!");
            board[row][col] = Y_GUESS_Y_SUB;
            if (drowned(board, row, col)) {
                subNums[playerIndex]--;
                if (player) {
                    System.out.println("The computer's battleship has been drowned, " + subNums[playerIndex] +
                            " more battleships to go!");
                } else {
                    System.out.println("Your battleship has been drowned, you have left " + subNums[playerIndex] + " more battleships!");
                }
            }
        }
    }

    /**
     * play turn of human
     *
     * @param board current board
     * @param subNums an array with current num of subs for each player
     */
    public static void playTurnPlayer(int[][] board, int[] subNums) {
        System.out.println("Your current guessing board:");
        printBoard(board, false);

        int[] tile = inputAndParseCoordinates(false);
        while (!checkValidAttackTile(board, tile[0], tile[1], false)){
            tile = inputAndParseCoordinates(true);
        }
        attackTile(board, tile[0], tile[1], subNums, true);
    }

    /**
     * play turn of computer
     *
     * @param board current board
     * @param subNums an array with current num of subs for each player
     */
    public static void playTurnComputer(int[][] board, int[] subNums) {
        int[] tile = new int[2];
        do {
            tile[0] = rnd.nextInt(board.length);  // generate x coordinate
            tile[1] = rnd.nextInt(board[0].length);
        } while (!checkValidAttackTile(board, tile[0], tile[1], true));
        System.out.println("The computer attacked (" + tile[0] + ", " + tile[1] + ")");
        attackTile(board, tile[0], tile[1], subNums, false);
        System.out.println("Your current game board:");
        printBoard(board, true);

    }

    /**
     * play game (not including init of board) until the game ends
     *
     * @param board current board (after initialization)
     * @param subNums an array with current num of subs for each player (after initialization)
     */
    public static void playGame(int[][][] board, int[] subNums) {
        while (true) {
            playTurnPlayer(board[BOARD_INDEX_COMP], subNums);
            if (subNums[BOARD_INDEX_COMP] == 0) {
                System.out.println("You won the game!");
                return;
            }

            playTurnComputer(board[BOARD_INDEX_PLAYER], subNums);
            if (subNums[BOARD_INDEX_PLAYER] == 0) {
                System.out.println("You lost ):");
                return;
            }
        }
    }

    /**
     * play a battleship game between a player and a computer
     */
    public static void battleshipGame() {
        int[] subNums = new int[2];
        int[][][] board = initBoard(subNums);

        playGame(board, subNums);
    }

    public static void main(String[] args) throws IOException {
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
}