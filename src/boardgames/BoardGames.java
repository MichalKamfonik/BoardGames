package boardgames;

import java.awt.*;
import java.util.ConcurrentModificationException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * A graphic interface for playing some board games.
 * 
 * @author Michał Kamfonik
 * @version 0.1 06-09-2020
 */
public class BoardGames extends JFrame {
    /**
     * Default Height of the window
     */
    private static final int Frame_H = 600;
    /**
     * Default Width of the window
     */
    private static final int Frame_W = 800;
    /**
     * Default Field size
     */
    private static final int FIELD_SIZE = 30;
    /**
     * Content Pane reference
     *
     */
    private final Container contentPane = this.getContentPane();
    /**
     * Array of players in the game
     */
    private final Player[] players = new Player[2];
    /**
     * Chosen Board game
     */
    private final Game chosenGame = new Draughts(this,players);
    private final Board chosenBoard = chosenGame.getBoard();
    /**
     * Panel for showing The Board
     */
    private final JPanel pBoard = new BoardPanel();
    /**
     * Panel for showing Player One
     */
    private final PlayerPanel[] pPlayer = {new PlayerPanel(1),new PlayerPanel(2)};
    /**
     * Method initializing all the panels int the window
     */
    private void InitComponents(){
        pBoard.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        pPlayer[0].setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        pPlayer[1].setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(pPlayer[0],200,200,200)
                .addComponent(pBoard)
                .addComponent(pPlayer[1],200,200,200)
        );
        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(pPlayer[0])
                .addComponent(pBoard)
                .addComponent(pPlayer[1])
        );
    }
    /**
     * Panel for displaying player information
     */
    private class PlayerPanel extends JPanel {

        private final String name;
        private final int number;
        private final JPanel selectionPanel = new JPanel();
        private final JPanel infoPanel = new JPanel();
        private final JPanel removedPanel = new JPanel();
        private final JLabel nameLabel = new JLabel();

        private final Player[] availablePlayers;
        
        private final JComboBox<Player> playerComboBox;
        
        private void initSelectionPanel() {
            GroupLayout layout = new GroupLayout(selectionPanel);
            selectionPanel.setLayout(layout);
            selectionPanel.setBorder(BorderFactory.createEtchedBorder());
            
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);

            playerComboBox.addActionListener(e -> {
                JComboBox<Player> comboBox = (JComboBox<Player>)e.getSource();
                Player newPlayer = (Player) comboBox.getSelectedItem();
                if(newPlayer != players[number-1]) {
                    players[number - 1].playerChanged();
                    players[number - 1] = newPlayer;
                    players[number - 1].initInfoPanel();
                }
            });
            
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addComponent(nameLabel,60,60,60)
                    .addComponent(playerComboBox,70,70,70)
            );
            layout.setVerticalGroup(layout.createParallelGroup()
                    .addComponent(nameLabel,20,20,20)
                    .addComponent(playerComboBox,20,20,20)
            );
        }
        
        public PlayerPanel(int number) {
            this.number = number;
            this.name = "Player " + this.number;
            nameLabel.setText(this.name);

            int direction = 3-2*number;
            availablePlayers = new Player[]{new User(infoPanel,pBoard,direction), new MinMaxAB(infoPanel,chosenGame,direction)};
            playerComboBox = new JComboBox<>(availablePlayers);

            initSelectionPanel();

            players[number-1] = availablePlayers[0];
            players[number-1].initInfoPanel();
            
            infoPanel.setBorder(BorderFactory.createEtchedBorder());
            removedPanel.setBorder(BorderFactory.createEtchedBorder());
            
            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);
            
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
            
            layout.setHorizontalGroup(layout.createParallelGroup()
                    .addComponent(selectionPanel,170,170,170)
                    .addComponent(infoPanel,170,170,170)
                    .addComponent(removedPanel,170,170,170)
            );
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addComponent(selectionPanel,50,50,50)
                    .addContainerGap(0,Short.MAX_VALUE)
                    .addComponent(infoPanel,200,200,200)
                    .addContainerGap(0,Short.MAX_VALUE)
                    .addComponent(removedPanel,120,120,120)
            );
        }

        public void captureFigure(Figure captured){
                removedPanel.add(new JLabel(captured.getImage()),new Object());
                revalidate();
                repaint();
        }
    }

    public void captureFigure(Player player,Figure captured){
        if(captured != null) {
            int playerNumber = -1;
            for (int i = 0; i < players.length; i++) {
                if(player == players[i]){
                    playerNumber=i;
                    break;
                }
            }
            if(playerNumber >= 0) {
                pPlayer[playerNumber].captureFigure(captured);
            }
        }
    }
    
    public BoardGames() {
        super("Player");
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setSize(Frame_W, Frame_H);
        this.setLocation((width-Frame_W)/2, (height-Frame_H)/2);
        
        InitComponents();
        //this.pack();
    }
    private class BoardPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {

            int maxX = chosenBoard.getMaxX();
            int maxY = chosenBoard.getMaxY();
            int offsetX = (this.getBounds().width - FIELD_SIZE*maxX)/2;
            int offsetY = (this.getBounds().height - FIELD_SIZE*maxY)/2;

            for(int y=maxY; y>0; y--) {
                for(int x=1; x<=maxX; x++) {
                    Position pos = new Position(x,y);
                    g.drawImage(chosenBoard.getField(pos).getImage(),offsetX+(x-1)*FIELD_SIZE,offsetY+(maxY-y)*FIELD_SIZE,null);
                }
            }
            try{
                chosenBoard.getFigures().forEach((fig) -> g.drawImage(fig.getImage().getImage(),offsetX+(fig.getPos().x-1)*FIELD_SIZE,offsetY+(maxY-fig.getPos().y)*FIELD_SIZE,null));
            }
            catch(ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
    }

    public void showMessage(String s){
        JOptionPane.showMessageDialog(null,s);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater( () -> {
            BoardGames bG = new BoardGames();
            bG.setVisible(true);

            Thread game = new Thread(bG.chosenGame);
            game.start();
        });
    }
}
