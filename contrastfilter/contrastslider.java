import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class contrastslider {

    static int[][] gray = null;
    static int min = 255;
    static int max = 0;
    static BufferedImage result = null;

    static JLabel leftlabel = new JLabel("upload an image", SwingConstants.CENTER);
    static JLabel rightlabel = new JLabel("output will show here", SwingConstants.CENTER);
    static JSlider slider = new JSlider(0, 100, 100);
    static JLabel valuelabel = new JLabel("strength: 100");

    static void load(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        gray = new int[h][w];
        min = 255;
        max = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 255;
                int g = (rgb >> 8) & 255;
                int b = rgb & 255;
                int v = (r + g + b) / 3;
                gray[y][x] = v;
                if (v < min) min = v;
                if (v > max) max = v;
            }
        }
    }

    static BufferedImage apply(int strength) {
        int h = gray.length;
        int w = gray[0].length;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int v = gray[y][x];
                int stretched = v;
                if (max > min) {
                    stretched = (v - min) * 255 / (max - min);
                }
                int n = v + (int) ((stretched - v) * strength / 100.0);
                if (n < 0) n = 0;
                if (n > 255) n = 255;
                out.setRGB(x, y, (n << 16) | (n << 8) | n);
            }
        }
        return out;
    }

    static ImageIcon fit(BufferedImage img, int maxw, int maxh) {
        double scale = Math.min((double) maxw / img.getWidth(), (double) maxh / img.getHeight());
        if (scale > 1) scale = 1;
        int nw = (int) (img.getWidth() * scale);
        int nh = (int) (img.getHeight() * scale);
        Image small = img.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
        return new ImageIcon(small);
    }

    static void update() {
        valuelabel.setText("strength: " + slider.getValue());
        if (gray == null) return;
        result = apply(slider.getValue());
        rightlabel.setText("");
        rightlabel.setIcon(fit(result, 470, 400));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("grayscale contrast filter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);

            JPanel images = new JPanel(new GridLayout(1, 2, 10, 10));
            leftlabel.setBorder(BorderFactory.createTitledBorder("original"));
            rightlabel.setBorder(BorderFactory.createTitledBorder("output"));
            images.add(leftlabel);
            images.add(rightlabel);

            JButton uploadbutton = new JButton("upload image");
            JButton resetbutton = new JButton("reset");
            JButton savebutton = new JButton("save output");

            slider.setPreferredSize(new Dimension(300, 40));

            slider.addChangeListener(e -> update());

            uploadbutton.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                        if (img == null) {
                            JOptionPane.showMessageDialog(frame, "this file is not an image");
                            return;
                        }
                        load(img);
                        leftlabel.setText("");
                        leftlabel.setIcon(fit(img, 470, 400));
                        slider.setValue(100);
                        update();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "could not open the image");
                    }
                }
            });

            resetbutton.addActionListener(e -> slider.setValue(100));

            savebutton.addActionListener(e -> {
                if (result == null) {
                    JOptionPane.showMessageDialog(frame, "upload an image first");
                    return;
                }
                JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = chooser.getSelectedFile();
                        if (!file.getName().toLowerCase().endsWith(".png")) {
                            file = new File(file.getAbsolutePath() + ".png");
                        }
                        ImageIO.write(result, "png", file);
                        JOptionPane.showMessageDialog(frame, "saved");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "could not save the image");
                    }
                }
            });

            JPanel controls = new JPanel();
            controls.add(valuelabel);
            controls.add(slider);

            JPanel buttons = new JPanel();
            buttons.add(uploadbutton);
            buttons.add(resetbutton);
            buttons.add(savebutton);

            JPanel bottom = new JPanel(new GridLayout(2, 1));
            bottom.add(controls);
            bottom.add(buttons);

            frame.add(images, BorderLayout.CENTER);
            frame.add(bottom, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }
}