# Grayscale Contrast Enhancement Filter

A Java implementation of three contrast enhancement techniques for grayscale images, built as a project for the **Computer Graphics** course.

The program loads an image, converts it to 8-bit grayscale, applies the chosen filter, and saves the result as a PNG. It uses only the standard Java library (no external dependencies).

## Techniques

All three filters are *point operations*: each output pixel depends only on the corresponding input pixel. Each one is implemented by building a 256-entry lookup table (LUT) and applying it to every pixel.

| Filter | Command | Formula | Best for |
|---|---|---|---|
| **Contrast stretching** | `stretch` | `new = (old − low) × 255 / (high − low)` | Washed-out images that use a narrow range of gray levels |
| **Histogram equalization** | `equalize` | `new = (cdf[old] − cdfMin) × 255 / (N − cdfMin)` | Strong, automatic contrast improvement |
| **Gamma correction** | `gamma` | `new = 255 × (old / 255)^γ` | Images that are too dark (γ < 1) or too bright (γ > 1) |

### Notes
- **Stretch** clips a percentage of the darkest and brightest pixels (default 1%) so a few outliers don't ruin the result. The optional parameter is the clip percentage.
- **Equalize** uses the cumulative distribution function (CDF) of the histogram to spread gray levels evenly. It takes no parameter.
- **Gamma** takes the gamma value as its parameter (default 0.6).

## Requirements

- JDK 8 or newer

## Project structure

```
contrast-filter/
├── README.md
├── .gitignore
├── src/
│   └── contrast/
│       └── ContrastFilter.java
├── samples/
│   ├── input/
│   └── output/
└── docs/
```

## Build and run

From the project root:

```bash
# Compile
javac -d out src/contrast/ContrastFilter.java

# Run
java -cp out contrast.ContrastFilter <input> <output.png> [stretch|equalize|gamma] [param]
```

### Examples

```bash
# Contrast stretching with default 1% clipping
java -cp out contrast.ContrastFilter samples/input/lowcontrast1.jpg result_stretch.png stretch

# Contrast stretching with 2% clipping
java -cp out contrast.ContrastFilter samples/input/lowcontrast1.jpg result_stretch2.png stretch 2

# Histogram equalization
java -cp out contrast.ContrastFilter samples/input/lowcontrast1.jpg result_equalize.png equalize

# Gamma correction with γ = 0.5
java -cp out contrast.ContrastFilter samples/input/lowcontrast1.jpg result_gamma.png gamma 0.5
```

If the input is a color image, it is converted to grayscale first. Supported input formats are those handled by Java's `ImageIO` (JPEG, PNG, BMP, GIF).

## How it works

1. **Load and convert.** The image is drawn onto a `TYPE_BYTE_GRAY` `BufferedImage`.
2. **Histogram.** The program counts how many pixels have each gray level (0 to 255).
3. **Build the LUT.** Depending on the chosen filter, a 256-entry array maps every possible input value to its output value.
4. **Apply the LUT.** Every pixel `p` is replaced with `lut[p]`, and the result is saved.

## Results

Replace the paths below with your own images.

| Original | Stretch | Equalize | Gamma |
|---|---|---|---|
| ![original](samples/input/lowcontrast1.jpg) | ![stretch](samples/output/lowcontrast1_stretch.png) | ![equalize](samples/output/lowcontrast1_equalize.png) | ![gamma](samples/output/lowcontrast1_gamma.png) |

## Possible improvements

- GUI with a live contrast slider (Swing or JavaFX)
- Histogram plots before and after filtering
- Adaptive histogram equalization (CLAHE)
- Support for color images by processing the luminance channel only

## Author

**Your Name**
Computer Graphics, Your University
Month Year

## License

This project is released under the MIT License. See `LICENSE` for details. (Delete this section if you are not including a license.)
