from vec3 import *
from ray import Ray

# Hittable class: all objects that can be hit by a ray implement this class and
# must implement a hit method as described below

class HitRecord:
    def __init__(self):
        self.p = None
        self.normal = None
        self.t = None
        self.frontFace = None
    
    # setting the direction of the normal based on wheter the ray is inside or 
    # outside the object
    def setFaceNormal(self, r: Ray, outwardNormal: Vec3):
        self.frontFace = dot(r.direction(), outwardNormal) < 0
        self.normal = outwardNormal if self.frontFace else -outwardNormal


class Hittable:
    def hit(self, r: Ray, tMin: float, tMax: float, rec: HitRecord) -> bool:
        pass