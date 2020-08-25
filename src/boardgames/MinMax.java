package boardgames;

import javax.swing.*;
import java.util.*;

public class MinMax extends Player {

    public MinMax(int team) {
        super();
        playerName = "MinMax";
        this.team = team;
        this.initPanel();
    }

    private void initPanel() {
        JLabel difficultyLabel = new JLabel("Difficulty", JLabel.CENTER);
        JSlider difficultySlider = new JSlider(1,5,3);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintLabels(true);
        difficultySlider.setSnapToTicks(true);

        GroupLayout layout = new GroupLayout(this.userPanel);
        this.userPanel.setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(difficultyLabel, Player.PANEL_W, Player.PANEL_W, Player.PANEL_W)
                .addComponent(difficultySlider, Player.PANEL_W, Player.PANEL_W, Player.PANEL_W)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(difficultyLabel, 20, 20, 20)
                .addComponent(difficultySlider, 50, 50, 50)
        );
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
        TreeMap<Integer,Move> nodes = new TreeMap<>();

        for (Move move : moves) {
            Board node = chosenBoard.deepClone();
            node.moveFigure(move);
            nodes.put(miniMax(node,1,team),move);
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

    private int miniMax(Board board, int difficulty, int team){
        List<Board> nodes = possibleMoves(board,team);
        if(difficulty == 0 || nodes.isEmpty()){
            return valueOfBoard(board);
        }
        int value;
        if(team == this.team) {
            value = Integer.MIN_VALUE;
            for (Board node : nodes) {
                int nodeValue = miniMax(node,difficulty-1,-team);
                if(nodeValue > value) {
                    value = nodeValue;
                }
            }
        }
        else{
            value = Integer.MAX_VALUE;
            for (Board node : nodes) {
                int nodeValue = miniMax(node,difficulty-1,team);
                if(nodeValue < value) {
                    value = nodeValue;
                }
            }
        }
        return value;
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

    private List<Board> possibleMoves(Board board,int team){
        List<Board> nodes = new LinkedList<>();
        if(board.getFigures(-team).isEmpty()) return nodes;

        for (Figure figure : board.getFigures(team)) {
            for (Move move : figure.getMoves(board)) {
                Board node = board.deepClone();
                node.moveFigure(move);
                nodes.add(node);
            }
        }
        return nodes;
    }
}
