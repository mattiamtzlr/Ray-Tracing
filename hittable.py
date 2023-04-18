from vec3 import Vec3, Point3
from ray import Ray

# Hittable class: all objects that can be hit by a ray implement this class and
# must implement a hit method as described below

class HitRecord:
    def __init__(self, p: Point3, normal: Vec3, t: float) -> None:
        self.p = p
        self.normal = normal
        self.t = t

class Hittable:
    def hit(self, r: Ray, tMin: float, tMax: float, rec: HitRecord) -> bool:
        pass