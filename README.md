# Grayscale Contrast Enhancement Filter

A Java program that improves the contrast of a grayscale image. It supports three methods:

- **Contrast stretching** (`stretch`): stretches the used gray range to fill 0 to 255.
- **Histogram equalization** (`equalize`): spreads gray levels evenly using the histogram.
- **Gamma correction** (`gamma`): brightens or darkens using a power curve.

If the input image is in color, it is converted to grayscale first.

## Requirements

JDK 8 or newer

## How to run

Compile:

```bash
javac -d out src/contrast/ContrastFilter.java
```

Run:

```bash
java -cp out contrast.ContrastFilter <input> <output.png> [stretch|equalize|gamma] [param]
```

Examples:

```bash
java -cp out contrast.ContrastFilter input.jpg output.png stretch
java -cp out contrast.ContrastFilter input.jpg output.png equalize
java -cp out contrast.ContrastFilter input.jpg output.png gamma 0.6
```

The optional `param` is the clip percentage for `stretch` (default 1) and the gamma value for `gamma` (default 0.6).

## How it works

1. Load the image and convert it to 8-bit grayscale.
2. Build a 256 entry lookup table for the chosen method.
3. Replace every pixel with its value from the table.
4. Save the result as a PNG.
