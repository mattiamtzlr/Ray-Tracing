package com.raytracing;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Instant start = Instant.now();

        Scanner bob = new Scanner(System.in);

        // dev boolean => set to false when rendering high quality
        boolean dev = false;

        // image properties
        final double aspectRatio = (double) 16 / 9;

        final int imageWidth;
        if (dev) {
            imageWidth = 400;
        } else {
            System.out.print("Image Width: ");
            imageWidth = bob.nextInt();
        }
        final int imageHeight = (int) (imageWidth / aspectRatio);

        final int samplesPerPixel;
        if (dev) {
            samplesPerPixel = 5;
        } else {
            System.out.print("Samples per Pixel: ");
            samplesPerPixel = bob.nextInt();
        }

        final int maxDepth = 100;

        // world
        HittableList world = new HittableList();

        // camera
        Point3 lookFrom;
        Point3 lookAt;
        int vFOV;
        double aperture = 0;

        switch (0) {
            case 1:
                world.add(Scenes.smallSpheres());
                lookFrom = new Point3(13, 2.2, 4);
                lookAt = new Point3(0, 0.4, 0);
                vFOV = 20;
                break;

            case 2:
                world.add(Scenes.twoSpheres());
                lookFrom = new Point3(13, 2, 3);
                lookAt = new Point3(0, 0, 0);
                vFOV = 30;
                break;

            default:
                world.add(Scenes.standardScene());
                lookFrom = new Point3(13, 2.2, 4);
                lookAt = new Point3(0, 0.4, 0);
                vFOV = 20;
                aperture = 0.1;
                break;
        }

        Vec3 viewUp = new Vec3(0, 1, 0); // horizontally level view
        double distToFocus = Vec3.sub(lookFrom, lookAt).length();

        Camera cam = new Camera(
            lookFrom, lookAt, viewUp, vFOV, aspectRatio, aperture, distToFocus, 0, 1
        );

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
            double timeElapsed = Duration.between(start, finish).toMillis();
            String timeScale = "milliseconds";

            if (timeElapsed > 1000) {
                timeElapsed = timeElapsed / 1000;
                timeScale = "seconds";
            }
            if (timeElapsed > 60 && timeScale.equals("seconds")) {
                timeElapsed = timeElapsed / 60;
                timeScale = "minutes";
            }

            System.out.printf("\nSuccessfully wrote to '%s' in %.2f %s.\n", fileName, timeElapsed, timeScale);

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
        Vec3 unitDirection = Vec3.unitVector(r.getDirection());
        double t = 0.5 * (unitDirection.y() + 1);
        // return (1 - t) * Color(1, 1, 1) + t * Color(0.5, 0.7, 1.0)
        return Vec3.add(
            Vec3.mul(Utility.rgbToColor("135, 188, 237"), (1 - t)),
            Vec3.mul(new Color(1, 1, 1), t)
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