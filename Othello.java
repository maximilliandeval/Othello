import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

public class Othello {
    // "Do not create multiple buffered wrappers on a single InputStream":
    static Scanner scannerObj = new Scanner(System.in);

    public static void main(String[] args) {
        int width = promptWidth();
        int numHumans = promptParticipants();
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

        /* char[] fullLine10 = new char[] {'F','U','L','L','L','I','N','E','9','1'};
        board[0] = fullLine10;
        board[3] = fullLine10; */

        // char[] fullLine4 = new char[] {'F','U','L','L'};
        // board[0] = fullLine4;

        gameLoop(board, numHumans);
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
    public static void gameLoop(char[][] board, int numHumans) {
        boolean finished = false;
        // Player 1 is represented by X on the board
        // Player 2 is represented by O on the board
        int player = 1;
        String[] participants;
        if (numHumans==2) {
            participants = new String[] {"Human", "Human"};
        } else if (numHumans==1) {
            participants = new String[] {"Human", "AI"};
        } else {
            participants = new String[] {"AI", "AI"};
        }
        while (finished == false) {
            displayBoard(board);
            System.out.println("");
            displayScore(board);
            System.out.println("");
            System.out.println("PLAYER " + player + "'S TURN");
            if ((hasPossibleMoves(board, player).length) !=0 ) {
                if (participants[player-1].equals("Human")) {
                    promptPlayerMove(board, player);
                } else {
                    // minimiax AI implementation
                    //minimax(currentPosition, 3, -∞, +∞, true);
                    int relativeInfinity = board.length * board.length + 1;
                    minimax(board, 3, (-1 * relativeInfinity), relativeInfinity, true, player);
                }
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

    public static int minimax(char[][] board, int depth, int alpha, int beta, boolean maximizingPlayer, int currentPlayer) {
        String[] possibleMoves = hasPossibleMoves(board, currentPlayer);
        // Base case
        // if depth == 0 or game over in position
        if (depth==0 || possibleMoves.length==0) {
            //return static evaluation of position
            return 1;
        }
        // Recursive cases
        if (maximizingPlayer) {
            int maxEval = -1 * (board.length * board.length + 1);
            for (String move: possibleMoves) {
                // Clone 2D array
                char[][] boardCopy = Arrays.stream(board).map(char[]::clone).toArray(char[][]::new);
                int x = move.charAt(0)-65; // (ASCII value for 'A' is 65)
                int y = Character.getNumericValue(move.charAt(1));
                int moveScore = performMove(boardCopy, currentPlayer, x, y, true);
                int eval = minimax(boardCopy, depth-1, alpha, beta, false, currentPlayer);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;

        } else {
            int minEval = board.length * board.length + 1; // minEval = +infinity
            for (String move : possibleMoves) {
                // Clone 2D array
                char[][] boardCopy = Arrays.stream(board).map(char[]::clone).toArray(char[][]::new);
                int x = move.charAt(0)-65; // (ASCII value for 'A' is 65)
                int y = Character.getNumericValue(move.charAt(1));
                int moveScore = performMove(boardCopy, currentPlayer, x, y, false);
                int eval = minimax(boardCopy, depth - 1, alpha, beta, true, currentPlayer);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    // Ask the user how many human players will be participating in the game
    public static int promptParticipants() {
        System.out.println("How many human players will be participating in the game?");
        System.out.println("0 humans (2 AI bots)");
        System.out.println("1 humans (1 AI bot)");
        System.out.println("2 humans (0 AI bots)");
        while (true) {
            System.out.print("Please select 0, 1, or 2: ");
            String input = scannerObj.nextLine();
            System.out.println();
            if (input.equals("0")) {
                return 0;
            } else if (input.equals("1")) {
                return 1;
            } else if (input.equals("2")) {
                return 2;
            } else {
                System.out.println("Invalid input");
            }
        }
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

    // Returns an array of strings indicating the user's possible moves
    // If an emptry string is returned, it means that the user does not have any possible moves
    public static String[] hasPossibleMoves(char[][] board, int player) {
        ArrayList<String> possibleMoves = new ArrayList<String>();
        int width = board.length;
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                if (performMove(board, player, x, y, false) != 0) {
                    // (ASCII value for 'A' is 65)
                    char column = (char) (65 + x);
                    String row = Integer.toString(width-y);
                    String combined = "" + column + row;
                    possibleMoves.add(combined);
                }
            }
        }
        System.out.println("Possible moves: " + possibleMoves.toString());
        // Convert arraylist to array
        return possibleMoves.toArray(new String[possibleMoves.size()]);
    }

    // Return true if the move is valid.  Return false if it is invalid
    public static int performMove(char[][] board, int player, int x, int y, boolean updateBoard) {
        char desiredChar;
        if (player == 1) {
            desiredChar = 'X';
        } else {
            desiredChar = 'O';
        }
        int width = board.length;

        try {
            if (board[y][x] != ' ') {
                return 0;
            }

            /* According to the official rules of the board game:
            "You may not skip over your own color disk to outflank
            an opposing disk."
            As such I will ___________ */

            int scoreTracker = 0;

            // Check above specified coordinates
            boolean encounteredOpponent = false;
            for (int i = y; i >= 0; i--) {
                if (i!=y && board[i][x]==' ') {
                    break;
                } else if (i==(y-1) && board[i][x] != desiredChar) {
                    encounteredOpponent = true;
                } else if (board[i][x]==desiredChar && encounteredOpponent) {
                    for (int j = i; j <= y; j++) {
                        scoreTracker++;
                        if (updateBoard) {
                            board[j][x] = desiredChar;
                        }
                    }
                    scoreTracker--;
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
                        scoreTracker++;
                        if (updateBoard) {
                            board[j][x] = desiredChar;
                        }
                    }
                    scoreTracker--;
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
                        scoreTracker++;
                        if (updateBoard) {
                            board[y][j] = desiredChar;
                        }
                    }
                    scoreTracker--;
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
                        scoreTracker++;
                        if (updateBoard) {
                            board[y][j] = desiredChar;
                        }
                    }
                    scoreTracker--;
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
                        scoreTracker++;
                        if (updateBoard) {
                            board[jy][jx] = desiredChar;
                        }
                    }
                    scoreTracker--;
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
                        scoreTracker++;
                        if (updateBoard) {
                            board[jy][jx] = desiredChar;
                        }
                    }
                    scoreTracker--;
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
                        scoreTracker++;
                        if (updateBoard) {
                            board[jy][jx] = desiredChar;
                        }
                    }
                    scoreTracker--;
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
                        scoreTracker++;
                        if (updateBoard) {
                            board[jy][jx] = desiredChar;
                        }
                    }
                    scoreTracker--;
                    break;
                }
            }
            encounteredOpponent = false;

            return scoreTracker;
        
        } catch (ArrayIndexOutOfBoundsException e) {
            // (ASCII value for 'A' is 65)
            char finalLetter = (char) (width + 64);
            System.out.println("The first value must be a letter from A-" + finalLetter);
            System.out.println("The second value must be an integer from 1-" + width);
            System.out.println("Please try again...");
            return 0;
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
            if (width>=10 && (width-y)<10) {
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
            if (performMove(board, player, x, y, true) != 0) {
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
        if ((hasPossibleMoves(board, 1).length==0) && (hasPossibleMoves(board, 2).length==0)) {
            displayScore(board);
            System.out.println("Neither player has any possible moves");
            System.out.println("GAME OVER");
            return true;
        } else {
            return false;
        }
    }

}