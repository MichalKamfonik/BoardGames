package boardgames;
        
import javax.swing.JPanel;

public abstract class Player {
    protected static final int PANEL_W = 140; // to be moved outside?
    protected static final int PANEL_H = 170; // to be moved outside?

    protected JPanel userPanel = new JPanel(); // to be moved outside?
    protected String playerName = "";
    protected int team;

    abstract JPanel getJPanel(); // to be moved outside?
    abstract String getName();
    abstract Move getMove(Board chosenBoard);
}

