package pl.kamfonik.boardgames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class User extends Player {

    private String userName = "Michal"; // !!!!to be changed
    private ImageIcon userIcon;
    private final JPanel boardPanel;    // !!!!required for mouse capture - to be moved outside

    private Figure picked = null;       // figure picked by user
    private Position oldPos = null;     // position of the figure to be moved
    private boolean myTurn = false;     // for recognition if this is current player turn
    private List<Move> moves;           // list of moves - to check if user move is valid
    private Move nextMove;              // chosen move
    private Board currentBoard;

    public User(JPanel infoPanel,JPanel boardPanel, int team) {
        playerName = "User";
        userIcon = new ImageIcon(new ImageIcon("images/borysek.jpg")
                .getImage().getScaledInstance(100, -1, Image.SCALE_DEFAULT)); // !!!!to be changed
        this.team = team;
        this.infoPanel = infoPanel;
        this.boardPanel = boardPanel;
        this.boardPanel.addMouseListener(new MouseFigurePicker());
        this.boardPanel.addMouseMotionListener(new MouseFigureDragger());
    }

    @Override
    public String toString() {
        return this.playerName;
    }

    @Override
    public String getName() {
        return this.userName;
    }

    public void initInfoPanel() {
        infoPanel.removeAll();      // clear the panel
        JLabel userName = new JLabel(this.userName, JLabel.CENTER);
        JLabel userImage = new JLabel(userIcon, JLabel.CENTER);

        initPlayerPanel(userName, userImage);
    }

    @Override
    void playerChanged() {
        playerChanged = true;
    }

    @Override
    Move getMove(Board chosenBoard,Figure moved) {
        playerChanged = false;          // clear player changed flag
        this.myTurn = true;             // set stop ignoring mouse actions
        this.currentBoard = chosenBoard;
        nextMove = null;                // clear chosen move

        if (!movePossible()) return null;

        // wait until the move has been chosen or player was changed
        while (nextMove == null && !playerChanged) {
            try {
                Thread.sleep(10); // !!!!waiting for player to click - busy waiting - to be changed
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        if (playerChanged){         // some cleaning if the player was changed
            clearMove();
            nextMove = null;
            playerChanged = false; // clear the flag just in case (not necessary)
        }
        return nextMove;
    }

    private boolean movePossible() {
        List<Figure> figures = currentBoard.getFigures(this.team);
        for (Figure figure : figures) {
            if(!figure.getMoves(currentBoard).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private Position findBoardPos(Position screenPos) {     // !!!! check mouse position - to be moved outside

        int offsetX = (boardPanel.getBounds().width - BoardGames.FIELD_SIZE * currentBoard.getMaxX()) / 2;
        int offsetY = (boardPanel.getBounds().height - BoardGames.FIELD_SIZE * currentBoard.getMaxY()) / 2;

        if (screenPos.x > offsetX && screenPos.x < offsetX + currentBoard.getMaxX() * BoardGames.FIELD_SIZE) {
            if (screenPos.y > offsetY && screenPos.y < offsetY + currentBoard.getMaxY() * BoardGames.FIELD_SIZE) {
                int x = (screenPos.x - offsetX) / BoardGames.FIELD_SIZE + 1;
                int y = currentBoard.getMaxY() - (screenPos.y - offsetY) / BoardGames.FIELD_SIZE;

                return new Position(x, y);
            }
        }
        return null;
    }

    private class MouseFigurePicker extends MouseAdapter {  // !!!! mouse click listener - to be moved outside

        @Override
        public void mousePressed(MouseEvent e) {
            mouseAction(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (myTurn) {                   // ignore the mouse if not my turn
                Position pos = findBoardPos(new Position(e.getX(), e.getY()));
                if (picked != null && pos != null
                        && currentBoard.getFigure(pos) == null) { // if figure was picked and the field is empty
                    picked.moveTo(oldPos);  // move chosen figure back - after dragging to desired position
                    finishMove(pos);        // check if move is valid and return it
                }
                ((JPanel) e.getSource()).repaint(); // refresh GUI after chosen figure was moved back
            }
        }

        private void mouseAction(MouseEvent e) {
            if (myTurn) {       // ignore mouse if not my turn
                Position pos = findBoardPos(new Position(e.getX(), e.getY()));

                if (picked == null && pos != null
                        && currentBoard.getFigure(pos) != null
                        && currentBoard.getFigure(pos).getTeam() == User.this.team) {
                // no figure picked, valid board position selected, figure selected, figure is from current team

                    picked = currentBoard.getFigure(pos);
                    picked.pick();              // mark selected figure
                    oldPos = picked.getPos();   // save current figure pos

                    moves = picked.getMoves(currentBoard); // check valid moves for current figure

                    if (moves.isEmpty()) {  // if no move possible unpick - selecting not possible
                        picked.unpick();
                        picked = null;
                        oldPos = null;
                    }
                } else if (picked != null && pos != null
                        && currentBoard.getFigure(pos) == null) {
                // figure picked, valid board position selected, field is empty
                    finishMove(pos);        // check if move is valid and return it
                } else if (picked != null && pos != null && picked.getPos().equals(pos)) {
                // unselect figure if selected second time
                    picked.unpick();
                    picked = null;
                    oldPos = null;
                }
                ((JPanel) e.getSource()).repaint(); // refresh GUI after selecting/deselecting
            }
        }

        private void finishMove(Position pos) {
            for (Move m : moves) {      // check if chosen move belongs to valid moves
                if (m.from.equals(picked.getPos()) && m.to.equals(pos)) {
                    clearMove();        // do some cleaning in temporary values
                    nextMove = m;       // return chosen move (getMove(...) is waiting for setting this value)
                    break;
                }
            }
        }
    }

    private void clearMove(){
        if(picked != null) {
            picked.unpick();    // restore default figure image
        }
        picked = null;          // clear picked figure
        myTurn = false;         // clear myTurn flag
        oldPos = null;          // clear original position of chosen figure
    }

    private class MouseFigureDragger extends MouseMotionAdapter {   // used for visualization of possible moves

        @Override
        public void mouseDragged(MouseEvent e) {
            if (myTurn) {       // ignore mouse if not my turn
                Position pos = findBoardPos(new Position(e.getX(), e.getY()));

                if (picked != null && pos != null
                        && currentBoard.getFigure(pos) == null) { // if figure was picked and the field is empty
                    boolean isValid = false;
                    for (Move m : moves) {
                        if (m.from.equals(oldPos) && m.to.equals(pos)) {
                        // if move marked by dragging belongs to valid moves
                            isValid = true;
                            break;
                        }
                    }
                    if(isValid) {   // if move is valid mark it
                        picked.moveTo(pos);
                    } else {        // if move not valid restore previous position
                        picked.moveTo(oldPos);
                    }
                    ((JPanel) e.getSource()).repaint();
                } else if (picked != null) {    // if figure picked but no valid filed selected
                    picked.moveTo(oldPos);      // restore old position
                    ((JPanel) e.getSource()).repaint();
                }
            }
        }
    }
}
