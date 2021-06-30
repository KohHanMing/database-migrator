import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.Dimension;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReader;


public class DimensionParser {
    private List<List<String>> dimensionData;

    public DimensionParser (List<List<String>> dimensionData) {
        this.dimensionData = dimensionData;
    }

    public static DimensionParser createDimensionParser(String path) {
        List<List<String>> dimensionData = new ArrayList<>();
                
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            List<String> dimensionRow = new ArrayList<>();
            String name = getNameFromFile(listOfFiles[i]);
            Dimension dimension = getDimensionsFromFile(listOfFiles[i]);
            String type = setDimensionType(dimension);
            dimensionRow.add(name);
            dimensionRow.add(type);
            dimensionData.add(dimensionRow);
            

        }
        return new DimensionParser(dimensionData);
    }

    public static String getNameFromFile(File file) {
        String raw = file.getName();
        String processed = raw.substring(0, raw.lastIndexOf('.'));
        return processed;
    }

    public static Dimension getDimensionsFromFile(File file) {
        try(ImageInputStream in = ImageIO.createImageInputStream(file)) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            Dimension temp = new Dimension();
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    temp = new Dimension(reader.getWidth(0), reader.getHeight(0));
                } finally {
                    reader.dispose();
                }
            }
            return temp;
        } catch (IllegalArgumentException e) {
            System.out.println("File not found! Check the input name vs actual file name");
            System.out.println("File name is: " + file.getName());
            return null;
        } catch (IOException e) {
            System.out.println("Read error occured!");
            return null;
        }
    }

    public static String setDimensionType(Dimension dimension) {
        String res = "";
        if (dimension.getWidth() > dimension.getHeight()) { //landscape
            res = "landscape";
        } else if (dimension.getWidth() < dimension.getHeight()) { //portrait
            res = "portrait";
        } else { // square
            res = "square";
        }
        return res;
    }

    public String getName(int i) {
        return dimensionData.get(i).get(0);
    }

    public String getDimensionType(int i) {
        return dimensionData.get(i).get(1);
    }

    public int getSize() {
        return dimensionData.size();
    }

    public void printAll() {
        for (int i = 0; i < dimensionData.size(); i++) {
            System.out.println("Name: " + getName(i) + ", Type: " + getDimensionType(i));
        }
    }
}
