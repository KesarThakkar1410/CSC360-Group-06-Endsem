# Grayscale Contrast Filter

A small Java project that makes a picture easier to see by improving its contrast.
It was made for the CSC360 end-semester work (Group 6).

## What is the problem?

Some pictures look dull or foggy. All the shades are close together, so dark parts
and bright parts do not stand out. A photo taken in poor light is a common example.

## What does this project do?

It takes a picture and does three things:

1. It changes the picture into shades of gray. Colour pictures are fine; the colour
   is simply removed.
2. It finds the darkest spot and the brightest spot in the picture.
3. It spreads all the shades out, so the darkest spot becomes pure black and the
   brightest spot becomes pure white.

The result is a clearer picture with stronger contrast, saved as a PNG file.
This idea is called **contrast stretching**.

## The three programs

The project gives you the same idea in three forms:

| Program | What it is |
| --- | --- |
| `contrastfilter` | A command-line tool. You give it an input picture and an output name, and it saves the improved picture. It has no window. |
| `contrastgui` | A simple window. You pick a picture, see the original and the improved version side by side, and save the result. |
| `contrastslider` | A window like `contrastgui`, with a **strength** slider from 0 to 100. At 0 you see the plain gray picture, at 100 you see the full effect, and in between you see a mix. |

## Screenshots

These are from the `contrastslider` window.

**When it first opens.** Nothing is loaded yet.

<!-- SCREENSHOT 1: drag "image.png" (empty window) onto this line, then delete this line -->

**Strength 50.** The picture on the right has more contrast than the original on the
left, but it is only a half-way mix.

<!-- SCREENSHOT 2: drag the strength 50 screenshot onto this line, then delete this line -->

**Strength 100.** This is the full effect. The darkest parts are now close to black
and the brightest parts are close to white.

<!-- SCREENSHOT 3: drag the strength 100 screenshot onto this line, then delete this line -->

## What is in this repo

```
CSC360-Group-06-Endsem/
├── readme.md                     <- this file: what the project is
├── JAVA_DOCUMENTATION.md         <- how to run it, and how the code works
├── ContrastFilterTest.java       <- a JUnit test
└── contrastfilter/
    ├── contrastfilter.java       <- command-line tool
    ├── contrastgui.java          <- simple window
    └── contrastslider.java       <- window with a slider
```

## Built with

- **Java**, with no extra libraries for the main programs.
- **Java Swing** for the two windows.
- **`ImageIO`** (part of Java) for reading and saving pictures.
- **JUnit 5** for the test.
