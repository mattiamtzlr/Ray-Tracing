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

### Image for Chapter 2.3
It appears as if nothing much has changed between this image and the ones for the to previous chapters, however thanks to the implementation of Bounding Volume Hierarchies the rendering was done about 4 times faster, even with more spheres than before.