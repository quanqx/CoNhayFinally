package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import handler.CharacterHandler;
import handler.CoordinateHandler;
import handler.Estimate;
import handler.GameBoardHandler;
import handler.HintHandler;
import helper.Helper;
import object.Hint;
import object.BestMove;
import object.Character;
import object.Coordinate;
import object.GameBoard;

public class Board extends JFrame implements MouseListener, KeyListener{
	
	private static final int startX = 150;
	private static final int startY = 100;
	private static final int widthGameBoard = 500;
	private static final int wCharacter = 50;
	private static final int wHint = 30;
	private static int DEPTH = 6;
	
	public static final boolean ALLY = true;
	public static final boolean ENEMY = false;
	
	private boolean turn;
	
	private Character characterSelected;
	private Hint lastMove;
	private Hint currentMove;
	
	private HintHandler hintHandler;
	private GameBoard gameBoard;
	private GameBoardHandler gameBoardHandler;
	private CharacterHandler characterHandler;
	private ArrayList<BestMove> bestMoves;
	
	public Board(){
		initProperties();
		this.addMouseListener(this);
		this.addKeyListener(this);
		initComps();
	}
	
	private void initProperties() {
		this.setTitle("Co nhay");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800,700);
		this.setLocationRelativeTo(null);
		getContentPane().setBackground(Color.WHITE);
	}
	
	private void initComps(){
		initGameBoard();
		hintHandler = new HintHandler(wHint);
		this.turn = ALLY;
		bestMoves = new ArrayList<BestMove>();
		lastMove = null;
		currentMove = null;
	}
	
	private void initGameBoard(){
		gameBoard = new GameBoard(startX, startY, widthGameBoard, wCharacter);
		gameBoardHandler = new GameBoardHandler(gameBoard, wCharacter);
		characterHandler = new CharacterHandler();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		gameBoardHandler.setGraphics(g);
		gameBoardHandler.drawGameBoard();
		gameBoardHandler.drawCharacters();
		gameBoardHandler.drawHints();
		gameBoardHandler.getG().setColor(Color.PINK);
		if(lastMove != null)
			gameBoardHandler.drawHint(lastMove);
		if(currentMove != null)
			gameBoardHandler.drawHint(currentMove);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("mouse click!");
		
		Coordinate mouseCoordinate = new Coordinate(e.getX(), e.getY());
		if(humanMove(mouseCoordinate)){
			characterSelected.setSelected(false);
			characterSelected = null;
			gameBoard.setHints(new ArrayList<>());
			turn =! turn;
			repaint();
			if(gameOver(gameBoard.getAllys(), gameBoard.getEnemys())){
				JOptionPane.showMessageDialog(this, "Chien thang!");
				reset();
				return;
			}
			computerMove();
			repaint();
			if(gameOver(gameBoard.getAllys(), gameBoard.getEnemys())){
				JOptionPane.showMessageDialog(this, "Thua roi!");
				reset();
				return;
			}
			return;
		}
		if(turn == ALLY){
			selectCharacter(mouseCoordinate, gameBoard.getAllys(), gameBoard.getEnemys());
		}
		else{
			selectCharacter(mouseCoordinate, gameBoard.getEnemys(), gameBoard.getAllys());
		}
		repaint();
	}
	
	private void reset(){
		initComps();
		
		repaint();
	}
	
	private void selectCharacter(Coordinate mouseCoordinate, ArrayList<Character> allys, ArrayList<Character> enemys){
		Character selected = getCharacterSelected(allys, mouseCoordinate);
		if(selected != null){
			if(characterSelected != null){
				if(selected.getCoordinate().equal(characterSelected.getCoordinate()))
					return;
				characterSelected.setSelected(false);
			}
			characterSelected = selected;
			characterSelected.setSelected(true);
			if(characterSelected.isKing()){
				gameBoard.setHints(hintHandler.getHintsForKing(characterSelected, allys, enemys));
			}
			else{
				gameBoard.setHints(hintHandler.getHints(characterSelected, allys, enemys));
			}
		}
	}
	
	private Character getCharacterSelected(ArrayList<Character> characters, Coordinate mouseCoordinate){
		for(Character c : characters){
			boolean selected = CharacterHandler.characterSelected(c, mouseCoordinate, gameBoard);
			if(selected){
				return c;
			}
		}
		return null;
	}
	
	private void move(Character character, Coordinate newCoordinate){
		character.setCoordinate(newCoordinate);
	}
	
	private boolean humanMove(Coordinate newCoordinate){
		if(gameBoard.getHints().size() == 0) 
			return false;
		for(Hint h : gameBoard.getHints()){
			Coordinate h_oxy = CoordinateHandler.getCoordinateOxy(h.getCoordinate(), startX, startY, widthGameBoard);
			int distance = Helper.distance(h_oxy, newCoordinate);
			if(distance <= wCharacter/2){
				move(characterSelected, h.getCoordinate());
				checkKillCharacter();
				return true;
			}
		}
		return false;
	}
	
	private void computerMove(){
		ArrayList<Character> allys = copyFrom(gameBoard.getAllys());
		ArrayList<Character> enemys = copyFrom(gameBoard.getEnemys());
		minimax(Integer.MIN_VALUE, Integer.MAX_VALUE, turn, 0, allys, enemys);
		BestMove best = bestMove(this.bestMoves);
		System.out.println("best move: cdn(" + best.getCharacter().getCoordinate().getX()+ " : " + best.getCharacter().getCoordinate().getY() +") -- score: " + best.getScores());
		Character c = characterHandler.getCharacterByCoordinate(best.getCharacter().getCoordinate(), gameBoard.getEnemys());
		lastMove = new Hint(c.getCoordinate(), 25);
		c.setCoordinate(best.getCoordinate());
		currentMove = new Hint(c.getCoordinate(), 25);
		checkKillCharacter(gameBoard.getEnemys(), gameBoard.getAllys());
		turn = !turn;
	}
	
	private int est(ArrayList<Character> allys, ArrayList<Character> enemys){
		int scores = 0;
		Estimate e = new Estimate(hintHandler);
		
		for(Character ally : allys){
			scores += e.hoaChanh(ally);
		}
		scores += -40*(-allys.size() + enemys.size());
		
		for(Character enemy : enemys){
			scores -= e.hoaChanh(enemy);
		}
		
		return scores;
	}
	
	
	public int minimax(int alpha, int beta, boolean turn, int depth, ArrayList<Character> allys, ArrayList<Character> enemys){
		
		if(alpha >= beta){
			return turn != ALLY ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		}
		
		if(gameOver(allys, enemys)){
			if(turn == ALLY){
				return Integer.MAX_VALUE;
			}
			else{
				return Integer.MIN_VALUE;
			}
		}
		
		if(depth == DEPTH){
			if(turn != ALLY){
				return est(enemys, allys);
			}
			else{
				return est(enemys, allys);
			}
		}
		if(depth == 0) bestMoves.clear();
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		ArrayList<Character> backupAllys = copyFrom(allys);
		ArrayList<Character> backupEnemys = copyFrom(enemys);
		if(turn == ALLY){
			for(int i = 0; i < allys.size(); i++){
				Character ally = allys.get(i);
				ArrayList<Hint> hints = new ArrayList<>();
				if(ally.isKing()){
					hints = hintHandler.getHintsForKing(ally, allys, enemys);
				}
				else{
					hints = hintHandler.getHints(ally, allys, enemys);
				}
				if(!hints.isEmpty()){
					
					for(Hint hint : hints){
						Coordinate currentCoordinate = hint.getCoordinate();
						move(ally, hint.getCoordinate());
						checkKillCharacter(allys, enemys);
						
						int currentScore = minimax(alpha, beta, !turn, depth + 1, allys, enemys);
						min = Math.min(min, currentScore);
						beta = Math.min(beta, currentScore);
						
						allys = copyFrom(backupAllys);
						enemys = copyFrom(backupEnemys);
						ally.setCoordinate(currentCoordinate);
						if(currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) break;
					}
					
				}
			}
		}
		else{
			for(int i = 0; i < enemys.size(); i++){
				Character enemy = enemys.get(i);
				ArrayList<Hint> hints = new ArrayList<>();
				if(enemy.isKing()){
					hints = hintHandler.getHintsForKing(enemy, enemys, allys);
				}
				else{
					hints = hintHandler.getHints(enemy, enemys, allys);
				}
				if(!hints.isEmpty()){
					
					for(Hint hint : hints){
						Character backup = new Character(enemy.getCoordinate(), enemy.getWidth(), enemy.isAlive(), enemy.isSelected(), enemy.isKing());
						
						move(enemy, hint.getCoordinate());
						checkKillCharacter(enemys, allys);
						
						int currentScore = minimax(alpha, beta, !turn, depth + 1, allys, enemys);
						max = Math.max(max, currentScore);
						alpha = Math.max(alpha, currentScore);
						
						allys = copyFrom(backupAllys);
						enemys = copyFrom(backupEnemys);
						enemy.setCoordinate(backup.getCoordinate());
						if(depth == 0){
							bestMoves.add(new BestMove(currentScore, hint.getCoordinate(), backup));
						}
						if(currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) break;
					}
					
				}
			}
		}
		
		return turn == ALLY ? min : max;
	}
	
	private BestMove bestMove(ArrayList<BestMove> bestMoves){
		BestMove bestMove = bestMoves.get(0);
		
//		System.out.println("************");
		for(BestMove bm : bestMoves){
//			System.out.println("last coordinate: " + bm.getCharacter().getCoordinate().getX() + " : " + bm.getCharacter().getCoordinate().getY() +" --- hint: " + bm.getCoordinate().getX() + " : " + bm.getCoordinate().getY() +" --- scores: " + bm.getScores());
			if(bm.getScores() > bestMove.getScores()){
				bestMove = bm;
			}
		}
//		System.out.println("************");
		
		return bestMove;
	}
	
	private void checkKillCharacter(){
		if(turn == ALLY){
			if(characterSelected.isKing()){
				kingKillCharacter(characterSelected, gameBoard.getEnemys());
			}
			checkCharacterEnclosed(gameBoard.getEnemys(), gameBoard.getAllys());
		}
		else{
			if(characterSelected.isKing()){
				kingKillCharacter(characterSelected, gameBoard.getAllys());
			}
			checkCharacterEnclosed(gameBoard.getAllys(), gameBoard.getEnemys());
		}
	}
	
	private void checkKillCharacter(ArrayList<Character> allys, ArrayList<Character> enemys){
		for(int i = 0; i < allys.size(); i++){
			if(allys.get(i).isKing()){
				kingKillCharacter(allys.get(i), enemys);
			}
		}
		checkCharacterEnclosed(enemys, allys);
	}
	
	private void kingKillCharacter(Character kingAlly, ArrayList<Character> enemys){
		for(int i = 0; i < enemys.size(); i++){
			if(kingAlly.getCoordinate().equal(enemys.get(i).getCoordinate())){
				enemys.remove(enemys.get(i));
				return;
			}
		}
	}
	
	private void checkCharacterEnclosed(ArrayList<Character> allys, ArrayList<Character> enemys){
		for(int i = 0; i < allys.size(); i++){
			if(characterHandler.enclosed(allys.get(i), enemys)){
//				System.out.println("quan bi vay");
				allys.remove(allys.get(i));
			}
		}
	}
	
	private boolean gameOver(ArrayList<Character> allys, ArrayList<Character> enemys){
		if(kingDead(allys, enemys)){
//			System.out.println("king dead *******************************");
			return true;
		}
		if(kingIsEnclosed(allys, enemys)){
//			System.out.println("*************king enclosed");
			return true;
		}
		if(charactersIsEnclosed(allys, enemys)) return true;
		if(charactersIsEmpty(allys, enemys)) return true;
		return false;
	}
	
	private boolean kingDead(ArrayList<Character> allys, ArrayList<Character> enemys){
		if(characterHandler.kingDead(allys))
			return true;
		if(characterHandler.kingDead(enemys))
			return true;
		return false;
	}
	
	private boolean charactersIsEmpty(ArrayList<Character> allys, ArrayList<Character> enemys){
		if(allys.size() <= 1) 
			return true;
		if(enemys.size() <= 1) 
			return true;
		return false;
	}
	
	private boolean kingIsEnclosed(ArrayList<Character> allys, ArrayList<Character> enemys){
		for(Character c : allys){
			if(c.isKing()){
				if(characterHandler.enclosed(c, enemys))
					return true;
			}
		}
		for(Character c : enemys){
			if(c.isKing()){
				if(characterHandler.enclosed(c, allys))
					return true;
			}
		}
		return false;
	}
	
	private ArrayList<Character> copyFrom(ArrayList<Character> list){
		ArrayList<Character> result = new ArrayList<>();
		if(list == null || list.isEmpty()) return result;
		for(Character item : list){
			Character c = new Character(item.getCoordinate(), item.getWidth(), item.isAlive(), item.isSelected(), item.isKing());
			result.add(c);
		}
		return result;
	}
	
	private boolean charactersIsEnclosed(ArrayList<Character> allys, ArrayList<Character> enemys){
		if(characterHandler.HetNuocDi(allys, enemys))
			return true;
		if(characterHandler.HetNuocDi(enemys, allys))
			return true;
		return false;
	}

















	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
