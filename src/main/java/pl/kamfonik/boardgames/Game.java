package pl.kamfonik.boardgames;

public abstract class Game implements Runnable {
    protected Player[] players;
    protected Board board;
    protected boolean running = false;

    public Board getBoard() {
        return board;
    }
    abstract public Figure round(Player player, Board currentBoard, Move move);
    abstract public String toString();
    public void stop(){
        running = false;
    }
}

