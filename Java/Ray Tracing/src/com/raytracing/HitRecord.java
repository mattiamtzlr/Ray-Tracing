package com.raytracing;

public class HitRecord {
    // HitRecord class: keeps track of elements that ray intersects with
    private Point3 p;
    private Vec3 normal;

    private Material material;
    private double t;
    // u, v surface coordinates for textures
    private double u;
    private double v;
    private boolean frontFace;

    public HitRecord() {
        this.p = null;
        this.normal = null;
        this.material = null;
        this.t = 0;
        this.u = 0;
        this.v = 0;
        this.frontFace = false;
    }

    public void setFaceNormal(Ray r, Vec3 outwardNormal) {
        this.frontFace = Vec3.dot(r.getDirection(), outwardNormal) < 0;
        this.normal = this.frontFace ? outwardNormal : outwardNormal.negate();
    }

    // setters
    public void set(HitRecord hitRecord) { // complete setter UPDATE WHEN NEW FIELD
        this.p = hitRecord.getP();
        this.normal = hitRecord.getNormal();
        this.material = hitRecord.getMaterial();
        this.t = hitRecord.getT();
        this.u = hitRecord.getU();
        this.v = hitRecord.getV();
        this.frontFace = hitRecord.isFrontFace();
    }

    public void setP(Point3 p) {
        this.p = p;
    }
    public void setNormal(Vec3 normal) {
        this.normal = normal;
    }
    public void setMaterial(Material material) {
        this.material = material;
    }
    public void setT(double t) {
        this.t = t;
    }
    public void setU(double u) {
        this.u = u;
    }
    public void setV(double v) {
        this.v = v;
    }
    public void setFrontFace(boolean frontFace) {
        this.frontFace = frontFace;
    }

    // getters
    public Point3 getP() {
        return p;
    }
    public Vec3 getNormal() {
        return normal;
    }
    public Material getMaterial() {
        return material;
    }
    public double getT() {
        return t;
    }
    public double getU() {
        return u;
    }
    public double getV() {
        return v;
    }
    public boolean isFrontFace() {
        return frontFace;
    }
}
