from vec3 import *
from ray import *
from hittable import *
import math

# Sphere class implementing the hittable class
class Sphere(Hittable):
    def __init__(self, cen=Point3(0, 0, 0), r=0.0) -> None:
        self.center = cen
        self.radius = r
    
    def hit(self, r: Ray, tMin: float, tMax: float, rec: HitRecord) -> bool:
        # substituting b with 2h
        oc = vecSub(r.origin(), self.center)
        a = r.direction().length_squared()
        h = dot(oc, r.direction())
        c = oc.length_squared() - self.radius**2
        
        discriminant = h**2 - a*c # value under sqrt, if > 0 => ray intersects sphere
        if discriminant < 0:
            return False
        
        sqrtd = math.sqrt(discriminant)

        # Find nearest root between tMin and tMax by trying both plus and minus values
        root = (-h - sqrtd) / a
        if root < tMin or root > tMax:
            root = (-h -sqrtd) / a
            if root < tMin or root > tMax:
                return False # root not in range
            
        rec.t = root
        rec.p = r.at(rec.t)
        # normal = (rec.p - center) / radius
        outwardNormal = vecScalarDiv(vecSub(rec.p, self.center), self.radius)
        rec.setFaceNormal(r, outwardNormal)

        return True