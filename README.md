# Ray Tracing in Java

Simple ray tracer (pathtracer) coded in Java following [Peter Shirley's "Ray Tracing in One Weekend" Series](https://raytracing.github.io/).  
Shirley originally coded his version in C++ which I don't know at all, so I used Java for my version. I did most of the transpiling on my own but also used _ChatGPT_ by _OpenAI_ to help sometimes.

_I originally coded everything in Python but that was too slow, which is why I ported everything to Java after chapter 8.4.  
The python script requires tqdm to be installed. Install it using `pip install tqdm`._

At the time of writing this (18.05.2023), I have finished the first book in the series (see `/images/chapter_13.png` for the result) and will be continuing with at least the second book and maybe even with the third one.  
Sadly the ray tracer is again really slow when rendering with high-quality settings. In low-quality and with a small image size it runs quite fast, though.

I saved some images of the progress of the ray tracer in the `/images` directory. The name of the image corresponds to the chapter, after which I generated that image.

## Notes / Remarks
Some notes and remarks to different chapters or changes.

### Coordinate System
As the position of the viewpoint and everything that needs to be rendered has to be hardocoded, it is important to understand how the coordinate system used works.
To help understand see the image below:  

![coordinate system](./coord_system.png)

The example on the right shows a sphere (red) at the coordinates (2, 1, 1).  
To capture this sphere from the front the viewpoint needs to be "in front" of the sphere, meaning itss z-coordinate needs to be bigger, thus its coordinates are (2, 1, 5).  
The point that the camera looks at from the viewpoint is "behind" the sphere at (2, 1, 0) (on the x/y-plane).

### Image for Chapter 2.3
It appears as if nothing much has changed between this image and the ones for the to previous chapters, however thanks to the implementation of Bounding Volume Hierarchies the rendering was done about 4 times faster, even with more spheres than before.