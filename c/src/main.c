#include <stdio.h>
#include <unistd.h>

#define OUTPUT_PPM "output.ppm"
#define OUTPUT OUTPUT_PPM

int main(void) {
    const int image_width = 256;
    const int image_height = 256;

    FILE *out = fopen(OUTPUT, "w");

    /* P3                   // ASCII colors
     * <width> <height>     // in pixels
     * 255                  // use max color */
    fprintf(out, "P3\n%d %d\n255\n", image_width, image_height);

    /* write left-to-right, top-to-bottom */
    for (int row = 0; row < image_height; row++) {
        /* progress indicator */
        printf("\rScanlines remaining: %d", image_height - row);
        fflush(stdout);

        for (int col = 0; col < image_width; col++) {
            /* real values, ranging from 0.0 to 1.0 */
            double r = (double)col / (image_width - 1);
            double g = (double)row / (image_height - 1);
            double b = (double)(image_width - col) / (image_width - 1);

            /* scaled values, ranging from 0 to 255, for writing */
            int ir = (int)(255.999 * r);
            int ig = (int)(255.999 * g);
            int ib = (int)(255.999 * b);

            fprintf(out, "%d %d %d\n", ir, ig, ib);
        }
    }

    printf("\rSuccessfully wrote to %s\n", OUTPUT);
    fclose(out);
    return 0;
}
