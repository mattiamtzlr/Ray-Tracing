package com.raytracing;

import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // render boolean => true for a good render
        boolean render = true;

        // image properties
        final double aspectRatio = (double) 16 / 9;
        final int imageWidth = render ? 1000 : 400;
        final int imageHeight = (int) (imageWidth / aspectRatio);
        final int samplesPerPixel = render ? 80 : 5;
        final int maxDepth = render ? 80 : 15;

        // world
        HittableList world = new HittableList();

        Material materialGround = new Lambertian(Utility.strToColor("119, 189, 43"));

        Material materialLeft = new Dielectric( 1.5, Utility.strToColor("255, 150, 150"));
        Material materialCenter = new Lambertian(Utility.strToColor("56, 84, 194"));
        Material materialRight = new Metal(Utility.strToColor("255, 172, 64"), 0.3);

        Material materialMirror = new Metal(new Color(0.7, 0.7, 0.7), 0);


        world.add(new Sphere(new Point3(0, -100.5, -10), 100, materialGround)); // "ground"

         // spheres next to each other
        world.add(new Sphere(new Point3(-1, 0, -1), 0.5, materialLeft)); // hollow glass sphere
        world.add(new Sphere(new Point3(-1, 0, -1), -0.4, materialLeft));

        world.add(new Sphere(new Point3(0, 0, -1), 0.5, materialCenter));
        world.add(new Sphere(new Point3(1, 0, -1), 0.5, materialRight));

        world.add(new Sphere(new Point3(0, 0, -7), 5, materialMirror));

        // camera
        boolean fromFront = false;

        Point3 lookFrom = fromFront ? new Point3(0, 0, 0) : new Point3(-2.5, 2.5, 1);
        Point3 lookAt = new Point3(0, 0, -1);
        Vec3 viewUp = new Vec3(0, 1, 0); // horizontally level view

        double vFOV = fromFront ? 90 : 40;

        double distToFocus = Vec3.sub(lookFrom, lookAt).length();
        double aperture = 0.5;

        Camera cam = new Camera(lookFrom, lookAt, viewUp, vFOV, aspectRatio, aperture, distToFocus);

        // render to ppm image format
        String fileName = "output.ppm";

        try {
            FileWriter writer = new FileWriter(fileName);
            StringBuilder output = new StringBuilder();
            output.append(String.format("P3\n%d %d\n255\n", imageWidth, imageHeight)); // header of file

            for (int j = imageHeight-1; j >= 0 ; j--) {
                // progress
                System.out.print("\rScanlines remaining: " + j + " --- " +
                    (int) (((double) (imageHeight - j) / imageHeight) * 100) + "% completed");
                System.out.flush();

                for (int i = 0; i < imageWidth; i++) {
                    Color pixelColor = new Color(0, 0, 0);
                    // multiple samples per pixel
                    for (int s = 0; s < samplesPerPixel; s++) {
                        double u = (i + Utility.randomDouble()) / (imageWidth - 1);
                        double v = (j + Utility.randomDouble()) / (imageHeight - 1);
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
            Ray scattered = new Ray();
            Color attenuation = new Color();
            if (rec.getMaterial().scatter(r, rec, attenuation, scattered)) {
                return Vec3.mul(attenuation, rayColor(scattered, world, depth - 1)).toColor();
            }
            return new Color(0, 0, 0);
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

        // return the translated (0 to 256) value of each component
        return String.format("%d %d %d\n",
                (int) (256 * Utility.clamp(r, 0, 0.999)),
                (int) (256 * Utility.clamp(g, 0, 0.999)),
                (int) (256 * Utility.clamp(b, 0, 0.999))
        );
    }
}