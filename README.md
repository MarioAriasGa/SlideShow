# SlideShow

The purpose of this software is watching pictures very quickly. Once chosen a folder, it creates a list of all images recursively and pre-caches a few of the next/previous so when pressing the next key the change is instant.

# Screenshot

![](/screenshots/screenshot1.jpg)

# Features

* Randomization of pictures.
* AWT and OpenGL renderer.
* Histogram view.
* Image exif information.
* Multi thread background preloading of images.
* Keyboard shortcuts for efficiency.
* Mouse zoom and pan.

# Running

`$ mvn clean package`

`$ java -cp target/slideshow-1.0.jar com.github.marioariasga.slideshow.SSLauncher`

Optionally you can pass the root folder with images that you want to recursivelly scan

# Keyboard shortcuts
* r: Randomize list of images
* ESC: Exit
* Space: Run/Pause slideshow
* +/-: Next/Previous picture in queue.
* 0: Zoom to Fit.
* 1-9: Zoom level, where 1 is smallest, 5 is actual image size, and 9 is biggest zoom.
* r: Randomize queue of images.
* v: Verbose details about image, size, exif etc.
* i: Queue information, file name and number in total queue.
* h: Image histogram.
* m: Memory usage.
* c: Cache information. Red is current image in queue, the green dots represent rest of images in the queue, and whether they are loaded or not.
* g: Force garbage collect.
