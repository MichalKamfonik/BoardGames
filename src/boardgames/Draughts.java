package boardgames;

import java.util.List;

class Draughts extends Game {
    private final int[] KingsNoCapture; // table of kings moves without capture - 15 moves each means a draw
    private final BoardGames bG;    // reference to GUI for requesting refresh and showing messages

    public Draughts(BoardGames bG, Player[] players) {
        this.bG = bG;
        this.board = new DraughtsBoard(8, 8);
        this.players = players;
        KingsNoCapture = new int[players.length];
    }

    @Override
    public void run() {
        int round = 0;

        while (true) {
            int currentPlayer = round % players.length;             // current player number
            int otherPlayer = (round + 1) % players.length;         // the other player number
            Player player = players[currentPlayer];                 // current Player
            Player opponent = players[otherPlayer];                 // the other Player
            round++;

            Move move = player.getMove(board,null);
            while (player != players[currentPlayer]) {   // in case that Player was changed while choosing move
                player = players[currentPlayer];
                move = player.getMove(board,null);
            }
            if (move == null) {                         // if no move possible current player looses
                bG.showMessage("Player " + (otherPlayer + 1) + ": " + opponent.getName() + " wins!");
                break;
            }
            while (move.captured == null && capturePossible(board, player.team)) {
                bG.showMessage("Capture is obligatory");
                move = player.getMove(board, null);
            }

            Figure moved = round(player, board, move);  // execute the move chosen by player
            bG.repaint();                               // request GUI refresh

            if (move.captured != null || moved instanceof DraughtsMan) {
                KingsNoCapture[currentPlayer] = 0;      // reset meaningless moves counter
            } else {
                KingsNoCapture[currentPlayer]++;        // count meaningless move
            }
            if (tooManyKingsMove()) {                   // if 15 meaningless moves done by each player it is a draw
                break;
            }
        }
        System.exit(0);
    }

    /**
     * Method executing a whole round with chosen move
     * Used by game to execute round and by players to simulate a round
     * Recognition between "official" round and simulation is required
     * GUI has to be informed about captured figures and refresh has to be requested
     * @param player        current player for this round
     * @param currentBoard  the current state of the board
     * @param move          the move to be executed
     * @return the figure that was moved in current round
     */
    public Figure round(Player player, Board currentBoard, Move move) {
        Figure moved = currentBoard.moveFigure(move);
        if (board == currentBoard) {        // if this is the "official" board and not a simulation
            bG.captureFigure(player, move.captured); // view captured figure in removed figures panel
            bG.repaint();                            // request GUI refresh
        }
        if (move.captured != null) {
            continueCapture(player, currentBoard, moved);
        }
        if (checkPromote(currentBoard, moved)) {
            moved = promoteMan(currentBoard, moved);
        }
        return moved;
    }

    private void continueCapture(Player player, Board currentBoard, Figure moved) {
        while (capturePossible(currentBoard, moved)) {
            Move move = player.getMove(currentBoard, moved);
            // continue capture only with the same figure
            if (move.from.equals(moved.getPos()) && move.captured != null) {
                currentBoard.moveFigure(move);
                if (board == currentBoard) {        // if this is the "official" board and not a simulation
                    bG.captureFigure(player, move.captured); // view captured figure in removed figures panel
                    bG.repaint();                                   // request GUI refresh
                }
            } else {
                bG.showMessage("Capture is obligatory");
            }
        }
    }

    private boolean checkPromote(Board currentBoard, Figure moved) {
        return moved instanceof DraughtsMan
                && ((moved.getTeam() == 1 && moved.getPos().y == currentBoard.getMaxY())
                || (moved.getTeam() == -1 && moved.getPos().y == 1));
    }

    private Figure promoteMan(Board currentBoard, Figure moved) {
        moved = new DraughtsKing(moved.getPos(), moved.getTeam());
        currentBoard.putFigure(moved);
        return moved;
    }

    private boolean tooManyKingsMove() {
        for (int i : KingsNoCapture) {
            if (i < 15) return false;
        }
        bG.showMessage("Draw");
        return true;
    }

    private boolean capturePossible(Board currentBoard, Figure figure) {
        List<Move> moves = figure.getMoves(currentBoard);
        return moves.stream().anyMatch((m) -> (m.captured != null));
    }

    private boolean capturePossible(Board currentBoard, int team) {
        List<Move> moves = currentBoard.getAllMoves(team);
        return moves.stream().anyMatch((m) -> (m.captured != null));
    }
}
