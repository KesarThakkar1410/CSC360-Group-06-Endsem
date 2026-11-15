// Import classes needed for image handling and the graphical interface.
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

// This class creates a graphical interface for the contrast filter.
public class contrastgui {

    // Store the enhanced image so it can be displayed and saved.
    static BufferedImage result = null;

    // Labels used to display the original and enhanced images.
    static JLabel leftlabel = new JLabel("upload a grayscale image", SwingConstants.CENTER);
    static JLabel rightlabel = new JLabel("output will be shown here", SwingConstants.CENTER);

    // This method converts an image to grayscale and improves its contrast.
    static BufferedImage stretch(BufferedImage img) {

        // Get the width and height of the input image.
        int w = img.getWidth();
        int h = img.getHeight();

        // Create a 2D array to store grayscale values.
        int[][] gray = new int[h][w];

        // Set the starting minimum and maximum values.
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

                // Calculate the grayscale value using the average of RGB.
                int v = (r + g + b) / 3;

                // Store the grayscale value in the array.
                gray[y][x] = v;

                // Find the minimum and maximum grayscale values.
                if (v < min) min = v;
                if (v > max) max = v;
            }
        }

        // Create a new image to store the enhanced result.
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // Apply contrast stretching to each pixel.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                // Get the original grayscale value.
                int v = gray[y][x];

                // Start with the original value.
                int n = v;

                // Stretch the grayscale values between 0 and 255.
                if (max > min) {
                    n = (v - min) * 255 / (max - min);
                }

                // Set the same new value for all three RGB channels.
                out.setRGB(x, y, (n << 16) | (n << 8) | n);
            }
        }

        // Return the enhanced image.
        return out;
    }

    // This method resizes an image to fit inside the given width and height.
    static ImageIcon fit(BufferedImage img, int maxw, int maxh) {

        // Calculate the scaling factor while keeping the original proportions.
        double scale = Math.min((double) maxw / img.getWidth(), (double) maxh / img.getHeight());

        // Do not enlarge the image if it is smaller than the available space.
        if (scale > 1) scale = 1;

        // Calculate the new width and height.
        int nw = (int) (img.getWidth() * scale);
        int nh = (int) (img.getHeight() * scale);

        // Resize the image smoothly.
        Image small = img.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);

        // Return the resized image as an ImageIcon.
        return new ImageIcon(small);
    }

    // Main method where the graphical interface starts.
    public static void main(String[] args) {

        // Run the interface on the Swing event thread.
        SwingUtilities.invokeLater(() -> {

            // Create the main application window.
            JFrame frame = new JFrame("grayscale contrast filter");

            // Close the application when the window is closed.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the size and position of the window.
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);

            // Create a panel to display the two images side by side.
            JPanel images = new JPanel(new GridLayout(1, 2, 10, 10));

            // Add titles around the original and output image areas.
            leftlabel.setBorder(BorderFactory.createTitledBorder("original"));
            rightlabel.setBorder(BorderFactory.createTitledBorder("output"));

            // Add both image labels to the panel.
            images.add(leftlabel);
            images.add(rightlabel);

            // Create buttons to upload an image and save the result.
            JButton uploadbutton = new JButton("upload image");
            JButton savebutton = new JButton("save output");

            // Action performed when the upload button is clicked.
            uploadbutton.addActionListener(e -> {

                // Open a file chooser to select an image.
                JFileChooser chooser = new JFileChooser();

                if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    try {

                        // Read the selected image.
                        BufferedImage img = ImageIO.read(chooser.getSelectedFile());

                        // Check whether the selected file is a valid image.
                        if (img == null) {
                            JOptionPane.showMessageDialog(frame, "this file is not an image");
                            return;
                        }

                        // Apply the contrast filter to the selected image.
                        result = stretch(img);

                        // Clear the text from both image labels.
                        leftlabel.setText("");
                        rightlabel.setText("");

                        // Display the original and enhanced images.
                        leftlabel.setIcon(fit(img, 470, 450));
                        rightlabel.setIcon(fit(result, 470, 450));

                    } catch (Exception ex) {

                        // Show an error message if the image cannot be opened.
                        JOptionPane.showMessageDialog(frame, "could not open the image");
                    }
                }
            });

            // Action performed when the save button is clicked.
            savebutton.addActionListener(e -> {

                // Check whether an image has been uploaded and processed.
                if (result == null) {
                    JOptionPane.showMessageDialog(frame, "upload an image first");
                    return;
                }

                // Open a file chooser to select where to save the output.
                JFileChooser chooser = new JFileChooser();

                if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    try {

                        // Get the selected file.
                        File file = chooser.getSelectedFile();

                        // Add the PNG extension if it is not already present.
                        if (!file.getName().toLowerCase().endsWith(".png")) {
                            file = new File(file.getAbsolutePath() + ".png");
                        }

                        // Save the enhanced image.
                        ImageIO.write(result, "png", file);

                        // Show a message after saving successfully.
                        JOptionPane.showMessageDialog(frame, "saved");

                    } catch (Exception ex) {

                        // Show an error message if the image cannot be saved.
                        JOptionPane.showMessageDialog(frame, "could not save the image");
                    }
                }
            });

            // Create a panel for the buttons.
            JPanel buttons = new JPanel();

            // Add the upload and save buttons to the panel.
            buttons.add(uploadbutton);
            buttons.add(savebutton);

            // Add the image panel in the center and buttons at the bottom.
            frame.add(images, BorderLayout.CENTER);
            frame.add(buttons, BorderLayout.SOUTH);

            // Make the application window visible.
            frame.setVisible(true);
        });
    }
}
