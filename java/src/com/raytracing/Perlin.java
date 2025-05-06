package com.raytracing;

public class Perlin {
    private static final int pointCount = 256;
    private final Vec3[] ranVec;
    private final int[] permX;
    private final int[] permY;
    private final int[] permZ;

    public Perlin() {
        this.ranVec = new Vec3[pointCount];
        for (int i = 0; i < pointCount; i++) {
            this.ranVec[i] = Vec3.unitVector(Vec3.random(-1, 1));
        }

        this.permX = perlinGeneratePerm();
        this.permY = perlinGeneratePerm();
        this.permZ = perlinGeneratePerm();
    }

    public double noise(Point3 p) {
        // remove everything before decimal
        double u = p.x() - Math.floor(p.x());
        double v = p.y() - Math.floor(p.y());
        double w = p.z() - Math.floor(p.z());

        int i = (int) Math.floor(p.x());
        int j = (int) Math.floor(p.y());
        int k = (int) Math.floor(p.z());
        Vec3[][][] c = new Vec3[2][2][2];

        for (int di = 0; di < 2; di++)
            for (int dj = 0; dj < 2; dj++)
                for (int dk = 0; dk < 2; dk++)
                    c[di][dj][dk] = ranVec[
                        permX[(i + di) & 255] ^
                        permY[(j + dj) & 255] ^
                        permZ[(k + dk) & 255]
                    ];

        return perlinInterpolation(c, u, v, w);
    }

    public double turbulence(Point3 p, int depth) {
        double accum = 0;
        Point3 tempP = p;
        double weight = 1;

        for (int i = 0; i < depth; i++) {
            accum += weight * noise(tempP);
            weight *= 0.5;
            tempP = Vec3.mul(tempP, 2).toPoint3();
        }

        return Math.abs(accum);
    }

    private static int[] perlinGeneratePerm() {
        int[] p = new int[pointCount];

        for (int i = 0; i < pointCount; i++) {
            p[i] = i;
        }

        permute(p, pointCount);
        return p;
    }

    private static void permute(int[] p, int n) {
        for (int i = n - 1; i > 0; i--) {
            int target = Utility.randomInt(0, i);
            int tmp = p[i];
            p[i] = p[target];
            p[target] = tmp;
        }
    }

    private static double perlinInterpolation(Vec3[][][] c, double u, double v, double w) {
        double uu = u * u * (3 - 2 * u);
        double vv = v * v * (3 - 2 * v);
        double ww = w * w * (3 - 2 * w);
        double accum = 0.0;

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++) {
                    Vec3 weightV = new Vec3(u - i, v - j, w - k);
                    accum +=
                        (i * uu + (1 - i) * (1 - uu))
                            * (j * vv + (1 - j) * (1 - vv))
                            * (k * ww + (1 - k) * (1 - ww))
                            * Vec3.dot(c[i][j][k], weightV);
                }

        return accum;
    }
}
