import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class contrastgui {

    static BufferedImage result = null;
    static JLabel leftlabel = new JLabel("upload a grayscale image", SwingConstants.CENTER);
    static JLabel rightlabel = new JLabel("output will be shown here", SwingConstants.CENTER);

    static BufferedImage stretch(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[][] gray = new int[h][w];
        int min = 255;
        int max = 0;

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

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int v = gray[y][x];
                int n = v;
                if (max > min) {
                    n = (v - min) * 255 / (max - min);
                }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("grayscale contrast filter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);

            JPanel images = new JPanel(new GridLayout(1, 2, 10, 10));
            leftlabel.setBorder(BorderFactory.createTitledBorder("original"));
            rightlabel.setBorder(BorderFactory.createTitledBorder("output"));
            images.add(leftlabel);
            images.add(rightlabel);

            JButton uploadbutton = new JButton("upload image");
            JButton savebutton = new JButton("save output");

            uploadbutton.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                        if (img == null) {
                            JOptionPane.showMessageDialog(frame, "this file is not an image");
                            return;
                        }
                        result = stretch(img);
                        leftlabel.setText("");
                        rightlabel.setText("");
                        leftlabel.setIcon(fit(img, 470, 450));
                        rightlabel.setIcon(fit(result, 470, 450));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "could not open the image");
                    }
                }
            });

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

            JPanel buttons = new JPanel();
            buttons.add(uploadbutton);
            buttons.add(savebutton);

            frame.add(images, BorderLayout.CENTER);
            frame.add(buttons, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }
}