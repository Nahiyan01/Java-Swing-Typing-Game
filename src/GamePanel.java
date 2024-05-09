import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.Timer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GamePanel extends JPanel implements ActionListener,KeyListener{
	private static final long serialVersionUID = 1L;
	
	public final int borderWidth = 800;
	public final int borderHeight = 600;
	BufferedImage background;
	JTextField textField;
	boolean gameOver = false;
	
	Timer gameLoop;
	Timer spawningWordTimer;
	
	public int score = 0;
	public final int FPS = 60;
	
	public class Word{
		int x;
		int y;
		String text;
		Color[] colors;
		Word(int x,int y,String text){
			this.x =x;
			this.y = y;
			this.text =text;
			this.colors = new Color[text.length()];
			for(int i =0 ;i<text.length();i++) {
				colors[i] = Color.white;
			}
		}
		
		void move() {
			y += 1;
		}
		
		void draw(Graphics2D g2) {
			g2.setColor(Color.white);
			g2.setFont(new Font("Arial",Font.BOLD,20));
			for(int i = 0;i<text.length();i++) {
				g2.setColor(colors[i]);
				g2.drawString(String.valueOf(text.charAt(i)), x + i * 20, y);
			}
		}
		
		boolean isFullyTyped() {
            for (Color color : colors) {
                if (color != Color.GREEN) {
                    return false;
                }
            }
            return true;
        }
	}
	
	
	//game logic
	
	Word word;
	ArrayList<Word>words;
	String currentTypedWord;
	 
	GamePanel() throws IOException{
		Dimension size = new Dimension(borderWidth,borderHeight);
		this.setPreferredSize(size);
		this.setFocusable(true);
		//setBackground(Color.black);
		
		background = ImageIO.read(getClass().getResourceAsStream("bg.png"));
		//bird_Img = ImageIO.read(getClass().getResourceAsStream("flappybird.png"));
		words= new ArrayList<>();
		
		/*setting up text field
		textField =new JTextField("TExt");
		textField.setBounds(15,15,400,50);
		textField.setBackground(Color.white);
		this.add(textField);*/
		
		spawningWordTimer = new Timer(1200, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spawningWords();
			}
		});
		spawningWordTimer.start();
		
		gameLoop = new Timer(1000/FPS,this);
		gameLoop.start();
		addKeyListener(this);
	}
	
	
	public void spawningWords() {
		ArrayList<String> loadedWords = loadWordsFromFile("word.txt");
		String randomWord = loadedWords.get((int) (Math.random() * loadedWords.size()));
		int randomXpos = (int)(Math.abs((0 -(Math.random() * borderHeight))))	;
		Word word = new Word(randomXpos,0,randomWord);
		words.add(word);
	}
	
	
	public ArrayList<String> loadWordsFromFile(String string) {
		ArrayList<String> wordsList = new ArrayList<>();
		try(Scanner s = new Scanner(getClass().getResourceAsStream("word.txt"))){
			while(s.hasNextLine()) {
				String word = s.nextLine().trim();
				wordsList.add(word);
			}
			}catch(Exception e) {
				e.printStackTrace();
			}
		return wordsList;
		}
		
	
	/*public void checkWord(String userInput) {
		if (currentTypedWord != null) {
            for (Word word : words) {
                if (currentTypedWord.equals(word.text.substring(0, currentTypedWord.length()))) {
                    for (int i = 0; i < currentTypedWord.length(); i++) {
                        word.colors[i] = Color.GREEN;
                    }
                    if (currentTypedWord.length() == word.text.length()) {
                        if (word.isFullyTyped()) {
                            words.remove(word);
                            score++;
                        }
                        currentTypedWord = "";
                    }
                }
            }
        }
	}*/

	@Override
	public void actionPerformed(ActionEvent e) {
		if(!gameOver) {
			update();
			repaint();
		}
		
	}
	
	public void update() {
		for(int i=0;i<words.size();i++) {
			word = words.get(i);
			word.move();
			
			if (word.isFullyTyped()) {
                words.remove(i);
                score++;
                i--;
            }
			
			else if(word.y > borderHeight ) {
				gameOver = true;
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		draw(g2);
		if(gameOver) {
			drawGameOverText(g2);
		}
		g2.dispose();
		
	}

	private void drawGameOverText(Graphics2D g2) {
		g2.setBackground(Color.black);
		g2.setFont(new Font("Arial", Font.BOLD, 36));
		g2.drawString("GAME OVER!!!", 200, 200);
		g2.setFont(new Font("Arial", Font.BOLD, 40));
		g2.setColor(Color.GREEN);
		g2.drawString("Words Collected " + score, 350, 400);
	}


	public void draw(Graphics2D g2) {
		g2.drawImage(background,0,0,borderWidth,borderHeight,null);
		
		for(Word word:words) {
			word.draw(g2);
		}
	}


	@Override
	public void keyTyped(KeyEvent e) {
		
		char c = e.getKeyChar();
		boolean letterTyped = false;
		for(Word word: words) {
			if(!word.isFullyTyped()) {
				String lowercaseWord = word.text.toLowerCase();
				for(int i=0;i<lowercaseWord.length();i++) {
					if(!word.colors[i].equals(Color.GREEN) && lowercaseWord.charAt(i) ==Character.toLowerCase(c)) {
						word.colors[i] = Color.GREEN;
						letterTyped = true ;
						break;
					}
				}
				if(letterTyped) {
					break;
				}
			}
		}
		if(!letterTyped) {
			currentTypedWord = "";
		}
		else {
			currentTypedWord += c;
		}
		
	    if(gameOver) {
	    	words.clear();
	    	gameOver = false;
	    	gameLoop.start();
	    	spawningWordTimer.start();
	    }
	}


	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
}

