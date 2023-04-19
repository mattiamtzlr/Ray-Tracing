from utility import *

def writeColor(pixelColor: Color, samplesPerPixel: int) -> str:
    r = pixelColor.x()
    g = pixelColor.y()
    b = pixelColor.z()

    # divide the color by the number of samples
    scale = 1 / samplesPerPixel
    r *= scale
    g *= scale
    b *= scale

    # return the translated (0 to 255) value of each component
    return f"{int(256 * clamp(r, 0, 0.999))} {int(256 * clamp(g, 0, 0.999))} {int(256 * clamp(b, 0, 0.999))}\n"
