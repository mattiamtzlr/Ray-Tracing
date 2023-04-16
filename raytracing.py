import sys

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
            r = i / (IMAGE_HEIGHT - 1)
            g = j / (IMAGE_HEIGHT - 1)
            b = .25

            ir = int(255.999 * r)
            ig = int(255.999 * g)
            ib = int(255.999 * b)

            output += f"{ir} {ig} {ib}\n"
    
    f.write(output)