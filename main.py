from utility import *
from vec3 import *
from ray import *
from color import *
from hittableList import *
from sphere import *
from camera import *

try:
    from tqdm import tqdm
except ModuleNotFoundError:
    print("This script requires tqdm to be installed.\n Install it using 'pip install tqdm'.")
    exit(-1)

# method to figure out what color the ray should return
def rayColor(r: Ray, world: Hittable, depth: int) -> Color:
    # safe guard because of recursion
    if depth <= 0:
        return Color(0, 0, 0) # black

    # HitRecord from world with hit info of all objects
    rec = HitRecord()
    if world.hit(r, 0.001, INFINITY, rec):
        # use random bounce to simulate diffuse material
        target = vecAdd(
            vecAdd(
            rec.p, rec.normal
            ),
            randomVecInUnitSphere()
        )
        # use rayColor recursively with the new resulting ray
        # return 0.5 * ray_color(ray(rec.p, target - rec.p), world, depth-1)
        return vecScalarMul(
            rayColor(
                Ray(
                    rec.p,
                    vecSub(target, rec.p)
                ), 
                world,
                depth-1
            ),
            0.5
        )

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
SAMPLES_PER_PIXEL = 5 # set to at least 30 for good images -> takes long as fuck
MAX_DEPTH = 20

# World
world = HittableList()
world.add(Sphere(Point3(-0.5, 0, -1.2), 0.5))
world.add(Sphere(Point3(-0.3, .6, -0.9), 0.15))
world.add(Sphere(Point3(4, 1.5, -7), 1))
world.add(Sphere(Point3(0, -200.5, -10), 200)) # "ground"

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
                pixelColor = vecAdd(pixelColor, rayColor(r, world, MAX_DEPTH))

            output += writeColor(pixelColor, SAMPLES_PER_PIXEL)
        progress.update(IMAGE_WIDTH - i)
    f.write(output)
 