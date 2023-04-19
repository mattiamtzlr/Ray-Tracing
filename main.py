from utility import *
from color import *
from hittableList import *
from sphere import *
from camera import *

try:
    from tqdm import tqdm
except ModuleNotFoundError:
    print("This script requires tqdm to be installed.\n Install it using 'pip install tqdm'.")
    exit(-1)

# returns a lerp between light purple and white as a background
def rayColor(r: Ray, world: Hittable) -> Color:
    # HitRecord from world with hit info of all objects
    rec = HitRecord()
    if world.hit(r, 0, INFINITY, rec):
        return vecScalarMul(vecAdd(rec.normal, Color(1, 1, 1)), 0.5)

    # if ray doesn't intersect sphere color the sky normally
    unitDirection = unit_vector(r.direction())
    t = 0.5 * (unitDirection.y() + 1)
    # return (1 - t) * Color(1, 1, 1) + t * Color(0.5, 0.7, 1.0)
    return vecAdd(
            vecScalarMul(Color(1, 1, 1), (1 - t)), 
            vecScalarMul(Color(.5, .7, 1), t)
        )

# Image
ASPECT_RATIO = 16 / 9
IMAGE_WIDTH = 400
IMAGE_HEIGHT = int(IMAGE_WIDTH / ASPECT_RATIO)
SAMPLES_PER_PIXEL = 15;

# World
world = HittableList()
world.add(Sphere(Point3(-0.5, 0, -1), 0.5))
world.add(Sphere(Point3(0, -100.5, -1), 100))
world.add(Sphere(Point3(10, 1, -15), 2))

# Camera
cam = Camera()

# Render to ppm image format
with open("image.ppm", "w") as f:
    output = ""
    output += f"P3\n{IMAGE_WIDTH} {IMAGE_HEIGHT}\n255\n" # header

    # progress bar
    progress = tqdm(range(IMAGE_HEIGHT), "Generating Lines", unit=" lines")

    # values are written from top left to bottom right
    for j in range(IMAGE_HEIGHT-1, -1, -1):        
        for i in range(IMAGE_WIDTH):
            pixelColor = Color(0, 0, 0)
            # sample each pixel multiple times
            for s in range(SAMPLES_PER_PIXEL):
                u = (i + randomFloat(0, 1)) / (IMAGE_WIDTH-1)
                v = (j + randomFloat(0, 1)) / (IMAGE_HEIGHT-1)
                r = cam.getRay(u, v)
                pixelColor = vecAdd(pixelColor, rayColor(r, world))

            output += writeColor(pixelColor, SAMPLES_PER_PIXEL)
        progress.update(IMAGE_WIDTH - i)
    f.write(output)
 