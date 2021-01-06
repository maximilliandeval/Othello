import java.util.Scanner;

public class Othello {
    // "Do not create multiple buffered wrappers on a single InputStream":
    static Scanner scannerObj = new Scanner(System.in);

    public static void main(String[] args) {
        int width = promptWidth();
        char[][] board = new char[width][width];

        // Initialize all cells to ' '
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                board[y][x] = ' ';
            }
        }
        // Create the 4 starting tokens
        board[(width/2)-1][(width/2)-1] = 'X';
        board[(width/2)-1][width/2] = 'O';
        board[width/2][(width/2)-1] = 'O';
        board[width/2][width/2] = 'X';

        /* char[] fullLine = new char[] {'F','U','L','L','L','I','N','E','9','1'};
        board[0] = fullLine;
        board[3] = fullLine; */

        gameLoop(board);
        scannerObj.close();
        return;
    }

    public static int promptWidth() {
        System.out.println("Width of board?");
        System.out.println("Specify an even integer from 4-26)");
        int width = 0;
        while (true) {
            try {
                width = Integer.parseInt(scannerObj.nextLine());
                if (width>=4 && width<=26 && (width%2)==0) {
                    return width;
                } else {
                    System.out.println("Invalid width. Please try again: ");
                }
            } catch (Exception e) {
                System.out.println("Invalid width. Please try again: ");
                continue;
            }
        }
    }

    // CENTRAL WHILE LOOP
    public static void gameLoop(char[][] board) {
        boolean finished = false;
        // Player 1 is represented by X on the board
        // Player 2 is represented by O on the board
        int player = 1;
        while (finished == false) {
            displayBoard(board);
            System.out.println("");
            displayScore(board);
            System.out.println("");
            System.out.println("PLAYER " + player + "'S TURN");
            if (hasPossibleMoves(board, player)) {
                promptPlayerMove(board, player);
            } else {
                System.out.println("Current player does not have any valid possible moves.  Skipping turn...");
            }
            // Switch player
            player = (player % 2) + 1;
            System.out.println("");
            finished = isGameOver(board);
        }

        int winner = displayScore(board);
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
    public static int displayScore(char[][] board) {
        int p1Counter = 0;
        int p2Counter = 0;
        int width = board.length;
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
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
    public static boolean hasPossibleMoves(char[][] board, int player) {
        int width = board.length;
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                if (!performMove(board, player, x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Return true if the move is valid.  Return false if it is invalid
    public static boolean performMove(char[][] board, int player, int x, int y) {
        char desiredChar;
        if (player == 1) {
            desiredChar = 'X';
        } else {
            desiredChar = 'O';
        }
        int width = board.length;

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
            for (int i = y; i < width; i++) {
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
            for (int i = x; i < width; i++) {
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
            for (int iy = y, ix = x; iy >= 0 && ix < width; ix++, iy--) {
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
            for (int iy = y, ix = x; iy < width && ix >= 0; ix--, iy++) {
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
            for (int iy = y, ix = x; iy < width && ix < width; ix++, iy++) {
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
        
        } catch (ArrayIndexOutOfBoundsException e) {
            // (ASCII value for 'A' is 65)
            char finalLetter = (char) (width + 64);
            System.out.println("The first value must be a letter from A-" + finalLetter);
            System.out.println("The second value must be an integer from 1-" + width);
            System.out.println("Please try again...");
            return false;
        }
    }

    // 
    public static void displayBoard(char[][] board) {
        int width = board.length;
        // Print the column labels
        // (ASCII value for 'A' is 65)
        System.out.print("   ");
        if (width>=10) {
            System.out.print(" ");
        }
        for (int i = 0; i < width; i++) {
            char letter = (char) (i+65);
            System.out.print(letter + " ");
        }
        System.out.println();
        // Print every matrix in the cell
        for (int y = 0; y < width; y++) {
            System.out.println();
            // Print the row labels
            System.out.print(Integer.toString(width-y) + " ");
            if (width-y < 10) {
                System.out.print(" ");
            }
            for (int x = 0; x < width; x++) {
                System.out.print(" " + board[y][x]);
            }
        }
        System.out.println();
        return;
    }

    public static void promptPlayerMove(char[][] board, int player) {
        while (true) {
            // Get input from the user
            System.out.print("Move? ");
            String userInput = scannerObj.nextLine();
            if (userInput.length() != 2) {
                System.out.println("Your input must have a length of 2");
                continue;
            }
            int x = Character.toUpperCase(userInput.charAt(0))-65; // (ASCII value for 'A' is 65)
            int y = (Character.getNumericValue(userInput.charAt(1))-board.length) * -1;
            // Check if move is valid
            if (performMove(board, player, x, y)) {
                break;
            } else {
                System.out.println("Invalid move");
            }
        }
        return;
    }

    // 
    public static boolean isGameOver(char[][] board) {
        /* // Check if all of the spots on the board are filled:
        // (if this is the case, it is faster to perform the following check 
        // rather than call hasPossibleMoves() twice)
        boolean openSpots = false;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board.length; x++) {
                if (board[y][x] == ' ') {
                    openSpots = true;
                }
            }
        }
        if (openSpots == false) {
            System.out.println("All spots have been filled");
            System.out.println("GAME OVER");
            return true;
        }  else */

        // It is possible for a game to end before all squares on the board have filled
        if (!hasPossibleMoves(board, 1) && !hasPossibleMoves(board, 2) ) {
            System.out.println("Neither player has any possible moves");
            System.out.println("GAME OVER");
            return true;
        } else {
            return false;
        }
    }

}