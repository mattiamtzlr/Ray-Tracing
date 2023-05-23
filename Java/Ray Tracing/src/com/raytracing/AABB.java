package com.raytracing;

public class AABB {
    /* axis-aligned bounding boxes:
        using "slabs" we define a bounding box around all objects, if a ray doesn't even hit said bounding box,
        we don't bother checking if it hits any objects inside thus saving time.
     */

    private Point3 minimum;
    private Point3 maximum;

    public AABB() {
        this.minimum = new Point3();
        this.maximum = new Point3();
    }

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

    public void setMinimum(Point3 minimum) {
        this.minimum = minimum;
    }

    public void setMaximum(Point3 maximum) {
        this.maximum = maximum;
    }

    public void set(AABB aabb) {
        this.minimum = aabb.getMinimum();
        this.maximum = aabb.getMaximum();
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

    public static AABB surroundingBox(AABB box0, AABB box1) {
        Point3 small = new Point3(
            Math.min(box0.getMinimum().x(), box1.getMinimum().x()),
            Math.min(box0.getMinimum().y(), box1.getMinimum().y()),
            Math.min(box0.getMinimum().z(), box1.getMinimum().z())
        );

        Point3 big = new Point3(
            Math.max(box0.getMaximum().x(), box1.getMaximum().x()),
            Math.max(box0.getMaximum().y(), box1.getMaximum().y()),
            Math.max(box0.getMaximum().z(), box1.getMaximum().z())
        );

        return new AABB(small, big);
    }
}
