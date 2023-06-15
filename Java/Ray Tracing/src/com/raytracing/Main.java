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
        double aspectRatio = (double) 16 / 9;

        final int imageWidth;
        if (dev) {
            imageWidth = 400;
        } else {
            System.out.print("Image Width: ");
            imageWidth = bob.nextInt();
        }

        int samplesPerPixel;
        if (dev) {
            samplesPerPixel = 7;
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
        Color background = Utility.hexToColor("#bce5f5");

        switch (6) {
            case 1:
                world.add(Scenes.smallSpheres());
                lookFrom = new Point3(13, 2.2, 4);
                lookAt = new Point3(0, 0.4, 0);
                vFOV = 20;
                break;

            case 2:
                world.add(Scenes.checkeredSpheres());
                lookFrom = new Point3(0, 0, 20);
                lookAt = new Point3(0, 0, 0);
                vFOV = 30;
                break;

            case 3:
                world.add(Scenes.perlinSpheres());
                lookFrom = new Point3(4, 4, 10);
                lookAt = new Point3(0, 1.8, -2.5);
                vFOV = 40;
                break;

            case 4:
                world.add(Scenes.earth());
                lookFrom = new Point3(5, 2, -1);
                lookAt = new Point3(0, 0, -1.5);
                vFOV = 60;
                break;

            case 5:
                samplesPerPixel = 150;
                world.add(Scenes.lights());
                background.set(new Color(0, 0, 0));

                lookFrom = new Point3(20, 3, 6);
                lookAt = new Point3(0, 2, 0);
                vFOV = 20;
                break;

            case 6:
                if (dev) samplesPerPixel = 30;
                world.add(Scenes.cornellBox());

                aspectRatio = 1;
                background.set(new Color(0, 0, 0));

                lookFrom = new Point3(0, 250, 950);
                lookAt = new Point3(0, 250, 0);
                vFOV = 40;
                break;

            default:
                world.add(Scenes.standardScene());
                lookFrom = new Point3(13, 2.2, 4);
                lookAt = new Point3(0, 0.4, 0);
                vFOV = 20;
                aperture = 0.1;
                break;
        }

        final int imageHeight = (int) (imageWidth / aspectRatio);

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
                System.out.print("\rScanlines remaining: " + j + " of " + imageHeight + " --- " +
                    (int) (((double) (imageHeight - j) / imageHeight) * 100) + "% completed");
                System.out.flush();

                for (int i = 0; i < imageWidth; i++) {
                    Color pixelColor = new Color(0, 0, 0);
                    // multiple samples per pixel
                    for (int s = 0; s < samplesPerPixel; s++) {
                        double u = (i + Utility.randomDouble()) / (imageWidth - 1);
                        double v = (j + Utility.randomDouble()) / (imageHeight - 1);
                        Ray r = cam.getRay(u, v);
                        pixelColor = Vec3.add(pixelColor, rayColor(r, background, world, maxDepth)).toColor();
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
    public static Color rayColor(Ray r, Color background, Hittable world, int depth) {
        HitRecord rec = new HitRecord();

        // recursion safeguard
        if (depth <= 0) {
            return new Color(0, 0, 0); // black
        }

        // Check if ray hits anything, if not return the background color
        if (!world.hit(r, 0.001, Utility.Infinity, rec))
            return background;

        Ray scattered = new Ray();
        Color attenuation = new Color();
        Color emitted = rec.getMaterial().emitted(rec.getU(), rec.getV(), rec.getP());

        // if ray doesn't get scattered return emitted color
        if (!rec.getMaterial().scatter(r, rec, attenuation, scattered))
            return emitted;

        // return emitted + attenuation * ray_color(scattered, background, world, depth-1);
        return Vec3.mul(
            Vec3.add(emitted, attenuation),
            rayColor(scattered, background, world, depth - 1)
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