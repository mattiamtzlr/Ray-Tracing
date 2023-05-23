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

    public static double randomDouble() {
        Random random = new Random();
        return random.nextDouble();
    }
    public static double randomDouble(double min, double max) {
        Random random = new Random();
        return min + (max - min) * random.nextDouble();
    }

    public static int randomInt(int min, int max) {
        // returns a random integer in [min, max]
        return (int) randomDouble(min, max + 1);
    }

    public static double clamp(double x, double min, double max) {
        // Clamps x between min and max, if outside the range, the return value is min or max respectively.
        return Math.min(Math.max(x, min), max);
    }

    /**
     * Returns a com.raytracing.Color object of the provided color in string format
     *
     * @param color a color in string format: "r, g, b"
     * @return a Color object with values between 0 and 1
     */
    public static Color strToColor(String color) {
        String[] colorArray = color.split(",");

        Color output = new Color();
        for (int i = 0; i < colorArray.length; i++) {
            output.setComp(i, Double.parseDouble(colorArray[i]) / 255);
        }
        return output;
    }
}
