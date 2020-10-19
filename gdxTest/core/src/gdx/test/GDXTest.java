package gdx.test;

import static com.badlogic.gdx.Gdx.input;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class GDXTest extends ApplicationAdapter {
	SpriteBatch batch;
	Texture baseMap;
	Texture zoomMap1;
	Texture zoomMap2;
	OrthographicCamera cam;
	int WIDTH, HEIGHT;
	int baseHeight, baseWidth;
	int mouseX, mouseY; //used to pan with mouse
	int camAngle = 0; //used to reset camera and help pan correctly
	boolean keyRelease = true; //used to cleanly rotate 90 degrees
	int zoomLevel = 0;
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		baseMap = new Texture("AerialMap.png"); //My awful MSPaint images
		zoomMap1 = new Texture("mapZoomOne.png");
		zoomMap2 = new Texture("mapZoomTwo.png");
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		baseWidth = baseMap.getWidth(); //Could be used for further development
		baseHeight = baseMap.getHeight();
		
		//Used to get the mouse position for use in vectors
		mouseX = Gdx.input.getX();
		mouseY = Gdx.input.getY();
		
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH/2, HEIGHT/2);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
	}
	
	
	//sin and cos used for code clarity
	public float sin(float angle) {
		return MathUtils.sinDeg(angle);
	}
	
	public float cos(float angle) {
		return MathUtils.cosDeg(angle);
	}
	
	public void handleInput() {
		boolean shiftPressed = false;
		boolean controlPressed = false;
		if (Gdx.input.isTouched()) {
			if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
				float deltaX = (mouseX-input.getX());
				float deltaY = (-mouseY+input.getY());
				float line = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY) * cam.zoom; //Takes into account the movement and the scale
				float mouseAngle =((180/MathUtils.PI)*MathUtils.atan2(-deltaY, -deltaX));//Determines the direction of the mouse movement in degrees
				if (mouseAngle < 0) { //More for debug than anything
					mouseAngle+=360;
				}
				
				/**
				 * The hardest part to understand, the line is composed of both x and y components of the
				 * initial mouse movement. It is scaled correctly above by multiplying the camera zoom. The
				 * final angle is the difference between the angle the camera is rotated to, and the angle
				 * the mouse was moved, creating a sort of projection angle, which is multiplied by the line
				 * to complete the projection vector. That vector is then split into ITS x and y movements
				 * which are the final camera translations.
				 */
				cam.translate(-line*cos(mouseAngle-camAngle),-line*sin(mouseAngle-camAngle));
				updateCam();
			}
			
			if(Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
				//Determines the zoom level in reference to which floor is visible.
				if (zoomLevel == 0) {
					zoomLevel++;
				} else if (zoomLevel == 1) {
					zoomLevel = 0; //Zoom level could be increased but two levels is enough for me
				}
				updateCam();
			}
			
		}
		
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) { //Helps alter rotation controls
			shiftPressed = true;
		} else {
			shiftPressed = false;
		}
		
		if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) { //Serves and a reset for rotation controls
			controlPressed = true;
		} else {
			controlPressed = false;
		}
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			if (!Gdx.input.isButtonPressed(Buttons.LEFT)) {
				if (shiftPressed && keyRelease == true) { //Rotated evenly by 90 degrees
					//camAngle must be kept track of to properly reset the camera and to move while rotated
					camAngle-=90;
					cam.rotate(-90);
					keyRelease = false;
				} else if (controlPressed) { //Resets rotation
					cam.rotate(-camAngle);
					camAngle = 0;
				} else if (!shiftPressed || keyRelease == true){ //Rotates smoothly
					camAngle -= 1;
					cam.rotate(-1);
				}
				updateCam();
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			if (!Gdx.input.isButtonPressed(Buttons.LEFT)) {
				if (shiftPressed && keyRelease == true) {
					camAngle+=90;
					cam.rotate(90);
					keyRelease = false;
				} else if (controlPressed) {
					cam.rotate(-camAngle);
					camAngle = 0;
				} else if (!shiftPressed || keyRelease == true){
					camAngle += 1;
					cam.rotate(1);
				}
				updateCam();
			}
		}
		
		//The keyRelease is for shift rotating, so it doesn't constantly rotate, effectively turning this into a "isKeyJustPressed" check
		if(!Gdx.input.isKeyPressed(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.LEFT)) {
			keyRelease = true;
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			//No in program zoom variable is needed, since cam takes care of that itself
			cam.zoom += .5;
			updateCam();
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.UP)) {
			cam.zoom -= .5;
			updateCam();
		}
	}
	
	//Never not have an updateCam method
	public void updateCam() {
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(8/255f, 135/255f, 39/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		handleInput();
		mouseX = Gdx.input.getX();
		mouseY = Gdx.input.getY();
		batch.begin();
		
		//Tells which image to render where, would be more helpful if they weren't the same size
		if(cam.zoom >= 0 && cam.zoom < 1) {
			if (zoomLevel == 0) {
				batch.draw(zoomMap1, 0, 0);
			} else {
				batch.draw(zoomMap2, 0, 0);
			}
		} else {
			batch.draw(baseMap, 0, 0);
		}
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		baseMap.dispose();
	}
}
