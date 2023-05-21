package com.raytracing;

public class AABB {
    /* axis-aligned bounding boxes:
        using "slabs" we define a bounding box around all objects, if a ray doesn't even hit said bounding box,
        we don't bother checking if it hits any objects inside thus saving time.
     */

    private final Point3 minimum;
    private final Point3 maximum;


    public AABB(Point3 a, Point3 b) {
        this.minimum = a;
        this.maximum = b;
    }

    public Point3 getMinimum() {
        return this.minimum;
    }

    public Point3 getMaximum() {
        return this.maximum;
    }

    public boolean hit(Ray r, double tMin, double tMax) {
        for (int a = 0; a < 3; a++) {
            double t0 = Math.min(
                (this.minimum.getComp(a) - r.getOrigin().getComp(a)) / r.getDirection().getComp(a),
                (this.maximum.getComp(a) - r.getOrigin().getComp(a)) / r.getDirection().getComp(a)
            );
            double t1 = Math.max(
                (this.minimum.getComp(a) - r.getOrigin().getComp(a)) / r.getDirection().getComp(a),
                (this.maximum.getComp(a) - r.getOrigin().getComp(a)) / r.getDirection().getComp(a)
            );

            tMin = Math.max(t0, tMin);
            tMax = Math.min(t1, tMax);

            if (tMax <= tMin) return false;
        }
        return true;
    }
}
