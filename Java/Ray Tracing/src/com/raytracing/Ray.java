package com.raytracing;

public class Ray {
    // Ray with an Origin Point and a direction vector.
    // Changing the parameter t moves the resulting Point along the ray. (Linear interpolation)

    private final Point3 origin;
    private final Vec3 direction;

    public Ray(Point3 origin, Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Point3 origin() {
        return origin;
    }

    public Vec3 direction() {
        return direction;
    }

    public Point3 at(double t) {
        return (Point3) Vec3.add(this.origin, Vec3.mul(this.direction, t));
    }
}
