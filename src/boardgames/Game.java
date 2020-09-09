package boardgames;

public abstract class Game implements Runnable {
    protected Player[] players;
    protected Board board;

    public Board getBoard() {
        return board;
    }
    abstract public Figure round(Player player, Board currentBoard, Move move);
}

