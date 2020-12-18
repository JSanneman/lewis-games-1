package edu.lewisu.cs.cpsc41000.common;

import com.badlogic.gdx.graphics.Texture;

public class AnimatedImageBasedScreenObject extends ImageBasedScreenObject {
    public AnimatedImageBasedScreenObject(Texture tex, int xpos, int ypos, int xorigin, 
    int yorigin, int rotation, int scaleX, int scaleY, boolean flipX, boolean flipY,
    float frameWidth, float frameHeight, int[] frameSequence, float animDelay) {
        super(tex,xpos,ypos,xorigin,yorigin,rotation,scaleX,scaleY,flipX,flipY);
        setAnimationParameters(frameWidth, frameHeight, frameSequence, animDelay);
    }
    public AnimatedImageBasedScreenObject(Texture tex, int xpos, int ypos, boolean geoCenter) {
        super(tex,xpos,ypos,geoCenter);
    }
    public AnimatedImageBasedScreenObject(Texture tex, int xpos, int ypos, boolean geoCenter, 
    float frameWidth, float frameHeight, int[] frameSequence, float animDelay) {
        super(tex,xpos,ypos,geoCenter);
        setAnimationParameters(frameWidth, frameHeight, frameSequence, animDelay);
    }
    public boolean shouldAnimate() {
        if (animationParameters == null || 
        animationParameters.getFrameSequence() == null ||
        animationParameters.getFrameSequence().length < 2) {
            return false;
        } else {
            return true;
        }
    }
    @Override
    public float getWidth() {
        if (shouldAnimate()) {
            return animationParameters.getFrameWidth();
        } else {
            return super.getWidth();  // on the width of the image
        }
    }
    @Override
    public float getHeight() {
        if (shouldAnimate()) {
            return animationParameters.getFrameHeight();
        } else {
            return super.getHeight();  // on the width of the image
        }
    }
    public void setAnimationActive(boolean active) {
        animationParameters.setActive(active);
    }
    public void resetAnimation() {
        setAnimationActive(false);
        animationParameters.setCurrentFrame(0);  // at the end of a run
    }
    public void animate(float dt) {
        animationParameters.addTime(dt);
        if (animationParameters.timeToChange()) {
            animationParameters.changeFrame();
        }
    }
    public void setDiscreteAnimation(boolean disc) {
        animationParameters.setDiscrete(disc);
        animationParameters.setActive(false);
    }
    public void startDiscreteAnimation() {
        resetAnimation();
        setAnimationActive(true);
    }
    public boolean isDiscreteAnimation() {
        return animationParameters.getDiscrete();
    }
    public boolean isAnimationActive() {
        return animationParameters.isActive();
    }
}
