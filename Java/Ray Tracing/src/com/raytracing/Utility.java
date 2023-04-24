package com.raytracing;

import java.util.Random;

public class Utility {
    // Constants
    public static final double Infinity = Double.POSITIVE_INFINITY;
    public static final double Pi = 3.1415926535897932385;

    // Utility Functions
    public static double degToRad(double deg) {
        return deg * Pi / 180;
    }

    public static double randomFloat() {
        Random random = new Random();
        return random.nextDouble();
    }
    public static double randomFloat(double min, double max) {
        Random random = new Random();
        return min + (max - min) * random.nextDouble();
    }

    public static double clamp(double x, double min, double max) {
        // Clamps x between min and max, if outside the range, the return value is min or max respectively.
        return Math.min(Math.max(x, min), max);
    }
}
