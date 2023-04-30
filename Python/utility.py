from random import random

# constants
INFINITY = float('inf')
PI = 3.1415926535897932385

# utility functions
def degToRad(deg):
    return deg * PI / 180

def randomFloat(min, max):
    return min + (max - min) * random()

def clamp(x, min, max):
    """Clamps x between min and max, if outside the range, the return value is min or max respectively."""
    if x < min: return min
    if x > max: return max
    return x