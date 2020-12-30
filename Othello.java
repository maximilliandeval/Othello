import java.util.Arrays;

public class Othello {
    // Initialize the board as a class variable
    static char[][] board = new char[8][8];

    public static void main(String[] args) {
        // Initialize all cells to the space character
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                board[y][x] = ' ';
            }
        }
        // Create the 4 starting tokens
        board[3][3] = 'X';
        board[3][4] = 'O';
        board[4][3] = 'O';
        board[4][4] = 'X';

        //char[] fullLine = new char[] {'F','U','L','L','L','I','N','E'};
        //board[7] = fullLine;

        gameLoop();
        
        return;
    }

    // 
    public static boolean isMoveValid(char x, char y) {
        return true;
    }

    // 
    public static void displayBoard() {
        //System.out.println(Arrays.deepToString(board));

        // Print the column labels
        // ASCII value for 'A' is 65
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            char letter = (char) (i+65);
            System.out.print(letter + " ");
        }
        System.out.println();
        // Print every matrix in the cell
        for (int y = 0; y < 8; y++) {
            System.out.println();
            // Print row label
            System.out.print(Integer.toString(8-y) + " ");
            for (int x = 0; x < 8; x++) {
                System.out.print(" " + board[y][x]);
            }
        }
        System.out.println();
        return;
    }

    // MAIN WHILE LOOP
    public static void gameLoop() {
        boolean finished = false;
        while (finished == false) {
            displayBoard();
            finished = true;
        }
    }

    // 
    public static boolean isGameOver() {
        return true;
    }
}