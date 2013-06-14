package net.sytes.reptilianshadow;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;


import static org.lwjgl.opengl.GL11.*;

public class Game {

	public static Game game;

	private static HashMap<String, String> configHashMap;

	//=============================
	//== Configuration Variables ==
	//=============================

	public double gConstant;

	private boolean startFullScreen;

	private Dimension displaySize;

	//=============================
	//==        My Values        ==
	//=============================

	private double version = 0.01;

	//=============================

	private Camera camera;

	private float zNear;

	private float zFar;


	private ArrayList<Body> allBodies;

	public static void main(String[] args){
		game = new Game();

		game.importConfig();

		game.initDisplay();

		game.initGL();

		game.start();
	}

	private void importConfig(){

		File configFile = new File("voidCosmos.conf");

		try {
			configHashMap = SimpleConfig.getHashMap(configFile);
		} catch (FileNotFoundException e) {

			JFileChooser jf = new JFileChooser();
			jf.setFileSelectionMode(JFileChooser.FILES_ONLY); //only files
			jf.setMultiSelectionEnabled(false); //only one file 

			while  (!configFile.exists()){
				jf.setCurrentDirectory(new File(".")); //start file chooser in the directory of the game jar
				if ( jf.showOpenDialog(null) != JFileChooser.APPROVE_OPTION ){
					JOptionPane.showMessageDialog(null, "You MUST choose a configuration file.");
					continue;
				}
				configFile =  jf.getSelectedFile();
			}
			try {
				configHashMap = SimpleConfig.getHashMap(configFile);
			} catch (FileNotFoundException e1) {
				// wut?
				e1.printStackTrace();
			}
		}

		//=============================
		//== Configuration Variables ==
		//=============================

		try{

			gConstant = Double.parseDouble(configHashMap.get("gConstant"));

			startFullScreen = Boolean.parseBoolean(configHashMap.get("startFullScreen"));

			String displaySizeStr = configHashMap.get("displaySize");
			//account for uppercase or lowercase x
			int displaySizeXIndex = (displaySizeStr.indexOf("x") != -1) ? displaySizeStr.indexOf("x") : displaySizeStr.indexOf("X");
			int displayWidth = Integer.parseInt(displaySizeStr.substring(0, displaySizeXIndex));
			int displayHeight = Integer.parseInt(displaySizeStr.substring(displaySizeXIndex + 1));

			displaySize = new Dimension(displayWidth, displayHeight);

			zNear = Float.parseFloat(configHashMap.get("zNear"));

			zFar = Float.parseFloat(configHashMap.get("zFar"));


		}catch(Exception e){
			System.err.println("Your configuration file is faulty in some way!");
		}

		//=============================



	}

	private void initDisplay(){

		try {
			if (startFullScreen){
				Display.setFullscreen(true);
			}else {
				Display.setDisplayMode(new DisplayMode(displaySize.getWidth(), displaySize.getHeight()));
			}


			Display.setVSyncEnabled(true);

			Display.setTitle("Void Cosmos " + "v" + version);
			Display.create();

		} catch (LWJGLException e) {
			System.err.println("Derp, LWJGL doesn't exist or\ndoes not have necessary natives for the system");
			e.printStackTrace();
		}




	}

	private void initGL(){

		camera = new Camera(new Point3D(0, 0, -10), new Rotation(0, 0, 0), 90, Display.getWidth()/Display.getHeight(), zNear, zFar);

		//TODO set custom background color?
		glClearColor(0.0f, 0.0f, 0.0f, 1);

		//Don't put stuff that's behind a thing in front of it...
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_DOUBLEBUFFER);


		//REMEMBER TO INITIALIZE VERTICES IN
		//COUNTER-CLOCKWISE ORDER!!!!!!!
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK); //Don't render the back of polygons

		camera.initPerspective();

		glMatrixMode(GL_MODELVIEW);

	}


	public void start(){

		allBodies =  new ArrayList<Body>();

		Mouse.setGrabbed(true);
		
		
		allBodies.add(new Body(100, new Point3D(-3, 0, 0), new Point3D(0, 0.2f, 0), new Rotation(0, 0, 0)));
		
		allBodies.add(new Body(100, new Point3D(3, 0, 0), new Point3D(0, -0.2f, 0), new Rotation(0, 0, 0)));
		
		allBodies.add(new Body(100, new Point3D(0, 3, 0), new Point3D(0.2f, 0, 0), new Rotation(0, 0, 0)));
		
		allBodies.add(new Body(100, new Point3D(0, -3, 0), new Point3D(-0.2f, 0, 0), new Rotation(0, 0, 0)));
		
		while(!Display.isCloseRequested()){
			doSimCalculations();
			render();
			handleInput();


			Display.update();
			Display.sync(60);
		}

		Display.destroy();
		System.exit(0);

	}

	public void doSimCalculations(){

		//update the accelerations and forces between all bodies

		Body.genNewAccelerations(allBodies);

		for (int i = 0; i < allBodies.size(); i++){
			allBodies.get(i).update();
		}

		

	}

	private void render(){

		glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();

		camera.doTranslations();

		
		for (int i = 0; i < allBodies.size(); i++){
			allBodies.get(i).render();
		}
		
		
		//draw platform cylinder
		glColor3f(0.5f, 0.2f, 0.8f);
		glPushMatrix();
		glTranslatef(0, -1f, 0);
		glRotated(90, 1, 0, 0);
		Cylinder patCyl = new Cylinder();
		patCyl.setDrawStyle(GLU.GLU_LINE);
		patCyl.draw(1.5f, 1.5f, 50.0f, 10, 10); //basically sets the radius, and number of rows/columns of
		//vertices that make up the circle
		glPopMatrix(); //remove any translations made





	}

	private void handleInput(){
		camera.doKeyboardControl();
		camera.doMouseControl();


		while (Keyboard.next()){
			if (Keyboard.getEventKey() == Keyboard.KEY_Q){
				System.exit(0);
			}


		}

		while (Mouse.next()){
			if (Mouse.getEventButtonState() && Mouse.getEventButton() == 2){
				Mouse.setGrabbed(!Mouse.isGrabbed());
			}
		}

	}


}
