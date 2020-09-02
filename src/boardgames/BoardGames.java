/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boardgames;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * A graphic interface for playing some board games.
 * 
 * @author Michał Kamfonik
 * @version 0.1 29-05-2020
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
     * Content Pane reference
     */
    private final Container contentPane = this.getContentPane();
    /**
     * Array of players in the game
     */
    private final Player[] players = new Player[2];
    /**
     * Chosen Board game
     */
    private final Game chosenGame = new Draughts(players);
    private final Board chosenBoard = chosenGame.getBoard();
    /**
     * Panel for showing The Board
     */
    private final JPanel pBoard = chosenBoard.getJPanel();
    /**
     * Panel for showing Player One
     */
    private final PlayerPanel pPlayer1 = new PlayerPanel(1);
    /**
     * Panel for showing Player Two
     */
    private final PlayerPanel pPlayer2 = new PlayerPanel(2);
    /**
     * Method initializing all the panels int the window
     */

    private void InitComponents(){
        pBoard.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        pPlayer1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        pPlayer2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(pPlayer1,200,200,200)
                .addComponent(pBoard)
                .addComponent(pPlayer2,200,200,200)
        );
        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(pPlayer1)
                .addComponent(pBoard)
                .addComponent(pPlayer2)
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
                JComboBox<Player> comboBox = (JComboBox<Player>) e.getSource();
                players[number-1].playerChanged();
                players[number-1] = (Player) comboBox.getSelectedItem();
                players[number-1].initInfoPanel();
            });
            
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addComponent(nameLabel,50,50,50)
                    .addComponent(playerComboBox,80,80,80)
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

            removedPanel.add(new JLabel("",JLabel.CENTER));                     // to be changed
            
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
                    .addComponent(removedPanel)
            );
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
