package boardgames;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public abstract class Board {
    abstract public Figure getFigure(Position pos);
//    abstract public Map<Position, Figure> getMap();
    abstract public void putFigure(Figure fig);
    abstract public JPanel getJPanel();
    abstract public int getMaxX();
    abstract public int getMaxY();
    abstract public Figure moveFigure(Move move);
    abstract public Figure captureFigure(Figure captured);
    abstract public boolean hasPos(Position pos);
}

class DraughtsBoard extends Board {
    
    private final int maxX;
    private final int maxY;
    
    private final Map<Position, Figure> board = new TreeMap<>();
    
    private final Image imageWhite = new ImageIcon("whiteSpot.png").getImage();
    private final Image imageBlack = new ImageIcon("blackSpot.png").getImage();
    
    private final JPanel panel = new BoardPanel();
    
    public DraughtsBoard(int maxX, int maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
        initializeBoard();
    }
    
    @Override
    public Figure getFigure(Position pos) {
        return board.get(pos);
    }
    
    @Override
    public void putFigure(Figure fig) {
        board.put(fig.getPos(), fig);
    }
    
    private void initializeBoard()
    {
        initializeTeam(1,3,1);
        initializeTeam(maxY-2,maxY,-1);
    }

    private void initializeTeam(int rowMin, int rowMax, int team) {
        for(int y=rowMin;y<=rowMax;y++) {
            for(int x=((y+1)%2)+1; x<=maxX;x+=2) {
                Position tmp = new Position(x,y);
                board.put(tmp, new DraughtsMan(tmp,team));
            }
        }
    }
    
//    @Override
//    public Map<Position, Figure> getMap() {
//        return board;
//    }

    @Override
    public JPanel getJPanel() {
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
        Figure moved = board.remove(move.from);
        
        board.put(move.to, moved);
        board.get(move.to).moveTo(move.to);
        panel.repaint();
        return captureFigure(move.captured);
    }

    @Override
    public Figure captureFigure(Figure captured) {
        if(captured != null)
            return board.remove(captured.getPos());
        else return null;
    }

    @Override
    public boolean hasPos(Position pos) {
        return pos.x<=this.maxX && pos.y<=this.maxY && pos.x>0 && pos.y>0;
    }

    private class BoardPanel extends JPanel {
        
        int offsetX;
        int offsetY;
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            offsetX = (this.getBounds().width - 30*maxX)/2;
            offsetY = (this.getBounds().height - 30*maxY)/2;
            
            for(int y=maxY; y>0; y--) {
                for(int x=1; x<=maxX; x++) {
                    if((x+y)%2==1)
                        g.drawImage(imageWhite,offsetX+(x-1)*30,offsetY+(maxY-y)*30,null);
                    else
                        g.drawImage(imageBlack,offsetX+(x-1)*30,offsetY+(maxY-y)*30,null);
                }
            }
            try{
                board.forEach((pos, fig) -> g.drawImage(fig.getImage(),offsetX+(fig.getPos().x-1)*30,offsetY+(maxY-fig.getPos().y)*30,null));
            }
            catch(ConcurrentModificationException e) {
                System.out.println("Zignorowano wyjątek");
            }
        }
    }
}
