package com.raytracing;

public class ConstantMedium implements Hittable {
    private final Hittable boundary;
    private final double negInvDensity;
    private final Material phaseFunction;

    public ConstantMedium(Hittable boundary, double density, Texture texture) {
        this.boundary = boundary;
        negInvDensity = -1 / density;
        phaseFunction = new Isotropic(texture);
    }

    public ConstantMedium(Hittable boundary, double density, Color color) {
        this.boundary = boundary;
        negInvDensity = -1 / density;
        phaseFunction = new Isotropic(color);
    }

    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        HitRecord rec1 = new HitRecord(), rec2 = new HitRecord();

        if (!boundary.hit(r, -Utility.Infinity, Utility.Infinity, rec1))
            return false;
        if (!boundary.hit(r, rec1.getT() + 0.0001, Utility.Infinity, rec2))
            return false;

        if (rec1.getT() < tMin) rec1.setT(tMin);
        if (rec2.getT() > tMax) rec2.setT(tMax);

        if (rec1.getT() >= rec2.getT())
            return false;

        if (rec1.getT() < 0)
            rec1.setT(0);

        final double rayLength = r.getDirection().length();
        final double distanceInsideBoundary = (rec2.getT() - rec1.getT()) * rayLength;
        final double hitDistance = negInvDensity * Math.log(Utility.randomDouble());

        if (hitDistance > distanceInsideBoundary)
            return false;

        rec.setT(rec1.getT() + hitDistance / rayLength);
        rec.setP(r.at(rec.getT()));

        rec.setNormal(new Vec3(1, 0, 0)); // arbitrary
        rec.setFrontFace(true);                      // also arbitrary
        rec.setMaterial(phaseFunction);

        return true;
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        return boundary.boundingBox(time0, time1, outputBox);
    }
}
