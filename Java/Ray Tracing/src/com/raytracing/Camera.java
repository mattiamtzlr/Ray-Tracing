package com.raytracing;

public class Camera {
    private final Point3 origin;
    private final Vec3 horizontal;
    private final Vec3 vertical;
    private final Vec3 lowerLeftCorner;

    public Camera() {
        final double aspectRatio = (double) 16 / 9;
        final double viewportHeight = 2;
        final double viewportWidth = aspectRatio * viewportHeight;
        final double focalLength = 1;

        this.origin = new Point3(0, 0, 0);
        this.horizontal = new Vec3(viewportWidth, 0, 0);
        this.vertical = new Vec3(0, viewportHeight, 0);

        // lowerLeftCorner = origin - horizontal/2 - vertical/2 - Vec3(0, 0, focalLength);
        this.lowerLeftCorner = Vec3.sub(
            Vec3.sub(
                Vec3.sub(
                    this.origin,
                    Vec3.div(this.horizontal, 2)
                ),
                Vec3.div(this.vertical, 2)
            ),
            new Vec3(0, 0, focalLength)
        );
    }

    public Ray getRay(double u, double v) {
        // return Ray(origin, lowerLeftCorner + u*horizontal + v*vertical - origin);
        return new Ray(this.origin,
            Vec3.add(
                Vec3.add(this.lowerLeftCorner, Vec3.mul(this.horizontal, u)),
                Vec3.sub(Vec3.mul(this.vertical, v), this.origin)
            )
        );
    }
}
