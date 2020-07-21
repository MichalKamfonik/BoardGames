package boardgames;

import java.awt.Image;
import java.util.LinkedList;
import javax.swing.ImageIcon;

public abstract class Figure {
    abstract public LinkedList<Move> getMoves(Board currentBoard);
    abstract public int getTeam();
    abstract public Image getImage();
    abstract public Position getPos();
    abstract public void moveTo(Position newPos);
    abstract public void pick();
    abstract public void unpick();
}

class Position implements Comparable {
    public int x;
    public int y;
    
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
    public int compareTo(Object o) {
        return x==((Position)o).x ? y-((Position)o).y : x-((Position)o).x;
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
    public Position from;
    public Position to;
    public Figure captured = null;
    
    public Move(Position from, Position to, Figure captured) {
        this.from = from;
        this.to = to;
        this.captured = captured;
    }
    
    @Override
    public String toString() {
        return "From "+from+" To "+ to+" Captured "+captured;
    }
}

class DraughtsMan extends Figure {
    private String name = "Man";
    private Position currentPos;
    private int team = 1;
    private Image imageBlack=new ImageIcon("black.png").getImage();
    private Image imageWhite=new ImageIcon("white.png").getImage();
    private Image imageBlackPicked=new ImageIcon("blackP.png").getImage();
    private Image imageWhitePicked=new ImageIcon("whiteP.png").getImage();
    
    private Image currentImage;
    
    public DraughtsMan(Position pos,int team) {
        this.currentPos = pos;
        this.team = team;
        this.unpick();
    }
    
    /**
     * Method returning list of possible moves that can be made by the figure on
     * current Board
     * @param currentBoard - variable representing current Board
     * @return A list of all possible moves with captured Figures
     */
    @Override
    public LinkedList<Move> getMoves(Board currentBoard) {
        
        var moves = new LinkedList<Move>();
        Position newPos = new Position(this.currentPos);
        
        newPos.riseY(team);

        if(newPos.y <= currentBoard.getMaxY() && newPos.y > 0) {

            newPos.riseX(-1);

            if(newPos.x > 0) {
                if(currentBoard.getFigure(newPos)==null)
                    moves.add(new Move(new Position(currentPos),new Position(newPos),null));
                else if(currentBoard.getFigure(newPos).getTeam() != this.getTeam()){
                    Position newPos2 = new Position(newPos);
                    newPos2.riseX(-1);
                    newPos2.riseY(team);

                    if(newPos2.x > 0 && newPos2.y <= currentBoard.getMaxY() && newPos2.y > 0) {
                        if(currentBoard.getFigure(newPos2)==null)
                            moves.add(new Move(new Position(currentPos),new Position(newPos2),currentBoard.getFigure(newPos)));
                    } 
                }
            }
            newPos.x = currentPos.x;
            newPos.riseX(1);

            if(newPos.x <= currentBoard.getMaxX()) {
                if(currentBoard.getFigure(newPos)==null)
                    moves.add(new Move(new Position(currentPos),new Position(newPos),null));
                else if(currentBoard.getFigure(newPos).getTeam() != this.getTeam()){
                    Position newPos2 = new Position(newPos);
                    newPos2.riseX(1);
                    newPos2.riseY(team);

                    if(newPos2.x <= currentBoard.getMaxX() && newPos2.y <= currentBoard.getMaxY() && newPos2.y > 0) {
                        if(currentBoard.getFigure(newPos2)==null)
                            moves.add(new Move(new Position(currentPos),new Position(newPos2),currentBoard.getFigure(newPos)));
                    } 
                }
            }
        }
        
        newPos.y = currentPos.y;
        newPos.riseY(-2*team);

        if(newPos.y <= currentBoard.getMaxY() && newPos.y > 0) {

            newPos.x = currentPos.x;
            newPos.riseX(-2);

            if(newPos.x > 0) {
                if(currentBoard.getFigure(newPos)==null) {
                    Position newPos2 = new Position(newPos.x+1,newPos.y+team);
                    if(currentBoard.getFigure(newPos2)!=null 
                            && currentBoard.getFigure(newPos2).getTeam() != this.getTeam())
                        moves.add(new Move(new Position(currentPos),new Position(newPos),currentBoard.getFigure(newPos2)));
                }
            }
            newPos.x = currentPos.x;
            newPos.riseX(2);

            if(newPos.x <= currentBoard.getMaxX()) {
                if(currentBoard.getFigure(newPos)==null) {
                    Position newPos2 = new Position(newPos.x-1,newPos.y+team);
                    if(currentBoard.getFigure(newPos2)!=null 
                            && currentBoard.getFigure(newPos2).getTeam() != this.getTeam())
                        moves.add(new Move(new Position(currentPos),new Position(newPos),currentBoard.getFigure(newPos2)));
                }
            }
        }
        return moves;
    }
    
    //Check possible, żeby wyczyścić getMoves;
    
    @Override
    public String toString() {
        return this.name;
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
    private String name = "King";
    private Position currentPos;
    private int team = 1;
    private Image imageBlack=new ImageIcon("blackKing.png").getImage();
    private Image imageWhite=new ImageIcon("whiteKing.png").getImage();
    private Image imageBlackPicked=new ImageIcon("blackKingP.png").getImage();
    private Image imageWhitePicked=new ImageIcon("whiteKingP.png").getImage();
    
    private Image currentImage;
    
    public DraughtsKing(Position pos,int team) {
        this.currentPos = pos;
        this.team = team;
        this.unpick();
    }
    
    @Override
    public LinkedList<Move> getMoves(Board currentBoard)
    {
        var moves = new LinkedList<Move>();
        Position newPos = new Position(this.currentPos);
        newPos.riseX(1);
        newPos.riseY(1);
        Figure captured = null;
        
        while(newPos.x <= currentBoard.getMaxX() && newPos.y <= currentBoard.getMaxY()) {
            if(currentBoard.getFigure(newPos) == null)
                moves.add(new Move(new Position(currentPos),new Position(newPos),captured));
            else if(currentBoard.getFigure(newPos).getTeam() != this.team 
                    && captured == null)
                captured = currentBoard.getFigure(newPos);
            else
                break;
            newPos.riseX(1);
            newPos.riseY(1);
        }
        
        captured = null;
        newPos = new Position(this.currentPos);
        newPos.riseX(-1);
        newPos.riseY(1);
        
        while(newPos.x > 0 && newPos.y <= currentBoard.getMaxY()) {
            if(currentBoard.getFigure(newPos) == null)
                moves.add(new Move(new Position(currentPos),new Position(newPos),captured));
            else if(currentBoard.getFigure(newPos).getTeam() != this.team 
                    && captured == null)
                captured = currentBoard.getFigure(newPos);
            else
                break;
            newPos.riseX(-1);
            newPos.riseY(1);
        }
        
        captured = null;
        newPos = new Position(this.currentPos);
        newPos.riseX(1);
        newPos.riseY(-1);
        
        while(newPos.x <= currentBoard.getMaxX() && newPos.y > 0) {
            if(currentBoard.getFigure(newPos) == null)
                moves.add(new Move(new Position(currentPos),new Position(newPos),captured));
            else if(currentBoard.getFigure(newPos).getTeam() != this.team 
                    && captured == null)
                captured = currentBoard.getFigure(newPos);
            else
                break;
            newPos.riseX(1);
            newPos.riseY(-1);
        }
        
        captured = null;
        newPos = new Position(this.currentPos);
        newPos.riseX(-1);
        newPos.riseY(-1);
        
        while(newPos.x > 0 && newPos.y > 0) {
            if(currentBoard.getFigure(newPos) == null)
                moves.add(new Move(new Position(currentPos),new Position(newPos),captured));
            else if(currentBoard.getFigure(newPos).getTeam() != this.team 
                    && captured == null)
                captured = currentBoard.getFigure(newPos);
            else
                break;
            newPos.riseX(-1);
            newPos.riseY(-1);
        }
        
        return moves;
    }
    
    @Override
    public String toString() {
        return this.name;
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