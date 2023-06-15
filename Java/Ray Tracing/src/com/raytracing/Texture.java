package com.raytracing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
    private final double size;

    /**
     * Constructs a checkered Texture made up of the two passed textures.
     * @param odd a com.raytracing.Texture object
     * @param even a com.raytracing.Texture object
     * @param size double value to control the size of the squares in the checkered texture, the larger
     *             this number the smaller the squares will be!
     */
    public CheckerTexture(Texture odd, Texture even, double size) {
        this.odd = odd;
        this.even = even;
        this.size = size;
    }

    /**
     * Constructs a checkered Texture made up of the two passed colors c1 and c2.
     * @param c1 a com.raytracing.Color object
     * @param c2 a com.raytracing.Color object
     * @param size double value to control the size of the squares in the checkered texture, the larger
     *             this number the smaller the squares will be!
     */
    public CheckerTexture(Color c1, Color c2, double size) {
        this.odd = new SolidColor(c1);
        this.even = new SolidColor(c2);
        this.size = size;
    }

    @Override
    public Color value(double u, double v, Point3 p) {
        /*
         We can create a checker texture by noting that the sign of sine and cosine just alternates in a
         regular way, and if we multiply trig functions in all three dimensions, the sign of that product forms
         a 3D checker pattern.
        */

        double sines = Math.sin(size * p.x()) * Math.sin(size * p.y()) * Math.sin(size * p.z());
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

class ImageTexture extends Texture {
    private int[] data;
    private int width;
    private int height;

    public ImageTexture() {
        this.data = null;
        this.width = 0;
        this.height = 0;
    }

    public ImageTexture(String filename) {
        try {
            BufferedImage image = ImageIO.read(new File(filename));
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.data = new int[this.width * this.height];

            this.data = image.getRGB(0, 0, this.width, this.height, this.data, 0, this.width);

        } catch (IOException e) {
            System.err.println("ERROR: Could not load texture image file '" + filename + "'");
            this.width = this.height = 0;
        }
    }

    @Override
    public Color value(double u, double v, Point3 p) {
        // if we have no texture data, then return solid cyan as a debugging aid.
        if (data == null) {
            return new Color(0, 1, 1);
        }

        // clamp input texture coordinates to [0,1] x [1,0]
        u = Utility.clamp(u, 0, 1);
        v = 1 - Utility.clamp(v, 0, 1); // flip V to image coordinates

        int i = (int) (u * this.width);
        int j = (int) (v * this.height);

        // clamp integer mapping, since actual coordinates should be less than 1
        if (i >= this.width) i = this.width - 1;
        if (j >= this.height) j = this.height - 1;

        int pixel = this.data[j * this.width + i];
        double colorScale = (double) 1 / 255;

        int red = pixel >> 16 & 0xFF;
        int green = pixel >> 8 & 0xFF;
        int blue = pixel & 0xFF;

        return new Color(colorScale * red, colorScale * green, colorScale * blue);
    }
}