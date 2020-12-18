package edu.lewisu.cs.cpsc41000.common;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

public class ImageBasedScreenObject implements Collidable {
    protected Texture img;
    protected float xpos;
    protected float ypos;
    protected float xorigin;
    protected float yorigin;
    protected float rotation;
    protected float scaleX;
    protected float scaleY;
    protected boolean flipX;
    protected boolean flipY;
    protected Polygon boundingPolygon;
    protected boolean visible;
    protected AnimationParameters animationParameters;

    public void setAnimationParameters() {
        animationParameters = new AnimationParameters(getWidth(), getHeight(), null, 0);
    }
        
    /**
     * Establishes how descendant classes will carve up a spritesheet
     * to simulate animation
     * @param frameWidth
     * @param frameHeight
     * @param frameSequence
     * @param delay
     */
    public void setAnimationParameters(float frameWidth, float frameHeight, 
    int[] frameSequence, float delay) {
        animationParameters = new AnimationParameters(frameWidth, frameHeight, frameSequence, delay);
        initBoundingPolygon();
        centerOriginGeometrically();
    }

    public ImageBasedScreenObject(Texture tex) {
        this(tex,0,0,0,0,0,1,1,false,false);
    }
    public void centerOriginGeometrically() {
        xorigin = getWidth() / 2;
        yorigin = getHeight() / 2;
    }
    /**
     * This constructor is ideal for centering the origin on your image
     * @param tex - texture to show
     * @param xpos - x position
     * @param ypos - y position
     * @param geoCenter - do you want to center the rotation origin in the center of the object?
     */
    public ImageBasedScreenObject(Texture tex, int xpos, int ypos, boolean geoCenter) {
        this(tex, xpos, ypos,0,0,0,1,1,false,false);
        if (geoCenter) {
            centerOriginGeometrically();
        }
    }
    public ImageBasedScreenObject(Texture tex, int xpos, int ypos, int xorigin, 
    int yorigin, int rotation, int scaleX, int scaleY, 
    boolean flipX, boolean flipY) {
        img = tex;
        setXPos(xpos);
        setYPos(ypos);
        setXOrigin(xorigin);
        setYOrigin(yorigin);
        setRotation(rotation);
        setScaleX(scaleX);
        setScaleY(scaleY);
        this.flipX = flipX;
        this.flipY = flipY;
        initBoundingPolygon();
        setAnimationParameters();
        visible = true;
    }
    // depending on your actual ImageBasedScreenObject (and note that
    // you will probably come up with descendants of ImageBasedScreenObject)
    // you will want to override this function to customize the bounding
    // polygon.
    // these vertices are with respect to the model.
    public void initBoundingPolygon() {
        float[] vertices = new float[8];
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = getWidth();
        vertices[3] = 0;
        vertices[4] = vertices[2];
        vertices[5] = getHeight();
        vertices[6] = 0;
        vertices[7] = vertices[5];
        boundingPolygon = new Polygon(vertices);
    }
    // return the bounding polygon as updated by the same translation
    // rotation, and scaling that were applied to the object.
    // transforms the bounding polygon's coordinates from model 
    // to world coordinates
    @Override
    public Polygon getBoundingPolygon() {
        boundingPolygon.setPosition(xpos,ypos);
        boundingPolygon.setOrigin(xorigin,yorigin);
        boundingPolygon.setRotation(rotation);
        boundingPolygon.setScale(scaleX,scaleY);
        return boundingPolygon;
    }
    // this enables us to do the collision detection
    public boolean overlaps(Collidable other) {
        Polygon p1 = getBoundingPolygon();
        Polygon p2 = other.getBoundingPolygon();
        // to save some computation with more complicated bounding
        // polygons, test the rectangles first to see if we can avoid
        // that calculation.
        if (!p1.getBoundingRectangle().overlaps(
        p2.getBoundingRectangle())) {
            return false;
        }
        return Intersector.overlapConvexPolygons(p1,p2);
    }
    public Texture getImg() {
        return img;
    }

    public void setImg(Texture img) {
        this.img = img;
    }

    public float getXPos() {
        return xpos;
    }

    public void setXPos(float xpos) {
        this.xpos = xpos;
    }

    public float getYPos() {
        return ypos;
    }

    public void setYPos(float ypos) {
        this.ypos = ypos;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }
    public void scale(float sx, float sy) {
        setScaleX(scaleX*sx);
        setScaleY(scaleY*sy);
    }
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }
    public void scaleWidth(float amt) {
        scale(scaleX*amt,1f);
    }
    public void scaleHeight(float amt) {
        scale(1f, scaleY*amt);
    }
    public void flipX() {
        flipX = !flipX;
    }
    public void flipY() {
        flipY = !flipY;
    }
    public boolean getFlipX() {
        return flipX;
    }
    public void setFlipX(boolean fx) {
        flipX = fx;
    }
    public boolean getFlipY() {
        return flipY;
    }
    public void setFlipY(boolean fy) {
        flipY = fy;
    }
    public void setXOrigin(float xorigin) {
        this.xorigin = xorigin;
    }
    public void setYOrigin(float yorigin) {
        this.yorigin = yorigin;
    }
    public float getXOrigin() {
        return xorigin;
    }
    public float getYOrigin() {
        return yorigin;
    }
    public int getDrawStartX() {
        return animationParameters.getDrawStartX();
    }
    public int getDrawStartY() {
        return animationParameters.getDrawStartY();
    }
    public float getWidth() {
        return img.getWidth();
    }
    public float getHeight() {
        return img.getHeight();
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public boolean isVisible() {
        return (visible == true);
    }
    public void show() {
        visible = true;
    }
    public void hide() {
        visible = false;
    }
}
