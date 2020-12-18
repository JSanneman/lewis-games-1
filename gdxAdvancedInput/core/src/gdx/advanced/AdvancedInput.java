package gdx.advanced;

import java.util.Scanner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

abstract class CameraEffect {
    protected OrthographicCamera cam;
    protected int duration, progress;
    protected ShapeRenderer renderer;
    protected SpriteBatch batch;
    public CameraEffect(OrthographicCamera cam, int duration, 
    SpriteBatch batch, ShapeRenderer renderer) {
        this.cam = cam;
        this.duration = duration;
        this.batch = batch;
        this.renderer = renderer;
        progress = duration;
    }
    public boolean isActive() {
        return (progress<duration);
    }
    public abstract void play();
    public void updateCamera() {
        cam.update();
        if (renderer != null) {
            renderer.setProjectionMatrix(cam.combined);
        }
        if (batch != null) {
            batch.setProjectionMatrix(cam.combined);
        }
    }
    public void start() {
        progress = 0;
    }
}

class CameraShake extends CameraEffect {
    private int intensity, intensitySafe;
    private int xFac, yFac;
    private float intensityMod;
    private int speed;
    
    public boolean yDir = false;
    public int getIntensity() {
        return intensity;
    }
    public void setIntensity(int intensity) {
        if (intensity < 0) {
            this.intensity = 0;
            this.intensitySafe=intensity;
        } else {
            this.intensity = intensity;
            this.intensitySafe=intensity;
            this.intensityMod = 1/((float) duration/speed);
            
        }
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        if (speed < 0) {
            speed = 0;
        } else {
            if (speed > duration) {
                speed = duration / 2;
            } else {
                this.speed = speed;
            }
        }
    }
    @Override
    public boolean isActive() {
        return super.isActive() && speed > 0;
    }
    public CameraShake(OrthographicCamera cam, int duration, SpriteBatch batch,
    ShapeRenderer renderer, int intensity, int speed) {
        super(cam,duration,batch,renderer);
        setSpeed(speed);
        setIntensity(intensity);
    }
    @Override
    public void play() {
        if (isActive()) {
        	System.out.println("Camera shake is active\n" + intensity + " " + intensityMod);
            if (progress % speed == 0) {
                intensity = -intensity;
                if (yDir) {
                	yFac = intensity;
                } else {
                	xFac = intensity;
                }
                
                cam.translate(xFac,yFac);
                if (intensity > 0) {
                	intensity -= intensitySafe*intensityMod;
                } else if (intensity < 0) {
                	intensity += intensitySafe*intensityMod;
                }
            }
            progress++;
            if (!isActive()) {
            	//float shakeFix = intensitySafe/3;
            }
            updateCamera();
        }
    }
    @Override
    public void start() {
    	
        super.start();
        this.intensity= this.intensitySafe;
        if (yDir ) {
        	xFac = 0;
        	yFac = intensity/2;
        } else {
        	xFac = intensity/2;
        	yFac = 0;
        }
        cam.translate(xFac,yFac);
        updateCamera();
    }
}

class InputHandler extends InputAdapter {
	private SpriteBatch batch;
	private OrthographicCamera cam;
	private Vector3 prevMouse;
	
	public int angle = 0;
	public boolean inBounds = false;
	public boolean shiftP = false;
	public boolean ctrlP = false;
	public boolean aPr, sPr, dPr, wPr, upPr, dnPr, lfPr, rtPr = false;
	
	public InputHandler(SpriteBatch batch, OrthographicCamera cam) {
		this.batch = batch;
		this.cam = cam;
	}
	
	@Override
	public boolean keyDown(int keyCode) {
		if (keyCode == Keys.SHIFT_LEFT) {
			shiftP =  true;
		} else if (keyCode == Keys.CONTROL_LEFT) {
			ctrlP = true;
		}
		
		if (keyCode == Keys.W) {
			wPr = true;
		}
		if (keyCode == Keys.A) {
			aPr = true;
		}
		
		if (keyCode == Keys.S) {
			sPr = true;
		}
		
		if (keyCode == Keys.D) {
			dPr = true;
		}
		return true;
	}
	
	@Override
	public boolean keyUp(int keyCode) {
		if (keyCode == Keys.SHIFT_LEFT) {
			shiftP =  false;
		} else if (keyCode == Keys.CONTROL_LEFT) {
			ctrlP = false;
		}
		
		if (keyCode == Keys.W) {
			wPr = false;
		}
		
		if (keyCode == Keys.A) {
			aPr = false;
		}
		
		if (keyCode == Keys.S) {
			sPr = false;
		}
		
		if (keyCode == Keys.D) {
			dPr = false;
		}
		return true;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		prevMouse = new Vector3(screenX, screenY, 0);
		return true;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(inBounds) {
			if (shiftP) {
				angle += (screenX - prevMouse.x);
				cam.rotate(screenX - prevMouse.x);
				if (angle < 0) {
					angle += 360;
				} else if (angle > 360) {
					angle -= 360;
				}
				
				updateCam();
			} else {
				float deltaX = (-screenX+prevMouse.x);
				float deltaY = (screenY-prevMouse.y);
				float deltaAngle = MathUtils.radDeg*MathUtils.atan2(-deltaY, -deltaX);
				float deltaLine = (float) Math.sqrt(deltaX*deltaX+deltaY*deltaY)*cam.zoom;
				
				cam.translate(-deltaLine*(MathUtils.cosDeg(-angle+deltaAngle)), -deltaLine*(MathUtils.sinDeg(-angle+deltaAngle)));
				updateCam();
			}
		}
		this.prevMouse = new Vector3(screenX, screenY, 0);
		return true;
	}
	
	public void updateCam() {
		cam.update();
		batch.setProjectionMatrix(cam.combined);
	}
	
	
}

public class AdvancedInput extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	OrthographicCamera cam;
	int WIDTH, HEIGHT;
	InputHandler handler;
	float imgX, imgY, imgWidth, imgHeight;
	CameraShake shaker;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		imgX = 0;
		imgY = 0;
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH/2, HEIGHT/2);
		handler = new InputHandler(batch, cam);
		Gdx.input.setInputProcessor(handler);
		updateCam();
		double speedCalc = 0;
		int speed = 0;
		Scanner in = new Scanner(System.in);
		while (speedCalc < 2 || speedCalc > 100) {
			System.out.println("Input Speed Percentage (number only, 2-100): ");
			speedCalc = in.nextDouble();
		}
		speed = (int) (1/speedCalc * 100);
		shaker = new CameraShake(cam, 100, batch, null, 100, speed);
		in.close();
	}
	
	public void updateCam() {
		cam.update();
		batch.setProjectionMatrix(cam.combined);
	}

	public void input() {
		
		/**
		 * I had no idea how to move the image from the input handler without moving
		 * EVERYTHING over and back again, like the image and width/height, so I just
		 * let the input handler run on it's own for the camera but give feedback for
		 * the image and take in the inBounds from here
		 */
		if(Gdx.input.getX() >= 0 && Gdx.input.getX() <= WIDTH && Gdx.input.getY() >= 0 && Gdx.input.getY() <= HEIGHT) {
			handler.inBounds = true;
		} else {
			handler.inBounds = false;
		}
		
		if (handler.wPr) {
			imgY+=10;
		}
		
		if (handler.sPr) {
			imgY-=10;
		}
		
		if (handler.aPr) {
			imgX-=10;
		}
		
		if (handler.dPr) {
			imgX+=10;
		}
		
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			cam.translate(0,5);
		}
		
		if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			cam.translate(0,-5);
		}
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			cam.translate(5,0);
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			cam.translate(-5,0);
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			if(handler.shiftP && !shaker.isActive()) {
				shaker.yDir = true;
				shaker.start();
			}else if(!shaker.isActive()) {
				shaker.yDir = false;
				shaker.start();
			}
		}
		shaker.play();
		updateCam();
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		input();
		batch.begin();
		batch.draw(img, imgX, imgY);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
