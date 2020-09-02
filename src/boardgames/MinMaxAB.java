package boardgames;

import javax.swing.*;
import java.util.*;

public class MinMaxAB extends Player {
    private static final int MAX_DIFFICULTY = 10;
    private static final int MIN_DIFFICULTY = 1;

    private int difficulty = 5;
    private final Game game;

    public MinMaxAB(JPanel infoPanel,Game game,int team) {
        super();
        this.infoPanel = infoPanel;
        this.game = game;
        playerName = "MinMax Alpha-Beta";
        this.team = team;
    }

    private MinMaxAB(Game game,int team,int difficulty){
        super();
        this.game = game;
        playerName = "MinMax Alpha-Beta";
        this.team = team;
        this.difficulty = difficulty;
    }

    public void initInfoPanel() {
        infoPanel.removeAll();
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
        playerChanged = false;
        List<Move> moves = getAllMoves(team,chosenBoard,moved);
        if(moves.isEmpty()) return null;

        TreeMap<Integer,Move> nodes = new TreeMap<>();

        for (Move move : moves) {
            Board node = chosenBoard.deepClone();
            game.round(this,node,move);

            nodes.put(alphaBeta(node,difficulty-1,Integer.MIN_VALUE,Integer.MAX_VALUE,-team),move);
            if(playerChanged) {
                if(moved == null) {
                    playerChanged = false;
                }
                return null;
            }
        }

        return nodes.lastEntry().getValue();
    }

    private int alphaBeta(Board currentBoard, int difficulty, int alpha, int beta, int team){
        List<Move> moves = getAllMoves(team,currentBoard,null);
        if(difficulty == 0 || moves.isEmpty() || playerChanged) {
            return valueOfBoard(currentBoard);
        }
        int value;
        if(team == this.team) {
            value = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board node = currentBoard.deepClone();
                game.round(new MinMaxAB(game,team,difficulty),node,move);

                value = max(value,alphaBeta(node,difficulty-1,alpha,beta,-team));
                alpha = max(alpha,value);
                if(alpha >= beta) {
                    break;
                }
            }
        }
        else{
            value = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board node = currentBoard.deepClone();
                game.round(new MinMaxAB(game,team,difficulty),node,move);

                value = min(value,alphaBeta(node,difficulty-1,alpha,beta,-team));
                beta = min(beta,value);
                if(beta <= alpha){
                    break;
                }
            }
        }
        return value;
    }
    int max(int a, int b){
        return a>b ? a : b;
    }
    int min(int a, int b){
        return a<b ? a : b;
    }

    private List<Move> getAllMoves(int team,Board chosenBoard,Figure moved) {
        if(moved != null){
            return moved.getMoves(chosenBoard);
        } else {
            return chosenBoard.getAllMoves(team);
        }
    }

    private int valueOfBoard(Board board) { // zmienic zeby to Board podawal swoja wartosc
        int sum = 0;
        List<Figure> myTeam = board.getFigures(team);
        List<Figure> otherTeam = board.getFigures(-team);

        if(myTeam.isEmpty()) return -100;
        if(otherTeam.isEmpty()) return 100;

        for (Figure figure : myTeam) {
            sum+=figure.getValue();
        }
        for (Figure figure : otherTeam) {
            sum-=figure.getValue();
        }
        return sum;
    }
}
