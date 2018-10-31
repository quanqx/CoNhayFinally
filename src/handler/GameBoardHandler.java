package handler;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import object.Character;
import object.Hint;
import object.GameBoard;

public class GameBoardHandler {
	
	private GameBoard gameBoard;
	private int x;
	private int y;
	private int width;
	private int wCharacter;
	private Graphics g;
	
	public GameBoardHandler(GameBoard gameBoard, int wCharacter){
		this.gameBoard = gameBoard;
		this.x = gameBoard.getStartX();
		this.y = gameBoard.getStartY();
		this.width = gameBoard.getWidth();
		this.wCharacter = wCharacter;
	}
	
	public void drawGameBoard(){
		
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, width);
		g.drawLine(x, y, x + width, y + width);
		g.drawLine(x + width, y, x, y + width);
		g.drawLine(x + width, y, x, y + width);
		g.drawLine(x, y+width/2, x+width, y+width/2);
		g.drawLine(x+width/2, y, x+width/2, y+width);
		g.drawLine(x, y+width/4, x+width, y+width/4);
		g.drawLine(x+width/4, y, x+width/4, y+width);
		g.drawLine(x, y+3*width/4, x+width, y+3*width/4);
		g.drawLine(x+3*width/4, y, x+3*width/4, y+width);
		g.drawLine(x+width/2, y, x, y + width/2);
		g.drawLine(x+width, y+width/2, x+width/2, y+width);
		g.drawLine(x+width/2, y, x+width, y+width/2);
		g.drawLine(x, y + width/2, x+width/2, y+width);
	}
	
	private void drawCharacter(Character c){
		if(!c.isKing()){
			g.drawOval(x - wCharacter/2 + c.getCoordinate().getX()*width/4, y - wCharacter/2 + c.getCoordinate().getY()*width/4, wCharacter, wCharacter);
			g.fillOval(x - wCharacter/2 + c.getCoordinate().getX()*width/4, y - wCharacter/2 + c.getCoordinate().getY()*width/4, wCharacter, wCharacter);
		}
		else{
			g.drawRect(x - wCharacter/2 + c.getCoordinate().getX()*width/4, y - wCharacter/2 + c.getCoordinate().getY()*width/4, wCharacter, wCharacter);
			g.fillRect(x - wCharacter/2 + c.getCoordinate().getX()*width/4, y - wCharacter/2 + c.getCoordinate().getY()*width/4, wCharacter, wCharacter);
		}
	}
	
	public void drawCharacters(){
		for(Character c : gameBoard.getAllys()){
			if(c.isSelected()) g.setColor(Color.YELLOW);
			else g.setColor(Color.RED);
			drawCharacter(c);
		}
		for(Character c : gameBoard.getEnemys()){
			if(c.isSelected()) g.setColor(Color.YELLOW);
			else g.setColor(Color.BLUE);
			drawCharacter(c);
		}
	}
	
	public void drawHints(){
		if(gameBoard.getHints().size() != 0){
			g.setColor(Color.GREEN);
			for(Hint hint : gameBoard.getHints()){
				drawHint(hint);
			}
		}
	}
	
	public void drawHint(Hint hint){
		g.drawOval(x - hint.getWidth()/2 + hint.getCoordinate().getX()*width/4, y - hint.getWidth()/2 + hint.getCoordinate().getY()*width/4, hint.getWidth(), hint.getWidth());
		g.fillOval(x - hint.getWidth()/2 + hint.getCoordinate().getX()*width/4, y - hint.getWidth()/2 + hint.getCoordinate().getY()*width/4, hint.getWidth(), hint.getWidth());
	}
	
	public void setGraphics(Graphics g){
		this.g = g;
	}
	
	public int countCharacterDead(ArrayList<Character> characters){
		int count = 0;
		for(Character c : characters){
			if(!c.isAlive()) count ++;
		}
		return count;
	}
	
	public void setGameBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}
	
	public Graphics getG() {
		return g;
	}
	
}
