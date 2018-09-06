import org.apache.poi.ss.usermodel.*;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;

public class Multiplier {
    private Workbook workbook;
    private Sheet sheet;

    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public Workbook duplicate(){
        sheet = workbook.getSheetAt(0);
        int dim = sheet.getLastRowNum(), i, j, rowDim;
        Row currRow, newRow;
        String value;
        Cell cell;
        Date fictitiousDate = createRandomDate();

        //Aggiunge una colonna
        for (i = 0; i < dim; i++) {
            currRow = sheet.getRow(i);
            cell = currRow.createCell(currRow.getLastCellNum());
            cell.setCellValue(fictitiousDate);
        }

        Sheet newSheet = workbook.createSheet("New Sheet");

        dim = 0;
        for(Row row : sheet){
            newRow = newSheet.createRow(dim);

            //Copia l'intera riga sulla nuova
            for (Cell c : row) {
                value = DATA_FORMATTER.formatCellValue(c);
                newRow.createCell(i).setCellValue(value);
            }

            dim++;

        }
        return workbook;
    }

    private Date createRandomDate(){
        // Get a new random instance, seeded from the clock
        Random rnd = new Random();

        // Get an Epoch value roughly between 2000 and 2018
        // -946684800000L = January 1, 2000
        // Add up to 18 years to it (using modulus on the next long)
        long ms = 946684800000L + (Math.abs(rnd.nextLong()) % (18L * 365 * 24 * 60 * 60 * 1000));

        return new Date(ms);
    }
}
