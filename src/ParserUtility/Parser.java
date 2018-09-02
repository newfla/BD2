package ParserUtility;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Parser {

    private static final String FILE_LOCATION="./excel/test Napoli 20-10-2017.xlsx";

    private static final DataFormatter DATA_FORMATTER = new DataFormatter();


    private File excelFile;

    private Workbook workbook;

    private Sheet sheet;

    private final List<RowEntity>rows=new ArrayList<>();

    public List<RowEntity> getRows() {
        return rows;
    }

    public void loadFile(){
        excelFile=new File(FILE_LOCATION);
        try {
            workbook= WorkbookFactory.create(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        sheet=workbook.getSheetAt(0);
    }

    public void parse(){
        Iterator<Row> rowIterator=sheet.rowIterator();
        Iterator<Cell>cellIterator;
        Cell cell;

        rowIterator.next();
        rowIterator.next();

        while (rowIterator.hasNext()){
            cellIterator=rowIterator.next().cellIterator();
            while (cellIterator.hasNext()){
                cell=cellIterator.next();
                System.out.println(DATA_FORMATTER.formatCellValue(cell));
            }
        }
    }
}
