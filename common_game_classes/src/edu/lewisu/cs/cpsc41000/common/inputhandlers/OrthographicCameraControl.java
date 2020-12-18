package edu.lewisu.cs.cpsc41000.common.inputhandlers;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class OrthographicCameraControl extends InputAdapter {
	private OrthographicCamera cam;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	public OrthographicCameraControl(OrthographicCamera cam, SpriteBatch batch, ShapeRenderer renderer) {
		this.cam = cam;
		this.batch = batch;
		this.renderer = renderer;
	}
	public void updateCamera() {
		cam.update();
		if (batch != null) {
			batch.setProjectionMatrix(cam.combined);
		}
		if (renderer != null) {
			renderer.setProjectionMatrix(cam.combined);
		}
	}
	/* consider providing definitions for these functions:
	 *      keyDown(int keycode)
		    keyTyped (char character)
		    keyUp(int keycode)
		    mouseMoved(int screenX, int screenY)
		    scrolled(float amountX, float amountY)
		    touchDown(int screenX, int screenY, int pointer, int button)
		    touchDragged(int screenX, int screenY, int pointer)
		    touchUp(int screenX, int screenY, int pointer, int button)
	 */
	/*
	@Override
	public boolean keyDown(int keyCode) {
		if (keyCode == Keys.SHIFT_LEFT || keyCode == Keys.SHIFT_RIGHT) {
			shiftHeld = true;
		}
		if (keyCode == Keys.SPACE) {
			if (shiftHeld) {
				System.out.println("Shiftfire!");
			} else {
				System.out.println("Fire!");
			}
		}
		return true;  // this function completely handles the event
	}
	@Override
	public boolean keyUp(int keyCode) {
		if (keyCode == Keys.SHIFT_LEFT || keyCode == Keys.SHIFT_RIGHT) {
			shiftHeld = false;
		}
		return true;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		startCam = new Vector3(cam.position.x,cam.position.y,0);
		startMouse = new Vector3(screenX, screenY, 0);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		float diffX = screenX - startMouse.x;
		float diffY = screenY - startMouse.y;
		cam.position.x = startCam.x + diffX;
		cam.position.y = startCam.y - diffY;
		updateCamera();
		return true;
    }
    */
}
