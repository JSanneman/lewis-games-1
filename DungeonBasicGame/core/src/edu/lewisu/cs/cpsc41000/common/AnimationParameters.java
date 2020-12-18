package edu.lewisu.cs.cpsc41000.common;

public class AnimationParameters {
    private float frameWidth;
    private float frameHeight;
    private int currentFrame;
    private int[] frameSequence;
    private float timeSince;
    private float delay; 
    private boolean discrete; // should this be played all at once?
    private boolean active;
    public boolean getDiscrete() {
        return discrete;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public void setDiscrete(boolean discrete) {
        this.discrete = discrete;
    }

    public float getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(float frameWidth) {
        this.frameWidth = frameWidth;
    }

    public void addTime(float dt) {
        timeSince += dt;
    }
    public boolean timeToChange() {
        return timeSince >= delay;
    }
    public float getFrameHeight() {
        return frameHeight;
    }
    public void setFrameHeight(float frameHeight) {
        this.frameHeight = frameHeight;
    }
    public int getCurrentFrame() {
        return currentFrame;
    }
    public void setCurrentFrame(int currentFrame) {
        if (frameSequence != null) {
            if (discrete && isActive()) {
                if (currentFrame < frameSequence.length/2) {
                    this.currentFrame = currentFrame;
                } else {
                    active = false;
                }
            } else {
                this.currentFrame = currentFrame % (frameSequence.length/2);
            }
        } else {
            this.currentFrame = 0;
        }
    }
    public int[] getFrameSequence() {
        return frameSequence;
    }

    public void setFrameSequence(int[] frameSequence) {
        this.frameSequence = frameSequence;
    }
    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        if (delay < 0) {
            this.delay = 0;
        } else {
            this.delay = delay;
        }
    }
    public AnimationParameters() {
        frameWidth = 0;
        frameHeight = 0;
        currentFrame = 1;
        frameSequence = null;
        timeSince = 0;
        delay = 0.1f;
        discrete = false;
    }
    public AnimationParameters(float frameWidth, float frameHeight, int[] frameSequence, float delay) {
        setFrameWidth(frameWidth);
        setFrameHeight(frameHeight);
        setCurrentFrame(0);
        setFrameSequence(frameSequence);
        setDelay(delay);
        discrete = false;
        timeSince = 0;
    }
    // this function is what actually accomplishes the animation
    public void changeFrame() {
        setCurrentFrame(currentFrame+1);
        timeSince = 0;
    }
    public int getDrawStartX() {
        if (frameSequence == null) {
            return 0;
        } else {
            return (int)(frameSequence[2*currentFrame] * frameWidth);
        }
    }
    public int getDrawStartY() {
        if (frameSequence == null) {
            return 0;
        } else {
            return (int)(frameSequence[2*currentFrame+1] * frameHeight);
        }
    }

}
