package pl.kamfonik.boardgames;

import javax.swing.*;
import java.util.*;

public class MinMaxAB extends Player {
    private static final int MAX_DIFFICULTY = 10;
    private static final int MIN_DIFFICULTY = 1;
    private static final String MINMAX = "MinMax";

    private int difficulty = 5;
    private final Game game;

    public MinMaxAB(JPanel infoPanel,Game game,int team) {
        this.infoPanel = infoPanel;
        this.game = game;
        playerName = MINMAX;
        this.team = team;
    }

    private MinMaxAB(Game game,int team,int difficulty){
        this(null,game,team);
        this.difficulty = difficulty;
    }

    public void initInfoPanel() {
        infoPanel.removeAll();  // remove all previous components from panel
        JLabel difficultyLabel = new JLabel("Difficulty", JLabel.CENTER);
        JSlider difficultySlider = new JSlider(MIN_DIFFICULTY,MAX_DIFFICULTY,difficulty);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintLabels(true);
        difficultySlider.setSnapToTicks(true);

        difficultySlider.addChangeListener(e -> difficulty = ((JSlider) e.getSource()).getValue());

        initPlayerPanel(difficultyLabel, difficultySlider);
    }

    @Override
    void playerChanged() {
        playerChanged = true;
    }

    @Override
    public String toString() {
        return this.playerName;
    }

    @Override
    public String getName() {
        return this.playerName;
    }

    @Override
    Move getMove(Board chosenBoard,Figure moved) {
        playerChanged = false;                                  // clear player changed flag
        List<Move> moves = getAllMoves(team,chosenBoard,moved);
        if(moves.isEmpty()) return null;                        // if no moves possible return null

        TreeMap<Integer,Move> nodes = new TreeMap<>();          // a tree collection of algorithm nodes
                                                                // !!!!!change to PriorityQueue for optimization
        for (Move move : moves) {
            Board node = chosenBoard.deepClone();               // simulate all moves on a copy of board
            game.round(this,node,move);                     // execute a round for each move

            // evaluate each move and map them accordingly
            nodes.put(alphaBeta(node,difficulty-1,Integer.MIN_VALUE,Integer.MAX_VALUE,-team),move);
            if(playerChanged) {             // if the player was changed during move evaluation
                if(moved == null) {         // after the whole round (not during capture loop)
                    playerChanged = false;  // clear the flag just in case (not necessary)
                }
                return null;
            }
        }
        return nodes.lastEntry().getValue();    // return the max value move
    }

    private int alphaBeta(Board currentBoard, int difficulty, int alpha, int beta, int team){
        List<Move> moves = getAllMoves(team,currentBoard,null);
        if(difficulty == 0 || moves.isEmpty() || playerChanged) {   // if reached the deepest allowed level or end
            return valueOfBoard(currentBoard);
        }
        int value;
        if(team == this.team) {             // if maximizing player
            value = Integer.MIN_VALUE;      // set value to -INF
            for (Move move : moves) {
                // execute round for every move on a copy of board
                Board node = currentBoard.deepClone();
                game.round(new MinMaxAB(game,team,difficulty),node,move);

                // recursively evaluate each move and choose the best one
                value = Math.max(value,alphaBeta(node,difficulty-1,alpha,beta,-team));
                alpha = Math.max(alpha,value);  // the minimum value that is assured for maximizing player
                if(alpha >= beta) {     // no need for evaluating moves that the second player will not make
                    break;
                }
            }
        }
        else{                               // if minimizing player
            value = Integer.MAX_VALUE;      // set value to +INF
            for (Move move : moves) {
                // execute round for every move on a copy of board
                Board node = currentBoard.deepClone();
                game.round(new MinMaxAB(game,team,difficulty),node,move);

                value = Math.min(value,alphaBeta(node,difficulty-1,alpha,beta,-team));
                beta = Math.min(beta,value);    // the maximum value that is assured for minimizing player
                if(beta <= alpha){              // no need for evaluating moves that the second player will not make
                    break;
                }
            }
        }
        return value;
    }

    private List<Move> getAllMoves(int team,Board chosenBoard,Figure moved) {
        if(moved != null){                      // if specific figure has to be moved
            return moved.getMoves(chosenBoard);
        } else {                                // if any figure can be moved
            return chosenBoard.getAllMoves(team);
        }
    }

    private int valueOfBoard(Board board) { // to be moved to board or game
        int sum = 0;
        List<Figure> myTeam = board.getFigures(team);
        List<Figure> otherTeam = board.getFigures(-team);

        if(myTeam.isEmpty()) return Integer.MIN_VALUE;
        if(otherTeam.isEmpty()) return Integer.MAX_VALUE;
        if(board.getAllMoves(team).isEmpty()) return Integer.MIN_VALUE;
        if(board.getAllMoves(-team).isEmpty()) return Integer.MAX_VALUE;

        for (Figure figure : myTeam) {
            sum+=figure.getValue();
        }
        for (Figure figure : otherTeam) {
            sum-=figure.getValue();
        }
        return sum;
    }
}
