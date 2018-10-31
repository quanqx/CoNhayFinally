package object;

import java.util.ArrayList;

import handler.CharacterHandler;
import object.Character;

public class GameBoard {
	
	private int startX;
	private int startY;
	private int width;
	private ArrayList<Character> allys;
	private ArrayList<Character> enemys;
	private ArrayList<Hint> hints;
	
	public GameBoard(int widthCharacter){
		initCharacter(widthCharacter);
	}
	
	public GameBoard(int startX, int startY, int widthGameBoard, int widthCharacter) {
		super();
		this.startX = startX;
		this.startY = startY;
		this.width = widthGameBoard;
		initCharacter(widthCharacter);
		hints = new ArrayList<>();
	}
	
	public void initCharacter(int widthCharacter){
		this.allys = CharacterHandler.genCharacter(CharacterHandler.ALLY, widthCharacter);
		this.enemys = CharacterHandler.genCharacter(CharacterHandler.ENEMY, widthCharacter);
	}
	
	public int getStartX() {
		return startX;
	}
	
	public void setStartX(int startX) {
		this.startX = startX;
	}
	
	public int getStartY() {
		return startY;
	}
	
	public void setStartY(int startY) {
		this.startY = startY;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public ArrayList<Character> getAllys() {
		return allys;
	}
	
	public ArrayList<Character> getEnemys() {
		return enemys;
	}
	
	public void setAllys(ArrayList<Character> allys) {
		this.allys = allys;
	}
	
	public void setEnemys(ArrayList<Character> enemys) {
		this.enemys = enemys;
	}
	
	public ArrayList<Hint> getHints() {
		return hints;
	}
	
	public void setHints(ArrayList<Hint> hints) {
		this.hints = hints;
	}
	
}
