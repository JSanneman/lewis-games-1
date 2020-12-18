package edu.lewisu.cs.cpsc41000.common;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class MobileImageBasedScreenObject extends AnimatedImageBasedScreenObject {
    protected Vector2 velocityVec;
    protected Vector2 accelerationVec;
    protected float acceleration;
    protected float maxSpeed;
    protected float deceleration;

    public MobileImageBasedScreenObject(Texture tex) {
        this(tex,0,0,0,0,0,1,1,false,false,0,0,null,0);
    }
    public MobileImageBasedScreenObject(Texture tex, int xpos, int ypos, boolean geoCenter) {
        this(tex, xpos, ypos,0,0,0,1,1,false,false,0,0,null,0);
        if (geoCenter) {
            centerOriginGeometrically();
        }
    }
    public MobileImageBasedScreenObject(Texture tex, int xpos, int ypos, int xorigin, 
    int yorigin, int rotation, int scaleX, int scaleY, boolean flipX, boolean flipY,
    float frameWidth, float frameHeight, int[] frameSequence, float animDelay) {
        super(tex,xpos,ypos,xorigin,yorigin,rotation,scaleX,scaleY,flipX,flipY,
        frameWidth,frameHeight,frameSequence,animDelay);
        initMovement();
    }
    public void initMovement() {
        velocityVec = new Vector2(0,0);
        accelerationVec = new Vector2(0,0);
        acceleration = 0;
        maxSpeed = 1000;
        deceleration = 0;
    }
    public void initMovement(float acc, float maxSpeed, float dec) {
        this.initMovement();
        setAcceleration(acc);
        setMaxSpeed(maxSpeed);
        setDeceleration(dec);
    }
    //this function helps us back of a collision
    public Vector2 preventOverlap(Collidable other) {
        Polygon p1 = getBoundingPolygon();
        Polygon p2 = other.getBoundingPolygon();
        MinimumTranslationVector mtv = new MinimumTranslationVector();
        boolean polygonOverlap = 
            Intersector.overlapConvexPolygons(p1, p2, mtv);
        if (!polygonOverlap) {
            return null;  // no need to move to clear the overlap
        }
        move(mtv.normal.x*mtv.depth, mtv.normal.y*mtv.depth);
        return mtv.normal;  // return the rebound direction.
    }
    /*
        In collisions, two obstacles run into each other.
        Those obstacles had momentum.
        p = momentum = mass of the object * its velocity
        In a perfectly elastic collision, the objects' momentum does
        not change because of all the energy remains as kinetic energy
        - energy associated with the objects' velocity.
        Real collisions are inelastic.
    */
    /** 
     * Adjust the acceleration to simulate a force that moves us
     * back from an obstacle
     * @param angle - direction we should rebound in
     * @param elasticity - how imperfect was the collision
     */
    public void rebound(float angle, float elasticity) {
        accelerationVec.add(
            new Vector2(
                (int)(getSpeed()*elasticity*acceleration),0).setAngle(angle));
    }
    public void setSpeed(float speed) {
        //if length is 0, assume angle is zero
        if (velocityVec.len() == 0) { 
            velocityVec.set(speed,0);
        } else {
            velocityVec.setLength(speed);
        }
    }
    public float getSpeed() {
        return velocityVec.len();
    }
    public void setMotionAngle(float angle) {
        velocityVec.setAngle(angle);
    }
    public float getMotionAngle() {
        return velocityVec.angle();
    }
    public void setAcceleration(float acc) {
        acceleration = acc;
    }
    public void accelerateAtAngle(float angle) {
        accelerationVec.add(new 
            Vector2(acceleration,0).setAngle(angle));
    }
    public void accelerateForward() {
        accelerateAtAngle(getRotation());
    }
    public void setMaxSpeed(float ms) {
        maxSpeed = ms;
    }
    public void setDeceleration(float dec) {
        deceleration = dec;
    }
    public boolean isMoving() {
        return (getSpeed() > 0);
    }
    public void applyPhysics(float dt) {
        // apply acceleration
        velocityVec.add(accelerationVec.x*dt, accelerationVec.y*dt);
        float speed = getSpeed();

        // decelerate when not accelerating
        if (accelerationVec.len() == 0) {
            speed -= deceleration * dt;
        }

        // keep speed within set bounds
        speed = MathUtils.clamp(speed,0,maxSpeed);

        // update velocity to match new speed
        setSpeed(speed);

        move(velocityVec.x*dt, velocityVec.y*dt);

        if (isMoving() || (isDiscreteAnimation() && isAnimationActive())) {
            animate(dt);
        } else if (!isDiscreteAnimation()) {
            resetAnimation();
        }

        // reset acceleration
        accelerationVec.set(0,0);
    }
    public void move(float dx, float dy) {
        xpos += dx;
        ypos += dy;
    }
    public void moveX(float amt) {
        xpos += amt;
    }
    public void moveY(float amt) {
        ypos += amt;
    }
    public void moveUp(float amt) {
        moveY(amt);
    }
    public void moveDown(float amt) {
        moveY(-amt);
    }
    public void moveLeft(float amt) {
        moveX(-amt);
    }
    public void moveRight(float amt) {
        moveX(amt);
    }
    public void rotateCW(float deg) {
        rotate(-deg);
    }
    public void rotateCCW(float deg) {
        rotate(deg);
    }
    public void rotate(float dAngle) {
        rotation += dAngle;
    } 
}

