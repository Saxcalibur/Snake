import javax.swing.*;
import java.io.File;


public class GameFrame extends JFrame {

    GameFrame() {
        this.add(new GamePanel());
        this.setTitle("Ollie Joestar's Snake");
        ImageIcon image = new ImageIcon("Data/snakeLogo.png");
        this.setIconImage(image.getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);

    }
}
