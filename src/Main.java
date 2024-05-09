import java.awt.FlowLayout;
import java.io.IOException;
import javax.swing.JFrame;

public class Main{


	public static void main(String[] args) throws IOException {
		JFrame window = new JFrame("Type_Hero");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		GamePanel gamePanel = new GamePanel();
		window.setSize(gamePanel.borderWidth,gamePanel.borderHeight);
		window.add(gamePanel);
		gamePanel.setLayout(new FlowLayout());
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		gamePanel.requestFocusInWindow();
	}
}
