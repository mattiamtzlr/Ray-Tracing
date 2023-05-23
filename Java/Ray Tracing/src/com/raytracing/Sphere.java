package com.raytracing;

public class Sphere implements Hittable {

    private final Point3 center;
    private final double radius;
    private final Material material;

    public Sphere(Point3 center, double radius, Material material) {
        this.center = center;
        this.radius = radius;
        this.material = material;
    }

    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        Vec3 oc = Vec3.sub(r.getOrigin(), this.center);
        double a = r.getDirection().lengthSquared();
        double h = Vec3.dot(oc, r.getDirection());
        double c = oc.lengthSquared() - (this.radius * this.radius);

        // value under sqrt, if > 0 => ray intersects
        double discriminant = (h * h) - (a * c);
        if (discriminant < 0) {
            return false;
        }

        double sqrtD = Math.sqrt(discriminant);
        // find the nearest root between tMin and tMax by trying both plus and minus calculations
        double root = (-h - sqrtD) / a;
        if (root < tMin || root > tMax) {
            root = (-h + sqrtD) / a;
            if (root < tMin || root > tMax) {
                return false; // root not in range
            }
        }

        rec.setT(root);
        rec.setP(r.at(rec.getT()));
        rec.setMaterial(this.material);
        // normal = (rec.p - center) / radius
        Vec3 outwardNormal = Vec3.div(Vec3.sub(rec.getP(), this.center), radius);
        rec.setFaceNormal(r, outwardNormal);

        return true;
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        // bounding box for a sphere is really easy => one "corner" to the diagonal "corner"
        outputBox.setMinimum(Vec3.sub(this.center, new Vec3(radius, radius, radius)).toPoint3());
        outputBox.setMaximum(Vec3.add(this.center, new Vec3(radius, radius, radius)).toPoint3());

        return true;
    }
}
