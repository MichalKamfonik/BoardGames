package boardgames;

import javax.swing.*;
import java.util.*;

public class MinMax extends Player {
    private static final int MAX_DIFFICULTY = 7;
    private static final int MIN_DIFFICULTY = 1;

    private int difficulty = 3;

    public MinMax(int team) {
        super();
        playerName = "MinMax";
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
                nodes.put(miniMax(node,difficulty,team,move.captured),move);
            } else if(!capturePossible){
                nodes.put(miniMax(node,difficulty-1,-team,null), move);
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

    private int miniMax(Board currentBoard, int difficulty, int team, Figure moved){
        Map<Move,Board> nodes = possibleMoves(currentBoard,team);
        if(moved != null){
            nodes.keySet().removeIf((move1) -> !move1.from.equals(moved.getPos())
                                               || move1.captured == null);
            if(nodes.isEmpty()) {
                return miniMax(currentBoard, difficulty-1,-team,null);
            }
        } else if(difficulty == 0 || nodes.isEmpty()) {
            return valueOfBoard(currentBoard);
        }

        int value;
        if(team == this.team) {
            value = Integer.MIN_VALUE;
            for (Map.Entry<Move,Board> node : nodes.entrySet()) {
                int nodeValue = getNodeValue(difficulty, team, node);
                if(nodeValue > value) {
                    value = nodeValue;
                }
            }
        }
        else{
            value = Integer.MAX_VALUE;
            for (Map.Entry<Move,Board> node : nodes.entrySet()) {
                int nodeValue = getNodeValue(difficulty, team, node);
                if(nodeValue < value) {
                    value = nodeValue;
                }
            }
        }
        return value;
    }

    private int getNodeValue(int difficulty, int team, Map.Entry<Move, Board> node) {
        Move move = node.getKey();
        Board board = node.getValue();

        if (move.captured != null) {
            Figure moved = board.getFigure(move.to);
            return miniMax(board, difficulty, team, moved);
        } else {
            return miniMax(board, difficulty - 1, -team, null);
        }
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

    private Map<Move,Board> possibleMoves(Board board,int team){
        Map<Move,Board> nodes = new HashMap<>();
        if(board.getFigures(-team).isEmpty()) return nodes;
        boolean capturePossible = false;    // I have to move it somewhere outside of the algorithm

        for (Figure figure : board.getFigures(team)) {
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
}
