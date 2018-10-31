package handler;

import java.util.ArrayList;

import Common.Constant;
import helper.Helper;
import object.Character;
import object.Coordinate;
import object.GameBoard;
import object.Hint;

public class CharacterHandler {

	public final static String ALLY = "ALLY";
	public final static String ENEMY = "ENEMY";

	private HintHandler hintHandler;

	public CharacterHandler(){
		hintHandler = new HintHandler(Constant.WIDTH_HINT);
	}
	
	public static ArrayList<Character> genCharacter(String type, int widthCharacter){
		ArrayList<Character> result = new ArrayList<>();
		
		for(int i = 0; i < 5; i++){
			if(type == ALLY && i < 3) continue;
			if(type == ENEMY && i > 1) break;
			for (int j = 0; j < 5; j++) {
				if((i == 1 || i == 3) && j == 2) continue;
				Coordinate coordinate = new Coordinate(j, i);
				Character character = new Character(coordinate, widthCharacter, true, false, false);
				if((i == 0 || i == 4) & j == 2) character.setKing(true);
				result.add(character);
			}
		}
		
		return result;
	}
	
	public static boolean characterSelected(Character c, Coordinate mouseCoordinate, GameBoard gameBoard){
		Coordinate c_oxy = CoordinateHandler.getCoordinateOxy(c.getCoordinate() , gameBoard.getStartX(), gameBoard.getStartY(), gameBoard.getWidth());
		int distance = Helper.distance(c_oxy, mouseCoordinate);
		if(distance <= c.getWidth()/2){
			c.setSelected(true);
			return true;
		}
		return false;
	}
	
	public Character getCharacterByCoordinate(Coordinate coordinate, ArrayList<Character> characters){// != king
		Character result = null;
		for(int i = 0; i<characters.size(); i++){
			if(characters.get(i).getCoordinate().equal(coordinate)){
				result = characters.get(i);
				break;
			}
		}
		return result;
	}
	
	public boolean enclosed(Character c, ArrayList<Character> characters){
		int count = 0;
		ArrayList<Coordinate> movable = HintHandler.getMovable(c);
		for(Coordinate coordinate : movable){
			for(Character item : characters){
				if(item.getCoordinate().equal(coordinate)){
					count ++;
					break;
				}
			}
		}
		if(count == movable.size()) return true;
		return false;
	}
	
	public boolean HetNuocDi(ArrayList<Character> allys, ArrayList<Character> enemys){
		for(Character ally : allys){
			ArrayList<Hint> movable = new ArrayList<>();
			if(ally.isKing()){
				movable = hintHandler.getHintsForKing(ally, allys, enemys);
			}
			else{
				movable = hintHandler.getHints(ally, allys, enemys);
			}
			if(!movable.isEmpty())
				return false;
		}
		return true;
	}
	
	public boolean kingDead(ArrayList<Character> characters){
		for(Character c : characters){
			if(c.isKing())
				return false;
		}
		return true;
	}
	
}
