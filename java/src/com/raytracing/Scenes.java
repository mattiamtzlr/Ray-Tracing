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

        Texture perlinMat4 = new NoiseTexture(
            Utility.rgbToColor("231, 122, 255"),
            2,
            true
        );
        Material material4 = new Metal(perlinMat4, 0);

        objects.add(new Sphere(new Point3(0, 1, 0), 1, material1));
        objects.add(new Sphere(new Point3(0, 1, 0), -0.9, material1));
        objects.add(new Sphere(new Point3(-4, 1, 0), 1, material2));
        objects.add(new Sphere(new Point3(4, 1, 0), 1, material3));
        objects.add(new Sphere(new Point3(3, 0.7, 3), .5, material4));

        return objects;
    }

    public static HittableList insideBox() {
        HittableList objects = new HittableList();

        // ground
        Material groundMaterial = new Lambertian(new CheckerTexture(
            Utility.hexToColor("#159fd1"), Utility.hexToColor("#ff6419"), 6
        ));
        objects.add(new Sphere(new Point3(0, -1000, 0), 1000, groundMaterial));

        // light
        Material light = new DiffuseLight(new Color(1.8, 1.8, 1.8));
        objects.add(new XZRect(-20, 20, -20, 20, 100, light));

        // walls
        Texture checkerGray = new CheckerTexture(
            new Color(0.6, 0.6, 0.6),
            new Color(0.8, 0.8, 0.8),
            6
        );
        Material wallMat = new Metal(checkerGray, 0.6);
        Material wallMatEmissive = new DiffuseLight(new Color(.95, .95, .87));
        objects.add(new YZRect(0, 20, -10, 10, -9, wallMat));
        objects.add(new YZRect(0, 20, -10, 10, 9, wallMat));

        objects.add(new XYRect(-10, 15, 0, 20, -7, wallMatEmissive));
        objects.add(new XYRect(-10, 15, 0, 20, 7, wallMat));

        // standard spheres and cube
        Material metalRed = new Metal(Utility.hexToColor("#e82e00"), 0.01);
        Material green = new Lambertian(Utility.hexToColor("#0db307"));
        Material metalGold = new Metal(Utility.rgbToColor("255, 174, 60"), 0.1);

        Texture perlinMat4 = new NoiseTexture(
            Utility.rgbToColor("231, 122, 255"),
            2,
            true
        );
        Material perlinPurple = new Metal(perlinMat4, 0.8);

        objects.add(new Sphere(new Point3(-5, 2, 3.3), 2, green));
        double boxSize = 2.4;
        Hittable box = new Box(
            new Point3(-boxSize / 2, 0, -boxSize / 2),
            new Point3(boxSize / 2, boxSize, boxSize / 2),
            metalRed
        );
        box = new RotateY(box, 12);
        objects.add(box);
        objects.add(new Sphere(new Point3(4, 1, -1), 1, metalGold));

        objects.add(new Sphere(new Point3(4, 0.55, 2.8), .45, perlinPurple));

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
        Texture perlin2 = new NoiseTexture(Utility.hexToColor("#cf6a5d"), 3, true);
        Texture perlin3 = new NoiseTexture(Utility.hexToColor("#7fcf5d"), 12, true);

        Texture checkerTexture = new CheckerTexture(
            Utility.hexToColor("#2c2c2c"), Utility.hexToColor("#4a4a4a"), 2
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
            Utility.hexToColor("#611c80"),
            Utility.hexToColor("#9c4ebf"),
            1.5
        );
        objects.add(new Sphere(new Point3(0, -1000, 0), 1000, new Lambertian(checkerTexture)));

        // objects
        Texture perlinText = new NoiseTexture( 5);
        objects.add(new Sphere(new Point3(-1, 2, -2.5), 2, new Lambertian(perlinText)));

        double boxSize = 2;
        double boxAngle = 25;
        Vec3 boxPos = new Vec3(1, 0.2, 1);
        Material metal = new Metal(new Color(.7, .7, .7), .2);
        Hittable box = new Box(new Point3(0, 0, 0), new Point3(boxSize, boxSize, boxSize), metal);
        box = new RotateY(box, boxAngle);
        box = new Translate(box, boxPos);
        objects.add(box);

        // top of box
        Hittable topBox = new XZRect(0, boxSize, 0, boxSize, 0, metal);
        topBox = new RotateY(topBox, boxAngle);
        topBox = new Translate(topBox, Vec3.add(boxPos, new Vec3(0, boxSize, 0)));
        objects.add(topBox);

        Material red = new Lambertian(new Color(.65, .05, .05));
        objects.add(new Sphere(new Point3(3.5, 1, -1), .5, red));

        // lights
        Material diffLightWhite = new DiffuseLight(new Color(1, 1, 1));
        Material diffLightBlue = new DiffuseLight(Utility.hexToColor("#5ec1ff"));
        Material diffLightPink = new DiffuseLight(Utility.hexToColor("#ff5ef2"));

        objects.add(new XZRect(-4, 4, -4, 4, 5, diffLightWhite));

        Hittable rightLight = new XYRect(-2, 2, 0, 3, 0, diffLightBlue);
        rightLight = new RotateY(rightLight, -40);
        rightLight = new Translate(rightLight, new Vec3(4, .5, -4));
        objects.add(rightLight);

        Hittable leftLight = new XYRect(-2, 2, 0, 3, 0, diffLightPink);
        leftLight = new RotateY(leftLight, 10);
        leftLight = new Translate(leftLight, new Vec3(3, .5, 4));
        objects.add(leftLight);

        return objects;
    }

    public static HittableList rotations() {
        HittableList objects = new HittableList();

        // ground
        Material groundMaterial = new Metal(Utility.hexToColor("#cbbecf"), .95);
        objects.add(new XZRect(-1000, 1000, -1000, 1000, 0, groundMaterial));

        Material cubeX = new Lambertian(new ImageTexture("textures/x.png"));
        Material cubeY = new Lambertian(new ImageTexture("textures/y.png"));
        Material cubeZ = new Lambertian(new ImageTexture("textures/z.png"));
        double cubeSize = 1.1;
        double forward = 2.5;

        Hittable cube1 = new Box(
            new Point3(-cubeSize, -cubeSize, -cubeSize),
            new Point3(cubeSize, cubeSize, cubeSize),
            cubeX
        );
        cube1 = new RotateX(cube1, 30);
        cube1 = new Translate(cube1, new Vec3(-5, 2, forward - 0.2));
        objects.add(cube1);

        Hittable cube2 = new Box(
            new Point3(-cubeSize, -cubeSize, -cubeSize),
            new Point3(cubeSize, cubeSize, cubeSize),
            cubeY
        );
        cube2 = new RotateY(cube2, 30);
        cube2 = new Translate(cube2, new Vec3(0, 2, forward + 0.2));
        objects.add(cube2);

        Hittable cube3 = new Box(
            new Point3(-cubeSize, -cubeSize, -cubeSize),
            new Point3(cubeSize, cubeSize, cubeSize),
            cubeZ
        );
        cube3 = new RotateZ(cube3, 30);
        cube3 = new Translate(cube3, new Vec3(5, 2, forward - 0.2));
        objects.add(cube3);

        Material bigCubeMat = new Lambertian(new NoiseTexture(
            Utility.hexToColor("#ff8717"),
            1
        ));
        double bigCubeSize = 3.5;
        Hittable bigCube = new Box(
            new Point3(-bigCubeSize, -bigCubeSize, -bigCubeSize),
            new Point3(bigCubeSize, bigCubeSize, bigCubeSize),
            bigCubeMat
        );
        bigCube = new RotateX(bigCube, -20);
        bigCube = new RotateY(bigCube, 25);
        bigCube = new RotateZ(bigCube, -5);
        bigCube = new Translate(bigCube, new Vec3(0, 4.8, -10));
        objects.add(bigCube);

        // wall
        Material wallMat = new Lambertian(Utility.hexToColor("#ecb3ff"));
        Hittable wall = new XYRect(-70, 70, 0, 30, -45, wallMat);
        objects.add(wall);

        // light
        Material lightMat = new DiffuseLight(new Color(2, 2, 2));

        Hittable light = new XZRect(-40, 40, -40, 40, 0, lightMat);
        light = new RotateX(light, 40);
        light = new Translate(light, new Vec3(0, 20, 15));
        objects.add(light);

        return objects;
    }

    public static HittableList cornellBox() {
        // standard cornell box with all positive coordinates because negative numbers fuck shit up
        HittableList objects = new HittableList();

        Material red   = new Lambertian(new Color(.65, .05, .05));
        Material white = new Lambertian(new Color(.85, .85, .85));
        Material green = new Lambertian(new Color(.12, .45, .15));
        Material light = new DiffuseLight(new Color(14.5, 14.5, 14.5));

        objects.add(new YZRect(0, 500, 0, 500, 0, green));
        objects.add(new YZRect(0, 500, 0, 500, 500, red));
        objects.add(new XZRect(185, 315, 185, 315, 499, light));

        objects.add(new XYRect(0, 500, 0, 500, 0, white));   // back wall
        objects.add(new XZRect(0, 500, 0, 500, 0, white));   // floor
        objects.add(new XZRect(0, 500, 0, 500, 500, white)); // ceiling

        // cuboid in the back, 170 x 170 x 340, rotated counterclockwise
        Hittable box1 = new Box(new Point3(0, 0, 0), new Point3(170, 340, 170), white);
        box1 = new RotateY(box1, 15);
        box1 = new Translate(box1, new Vec3(60, 0, 80));
        objects.add(box1);

        // cube in the front, length 170, rotated clockwise
        Hittable box2 = new Box(new Point3(0, 0, 0), new Point3(170, 170, 170), white);
        box2 = new RotateY(box2, -20);
        box2 = new Translate(box2, new Vec3(290, 0, 250));
        objects.add(box2);

        return objects;
    }

    public static HittableList cornellBoxSmoke() {
        // cornell box with all positive coordinates because negative numbers fuck shit up
        HittableList objects = new HittableList();

        Material red   = new Lambertian(new Color(.65, .05, .05));
        Material white = new Lambertian(new Color(.85, .85, .85));
        Material green = new Lambertian(new Color(.12, .45, .15));
        Material light = new DiffuseLight(new Color(5.5, 5.5, 5.5));

        objects.add(new YZRect(0, 500, 0, 500, 0, green));
        objects.add(new YZRect(0, 500, 0, 500, 500, red));
        objects.add(new XZRect(85, 415, 85, 415, 499, light)); // 330 x 330

        objects.add(new XYRect(0, 500, 0, 500, 0, white));   // back wall
        objects.add(new XZRect(0, 500, 0, 500, 0, white));   // floor
        objects.add(new XZRect(0, 500, 0, 500, 500, white)); // ceiling

        // cuboid in the back, 170 x 170 x 340, rotated counterclockwise
        Hittable box1 = new Box(new Point3(0, 0, 0), new Point3(170, 340, 170), white);
        box1 = new RotateY(box1, 15);
        box1 = new Translate(box1, new Vec3(60, 0, 80));

        // cube in the front, length 170, rotated clockwise
        Hittable box2 = new Box(new Point3(0, 0, 0), new Point3(170, 170, 170), white);
        box2 = new RotateY(box2, -20);
        box2 = new Translate(box2, new Vec3(290, 0, 250));

        objects.add(new ConstantMedium(box1, 0.01, new Color(0, 0, 0)));
        objects.add(new ConstantMedium(box2, 0.01, new Color(1, 1, 1)));

        return objects;
    }

    public static HittableList finalScene() {
        HittableList objects = new HittableList();

        // ---------------------------------------------------------------------------------- ground
        HittableList groundBoxes = new HittableList();
        Material groundMat1 = new Lambertian(Utility.hexToColor("#70e081"));
        Material groundMat2 = new Lambertian(Utility.hexToColor("#34d976"));
        Material groundMat3 = new Lambertian(Utility.hexToColor("#34d94d"));
        Material[] groundMat = {groundMat1, groundMat2, groundMat3};

        int boxesPerSide = 20;
        int boxWidth = 100;
        for (int i = 0; i < boxesPerSide; i++) {
            for (int j = 0; j < boxesPerSide; j++) {
                double x0 = -1000 + (i * boxWidth);
                double y0 = 0;
                double z0 = -1000 + (j * boxWidth);
                double x1 = x0 + boxWidth;
                double y1 = Utility.randomDouble(20, 80);
                double z1 = z0 + boxWidth;

                groundBoxes.add(new Box(
                    new Point3(x0, y0, z0),
                    new Point3(x1, y1, z1),
                    Utility.randomChoice(groundMat)
                ));
            }
        }

        objects.add(new BVHNode(groundBoxes, 0, 1));

        // ---------------------------------------------------------------------------------- light & fog
        Material lightMat = new DiffuseLight(new Color(2, 2, 2));
        Hittable light = new XZRect(-1000, 1000, -1000, 1000, 0, lightMat);
        light = new RotateY(light, 45);
        light = new RotateX(light, 45);
        light = new Translate(light, new Vec3(0, 1300, 300));
        objects.add(light);

        Sphere fogBoundary = new Sphere(new Point3(), 3000, new Dielectric(1.5));
        objects.add(new ConstantMedium(fogBoundary, 0.00016, new Color(1, 1, 1)));

        // ---------------------------------------------------------------------------------- walls
        Material wallMat = new Metal(new Color(.3, .3, .3), 0.9);
        objects.add(new XYRect(-1000, 1000, 0, 2000, -1000, wallMat));
        objects.add(new YZRect(0, 2000, -1000, 1000, -1000, wallMat));

        // ---------------------------------------------------------------------------------- texture cube
        Texture texture = new ImageTexture("textures/cubeTexture.jpg");
        Hittable checkerBox = new Box(
            new Point3(0, 0, 0),
            new Point3(100, 100, 100),
            new Lambertian(texture)
        );
        checkerBox = new RotateX(checkerBox, 30);
        checkerBox = new RotateY(checkerBox, 20);
        checkerBox = new Translate(checkerBox, new Vec3(360, 170, 660));
        objects.add(checkerBox);

        // ---------------------------------------------------------------------------------- cube of spheres
        HittableList cubeSpheres = new HittableList();
        Material smallSphereMat1 = new Lambertian(Utility.hexToColor("#d7f2fa"));
        Material smallSphereMat2 = new Lambertian(Utility.hexToColor("#ff8375"));
        Material smallSphereMat3 = new Lambertian(Utility.hexToColor("#ffd70d"));
        Material[] smallSphereMat = {smallSphereMat1, smallSphereMat2, smallSphereMat3};

        int ns = 1500; // number of Spheres
        for (int i = 0; i < ns; i++) {
            cubeSpheres.add(new Sphere(
                Vec3.random(0, 150).toPoint3(),
                10,
                Utility.randomChoice(smallSphereMat))
            );
        }
        Hittable cubeSpheresBVH = new BVHNode(cubeSpheres, 0, 1);
        cubeSpheresBVH = new RotateY(cubeSpheresBVH, -20);
        cubeSpheresBVH = new Translate(cubeSpheresBVH, new Vec3(590, 120, 360));
        objects.add(cubeSpheresBVH);

        // ---------------------------------------------------------------------------------- other stuff
        Material roseGold = new Metal(Utility.hexToColor("#f0ab9c"), 0.5);
        objects.add(new Sphere(new Point3(-200, 400, -300), 280, roseGold));

        Material glass = new Dielectric(1.5);
        objects.add(new Sphere(new Point3(650, 140, 730), 20, glass));

        return objects;
    }

    public static HittableList bokeh() {
        HittableList objects = new HittableList();

        // ----------------------------------------------------------------------------------------- spheres
        Material sphereMat1 = new DiffuseLight(Utility.hexToColor("#ffd969"));
        Material sphereMat2 = new DiffuseLight(Utility.hexToColor("#ffb769"));
        Material sphereMat3 = new DiffuseLight(Utility.hexToColor("#fcec9d"));
        Material[] sphereMat = {sphereMat1, sphereMat2, sphereMat3};

        HittableList spheres = new HittableList();

        final int[] gridSize = {22, 10};
        final double sphereRadius = 0.5;
        final double gap = 3.4;

        double startX = -(gridSize[0] * (sphereRadius / 2) + (double) ((gridSize[0] - 1) / 2) * gap);
        double startY = -(gridSize[1] * (sphereRadius / 2) + (double) ((gridSize[1] - 1) / 2) * gap);

        for (int i = 0; i < gridSize[0]; i++) {
            for (int j = 0; j < gridSize[1]; j++) {
                double x = startX + (i * sphereRadius) + (i * gap) + Utility.randomDouble(-gap / 3, gap / 3);
                double y = startY + (j * sphereRadius) + (j * gap) + Utility.randomDouble(-gap / 3, gap / 3);
                Point3 origin = new Point3(x, y, 0);

                spheres.add(new Sphere(
                    origin,
                    sphereRadius + Utility.randomDouble(-0.2, 0.2),
                    Utility.randomChoice(sphereMat)
                ));
            }
        }
        Hittable spheresBVH = new BVHNode(spheres, 0, 1);
        objects.add(spheresBVH);

        Material wallMat = new Lambertian(new Color(.1, .1, .1));
        Hittable wallBehindLights = new XYRect(-50, 50, -50, 50, -5, wallMat);
        objects.add(wallBehindLights);

        // ----------------------------------------------------------------------------------------- cube & platform
        Texture noise = new NoiseTexture(Utility.hexToColor("#5c4941"), 2, true);
        Material cubeMat = new Lambertian(noise);
        double cubeSize = .7;

        Hittable cube = new Box(
            new Point3(-cubeSize, -cubeSize, -cubeSize),
            new Point3(cubeSize, cubeSize, cubeSize),
            cubeMat
        );
        cube = new RotateY(cube, 30);
        cube = new Translate(cube, new Vec3(0, cubeSize * 1.5, 35));

        Hittable platform = new Box(
            new Point3(-cubeSize * 6, -1, -cubeSize * 2),
            new Point3(cubeSize * 6, cubeSize * 0.5, cubeSize * 2),
            new Lambertian(new Color(0.08, 0.08, 0.08))
        );
        platform = new Translate(platform, new Vec3(0, 0, 35));

        objects.add(cube);
        objects.add(platform);

        // ----------------------------------------------------------------------------------------- light
        Material lightMat = new DiffuseLight(new Color(8.5, 8.5, 8.5));
        Hittable light = new XYRect(-5, 5, -5, 5, 0, lightMat);
        light = new RotateX(light, -50);
        light = new Translate(light, new Vec3(0, 10, 45));

        objects.add(light);

        return objects;
    }
}