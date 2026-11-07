import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class contrastfilter {

    static BufferedImage originalImage = null;
    static BufferedImage enhancedImage = null;
    static JLabel leftLabel;
    static JLabel rightLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> buildWindow());
    }

    static void buildWindow() {
        JFrame frame = new JFrame("Contrast Improver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 680);
        frame.setLocationRelativeTo(null);

        // Top bar: insert button
        JButton openButton = new JButton("Insert your grayscale image here");
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(openButton);

        leftLabel = new JLabel("Insert a grayscale image to begin", SwingConstants.CENTER);
        rightLabel = new JLabel("The improved image will appear here", SwingConstants.CENTER);

        // Left side: original image, improve button below it
        JButton improveButton = new JButton("Improve Contrast");
        JPanel leftBottom = new JPanel();
        leftBottom.add(improveButton);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Original"));
        leftPanel.add(leftLabel, BorderLayout.CENTER);
        leftPanel.add(leftBottom, BorderLayout.SOUTH);

        // Right side: improved image, save button below it
        JButton saveButton = new JButton("Save Improved Image");
        JPanel rightBottom = new JPanel();
        rightBottom.add(saveButton);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Improved"));
        rightPanel.add(rightLabel, BorderLayout.CENTER);
        rightPanel.add(rightBottom, BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));
        center.add(leftPanel);
        center.add(rightPanel);

        frame.add(topBar, BorderLayout.NORTH);
        frame.add(center, BorderLayout.CENTER);

        openButton.addActionListener(e -> openImage(frame));
        improveButton.addActionListener(e -> improveImage(frame));
        saveButton.addActionListener(e -> saveImage(frame));

        frame.setVisible(true);
    }

    static void openImage(JFrame frame) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose an image");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Images (png, jpg, jpeg, bmp, gif)", "png", "jpg", "jpeg", "bmp", "gif"));
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            BufferedImage img = ImageIO.read(chooser.getSelectedFile());
            if (img == null) {
                JOptionPane.showMessageDialog(frame, "This file is not a valid image.");
                return;
            }
            originalImage = img;
            enhancedImage = null;
            leftLabel.setText(null);
            leftLabel.setIcon(scaleToFit(img, 450, 450));
            rightLabel.setIcon(null);
            rightLabel.setText("The improved image will appear here");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Could not open this file.");
        }
    }

    // Runs the contrast filter and shows the result on the right side
    static void improveImage(JFrame frame) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(frame, "Please insert an image first.");
            return;
        }

        BufferedImage gray = toGrayscale(originalImage);
        int[] hist = buildHistogram(gray);
        int[] cdf = buildCdf(hist);
        int total = gray.getWidth() * gray.getHeight();
        int[] table = buildTable(cdf, total);
        enhancedImage = applyTable(gray, table);

        rightLabel.setText(null);
        rightLabel.setIcon(scaleToFit(enhancedImage, 450, 450));
    }

    // Saves the improved image as a PNG file
    static void saveImage(JFrame frame) {
        if (enhancedImage == null) {
            JOptionPane.showMessageDialog(frame, "Please improve an image first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save improved image");
        chooser.setSelectedFile(new File("improved.png"));
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(file.getAbsolutePath() + ".png");
        }

        if (file.exists()) {
            int answer = JOptionPane.showConfirmDialog(frame,
                    "This file already exists. Replace it?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            ImageIO.write(enhancedImage, "png", file);
            JOptionPane.showMessageDialog(frame, "Saved:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Could not save the image.");
        }
    }

    // Shrinks a big image so it fits the window (display only)
    static ImageIcon scaleToFit(BufferedImage img, int maxW, int maxH) {
        double scale = Math.min((double) maxW / img.getWidth(),
                                (double) maxH / img.getHeight());
        scale = Math.min(scale, 1.0);
        int w = (int) (img.getWidth() * scale);
        int h = (int) (img.getHeight() * scale);
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // ---------- Filter code (unchanged) ----------

    static BufferedImage toGrayscale(BufferedImage src) {
        BufferedImage gray = new BufferedImage(
                src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = gray.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return gray;
    }

    static int[] buildHistogram(BufferedImage img) {
        int[] hist = new int[256];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int value = img.getRaster().getSample(x, y, 0);
                hist[value]++;
            }
        }
        return hist;
    }

    static int[] buildCdf(int[] hist) {
        int[] cdf = new int[256];
        int running = 0;
        for (int i = 0; i < 256; i++) {
            running += hist[i];
            cdf[i] = running;
        }
        return cdf;
    }

    static int[] buildTable(int[] cdf, int total) {
        int cdfMin = 0;
        for (int i = 0; i < 256; i++) {
            if (cdf[i] > 0) {
                cdfMin = cdf[i];
                break;
            }
        }

        int[] table = new int[256];
        int range = total - cdfMin;
        for (int i = 0; i < 256; i++) {
            if (range == 0) {
                table[i] = i;
            } else {
                double scaled = (double) (cdf[i] - cdfMin) / range * 255.0;
                table[i] = (int) Math.round(Math.max(0, scaled));
            }
        }
        return table;
    }

    static BufferedImage applyTable(BufferedImage gray, int[] table) {
        BufferedImage out = new BufferedImage(
                gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < gray.getHeight(); y++) {
            for (int x = 0; x < gray.getWidth(); x++) {
                int oldValue = gray.getRaster().getSample(x, y, 0);
                out.getRaster().setSample(x, y, 0, table[oldValue]);
            }
        }
        return out;
    }
}
