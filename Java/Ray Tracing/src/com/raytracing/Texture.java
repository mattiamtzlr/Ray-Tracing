package com.raytracing;

public abstract class Texture {
    public abstract Color value(double u, double v, Point3 p);
}

class SolidColor extends Texture {
    private final Color colorValue;

    public SolidColor() {
        this.colorValue = new Color(0, 0, 0);
    }

    public SolidColor(Color color) {
        this.colorValue = color;
    }

    public SolidColor(double red, double green, double blue) {
        this.colorValue = new Color(red, green, blue);
    }

    @Override
    public Color value(double u, double v, Point3 p) {
        return colorValue;
    }
}

class CheckerTexture extends Texture {
    private final Texture odd;
    private final Texture even;

    public CheckerTexture(Texture odd, Texture even) {
        this.odd = odd;
        this.even = even;
    }

    public CheckerTexture(Color c1, Color c2) {
        this.odd = new SolidColor(c1);
        this.even = new SolidColor(c2);
    }

    @Override
    public Color value(double u, double v, Point3 p) {
        /*
         We can create a checker texture by noting that the sign of sine and cosine just alternates in a
         regular way, and if we multiply trig functions in all three dimensions, the sign of that product forms
         a 3D checker pattern.
        */

        double sines = Math.sin(10 * p.x()) * Math.sin(10 * p.y()) * Math.sin(10 * p.z());
        if (sines < 0)
            return odd.value(u, v, p);
        else
            return even.value(u, v, p);
    }
}

class NoiseTexture extends Texture {
    private final Color albedo;
    private final Perlin perlin = new Perlin();
    private final double scale;

    public NoiseTexture() {
        this.albedo = new Color(1, 1, 1);
        this.scale = 1.0;
    }
    public NoiseTexture(double scale) {
        this.albedo = new Color(1, 1, 1);
        this.scale = scale;
    }
    public NoiseTexture(Color albedo, double scale) {
        this.albedo = albedo;
        this.scale = scale;
    }

    @Override
    public Color value(double u, double v, Point3 p) {
        return Vec3.mul(
            Vec3.mul(
                this.albedo,
                0.5
            ),
            1 + Math.sin(this.scale * p.z() + 10 * perlin.turbulence(p, 7))
        ).toColor();
    }
}