import sys
from vec3 import *
from color import *

# image size
IMAGE_WIDTH = 256
IMAGE_HEIGHT = 256

# render to ppm image format
with open("image.ppm", "w") as f:
    output = ""
    output += f"P3\n{IMAGE_WIDTH} {IMAGE_HEIGHT}\n255\n" # header

    # values are written from top left to bottom right
    for j in range(IMAGE_HEIGHT-1, -1, -1):
        # progress
        sys.stderr.write("\rScanlines remaining: {} ".format(j))
        sys.stderr.flush()
        
        for i in range(IMAGE_WIDTH):
            # create color vector
            pixelColor = Color(i / (IMAGE_HEIGHT - 1), j / (IMAGE_HEIGHT - 1), .25)
            output += writeColor(pixelColor)
    
    f.write(output)
    