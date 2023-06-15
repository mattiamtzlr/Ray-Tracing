package com.raytracing;

public class Scenes {

    public static HittableList standardScene() {
        HittableList objects = new HittableList();

        // ground
        Material groundMaterial = new Lambertian(new CheckerTexture(
            Utility.hexToColor("#15c4d1"), Utility.hexToColor("#F57131"), 6
        ));
        objects.add(new Sphere(new Point3(0, -1000, 0), 1000, groundMaterial));

        // standard spheres
        Material material1 = new Dielectric( 1.5, Utility.rgbToColor("255, 150, 150"));
        Material material2 = new Lambertian(Utility.rgbToColor("71, 160, 255"));
        Material material3 = new Metal(Utility.rgbToColor("255, 174, 60"), 0.01);

        Texture perlinMat4 = new NoiseTexture(Utility.rgbToColor("231, 122, 255"), 2);
        Material material4 = new Metal(perlinMat4, 0);

        objects.add(new Sphere(new Point3(0, 1, 0), 1, material1));
        objects.add(new Sphere(new Point3(0, 1, 0), -0.9, material1));
        objects.add(new Sphere(new Point3(-4, 1, 0), 1, material2));
        objects.add(new Sphere(new Point3(4, 1, 0), 1, material3));
        objects.add(new Sphere(new Point3(3, 0.7, 3), .5, material4));

        return objects;
    }

    public static HittableList smallSpheres() {
        HittableList objects = new HittableList();

        // ground
        Material groundMaterial = new Lambertian(new Color(0.2, 0.2, 0.2));
        objects.add(new Sphere(new Point3(0, -1000, 0), 1000, groundMaterial));

        HittableList spheres = new HittableList();

        int constraint = 15;

        for (int a = -constraint; a < constraint; a++) {
            for (int b = -constraint; b < constraint; b++) {
                double chooseMat = Utility.randomDouble();
                Point3 center = new Point3(a + (0.7 * Utility.randomDouble()), Utility.randomDouble(0, .6), b + (0.7 * Utility.randomDouble()));

                if (Vec3.sub(center, new Point3(4, 0.2, 0)).length() > 0.9) {
                    Material sphereMat;

                    if (chooseMat < 0.6) {
                        // diffuse
                        Color albedo = Vec3.random(0.1, 0.9).toColor();
                        sphereMat = new Lambertian(albedo);
                        spheres.add(new Sphere(center, 0.2, sphereMat));

                    } else if (chooseMat < 0.8) {
                        // metal
                        Color albedo = Vec3.random(0.5, 1).toColor();
                        double fuzz = Utility.randomDouble(0, 0.3);
                        sphereMat = new Metal(albedo, fuzz);
                        spheres.add(new Sphere(center, 0.2, sphereMat));

                    } else {
                        // glass
                        Color albedo = Vec3.random(0.5, 1).toColor();
                        sphereMat = new Dielectric(1.5, albedo);
                        spheres.add(new Sphere(center, 0.2, sphereMat));
                    }
                }
            }
        }

        objects.add(new BVHNode(spheres, 0, 1));
        return objects;
    }

    public static HittableList checkeredSpheres() {
        HittableList objects = new HittableList();

        Material checker1 = new Lambertian(new CheckerTexture(
            Vec3.random(0.3, 0.8).toColor(), new Color(), 2
        ));
        Material checker2 = new Lambertian(new CheckerTexture(
            Vec3.random(0.3, 0.8).toColor(), new Color(), 20
        ));

        objects.add(new Sphere(new Point3(0, -10, 0), 10, checker1));
        objects.add(new Sphere(new Point3(0, 10, 0), 10, checker2));

        return objects;
    }

    public static HittableList perlinSpheres() {
        HittableList objects = new HittableList();

        Texture perlin1 = new NoiseTexture(5);
        Texture perlin2 = new NoiseTexture(Utility.hexToColor("#cf6a5d"), 3);
        Texture perlin3 = new NoiseTexture(Utility.hexToColor("#7fcf5d"), 12);

        Texture checkerTexture = new CheckerTexture(
            Utility.hexToColor("#2c2c2c"), Utility.hexToColor("#4a4a4a"), .8
        );

        objects.add(new Sphere(new Point3(0, -1000, 0), 1000, new Lambertian(checkerTexture)));
        objects.add(new Sphere(new Point3(0, 2, 0), 2, new Lambertian(perlin1)));
        objects.add(new Sphere(new Point3(-5, 4.3, -1), 1.1, new Lambertian(perlin2)));
        objects.add(new Sphere(new Point3(4, 1.2, 1), 1.3, new Lambertian(perlin3)));

        return objects;
    }

    public static HittableList earth() {
        HittableList objects = new HittableList();

        Texture earthTexture = new ImageTexture("textures/earthmap.jpg");
        Material earthMat = new Lambertian(earthTexture);
        objects.add(new Sphere(new Point3(0, 0, 0), 2, earthMat));

        Texture moonTexture = new ImageTexture("textures/moonmap.jpg");
        Material moonMat = new Lambertian(moonTexture);
        objects.add(new Sphere(new Point3(2, 1.5, -3.2), .5, moonMat));

        return objects;
    }

    public static HittableList lights() {
        HittableList objects = new HittableList();

        // ground
        Texture checkerTexture = new CheckerTexture(
            Utility.hexToColor("#611c80"), Utility.hexToColor("#9c4ebf"), 2
        );
        objects.add(new Sphere(new Point3(0, -1000, 0), 1000, new Lambertian(checkerTexture)));

        // perlin sphere
        Texture perlinText = new NoiseTexture(Utility.hexToColor("#ffeb87"), 5);
        objects.add(new Sphere(new Point3(0, 2, 0), 2, new Lambertian(perlinText)));

        // lights
        Material diffLight1 = new DiffuseLight(new Color(5, 5, 5));
        Material diffLight2 = new DiffuseLight(new Color(3, 3, 3));
        objects.add(new XYRect(1, 3, .5, 3.5, -3, diffLight1));
        objects.add(new XZRect(-2, 2, -2, 2, 5, diffLight2));

        return objects;
    }

    public static HittableList cornellBox() {
        // standard cornell box with all positive coordinates because negative numbers fuck shit up
        HittableList objects = new HittableList();

        Material red   = new Lambertian(new Color(.65, .05, .05));
        Material white = new Lambertian(new Color(.85, .85, .85));
        Material green = new Lambertian(new Color(.12, .45, .15));
        Material light = new DiffuseLight(new Color(14, 14, 14));

        objects.add(new YZRect(0, 500, 0, 500, 0, green));
        objects.add(new YZRect(0, 500, 0, 500, 500, red));
        objects.add(new XZRect(185, 315, 185, 315, 499, light));

        objects.add(new XYRect(0, 500, 0, 500, 0, white));   // back wall
        objects.add(new XZRect(0, 500, 0, 500, 0, white));   // floor
        objects.add(new XZRect(0, 500, 0, 500, 500, white)); // ceiling

        // cuboid in the back, 170 x 170 x 340
        Hittable box1 = new Box(new Point3(100, 0, 50), new Point3(270, 340, 220), white);
        box1 = new Translate(box1, new Vec3(-5, 0, 0));
        objects.add(box1);

        // cube in the front, length 170
        Hittable box2 = new Box(new Point3(230, 0, 280), new Point3(400, 170, 450), white);
        box2 = new Translate(box2, new Vec3(5, 0, 0));
        objects.add(box2);

        return objects;
    }
}