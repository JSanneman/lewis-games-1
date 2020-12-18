package edu.lewisu.cs.cpsc41000.common;

import com.badlogic.gdx.math.Polygon;

public interface Collidable {
    public abstract Polygon getBoundingPolygon();
}
