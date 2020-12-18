package edu.lewisu.cs.cpsc41000.common;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EdgeHandler {
	// these next four variables describe the dimensions of the world the objects can move in.
    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    // at what distance from the edges should we start panning?
    private float border;
    // these will help us set the view and projection - they're used in the panning operation
    private OrthographicCamera cam;
    private SpriteBatch batch;
    // this is the object that we are responding to when we enforce edges
    private ImageBasedScreenObject obj;
    // how will we deal with edges in the horizontal and vertical directions?
    // see the internal class EdgeConstants to determine how to interpret these strategies
    private int verticalStrategy;
    private int horizontalStrategy;
    
    public class EdgeConstants {
    	public static final int LOCK = 0;
    	public static final int WRAP = 1;
    	public static final int PAN = 2;
    	public static final int XAXIS = 0;
    	public static final int YAXIS = 1;
    }
    
    public EdgeHandler(ImageBasedScreenObject obj, OrthographicCamera cam,
    SpriteBatch batch, float minX, float maxX, float minY, float maxY,
    float border, int horizontalStrategy, int verticalStrategy) {
        this.obj = obj;
        this.cam = cam;
        this.batch = batch;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.border = border;
        this.verticalStrategy = verticalStrategy;
        this.horizontalStrategy = horizontalStrategy;
    }
    /**
     * This constructor assumes we want to lock at both boundaries.
     * Also assumes we only want to travel within one screen and enforce
     * a boundary of 20 pixels
     * @param obj - the object to monitor
     * @param cam - the camera to control if we need to pan
     * @param batch - the drawing tool to adjust the projection matrix for
     */
    public EdgeHandler(ImageBasedScreenObject obj, OrthographicCamera cam,
    SpriteBatch batch) {
        this(obj,cam,batch,0,Gdx.graphics.getWidth(),0,Gdx.graphics.getHeight(),20,
        EdgeConstants.LOCK,EdgeConstants.LOCK);
    }
    /**
     * This constructor assumes we want limit ourselves to one screen,
     * but we can customize the horizontal and vertical strategies.
     * Assumes a boundary of 20 pixels
     * @param obj - the object to monitor
     * @param cam - the camera to control if we need to pan
     * @param batch - the drawing tool to adjust the projection matrix for
     * @param horizontalStrategy - what to do in x direction
     * @param verticalStragegy - what do do in y direction
     */    
    public EdgeHandler(ImageBasedScreenObject obj, OrthographicCamera cam,
    SpriteBatch batch, int horizontalStrategy, int verticalStrategy) {
        this(obj,cam,batch,0,Gdx.graphics.getWidth(),0,Gdx.graphics.getHeight(),20,
        horizontalStrategy,verticalStrategy);
    }

    public void lockCoordinates(int axis) {
        float x = obj.getXPos();
        float y = obj.getYPos();
        float imgWidth = obj.getWidth();
        float imgHeight = obj.getHeight();
        if (axis == EdgeConstants.XAXIS) {
            if (x > maxX - imgWidth) {  // off the right edge of the world
                obj.setXPos(maxX-imgWidth);
            } else if (x < minX) {  // off the left edge of the world
                obj.setXPos(minX);
            }
        }
        if (axis == EdgeConstants.YAXIS) {
            if (y > maxY - imgHeight) {
                obj.setYPos(maxY - imgHeight); // off the top edge of the world
            } else if (y < minY) {		// off the bottom edge of the world
                obj.setYPos(minY);
            }
        }
    }

    public void wrapCoordinates(int axis) {
        float x = obj.getXPos();
        float y = obj.getYPos();
        float imgWidth = obj.getWidth();
        float imgHeight = obj.getHeight(); 
        if (axis == EdgeConstants.XAXIS) {
            if (x > maxX) {   // beyond the right edge of the world
                obj.setXPos(minX-obj.getWidth());  // about to enter the other side of the world
            } else if (x < minX -obj.getWidth()) { // vanished from the world on the left edge
                obj.setXPos(maxX);  // position my left edge at the right edge of the world
            }
        }
        if (axis == EdgeConstants.YAXIS) {
            if (y > maxY) {   // vanished at the top of the world
                obj.setYPos(minY-obj.getHeight());  // re-enter at the bottom of the world
            } else if (y < minY - obj.getHeight()) {  // vanishing at the bottom of the world
                obj.setYPos(maxY);  // position bottom edge to align with the top of the world
            }
        }
    }
    
    public void panCoordinates(int axis) {
        float x = obj.getXPos();  // world coordinates
        float y = obj.getYPos();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        // this should be adjusted if you decide to position the camera
        // initially somewhere other than screenWidth/2 and screenHeight/2
        float screenPosX = x - (cam.position.x - screenWidth/2);
        float screenPosY = y - (cam.position.y - screenHeight/2);
        if (axis == EdgeConstants.XAXIS) {
            if (screenPosX > screenWidth - obj.getWidth() - border) {
            	// pan because we are moving right off the screen
                cam.position.x = cam.position.x + screenPosX - screenWidth + 
                    obj.getWidth() + border;
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            } else if (screenPosX < border) {
            	// moving left off the screen - we need to pan the camera left
            	cam.position.x = cam.position.x - (border-screenPosX);
            	cam.update();
            	batch.setProjectionMatrix(cam.combined);
            }
            lockCoordinates(EdgeConstants.XAXIS);
        }
        if (axis == EdgeConstants.YAXIS) {
            if (screenPosY > screenHeight - obj.getHeight() - border) {
            	// moving off the top edge
                cam.position.y = cam.position.y + screenPosY - screenHeight +
                    obj.getHeight() + border;
                cam.update();
                batch.setProjectionMatrix(cam.combined);
            } else if (screenPosY < border) {
            	cam.position.y = cam.position.y - (border-screenPosY);
            	cam.update();
            	batch.setProjectionMatrix(cam.combined);
            }
            lockCoordinates(EdgeConstants.YAXIS);
        }
    }
    /**
     * This function serves as a one-stop shop for enforcing edges.
     * It's provided for convenience.
     * This should be the only function that needs to be called.
     */
    public void enforceEdges() {
    	if (horizontalStrategy == EdgeConstants.LOCK) {
    		lockCoordinates(EdgeConstants.XAXIS);
    	} else if (horizontalStrategy == EdgeConstants.WRAP) {
    		wrapCoordinates(EdgeConstants.XAXIS);
    	} else if (horizontalStrategy == EdgeConstants.PAN) {
            panCoordinates(EdgeConstants.XAXIS);
        }
        if (verticalStrategy == EdgeConstants.LOCK) {
            lockCoordinates(EdgeConstants.YAXIS);
        } else if (verticalStrategy == EdgeConstants.WRAP) {
            wrapCoordinates(EdgeConstants.YAXIS);
        } else if (verticalStrategy == EdgeConstants.PAN) {
            panCoordinates(EdgeConstants.YAXIS);
        }
    }
}
