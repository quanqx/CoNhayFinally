package object;

public class CoordinateAndScores {
	
	private int scores;
	private Coordinate coordinate;
	
	
	public CoordinateAndScores(int scores, Coordinate coordinate){
		this.scores = scores;
		this.coordinate = coordinate;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	public int getScores() {
		return scores;
	}
	
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	public void setScores(int scores) {
		this.scores = scores;
	}
}
