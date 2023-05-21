package com.raytracing;

public class Ray {
    // Ray with an Origin Point and a direction vector.
    // Changing the parameter t moves the resulting Point along the ray. (Linear interpolation)

    private Point3 origin;
    private Vec3 direction;
    private double time;

    public Ray() {
        this.origin = new Point3();
        this.direction = new Vec3();
        this.time = 0;
    }

    public Ray(Point3 origin, Vec3 direction, double time) {
        this.origin = origin;
        this.direction = direction;
        this.time = time;
    }

    public Point3 getOrigin() {
        return this.origin;
    }

    public Vec3 getDirection() {
        return this.direction;
    }

    public double getTime() {
        return this.time;
    }

    public void setOrigin(Point3 origin) {
        this.origin = origin;
    }

    public void setDirection(Vec3 direction) {
        this.direction = direction;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Point3 at(double t) {
        return Vec3.add(this.origin, Vec3.mul(this.direction, t)).toPoint3();
    }
}
