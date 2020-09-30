package pl.kamfonik.boardgames;

import javax.swing.*;
import java.awt.*;

public class Field {
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    private static final Image imageWhite = new ImageIcon(Field.class.getResource("/images/whiteSpot.png")).getImage();
    private static final Image imageBlack = new ImageIcon(Field.class.getResource("/images/blackSpot.png")).getImage();

    private final int color;

    public Field(int color) {
        this.color = color;
    }
    public Field(Field other) {
        this.color = other.color;
    }

    public Image getImage(){
        if(color == WHITE) {
            return imageWhite;
        }
        else if(color == BLACK){
            return imageBlack;
        }
        return null;
    }
}
