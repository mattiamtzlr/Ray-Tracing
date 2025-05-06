package com.raytracing;

public class Box implements Hittable {
    private final Point3 boxMin;
    private final Point3 boxMax;
    private final HittableList sides = new HittableList();

    /**
     * Constructs a com.raytracing.Box object which represents a cuboid. The two given points p0 and p1
     * represent the two defining points of the cuboid, separated by the cuboid diagonal in all three dimensions.
     * @param p0 a com.raytracing.Point object representing the first defining point
     * @param p1 a com.raytracing.Point object representing the second defining point
     * @param material a com.raytracing.Material object which will be used to shade the cuboid
     */
    public Box(Point3 p0, Point3 p1, Material material) {
        this.boxMin = p0;
        this.boxMax = p1;

        // "front" and "back"
        sides.add(new XYRect(p0.x(), p1.x(), p0.y(), p1.y(), p1.z(), material));
        sides.add(new XYRect(p0.x(), p1.x(), p0.y(), p1.y(), p0.z(), material));

        // "top" and "bottom"
        sides.add(new XZRect(p0.x(), p1.x(), p0.z(), p1.z(), p1.y(), material));
        sides.add(new XZRect(p0.x(), p1.x(), p0.z(), p1.z(), p0.y(), material));

        // "left" and "right"
        sides.add(new YZRect(p0.y(), p1.y(), p0.z(), p1.z(), p1.x(), material));
        sides.add(new YZRect(p0.y(), p1.y(), p0.z(), p1.z(), p0.x(), material));
    }

    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        return sides.hit(r, tMin, tMax, rec);
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        outputBox.set(new AABB(boxMin, boxMax));
        return true;
    }
}
