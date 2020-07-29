package boardgames;
        
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.LinkedList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class Player {
    protected JPanel userPanel = new JPanel();
    protected String playerName = "";
    protected int team;
    
    static final int PANEL_W = 140;
    static final int PANEL_H = 170;
    abstract JPanel getJPanel();
    abstract String getName();
    abstract void setName(String s);
    abstract Move getMove(Board chosenBoard);
}

class User extends Player {
    
    private String userName = "Michał";
    private ImageIcon userIcon = new ImageIcon(new ImageIcon("borysek.jpg").getImage().getScaledInstance(100, -1, Image.SCALE_DEFAULT));
    private final JPanel boardPanel;
    
    private Figure picked = null;
    private Position oldPos = null;
    private boolean myTurn = false;
    private List<Move> moves;
    private Move nextMove;
    protected Board currentBoard;
    
    public User(JPanel boardPanel, int team) {
        super();
        playerName = "User";
        this.team = team;
        this.initPanel();
        this.boardPanel = boardPanel;
        this.boardPanel.addMouseListener(new MouseFigurePicker());
        this.boardPanel.addMouseMotionListener(new MouseFigureDragger());
    }
    
    @Override
    public JPanel getJPanel() {
        return this.userPanel;
    }
    
    @Override
    public String toString() {
        return this.playerName;
    }
    
    @Override
    public String getName() {
        return this.userName;
    }
    
    @Override
    public void setName(String s) {
        this.playerName = s;
    }
    
    private void initPanel() {
        JLabel userName = new JLabel(this.userName,JLabel.CENTER);
        JLabel userImage = new JLabel(userIcon,JLabel.CENTER);
        
        GroupLayout layout = new GroupLayout(this.userPanel);
        this.userPanel.setLayout(layout);
        
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(userName,Player.PANEL_W,Player.PANEL_W,Player.PANEL_W)
                .addComponent(userImage,Player.PANEL_W,Player.PANEL_W,Player.PANEL_W)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(userName,20,20,20)
                .addComponent(userImage,Player.PANEL_H-20,Player.PANEL_H-20,Player.PANEL_H-20)
        );
    }
    
    @Override
    Move getMove(Board chosenBoard) {
        this.myTurn = true;
        this.currentBoard = chosenBoard;
        nextMove = null;
        
        if(!movePossible())
            return null;
        
        while(nextMove == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        return nextMove;
    }
    
    private boolean movePossible() {
        Position pos = new Position(1,1);
        
        for(int y=1; y<=currentBoard.getMaxY(); y++) {
            for(int x=1; x<=currentBoard.getMaxX(); x++) {
                if(currentBoard.getFigure(pos)!= null
                        && currentBoard.getFigure(pos).getTeam()== this.team
                        && !currentBoard.getFigure(pos).getMoves(currentBoard).isEmpty()) {
                    return true;
                }
                pos.riseX(1);
            }
            pos.x = 1;
            pos.riseY(1);
        }
        return false;
    }
    
    private Position findBoardPos(Position screenPos) {
        
        int offsetX = (boardPanel.getBounds().width - 30*currentBoard.getMaxX())/2;
        int offsetY = (boardPanel.getBounds().height - 30*currentBoard.getMaxY())/2;

        if(screenPos.x>offsetX && screenPos.x<offsetX+currentBoard.getMaxX()*30) {
            if(screenPos.y>offsetY && screenPos.y<offsetY+currentBoard.getMaxY()*30) {
                int x = (screenPos.x - offsetX)/30+1;
                int y = currentBoard.getMaxY()-(screenPos.y - offsetY)/30;

                return new Position(x,y);
            }
        }
        return null;
    }
    
    private class MouseFigurePicker extends MouseAdapter {
        
        @Override
        public void mousePressed(MouseEvent e) {
            mouseAction(e);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if(myTurn) {
                Position pos = findBoardPos(new Position(e.getX(), e.getY()));
                if(picked != null  && pos != null
                        && currentBoard.getFigure(pos) == null) {
                    picked.moveTo(oldPos);
                    for(Move m: moves) {
                        if(m.from.equals(picked.getPos()) && m.to.equals(pos)) {
                            picked.unpick();
                            picked = null;
                            nextMove = m;
                            myTurn = false;
                            oldPos=null;
                            break;
                        }
                    }
                }
                ((JPanel)e.getSource()).repaint();
            }
        }
            
        private void mouseAction(MouseEvent e) {
            if(myTurn) {
                
                Position pos = findBoardPos(new Position(e.getX(), e.getY()));

                if(picked == null && pos != null 
                        && currentBoard.getFigure(pos) != null 
                        && currentBoard.getFigure(pos).getTeam() == User.this.team) {

                    picked = currentBoard.getFigure(pos);
                    picked.pick();
                    oldPos=picked.getPos();

                    moves = picked.getMoves(currentBoard);

                    if(moves.isEmpty()) {
                        picked.unpick();
                        picked = null;
                        oldPos=null;
                    }
                }
                else if(picked != null  && pos != null
                        && currentBoard.getFigure(pos) == null) {
                    for(Move m: moves) {
                        
                        if(m.from.equals(picked.getPos()) && m.to.equals(pos)) {
                            picked.unpick();
                            picked = null;
                            nextMove = m;
                            myTurn = false;
                            oldPos=null;
                            break;
                        }
                    }
                }
                else if(picked != null && pos!= null && picked.getPos().equals(pos)) {
                    picked.unpick();
                    picked = null;
                    oldPos=null;
                }
                ((JPanel)e.getSource()).repaint();
            }
        }
    }
    
    private class MouseFigureDragger extends MouseMotionAdapter {
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if(myTurn) {
                
                Position pos = findBoardPos(new Position(e.getX(), e.getY()));
                
                if(picked != null  && pos != null
                        && currentBoard.getFigure(pos) == null) {
                    
                    for(Move m: moves) {
                        
                        if(m.from.equals(oldPos) && m.to.equals(pos)) {
                            picked.moveTo(pos);
                            ((JPanel)e.getSource()).repaint();
                            break;
                        }
                        picked.moveTo(oldPos);
                        ((JPanel)e.getSource()).repaint();
                    }
                }
                else if(picked != null){
                    picked.moveTo(oldPos);
                    ((JPanel)e.getSource()).repaint();
                }
            }
        }
    }
}