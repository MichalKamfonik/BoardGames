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
    static final int FIELD_SIZE = 30;   // !!!!to be changed to private after moving mouse listener to BoardGames class
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
     * Available games array
     */
    private final Game[] availableGames = new Game[1];
    /**
     * Chosen Board game
     */
    private Game chosenGame;
    private Board chosenBoard;
    /**
     * Panel for showing The Board
     */
    private final JPanel pBoard;
    /**
     * Panel for showing Player One
     */
    private final PlayerPanel[] pPlayer;
    /**
     * Game thread
     */
    Thread game;
    /**
     * Method initializing all the panels in the window
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
            } else {
                throw new IllegalArgumentException("There is no such player");
            }
        }
    }

    public BoardGames() {
        super("Player");
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setSize(Frame_W, Frame_H);
        this.setLocation((width-Frame_W)/2, (height-Frame_H)/2);    // place the window in center of screen

        availableGames[0] = new Draughts(this,players);
        // ask user for chosen game, has to be done before initialization of BoardPanel and PlayerPanel
        new ChooseGameDialog().setVisible(true);
        pBoard = new BoardPanel();
        pPlayer = new PlayerPanel[2];
        pPlayer[0] = new PlayerPanel(1);
        pPlayer[1] = new PlayerPanel(2);

        InitComponents();
        //this.pack();      // does not look good
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
                @SuppressWarnings("unchecked")
                JComboBox<Player> comboBox = (JComboBox<Player>)e.getSource();
                comboBox.setEditable(false);
                Player newPlayer = (Player) comboBox.getSelectedItem();
                if(newPlayer != players[number-1]) {        // ignore if the same player was selected
                    players[number - 1].playerChanged();    // notify player - to interrupt current task
                    players[number - 1] = newPlayer;        // change the player
                    players[number - 1].initInfoPanel();    // change the info panel content
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

            int direction = 3-2*number;     // !!!! to be changed (now only 2-player-games possible)
            availablePlayers = new Player[]{
                    new User(infoPanel,pBoard,direction),
                    new MinMaxAB(infoPanel,chosenGame,direction)
            };
            playerComboBox = new JComboBox<>(availablePlayers);

            initSelectionPanel();

            players[number-1] = availablePlayers[0];    // default player is User
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
            removedPanel.add(new JLabel(captured.getImage()),new Object()); // display captured figure in GUI
            revalidate();   // refresh the GUI
            repaint();
        }
    }

    /**
     * Panel for displaying board
     */
    private class BoardPanel extends JPanel {   // class only for overriding paintComponent method

        private boolean gameStarted = false;        // flag for signalizing that game have been started

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(gameStarted){
                drawBoard(g);
            }
        }

        public BoardPanel(){
            JButton startButton = new JButton("START");
            startButton.addActionListener(e->{
                this.remove(startButton);
                gameStarted = true;
                repaint();
                game = new Thread(chosenGame);
                game.start();
            });
            this.add(startButton);
        }

        private void drawBoard(Graphics g) {
            int maxX = chosenBoard.getMaxX();   // number of columns
            int maxY = chosenBoard.getMaxY();   // number of rows
            int offsetX = (this.getBounds().width - FIELD_SIZE*maxX)/2;     // position of board in the panel
            int offsetY = (this.getBounds().height - FIELD_SIZE*maxY)/2;    // position of board in the panel

            for(int y=maxY; y>0; y--) {         // for each row
                for(int x=1; x<=maxX; x++) {    // for each column
                    Position pos = new Position(x,y);
                    g.drawImage(
                            chosenBoard.getField(pos).getImage(),
                            offsetX+(x-1)*FIELD_SIZE,
                            offsetY+(maxY-y)*FIELD_SIZE,
                            null
                    );  // draw all fields on the panel
                }
            }
            try{
                chosenBoard.getFigures().
                        forEach((fig) ->
                        g.drawImage(
                                fig.getImage().getImage(),
                                offsetX+(fig.getPos().x-1)*FIELD_SIZE,
                                offsetY+(maxY-fig.getPos().y)*FIELD_SIZE,
                                null)
                );  // draw all figures on the panel
            }
            catch(ConcurrentModificationException e) {  // !!!! need to synchronize the method with game thread
                e.printStackTrace();
            }
        }
    }

    public void showMessage(String s){
        JOptionPane.showMessageDialog(null,s);
    }

    public void showEndMessage(String s) {
        int option = JOptionPane.showOptionDialog(null,s,"Game Over",JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,null,new String[]{"New game","Exit"},null);
        if(option == 0){
            this.dispose();
            newGame();
        } else if(option == 1 || option == -1){
            System.exit(0);
        }
    }

    private static void newGame(){
        BoardGames bG = new BoardGames();
        bG.setVisible(true);
    }

    private class ChooseGameDialog extends JDialog{
        public ChooseGameDialog() {
            super(BoardGames.this, "Choose game", true);
            this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);

            int width = Toolkit.getDefaultToolkit().getScreenSize().width;
            int height = Toolkit.getDefaultToolkit().getScreenSize().height;
            this.setSize(Frame_W/2, Frame_H/2);
            this.setLocation((width-Frame_W/2)/4, (height-Frame_H/2)/4);    // place the window in center of screen

            this.InitComponents();
            this.pack();
        }
        private void InitComponents(){
            JPanel comboBoxPanel = new JPanel();
            JPanel buttonPanel = new JPanel();
            JComboBox<Game> gameSelector = new JComboBox<>(availableGames);
            gameSelector.setEditable(false);
            chosenGame = (Game) gameSelector.getSelectedItem();     // select first game as default
            chosenBoard = chosenGame.getBoard();                    // select first game as default

            gameSelector.addActionListener(e -> {
                    chosenGame = (Game)((JComboBox<Game>) e.getSource()).getSelectedItem();
                    chosenBoard = chosenGame.getBoard();
            });

            JButton select = new JButton("OK");
            select.addActionListener(e -> this.dispose());

            comboBoxPanel.add(gameSelector);
            buttonPanel.add(select);

            this.add(comboBoxPanel,BorderLayout.NORTH);
            this.add(buttonPanel,BorderLayout.SOUTH);
        }
    }

    public static void main(String[] args) {
        // give the control to GUI thread
        EventQueue.invokeLater(BoardGames::newGame);
    }
}
