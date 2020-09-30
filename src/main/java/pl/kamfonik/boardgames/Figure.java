package pl.kamfonik.boardgames;

import java.util.List;
import javax.swing.ImageIcon;

public abstract class Figure {
    protected String name;
    protected int value;

    protected Position currentPos;
    protected int team;

    protected ImageIcon currentImage;

    abstract public List<Move> getMoves(Board currentBoard);
    abstract public int getTeam();
    abstract public ImageIcon getImage();
    abstract public Position getPos();
    abstract public void moveTo(Position newPos);
    abstract public void pick();
    abstract public void unpick();
    abstract public Figure deepClone();
    abstract public int getValue();
}

class Position implements Comparable<Position> {
    int x;
    int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position(Position pos) {
        this.x = pos.x;
        this.y = pos.y;
    }
    
    public void riseX(int x) {
        this.x +=x;
    }
    public void riseY(int y) {
        this.y +=y;
    }

    @Override
    public String toString() {
        return "(X,Y)= (" + x + "," + y + ")";
    }

    @Override
    public int compareTo(Position position) {
        return x==position.x ? y-position.y : x-position.x;
    }
    
    @Override
    public boolean equals(Object otherObject) {
        if(this == otherObject) return true;
        if(otherObject == null) return false;
        if(getClass() != otherObject.getClass()) return false;
        Position other = (Position) otherObject;
        return this.x == other.x && this.y == other.y;
    }
}

class Move {
    Position from;
    Position to;
    Figure captured;
    
    public Move(Position from, Position to, Figure captured) {
        this.from = new Position(from);
        this.to = new Position(to);
        this.captured = captured;
    }
    
    @Override
    public String toString() {
        return "Move(From "+from+" To "+ to+" Captured "+captured;
    }
}

