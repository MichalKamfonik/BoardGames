package pl.kamfonik.boardgames;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

class DraughtsMan extends Figure {
    private static final ImageIcon imageBlack = new ImageIcon("src/main/resources/images/black.png");
    private static final ImageIcon imageWhite = new ImageIcon("src/main/resources/images/white.png");
    private static final ImageIcon imageBlackPicked = new ImageIcon("src/main/resources/images/blackP.png");
    private static final ImageIcon imageWhitePicked = new ImageIcon("src/main/resources/images/whiteP.png");

    private static final int MAN_VALUE = 1;

    public DraughtsMan(Position pos, int team) {
        this.name = "Man";
        this.value = MAN_VALUE;
        this.currentPos = pos;
        this.team = team;
        this.unpick();
    }

    @Override
    public Figure deepClone() {
        return new DraughtsMan(new Position(currentPos), this.team);
    }

    @Override
    public int getValue() {
        return value;
    }

    /**
     * Method returning list of possible moves that can be made by the figure on
     * current Board
     *
     * @param currentBoard - variable representing current Board
     * @return A list of all possible moves with captured Figures
     */
    @Override
    public List<Move> getMoves(Board currentBoard) {

        List<Move> moves = new LinkedList<>();                      // a lot of add and remove
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};  // all movement directions [0] - column [1] - row
        boolean capturePossible = false;

        for (int i = 0; i < 4; i++) {                       // check movements in every direction
            Position newPos = new Position(currentPos);
            newPos.riseX(directions[i][0]);
            newPos.riseY(directions[i][1]);

            if (currentBoard.hasPos(newPos)) {                  // check if position is not outside the board
                if (currentBoard.getFigure(newPos) == null) {   // if field is free
                    // without capture only forward is possible, ignore the move if capture is possible
                    if (directions[i][1] == team && !capturePossible) {
                        moves.add(new Move(currentPos, newPos, null));
                    }
                } else if (currentBoard.getFigure(newPos).getTeam() != this.team) { // if opponents figure is in front
                    Figure captured = currentBoard.getFigure(newPos);
                    newPos.riseX(directions[i][0]);
                    newPos.riseY(directions[i][1]);
                    // check if field behind opponent exists and is free
                    if (currentBoard.hasPos(newPos) && currentBoard.getFigure(newPos) == null) {
                        if(!capturePossible) {
                            moves.clear();          // capture is obligatory - remove all previous moves without capture
                            capturePossible = true; // capture is obligatory - ignore all next moves without capture
                        }
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
    public ImageIcon getImage() {
        return this.currentImage;
    }

    @Override
    public Position getPos() {
        return currentPos;
    }

    @Override
    public void moveTo(Position newPos) {
        this.currentPos = new Position(newPos);
    }

    @Override
    public void pick() {
        if (this.team == 1)
            currentImage = imageWhitePicked;
        else
            currentImage = imageBlackPicked;
    }

    @Override
    public void unpick() {
        if (this.team == 1)
            currentImage = imageWhite;
        else
            currentImage = imageBlack;
    }
}
