package com.raytracing;

public class Main {
    public static void main(String[] args) {
        Vec3 a = new Vec3(1, 2, 3);
        Vec3 b = new Vec3(4, 5, 6);
        Vec3 c = new Vec3(5, 10, 15);

        int x = 5;

        System.out.println(Vec3.add(a, b).toString());
        System.out.println(Vec3.sub(b, a).toString());
        System.out.println(Vec3.mul(a, x).toString());
        System.out.println(Vec3.div(c, x).toString());
        System.out.println(Vec3.dot(a, b));
        System.out.println(Vec3.cross(a, b).toString());

        System.out.println("-----------------------------------------------");

        System.out.println(Vec3.random(0.3, 0.8).toString());

        double y = 0.4;
        double min = 0.3;
        double max = 0.9;

        System.out.println("-----------------------------------------------");
        System.out.println(Utility.clamp(y, min, max));
    }
}