package pl.kamfonik.boardgames;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

class DraughtsKing extends Figure {
    private static final ImageIcon imageBlack = new ImageIcon("src/main/resources/images/blackKing.png");
    private static final ImageIcon imageWhite = new ImageIcon("src/main/resources/images/whiteKing.png");
    private static final ImageIcon imageBlackPicked = new ImageIcon("src/main/resources/images/blackKingP.png");
    private static final ImageIcon imageWhitePicked = new ImageIcon("src/main/resources/images/whiteKingP.png");

    private static final int KING_VALUE = 5;

    public DraughtsKing(Position pos, int team) {
        this.name = "King";
        this.value = KING_VALUE;
        this.currentPos = pos;
        this.team = team;
        this.unpick();
    }

    @Override
    public Figure deepClone() {
        return new DraughtsKing(new Position(currentPos), this.team);
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public List<Move> getMoves(Board currentBoard) {
        List<Move> moves = new LinkedList<>();                      // a lot of add and remove
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};  // all movement directions [0] - column [1] - row
        boolean capturePossible = false;

        for (int i = 0; i < 4; i++) {                               // check movements in every direction
            Figure captured = null;
            Position newPos = new Position(this.currentPos);
            newPos.riseX(directions[i][0]);
            newPos.riseY(directions[i][1]);

            while (currentBoard.hasPos(newPos)) {           // while there are next fields in current direction
                if (currentBoard.getFigure(newPos) == null) {   // if field is empty
                    if (captured != null) {                 // if any figure was captured along this direction
                        if(!capturePossible) {      // if this is first possible capture
                            moves.clear();          // capture is obligatory - remove all previous moves without capture
                            capturePossible = true; // capture is obligatory - ignore all next moves without capture
                        }
                        moves.add(new Move(currentPos, newPos, captured));
                    } else if (!capturePossible) {          // capture obligatory - ignore moves without capture
                        moves.add(new Move(currentPos, newPos, null));
                    }
                // if field is taken check if by opponent and
                // if already captured any figure - only one capture possible in one move
                } else if (currentBoard.getFigure(newPos).getTeam() != this.team && captured == null) {
                    captured = currentBoard.getFigure(newPos);
                } else {    // if filed is not empty and not taken by the opponent or figure already captured
                    break;
                }
                newPos.riseX(directions[i][0]); // check next field along current direction
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
