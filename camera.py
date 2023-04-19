from utility import *

class Camera:
    def __init__(self) -> None:
        aspectRatio = 16 / 9
        viewportHeight = 2
        viewportWidth = aspectRatio * viewportHeight
        focalLength = 1

        self.origin = Point3(0, 0, 0)
        self.horizontal = Vec3(viewportWidth, 0, 0)
        self.vertical = Vec3(0, viewportHeight, 0)
        
        # lower_left_corner = origin - horizontal/2 - vertical/2 - vec3(0, 0, focal_length)
        self.lowerLeftCorner = vecSub(
            vecSub(
                vecSub(
                    self.origin, vecScalarDiv(self.horizontal, 2)
                ), 
                vecScalarDiv(self.vertical, 2)
            ), 
            Vec3(0, 0, focalLength)
        )

    def getRay(self, u: float, v: float) -> Ray:
        # return Ray(origin, lower_left_corner + u*horizontal + v*vertical - origin)
        return Ray(self.origin, vecAdd(
            vecAdd(self.lowerLeftCorner, vecScalarMul(self.horizontal, u)),
            vecSub(vecScalarMul(self.vertical, v), self.origin)
        ))
