package pl.kamfonik.boardgames;
        
import javax.swing.*;

public abstract class Player {
    protected static final int PANEL_W = 140;   // to be moved outside?
    protected static final int PANEL_H = 170;   // to be moved outside?

    protected JPanel infoPanel;                 // to be moved outside?
    protected String playerName = "";
    protected int team;
    protected boolean playerChanged = false;

    abstract String getName();
    abstract Move getMove(Board chosenBoard,Figure moved);
    abstract void initInfoPanel();              // to be moved outside?
    abstract void playerChanged();

    protected final void initPlayerPanel(JLabel playerLabel, JComponent playerComponent) { // to be moved outside?
        GroupLayout layout = new GroupLayout(this.infoPanel);
        this.infoPanel.setLayout(layout);

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

