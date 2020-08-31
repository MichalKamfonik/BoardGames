package boardgames;

import java.awt.Graphics;
import java.util.*;
import javax.swing.JPanel;

public abstract class Board {
    abstract public Figure getFigure(Position pos);
    abstract public Field getField(Position pos);
    abstract public void putFigure(Figure fig);
    abstract public JPanel getJPanel(); // to be moved outside?
    abstract public int getMaxX();
    abstract public int getMaxY();
    abstract public Figure moveFigure(Move move);
    abstract public boolean hasPos(Position pos);
    abstract public List<Figure> getFigures(int team);
    abstract public Board deepClone();
    abstract public List<Move> getAllMoves(int team);
}

class DraughtsBoard extends Board {
    
    private final int maxX;
    private final int maxY;
    
    private final Map<Position, Figure> figures = new TreeMap<>();
    private final Field[][] fields;
    
    private final JPanel panel; // to be deleted?
    
    public DraughtsBoard(int maxX, int maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
        panel = new BoardPanel();
        fields = new Field[maxY][maxX];
        initializeBoard();
    }

    private DraughtsBoard(DraughtsBoard other) {
        maxX = other.maxX;
        maxY = other.maxY;
        fields = new Field[maxY][maxX];
        panel = null;

        for (int i = 0; i<fields.length; i++){
            for (int j = 0; j < fields[i].length; j++) {
                fields[i][j] = new Field(other.fields[i][j]);
            }
        }

        for (Figure figure : other.getFigures(1)) {
            figures.put(new Position(figure.getPos()),figure.deepClone());
        }
        for (Figure figure : other.getFigures(-1)) {
            figures.put(new Position(figure.getPos()),figure.deepClone());
        }
    }
    
    @Override
    public Figure getFigure(Position pos) {
        return figures.get(pos);
    }

    @Override
    public Field getField(Position pos) {
        return fields[pos.y-1][pos.x-1];
    }

    @Override
    public void putFigure(Figure fig) {
        figures.put(fig.getPos(), fig);
    }
    
    private void initializeBoard()
    {
        initializeFigures(1,3,1);
        initializeFigures(maxY-2,maxY,-1);
        initializeFields();
    }

    private void initializeFields() {
        for(int y=0;y<maxY;y++) {
            for(int x=0; x<maxX;x++) {
                if((x+y)%2==0) {
                    fields[y][x] = new Field(Field.BLACK);
                } else {
                    fields[y][x] = new Field(Field.WHITE);
                }
            }
        }
    }

    private void initializeFigures(int rowMin, int rowMax, int team) {
        for(int y=rowMin;y<=rowMax;y++) {
            for(int x=((y+1)%2)+1; x<=maxX;x+=2) {
                Position tmp = new Position(x,y);
                figures.put(tmp, new DraughtsMan(tmp,team));
            }
        }
    }

    @Override
    public JPanel getJPanel() { // to be deleted
        return panel;
    }

    @Override
    public int getMaxX() {
        return maxX;
    }

    @Override
    public int getMaxY() {
        return maxY;
    }

    @Override
    public Figure moveFigure(Move move) {
        Figure moved = figures.remove(move.from);
        
        figures.put(move.to, moved); // Map moved-figure correctly
        moved.moveTo(move.to); // Update position in moved-figure
        if(panel!= null) {
            panel.repaint();    // to be deleted
        }
        captureFigure(move.captured);
        return moved;
    }

    private Figure captureFigure(Figure captured) {
        if(captured != null) {
            return figures.remove(captured.getPos());
        } else return null;
    }

    @Override
    public boolean hasPos(Position pos) {
        return pos.x<=this.maxX && pos.y<=this.maxY && pos.x>0 && pos.y>0;
    }

    @Override
    public List<Figure> getFigures(int team){
        List<Figure> figures = new LinkedList<>();

        this.figures.forEach((position, figure) -> {
            if(figure.getTeam() == team) {
                figures.add(figure);
            }
        });

        return figures;
    }

    public Board deepClone() {
        return new DraughtsBoard(this);
    }

    @Override
    public List<Move> getAllMoves(int team) {
        List<Move> moves = new LinkedList<>();
        boolean capturePossible = false;

        this.figures.forEach((position, figure) -> {
            if(figure.getTeam() == team) {
                moves.addAll(figure.getMoves(this));
            }
        });

        for (Move move : moves) {
            if(move.captured != null){
                capturePossible = true;
                break;
            }
        }
        if(capturePossible) {
            moves.removeIf((move)->move.captured==null);
        }

        return moves;
    }

    private class BoardPanel extends JPanel { // to be removed?
        
        int offsetX;
        int offsetY;
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            offsetX = (this.getBounds().width - 30*maxX)/2;
            offsetY = (this.getBounds().height - 30*maxY)/2;
            
            for(int y=maxY; y>0; y--) {
                for(int x=1; x<=maxX; x++) {
                    Position pos = new Position(x,y);
                    g.drawImage(getField(pos).getImage(),offsetX+(x-1)*30,offsetY+(maxY-y)*30,null);
                }
            }
            try{
                figures.forEach((pos, fig) -> g.drawImage(fig.getImage(),offsetX+(fig.getPos().x-1)*30,offsetY+(maxY-fig.getPos().y)*30,null));
            }
            catch(ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
    }
}
