import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.opencsv.exceptions.CsvValidationException;

public class ImageFinder {
    private LinkedHashMap<String, List<String>> data;
    private List<String> names;
    private static final String SOURCE_ROOT = ".\\source\\";
    private static final String TARGET_ROOT = ".\\target\\";
    private static final String PRODUCT_PATH = SOURCE_ROOT + "product.csv";
    private static final String PRODUCT_IMG_PATH = SOURCE_ROOT + "product_image.csv";
    private static final String COMPANY_BASE = "TG";
    private static final String PRODUCT_CLASS = "ANDYYONG";
    private static boolean HAS_VALID_SKU = true;
    private static boolean IS_FROM_CACHE = false;
    private static boolean HAS_ADDITIONAL = false;
    private static boolean IS_RENAMED = false;
    private static boolean HAS_OPTIONS = false;
    private int startId;
    private int imgId;



    public ImageFinder(LinkedHashMap<String, List<String>> data, List<String> names) {
        this.data = data;
        this.names = names;
        this.startId = 1;
        this.imgId = 1;
    }

    public static void main(String[] args) {
        ImageFinder imageFinder = ImageFinder.createFinder(PRODUCT_PATH, PRODUCT_IMG_PATH);
        System.out.println(imageFinder.names.toString());
        imageFinder.transferImages();
        // List<List<String>> rows = new ArrayList<>(imageFinder.data.values());
        // imageFinder.moveMultipleImages(rows.get(2));
        
    }

    public static ImageFinder createFinder(String productFilePath, String productImgFilePath) {
        LinkedHashMap<String, List<String>> productData = new LinkedHashMap<>();
        List<String> productNames = new ArrayList<>();
        try {
            ImageFinder.getProductInfo(productFilePath, productData, productNames);
            if (HAS_ADDITIONAL) ImageFinder.getAdditionalImagePaths(productImgFilePath, productData);
            
        } catch (FileNotFoundException e) {
            System.out.println("File not found! Check filepath given");
        } catch (IOException e) {
            System.out.println("IO exception encountered. For opened files");
            e.printStackTrace();
        } catch (CsvValidationException e) {
            System.out.println("CSV Validation error. Check cell formatting");
        }
        return new ImageFinder(productData, productNames);
    }

    public static void getProductInfo(String filePath, LinkedHashMap<String, List<String>> productData, List<String> productNames)
    throws FileNotFoundException, IOException, CsvValidationException {
        CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
        CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(parser).build();
        String[] data;
        
        while ((data = csvReader.readNext()) != null) {
            System.out.println(data.toString());
            List<String> productRow = new ArrayList<>();
            String productId = data[0];
            String productImgPath = data[1];   
            if (HAS_VALID_SKU) {
                String sku = data[2];
                productNames.add(sku);
            }
            //if (IS_FROM_CACHE) productImgPath = changeToCacheFormat(productImgPath);         
            productRow.add(productImgPath);
            
            productData.put(productId, productRow);
            System.out.println("Product Row: " + productRow.toString());
        }
        csvReader.close();
    }

    public static String changeToCacheFormat(String string) {
        String[] stringSplit = string.split("\\.");
        String res = "";
        for (int i = 0; i < stringSplit.length - 1; i++) {
            res += stringSplit[i];
            if (i < stringSplit.length - 2) res += ".";
        }
        return res + "-1400x1400." + stringSplit[stringSplit.length - 1];
    }

    public static String changeToCacheFormatAlt(String string) {
        String[] stringSplit = string.split("\\.");
        String res = "";
        for (int i = 0; i < stringSplit.length - 1; i++) {
            res += stringSplit[i];
            if (i < stringSplit.length - 2) res += ".";
        }
        return res + "-1400." + stringSplit[stringSplit.length - 1];
    }

    public static void getAdditionalImagePaths(String filePath, LinkedHashMap<String, List<String>> productData)
    throws FileNotFoundException, IOException, CsvValidationException {
        CSVParser parser = new CSVParserBuilder().withSeparator(',').build();
        CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(parser).build();
        String[] data;
        
        while ((data = csvReader.readNext()) != null) {
            //System.out.println(data.toString());
            String productId = data[0];
            String productImgPath = data[1]; 
            
            productData.get(productId).add(productImgPath);
        }
        csvReader.close();
    }

    public void transferImages() {
            List<List<String>> rows = new ArrayList<>(data.values());
            for (int i = 0; i < rows.size(); i++) {
                List<String> list = rows.get(i);
                System.out.println("Row size: " + list.size());
                System.out.println("Row: " + list.toString());
                if (HAS_VALID_SKU) {
                    moveImagesWithSku(list, names.get(i));
                } else if (IS_RENAMED) {
                    moveImagesAndRename(list);
                    startId++;
                } else {
                    moveImagesOnly(list);
                }
            }
            System.out.println("Done!");
    }
    public void moveImagesWithSku(List<String> list, String sku) {
        System.out.println("Move with Sku called");
        if (HAS_OPTIONS) {
            for (int i = 0; i < list.size(); i++) {
                String listString = list.get(i);
                String sourceString = SOURCE_ROOT + listString;
                String newImgName = sku
                    + "-01-"
                    + String.valueOf(imgId);
                String imgType = getImageType(sourceString);
                String targetString = TARGET_ROOT + newImgName + "." + imgType;
                // System.out.println(sourceString);
                // System.out.println(targetString);
                copyImage(sourceString, targetString);
                imgId++;
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                String listString = list.get(i);
                String sourceString = SOURCE_ROOT + listString;
                String newImgName = sku
                    + "-"
                    + String.valueOf(imgId);
                String imgType = getImageType(sourceString);
                String targetString = TARGET_ROOT + newImgName + "." + imgType;
                // System.out.println(sourceString);
                // System.out.println(targetString);
                copyImage(sourceString, targetString);
                imgId++;
            }
        }
        resetImgId();

    }

    public void moveImagesAndRename(List<String> list) {
        System.out.println("Move and Rename called");
        if (HAS_OPTIONS) {
            for (String listString : list) {
                String sourceString = SOURCE_ROOT + listString;
                String newImgName = COMPANY_BASE
                    + "-" 
                    + PRODUCT_CLASS 
                    + "-" 
                    + String.format("%03d", startId) 
                    + "-01-"
                    + String.valueOf(imgId);
                String imgType = getImageType(sourceString);
                String targetString = TARGET_ROOT + newImgName + "." + imgType;
                // System.out.println(sourceString);
                // System.out.println(targetString);
                copyImage(sourceString, targetString);
                imgId++;
            }
        } else {
            for (String listString : list) {
                String sourceString = SOURCE_ROOT + listString;
                String newImgName = COMPANY_BASE
                    + "-" 
                    + PRODUCT_CLASS 
                    + "-" 
                    + String.format("%03d", startId) 
                    + "-"
                    + String.valueOf(imgId);
                String imgType = getImageType(sourceString);
                String targetString = TARGET_ROOT + newImgName + "." + imgType;
                // System.out.println(sourceString);
                // System.out.println(targetString);
                copyImage(sourceString, targetString);
                imgId++;
            }
        }
        resetImgId();

    }

    public void moveImagesOnly(List<String> list) {
        System.out.println("Move Only called");
        for (String listString : list) {
            String sourceString = SOURCE_ROOT + listString;
            String[] sourceStringSplit = sourceString.split("/");
            String img = sourceStringSplit[sourceStringSplit.length - 1];
            String targetString = TARGET_ROOT + img;
            // System.out.println(sourceString);
            // System.out.println(targetString);
            copyImage(sourceString, targetString);
        }
    }



    public String getImageType(String sourcePath) {
        String[] sourcePathSplit = sourcePath.split("/");
        String[] imageNameSplit = sourcePathSplit[sourcePathSplit.length - 1].split("\\.");
        return imageNameSplit[imageNameSplit.length - 1];
    }

    public void copyImage(String source, String target) {
        try {
            System.out.println("Source: " + source);
            System.out.println("Target: " + target);
            Path sourcePath = Paths.get(source);
            if (!Files.exists(sourcePath)) {
                sourcePath = Paths.get(changeToCacheFormat(source));  
            }

            if (!Files.exists(sourcePath)) {
                sourcePath = Paths.get(changeToCacheFormatAlt(source));  
            }

            Files.copy(sourcePath, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied");
        } catch (IOException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    public void resetImgId() {
        imgId = 1;
    }

}