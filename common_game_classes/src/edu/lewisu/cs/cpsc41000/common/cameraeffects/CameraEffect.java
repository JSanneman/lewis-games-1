package edu.lewisu.cs.cpsc41000.common.cameraeffects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class CameraEffect {
	protected OrthographicCamera cam;
	protected int duration, progress;
	protected SpriteBatch batch;
	protected ShapeRenderer renderer;
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
