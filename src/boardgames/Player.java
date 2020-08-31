package boardgames;
        
import javax.swing.*;

public abstract class Player {
    protected static final int PANEL_W = 140; // to be moved outside?
    protected static final int PANEL_H = 170; // to be moved outside?

    protected JPanel userPanel = new JPanel(); // to be moved outside?
    protected String playerName = "";
    protected int team;

    abstract JPanel getJPanel(); // to be moved outside?
    abstract String getName();
    abstract Move getMove(Board chosenBoard,Figure moved);

    protected void initPlayerPanel(JLabel playerLabel, JComponent playerComponent) {
        GroupLayout layout = new GroupLayout(this.userPanel);
        this.userPanel.setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(playerLabel, Player.PANEL_W, Player.PANEL_W, Player.PANEL_W)
                .addComponent(playerComponent, Player.PANEL_W, Player.PANEL_W, Player.PANEL_W)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(playerLabel, 20, 20, 20)
                .addComponent(playerComponent, Player.PANEL_H - 20, Player.PANEL_H - 20, Player.PANEL_H - 20)
        );
    }
}

