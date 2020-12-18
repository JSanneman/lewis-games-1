package edu.lewisu.cs.cpsc41000.common.inputhandlers;

import edu.lewisu.cs.cpsc41000.common.MobileImageBasedScreenObject;

/**
 * An InputAdapter subclass that can control
 * a MovingImageBasedScreenObject
 * @author klumpra
 *
 */
public class MIBSOControl {
	private MobileImageBasedScreenObject obj;
	
	public MIBSOControl(MobileImageBasedScreenObject obj) {
		this.obj = obj;
	}
    /*functions to override, all of which return boolean
    keyDown(int keycode)
    keyTyped (char character)
    keyUp(int keycode)
    mouseMoved(int screenX, int screenY)
    scrolled(float amountX, float amountY)
    touchDown(int screenX, int screenY, int pointer, int button)
    touchDragged(int screenX, int screenY, int pointer)
    touchUp(int screenX, int screenY, int pointer, int button)
	*/
}
