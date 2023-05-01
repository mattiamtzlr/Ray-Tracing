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
        // use either randomUnitVector() or randomInHemisphere(rec.getNormal()) as second parameter
        Vec3 scatterDirection = Vec3.add(rec.getNormal(), Vec3.randomUnitVector());

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
