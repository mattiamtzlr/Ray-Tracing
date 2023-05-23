package com.raytracing;

public interface Hittable {
    // Hittable Interface: all objects that can be hit by a ray implement this class and
    // must implement a hit method as described below

    boolean hit(Ray r, double tMin, double tMax, HitRecord rec);
    boolean boundingBox(double time0, double time1, AABB outputBox);
}
