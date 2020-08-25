package boardgames;

import java.awt.Image;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;

public abstract class Figure {
    abstract public List<Move> getMoves(Board currentBoard);
    abstract public int getTeam();
    abstract public Image getImage();
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

class DraughtsMan extends Figure {
    private static final String name = "Man";
    private static final Image imageBlack=new ImageIcon("black.png").getImage();
    private static final Image imageWhite=new ImageIcon("white.png").getImage();
    private static final Image imageBlackPicked=new ImageIcon("blackP.png").getImage();
    private static final Image imageWhitePicked=new ImageIcon("whiteP.png").getImage();
    private static final int VALUE = 1;

    private Position currentPos;
    private final int team;
    
    private Image currentImage;
    
    public DraughtsMan(Position pos,int team) {
        this.currentPos = pos;
        this.team = team;
        this.unpick();
    }

    @Override
    public Figure deepClone(){
        return new DraughtsMan(new Position(currentPos),this.team);
    }

    @Override
    public int getValue(){
        return VALUE;
    }
    
    /**
     * Method returning list of possible moves that can be made by the figure on
     * current Board
     * @param currentBoard - variable representing current Board
     * @return A list of all possible moves with captured Figures
     */
    @Override
    public List<Move> getMoves(Board currentBoard) {
        
        List<Move> moves = new LinkedList<>();
        int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1}}; //all movement directions

        for(int i=0; i<4; i++) {
            Position newPos = new Position(currentPos);
            newPos.riseX(directions[i][0]);
            newPos.riseY(directions[i][1]);

            if(currentBoard.hasPos(newPos)) {
                if(currentBoard.getFigure(newPos) == null) {
                    if (directions[i][1] == team) {
                        moves.add(new Move(currentPos, newPos, null));
                    }
                } else if (currentBoard.getFigure(newPos).getTeam() != this.team) {
                    Figure captured = currentBoard.getFigure(newPos);
                    newPos.riseX(directions[i][0]);
                    newPos.riseY(directions[i][1]);
                    if (currentBoard.hasPos(newPos) && currentBoard.getFigure(newPos) == null) {
                        moves.add(new Move(currentPos, newPos, captured));
                    }
                }
            }
        }

        return moves;
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getTeam() {
        return this.team;
    }

    @Override
    public Image getImage() {
        return this.currentImage;
    }

    @Override
    public Position getPos() {
        return currentPos;
    }

    @Override
    public void moveTo(Position newPos) {
        this.currentPos = newPos;
    }

    @Override
    public void pick() {
        if(this.team == 1)
            currentImage = imageWhitePicked;
        else
            currentImage = imageBlackPicked;
    }

    @Override
    public void unpick() {
        if(this.team == 1)
            currentImage = imageWhite;
        else
            currentImage = imageBlack;
    }
}

class DraughtsKing extends Figure{
    private static final String name = "King";
    private static final Image imageBlack=new ImageIcon("blackKing.png").getImage();
    private static final Image imageWhite=new ImageIcon("whiteKing.png").getImage();
    private static final Image imageBlackPicked=new ImageIcon("blackKingP.png").getImage();
    private static final Image imageWhitePicked=new ImageIcon("whiteKingP.png").getImage();
    private static final int VALUE = 5;

    private Position currentPos;
    private final int team;
    
    private Image currentImage;
    
    public DraughtsKing(Position pos,int team) {
        this.currentPos = pos;
        this.team = team;
        this.unpick();
    }

    @Override
    public Figure deepClone(){
        return new DraughtsKing(new Position(currentPos),this.team);
    }

    @Override
    public int getValue(){
        return VALUE;
    }
    
    @Override
    public List<Move> getMoves(Board currentBoard)
    {
        List<Move> moves = new LinkedList<>();
        int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1}}; //all move directions

        for(int i=0; i<4; i++) {
            Figure captured = null;
            Position newPos = new Position(this.currentPos);
            newPos.riseX(directions[i][0]);
            newPos.riseY(directions[i][1]);

            while(currentBoard.hasPos(newPos)) {
                if(currentBoard.getFigure(newPos) == null) {
                    moves.add(new Move(currentPos, newPos, captured));
                }
                else if(currentBoard.getFigure(newPos).getTeam() != this.team && captured == null) {
                    captured = currentBoard.getFigure(newPos);
                } else {
                    break;
                }
                newPos.riseX(directions[i][0]);
                newPos.riseY(directions[i][1]);
            }
        }
        return moves;
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getTeam() {
        return this.team;
    }

    @Override
    public Image getImage() {
        return this.currentImage;
    }

    @Override
    public Position getPos() {
        return currentPos;
    }

    @Override
    public void moveTo(Position newPos) {
        this.currentPos = newPos;
    }

    @Override
    public void pick() {
        if(this.team == 1)
            currentImage = imageWhitePicked;
        else
            currentImage = imageBlackPicked;
    }

    @Override
    public void unpick() {
        if(this.team == 1)
            currentImage = imageWhite;
        else
            currentImage = imageBlack;
    }
}