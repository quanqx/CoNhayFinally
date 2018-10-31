package handler;

import java.util.ArrayList;

import gui.GUI;
import object.Character;
import object.Coordinate;
import object.GameBoard;
import object.Hint;

public class HintHandler {
	
	private int wHint;
	
	public HintHandler(int wHint){
		this.wHint = wHint;
	}
	
	public static ArrayList<Coordinate> getMovable(Character character){
		ArrayList<Coordinate> result = new ArrayList<>();
		int x = character.getCoordinate().getX();
		int y = character.getCoordinate().getY();
		if((x+y)%2 == 0){
			if(x-1 >= 0 && y-1 >= 0)
				result.add(new Coordinate(x-1, y-1));
			if(x>=0 && y-1 >=0)
				result.add(new Coordinate(x, y-1));
			if(x+1 <= 4 && y-1 >= 0)
				result.add(new Coordinate(x+1, y-1));
			if(x+1 <= 4 && y >= 0)
				result.add(new Coordinate(x+1, y));
			if(x+1 <= 4 && y+1 <= 4)
				result.add(new Coordinate(x+1, y+1));
			if(x >= 0 && y+1 <= 4)
				result.add(new Coordinate(x, y+1));
			if(x-1 >= 0 && y+1 <= 4)
				result.add(new Coordinate(x-1, y+1));
			if(x-1 >= 0 && y >= 0)
				result.add(new Coordinate(x-1, y));
		}
		else{
			if(x>=0&&y-1>=0)
				result.add(new Coordinate(x, y-1));
			if(x+1<=4 && y>=0)
				result.add(new Coordinate(x+1, y));
			if(x-1>=0  && y>=0)
				result.add(new Coordinate(x-1,y));
			if(x>=0 && y+1<=4)
				result.add(new Coordinate(x, y+1));
		}
		
		return result;
	}
	
	public ArrayList<Hint> getHints(Character c, ArrayList<Character> allys, ArrayList<Character> enemys){
		ArrayList<Coordinate> movable = getMovable(c);
		ArrayList<Hint> hints = new ArrayList<>();
		if(movable.size() == 0) return hints;
		for(Coordinate cdn : movable){
			int count = 0;
			for(Character item : allys){
				if((item.getCoordinate().getX() == cdn.getX() && item.getCoordinate().getY() == cdn.getY())){
					count ++;
					break;
				}
			}
			for(Character item : enemys){
				if((item.getCoordinate().getX() == cdn.getX() && item.getCoordinate().getY() == cdn.getY())){
					count ++;
					break;
				}
			}
			if(count == 0){
				Hint hint = new Hint(cdn, wHint);
				hints.add(hint);
			}
		}
		return hints;
	}
	
	public ArrayList<Coordinate> getKingEatable(Character king, ArrayList<Character> enemys, ArrayList<Character> allys){
		ArrayList<Coordinate> result = new ArrayList<>();
		
		int x = king.getCoordinate().getX();
		int y = king.getCoordinate().getY();
		
		ArrayList<Coordinate> movable = getMovable(king);
		for(Coordinate c : movable){
			for(Character ally : allys){
				if(c.getX() == ally.getCoordinate().getX() && c.getY() == ally.getCoordinate().getY()){
					Coordinate eatable = new Coordinate((c.getX()-x)*2 + x, (c.getY()-y)*2 + y);
					for(Character enemy : enemys){
						if(eatable.getX() == enemy.getCoordinate().getX() && eatable.getY() == enemy.getCoordinate().getY()){
							result.add(eatable);
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public ArrayList<Hint> getHintsForKing(Character kingAlly, ArrayList<Character> allys, ArrayList<Character> enemys){
		ArrayList<Hint> hints = new ArrayList<>();
		ArrayList<Coordinate> kingEatable = getKingEatable(kingAlly, enemys, allys);
		for(Coordinate c : kingEatable){
			Hint h = new Hint(c, wHint);
			hints.add(h);
		}
		hints.addAll(getHints(kingAlly, allys, enemys));
		return hints;
	}
	
}
