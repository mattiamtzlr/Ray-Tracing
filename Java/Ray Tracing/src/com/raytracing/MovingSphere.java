package com.raytracing;

public class MovingSphere implements Hittable {
    private final Point3 center0, center1;
    private final double time0, time1;
    private final double radius;
    private final Material material;

    public MovingSphere(Point3 center0, Point3 center1, double time0, double time1, double r, Material material) {
        this.center0 = center0;
        this.center1 = center1;
        this.time0 = time0;
        this.time1 = time1;
        this.radius = r;
        this.material = material;
    }

    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        Vec3 oc = Vec3.sub(r.getOrigin(), center(r.getTime()));
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
        // normal = (rec.p - center(r.time())) / radius
        Vec3 outwardNormal = Vec3.div(
            Vec3.sub(rec.getP(), center(r.getTime())),
            radius);
        rec.setFaceNormal(r, outwardNormal);

        return true;
    }

    private Point3 center(double time) {
        // return center0 + ((time - time0) / (time1 - time0))*(center1 - center0);
        // lerp between center0 and center1 based on the time

        return Vec3.add(
            this.center0,
            Vec3.mul(
                Vec3.sub(this.center1, this.center0),
                (time - time0) / (time1 - time0)
            )
        ).toPoint3();
    }
}
