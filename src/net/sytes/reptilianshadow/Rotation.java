package net.sytes.reptilianshadow;

public class Rotation {
    
    /**
     * The pitch of this rotation instance (angle up and down).
     */
	public float pitch;
	
	/**
	 * The yaw of this rotation instance (angle looking left and right).
	 */
	public float yaw;
	
	/**
	 * The roll of this rotation instance (angle spinning left or right).
	 */
	public float roll;
	
	/**
	 * Calls the other constructor with 3 zeros.
	 */
	public Rotation(){
		this(0,0,0);
	}
	
	/**
	 * See fields for parameter explanations.
	 * @param pitch
	 * @param yaw
	 * @param roll
	 */
	public Rotation(float pitch, float yaw, float roll){
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}
	
	public String toString(){
		return "Pitch: " + pitch + "\tYaw: " + yaw + "\tRoll: " + roll;
	}
}
