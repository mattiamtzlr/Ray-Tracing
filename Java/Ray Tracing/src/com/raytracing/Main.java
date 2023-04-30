package com.raytracing;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // image properties
        final double aspectRatio = (double) 16 / 9;
        final int imageWidth = 400;
        final int imageHeight = (int) (imageWidth / aspectRatio);
        final int samplesPerPixel = 5;
        final int maxDepth = 20;

        // world
        HittableList world = new HittableList();
        world.add(new Sphere(new Point3(-0.5, 0, -1.2), 0.5));
        world.add(new Sphere(new Point3(-0.3, .6, -0.9), 0.15));
        world.add(new Sphere(new Point3(4, 1.5, -7), 1));
        world.add(new Sphere(new Point3(0, -200.5, -10), 200)); // "ground"

        // camera
        Camera cam = new Camera();
    
        // render to ppm image format
        String fileName = "output.ppm";

        try {
            FileWriter writer = new FileWriter(fileName);
            StringBuilder output = new StringBuilder();
            output.append(String.format("P3\n%d %d\n255\n", imageWidth, imageHeight)); // header of file

            for (int j = imageHeight-1; j >= 0 ; j--) {
                // progress
                System.out.print("\rScanlines remaining: " + j + " - " + (imageHeight - j) / imageHeight * 100 + "% ");
                System.out.flush();

                for (int i = 0; i < imageWidth; i++) {
                    Color pixelColor = new Color(0, 0, 0);
                    // multiple samples per pixel
                    for (int s = 0; s < samplesPerPixel; s++) {
                        double u = (i + Utility.randomFloat()) / (imageWidth - 1);
                        double v = (j + Utility.randomFloat()) / (imageHeight - 1);
                        Ray r = cam.getRay(u, v);
                        pixelColor = Vec3.add(pixelColor, rayColor(r, world, maxDepth)).toColor();
                    }
                    output.append(writeColor(pixelColor, samplesPerPixel));
                }
            }           
            
            writer.write(output.toString());
            writer.close();
            System.out.printf("\nSuccessfully wrote to '%s'.\n", fileName);

        } catch (IOException e) {
            System.out.printf("Error while writing to '%s'.\n", fileName);
            e.printStackTrace();
        }

    }

    // function to figure out what color the ray returns
    public static Color rayColor(Ray r, Hittable world, int depth) {
        // recursion safeguard
        if (depth <= 0) {
            return new Color(0, 0, 0); // black
        }

        // HitRecord from world with hit info of all objects
        HitRecord rec = new HitRecord();
        if (world.hit(r, 0.001, Utility.Infinity, rec)) {
            // random bounce to simulate diffuse material
            Vec3 target = Vec3.add(
                Vec3.add(
                    rec.getP(),
                    rec.getNormal()
                ),
                Vec3.randomInUnitSphere()
            );

            // use rayColor recursively with the new target ray
            // return 0.5 * rayColor(ray(rec.p, target - rec.p), world)
            return Vec3.mul(
                rayColor(
                    new Ray(
                        rec.getP(),
                        Vec3.sub(target, rec.getP())
                    ),
                    world,
                    depth-1
                ),
                0.5
            ).toColor();
        }

        // if the ray doesn't intersect anything, color the sky normally
        Vec3 unitDirection = Vec3.unitVector(r.direction());
        double t = 0.5 * (unitDirection.y() + 1);
        // return (1 - t) * Color(1, 1, 1) + t * Color(0.5, 0.7, 1.0)
        return Vec3.add(
            Vec3.mul(new Color(1, 1, 1), (1 - t)),
            Vec3.mul(new Color(0.5, 0.7, 1), t)
        ).toColor();
    }

    // function to generate a color String, originally in color.py
    public static String writeColor(Color pixelColor, int samplesPerPixel) {
        double r = pixelColor.x();
        double g = pixelColor.y();
        double b = pixelColor.z();

        // divide the color by the number of samples and gamma correct for gamma = 2
        // -> raising color to the power 1/gamma -> 1/2 -> sqrt

        double scale = (double) 1 / samplesPerPixel;
        r = Math.sqrt(scale * r);
        g = Math.sqrt(scale * g);
        b = Math.sqrt(scale * b);

        // return the translated (0 to 255) value of each component
        return String.format("%d %d %d\n",
                (int) (256 * Utility.clamp(r, 0, 0.999)),
                (int) (256 * Utility.clamp(g, 0, 0.999)),
                (int) (256 * Utility.clamp(b, 0, 0.999))
        );
    }
}