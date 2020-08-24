package boardgames;

import javax.swing.JOptionPane;

public abstract class Game implements Runnable {
    protected Player[] players;
    protected Board board;
}

class Draughts extends Game {
    int[] KingsNoCapture;

    public Draughts(Player[] players, Board board) {
        this.board = board;
        this.players = players;
        KingsNoCapture = new int[players.length];
    }
    
    @Override
    public void run() {
        int round = 0;
        
        while(true) {
            int team = round%players.length;
            round ++;
            Move move = players[team].getMove(this.board);

            if(move == null) {
                JOptionPane.showMessageDialog(null,"The winner is: " + players[round%players.length].getName());
                break;
            }
            while(capturePossible(players[team].team) && move.captured==null) {
                JOptionPane.showMessageDialog(null,"Capture is obligatory");
                move = players[team].getMove(this.board);
            }
            board.moveFigure(move);
            Figure moved = board.getFigure(move.to);

            if(move.captured!=null) {
                KingsNoCapture[team]=0;
                continueCapture(moved,team);
            } else if(board.getFigure(move.to) instanceof DraughtsKing) {
                KingsNoCapture[team]++;
                if(tooManyKingsMove()) break;
            } else {
                KingsNoCapture[team] = 0;
            }

            if(checkPromote(moved)) {
                promoteMan(moved);
            }
        }
        System.exit(0);
    }

    private void promoteMan(Figure moved){
        moved=new DraughtsKing(moved.getPos(), moved.getTeam());
        board.putFigure(moved);
        board.moveFigure(new Move(moved.getPos(),moved.getPos(),null));
    }

    private boolean checkPromote(Figure moved) {
        return moved instanceof DraughtsMan
                && ((moved.getTeam() == 1 && moved.getPos().y == board.getMaxY())
                || (moved.getTeam() == -1 && moved.getPos().y == 1));
    }

    private boolean tooManyKingsMove(){
        for(int i: KingsNoCapture) {
            if(i < 15) return false;
        }
        JOptionPane.showMessageDialog(null,"Draw");
        return true;
    }

    private void continueCapture(Figure moved, int team){
        while(capturePossible(moved)) {
            Move move = players[team].getMove(this.board);
            if(move.from.equals(moved.getPos()) && move.captured != null)
                board.moveFigure(move);
            else
                JOptionPane.showMessageDialog(null,"Capture is obligatory");
        }
    }
    
    private boolean capturePossible(Figure figure) {
        var moves = figure.getMoves(board);
        return moves.stream().anyMatch((m) -> (m.captured != null));
    }
    
    private boolean capturePossible(int team) {
        Position pos = new Position(1,1);
        
        for(int y=1; y<=board.getMaxY(); y++) {
            for(int x=1; x<=board.getMaxX(); x++) {
                if(board.getFigure(pos)!= null
                        && board.getFigure(pos).getTeam()== team
                        && capturePossible(board.getFigure(pos))) {
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