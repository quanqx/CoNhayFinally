package helper;

import object.Coordinate;

public class Helper {
	
	public static int distance(Coordinate c1, Coordinate c2){
		int c = (int)Math.sqrt(Math.pow(c1.getX() - c2.getX(), 2) + Math.pow(c1.getY() - c2.getY(), 2));
		return c;
	}
	
}
