package com.raytracing;

public class Camera {
    private final Point3 origin;
    private final Vec3 horizontal;
    private final Vec3 vertical;
    private final Vec3 lowerLeftCorner;
    private final Vec3 u;
    private final Vec3 v;
    private final double lensRadius;
    private final double time0, time1;

    public Camera(
        Point3 lookFrom,
        Point3 lookAt,
        Vec3 viewUp,
        double vFOV, // vertical FOV
        double aspectRatio,
        double aperture,
        double focusDist,
        double time0,
        double time1
    ) {

        double theta = Utility.degToRad(vFOV); // angle between z plane and viewing vector
        double h = Math.tan(theta / 2); // height between z plane and viewing vector at z = -1
        double viewportHeight = 2 * h;
        double viewportWidth = aspectRatio * viewportHeight;

        // https://raytracing.github.io/images/fig-1.16-cam-view-up.jpg
        Vec3 w = Vec3.unitVector(Vec3.sub(lookFrom, lookAt));
        this.u = Vec3.unitVector(Vec3.cross(viewUp, w));
        this.v = Vec3.cross(w, this.u);

        this.origin = lookFrom;
        this.horizontal = Vec3.mul(this.u, viewportWidth * focusDist);
        this.vertical = Vec3.mul(this.v, viewportHeight * focusDist);

        // lowerLeftCorner = origin - horizontal/2 - vertical/2 - w * focusDist;
        this.lowerLeftCorner = Vec3.sub(
            Vec3.sub(
                Vec3.sub(
                    this.origin,
                    Vec3.div(this.horizontal, 2)
                ),
                Vec3.div(this.vertical, 2)
            ),
            Vec3.mul(w, focusDist)
        );

        this.lensRadius = aperture / 2;
        this.time0 = time0;
        this.time1 = time1;
    }

    public Ray getRay(double s, double t) {
        Vec3 rd = Vec3.mul(Vec3.randomInUnitDisk(), this.lensRadius);
        // Vec3 offset = u * rd.x() + v * rd.y();
        Vec3 offset = Vec3.add(
            Vec3.mul(this.u, rd.x()),
            Vec3.mul(this.v, rd.y())
        );

        /*  equals
            return Ray(
                origin + offset,
                lowerLeftCorner + s*horizontal + t*vertical - origin - offset,
                randomDouble(time0, time1)
            ); */
        return new Ray(
            Vec3.add(this.origin, offset).toPoint3(),
            Vec3.add(
                Vec3.add(this.lowerLeftCorner, Vec3.mul(this.horizontal, s)),
                Vec3.sub(
                    Vec3.sub(Vec3.mul(this.vertical, t), this.origin),
                    offset
                )
            ),
            Utility.randomDouble(this.time0, this.time1)
        );
    }
}
