package com.raytracing;
class XYRect implements Hittable {
    private final Material material;
    private final double x0, x1, y0, y1, k;

    public XYRect(double x0, double x1, double y0, double y1, double k, Material material) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
        this.k = k;
        this.material = material;
    }

    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        double t = (k - r.getOrigin().z()) / r.getDirection().z();
        if (t < tMin || t > tMax)
            return false;

        double x = r.getOrigin().x() + (t * r.getDirection().x());
        double y = r.getOrigin().y() + (t * r.getDirection().y());
        if (x < x0 || x > x1 || y < y0 || y > y1)
            return false;

        rec.setU((x - x0) / (x1 - x0));
        rec.setV((y - y0) / (y1 - y0));
        rec.setT(t);

        Vec3 outwardNormal = new Vec3(0, 0, 1);
        rec.setFaceNormal(r, outwardNormal);
        rec.setMaterial(material);
        rec.setP(r.at(t));

        return true;
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        // The bounding box must have non-zero width in each dimension, so pad the Z dimension a small amount.
        outputBox.set(new AABB(
            new Point3(x0, y0, k - 0.0001),
            new Point3(x1, y1, k + 0.0001)
        ));
        return true;
    }
}

class XZRect implements Hittable {
    private final Material material;
    private final double x0, x1, z0, z1, k;

    public XZRect(double x0, double x1, double z0, double z1, double k, Material material) {
        this.x0 = x0;
        this.x1 = x1;
        this.z0 = z0;
        this.z1 = z1;
        this.k = k;
        this.material = material;
    }

    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        double t = (k - r.getOrigin().y()) / r.getDirection().y();
        if (t < tMin || t > tMax)
            return false;

        double x = r.getOrigin().x() + (t * r.getDirection().x());
        double z = r.getOrigin().z() + (t * r.getDirection().z());
        if (x < x0 || x > x1 || z < z0 || z > z1)
            return false;

        rec.setU((x - x0) / (x1 - x0));
        rec.setV((z - z0) / (z1 - z0));
        rec.setT(t);

        Vec3 outwardNormal = new Vec3(0, 1, 0);
        rec.setFaceNormal(r, outwardNormal);
        rec.setMaterial(material);
        rec.setP(r.at(t));

        return true;
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        // The bounding box must have non-zero width in each dimension, so pad the Z dimension a small amount.
        outputBox.set(new AABB(
            new Point3(x0, k - 0.0001, z0),
            new Point3(x1, k + 0.0001, z1)
        ));
        return true;
    }
}

class YZRect implements Hittable {
    private final Material material;
    private final double y0, y1, z0, z1, k;

    public YZRect(double y0, double y1, double z0, double z1, double k, Material material) {
        this.y0 = y0;
        this.y1 = y1;
        this.z0 = z0;
        this.z1 = z1;
        this.k = k;
        this.material = material;
    }

    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        double t = (k - r.getOrigin().x()) / r.getDirection().x();
        if (t < tMin || t > tMax)
            return false;

        double y = r.getOrigin().y() + (t * r.getDirection().y());
        double z = r.getOrigin().z() + (t * r.getDirection().z());
        if (y < y0 || y > y1 || z < z0 || z > z1)
            return false;

        rec.setU((y - y0) / (y1 - y0));
        rec.setV((z - z0) / (z1 - z0));
        rec.setT(t);

        Vec3 outwardNormal = new Vec3(1, 0, 0);
        rec.setFaceNormal(r, outwardNormal);
        rec.setMaterial(material);
        rec.setP(r.at(t));

        return true;
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        // The bounding box must have non-zero width in each dimension, so pad the Z dimension a small amount.
        outputBox.set(new AABB(
            new Point3(k - 0.0001, y0, z0),
            new Point3(k + 0.0001, y1, z1)
        ));
        return true;
    }
}