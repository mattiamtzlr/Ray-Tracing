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

class RotateY implements Hittable {
    private final Hittable hittable;
    private final double sinTheta;
    private final double cosTheta;
    private final boolean hasBox;
    private final AABB bBox;

    public RotateY(Hittable hittable1, double angle) {
        this.hittable = hittable1;
        this.bBox = new AABB();

        double radians = Utility.degToRad(angle);
        this.sinTheta = Math.sin(radians);
        this.cosTheta = Math.cos(radians);
        this.hasBox = hittable.boundingBox(0, 1, bBox);

        Point3 min = new Point3(Utility.Infinity, Utility.Infinity, Utility.Infinity);
        Point3 max = new Point3(-Utility.Infinity, -Utility.Infinity, -Utility.Infinity);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    double x = i * bBox.getMaximum().x() + (1 - i) * bBox.getMinimum().x();
                    double y = j * bBox.getMaximum().y() + (1 - j) * bBox.getMinimum().y();
                    double z = k * bBox.getMaximum().z() + (1 - k) * bBox.getMinimum().z();

                    double newX =  cosTheta * x + sinTheta * z;
                    double newZ = -sinTheta * x + cosTheta * z;

                    Vec3 tester = new Vec3(newX, y, newZ);

                    for (int c = 0; c < 3; c++) {
                        min.setComp(c, Math.min(min.getComp(c), tester.getComp(c))); // min[c] = min(min[c], tester[c]);
                        max.setComp(c, Math.max(max.getComp(c), tester.getComp(c))); // max[c] = max(max[c], tester[c]);
                    }
                }
            }
        }

        this.bBox.set(new AABB(min, max));
    }

    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        Point3 origin = new Point3();
        origin.set(r.getOrigin());

        Vec3 direction = new Vec3();
        direction.set(r.getDirection());

        // origin[0] = cosTheta * r.origin()[0] - sinTheta * r.origin()[2];
        origin.setComp(
            0,
            cosTheta * r.getOrigin().getComp(0) - sinTheta * r.getOrigin().getComp(2)
        );
        // origin[2] = sinTheta * r.origin()[0] + cosTheta * r.origin()[2];
        origin.setComp(
            2,
            sinTheta * r.getOrigin().getComp(0) + cosTheta * r.getOrigin().getComp(2)
        );

        // direction[0] = cosTheta * r.direction()[0] - sinTheta * r.direction()[2];
        direction.setComp(
            0,
            cosTheta * r.getDirection().getComp(0) - sinTheta * r.getDirection().getComp(2)
        );
        // direction[2] = sinTheta * r.direction()[0] + cosTheta * r.direction()[2];
        direction.setComp(
            2,
            sinTheta * r.getDirection().getComp(0) + cosTheta * r.getDirection().getComp(2)
        );

        Ray rotatedR = new Ray(origin, direction, r.getTime());

        if (!hittable.hit(rotatedR, tMin, tMax, rec))
            return false;

        Point3 p = new Point3();
        p.set(rec.getP());

        Vec3 normal = new Vec3();
        normal.set(rec.getNormal());

        // p[0] =  cosTheta * rec.p[0] + sinTheta * rec.p[2];
        p.setComp(
            0,
            cosTheta * rec.getP().getComp(0) + sinTheta * rec.getP().getComp(2)
        );
        // p[2] = -sinTheta * rec.p[0] + cosTheta * rec.p[2];
        p.setComp(
            2,
            -sinTheta * rec.getP().getComp(0) + cosTheta * rec.getP().getComp(2)
        );

        // normal[0] =  cosTheta * rec.normal[0] + sinTheta * rec.normal[2];
        normal.setComp(
            0,
            cosTheta * rec.getNormal().getComp(0) + sinTheta * rec.getNormal().getComp(2)
        );
        // normal[2] = -sinTheta * rec.normal[0] + cosTheta * rec.normal[2];
        normal.setComp(
            2,
            -sinTheta * rec.getNormal().getComp(0) + cosTheta * rec.getNormal().getComp(2)
        );

        rec.setP(p);
        rec.setFaceNormal(rotatedR, normal);

        return true;
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        outputBox.set(bBox);
        return hasBox;
    }
}
