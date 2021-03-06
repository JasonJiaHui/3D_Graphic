package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * COMMENT: Comment Game
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener {

	public static Terrain myTerrain;
	public static Camera myCamera;
	private long myTime;


	public Game(Terrain terrain) {
		super("Assignment bak 2");
		myTerrain = terrain;
	}

	/**
	 * Run the game.
	 *
	 */
	public void run() {
		// basic setup
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);

		GLJPanel panel = new GLJPanel();
		panel.addGLEventListener(this);
		panel.addKeyListener(this);
		panel.setFocusable(true);

		getContentPane().add(panel);
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Add an animator to call 'display' at 60fps
		FPSAnimator animator = new FPSAnimator(60);
		animator.add(panel);
		animator.start();


	}

	/**
	 * Load a level file and display it.
	 * 
	 * @param args
	 *            - The first argument is a level file in JSON format
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Terrain terrain = LevelIO.load(new File(args[0]));
		//		Terrain terrain =  new Terrain(5, 5);
		Game game = new Game(terrain);
		game.run();
	}

	private void update() {

		// compute the time since the last frame
		long myTime = System.currentTimeMillis();

		// take a copy of the ALL_OBJECTS list to avoid errors 
		// if new objects are created in the update
		List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);

		// update all objects
		for (GameObject g : objects) {
			g.update(myTime);
		}        
	}

	@Override
	public void display(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		myCamera.setView(gl);

		myTerrain.drawTerrain(gl);

		update();
		GameObject.ROOT.draw(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {

		// init time
		myTime = System.currentTimeMillis();

		//drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();

		// Enable depth testing.
		gl.glEnable(GL2.GL_DEPTH_TEST);

		// Enable Lighting
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LIGHT1);

		gl.glEnable(GL2.GL_NORMALIZE);

		// Cull back faces.
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);

		// Enable texturing
		gl.glEnable(GL2.GL_TEXTURE_2D);

		// Enable Blending 
//		gl.glEnable(GL_BLEND);      
		//Creates an additive blend, which looks spectacular on a black background
//		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);

		// Initialise the scene
		myCamera = new Camera();
		MathUtil.genTrigTables();


		/*
    	// Anti Aliasing + Alpha Blending
    	gl.glEnable(GL2.GL_LINE_SMOOTH);
    	gl.glHint(GL2.GL_LINE_SMOOTH_HINT,GL2.GL_NICEST);
    	gl.glEnable(GL2.GL_BLEND);
    	gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);
    	GLCapabilities capabilities = new GLCapabilities(null);
    	capabilities.setNumSamples(4);
    	capabilities.setSampleBuffers(true);
    	gl.glEnable(GL.GL_MULTISAMPLE);
		 */

		/*
    	// Enable Textures
    	gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE); 
    	gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
		 */

		// Create Terrain
		myTerrain.initTerrain(gl);

		makeSceneObjects(gl);

		List<GameObject> objects = new ArrayList<GameObject>(GameObject.ALL_OBJECTS);

		// update all objects
		for (GameObject g : objects) {
			g.init(gl);
		} 

	}

	private void makeSceneObjects(GL2 gl) {
		GameObjectTest s1 = new GameObjectTest("glass_ball.jpg", "jpg", true);
		gl.glPushMatrix();
		{
			GameObjectSphere ds = new GameObjectSphere("deathstar.jpg", "jpg", false);
			ds.setPosition(myTerrain.size().width+6, 7, 4);
			ds.scale(5);
			ds.rotateX(45);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
		}
		gl.glPopMatrix();
	
		gl.glPushMatrix();
		gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
		{
			SpaceShipBlaster s2 = new SpaceShipBlaster(0.7);
			s2.rotateX(-90);
			s2.setPosition(myTerrain.getHighest()[0]+0.5, myTerrain.getHighest()[2]+0.5, 1);
		}
		gl.glPopAttrib();
		gl.glPopMatrix();   
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {

		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluPerspective(60, 1, 0.01, 90);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			myCamera.WalkForward();
			break;

		case KeyEvent.VK_S:
			myCamera.WalkBackward();
			break;

		case KeyEvent.VK_A:
			myCamera.WalkLeft();
			break;

		case KeyEvent.VK_D:
			myCamera.WalkRight();
			break;

		case KeyEvent.VK_UP:
			myCamera.lookUp();
			break;

		case KeyEvent.VK_DOWN:
			myCamera.lookDown();
			break;

		case KeyEvent.VK_LEFT:
			myCamera.lookLeft();
			break;

		case KeyEvent.VK_RIGHT:
			myCamera.lookRight();
			break;

		case KeyEvent.VK_Z:
			myCamera.moveUp();
			break;

		case KeyEvent.VK_X:
			myCamera.moveDown();
			break;

		case KeyEvent.VK_C:
			myCamera.toggleGroundMode();
			break;

		case KeyEvent.VK_V:
			myCamera.toggleFollowMode();
			break;

		case KeyEvent.VK_OPEN_BRACKET:
			myTerrain.decreaseTrees();
			break;

		case KeyEvent.VK_CLOSE_BRACKET:
			myTerrain.increaseTrees();
			break;

		default:
			break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
