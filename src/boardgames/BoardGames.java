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
    private Container contentPane = this.getContentPane();
    /**
     * Chosen Board game
     */
    private Board chosenBoard = new DraughtsBoard(8,8);
    /**
     * Panel for showing The Board
     */
    private JPanel pBoard = chosenBoard.getJPanel();
    /**
     * Array of players in the game
     */
    private Player[] players = new Player[2];
    /**
     * Panel for showing Player One
     */
    private PlayerPanel pPlayer1 = new PlayerPanel(1);
    /**
     * Panel for showing Player Two
     */
    private PlayerPanel pPlayer2 = new PlayerPanel(2);
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
        
        private String name;
        private JPanel selectionPanel = new JPanel();
        private JPanel infoPanel;
        private JPanel removedPanel = new JPanel();
        private JLabel nameLabel = new JLabel();
        
        private Player[] availablePlayers = new Player[2];                      // tutaj dodawaj możliwych graczy(algorytmy/userow...)
        
        private JComboBox<Player> playerCombBox = new JComboBox<>(availablePlayers);
        
        private void initSelectionPanel() {
            GroupLayout layout = new GroupLayout(selectionPanel);
            selectionPanel.setLayout(layout);
            selectionPanel.setBorder(BorderFactory.createEtchedBorder());
            
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
            
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addComponent(nameLabel,50,50,50)
                    .addComponent(playerCombBox,80,80,80)
            );
            layout.setVerticalGroup(layout.createParallelGroup()
                    .addComponent(nameLabel,20,20,20)
                    .addComponent(playerCombBox,20,20,20)
            );
        }
        
        public PlayerPanel(int number) {
            super();
            this.name = "Player" + number;
            nameLabel.setText(this.name);
            
            initSelectionPanel();
            
            int direction = 3-2*number;

//            players[number-1] = new User(pBoard,direction);
            Game game = new Draughts(players,chosenBoard);
            if(number == 1) players[number-1] = new User(pBoard,direction); // to be deleted
            if(number == 2) players[number-1] = new MinMaxAB(game,direction); // to be deleted
            
            infoPanel = players[number-1].getJPanel();                          // to be changed
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

            Thread game = new Thread(new Draughts(bG.players, bG.chosenBoard));
            game.start();
        });
    }
}
