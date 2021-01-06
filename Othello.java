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
            System.out.println("");
            displayScore();
            System.out.println("");
            System.out.println("PLAYER " + player + "'S TURN");
            if (hasPossibleMoves(player)) {
                promptPlayer(player);
            } else {
                System.out.println("Current player does not have any valid possible moves.  Skipping turn...");
            }
            // Switch player
            player = (player % 2) + 1;
            System.out.println("");
            finished = isGameOver();
        }
        System.out.println("GAME OVER");
        int winner = displayScore();
        if (winner == 1) {
            System.out.println("Player 1 is the winner!");
        } else if (winner == 2) {
            System.out.println("Player 2 is the winner!");
        } else {
            System.out.println("The game is a tie!");
        }
        return;
    }

    // Display score
    // Returns an int indicating which player is currently leading
    // (0 represents the score is tied)
    public static int displayScore() {
        int p1Counter = 0;
        int p2Counter = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] == 'X') {
                    p1Counter++;
                } else if (board[y][x] == 'O') {
                    p2Counter++;
                }
            }
        }
        System.out.println("Player 1 score: " + p1Counter);
        System.out.println("Player 2 score: " + p2Counter); 
        if (p1Counter > p2Counter) {
            return 1;
        } else if (p2Counter > p1Counter) {
            return 2;
        } else {
            return 0;
        }
    }

    // Checks if the given user has any possible valid moves
    public static boolean hasPossibleMoves(int player) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] != ' ') {
                    return true;
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

        try {
            if (board[y][x] != ' ') {
                return false;
            }

            /* According to the official rules of the board game:
            "You may not skip over your own color disk to outflank
            an opposing disk."
            As such I will ___________ */

            boolean madeChanges = false;

            // Check above specified coordinates
            boolean encounteredOpponent = false;
            for (int i = y; i >= 0; i--) {
                if (i!=y && board[i][x]==' ') {
                    break;
                } else if (i==(y-1) && board[i][x] != desiredChar) {
                    encounteredOpponent = true;
                } else if (board[i][x]==desiredChar && encounteredOpponent) {
                    for (int j = i; j <= y; j++) {
                        board[j][x] = desiredChar;
                    }
                    madeChanges = true;
                    break;
                }
            }
            encounteredOpponent = false;

            // Check below
            for (int i = y; i < 8; i++) {
                if (i!=y && board[i][x]==' ') {
                    break;
                } else if (i==(y+1) && board[i][x] != desiredChar) {
                    encounteredOpponent = true;
                } else if (board[i][x]==desiredChar && encounteredOpponent) {
                    for (int j = i; j >= y; j--) {
                        board[j][x] = desiredChar;
                    }
                    madeChanges = true;
                    break;
                }
            }
            encounteredOpponent = false;

            // Check right
            for (int i = x; i < 8; i++) {
                if (i!=x && board[y][i]==' ') {
                    break;
                } else if (i==(x+1) && board[y][i] != desiredChar) {
                    encounteredOpponent = true;
                } else if (board[y][i]==desiredChar && encounteredOpponent) {
                    for (int j = i; j >= x; j--) {
                        board[y][j] = desiredChar;
                    }
                    madeChanges = true;
                    break;
                }
            }
            encounteredOpponent = false;

            // Check left
            for (int i = x; i >= 0; i--) {
                if (i!=x && board[y][i]==' ') {
                    break;
                } else if (i==(x-1) && board[y][i] != desiredChar) {
                    encounteredOpponent = true;
                } else if (board[y][i]==desiredChar && encounteredOpponent) {
                    for (int j = i; j <= x; j++) {
                        board[y][j] = desiredChar;
                    }
                    madeChanges = true;
                    break;
                }
            }
            encounteredOpponent = false;

            // Check above-left diagonal
            for (int iy = y, ix = x; iy >= 0 && ix >= 0; ix--, iy--) {
                if (ix!=x && board[iy][ix]==' ') {
                    break;
                } else if (ix==(x-1) && board[iy][ix] != desiredChar) {
                    encounteredOpponent = true;
                } else if (board[iy][ix]==desiredChar && encounteredOpponent) {
                    for (int jy = iy, jx = ix; jx <= x && jy <= y; jy++, jx++) {
                        board[jy][jx] = desiredChar;
                    }
                    madeChanges = true;
                    break;
                }
            }
            encounteredOpponent = false;

            // Check above-right diagonal
            for (int iy = y, ix = x; iy >= 0 && ix < 8; ix++, iy--) {
                if (ix!=x && board[iy][ix]==' ') {
                    break;
                } else if (ix==(x+1) && board[iy][ix] != desiredChar) {
                    encounteredOpponent = true;
                } else if (board[iy][ix]==desiredChar && encounteredOpponent) {
                    for (int jy = iy, jx = ix; jx >= x && jy <= y; jy++, jx--) {
                        board[jy][jx] = desiredChar;
                    }
                    madeChanges = true;
                    break;
                }
            }
            encounteredOpponent = false;

            // Check below-left diagonal
            for (int iy = y, ix = x; iy < 8 && ix >= 0; ix--, iy++) {
                if (ix!=x && board[iy][ix]==' ') {
                    break;
                } else if (ix==(x-1) && board[iy][ix] != desiredChar) {
                    encounteredOpponent = true;
                } else if (board[iy][ix]==desiredChar && encounteredOpponent) {
                    for (int jy = iy, jx = ix; jx <= x && jy >= y; jy--, jx++) {
                        board[jy][jx] = desiredChar;
                    }
                    madeChanges = true;
                    break;
                }
            }
            encounteredOpponent = false;

            // Check below-right diagonal
            for (int iy = y, ix = x; iy < 8 && ix < 8; ix++, iy++) {
                if (ix!=x && board[iy][ix]==' ') {
                    break;
                } else if (ix==(x+1) && board[iy][ix] != desiredChar) {
                    encounteredOpponent = true;
                } else if (board[iy][ix]==desiredChar && encounteredOpponent) {
                    for (int jy = iy, jx = ix; jx >= x && jy >= y; jy--, jx--) {
                        board[jy][jx] = desiredChar;
                    }
                    madeChanges = true;
                    break;
                }
            }
            encounteredOpponent = false;

            // Check if a move was performed:
            if (madeChanges) {
                return true;
            } else {
                return false;
            }
        
        } catch (ArrayIndexOutOfBoundsException exception) {
            System.out.println("The first value must be a letter from A-F");
            System.out.println("The second value must be an integer from 1-8");
            System.out.println("Please try again...");
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
            // Get input from the user
            System.out.print("Move? ");
            String userInput = scannerObj.nextLine();
            if (userInput.length() != 2) {
                System.out.println("Your input must have a length of 2");
                continue;
            }
            int x = Character.toUpperCase(userInput.charAt(0))-65; // (ASCII value for 'A' is 65)
            int y = (Character.getNumericValue(userInput.charAt(1))-8) * -1;
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