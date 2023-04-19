from random import random

from vec3 import *
from ray import *

# constants
INFINITY = float('inf')
PI = 3.1415926535897932385

# utility functions
def degToRad(deg):
    return deg * PI / 180

def randomFloat(min, max):
    return min + (max - min) * random()