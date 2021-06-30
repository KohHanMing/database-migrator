import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageConverter {

    public static void main(String[] args) {
        File folder = new File("./target");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("Files " + listOfFiles[i]);
                ImageConverter.convert(listOfFiles[i]);
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }

    public static void convert(File inputFile) {
        try {
            String img = inputFile.getName().split("\\.")[0];
            File outputFile = new File("./converted/" + img + ".jpg");
            BufferedImage originalImage = ImageIO.read(inputFile);
            BufferedImage trimmedImage = trimImage(originalImage);

            // jpg needs BufferedImage.TYPE_INT_RGB
            // png needs BufferedImage.TYPE_INT_ARGB

            // create a blank, RGB, same width and height
            BufferedImage newBufferedImage = new BufferedImage(
                    trimmedImage.getWidth(),
                    trimmedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            // draw a white background and puts the originalImage on it.
            newBufferedImage.createGraphics()
                    .drawImage(trimmedImage,
                            0,
                            0,
                            Color.WHITE,
                            null);

            // save an image
            ImageIO.write(newBufferedImage, "jpg", outputFile);
            System.out.println("Written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage trimImage(BufferedImage image) {
        WritableRaster raster = image.getAlphaRaster();
        if (raster == null) {
            return image; //if no raster, return original image
        } else {
            int width = raster.getWidth();
            int height = raster.getHeight();
            int left = 0;
            int top = 0;
            int right = width - 1;
            int bottom = height - 1;
            int minRight = width - 1;
            int minBottom = height - 1;
        
            top:
            for (;top < bottom; top++){
                for (int x = 0; x < width; x++){
                    if (raster.getSample(x, top, 0) != 0){
                        minRight = x;
                        minBottom = top;
                        break top;
                    }
                }
            }
        
            left:
            for (;left < minRight; left++){
                for (int y = height - 1; y > top; y--){
                    if (raster.getSample(left, y, 0) != 0){
                        minBottom = y;
                        break left;
                    }
                }
            }
        
            bottom:
            for (;bottom > minBottom; bottom--){
                for (int x = width - 1; x >= left; x--){
                    if (raster.getSample(x, bottom, 0) != 0){
                        minRight = x;
                        break bottom;
                    }
                }
            }
        
            right:
            for (;right > minRight; right--){
                for (int y = bottom; y >= top; y--){
                    if (raster.getSample(right, y, 0) != 0){
                        break right;
                    }
                }
            }
        
            return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
        }
    }
}
