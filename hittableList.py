from hittable import Hittable, HitRecord
from ray import Ray
from typing import Optional

# class for a list containing all hittable objects in the scene. The list can be
# instantiated with an optional object.
class HittableList(Hittable):
    def __init__(self, object: Optional[Hittable] = None):
        self.objects = []
        if object is not None:
            self.add(object)
    
    def clear(self):
        self.objects.clear()

    def add(self, object: Hittable):
        self.objects.append(object)

    # hit method loops through all objects and tests if they are hit by the ray
    # if save time it sets the closest t value to avoid searching behind already 
    # hit objects
    def hit(self, r: Ray, tMin: float, tMax: float, rec: HitRecord) -> bool:
        tempRec = HitRecord()
        hitAnything = False
        closest = tMax

        for object in self.objects:
            if object.hit(r, tMin, closest, tempRec):
                hitAnything = True
                closest = tempRec.t
                rec = tempRec
        
        return hitAnything
