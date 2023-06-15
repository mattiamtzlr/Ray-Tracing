package com.raytracing;

public interface Hittable {
    // Hittable Interface: all objects that can be hit by a ray implement this class and
    // must implement a hit method as described below

    boolean hit(Ray r, double tMin, double tMax, HitRecord rec);
    boolean boundingBox(double time0, double time1, AABB outputBox);
}

class Translate implements Hittable {
    private final Hittable hittable;
    private final Vec3 offset;

    public Translate(Hittable hittable, Vec3 displacement) {
        this.hittable = hittable;
        this.offset = displacement;
    }
    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        Ray movedR = new Ray(
            Vec3.sub(r.getOrigin(), offset).toPoint3(), // r.origin - offset
            r.getDirection(),
            r.getTime()
        );
        if (!hittable.hit(movedR, tMin, tMax, rec))
            return false;

        rec.setP(Vec3.add(rec.getP(), offset).toPoint3()); // rec.p += offset
        rec.setFaceNormal(movedR, rec.getNormal());

        return true;
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        if (!hittable.boundingBox(time0, time1, outputBox))
            return false;

        outputBox.set(new AABB(
            Vec3.add(outputBox.getMinimum(), offset).toPoint3(), // outputBox.min += offset
            Vec3.add(outputBox.getMaximum(), offset).toPoint3()  // outputBox.max += offset
        ));
        return true;
    }
}
