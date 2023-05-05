package com.raytracing;

public abstract class Material {
    public abstract boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered);
}

class Lambertian extends Material {
    // lambertian (diffuse) material
    public Color albedo;

    public Lambertian(Color albedo) {
        this.albedo = albedo;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered) {
        // use either randomInUnitSphere(), randomUnitVector() or randomInHemisphere(rec.getNormal())
        // as second parameter
        Vec3 scatterDirection = Vec3.add(rec.getNormal(), Vec3.randomInUnitSphere());

        // catch near zero scatter direction
        if (scatterDirection.nearZero()) {
            scatterDirection = rec.getNormal();
        }

        scattered.setOrigin(rec.getP());
        scattered.setDirection(scatterDirection);

        attenuation.set(this.albedo);
        return true;
    }
}

class Metal extends Material {
    public Color albedo;
    public double fuzz; // makes fuzzy reflections by choosing the endpoint of the ray randomly in a small sphere

    public Metal(Color albedo, double fuzz) {
        this.albedo = albedo;
        this.fuzz = (fuzz < 1) ? fuzz : 1;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered) {
        Vec3 reflected = Vec3.reflect(Vec3.unitVector(rIn.direction()), rec.getNormal());

        scattered.setOrigin(rec.getP());
        scattered.setDirection(Vec3.add(reflected, Vec3.mul(Vec3.randomInUnitSphere(), this.fuzz)));

        attenuation.set(this.albedo);
        return (Vec3.dot(scattered.direction(), rec.getNormal()) > 0);
    }
}

class Dielectric extends Material {
    public double ir; // index of refraction

    public Dielectric(double ir) {
        this.ir = ir;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered) {
        // also a lot of scary math
        attenuation.set(new Color(1, 1, 1));
        double refractionRatio = rec.isFrontFace() ? (1 / this.ir) : this.ir;

        Vec3 unitDirection = Vec3.unitVector(rIn.direction());
        // only refract if possible, meaning as long as not inside the sphere
        double cosTheta = Math.min(Vec3.dot(unitDirection.negate(), rec.getNormal()), 1);
        double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);

        boolean cannotRefract = refractionRatio * sinTheta > 1;
        Vec3 direction = new Vec3();

        if (cannotRefract || reflectance(cosTheta, refractionRatio) > Utility.randomDouble()) {
            direction.set(Vec3.reflect(unitDirection, rec.getNormal()));
        } else {
            direction.set(Vec3.refract(unitDirection, rec.getNormal(), refractionRatio));
        }

        scattered.setOrigin(rec.getP());
        scattered.setDirection(direction);
        return true;
    }

    private static double reflectance(double cosine, double refIdx) {
        // Schlick's approximation for reflectance
        double r0 = (1 - refIdx) / (1 + refIdx);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow((1 - cosine), 5);
    }
}