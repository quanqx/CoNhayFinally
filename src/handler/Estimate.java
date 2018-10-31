package handler;

import java.util.ArrayList;

import object.Coordinate;
import object.Character;

public class Estimate {

	private HintHandler hintHandler;
	
	public Estimate(HintHandler hintHandler){
		this.hintHandler = hintHandler;
	}
	
	//xet 5 diem hoa chanh
	public int hoaChanh(Character character){
		ArrayList<Coordinate> movable = HintHandler.getMovable(character);
		if(movable.size() == 8){
			if(character.getCoordinate().getX() == 2 && character.getCoordinate().getY() == 2){
				System.out.println("hoa chanh chinh");
				return 10;
			}
			else{
				System.out.println("Hoa chanh phu");
				return 5;
			}
		}
		return 0;
	}
	
	// quan dong minh vua di vao giua tuong dong minh va quan dich
	public int deDoaQuanDich(Character character, ArrayList<Character> allys, ArrayList<Character> enemys){ 
		ArrayList<Coordinate> movable = HintHandler.getMovable(character);
		//xet xem quanh quan vua di co tuong khong.
		for(Coordinate cdn : movable){
			for(Character ally : allys){
				if(ally.isKing()){//neu la tuong
					if(ally.getCoordinate().getX() == cdn.getX() && ally.getCoordinate().getY() == cdn.getY()){//va nam canh quan vua di
						for(Character enemy : enemys){
							if(ally.getCoordinate().getX() - character.getCoordinate().getX() == character.getCoordinate().getX() - enemy.getCoordinate().getX()
									&& ally.getCoordinate().getY() - character.getCoordinate().getY() == character.getCoordinate().getY() - enemy.getCoordinate().getY()){
								System.out.println("quan dong minh di vao giua tuong va quan dich");
								return 10;
							}
						}
					}
				}
			}
		}
		return 0;
	}
	
	//neu la tuong va co the an quan dich o nuoc di tiep theo
	public int tuongDoaQuanDich(Character king, ArrayList<Character> enemys, ArrayList<Character> allys){
		ArrayList<Coordinate> kingEatable = hintHandler.getKingEatable(king, enemys, allys);
		if(kingEatable.isEmpty())
			return 0;
		else{
			System.out.println("king eatable " + kingEatable.size());
			return kingEatable.size()*11;
		}
			
	}
	
	//kiem tra xem day co phai la nuoc tuong an quan khong
	public int nuocAnQuan(Character king, ArrayList<Character> enemys){
		for(Character enemy : enemys){
			if(king.getCoordinate().equal(enemy.getCoordinate())){
				if(enemy.isKing()){
					System.out.println("Nuoc an tuong");
					return Integer.MAX_VALUE;
				}
				else{
					System.out.println("nuoc an quan");
					return 20;
				}
			}
		}
		return 0;
	}
	CharacterHandler characterHandler = new CharacterHandler();
	//quan dich bi bao vay
	public int vayQuan(Character character, ArrayList<Character> enemys, ArrayList<Character> allys){
		int scores = 0;
		for(Character enemy : allys){
			if(characterHandler.enclosed(enemy, enemys)){
				scores += 20;
				System.out.println("vay quan " + scores);
			}
		}
		return scores;
	}
	
	public int raKhoiHoaChanh(Coordinate lastCoordinate, Character character){
		int diem = 0;
		ArrayList<Coordinate> movable = HintHandler.getMovable(character);
		
		if(movable.size() == 8){
			ArrayList<Coordinate> movesOfCharacter = HintHandler.getMovable(character);
			if(lastCoordinate.getX() == 2 && lastCoordinate.getY() == 2){
				if(movesOfCharacter.size() == 8){
					diem -= 8;
				}
				if(movesOfCharacter.size() < 8){
					diem -= 10;
				}
			}
			else{
				if(movesOfCharacter.size() == 8){
					diem += 10;
				}
				if(movesOfCharacter.size() < 8){
					diem -= 10;
				}
			}
		}
		
		return diem;
	}
	
	//nuoc di do co the bi tuong dich an
	public int coTheBiAn(Coordinate coordinate, ArrayList<Character> enemys, ArrayList<Character> allys){
		int diem = 0;
		for (int i = 0; i < enemys.size(); i++) {
			if(enemys.get(i).isKing()){
				ArrayList<Coordinate> eatable = hintHandler.getKingEatable(enemys.get(i), allys, enemys);
				for(Coordinate cdn : eatable){
					if(cdn.getX() == coordinate.getX() && cdn.getY() == coordinate.getY()){
						System.out.println("co the bi an");
						diem -= 20;
					}
				}
			}
		}
		return diem;
	}
	
	//quan dong minh dang bi de doa
	public int dangBiDeDoa(Coordinate lastCoordinate, ArrayList<Character> enemys, ArrayList<Character> allys){
		int diem = 0;
//		System.out.println("last coordinate: " + lastCoordinate.getX() +" : "+ lastCoordinate.getY());
//		System.out.println("enemys, allys: " + coTheBiAn(lastCoordinate, enemys, allys));
//		System.out.println("allys, enemys: " + coTheBiAn(lastCoordinate, allys, enemys));
		if(coTheBiAn(lastCoordinate,enemys, allys) < 0){ // = -50
			diem += 20; // sau khi di khoi vi tri lastCoordinate thi dc cong 20
			System.out.println("dang bi de doa");
		}

		return diem;
	}
	
}
