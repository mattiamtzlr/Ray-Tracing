package com.raytracing;

import java.lang.Math;

public class Vec3 {
    public double[] e = new double[3];

    public Vec3() {
        e = new double[]{0, 0, 0};
    }

    public Vec3(double e0, double e1, double e2) {
        this.e[0] = e0;
        this.e[1] = e1;
        this.e[2] = e2;
    }

    public double x() {
        return this.e[0];
    }

    public double y() {
        return this.e[1];
    }

    public double z() {
        return this.e[2];
    }

    public Vec3 negate() {
        return new Vec3(-this.e[0], -this.e[1], -this.e[2]);
    }

    public double getComp(int comp) {
        return this.e[comp];
    }

    public void setComp(int comp, double val) {
        this.e[comp] = val;
    }

    public void set(Vec3 newVec) {
        this.e[0] = newVec.e[0];
        this.e[1] = newVec.e[1];
        this.e[2] = newVec.e[2];
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double lengthSquared() {
        return this.e[0] * this.e[0] + this.e[1] * this.e[1] + this.e[2] * this.e[2];
    }

    // function to check whether the vector is very close to being zero in any dimension
    public boolean nearZero() {
        final double s = 1e-8;
        return (Math.abs(this.e[0]) < s) && (Math.abs(this.e[1]) < s) && (Math.abs(this.e[2]) < s);
    }

    public String toString() {
        return this.e[0] + " " + this.e[1] + " " + this.e[2];
    }

    public Color toColor() {
        return new Color(this.e[0], this.e[1], this.e[2]);
    }

    public Point3 toPoint3() {
        return new Point3(this.x(), this.y(), this.z());
    }

    // Utility (static)
    public static Vec3 add(Vec3 u, Vec3 v) {
        return new Vec3(u.e[0] + v.e[0], u.e[1] + v.e[1], u.e[2] + v.e[2]);
    }

    public static Vec3 sub(Vec3 u, Vec3 v) {
        return new Vec3(u.e[0] - v.e[0], u.e[1] - v.e[1], u.e[2] - v.e[2]);
    }

    public static Vec3 mul(Vec3 u, Vec3 v) {
        return new Vec3(u.e[0] * v.e[0], u.e[1] * v.e[1], u.e[2] * v.e[2]);
    }
    public static Vec3 mul(Vec3 v, double t) {
        return new Vec3(v.e[0] * t, v.e[1] * t, v.e[2] * t);
    }

    public static Vec3 div(Vec3 v, double t) {
        return mul(v, 1 / t);
    }

    public static double dot(Vec3 u, Vec3 v) {
        return u.e[0] * v.e[0] + u.e[1] * v.e[1] + u.e[2] * v.e[2];
    }

    public static Vec3 cross(Vec3 u, Vec3 v) {
        return new Vec3(
            u.e[1] * v.e[2] - u.e[2] * v.e[1],
            u.e[2] * v.e[0] - u.e[0] * v.e[2],
            u.e[0] * v.e[1] - u.e[1] * v.e[0]
        );
    }

    public static Vec3 reflect(Vec3 v, Vec3 n) {
        // return v - 2 * dot(v, n) * n
        return Vec3.sub(
            v,
            Vec3.mul(n, 2 * dot(v, n))
        );
    }

    public static Vec3 refract(Vec3 uv, final Vec3 n, double etaiOverEtat) {
        // very complicated math that I don't understand, but we ball
        double cosTheta = Math.min(dot(uv.negate(), n), 1);
        // rOutPerp = etaiOverEtat * (uv + cosTheta * n))
        Vec3 rOutPerp = Vec3.mul(
            Vec3.add(uv, Vec3.mul(n, cosTheta)),
            etaiOverEtat
        );
        Vec3 rOutParallel = Vec3.mul(n, -Math.sqrt(Math.abs(1 - rOutPerp.lengthSquared())));
        return Vec3.add(rOutPerp, rOutParallel);
    }

    public static Vec3 unitVector(Vec3 v) {
        return div(v, v.length());
    }

    public static Vec3 random() {
        return new Vec3(Utility.randomDouble(), Utility.randomDouble(), Utility.randomDouble());
    }

    public static Vec3 random(double min, double max) {
        return new Vec3(
                Utility.randomDouble(min, max),
                Utility.randomDouble(min, max),
                Utility.randomDouble(min, max)
        );
    }

    public static Vec3 randomInUnitSphere() {
        while (true) {
            Vec3 p = random(-1, 1);
            if (p.lengthSquared() >= 1) {
                continue;
            }
            return p;
        }
    }

    public static Vec3 randomInUnitDisk() {
        while (true) {
            Vec3 p = new Vec3(Utility.randomDouble(-1, 1), Utility.randomDouble(-1, 1), 0);
            if (p.lengthSquared() >= 1) continue;
            return p;
        }
    }

    // lambertian distribution light scattering
    public static Vec3 randomUnitVector() {
        // normalized random Vector => correct lambertian distribution
        return unitVector(randomInUnitSphere());
    }

    // alternative method of light scattering: hemisphere scattering
    public static Vec3 randomInHemisphere(Vec3 normal) {
        Vec3 inUnitSphere = randomInUnitSphere();
        if (dot(inUnitSphere, normal) > 0) {
            return inUnitSphere;
        } else {
            return inUnitSphere.negate();
        }
    }
}

// Type aliases
class Point3 extends Vec3 {
    public Point3() {
        super();
    }
    public Point3(double x, double y, double z) {
        super(x, y, z);
    }
}

class Color extends Vec3 {
    public Color() {
        super();
    }
    public Color(double r, double g, double b) {
        super(r, g, b);
    }
}

//
