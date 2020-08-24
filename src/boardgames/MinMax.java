package boardgames;

import javax.swing.*;
import java.util.List;

public class MinMax extends Player {
    private final JPanel boardPanel;
    private List<Move> moves;
    private Board currentBoard;

    public MinMax(JPanel boardPanel, int team) {
        super();
        playerName = "MinMax";
        this.team = team;
        this.initPanel();
        this.boardPanel = boardPanel;
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
//        fdasdfdaadafds
        return null;
    }
}
