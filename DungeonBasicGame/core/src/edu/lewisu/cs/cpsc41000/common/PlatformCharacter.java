package edu.lewisu.cs.cpsc41000.common;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class PlatformCharacter extends MobileImageBasedScreenObject {
    private float gravity;
    private float maxVerticalSpeed;
    private float jumpSpeed;
    private Polygon bottomSurface;
    private ArrayList<ImageBasedScreenObject> platforms;
    public float getGravity() {
        return gravity;
    }    
    public float getMaxVerticalSpeed() {
        return maxVerticalSpeed;
    }
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
    public void setMaxVerticalSpeed(float mvs) {
        maxVerticalSpeed = mvs;
    }
    public float getJumpSpeed() {
        return jumpSpeed;
    }
    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }
    public void initBottomSurface() {
        float[] vertices = new float[8];
        vertices[0] = 5;
        vertices[1] = -10;
        vertices[2] = vertices[0]+getWidth()-10;
        vertices[3] = vertices[1];
        vertices[4] = vertices[2];
        vertices[5] = 10;
        vertices[6] = vertices[0];
        vertices[7] = vertices[5];
        bottomSurface = new Polygon(vertices);
    }
    public Polygon getBottomSurface() {
        bottomSurface.setPosition(xpos,ypos);
        bottomSurface.setOrigin(xorigin,yorigin);
        bottomSurface.setRotation(rotation);
        bottomSurface.setScale(scaleX,scaleY);
        return bottomSurface;        
    }
    public ArrayList<ImageBasedScreenObject> getPlatforms() {
        return platforms;
    }
    public void setPlatforms(ArrayList<ImageBasedScreenObject> platforms) {
        this.platforms = platforms;
    }
    public PlatformCharacter(Texture tex) {
        this(tex,0,0,0,0,0,1,1,false,false,0,0,null,0,700,1000,450,null);
    }
    public PlatformCharacter(Texture tex, int xpos, int ypos, boolean geoCenter) {
        this(tex, xpos, ypos,0,0,0,1,1,false,false,0,0,null,0,700,1000,450,null);
        if (geoCenter) {
            centerOriginGeometrically();
        }
    }
    public PlatformCharacter(Texture tex, int xpos, int ypos, int xorigin, 
    int yorigin, int rotation, int scaleX, int scaleY, boolean flipX, boolean flipY,
    float frameWidth, float frameHeight, int[] frameSequence, float animDelay, float 
    gravity, float maxVerticalSpeed, float jumpSpeed, ArrayList<ImageBasedScreenObject> platforms) {
        super(tex,xpos,ypos,xorigin,yorigin,rotation,scaleX,scaleY,flipX,flipY,
        frameWidth,frameHeight,frameSequence,animDelay);
        initMovement();
        setGravity(gravity);
        setMaxVerticalSpeed(maxVerticalSpeed);
        setJumpSpeed(jumpSpeed);
        initBottomSurface();
        setPlatforms(platforms);
    }
    public void jump() {
        moveUp(100);
        velocityVec.y = jumpSpeed;
    }
    public boolean onSolid() {
        Polygon bottom = getBottomSurface();
        Polygon other;
        if (ypos <= 0) {
            ypos = 0;
            return true;
        } else if (platforms != null) {
            for (ImageBasedScreenObject platform : platforms) {
                other = platform.getBoundingPolygon();
                if (Intersector.overlapConvexPolygons(bottom, other)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
    public boolean onSolid(ImageBasedScreenObject surface) {
        Polygon bottom = getBottomSurface();
        Polygon other = surface.getBoundingPolygon();
        if (Intersector.overlapConvexPolygons(bottom,other)) {
            return true;
        } else {
            return false;
        }
    }
    public void applyGravity() {
        if (!onSolid()) {
            accelerationVec.add(0,-gravity);
        }   
    }
    @Override
    public void applyPhysics(float dt) {
        applyGravity();
        super.applyPhysics(dt);
    }
    public Vector2 preventOverlap(ImageBasedScreenObject other) {
        if (!onSolid(other)) {
            return super.preventOverlap(other);
        } else {
            return null;
        }
    }
}
