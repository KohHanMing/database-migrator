import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class OptionModel {
    private LinkedHashMap<String, List<List<String>>> optionMap;
    private DimensionParser dimensionData;
    private List<String> productOptionTypes;
    private static final String WEIGHT = String.valueOf(0);
    private static final String QUANTITY = String.valueOf(100);

    public OptionModel(LinkedHashMap<String, List<List<String>>> optionMap, DimensionParser dimensionData) {
        this.optionMap = optionMap;
        this.dimensionData = dimensionData;
        List<String> tempList = new ArrayList<>();
    }

    public static OptionModel createOptionModel(String optionString) {
        List<List<String>> options = WriterUtils.readFromCsv(optionString);
        LinkedHashMap<String, List<List<String>>> tempMap = new LinkedHashMap<>();
        for (List<String> row : options) {
            String key = row.get(0);
            if (!tempMap.containsKey(key)) {
                List<List<String>> value = new ArrayList<>();
                tempMap.put(key, value);
            }
            List<List<String>> optionValues = tempMap.get(key);
            List<String> newRow = row.subList(1, row.size());
            optionValues.add(newRow);
        }
        return new OptionModel(tempMap, DimensionParser.createDimensionParser("./converted"));
    }

    // public List<List<String>> addOptions(List<List<String>> sourceData) {
    //     for (int i = 1; i < sourceData.size(); i++) {
    //         List<String> sourceRow = sourceData.get(i);
    //         String optionType = sourceData.get(i).get(sourceRow.size() - 1);
    //         if (optionType.equals("dimension")) {
    //             return addOptionsFromDimensions(sourceData)
    //         }
    //     }
        
    // }


    public List<List<String>> addOptionsFromDimensions(List<List<String>> sourceData) {
        List<List<String>> newData = new ArrayList<>();
        for (int i = 1 ; i < sourceData.size(); i++) { //starts from 1 to ignore headers
            List<String> sourceRow = sourceData.get(i);
            String dimensionType = dimensionData.getDimensionType(i - 1);
            List<List<String>> options = optionMap.get(dimensionType);
            if (dimensionType.equals("portrait")) {
                String newDesc = sourceRow.get(2).replace("Landscape", "Portrait");
                sourceRow.set(2, newDesc);
            }
            List<List<String>> optionRows = generateOptionRows(sourceRow, options);
            newData.addAll(optionRows);

        }
        return newData;
    }

    public List<List<String>> addOptionsFromType(List<List<String>> sourceData, List<String> productTypes) {
        List<List<String>> newData = new ArrayList<>();
        for (int i = 1 ; i < sourceData.size(); i++) { //starts from 1 to ignore headers
            List<String> sourceRow = sourceData.get(i);
            System.out.println("Source row is: " + sourceRow.toString());
            String optionType = productTypes.get(i);
            System.out.println("Option type is: " + optionType);
            List<List<String>> options = optionMap.get(optionType);
            // if (dimensionType.equals("portrait")) {
            //     String newDesc = sourceRow.get(2).replace("Landscape", "Portrait");
            //     sourceRow.set(2, newDesc);
            // }
            List<List<String>> optionRows = generateOptionRows(sourceRow, options);
            newData.addAll(optionRows);

        }
        return newData;
    }

    public List<List<String>> generateOptionRows(List<String> sourceRow, List<List<String>> options) {
        List<List<String>> optionRows = new ArrayList<>();
        String handler = sourceRow.get(0);
        int optionId = 1;
        String sku = handler + "-" + String.format("%02d", optionId);
        for (List<String> option : options) {
            if (optionId == 1) {
                setOptionData(sourceRow, sku, option);
                optionRows.add(sourceRow);
            } else {
                List<String> newRow = generateBlankRow();
                setOptionData(newRow, sku, option);
                optionRows.add(newRow);
            }
            optionId++;
            sku = handler + "-" + String.format("%02d", optionId);
        }
        return optionRows;
    }

    public List<String> generateBlankRow() {
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < 34; i++) {
            temp.add("");
        }
        return temp;
    }

    public void setOptionData(List<String> row, String sku, List<String> option) {
        row.set(17, sku);
        row.set(18, option.get(0));
        row.set(19, option.get(1));
        row.set(29, option.get(2));
        row.set(30, option.get(3)); 
        row.set(31, WEIGHT);
        row.set(32, QUANTITY);
    }

    public void printDimensionData() {
        for (int i = 0; i < dimensionData.getSize(); i++) {
            System.out.println(dimensionData.getDimensionType(i));
        }
    }

    @Override 
    public String toString() {
        // Finding the Set of keys from
        // the HashMap
        Set<String> keySet = optionMap.keySet();
  
        // Creating an ArrayList of keys
        // by passing the keySet
        ArrayList<String> listOfKeys = new ArrayList<String>(keySet);

        // Getting Collection of values from HashMap
        Collection<List<List<String>>> values = optionMap.values();
  
        // Creating an ArrayList of values
        ArrayList<List<List<String>>> listOfValues = new ArrayList<>(values);

        String res = "";
        for (int i = 0; i < listOfKeys.size(); i++) {
            String key = listOfKeys.get(i);
            List<List<String>> value = listOfValues.get(i);
            res += "Key: " + key + "\n"
                + "Values: " + "\n";
            for (List<String> row : value) {
                res += row.toString() + "\n";
            }
        }
        
        return res; 
    }
}
