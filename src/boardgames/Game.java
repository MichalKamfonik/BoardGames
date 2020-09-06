package boardgames;

import java.util.List;

public abstract class Game implements Runnable {
    protected Player[] players;
    protected Board board;

    public Board getBoard() {
        return board;
    }
    abstract public Figure round(Player player, Board currentBoard, Move move);
}

class Draughts extends Game {
    private final int[] KingsNoCapture;
    private final BoardGames bG;

    public Draughts(BoardGames bG,Player[] players) {
        this.bG = bG;
        this.board = new DraughtsBoard(8,8);
        this.players = players;
        KingsNoCapture = new int[players.length];
    }
    @Override
    public void run() {
        int round = 0;
        
        while(true) {
            int myTeam = round%players.length;
            int otherTeam = (round+1)%players.length;
            Player player = players[myTeam];
            Player opponent = players[otherTeam];
            round ++;

            Move move = player.getMove(board,null);
            while(player != players[myTeam]){   // in case that player was changed while choosing move
                player = players[myTeam];
                move = player.getMove(board,null);
            }
            if(move == null) {
                bG.showMessage("Player "+(otherTeam+1)+": "+opponent.getName()+" wins!");
                break;
            }
            while(capturePossible(board,player.team) && move.captured==null) {
                bG.showMessage("Capture is obligatory");
                move = player.getMove(board,null);
            }

            Figure moved = round(player,board,move);
            bG.repaint();

            if(move.captured!=null || moved instanceof DraughtsMan) {
                KingsNoCapture[myTeam] = 0;
            } else {
                KingsNoCapture[myTeam]++;
            }
            if(tooManyKingsMove()) {
                break;
            }

        }
        System.exit(0);
    }

    public Figure round(Player player,Board currentBoard,Move move){
        Figure moved = currentBoard.moveFigure(move);
        if(board == currentBoard) {
            int currentPlayer;
            if(player.team == 1) currentPlayer = 0;
            else currentPlayer = 1;
            bG.captureFigure(currentPlayer,move.captured);
            bG.repaint();
        }
        if(move.captured!=null) {
            continueCapture(player,currentBoard,moved);
        }
        if(checkPromote(currentBoard,moved)) {
            moved = promoteMan(currentBoard,moved);
        }
        return moved;
    }

    private void continueCapture(Player player,Board currentBoard,Figure moved){
        while(capturePossible(currentBoard,moved)) {
            Move move = player.getMove(currentBoard,moved);
            if(move.from.equals(moved.getPos()) && move.captured != null) {
                currentBoard.moveFigure(move);
                if(board == currentBoard) {
                    int currentPlayer;
                    if(player.team == 1) currentPlayer = 0;
                    else currentPlayer = 1;
                    bG.captureFigure(currentPlayer,move.captured);
                    bG.repaint();
                }
            }
            else {
                bG.showMessage("Capture is obligatory");
            }
        }
    }

    private Figure promoteMan(Board currentBoard, Figure moved){
        moved=new DraughtsKing(moved.getPos(), moved.getTeam());
        currentBoard.putFigure(moved);
        currentBoard.moveFigure(new Move(moved.getPos(),moved.getPos(),null));
        return moved;
    }

    private boolean checkPromote(Board currentBoard,Figure moved) {
        return moved instanceof DraughtsMan
                && ((moved.getTeam() == 1 && moved.getPos().y == currentBoard.getMaxY())
                || (moved.getTeam() == -1 && moved.getPos().y == 1));
    }

    private boolean tooManyKingsMove(){
        for(int i: KingsNoCapture) {
            if(i < 15) return false;
        }
        bG.showMessage("Draw");
        return true;
    }
    
    private boolean capturePossible(Board currentBoard,Figure figure) {
        List<Move> moves = figure.getMoves(currentBoard);
        return moves.stream().anyMatch((m) -> (m.captured != null));
    }
    
    private boolean capturePossible(Board currentBoard,int team) {
        Position pos = new Position(1,1);
        
        for(int y=1; y<=currentBoard.getMaxY(); y++) {
            for(int x=1; x<=currentBoard.getMaxX(); x++) {
                if(currentBoard.getFigure(pos)!= null
                        && currentBoard.getFigure(pos).getTeam()== team
                        && capturePossible(currentBoard,currentBoard.getFigure(pos))) {
                    return true;
                }
                pos.riseX(1);
            }
            pos.x = 1;
            pos.riseY(1);
        }
        return false;
    }
}