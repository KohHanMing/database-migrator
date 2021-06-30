import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class WriterUtils {
    private static final char CSV_DELIMITER = ',';
    public static List<List<String>> readFromCsv(String filePath) {
        List<List<String>> productData = new ArrayList<>();
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(CSV_DELIMITER).build();
            CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(parser).build();
            String[] data;
            
            while ((data = csvReader.readNext()) != null) {
                //System.out.println(data.toString());
                List<String> productRow = new ArrayList<>();
                // String productName = data[0].trim();
                // String productCategory = data[1];
                // String productDesc = "&lt;p&gt;" + data[2] + "&lt;/p&gt;";

                // productRow.add(productName);
                // productRow.add(productCategory);
                // productRow.add(productDesc);
                for (String item : data) {
                    productRow.add(item);
                }
                String description = productRow.get(2);
                String newDesc = description.replace("\r\n", "").replace("\n", "");
                productRow.set(2, newDesc);

                productData.add(productRow);

                //System.out.println("Product Row: " + productRow.toString());
            }
            csvReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found! Check filepath given");
        } catch (IOException e) {
            System.out.println("IO exception encountered. Check for opened files");
            e.printStackTrace();
        } catch (CsvValidationException e) {
            System.out.println("CSV Validation error. Check cell formatting");
        }
        return productData;
    }

    public static void writeToCsv(List<List<String>> dataArray, String savePath) {
        try (
            Writer writer = Files.newBufferedWriter(Paths.get(savePath));

            CSVWriter csvWriter = new CSVWriter(writer,
                    CSV_DELIMITER,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
        ) {
            for (List<String> data : dataArray) {
                csvWriter.writeNext(data.toArray(new String[0]));
            }
        } catch (IOException e) {
            System.out.println("IO exception encountered. Check for opened files");
            e.printStackTrace();
        }
    }
}
