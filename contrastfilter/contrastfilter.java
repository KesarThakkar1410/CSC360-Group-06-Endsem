import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class contrastfilter {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("use: java contrastfilter input.jpg output.png");
            return;
        }

        BufferedImage img = ImageIO.read(new File(args[0]));
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
                int rgb = (n << 16) | (n << 8) | n;
                out.setRGB(x, y, rgb);
            }
        }

        ImageIO.write(out, "png", new File(args[1]));
        System.out.println("done. darkest was " + min + ", brightest was " + max);
    }
}