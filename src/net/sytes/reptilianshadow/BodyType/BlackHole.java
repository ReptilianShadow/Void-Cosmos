package net.sytes.reptilianshadow.BodyType;

import net.sytes.reptilianshadow.Body;
import net.sytes.reptilianshadow.Point3D;
import net.sytes.reptilianshadow.Rotation;

public class BlackHole extends Body{
	
	//Mass
	//Super Massive Black Hole = ~1.98855e35 kg to ~1.98855e40 kg
	//Super massive black holes are presumably at the
	//center of most or all galaxies
	//
	//Intermediate-Mass Black Hole = ~1.98855e33 kg
	//
	//
	//Stellar Black Hole = ~1.98855e1 kg
	//Formed from the collapse of a large star
	
	
	public BlackHole(double mass, Point3D position, Point3D velocity, Rotation rotation){
		super(mass, position, velocity, rotation);
	}
	
	
	
	
}
