import math

class Vec3:
    def __init__(self, e0=0, e1=0, e2=0) -> None:
        self.e = [e0, e1, e2]

    def x(self):
        return self.e[0]
    
    def y(self):
        return self.e[1]

    def z(self):
        return self.e[2]
    
    def __neg__(self):
        return Vec3(-self.e[0], -self.e[1], -self.e[2])
    
    def __getComp__(self, comp):
        return self.e[comp]

    def __setComp__(self, comp, val):
        self.e[comp] = val

    def __iadd__(self, vec):
        self.e[0] += vec.e[0]
        self.e[1] += vec.e[1]
        self.e[2] += vec.e[2]
        return self
    
    def __imul__(self, t):
        self.e[0] *= t
        self.e[1] *= t
        self.e[2] *= t
        return self
    
    def __idiv__(self, t):
        return self.__imul__(1/t)
    
    def length(self):
        return math.sqrt(self.length_squared())

    def length_squared(self):
        return self.e[0] ** 2 + self.e[1] ** 2 + self.e[2] ** 2   
    
# Type aliases
Point3 = Vec3
Color = Vec3

# Utility Functions
def vecToString(v: Vec3):
    return "".join(v.e)

def vecAdd(u: Vec3, v: Vec3):
    return u.__iadd__(v)

def vecSub(u: Vec3, v: Vec3):
    return u.__iadd__(v.__neg__)

def vecMul(u: Vec3, v: Vec3):
    return Vec3(u.e[0] * v.e[0], u.e[1] * v.e[1], u.e[2] * v.e[2])

def vecScalarMul(v: Vec3, t):
    return v.__imul__(t)

def vecScalarDiv(v: Vec3, t):
    return v.__imul__(1/t)

def dot(u: Vec3, v: Vec3):
    return u.e[0] * v.e[0] + u.e[1] * v.e[1] + u.e[2] * v.e[2]

def cross(u: Vec3, v: Vec3):
    return Vec3(
        u.e[1] * v.e[2] - u.e[2] * v.e[1],
        u.e[2] * v.e[0] - u.e[0] * v.e[2],
        u.e[0] * v.e[1] - u.e[1] * v.e[0]
    )

def unit_vector(v: Vec3):
    return vecScalarDiv(v, v.length())