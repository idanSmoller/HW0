public class Main {
    static final int N_GUESS_N_SUB = 0;
    static final int N_GUESS_Y_SUB = 1;
    static final int Y_GUESS_N_SUB = 2;
    static final int Y_GUESS_Y_SUB = 3;

    static final char INPUT_DEVIDER = 'X';

    public static int[] parseInput(String str)
    {
        int i = 0;
        for (i = 0; i < str.length(); i++)
        {
            if (str.charAt(i) == INPUT_DEVIDER)
            {
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

    public static void main(String[] args) {
        int[] arr = parseInput("4X2");
        for (int i = 0; i < 2; i++)
        {
            System.out.println(arr[i]);
        }
    }
}