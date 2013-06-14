package net.sytes.reptilianshadow;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;

public class Camera {

	private Point3D position;

	private Rotation rotation;

	private boolean movable = true;
	
	private float speed = 0.1f;

	private float yFOV;
	private float aspectRatio;
	private float zNear;
	private float zFar;


	/**
	 * 
	 * @param position {@link Point3D} representing the camera's position.
	 * @param rotation {@link Rotation} representing the rotation of the camera.
	 * @param yFOV The field of view angle, in degrees, in the y-direction. (copied from OpenGL docs)
	 * @param aspRatio The aspect ratio that determines the field of view in the x-direction. The aspect ratio is the ratio of x (width) to y (height). (copied from OpenGL docs)
	 * @param zNear The distance from the viewer to the near clipping plane (always positive). (copied from OpenGL docs)
	 * @param zFar The distance from the viewer to the far clipping plane (always positive). (copied from OpenGL docs)
	 */
	public Camera(Point3D position, Rotation rotation, float yFOV, float aspRatio, float zNear, float zFar){

		this.position = new Point3D(position.x, position.y, position.z);

		this.rotation = new Rotation(rotation.pitch, rotation.yaw, rotation.roll);

		this.yFOV = yFOV;
		this.aspectRatio = aspRatio;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	/**
	 * Initializes the perspective. Essentially just sets up field of view, aspect ratio,
	 * and render distance.
	 */
	public void initPerspective(){

		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(yFOV, aspectRatio, zNear, zFar);
		glPopAttrib();


	}

	/**
	 * Translates the objects to account for camera position and rotation. This is because this code
	 * moves the camera in terms of the world, but OpenGL needs to have world coordinates relative to the camera.
	 */
	public void doTranslations(){

		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_MODELVIEW);
		glRotatef(rotation.pitch, 1, 0, 0);
		glRotatef(rotation.yaw, 0, 1, 0);
		glRotatef(rotation.roll, 0, 0, 1);
		glTranslatef(position.x, position.y, position.z); //x y z of camera		
		glPopAttrib();

	}

	/**
	 * Handles keyboard movement. Controls are standard awsd/arrow keys with space bar to ascend and shift to descend.
	 */
	public void doKeyboardControl(){

		if (this.isMovable()){ //can the camera move around, or should it just look?

			boolean left = Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT);
			boolean right = Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT);

			boolean ascend = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
			boolean descend = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

			boolean forward = Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP);
			boolean backward = Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN);

			position.y += (ascend) ? -speed : (descend) ? speed: 0;

			if (forward){
				position.x += ( -speed *  degSin(getRotation().yaw));
				position.z += (speed * degCos(getRotation().yaw));
			}
			else if (backward){
				position.x -= ( -speed *  degSin(getRotation().yaw));
				position.z -= (speed * degCos(getRotation().yaw));
			}
			if (left){
				position.x += ( -speed *  degSin(getRotation().yaw - 90));
				position.z += (speed * degCos(getRotation().yaw - 90));
			}
			else if  (right){
				position.x += ( -speed *  degSin(getRotation().yaw + 90));
				position.z += (speed * degCos(getRotation().yaw + 90));
			}		
		}
	}

	/**
	 * Handles changing pitch, yaw, and roll based on mouse control. Move the mouse to affect pitch and yaw, and use
	 * the mousewheel to change roll.
	 */
	public void doMouseControl(){

		if (Mouse.isGrabbed()){

			//handle camera looking around
			float dX = Mouse.getDX() * 0.1f;
			float dY = Mouse.getDY() * 0.1f;

			//pitch
			if (rotation.pitch - dY >= -90 && rotation.pitch - dY <= 90) {
				rotation.pitch += -dY;
			}

			rotation.yaw = (rotation.yaw + dX) % 360;

			rotation.roll = (rotation.roll + Mouse.getDWheel() / 120.0f) % 360;

		}
	}

	/**
	 * 
	 * @return The {@link Point3D} position of the camera.
	 */
	public Point3D getPosition(){
		return position;
	}

	/**
	 * 
	 * @param position The new {@link Point3D} position of the camera.
	 */
	public void setPositon(Point3D position){
		this.position = new Point3D(position.x, position.y, position.z);
	}

	/**
	 * 
	 * @return The {@link Rotation} of the camera.
	 */
	public Rotation getRotation(){
		return rotation;
	}

	/**
	 * 
	 * @param rotation The new {@link Rotation} of the camera.
	 */
	public void setRotation(Rotation rotation){
		this.rotation = new Rotation(rotation.pitch, rotation.yaw, rotation.roll);
	}

	/**
	 * 
	 * @return True if the camera can move. False otherwise.
	 */
	public boolean isMovable() {
		return movable;
	}

	/**
	 * 
	 * @param movable True if the camera should be allowed to move. False otherwise.
	 */
	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	/**
	 * 
	 * @return The camera's speed.
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * 
	 * @param speed The new camera speed.
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * 
	 * @return The FOV (degrees) in the y direction.
	 */
	public float getyFOV() {
		return yFOV;
	}

	/**
	 * 
	 * @param yFOV The new FOV (degrees) in the y direction.
	 */
	public void setyFOV(float yFOV) {
		this.yFOV = yFOV;
	}


	/**
	 * 
	 * @return The aspect ratio of the camera.
	 */
	public float getAspectRatio() {
		return aspectRatio;
	}

	/**
	 * 
	 * @param aspectRatio The new aspect ratio of the camera.
	 */
	public void setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	/**
	 * 
	 * @return zNear, which is the closest distance the camera will render.
	 */
	public float getzNear() {
		return zNear;
	}

	/**
	 * 
	 * @param zNear The new closest distance for the camera to render.
	 */
	public void setzNear(float zNear) {
		this.zNear = zNear;
	}

	/**
	 * 
	 * @return zFar, which is the farthest distance the camera will render.
	 */
	public float getzFar() {
		return zFar;
	}

	/**
	 * 
	 * @param zFar The new farthest distance for the camera to render.
	 */
	public void setzFar(float zFar) {
		this.zFar = zFar;
	}

	private static double degSin(double degrees){
		return Math.sin(Math.toRadians(degrees));
	}

	private static double degCos(double degrees){
		return Math.cos(Math.toRadians(degrees));
	}
}