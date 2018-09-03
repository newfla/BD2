import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public final class Cleaner {

    private static final String LOCATION_EXCEL="./excel/test Napoli 20-10-2017.xlsx";

    private static final String LOCATION_CSV="./excel/test Napoli 20-10-2017.csv";

    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT= new SimpleDateFormat("hh:mm:ss");


    private File excelFile;

    private PrintWriter csvFile;

    private Workbook workbook;

    private Sheet sheet;

    FormulaEvaluator evaluator;

    public void loadFile(){
        excelFile=new File(LOCATION_EXCEL);
        try {
            workbook= new XSSFWorkbook(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        sheet=workbook.getSheetAt(0);
        evaluator= workbook.getCreationHelper().createFormulaEvaluator();
    }

    private void createCSV(){
        try {
            csvFile=new PrintWriter(LOCATION_CSV, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void clean(){
        Row lastRow=null, row=null;
        removeBadRows();
        Iterator<Row> rowIterator=sheet.rowIterator();
        createCSV();
        metaDataOnCSV(rowIterator.next(),rowIterator.next());
        while (rowIterator.hasNext()) {
            row=rowIterator.next();
            processRow(row,lastRow);
            lastRow=row;
        }
            csvFile.close();
    }

    private void metaDataOnCSV(Row row1, Row row2){
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < row1.getLastCellNum(); i++) {
            if (i==17)
                continue;
            builder.append(DATA_FORMATTER.formatCellValue(row1.getCell(i)));
            builder.append(';');
        }
        csvFile.println(builder.toString());

        builder=new StringBuilder();
        for (int i = 0; i < row2.getLastCellNum(); i++) {
            if (i==17)
                continue;
            builder.append(DATA_FORMATTER.formatCellValue(row2.getCell(i)));
            builder.append(';');
        }
        csvFile.println(builder.toString());
    }

    private void removeBadRows(){
        sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
        sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
    }

    private void processRow(Row row, Row lastRow){
        StringBuilder builder=new StringBuilder();
        String value;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell currentCell=row.getCell(i);
            if (currentCell!=null && currentCell.getCellTypeEnum() == CellType.FORMULA)
                value=String.valueOf(currentCell.getNumericCellValue());
            else
                value=DATA_FORMATTER.formatCellValue(currentCell);


            //CHECK ABSOLUTE
            if (i==0 && value.isEmpty() && lastRow!=null){
                value=DATA_FORMATTER.formatCellValue(lastRow.getCell(0));
                if (value.isEmpty())
                    return;
                try {
                    value=addSecond(value);
                   row.createCell(0,CellType.STRING).setCellValue(value);

                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }
            }

            //CHECK DISTANCE
            if (i==31 && value.isEmpty() && lastRow!=null){
                value=DATA_FORMATTER.formatCellValue(lastRow.getCell(31));
                if (value.isEmpty())
                    break;

                double dist=Double.valueOf(value);

                value=DATA_FORMATTER.formatCellValue(row.getCell(17));

                if (value.isEmpty())
                    break;
                double vel=Double.valueOf(value);
                vel/=3.6;

                value=String.valueOf(dist+vel);
                currentCell.setCellValue(value);
            }

            //VELOCITY=SPEED
            if (i==17)
                continue;

            //CHECK NEGATIVE VALUE
            if  (i!=11 && i!=16 && Arrays.binarySearch(value.toCharArray(),'-')>=0)
                return;
            builder.append(value);
            builder.append(';');
        }
        csvFile.println(builder.toString());
    }

    private String addSecond(String value) throws ParseException{
        Date date= SIMPLE_DATE_FORMAT.parse(value);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND,1);
        return SIMPLE_DATE_FORMAT.format(cal.getTime());
    }
}
