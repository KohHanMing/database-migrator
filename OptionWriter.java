import java.util.ArrayList;
import java.util.List;

public class OptionWriter {
    private static final boolean HAS_OPTIONS = true;

    // private List<List<String>> data;

    // public OptionWriter(List<List<String>> data) {
    //     this.data = data;
    // }
    public static void main(String[] args) {
        String sourceString = "./source/upload.csv";
        List<List<String>> rawData = WriterUtils.readFromCsv(sourceString); //rmb to start form index 1 cos row 0 is titles

        if (HAS_OPTIONS) {
            List<List<String>> sourceData = OptionWriter.createOptionWriter(rawData);
            List<String> productTypes = OptionWriter.createTypes(rawData);

            System.out.println(productTypes.toString());
            String optionString = "./source/options.csv";
            OptionModel optionModel = OptionModel.createOptionModel(optionString);
            optionModel.printDimensionData();
            System.out.println(optionModel.toString());
            List<List<String>> newData = optionModel.addOptionsFromType(sourceData, productTypes);
            //List<List<String>> newData = optionModel.addOptionsFromDimensions(sourceData);

            newData.add(0, sourceData.get(0)); // add headers back to new data
            for (List<String> row : newData) {
                System.out.println(row.toString());
            }
            
            WriterUtils.writeToCsv(newData, "./newUpload.csv");
            System.out.println("New Data Written");
        } else {
            WriterUtils.writeToCsv(rawData, "./newUpload.csv");
            System.out.println("Raw Data Written");
        }
    }

    public static List<List<String>> createOptionWriter(List<List<String>> data) { 
        List<List<String>> newData = new ArrayList<>();
        for (List<String> row : data) {
            newData.add(row.subList(0, row.size() - 1));
        }

        return newData;
    }

    public static List<String> createTypes(List<List<String>> data) {
    List<String> newData = new ArrayList<>();
    for (List<String> row : data) {
        newData.add(row.get(row.size() - 1));
    }

    return newData;
}
}
