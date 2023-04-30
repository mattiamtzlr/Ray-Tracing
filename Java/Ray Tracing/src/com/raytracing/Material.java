package com.raytracing;

public abstract class Material {
    public abstract boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered);
}

class Lambertian extends Material {
    // lambertian (diffuse) material
    public final Color albedo;

    public Lambertian(final Color albedo) {
        this.albedo = albedo;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered) {
        // use either randomUnitVector() or randomInHemisphere(rec.getNormal()) as second parameter
        Vec3 scatterDirection = Vec3.add(rec.getNormal(), Vec3.randomUnitVector());

        // catch near zero scatter direction
        if (scatterDirection.nearZero()) {
            scatterDirection = rec.getNormal();
        }

        scattered = new Ray(rec.getP(), scatterDirection);
        attenuation = this.albedo;
        return true;
    }
}

class Metal extends Material {
    public final Color albedo;

    public Metal(final Color albedo) {
        this.albedo = albedo;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered) {
        Vec3 reflected = Vec3.reflect(Vec3.unitVector(rIn.direction()), rec.getNormal());
        scattered = new Ray(rec.getP(), reflected);
        attenuation = this.albedo;
        return (Vec3.dot(scattered.direction(), rec.getNormal()) > 0);
    }
}