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

class RotateX implements Hittable {
    private final Hittable hittable;
    private final double sinTheta;
    private final double cosTheta;
    private final boolean hasBox;
    private final AABB bBox;

    public RotateX(Hittable hittable1, double angle) {
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

                    double newY = cosTheta * y - sinTheta * z;
                    double newZ = sinTheta * y + cosTheta * z;

                    Vec3 tester = new Vec3(x, newY, newZ);

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
        Vec3 direction = new Vec3();

        origin.set(
            r.getOrigin().x(),
             cosTheta * r.getOrigin().y() + sinTheta * r.getOrigin().z(),
            -sinTheta * r.getOrigin().y() + cosTheta * r.getOrigin().z()
        );

        direction.set(
            r.getDirection().x(),
             cosTheta * r.getDirection().y() + sinTheta * r.getDirection().z(),
            -sinTheta * r.getDirection().y() + cosTheta * r.getDirection().z()
        );

        Ray rotatedR = new Ray(origin, direction, r.getTime());

        if (!hittable.hit(rotatedR, tMin, tMax, rec))
            return false;

        Point3 p = new Point3();
        Vec3 normal = new Vec3();

        p.set(
            rec.getP().x(),
            cosTheta * rec.getP().y() - sinTheta * rec.getP().z(),
            sinTheta * rec.getP().y() + cosTheta * rec.getP().z()
        );
        normal.set(
            rec.getNormal().x(),
            cosTheta * rec.getNormal().y() - sinTheta * rec.getNormal().z(),
            sinTheta * rec.getNormal().y() + cosTheta * rec.getNormal().z()
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
        Vec3 direction = new Vec3();

        origin.set(
            cosTheta * r.getOrigin().x() - sinTheta * r.getOrigin().z(),
            r.getOrigin().y(),
            sinTheta * r.getOrigin().x() + cosTheta * r.getOrigin().z()
        );

        direction.set(
            cosTheta * r.getDirection().x() - sinTheta * r.getDirection().z(),
            r.getDirection().y(),
            sinTheta * r.getDirection().x() + cosTheta * r.getDirection().z()
        );

        Ray rotatedR = new Ray(origin, direction, r.getTime());

        if (!hittable.hit(rotatedR, tMin, tMax, rec))
            return false;

        Point3 p = new Point3();
        Vec3 normal = new Vec3();

        p.set(
             cosTheta * rec.getP().x() + sinTheta * rec.getP().z(),
            rec.getP().y(),
            -sinTheta * rec.getP().x() + cosTheta * rec.getP().z()
        );

        normal.set(
             cosTheta * rec.getNormal().x() + sinTheta * rec.getNormal().z(),
            rec.getNormal().y(),
            -sinTheta * rec.getNormal().x() + cosTheta * rec.getNormal().z()
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

class RotateZ implements Hittable {
    private final Hittable hittable;
    private final double sinTheta;
    private final double cosTheta;
    private final boolean hasBox;
    private final AABB bBox;

    public RotateZ(Hittable hittable1, double angle) {
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

                    double newX = cosTheta * x - sinTheta * y;
                    double newY = sinTheta * x + cosTheta * y;

                    Vec3 tester = new Vec3(newX, newY, z);

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
        Vec3 direction = new Vec3();

        origin.set(
             cosTheta * r.getOrigin().x() + sinTheta * r.getOrigin().y(),
            -sinTheta * r.getOrigin().x() + cosTheta * r.getOrigin().y(),
            r.getOrigin().z()
        );

        direction.set(
             cosTheta * r.getDirection().x() + sinTheta * r.getDirection().y(),
            -sinTheta * r.getDirection().x() + cosTheta * r.getDirection().y(),
            r.getDirection().z()
        );

        Ray rotatedR = new Ray(origin, direction, r.getTime());

        if (!hittable.hit(rotatedR, tMin, tMax, rec))
            return false;

        Point3 p = new Point3();
        Vec3 normal = new Vec3();

        p.set(
            cosTheta * rec.getP().x() - sinTheta * rec.getP().y(),
            sinTheta * rec.getP().x() + cosTheta * rec.getP().y(),
            rec.getP().z()
        );

        normal.set(
            cosTheta * rec.getNormal().x() - sinTheta * rec.getNormal().y(),
            sinTheta * rec.getNormal().x() + cosTheta * rec.getNormal().y(),
            rec.getNormal().z()
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