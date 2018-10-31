package object;

public class Hint {
	private Coordinate coordinate;
	private int width;
	
	public Hint(){}
	
	public Hint(Coordinate coordinate, int width) {
		super();
		this.coordinate = coordinate;
		this.width = width;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
}
