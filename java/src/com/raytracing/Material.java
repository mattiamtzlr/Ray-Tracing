package com.raytracing;

public abstract class Material {
    public abstract boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered);
    public Color emitted(double u, double v, Point3 p) {
        return new Color(0, 0, 0);
    }
}

class Lambertian extends Material {
    // lambertian (diffuse) material
    public Texture albedo;

    public Lambertian(Color albedo) {
        this.albedo = new SolidColor(albedo);
    }

    public Lambertian(Texture albedo) {
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
        scattered.setTime(rIn.getTime());

        attenuation.set(this.albedo.value(rec.getU(), rec.getV(), rec.getP()));
        return true;
    }
}

class Metal extends Material {
    public Texture albedo;
    public double fuzz; // makes fuzzy reflections by choosing the endpoint of the ray randomly in a small sphere

    public Metal(Color albedo, double fuzz) {
        this.albedo = new SolidColor(albedo);
        this.fuzz = (fuzz < 1) ? fuzz : 1;
    }

    public Metal(Texture texture, double fuzz) {
        this.albedo = texture;
        this.fuzz = (fuzz < 1) ? fuzz : 1;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered) {
        Vec3 reflected = Vec3.reflect(Vec3.unitVector(rIn.getDirection()), rec.getNormal());

        scattered.setOrigin(rec.getP());
        scattered.setDirection(Vec3.add(reflected, Vec3.mul(Vec3.randomInUnitSphere(), this.fuzz)));
        scattered.setTime(rIn.getTime());

        attenuation.set(this.albedo.value(rec.getU(), rec.getV(), rec.getP()));
        return (Vec3.dot(scattered.getDirection(), rec.getNormal()) > 0);
    }
}

class Dielectric extends Material {
    public double ir; // index of refraction
    public Color albedo;

    public Dielectric(double ir) {
        this.ir = ir;
        this.albedo = new Color(1, 1, 1);
    }

    // overloading to allow colored spheres
    public Dielectric(double ir, Color albedo) {
        this.ir = ir;
        this.albedo = albedo;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered) {
        // also a lot of scary math
        attenuation.set(this.albedo);
        double refractionRatio = rec.isFrontFace() ? (1 / this.ir) : this.ir;

        Vec3 unitDirection = Vec3.unitVector(rIn.getDirection());
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
        scattered.setTime(rIn.getTime());
        return true;
    }

    private static double reflectance(double cosine, double refIdx) {
        // Schlick's approximation for reflectance
        double r0 = (1 - refIdx) / (1 + refIdx);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow((1 - cosine), 5);
    }
}

class DiffuseLight extends Material {
    private final Texture emit;

    public DiffuseLight(Color color) {
        this.emit = new SolidColor(color);
    }
    public DiffuseLight(Texture texture) {
        this.emit = texture;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered) {
        return false;
    }

    @Override
    public Color emitted(double u, double v, Point3 p) {
        return emit.value(u, v, p);
    }
}

class Isotropic extends Material {
    private Texture albedo;

    public Isotropic(Color color) {
        albedo = new SolidColor(color);
    }
    public Isotropic(Texture texture) {
        albedo = texture;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, Color attenuation, Ray scattered) {
        scattered.setOrigin(rec.getP());
        scattered.setDirection(Vec3.randomInUnitSphere());
        scattered.setTime(rIn.getTime());

        attenuation.set(albedo.value(rec.getU(), rec.getV(), rec.getP()));
        return true;
    }
}