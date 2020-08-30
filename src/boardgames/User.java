package boardgames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class User extends Player {

    private String userName = "Michal";
    private ImageIcon userIcon;
    private final JPanel boardPanel;

    private Figure picked = null;
    private Position oldPos = null;
    private boolean myTurn = false;
    private List<Move> moves;
    private Move nextMove;
    private Board currentBoard;

    public User(JPanel boardPanel, int team) {
        super();
        playerName = "User";
        userIcon = new ImageIcon(new ImageIcon("borysek.jpg").getImage().getScaledInstance(100, -1, Image.SCALE_DEFAULT));
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

    private void initPanel() {
        JLabel userName = new JLabel(this.userName, JLabel.CENTER);
        JLabel userImage = new JLabel(userIcon, JLabel.CENTER);

        initPlayerPanel(userName, userImage);
    }

    @Override
    Move getMove(Board chosenBoard) {
        this.myTurn = true;
        this.currentBoard = chosenBoard;
        nextMove = null;

        if (!movePossible()) return null;

        while (nextMove == null) {
            try {
                Thread.sleep(10); // waiting for player to click;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
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

    private Position findBoardPos(Position screenPos) {

        int offsetX = (boardPanel.getBounds().width - 30 * currentBoard.getMaxX()) / 2;
        int offsetY = (boardPanel.getBounds().height - 30 * currentBoard.getMaxY()) / 2;

        if (screenPos.x > offsetX && screenPos.x < offsetX + currentBoard.getMaxX() * 30) {
            if (screenPos.y > offsetY && screenPos.y < offsetY + currentBoard.getMaxY() * 30) {
                int x = (screenPos.x - offsetX) / 30 + 1;
                int y = currentBoard.getMaxY() - (screenPos.y - offsetY) / 30;

                return new Position(x, y);
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
            if (myTurn) {
                Position pos = findBoardPos(new Position(e.getX(), e.getY()));
                if (picked != null && pos != null
                        && currentBoard.getFigure(pos) == null) {
                    picked.moveTo(oldPos);
                    finishMove(pos);
                }
                ((JPanel) e.getSource()).repaint();
            }
        }

        private void mouseAction(MouseEvent e) {
            if (myTurn) {

                Position pos = findBoardPos(new Position(e.getX(), e.getY()));

                if (picked == null && pos != null
                        && currentBoard.getFigure(pos) != null
                        && currentBoard.getFigure(pos).getTeam() == User.this.team) {

                    picked = currentBoard.getFigure(pos);
                    picked.pick();
                    oldPos = picked.getPos();

                    moves = picked.getMoves(currentBoard);

                    if (moves.isEmpty()) {
                        picked.unpick();
                        picked = null;
                        oldPos = null;
                    }
                } else if (picked != null && pos != null
                        && currentBoard.getFigure(pos) == null) {
                    finishMove(pos);
                } else if (picked != null && pos != null && picked.getPos().equals(pos)) {
                    picked.unpick();
                    picked = null;
                    oldPos = null;
                }
                ((JPanel) e.getSource()).repaint();
            }
        }

        private void finishMove(Position pos) {
            for (Move m : moves) {
                if (m.from.equals(picked.getPos()) && m.to.equals(pos)) {
                    picked.unpick();
                    picked = null;
                    nextMove = m;
                    myTurn = false;
                    oldPos = null;
                    break;
                }
            }
        }
    }

    private class MouseFigureDragger extends MouseMotionAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (myTurn) {
                Position pos = findBoardPos(new Position(e.getX(), e.getY()));

                if (picked != null && pos != null
                        && currentBoard.getFigure(pos) == null) {

                    for (Move m : moves) {

                        if (m.from.equals(oldPos) && m.to.equals(pos)) {
                            picked.moveTo(pos);
                            ((JPanel) e.getSource()).repaint();
                            break;
                        }
                        picked.moveTo(oldPos);
                        ((JPanel) e.getSource()).repaint();
                    }
                } else if (picked != null) {
                    picked.moveTo(oldPos);
                    ((JPanel) e.getSource()).repaint();
                }
            }
        }
    }
}
