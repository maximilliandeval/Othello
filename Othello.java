import java.util.Scanner;

public class Othello {
    static char[][] board = new char[8][8];
    // "Do not create multiple buffered wrappers on a single InputStream":
    static Scanner scannerObj = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize all cells to ' '
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
        scannerObj.close();
        return;
    }

    // MAIN WHILE LOOP
    public static void gameLoop() {
        boolean finished = false;
        // Player 1 is represented by X on the board
        // Player 2 is represented by O on the board
        int player = 1;
        while (finished == false) {
            displayBoard();
            System.out.println("PLAYER " + player + "'S TURN");

            promptPlayer(player);
            finished = isGameOver();
        }
        return;
    }

    // Checks if the given user has any possible valid moves
    public static boolean hasPossibleMoves(int player) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] != ' ') {
                    System.out.println("PLACEHOLDER"); //
                }
            }
        }
        return true;
    }

    // Return true if the move is valid.  Return false if it is invalid
    public static boolean performMove(int player, int x, int y) {
        char desiredChar;
        if (player == 1) {
            desiredChar = 'X';
        } else {
            desiredChar = 'O';
        }

        if (board[y][x] != ' ') {
            return false;
        }

        /* According to the official rules of the board game:
        "You may not skip over your own color disk to outflank
        an opposing disk."
        As such I will ___________ */
        boolean madeChanges = false;
        // Check above
        for (int i = y; i >= 0; i--) {
            if (i!=y && board[i][x]==' ') {
                break;
            }
            if (i!=y && board[i][x]==desiredChar) {
                for (int j = i; j <= y; j++) {
                    board[j][x] = desiredChar;
                }
                madeChanges = true;
                break;
            }
        }
        // Check below

        // Chech left

        // Check right

        // Check 
        if (madeChanges) {
            return true;
        } else {
            return false;
        }
    }

    // 
    public static void displayBoard() {
        // Print the column labels
        // (ASCII value for 'A' is 65)
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            char letter = (char) (i+65);
            System.out.print(letter + " ");
        }
        System.out.println();
        // Print every matrix in the cell
        for (int y = 0; y < 8; y++) {
            System.out.println();
            // Print the row labels
            System.out.print(Integer.toString(8-y) + " ");
            for (int x = 0; x < 8; x++) {
                System.out.print(" " + board[y][x]);
            }
        }
        System.out.println();
        return;
    }

    public static void promptPlayer(int player) {
        while (true) {
            // Check if the user has any valid moves

            // Get input from the user
            System.out.print("Move? ");
            String userInput = scannerObj.nextLine();
            int x = Character.toUpperCase(userInput.charAt(0))-65; // (ASCII value for 'A' is 65)
            int y = (Character.getNumericValue(userInput.charAt(1))-8) * -1;
            System.out.println("x: " + x);
            System.out.println("y: " + y);
            // Check if move is valid
            if (performMove(player, x, y)) {
                break;
            } else {
                System.out.println("Invalid move");
            }
            
        }
        return;
    }

    // 
    public static boolean isGameOver() {
        // Check if all of the spots on the board are filled:
        // (if this is the case, it is faster to perform the following check 
        // rather than call hasPossibleMoves() twice)
        boolean openSpots = false;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] == ' ') {
                    openSpots = true;
                }
            }
        }
        if (openSpots == false) {
            System.out.println("All spots have been filled");
            System.out.println("GAME OVER");
            return true;
        }

        // It is possible for a game to end before all 64 squares are filled
        else if (!hasPossibleMoves(1) && !hasPossibleMoves(2) ) {
            System.out.println("Neither player has any possible moves");
            System.out.println("GAME OVER");
            return true;
        }

        else {
            return false;
        }
    }

}