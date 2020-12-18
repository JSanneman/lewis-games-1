package edu.lewisu.cs.cpsc41000.common;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ImageBasedScreenObjectDrawer {
    private SpriteBatch batch;
    public ImageBasedScreenObjectDrawer(SpriteBatch batch) {
        this.batch = batch;
    }
    public void draw(ImageBasedScreenObject obj) {
        if (obj.isVisible()) {
            batch.draw(obj.getImg(), obj.getXPos(), obj.getYPos(), 
            obj.getXOrigin(), obj.getYOrigin(), 
            obj.getWidth(), obj.getHeight(), obj.getScaleX(), obj.getScaleY(), obj.getRotation(),
            obj.getDrawStartX(),obj.getDrawStartY(),(int)(obj.getWidth()),(int)(obj.getHeight()), obj.getFlipX(), obj.getFlipY());
        }
    }
}

