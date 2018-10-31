package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import handler.CharacterHandler;
import handler.CoordinateHandler;
import handler.Estimate;
import handler.GameBoardHandler;
import handler.HintHandler;
import helper.Helper;
import object.BestMove;
import object.Character;
import object.Coordinate;
import object.CoordinateAndScores;
import object.GameBoard;
import object.Hint;

public class GUI extends JFrame implements MouseListener, KeyListener{
	
	private final static int startX = 150;
	private final static int startY = 100;
	private final static int widthGameBoard = 500;
	private final static int wCharacter = 50;
	private final static int wHint = 40;
	
	public final static boolean ALLY = true;
	private boolean turn;
	private Character cSelected;
	private Hint lastMove;
	private Hint currentMove;
	
	private Stack<Character> undoCharacter;
	private Stack<Coordinate> undoCoordinate;
	
	private Stack<Character> redoCharacter;
	private Stack<Coordinate> redoCoordinate;
	
	private GameBoard gameBoard;
	private GameBoardHandler gameBoardHandler;
	private HintHandler hintHandler;
	
	public GUI(){
		
		initProperties();
		this.addMouseListener(this);
		this.addKeyListener(this);
		initComps();
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
	
	private void initProperties(){
		this.setTitle("Cá»� nháº£y");
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
		undoCharacter = new Stack<>();
		redoCharacter = new Stack<>();
		undoCoordinate = new Stack<>();
		redoCoordinate = new Stack<>();
	}
	
	private void initGameBoard(){
		gameBoard = new GameBoard(startX, startY, widthGameBoard, wCharacter);
		gameBoardHandler = new GameBoardHandler(gameBoard, wCharacter);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(move(e)) return;
		if(turn == ALLY){
			System.out.println("mouse click! -- turn == ALLY");
			selectCharacter(gameBoard.getAllys(), e);
		}
		
	}
	
	private void selectCharacter(ArrayList<Character> characters, MouseEvent e){
		Coordinate mouseCoordinate = new Coordinate(e.getX(), e.getY());
		if(cSelected != null){
			Coordinate cSelected_oxy = CoordinateHandler.getCoordinateOxy(cSelected.getCoordinate(), startX, startY, widthGameBoard);
			if(Helper.distance(cSelected_oxy, mouseCoordinate) <= wCharacter/2) return;
		}
		for(Character c : characters){
			boolean selected = CharacterHandler.characterSelected(c, mouseCoordinate, gameBoard);
			if(selected){
				if(cSelected != null) 
					cSelected.setSelected(false);
				cSelected = c;
//				if(c.isKing()) 
//					gameBoard.setHints(hintHandler.getHintsForKing(c, gameBoard, turn));
//				else
//					gameBoard.setHints(hintHandler.getHints(c, gameBoard));
				repaint();
				break;
			}
		}
	}

	CharacterHandler characterHandler = new CharacterHandler();
	private boolean move(MouseEvent e){
		if(gameBoard.getHints().size() == 0) 
			return false;
		for(Hint h : gameBoard.getHints()){
			Coordinate h_oxy = CoordinateHandler.getCoordinateOxy(h.getCoordinate(), startX, startY, widthGameBoard);
			int distance = Helper.distance(h_oxy, new Coordinate(e.getX(), e.getY()));
			if(distance <= wCharacter){
				if(cSelected.isKing()){
					Character c = null;
					
					if(turn == ALLY){
						c = characterHandler.getCharacterByCoordinate(h.getCoordinate(), gameBoard.getEnemys());
						if(c != null){
							if(c.isKing()){
								JOptionPane.showMessageDialog(this, "Ä�á»� tháº¯ng!");
								reset();
								return true;
							}
							gameBoard.getEnemys().remove(c);
						}
					}
					else{
						c = characterHandler.getCharacterByCoordinate(h.getCoordinate(), gameBoard.getAllys());
						if(c != null){
							if(c.isKing()){
								JOptionPane.showMessageDialog(this, "Xanh tháº¯ng!");
								reset();
								return true;
							}
							gameBoard.getAllys().remove(c);
						}
					}
				}
				if(gameOver()) 
					return true;
				undoCoordinate.push(cSelected.getCoordinate());
				cSelected.setCoordinate(h.getCoordinate());
				undoCharacter.push(cSelected);
				cSelected.setSelected(false);
				cSelected = null;
				gameBoard.setHints(new ArrayList<>());
				turn = !turn;
				killCharacterEnclosed();
				repaint();
				hetNuocDi();

				computerMove();
				turn =! turn;
//				hetNuocDi();
				return true;
			}
		}
		return false;
	}
	
	private void killCharacterEnclosed(){
		boolean enclosed = false;
		for(int i = 0; i < gameBoard.getAllys().size(); i++){
			if(characterHandler.enclosed(gameBoard.getAllys().get(i), gameBoard.getEnemys())){
				if(gameBoard.getAllys().get(i).isKing()){
					JOptionPane.showMessageDialog(this, "Xanh tháº¯ng!");
					reset();
					return;
				}
				gameBoard.getAllys().remove(gameBoard.getAllys().get(i));
				enclosed = true;
			}
		}
		for(int i = 0; i < gameBoard.getEnemys().size(); i++){
			if(characterHandler.enclosed(gameBoard.getEnemys().get(i), gameBoard.getAllys())){
				if(gameBoard.getEnemys().get(i).isKing()){
					JOptionPane.showMessageDialog(this, "Ä�á»� tháº¯ng!");
					reset();
					return;
				}
				gameBoard.getEnemys().remove(gameBoard.getEnemys().get(i));
				enclosed = true;
			}
		}
		if(enclosed) repaint();
	}
	
	private boolean gameOver(){
		if(gameBoardHandler.countCharacterDead(gameBoard.getAllys()) == gameBoard.getAllys().size()-1){
			JOptionPane.showMessageDialog(this, "Xanh tháº¯ng!");
			reset();
			return true;
		}
		
		if(gameBoardHandler.countCharacterDead(gameBoard.getEnemys()) == gameBoard.getEnemys().size()-1){
			JOptionPane.showMessageDialog(this, "Ä�á»� tháº¯ng!");
			reset();
			return true;
		}
		return false;
	}
	
	private void reset(){
		initComps();
		cSelected = null;
		repaint();
	}
	
	private void computerMove(){
		
		ArrayList<BestMove> bestMoves = new ArrayList<>();
		for(Character enemy : gameBoard.getEnemys()){
			Coordinate curcdn = enemy.getCoordinate();
			int bestScore = 0;
			Coordinate bestCoordinate = curcdn;
			ArrayList<Hint> hints = new ArrayList<>();
//			if(enemy.isKing()){
//				hints = hintHandler.getHintsForKing(enemy, gameBoard, turn);
//			}
//			else
//				hints = hintHandler.getHints(enemy, gameBoard);
			if(!hints.isEmpty()){
				for(Hint h : hints){
					int curScore = 0;
					curScore += dangBiDeDoa(curcdn);
					enemy.setCoordinate(h.getCoordinate());
					curScore += estimateSub(curcdn, enemy, turn);
					curScore += estimatePlus(enemy, turn);
					if(bestScore < curScore){
						bestScore = curScore;
						bestCoordinate = h.getCoordinate();
					}
				}
				bestMoves.add(new BestMove(bestScore, bestCoordinate, enemy));
				enemy.setCoordinate(curcdn);
			}
			
		}
		bestMove(bestMoves);
	}
	
	private int dangBiDeDoa(Coordinate lastCdn){

		Estimate est = new Estimate(hintHandler);
		int scores = 0;
		
		if(turn == ALLY){ //enemy vua danh
			scores += est.dangBiDeDoa(lastCdn, gameBoard.getEnemys(), gameBoard.getAllys());
		}
		else{
			scores += est.dangBiDeDoa(lastCdn, gameBoard.getAllys(), gameBoard.getEnemys());
		}
		return scores;
	}
	
	private int estimatePlus(Character c, boolean turn){
		int scores = 0;
		
		Estimate est = new Estimate(hintHandler);
		
		scores += est.hoaChanh(c);
		
		if(turn == ALLY){ //enemy vua danh
			scores += est.deDoaQuanDich(c, gameBoard.getAllys(), gameBoard.getEnemys());
		}
		else{
			scores += est.deDoaQuanDich(c, gameBoard.getEnemys(), gameBoard.getAllys());
		}
		
		if(c.isKing()){
			if(turn == ALLY){ //enemy vua danh
				scores += est.tuongDoaQuanDich(c, gameBoard.getAllys(), gameBoard.getEnemys());
			}
			else{
				scores += est.tuongDoaQuanDich(c, gameBoard.getEnemys(), gameBoard.getAllys());
			}
		}
		
		if(turn == ALLY){ //enemy vua danh
			scores += est.nuocAnQuan(c, gameBoard.getEnemys());
		}
		else{
			scores += est.nuocAnQuan(c, gameBoard.getAllys());
		}
		
		if(turn == ALLY){ //enemy vua danh
			scores += est.vayQuan(c, gameBoard.getAllys(), gameBoard.getEnemys());
		}
		else{
			scores += est.vayQuan(c, gameBoard.getEnemys(), gameBoard.getAllys());
		}
		
		return scores;
	}
	
	private int estimateSub(Coordinate lastCoordinate, Character character, boolean turn){
		
		int scores = 0;
		
		Estimate est = new Estimate(hintHandler);
		
		scores += est.raKhoiHoaChanh(lastCoordinate, character);
		
		if(turn == ALLY){
			scores += est.coTheBiAn(character.getCoordinate(), gameBoard.getEnemys(), gameBoard.getAllys());
		}
		else{
			scores += est.coTheBiAn(character.getCoordinate(), gameBoard.getAllys(), gameBoard.getEnemys());
		}
		
		return scores;
		
	}
	
	private void bestMove(ArrayList<BestMove> bestMoves){
		BestMove b = bestMoves.get(0);
		for(BestMove best : bestMoves){
			if(b.getScores() <= best.getScores()){
				b = best;
			}
		}
		if(b.getCharacter().isKing()){
			Character c = characterHandler.getCharacterByCoordinate(b.getCoordinate(), gameBoard.getAllys());
			if(c != null){
				if(c.isKing()){
					JOptionPane.showMessageDialog(this, "MÃ¡y tháº¯ng!");
					reset();
					return;
				}
				gameBoard.getAllys().remove(c);
			}
		}
		lastMove = new Hint(b.getCharacter().getCoordinate(), 20);
		currentMove = new Hint(b.getCoordinate(), 20);
		b.getCharacter().setCoordinate(b.getCoordinate());
		killCharacterEnclosed();
		System.out.println("Computer moveeee");
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			undo();
        }
		if ((e.getKeyCode() == KeyEvent.VK_T) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			redo();
        }
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void undo(){
		if(undoCharacter.isEmpty() || undoCoordinate.isEmpty()) 
			return;
		Character uCharacter = undoCharacter.pop();
		Coordinate uCoordinate = undoCoordinate.pop();
		redoCharacter.push(uCharacter);
		redoCoordinate.push(uCharacter.getCoordinate());
		uCharacter.setCoordinate(uCoordinate);
		repaint();
        turn = !turn;
	}
	
	private void redo(){
		if(redoCharacter.isEmpty() || redoCoordinate.isEmpty())
			return;
		Character rCharacter = redoCharacter.pop();
		Coordinate rCoordinate = redoCoordinate.pop();
		undoCharacter.push(rCharacter);
		undoCoordinate.push(rCharacter.getCoordinate());
		rCharacter.setCoordinate(rCoordinate);
		repaint();
		turn = !turn;
	}
	
	private void hetNuocDi(){
//		if(turn == ALLY){
//			if(CharacterHandler.HetNuocDi(gameBoard.getAllys(), hintHandler, gameBoard, turn)){
//				JOptionPane.showMessageDialog(this, "MÃ¡y tháº¯ng!");
//				reset();
//				return;
//			}
//		}
//		else{
//			if(CharacterHandler.HetNuocDi(gameBoard.getEnemys(), hintHandler, gameBoard, turn)){
//				JOptionPane.showMessageDialog(this, "Báº¡n tháº¯ng!");
//				reset();
//				return;
//			}
//		}
	}
	
}
