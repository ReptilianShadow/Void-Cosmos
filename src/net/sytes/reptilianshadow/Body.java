package net.sytes.reptilianshadow;


import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotated;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;

import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;


public class Body{
	
	/*
	 * All masses are measured in solar masses
	 * 
	 * All distances are measured in astronomical units (au)
	 * 
	 * 
	 */
	
	
	private Point3D position, velocity, acceleration;
	private Rotation rotation;

	private double mass; //measured in solar masses
	
	private float radius; 
	
	public Body(){
		this(1, new Point3D(0, 0, 0), new Point3D(0, 0, 0), new Rotation(0, 0, 0));	
	}

	public Body(double mass, Point3D position, Point3D velocity, Rotation rotation){
		this.mass = mass;
		this.setPosition(position);
		this.setVelocity(velocity);
		this.setRotation(rotation);
		
		radius = 1.0f;
	}

	public void update(){

		position = position.add(velocity);

		velocity = velocity.add(acceleration);

	}




	//generate gravitational forces between planets
	//this should decrease calculations, since we won't be 
	//redundant with "WHat is force of planet a on planet b" and with a and b switched
	public static void genNewAccelerations(ArrayList<Body> bodiesInSystem){

		for (int i = 0; i < bodiesInSystem.size(); i++){
			//reset accleration to zero so we can just add to it
			bodiesInSystem.get(i).setAcceleration(new Point3D(0, 0, 0));
		}

		//TODO check this more with making sure it's amassing accelerations
		//the correct amount of times
		for (int first = 0; first < bodiesInSystem.size(); first++){

			for (int second = first; second < bodiesInSystem.size(); second++){
				if (bodiesInSystem.get(first) != bodiesInSystem.get(second)){
					//F = (Gm1m2)/r^2

					double distTotal = bodiesInSystem.get(first).getPosition().distanceFrom( bodiesInSystem.get(second).getPosition() );

					float calcForce = (float) (
							(
									Game.game.gConstant *
									bodiesInSystem.get(first).mass *
									bodiesInSystem.get(second).mass 
									) / Math.pow(distTotal, 2)
							);

					//necessary temp variables used in first/second body accel calcs
					float tempAccelTotal, tempAccelX, tempAccelY, tempAccelZ;
					double tempDistX, tempDistY, tempDistZ;
					//do stuff for first body
					tempAccelTotal = (float) (calcForce/bodiesInSystem.get(first).mass);

					tempDistX = bodiesInSystem.get(first).position.x - bodiesInSystem.get(second).position.x;
					tempAccelX = (float) ((tempDistX / distTotal) * tempAccelTotal);
					tempAccelX *= -1;

					tempDistY = bodiesInSystem.get(first).position.y - bodiesInSystem.get(second).position.y;
					tempAccelY = (float) ((tempDistY / distTotal) * tempAccelTotal);
					tempAccelY *= -1;

					tempDistZ = bodiesInSystem.get(first).position.z - bodiesInSystem.get(second).position.z;
					tempAccelZ = (float) ((tempDistZ / distTotal) * tempAccelTotal);
					tempAccelZ *= -1;

					//ACCELERATIONS CALCULATIONS SHOULD ACCUMULATE
					bodiesInSystem.get(first).setAcceleration(bodiesInSystem.get(first).acceleration.add(new Point3D(tempAccelX, tempAccelY, tempAccelZ)));


					//do stuff for second body (should decrease calculations due to less looping)
					//and less mass with force calculations I think
					tempAccelTotal = (float) (calcForce/bodiesInSystem.get(second).mass);

					tempDistX = bodiesInSystem.get(second).position.x - bodiesInSystem.get(first).position.x;
					tempAccelX = (float) ((tempDistX / distTotal) * tempAccelTotal);
					tempAccelX *= -1;

					tempDistY = bodiesInSystem.get(second).position.y - bodiesInSystem.get(first).position.y;
					tempAccelY = (float) ((tempDistY / distTotal) * tempAccelTotal);
					tempAccelY *= -1;

					tempDistZ = bodiesInSystem.get(second).position.z - bodiesInSystem.get(first).position.z;
					tempAccelZ = (float) ((tempDistZ / distTotal) * tempAccelTotal);
					tempAccelZ *= -1;

					//ACCELERATIONS CALCULATIONS SHOULD ACCUMULATE
					bodiesInSystem.get(second).setAcceleration(bodiesInSystem.get(second).acceleration.add(new Point3D(tempAccelX, tempAccelY, tempAccelZ)));	

				}
			}

		}


	}

	public static void updateBodies(ArrayList<Body> bodiesInSystem){
		for (int i = 0; i < bodiesInSystem.size(); i++){
			bodiesInSystem.get(i).update();
		}
	}

	public static void doCollisionMethod(){
		/* 
		 * 
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 *** Three different methods of Collision between bodies ***
		 *
		 *	- Swallow and grow 
		 * 		- Just combine the two bodies into one large object (reevaluating the size given the combined masses and the body type)
		 * 	
		 * 	- Bounce
		 * 		- Bodies bounce off of each other
		 * 
		 * 	- Shatter
		 * 		- Break the bodies in some collision defined amount 
		 * 
		 * 
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 * Constants throughout methods:
		 *	- Stars/Black holes just engulf things and grow (and do other star physics)
		 *	- Stars/Black holes never really break apart and never bounce off of things
		 * 
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 */
		
		
		
		


	}


	public void render(){

		//draw platform cylinder
		glColor3f(1,1,1);
		glPushMatrix();
		glTranslatef(position.x, position.y, position.z);
		glRotated(rotation.pitch, 1, 0, 0);
		glRotated(rotation.yaw, 0, 1, 0);
		glRotated(rotation.roll, 0, 0, 1);

		Sphere patCyl = new Sphere();
		patCyl.setDrawStyle(GLU.GLU_LINE);
		patCyl.draw(radius, 10, 10); //basically sets the radius, and number of rows/columns of
		//vertices that make up the circle
		glPopMatrix(); //remove any translations made

	}

	public Point3D getPosition() {
		return position;
	}

	public void setPosition(Point3D position) {
		this.position = new Point3D(position.x, position.y, position.z);
	}

	public Point3D getVelocity() {
		return velocity;
	}

	public void setVelocity(Point3D velocity) {
		this.velocity = new Point3D(velocity.x, velocity.y, velocity.z);
	}

	public Point3D getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Point3D acceleration) {
		this.acceleration = new Point3D(acceleration.x, acceleration.y, acceleration.z);
	}

	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(Rotation rotation) {
		this.rotation = new Rotation(rotation.pitch, rotation.yaw, rotation.roll);
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}




}

