import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Othello {
    static int MaxDepth;
    // "Do not create multiple buffered wrappers on a single InputStream":
    static Scanner ScannerObj = new Scanner(System.in);

    public static void main(String[] args) {
        int width = promptWidth();
        int numHumans = promptParticipants();
        MaxDepth = promptDepth(width);
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
        ScannerObj.close();
        return;
    }

    public static int promptWidth() {
        System.out.println();
        System.out.println("Traditionally, Othello is played on an 8x8 board.");
        System.out.println("What size do you want the side of the square board to be?");
        System.out.print("Specify an even integer from 4-26: ");
        int width = 0;
        while (true) {
            try {
                width = Integer.parseInt(ScannerObj.nextLine());
                if (width>=4 && width<=26 && (width%2)==0) {
                    System.out.println();
                    return width;
                } else {
                    System.out.print("Invalid input. Please try again: ");
                }
            } catch (Exception e) {
                System.out.print("Invalid input. Please try again: ");
                continue;
            }
        }
    }

    public static int promptDepth(int width) {
        System.out.println("How many moves ahead would you like the AI to consider?");
        int totalLevels = width * width - 4;
        System.out.print("Please enter an integer from 1-" + totalLevels + ": ");
        int input = 0;
        while (true) {
            try {
                input = Integer.parseInt(ScannerObj.nextLine());
                if (input>=0 && input<=totalLevels) {
                    System.out.println();
                    return input;
                } else {
                    System.out.print("Invalid input. Please try again: ");
                }
            } catch (Exception e) {
                System.out.print("Invalid input. Please try again: ");
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
            System.out.println("PLAYER " + player + "'S TURN:");
            System.out.println();
            displayBoard(board);
            System.out.println();
            int[] scores = getScores(board);
            System.out.println("Player 1 score: " + scores[0]);
            System.out.println("Player 2 score: " + scores[1]); 
            System.out.println();
            
            if ((hasPossibleMoves(board, player).length) !=0 ) {
                if (participants[player-1].equals("Human")) {
                    promptPlayerMove(board, player); // Performs move as well
                } else {
                    pause(board.length);
                    // Get best move using minimax helper function
                    String bestMove = getBestMove(board, player, MaxDepth);
                    int x = bestMove.charAt(0)-65; // (ASCII value for 'A' is 65)
                    int y = (Character.getNumericValue(bestMove.charAt(1))-board.length) * -1;
                    performMove(board, player, x, y, true);
                }
            } else {
                System.out.println("Current player does not have any valid possible moves.  Skipping turn...");
            }
            // Switch player
            player = (player % 2) + 1;
            System.out.println("");
            finished = isGameOver(board);
        }
        return;
    }

    public static String getBestMove(char[][] board, int currentPlayer, int depth) {
        String[] possibleMoves = hasPossibleMoves(board, currentPlayer);
        int maxScore = -1 * (board.length * board.length + 1); // int maxMove = -infinity
        String bestMove = "";
        for (String move : possibleMoves) {
            int resultingMoveScore = minimax(board, depth, maxScore, (-1 * maxScore), true, currentPlayer);
            if (resultingMoveScore > maxScore) {
                maxScore = resultingMoveScore;
                bestMove = move;
            }
        }
        return bestMove;
    }

    // Temporarily pause the program so that the player can see what the board looks like
    // before the AI plays their move
    public static void pause(int width) {
        try {
            if (width >= 10) {
                width = width + 1;
            }
            width = width*2 + 2;
            int totalPauseTimeMS = 3000;
            for (int i = 0; i < width; i++) {
                TimeUnit.MILLISECONDS.sleep(totalPauseTimeMS/width); // totalPauseTimeMS/width
                System.out.print("-");
            }
            System.out.println("");
        } catch (InterruptedException e) {
            // If this function somehow fails, it is not a huge issue because the user
            // can just scroll up
        }
    }

    public static int minimax(char[][] board, int depth, int alpha, int beta, boolean maximizingPlayer, int currentPlayer) {
        String[] possibleMovesAI = hasPossibleMoves(board, currentPlayer);
        String[] possibleMovesOpponent = hasPossibleMoves(board, ((currentPlayer%2)+1));
        // BASE CASE
        // if depth==0 or game over in position
        if (depth==0 || (maximizingPlayer && possibleMovesAI.length==0) || (!maximizingPlayer && possibleMovesOpponent.length==0)) {
            //return static evaluation of position
            return getScores(board)[currentPlayer-1];
        }

        // RECURSIVE CASES
        // AI's turn:
        if (maximizingPlayer) {
            int maxEval = -1 * (board.length * board.length + 1);
            for (String move: possibleMovesAI) {
                // Clone 2D array
                char[][] boardCopy = Arrays.stream(board).map(char[]::clone).toArray(char[][]::new);
                int x = move.charAt(0)-65; // (ASCII value for 'A' is 65)
                int y = (Character.getNumericValue(move.charAt(1))-board.length) * -1;
                performMove(boardCopy, currentPlayer, x, y, true);
                int eval = minimax(boardCopy, depth-1, alpha, beta, false, currentPlayer);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        // Opponent's turn:
        } else {
            int minEval = board.length * board.length + 1; // minEval = +infinity
            for (String move : possibleMovesOpponent) {
                // Clone 2D array
                char[][] boardCopy = Arrays.stream(board).map(char[]::clone).toArray(char[][]::new);
                int x = move.charAt(0)-65; // (ASCII value for 'A' is 65)
                int y = (Character.getNumericValue(move.charAt(1))-board.length) * -1;
                performMove(boardCopy, currentPlayer, x, y, false);
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
        System.out.println("  - 0 humans (2 AI bots)");
        System.out.println("  - 1 humans (1 AI bot)");
        System.out.println("  - 2 humans (0 AI bots)");
        while (true) {
            System.out.print("Please select 0, 1, or 2: ");
            String input = ScannerObj.nextLine();
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
    // Returns an array of length 2 holding the participants scores
    // idx 0: player 1's score
    // idx 1: plauer 2's score
    public static int[] getScores(char[][] board) {
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
        return new int[] {p1Counter, p2Counter};
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
        //System.out.println("Possible moves: " + possibleMoves.toString());
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
        int width = board.length;
        while (true) {
            // Get input from the user
            System.out.print("Move? ");
            String userInput = ScannerObj.nextLine();
            if (userInput.length() != 2) {
                System.out.println("Your input must have a length of 2");
                continue;
            }
            int x = Character.toUpperCase(userInput.charAt(0))-65; // (ASCII value for 'A' is 65
            int y = (Character.getNumericValue(userInput.charAt(1))-board.length) * -1;
            // Check if input is valid
            if ((0<=x && x<=width-1) && (0<=y && y<=width-1)) {
                // Check if move is valid
                if (performMove(board, player, x, y, true) != 0) {
                    break;
                } else {
                    System.out.println("Invalid move");
                }
            } else {
                // (ASCII value for 'A' is 65)
                char finalLetter = (char) (width + 64);
                System.out.println("The first value must be a letter from A-" + finalLetter);
                System.out.println("The second value must be an integer from 1-" + width);
                System.out.println("Please try again...");
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
            displayBoard(board);
            System.out.println();
            System.out.println("Neither player has any possible moves...");
            System.out.println("GAME OVER");
            int[] finalScores = getScores(board);
            if (finalScores[0] > finalScores[1]) {
                System.out.println("Player 1 is the winner!");
            } else if (finalScores[1] > finalScores[0]) {
                System.out.println("Player 2 is the winner!");
            } else {
                System.out.println("The game is a tie!");
            }
            return true;
        } else {
            return false;
        }
    }

}