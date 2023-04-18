from utility import *
from color import *
from hittableList import *
from sphere import *

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

# World
world = HittableList()
world.add(Sphere(Point3(-0.5, 0, -1), 0.5))
world.add(Sphere(Point3(0, -100.5, -1), 100))
world.add(Sphere(Point3(10, 1, -15), 2))

# Camera
viewportHeight = 2
viewportWidth = ASPECT_RATIO * viewportHeight
focalLength = 1

origin = Point3(0, 0, 0)
horizontal = Vec3(viewportWidth, 0, 0)
vertical = Vec3(0, viewportHeight, 0)
# lower_left_corner = origin - horizontal/2 - vertical/2 - vec3(0, 0, focal_length)

lowerLeftCorner = vecSub(
        vecSub(
            vecSub(
                origin, vecScalarDiv(horizontal, 2)
            ), 
            vecScalarDiv(vertical, 2)
        ), 
        Vec3(0, 0, focalLength)
    )

# Render to ppm image format
with open("image.ppm", "w") as f:
    output = ""
    output += f"P3\n{IMAGE_WIDTH} {IMAGE_HEIGHT}\n255\n" # header

    # progress bar
    progress = tqdm(range(IMAGE_HEIGHT), "Generating Lines", unit=" lines")

    # values are written from top left to bottom right
    for j in range(IMAGE_HEIGHT-1, -1, -1):        
        for i in range(IMAGE_WIDTH):
            u = i / (IMAGE_WIDTH - 1)
            v = j / (IMAGE_HEIGHT - 1)

            # r = Ray(origin, lower_left_corner + u*horizontal + v*vertical - origin)
            r = Ray(origin, vecAdd(
                vecAdd(lowerLeftCorner, vecScalarMul(horizontal, u)),
                vecSub(vecScalarMul(vertical, v), origin)
            ))
            pixelColor = rayColor(r, world)
            # create color vector
            output += writeColor(pixelColor)
        progress.update(IMAGE_WIDTH - i)
    f.write(output)
 