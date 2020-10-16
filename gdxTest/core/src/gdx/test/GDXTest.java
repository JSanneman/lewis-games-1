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
	OrthographicCamera cam;
	int WIDTH, HEIGHT;
	int baseHeight, baseWidth;
	int mouseX, mouseY; //used to pan with mouse
	int camAngle = 0; //used to reset camera and help pan correctly
	boolean keyRelease = true; //used to cleanly rotate 90 degrees
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		baseMap = new Texture("AerialMap.png");
		
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		baseWidth = baseMap.getWidth();
		baseHeight = baseMap.getHeight();
		
		mouseX = Gdx.input.getX();
		mouseY = Gdx.input.getY();
		
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH/2, HEIGHT/2);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
	}
	
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
				int deltaX = (mouseX-input.getX());
				int deltaY = (mouseY-input.getY());
				float mouseAngle = MathUtils.atan2(deltaY, deltaX);
				cam.rotate(-camAngle);
				cam.translate(deltaX*(cam.zoom)*cos(mouseAngle),deltaY*(cam.zoom)*sin(mouseAngle));
				cam.rotate(camAngle);
				updateCam();
			}
			
		}
		
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
			shiftPressed = true;
		} else {
			shiftPressed = false;
		}
		
		if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
			controlPressed = true;
		} else {
			controlPressed = false;
		}
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			if (shiftPressed && keyRelease == true) {
				camAngle-=90;
				cam.rotate(-90);
				keyRelease = false;
			} else if (controlPressed) {
				cam.rotate(-camAngle);
				camAngle = 0;
			} else if (!shiftPressed || keyRelease == true){
				camAngle -= 1;
				cam.rotate(-1);
			}
			updateCam();
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
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
		
		if(!Gdx.input.isKeyPressed(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.LEFT)) {
			keyRelease = true;
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			cam.zoom += .5;
			updateCam();
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.UP)) {
			cam.zoom -= .5;
			updateCam();
		}
	}
	
	public void updateCam() {
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		handleInput();
		mouseX = Gdx.input.getX();
		mouseY = Gdx.input.getY();
		batch.begin();
		
		batch.draw(baseMap, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		baseMap.dispose();
	}
}
