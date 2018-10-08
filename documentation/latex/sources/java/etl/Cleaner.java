package ETL;

import org.apache.poi.ss.usermodel.*;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public final class Cleaner {

    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT= new SimpleDateFormat("hh:mm:ss");


    private PrintWriter csvFile;

    private Workbook workbook;

    private Sheet sheet;

    private FormulaEvaluator evaluator;

    public void setCsvFile(PrintWriter csvFile) {
        this.csvFile = csvFile;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public void clean(){
        evaluator= workbook.getCreationHelper().createFormulaEvaluator();
        Row lastRow=null, row;
        Iterator<Row> rowIterator=workbook.getSheetAt(0).rowIterator();
        metaDataOnCSV(rowIterator.next(),rowIterator.next());
        for (Sheet actualSheet:workbook) {
            //Remove bad rows from each sheet
            sheet=actualSheet;
            removeBadRows();

            //Skip header
            rowIterator=sheet.rowIterator();
            rowIterator.next();
            rowIterator.next();

            //Process Row data
            while (rowIterator.hasNext()) {
                row=rowIterator.next();
                processRow(row,lastRow);
            }
        }
        csvFile.close();
    }

    private void metaDataOnCSV(Row row1, Row row2){
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < row1.getLastCellNum(); i++) {
            String temp=DATA_FORMATTER.formatCellValue(row1.getCell(i)).toLowerCase();
            temp=temp.replace(' ','_').replace('/','_').replace(".","").replace('(','_').replace(')','_');
            builder.append(temp);
            if (i!=0 && i!=6 && i!=row1.getLastCellNum()-1)
                builder.append("_");
            if (i==23)
                builder.append("kw");
            else
                builder.append(DATA_FORMATTER.formatCellValue(row2.getCell(i)).toLowerCase().replace("[","").replace("]","").replace('/','_').replace("%","_perc").replace("rh","_rh"));
            if (i< row1.getLastCellNum()-1)
                builder.append(';');
        }
        csvFile.println(builder.toString().replace("__","_"));
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
               value= Double.toString(evaluator.evaluate(currentCell).getNumberValue());
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

            //CHECK NEGATIVE VALUE
            if  (i!=11 && i!=16  && !value.isEmpty() && value.toCharArray()[0]=='-')
                return;
          //  if (i!=15 && i!=16)
                value=value.replace(',','.');
            builder.append(value);
            if (i< row.getLastCellNum()-1)
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
