package boardgames;

import javax.swing.*;
import java.util.*;

public class MinMaxAB extends Player {
    private static final int MAX_DIFFICULTY = 10;
    private static final int MIN_DIFFICULTY = 1;

    private int difficulty = 5;

    public MinMaxAB(int team) {
        super();
        playerName = "MinMax Alpha-Beta";
        this.team = team;
        this.initPanel();
    }

    public void setDifficulty(int difficulty) {
        if(difficulty<=MAX_DIFFICULTY && difficulty>=MIN_DIFFICULTY) {
            this.difficulty = difficulty;
        }
    }

    private void initPanel() {
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
    public JPanel getJPanel() {
        return this.userPanel;
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
    Move getMove(Board chosenBoard) {
        List<Move> moves = getAllMoves(chosenBoard);
        if(moves.isEmpty()) return null;

        TreeMap<Integer,Move> nodes = new TreeMap<>();
        boolean capturePossible = false;

        for (Move move : moves) {
            Board node = chosenBoard.deepClone();
            node.moveFigure(move);
            if(move.captured!=null) {
                capturePossible = true;
                Figure moved = node.getFigure(move.to);
                nodes.put(alphaBeta(node,difficulty,Integer.MIN_VALUE,Integer.MAX_VALUE,team,moved),move);
            } else if(!capturePossible){
                nodes.put(alphaBeta(node,difficulty-1,Integer.MIN_VALUE,Integer.MAX_VALUE,-team,null),move);
            }
        }
        if(capturePossible){
            nodes.values().removeIf((move)->move.captured==null);
        }
        return nodes.lastEntry().getValue();
    }

    private List<Move> getAllMoves(Board chosenBoard) {
        List<Move> moves = new LinkedList<>();

        for (Figure figure : chosenBoard.getFigures(team)) {
            moves.addAll(figure.getMoves(chosenBoard));
        }
        return moves;
    }

    private int valueOfBoard(Board board) {
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

    private Map<Move,Board> possibleMoves(Board board,int team,Figure moved){
        Map<Move,Board> nodes = new HashMap<>();

        if(board.getFigures(-team).isEmpty()) return nodes; // If I won I do not have more moves
        boolean capturePossible = false;    // I have to move it somewhere outside of the algorithm

        List<Figure> figures;

        if(moved != null){
            figures = new LinkedList<>();
            figures.add(moved);
        } else {
            figures = board.getFigures(team);
        }

        for (Figure figure : figures) {
            for (Move move : figure.getMoves(board)) {
                if(move.captured != null) {
                    capturePossible = true;
                    Board node = board.deepClone();
                    node.moveFigure(move);
                    nodes.put(move,node);
                } else if(!capturePossible) {
                    Board node = board.deepClone();
                    node.moveFigure(move);
                    nodes.put(move,node);
                }
            }
        }
        if(capturePossible){
            nodes.keySet().removeIf((move) -> move.captured==null);
        }
        return nodes;
    }
    private int alphaBeta(Board currentBoard, int difficulty, int alpha, int beta, int team, Figure moved){
        Map<Move,Board> nodes = possibleMoves(currentBoard,team,moved);
        if(moved != null) {
            if(nodes.isEmpty()) {
                return alphaBeta(currentBoard,difficulty-1,alpha,beta,-team,null);
            }
        } else if(difficulty == 0 || nodes.isEmpty()) {
            return valueOfBoard(currentBoard);
        }

        int value;
        if(team == this.team) {
            value = Integer.MIN_VALUE;
            for (Map.Entry<Move,Board> node : nodes.entrySet()) {
                int nodeValue = getNodeValueAB(node,difficulty,alpha,beta,team);
                if(nodeValue > value) {
                    value = nodeValue;
                }
                if(value > alpha){
                    alpha = value;
                }
                if(alpha >= beta) {
                    break;
                }
            }
        }
        else{
            value = Integer.MAX_VALUE;
            for (Map.Entry<Move,Board> node : nodes.entrySet()) {
                int nodeValue = getNodeValueAB(node,difficulty,alpha,beta,team);
                if(nodeValue < value) {
                    value = nodeValue;
                }
                if(value < beta){
                    beta = value;
                }
                if(beta <= alpha){
                    break;
                }
            }
        }
        return value;
    }
    private int getNodeValueAB(Map.Entry<Move, Board> node,int difficulty,int alpha, int beta, int team) {
        Move move = node.getKey();
        Board board = node.getValue();

        if (move.captured != null) {
            Figure moved = board.getFigure(move.to);
            return alphaBeta(board,difficulty,alpha,beta,team,moved);
        } else {
            return alphaBeta(board,difficulty-1,alpha,beta,-team,null);
        }
    }
}
