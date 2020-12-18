package edu.lewisu.cs.cpsc41000.common;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Boundary implements Collidable {
    private Polygon boundingPoly;
    private Vector2 parallel;
    private void calculateParallel(float x1, float y1, float x2, float y2) {
        float diffX = Math.abs(x1-x2);
        float diffY = Math.abs(y1-y2);
        if (diffX > diffY) {
            parallel = new Vector2(1,0);
        } else {
            parallel = new Vector2(0,1);
        }
    }
    public Vector2 getParallel() {
        return parallel;
    }
    /**
     * defines a boundary with bottom-left corner at (x1,y1) and
     * top right corner at (x2,y2);
     * @param x1 
     * @param y1
     * @param x2
     * @param y2
     */
    public Boundary(float x1, float y1, float x2, float y2) {
        float[] vertices = new float[8];
        vertices[0] = x1;
        vertices[1] = y1;
        vertices[2] = x2;
        vertices[3] = y1;
        vertices[4] = x2;
        vertices[5] = y2;
        vertices[6] = x1;
        vertices[7] = y2;
        boundingPoly = new Polygon(vertices);
        calculateParallel(x1,y1,x2,y2);
    }
    @Override
    public Polygon getBoundingPolygon() {
        return boundingPoly;
    }
}
