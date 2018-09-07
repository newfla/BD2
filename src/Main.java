import ETL.Cleaner;
import ETL.Transformer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main {

    private static final String LOCATION_EXCEL="./excel/test Napoli 20-10-2017.xlsx";

    private static final String LOCATION_CSV="./excel/test Napoli 20-10-2017.csv";

    private static final int NUM_TEST=400;

    public static void main(String[] args) throws IOException {
        Multiplier multiplier = new Multiplier();
        multiplier.setWorkbook(loadEXCEL());
        Workbook wb = multiplier.duplicate();

        wb.close();
        /*FileOutputStream out = new FileOutputStream(LOCATION_EXCEL);
        wb.write(out);
        out.close();*/

            /*Cleaner cleaner =new Cleaner();
            cleaner.setWorkbook(loadEXCEL());
            cleaner.setCsvFile(createCSV());
            cleaner.clean();
            Transformer transformer=new Transformer();
            transformer.setReader(loadCSV());*/

    }


    private static PrintWriter createCSV(){
        try {
           return new PrintWriter(LOCATION_CSV, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Workbook loadEXCEL(){
        File excelFile=new File(LOCATION_EXCEL);
        try {
           return new XSSFWorkbook(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Reader loadCSV(){

        try {
            return Files.newBufferedReader(Paths.get(LOCATION_CSV));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
