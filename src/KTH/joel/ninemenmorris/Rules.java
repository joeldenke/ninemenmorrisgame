package KTH.joel.ninemenmorris;

/**
 * @author Jonas Wåhslén, jwi@kth.se.
 * Revised by Anders Lindström, anderslm@kth.se
 */

/*
 * The game board positions
 *
 * 03           06           09
 *     02       05       08
 *         01   04   07
 * 24  23  22        10  11  12
 *         19   16   13
 *     20       17       14
 * 21           18           15
 *
 */

public class Rules
{
    private int[] gameplan;
    private int bluemarker, redmarker;
    private int turn; // player in turn

    public static final int BLUE_MOVES = 1;
    public static final int READ_MOVES = 2;

    public static final int EMPTY_SPACE = 0;
    public static final int BLUE_MARKER = 4;
    public static final int READ_MARKER = 5;

    public Rules() {
        gameplan = new int[25]; // zeroes
        bluemarker = 9;
        redmarker = 9;
        turn = READ_MOVES;
    }

    public int getTurn()
    {
        return turn;
    }

    /**
     * Returns true if a move is successful
     */
    public boolean legalMove(int To, int From, int color) {
        if (color == turn) {
            if (turn == READ_MOVES) {
                if (redmarker >= 0) {
                    if (gameplan[To] == EMPTY_SPACE) {
                        gameplan[To] = READ_MARKER;
                        redmarker--;
                        turn = BLUE_MOVES;
                        return true;
                    }
                }
				/*else*/
                if (gameplan[To] == EMPTY_SPACE) {
                    boolean valid = isValidMove(To, From);
                    if (valid == true) {
                        gameplan[To] = READ_MARKER;
                        turn = BLUE_MOVES;
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (bluemarker >= 0) {
                    if (gameplan[To] == EMPTY_SPACE) {
                        gameplan[To] = BLUE_MARKER;
                        bluemarker--;
                        turn = READ_MOVES;
                        return true;
                    }
                }
                if (gameplan[To] == EMPTY_SPACE) {
                    boolean valid = isValidMove(To, From);
                    if (valid == true) {
                        gameplan[To] = BLUE_MARKER;
                        turn = READ_MOVES;
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * Returns true if position "to" is part of three in a row.
     */
    public boolean remove(int to) {

        if ((to == 1 || to == 4 || to == 7) && gameplan[1] == gameplan[4]
                && gameplan[4] == gameplan[7]) {
            return true;
        } else if ((to == 2 || to == 5 || to == 8)
                && gameplan[2] == gameplan[5] && gameplan[5] == gameplan[8]) {
            return true;
        } else if ((to == 3 || to == 6 || to == 9)
                && gameplan[3] == gameplan[6] && gameplan[6] == gameplan[9]) {
            return true;
        } else if ((to == 7 || to == 10 || to == 13)
                && gameplan[7] == gameplan[10] && gameplan[10] == gameplan[13]) {
            return true;
        } else if ((to == 8 || to == 11 || to == 14)
                && gameplan[8] == gameplan[11] && gameplan[11] == gameplan[14]) {
            return true;
        } else if ((to == 9 || to == 12 || to == 15)
                && gameplan[9] == gameplan[12] && gameplan[12] == gameplan[15]) {
            return true;
        } else if ((to == 13 || to == 16 || to == 19)
                && gameplan[13] == gameplan[16] && gameplan[16] == gameplan[19]) {
            return true;
        } else if ((to == 14 || to == 17 || to == 20)
                && gameplan[14] == gameplan[17] && gameplan[17] == gameplan[20]) {
            return true;
        } else if ((to == 15 || to == 18 || to == 21)
                && gameplan[15] == gameplan[18] && gameplan[18] == gameplan[21]) {
            return true;
        } else if ((to == 1 || to == 22 || to == 19)
                && gameplan[1] == gameplan[22] && gameplan[22] == gameplan[19]) {
            return true;
        } else if ((to == 2 || to == 23 || to == 20)
                && gameplan[2] == gameplan[23] && gameplan[23] == gameplan[20]) {
            return true;
        } else if ((to == 3 || to == 24 || to == 21)
                && gameplan[3] == gameplan[24] && gameplan[24] == gameplan[21]) {
            return true;
        } else if ((to == 22 || to == 23 || to == 24)
                && gameplan[22] == gameplan[23] && gameplan[23] == gameplan[24]) {
            return true;
        } else if ((to == 4 || to == 5 || to == 6)
                && gameplan[4] == gameplan[5] && gameplan[5] == gameplan[6]) {
            return true;
        } else if ((to == 10 || to == 11 || to == 12)
                && gameplan[10] == gameplan[11] && gameplan[11] == gameplan[12]) {
            return true;
        } else if ((to == 16 || to == 17 || to == 18)
                && gameplan[16] == gameplan[17] && gameplan[17] == gameplan[18]) {
            return true;
        }
        return false;
    }

    /**
     * Request to remove a marker for the selected player.
     * Returns true if the marker where successfully removed
     */
    public boolean remove(int From, int color) {
        if (gameplan[From] == color) {
            gameplan[From] = EMPTY_SPACE;
            return true;
        } else
            return false;
    }

    /**
     *  Returns true if the selected player have less than three markerss left.
     */
    public boolean win(int color) {
        int countMarker = 0;
        int count = 0;
        while (count < 23) {
            if (gameplan[count] != EMPTY_SPACE && gameplan[count] != color)
                countMarker++;
            count++;
        }
        if (bluemarker <= 0 && redmarker <= 0 && countMarker < 3)
            return true;
        else
            return false;
    }

    /**
     * Returns EMPTY_SPACE = 0 BLUE_MARKER = 4 READ_MARKER = 5
     */
    public int board(int From) {
        return gameplan[From];
    }

    /**
     * Check whether this is a legal move.
     */
    private boolean isValidMove(int to, int from) {

        if(to != EMPTY_SPACE) return false;

        switch (to) {
            case 1:
                return (from == 4 || from == 22);
            case 2:
                return (from == 5 || from == 23);
            case 3:
                return (from == 6 || from == 24);
            case 4:
                return (from == 1 || from == 7 || from == 5);
            case 5:
                return (from == 4 || from == 6 || from == 2 || from == 8);
            case 6:
                return (from == 3 || from == 5 || from == 9);
            case 7:
                return (from == 4 || from == 10);
            case 8:
                return (from == 5 || from == 11);
            case 9:
                return (from == 6 || from == 12);
            case 10:
                return (from == 11 || from == 7 || from == 13);
            case 11:
                return (from == 10 || from == 12 || from == 8 || from == 14);
            case 12:
                return (from == 11 || from == 15 || from == 9);
            case 13:
                return (from == 16 || from == 10);
            case 14:
                return (from == 11 || from == 17);
            case 15:
                return (from == 12 || from == 18);
            case 16:
                return (from == 13 || from == 17 || from == 19);
            case 17:
                return (from == 14 || from == 16 || from == 20 || from == 18);
            case 18:
                return (from == 17 || from == 15 || from == 21);
            case 19:
                return (from == 16 || from == 22);
            case 20:
                return (from == 17 || from == 23);
            case 21:
                return (from == 18 || from == 24);
            case 22:
                return (from == 1 || from == 19 || from == 23);
            case 23:
                return (from == 22 || from == 2 || from == 20 || from == 24);
            case 24:
                return (from == 3 || from == 21 || from == 23);
        }
        return false;
    }
}
