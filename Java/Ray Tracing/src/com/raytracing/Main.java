package com.raytracing;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        Instant start = Instant.now();

        // render boolean => true for a good render
        boolean render = false;

        // image properties
        final double aspectRatio = (double) 16 / 9;
        final int imageWidth = render ? 1000 : 400;
        final int imageHeight = (int) (imageWidth / aspectRatio);
        final int samplesPerPixel = render ? 50 : 5;
        final int maxDepth = render ? 50 : 10;

        // world
        HittableList world = generateScene(true);

        // camera
        Point3 lookFrom = new Point3(12, 2, 4);
        Point3 lookAt = new Point3(0, 0.25, 0);
        Vec3 viewUp = new Vec3(0, 1, 0); // horizontally level view

        double aperture = 0.1;
        double distToFocus = Vec3.sub(lookFrom, lookAt).length();

        Camera cam = new Camera(lookFrom, lookAt, viewUp, 20, aspectRatio, aperture, distToFocus);

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

            Instant finish = Instant.now();
            Duration timeElapsedRaw = Duration.between(start, finish);

            long timeElapsed;
            String timeScale;

            if (timeElapsedRaw.toMinutes() > 1) {
                timeElapsed = timeElapsedRaw.toMinutes();
                timeScale = "minutes";

            } else if (timeElapsedRaw.toSeconds() > 1) {
                timeElapsed = timeElapsedRaw.toSeconds();
                timeScale = "seconds";

            } else {
                timeElapsed = timeElapsedRaw.toMillis();
                timeScale = "milliseconds";
            }

            System.out.printf("\nSuccessfully wrote to '%s' in %d %s.\n", fileName, timeElapsed, timeScale);

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
            Vec3.mul(Utility.strToColor("238, 192, 252"), (1 - t)),
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

    private static HittableList generateScene(boolean smallSpheres) {
        HittableList world = new HittableList();

        Material groundMaterial = new Lambertian(Utility.strToColor("61, 67, 74"));
        world.add(new Sphere(new Point3(0, -1000, 0), 1000, groundMaterial));

        if (smallSpheres) {
            int constraint = 14;

            for (int a = -constraint; a < constraint; a++) {
                for (int b = -constraint; b < constraint; b++) {
                    double chooseMat = Utility.randomDouble();
                    Point3 center = new Point3(a + (0.9 * Utility.randomDouble()), 0.2, b + (0.9 * Utility.randomDouble()));

                    if (Vec3.sub(center, new Point3(4, 0.2, 0)).length() > 0.9) {
                        Material sphereMat;

                        if (chooseMat < 0.6) {
                            // diffuse
                            Color albedo = Vec3.random(0.1, 0.9).toColor();
                            sphereMat = new Lambertian(albedo);
                            world.add(new Sphere(center, 0.2, sphereMat));

                        } else if (chooseMat < 0.8) {
                            // metal
                            Color albedo = Vec3.random(0.5, 1).toColor();
                            double fuzz = Utility.randomDouble(0, 0.5);
                            sphereMat = new Metal(albedo, fuzz);
                            world.add(new Sphere(center, 0.2, sphereMat));

                        } else {
                            // glass
                            Color albedo = Vec3.random(0.5, 1).toColor();
                            sphereMat = new Dielectric(1.5, albedo);
                            world.add(new Sphere(center, 0.2, sphereMat));
                        }
                    }
                }
            }
        }

        Material material1 = new Dielectric( 1.5, Utility.strToColor("255, 150, 150"));
        Material material2 = new Lambertian(Utility.strToColor("71, 160, 255"));
        Material material3 = new Metal(Utility.strToColor("255, 174, 60"), 0.01);

        world.add(new Sphere(new Point3(0, 1, 0), 1, material1));
        world.add(new Sphere(new Point3(0, 1, 0), -0.9, material1));
        world.add(new Sphere(new Point3(-4, 1, 0), 1, material2));
        world.add(new Sphere(new Point3(4, 1, 0), 1, material3));

        return world;
    }

}