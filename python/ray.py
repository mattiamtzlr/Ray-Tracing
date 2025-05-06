from vec3 import *

class Ray:
    """Ray with an Origin Point and a directon vector. Changing the parameter t moves the resulting Point along the ray. (Linear interpolation)"""
    def __init__(self, origin=Point3(0, 0, 0), direction=Vec3(0, 0, 0)) -> None:
        self.orig = origin
        self.dir = direction
    
    def origin(self):
        return self.orig
    
    def direction(self):
        return self.dir
    
    def at(self, t):
        return vecAdd(self.orig, vecScalarMul(self.dir, t))
