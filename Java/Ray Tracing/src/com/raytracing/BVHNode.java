package com.raytracing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BVHNode implements Hittable {
    private final Hittable left;
    private final Hittable right;
    private final AABB box;

    public BVHNode(HittableList list, double time0, double time1) {
        // simple constructor to be called in code, transfers arguments to detailed constructor
        this(list.getObjects(), 0, list.getObjects().size(), time0, time1);
    }

    public BVHNode(List<Hittable> srcObjects, int start, int end, double time0, double time1) {
        List<Hittable> objects = new ArrayList<>(srcObjects); // modifiable array of srcObjects

        int axis = Utility.randomInt(0, 2);
        Comparator<Hittable> comparator = (axis == 0) ? this::boxCompareX
            : (axis == 1) ? this::boxCompareY : this::boxCompareZ;

        int objectSpan = end - start;

        if (objectSpan == 1) {
            this.left = this.right = objects.get(start);

        } else if (objectSpan == 2) {
            if (comparator.compare(objects.get(start), objects.get(start + 1)) < 0) {
                this.left = objects.get(start);
                this.right = objects.get(start + 1);
            } else {
                this.left = objects.get(start + 1);
                this.right = objects.get(start);
            }
        } else {
            objects.subList(start, end).sort(comparator);

            int mid = start + objectSpan / 2;
            this.left = new BVHNode(objects, start, mid, time0, time1);
            this.right = new BVHNode(objects, mid, end, time0, time1);
        }

        AABB boxLeft = new AABB();
        AABB boxRight = new AABB();

        if (!this.left.boundingBox(time0, time1, boxLeft) || !this.right.boundingBox(time0,time1, boxRight))
            System.err.println("No bounding box in BVHNode constructor");

        this.box = AABB.surroundingBox(boxLeft, boxRight);
    }

    // box comparison functions, generic comparison function which gets called by axis-specific functions
    private boolean boxCompare(Hittable a, Hittable b, int axis) {
        AABB boxA = new AABB();
        AABB boxB = new AABB();

        if (!a.boundingBox(0, 0, boxA) || !b.boundingBox(0, 0, boxB))
            System.err.println("No bounding box in BVHNode constructor.");

        return boxA.getMinimum().getComp(axis) < boxB.getMinimum().getComp(axis);
    }
    private int boxCompareX(Hittable a, Hittable b) {
        return boxCompare(a, b, 0) ? -1 : 1;
    }
    private int boxCompareY(Hittable a, Hittable b) {
        return boxCompare(a, b, 1) ? -1 : 1;
    }
    private int boxCompareZ(Hittable a, Hittable b) {
        return boxCompare(a, b, 2) ? -1 : 1;
    }


    @Override
    public boolean hit(Ray r, double tMin, double tMax, HitRecord rec) {
        if (!this.box.hit(r, tMin, tMax)) return false;

        boolean hitLeft = this.left.hit(r, tMin, tMax, rec);
        boolean hitRight = this.right.hit(r, tMin, hitLeft ? rec.getT() : tMax, rec);

        return hitLeft || hitRight;
    }

    @Override
    public boolean boundingBox(double time0, double time1, AABB outputBox) {
        outputBox.set(this.box);
        return true;
    }
}
