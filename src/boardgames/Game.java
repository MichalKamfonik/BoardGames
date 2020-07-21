package boardgames;

import javax.swing.JOptionPane;

public abstract class Game implements Runnable {
    protected Player[] players;
    protected Board board;
}

class Draughts extends Game {
    
    boolean isOver = false;

    public Draughts(Player[] players, Board board) {
        this.board = board;
        this.players = players;
    }
    
    @Override
    public void run() {
        int round = 0;
        Move move;
        int[] KingsNoCapture = new int[players.length];
        
        while(!isOver) {
            int team = round%players.length;
            round ++;
            
            move = players[team].getMove(this.board);
            if(move == null) {
                isOver = true;
                JOptionPane.showMessageDialog(null,"Zwycięża " + players[round%players.length].getName());
            }
            else {
                while(capturePossible(players[team].team) && move.captured==null) {
                    JOptionPane.showMessageDialog(null,"Bicie jest obowiązkowe");
                    move = players[team].getMove(this.board);
                }
                board.moveFigure(move);
                Figure moved = board.getFigure(move.to);
                
                if(move.captured!=null) {
                    KingsNoCapture[team]=0;
                    
                    while(capturePossible(moved)) {
                        move = players[team].getMove(this.board);
                        if(move.from.equals(moved.getPos()) && move.captured != null)
                            board.moveFigure(move);
                        else
                            JOptionPane.showMessageDialog(null,"Należy kontynuować bicie");
                    }
                }
                else if(board.getFigure(move.to) instanceof DraughtsKing) {
                    KingsNoCapture[team]++;
                    isOver = true;
                    for(int i: KingsNoCapture) {
                        if(i < 15) {
                            isOver = false;
                            break;
                        }
                    }
                    if(isOver)
                        JOptionPane.showMessageDialog(null,"Remis");
                }
                else
                    KingsNoCapture[team]=0;
                
                if(moved instanceof DraughtsMan
                        && ((moved.getTeam() == 1 && move.to.y == board.getMaxY())
                        || (moved.getTeam() == -1 && move.to.y == 1))) {
                    moved=new DraughtsKing(moved.getPos(), moved.getTeam());
                    board.putFigure(moved);
                    board.moveFigure(new Move(moved.getPos(),moved.getPos(),null));
                }
            }
        }
        System.exit(0);
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