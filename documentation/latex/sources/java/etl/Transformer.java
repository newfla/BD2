package ETL;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class Transformer {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT= new SimpleDateFormat("hh:mm");

    private BufferedReader reader;

    private CSVParser csvParser;

    private String exportedCsv;

    private StringBuilder builder;

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public void transform(int j){
        builder=new StringBuilder();

        try {
            csvParser= new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withHeader().withIgnoreHeaderCase());
            Map<String,Integer> mapHeader=csvParser.getHeaderMap();
            int size =csvParser.getHeaderMap().keySet().size();
            for (int i = 0; i < size-1; i++) {
                for (String headerString: mapHeader.keySet()) {
                    if (mapHeader.get(headerString)==i) {
                        builder.append(headerString);
                        if (i<size-2)
                            builder.append(';');
                    }
                }
            }
            //Remove latitude and longitude from header
            String header=builder.toString();
            header=header.replace("latitude_n_s;","");
            header=header.replace("longitude_w_e;","");
            header=header.replace("absolute","time");

            //Add header to final CSV
            builder=new StringBuilder();
            builder.append("test_id;");
            builder.append("date;");
            builder.append(header);
            builder.append('\n');
            List<CSVRecord>records= csvParser.getRecords();
            String time=cleanTime(records.get(0).get(0));
            size--;
            for (CSVRecord record: records){
                //setTestID
                builder.append(j);
                builder.append(';');
                //setDateTest
                builder.append(record.get(size));
                builder.append(';');
                //setTimeTest
                builder.append(time);
                builder.append(';');

                //set others
                for (int i = 1; i < size; i++) {

                    //Remove latitude and longitude from every row
                    if (i==14 || i==15)
                        continue;

                    builder.append(record.get(i));
                    if (i<size-1)
                        builder.append(';');
                }
                builder.append('\n');
            }
            csvParser.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String cleanTime(String value) {
        Date date= null;
        try {
            date = SIMPLE_DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return SIMPLE_DATE_FORMAT.format(date)+":00";
    }

    public void setCsvFile(PrintWriter csvFile){
        String temp=builder.toString();
        csvFile.print(temp.substring(0,temp.length()-1));
        csvFile.close();
    }
}
