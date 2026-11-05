# CSC360-Group-06-Endsem
Group 6 End Sem Project: Create a filter to improve the contrast of a grayscale image

# CSC360-Group-06-Endsem

**Group 6 End Sem Project: Create a filter to improve the contrast of a grayscale image**

## Overview

Contrast is the difference in brightness between the lightest and darkest regions of an image. Low-contrast images look flat, foggy or washed out because their pixel values are squeezed into a narrow range. This project implements several image filters in Java that spread out the pixel intensities of a grayscale image to make details clearer, and compares how well each one works.

## Features

- Load an image and convert it to grayscale
- **Contrast stretching** (min-max linear normalization)
- **Histogram equalization** (global)
- **Gamma correction**
- **Unsharp masking** for local contrast and detail
- **Convolution engine** supporting custom kernels (blur, sharpen)
- Filter pipeline to chain multiple filters
- Evaluation metrics: standard deviation, entropy and histogram plots
- Side-by-side before/after comparison [GUI, if implemented]

## Techniques Used

| Technique | Idea |
|-----------|------|
| Contrast stretching | Linearly maps the range [min, max] of pixel values to [0, 255] |
| Histogram equalization | Uses the cumulative histogram to redistribute intensities evenly |
| Gamma correction | Applies a power-law curve `out = 255 × (in / 255)^γ` to adjust mid-tones |
| Unsharp masking | `result = original + amount × (original − blurred)` |

## Tech Stack

- Language: Java [version, e.g. 17]
- IDE: IntelliJ IDEA
- Libraries: `javax.imageio`, `java.awt.image` [and Swing for the GUI]
- Version control: Git and GitHub

## Project Structure

```
CSC360-Group-06-Endsem/
├── src/
│   ├── Main.java
│   ├── ImageFilter.java
│   ├── GrayscaleConverter.java
│   ├── ContrastStretch.java
│   ├── HistogramEqualization.java
│   ├── GammaCorrection.java
│   └── ConvolutionFilter.java
├── images/        # sample input images
├── output/        # filtered results
└── README.md
```

## How to Run

1. Clone the repository:
   ```
   git clone https://github.com/KesarThakkar1410/CSC360-Group-06-Endsem.git
   ```
2. Open the folder in IntelliJ IDEA.
3. Set the Project SDK to JDK [17 or higher] (File → Project Structure).
4. Mark `src` as Sources Root.
5. Place an input image in the `images/` folder (e.g. `test.jpg`).
6. Run `Main.java`. The results are saved in the `output/` folder.

## Results

Add before/after images and your metrics table here.

| Image | Filter | Std. Deviation | Entropy |
|-------|--------|----------------|---------|
| [image name] | Original | | |
| [image name] | Contrast stretching | | |
| [image name] | Histogram equalization | | |

## Limitations and Future Work

- Global histogram equalization can over-amplify noise and wash out images with both very bright and very dark regions.
- Future work: CLAHE (contrast limited adaptive histogram equalization), color image support, real-time preview.

## Team Members

| Name | Role / Contribution |
|------|---------------------|
| [Member 1] | [e.g. image loading, pipeline] |
| [Member 2] | [e.g. point filters] |
| [Member 3] | [e.g. convolution and unsharp mask] |
| [Member 4] | [e.g. GUI and evaluation] |

## Course

CSC360, [University name], [semester/year]
