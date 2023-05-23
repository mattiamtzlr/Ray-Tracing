package com.raytracing;

import java.util.ArrayList;
import java.util.List;

public class HittableList implements Hittable {
    // class for a list containing all hittable objects in the scene.
    private final List<Hittable> objects;

    public HittableList() {
        this.objects = new ArrayList<>();
    }

    // The list can be instantiated with an optional object.
    public HittableList(Hittable object) {
        this.objects = new ArrayList<>();
        if (!(object == null)) {
            this.add(object);
        }
    }

    public void clear() {
        this.objects.clear();
    }

    public void add(Hittable object) {
        this.objects.add(object);
    }

    public List<Hittable> getObjects() {
        return this.objects;
    }

    // hit method loops through all objects and tests if they are hit by the ray.
    // to save time it sets the closest t value to avoid searching behind already hit objects
    @Override

    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        HitRecord tempRec = new HitRecord();
        boolean hitAnything = false;
        double closest = tMax;

        for (Hittable object : this.objects) {
            if (object.hit(r, tMin, closest, tempRec)) {
                hitAnything = true;
                closest = tempRec.getT();
                rec.setP(tempRec.getP());
                rec.setT(tempRec.getT());
                rec.setMaterial(tempRec.getMaterial());
                rec.setNormal(tempRec.getNormal());
                rec.setFrontFace(tempRec.isFrontFace());
            }
        }

        return hitAnything;
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        if (objects.isEmpty()) return false;

        AABB tempBox = new AABB();
        boolean firstBox = true;

        for (Hittable object : objects) {
            if (!object.boundingBox(time0, time1, tempBox)) return false;
            outputBox.set(firstBox ? tempBox : AABB.surroundingBox(outputBox, tempBox));
            firstBox = false;
        }

        return true;
    }
}
