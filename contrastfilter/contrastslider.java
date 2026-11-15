// Import classes needed for image handling and the graphical interface.
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

// This class provides a contrast filter with an adjustable strength slider.
public class contrastslider {

    // Store the grayscale values of the uploaded image.
    static int[][] gray = null;

    // Store the minimum and maximum grayscale values.
    static int min = 255;
    static int max = 0;

    // Store the enhanced image.
    static BufferedImage result = null;

    // Labels used to display the original and enhanced images.
    static JLabel leftlabel = new JLabel("upload an image", SwingConstants.CENTER);
    static JLabel rightlabel = new JLabel("output will show here", SwingConstants.CENTER);

    // Create a slider with values from 0 to 100 and an initial value of 100.
    static JSlider slider = new JSlider(0, 100, 100);

    // Display the current contrast strength.
    static JLabel valuelabel = new JLabel("strength: 100");

    // This method reads the image and stores its grayscale values.
    static void load(BufferedImage img) {

        // Get the width and height of the image.
        int w = img.getWidth();
        int h = img.getHeight();

        // Create a 2D array to store grayscale values.
        gray = new int[h][w];

        // Reset the minimum and maximum values.
        min = 255;
        max = 0;

        // Go through every pixel in the image.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                // Get the RGB value of the current pixel.
                int rgb = img.getRGB(x, y);

                // Extract the red, green, and blue values.
                int r = (rgb >> 16) & 255;
                int g = (rgb >> 8) & 255;
                int b = rgb & 255;

                // Calculate the grayscale value using the RGB average.
                int v = (r + g + b) / 3;

                // Store the grayscale value in the array.
                gray[y][x] = v;

                // Find the darkest and brightest pixel values.
                if (v < min) min = v;
                if (v > max) max = v;
            }
        }
    }

    // This method applies contrast enhancement based on the slider strength.
    static BufferedImage apply(int strength) {

        // Get the height and width of the stored grayscale image.
        int h = gray.length;
        int w = gray[0].length;

        // Create a new image to store the result.
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        // Process every pixel in the image.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                // Get the original grayscale value.
                int v = gray[y][x];

                // Start with the original grayscale value.
                int stretched = v;

                // Calculate the contrast-stretched value.
                if (max > min) {
                    stretched = (v - min) * 255 / (max - min);
                }

                // Mix the original and stretched values based on slider strength.
                int n = v + (int) ((stretched - v) * strength / 100.0);

                // Make sure the new value stays between 0 and 255.
                if (n < 0) n = 0;
                if (n > 255) n = 255;

                // Set the same grayscale value for all RGB channels.
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

    // This method updates the slider value and displays the latest output.
    static void update() {

        // Update the text to show the current slider strength.
        valuelabel.setText("strength: " + slider.getValue());

        // Stop if no image has been uploaded.
        if (gray == null) return;

        // Apply the filter using the current slider value.
        result = apply(slider.getValue());

        // Clear the text and display the updated image.
        rightlabel.setText("");
        rightlabel.setIcon(fit(result, 470, 400));
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
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);

            // Create a panel to display the two images side by side.
            JPanel images = new JPanel(new GridLayout(1, 2, 10, 10));

            // Add titles around the original and output image areas.
            leftlabel.setBorder(BorderFactory.createTitledBorder("original"));
            rightlabel.setBorder(BorderFactory.createTitledBorder("output"));

            // Add both image labels to the panel.
            images.add(leftlabel);
            images.add(rightlabel);

            // Create buttons for uploading, resetting, and saving.
            JButton uploadbutton = new JButton("upload image");
            JButton resetbutton = new JButton("reset");
            JButton savebutton = new JButton("save output");

            // Set the preferred size of the contrast slider.
            slider.setPreferredSize(new Dimension(300, 40));

            // Call the update method whenever the slider value changes.
            slider.addChangeListener(e -> update());

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

                        // Read and store the grayscale values of the image.
                        load(img);

                        // Clear the text and display the original image.
                        leftlabel.setText("");
                        leftlabel.setIcon(fit(img, 470, 400));

                        // Set the slider to 100 and update the output.
                        slider.setValue(100);
                        update();

                    } catch (Exception ex) {

                        // Show an error message if the image cannot be opened.
                        JOptionPane.showMessageDialog(frame, "could not open the image");
                    }
                }
            });

            // Action performed when the reset button is clicked.
            resetbutton.addActionListener(e -> slider.setValue(100));

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

            // Create a panel to hold the slider and its value label.
            JPanel controls = new JPanel();

            // Add the strength label and slider to the panel.
            controls.add(valuelabel);
            controls.add(slider);

            // Create a panel to hold the three buttons.
            JPanel buttons = new JPanel();

            // Add the buttons to the panel.
            buttons.add(uploadbutton);
            buttons.add(resetbutton);
            buttons.add(savebutton);

            // Create a bottom panel with two rows for controls and buttons.
            JPanel bottom = new JPanel(new GridLayout(2, 1));

            // Add the controls and buttons to the bottom panel.
            bottom.add(controls);
            bottom.add(buttons);

            // Add the image panel in the center and controls at the bottom.
            frame.add(images, BorderLayout.CENTER);
            frame.add(bottom, BorderLayout.SOUTH);

            // Make the application window visible.
            frame.setVisible(true);
        });
    }
}
