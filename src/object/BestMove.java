package object;

public class BestMove {
	
	private int scores;
	private Coordinate coordinate;
	private Character character;
	
	public BestMove(int scores, Coordinate coordinate, Character character){
		this.scores = scores;
		this.coordinate = coordinate;
		this.character = character;
	}
	
	public Character getCharacter() {
		return character;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	public int getScores() {
		return scores;
	}
	
	public void setCharacter(Character character) {
		this.character = character;
	}
	
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	public void setScores(int scores) {
		this.scores = scores;
	}
	
}
