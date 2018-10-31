package object;

public class Character {
	
	private Coordinate coordinate;
	private int width;
	private boolean alive;
	private boolean selected;
	private boolean king;
	
	public Character() {
		super();
	}
	
	public Character(Coordinate coordinate, int width, boolean alive, boolean selected, boolean king) {
		super();
		this.coordinate = coordinate;
		this.width = width;
		this.alive = alive;
		this.selected = selected;
		this.king = king;
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
	
	public boolean isAlive() {
		return alive;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isKing(){
		return king;
	}
	
	public void setKing(boolean king){
		this.king = king;
	}
	
}
