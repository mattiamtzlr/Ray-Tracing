package com.raytracing;

import java.awt.*;

public class Camera {
    private final Point3 origin;
    private final Vec3 horizontal;
    private final Vec3 vertical;
    private final Vec3 lowerLeftCorner;

    public Camera(Point3 lookFrom, Point3 lookAt, Vec3 viewUp, double vFOV, double aspectRatio) {
        // vFOV = vertical FOV
        double theta = Utility.degToRad(vFOV); // angle between z plane and viewing vector
        double h = Math.tan(theta / 2); // height between z plane and viewing vector at z = -1
        double viewportHeight = 2 * h;
        double viewportWidth = aspectRatio * viewportHeight;

        // https://raytracing.github.io/images/fig-1.16-cam-view-up.jpg
        Vec3 w = Vec3.unitVector(Vec3.sub(lookFrom, lookAt));
        Vec3 u = Vec3.unitVector(Vec3.cross(viewUp, w));
        Vec3 v = Vec3.cross(w, u);

        this.origin = lookFrom;
        this.horizontal = Vec3.mul(u, viewportWidth);
        this.vertical = Vec3.mul(v, viewportHeight);

        // lowerLeftCorner = origin - horizontal/2 - vertical/2 - w;
        this.lowerLeftCorner = Vec3.sub(
            Vec3.sub(
                Vec3.sub(
                    this.origin,
                    Vec3.div(this.horizontal, 2)
                ),
                Vec3.div(this.vertical, 2)
            ),
            w
        );
    }

    public Ray getRay(double s, double t) {
        // return Ray(origin, lowerLeftCorner + s*horizontal + t*vertical - origin);
        return new Ray(this.origin,
            Vec3.add(
                Vec3.add(this.lowerLeftCorner, Vec3.mul(this.horizontal, s)),
                Vec3.sub(Vec3.mul(this.vertical, t), this.origin)
            )
        );
    }
}
