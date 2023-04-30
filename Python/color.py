from utility import *
from vec3 import *
from math import sqrt

def writeColor(pixelColor: Color, samplesPerPixel: int) -> str:
    r = pixelColor.x()
    g = pixelColor.y()
    b = pixelColor.z()

    # divide the color by the number of samples and gamma correct for gamma = 2
    # -> raising color to the power 1/gamma -> 1/2 -> sqrt
    scale = 1 / samplesPerPixel
    r = sqrt(scale * r)
    g = sqrt(scale * g)
    b = sqrt(scale * b)

    # return the translated (0 to 255) value of each component
    return f"{int(256 * clamp(r, 0, 0.999))} {int(256 * clamp(g, 0, 0.999))} {int(256 * clamp(b, 0, 0.999))}\n"
