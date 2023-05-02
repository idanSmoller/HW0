import java.util.Random;
import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);    // delete to what scanner equal when finish
    public static Random rnd = new Random();

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
     *
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

    /**
     * parse string input in format "x, y, orientation" into an int array in format [x, y, orientation]
     *
     * @param str the string input
     * @return the formatted int array
     */
    public static int[] parseCoordinateOrientation(String str) {
        int i = 0;
        int found = 0;
        int ret[] = new int[3];
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
        int ret[] = new int[2];
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
        System.out.println("Enter the battleship sizes");
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
     * @return the sub as an array of size 2: [x, y]
     */
    public static int[] inputAndParseCoordinates() {
        System.out.println("Enter a tile to attack");
        return parseCoordinate(scanner.nextLine());
    }

    /**
     * input and parse the location and the orientation of a sub
     *
     * @param size the size of the sub
     * @return the sub as an array of size 3: [x, y, orientation] (x and y of the top left corner of the sub)
     */
    public static int[] inputAndParseCoordinatesOrientation(int size) {
        System.out.println("Enter location and orientation for battleship of size " + size);
        return parseCoordinateOrientation(scanner.nextLine());
    }

    /**
     * check if the tile (x, y) in the board is currently empty
     *
     * @param board the board
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @return whether the spot is available or not
     */
    public static boolean notOverlapping(int[][] board, int x, int y) {
        return board[y][x] == N_GUESS_N_SUB;
    }

    /**
     * check if the surrounding of a given tile is empty
     *
     * @param board the board
     * @param x     the tile's x coordinate
     * @param y     the tile's x coordinate
     * @return whether the tile's surrounding is valid for sub placement or not
     */
    public static boolean validSurrounding(int[][] board, int x, int y) {
        boolean rightEdge = (x == board[0].length - 1);
        boolean leftEdge = (x == 0);
        boolean bottomEdge = (y == board.length - 1);
        boolean topEdge = (y == 0);
        int startX = leftEdge ? x : x - 1;
        int endX = rightEdge ? x : x + 1;
        int startY = topEdge ? y : y - 1;
        int endY = bottomEdge ? y : y + 1;
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                if (board[j][i] == N_GUESS_Y_SUB) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean guessed(int tile) {
        return tile == Y_GUESS_N_SUB || tile == Y_GUESS_Y_SUB;
    }

    public static boolean checkValidAttackTile(int[][] board, int x, int y) {
        if (!(x >= 0 && x < board[0].length && y >= 0 && y < board.length)) {
            System.out.println("Illegal tile, try again!");
            return false;
        }
        if (guessed(board[y][x])) {
            System.out.println("Tile already attacked, try again!");
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
        if (sub[SUB_INDEX_X] >= board[0].length || sub[SUB_INDEX_X] < 0 ||
                sub[SUB_INDEX_Y] >= board.length || sub[SUB_INDEX_Y] < 0) {
            if (!mute) {
                System.out.println("Illegal tile, try again!");
            }
            return false;
        }

        // check sub in boundaries
        if (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_HORIZONTAL) {
            if (sub[SUB_INDEX_X] + size > board[0].length) {
                if (!mute) {
                    System.out.println("Battleship exceeds the boundaries of the board, try again!");
                }
                return false;
            }
        } else {
            if (sub[SUB_INDEX_Y] + size > board.length) {
                if (!mute) {
                    System.out.println("Battleship exceeds the boundaries of the board, try again!");
                }
                return false;
            }
        }

        // check battleship overlaps another battleships or adjacent
        for (int i = 0; i < size; i++) {
            int currLocationX = sub[SUB_INDEX_X] + (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_HORIZONTAL ? i : 0);
            int currLocationY = sub[SUB_INDEX_Y] + (sub[SUB_INDEX_ORIENTATION] == ORIENTATION_VERTICAL ? i : 0);

            // check overlaps
            if (!notOverlapping(board, currLocationX, currLocationY)) {
                if (!mute) {
                    System.out.println("Battleship overlaps another battleship, try again!");
                }
                return false;
            }

            // check adjacent
            if (!validSurrounding(board, currLocationX, currLocationY)) {
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
                board[sub[SUB_INDEX_Y] + i][sub[SUB_INDEX_X]] = N_GUESS_Y_SUB;
            } else {
                board[sub[SUB_INDEX_Y]][sub[SUB_INDEX_X] + i] = N_GUESS_Y_SUB;
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
        int m = board[0].length;
        int n = board.length;

        do {
            sub[0] = rnd.nextInt(m);
            sub[1] = rnd.nextInt(n);
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
        int[] sub;
        do {
            sub = inputAndParseCoordinatesOrientation(size);

        } while (!checkValidSub(board, sub, size, false));

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

    public static void countSubs(int[] subSizes, int[] subNums) {
        int sum = 0;
        for (int size : subSizes) {
            sum += size;
        }
        subNums[0] = sum;
        subNums[1] = sum;
    }

    /**
     * initialize the boards for the player and the computer - input subs and place them on the board
     *
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
                return '-';
            case N_GUESS_Y_SUB:
                return player ? '#' : '-';
            case Y_GUESS_N_SUB:
                return player ? '-' : 'X';
            case Y_GUESS_Y_SUB:
                return player ? 'X' : 'V';
            default:
                return 0;
        }
    }

    public static void printBoard(int[][] board, boolean player) {
        // todo: double digit shit
        System.out.print(" ");

        // print the first row of indexes
        for (int i = 0; i < board[0].length; i++) {
            System.out.print(" " + Integer.toString(i));
        }
        System.out.println();
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            System.out.print(Integer.toString(i));
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(" " + tileToChar(board[i][j], player));
            }
            System.out.println();
        }
        System.out.println();
    }

    public static boolean drownedOneDirection(int[][] board, int x, int y, int incX, int incY) {
        while (!(x < 0 || x >= board[0].length || y < 0 || y >= board.length)) {
            if (board[y][x] == N_GUESS_N_SUB || board[y][x] == Y_GUESS_N_SUB) {
                return true;
            }
            if (board[y][x] == N_GUESS_Y_SUB) {
                return false;
            }

            x += incX;
            y += incY;
        }

        return true;
    }

    public static boolean drowned(int[][] board, int x, int y) {
        boolean right = drownedOneDirection(board, x, y, 1, 0);
        boolean left = drownedOneDirection(board, x, y, -1, 0);
        boolean up = drownedOneDirection(board, x, y, 0, -1);
        boolean down = drownedOneDirection(board, x, y, 0, 1);

        return right && left && up && down;
    }

    public static void attackTile(int[][] board, int x, int y, int[] subNums, boolean player) {
        if (board[y][x] == N_GUESS_N_SUB) {
            System.out.println("That is a miss!");
            board[y][x] = Y_GUESS_N_SUB;
        } else {
            int playerIndex = player ? BOARD_INDEX_COMP : BOARD_INDEX_PLAYER;
            System.out.println("That is a hit!");
            board[y][x] = Y_GUESS_Y_SUB;
            if (drowned(board, x, y)) {
                subNums[playerIndex]--;
                if (player) {
                    System.out.println("The computer's battleship has been drowned, " + subNums[playerIndex] + " more battleship to go!");
                } else {
                    System.out.println("Your battleship has been drowned, you have left " + subNums[playerIndex] + " more battleships!");
                }
            }
        }
    }

    public static void playTurnPlayer(int[][] board, int[] subNums) {
        System.out.println("Your current guessing board:");
        printBoard(board, false);

        int[] tile;
        do {
            tile = inputAndParseCoordinates();
        } while (!checkValidAttackTile(board, tile[0], tile[1]));

        attackTile(board, tile[0], tile[1], subNums, true);
    }

    public static void playTurnComputer(int[][] board, int[] subNums) {
        int[] tile = new int[2];
        do {
            tile[0] = rnd.nextInt(board[0].length);  // generate x coordinate
            tile[1] = rnd.nextInt(board.length);
        } while (!checkValidAttackTile(board, tile[0], tile[1]));
        System.out.println("The computer attacked (" + tile[0] + ", " + tile[1] + ")");
        attackTile(board, tile[0], tile[1], subNums, false);
        System.out.println("Your current game board:");
        printBoard(board, true);

    }

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

    public static void main(String[] args) {
        battleshipGame();
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