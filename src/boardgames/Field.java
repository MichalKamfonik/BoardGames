package boardgames;

import javax.swing.*;
import java.awt.*;

public class Field {
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    private static final Image imageWhite = new ImageIcon("whiteSpot.png").getImage();
    private static final Image imageBlack = new ImageIcon("blackSpot.png").getImage();

    final int color;

    public Field(int color) {
        this.color = color;
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
