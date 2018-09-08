import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public final class Multiplier {

    private XSSFWorkbook workbook;

    public void setWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }


    public void duplicate(String path){
        Cell cell;
        String fictitiousDate;

        final Sheet sheet = workbook.getSheetAt(0);
        final int rowDim = sheet.getLastRowNum();
        Row currRow = sheet.getRow(0);
        final int colDim = currRow.getLastCellNum();

        currRow.createCell(colDim, CellType.STRING).setCellValue("Date");

        fictitiousDate = createRandomDate();
        for (int i = 2; i < rowDim; i++) {
            currRow = sheet.getRow(i);
            cell = currRow.createCell(colDim, CellType.STRING);
            cell.setCellValue(fictitiousDate);
        }

        workbook.setSheetName(0, fictitiousDate);
        try {
            workbook.write(new FileOutputStream(path+"/"+fictitiousDate+".xlsx"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createRandomDate(){
        // Get a new random instance, seeded from the clock
        Random rnd = new Random();

        // Get an Epoch value roughly between 2000 and 2018
        // -946684800000L = January 1, 2000
        // Add up to 18 years to it (using modulus on the next long)
        long ms = 946684800000L + (Math.abs(rnd.nextLong()) % (18L * 365 * 24 * 60 * 60 * 1000));
        //LocalDate date = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate();
        return new SimpleDateFormat("MM-dd-yyyy").format(new Date(ms));
    }


}
