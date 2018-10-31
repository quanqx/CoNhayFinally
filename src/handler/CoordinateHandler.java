package handler;

import object.Coordinate;

public class CoordinateHandler {

	public static Coordinate getCoordinateOxy(Coordinate coordinate, int startX, int startY, int widthGameBoard){
		Coordinate cdn = new Coordinate(startX + coordinate.getX()*widthGameBoard/4, startY + coordinate.getY()*widthGameBoard/4);
		return cdn;
	}
	
}
