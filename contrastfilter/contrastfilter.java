// Import classes needed to read and save images.
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

// This class applies contrast enhancement to an image.
public class contrastfilter {

    // Main method where the program starts.
    public static void main(String[] args) throws Exception {

        // Check whether the input and output file names are provided.
        if (args.length < 2) {
            System.out.println("use: java contrastfilter input.jpg output.png");
            return;
        }

        // Read the input image from the given file.
        BufferedImage img = ImageIO.read(new File(args[0]));

        // Get the width and height of the image.
        int w = img.getWidth();
        int h = img.getHeight();

        // Create a 2D array to store grayscale values of each pixel.
        int[][] gray = new int[h][w];

        // Set initial minimum and maximum pixel values.
        int min = 255;
        int max = 0;

        // Go through every pixel in the image.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                // Get the RGB value of the current pixel.
                int rgb = img.getRGB(x, y);

                // Extract the red, green, and blue values.
                int r = (rgb >> 16) & 255;
                int g = (rgb >> 8) & 255;
                int b = rgb & 255;

                // Calculate the grayscale value by taking the average.
                int v = (r + g + b) / 3;

                // Store the grayscale value in the array.
                gray[y][x] = v;

                // Find the darkest and brightest pixel values.
                if (v < min) min = v;
                if (v > max) max = v;
            }
        }

        // Create a new image to store the enhanced result.
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // Go through every pixel again to adjust its contrast.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                // Get the original grayscale value.
                int v = gray[y][x];

                // Keep the original value as the starting value.
                int n = v;

                // Apply contrast stretching if the pixel values are not all the same.
                if (max > min) {
                    n = (v - min) * 255 / (max - min);
                }

                // Set the new grayscale value for the red, green, and blue channels.
                int rgb = (n << 16) | (n << 8) | n;

                // Store the updated pixel in the output image.
                out.setRGB(x, y, rgb);
            }
        }

        // Save the enhanced image in PNG format.
        ImageIO.write(out, "png", new File(args[1]));

        // Display the darkest and brightest values found in the original image.
        System.out.println("done. darkest was " + min + ", brightest was " + max);
    }
}
