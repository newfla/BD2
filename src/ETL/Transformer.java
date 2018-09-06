package ETL;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;

public final class Transformer {

    private Reader reader;

    private CSVParser csvParser;

    private String exportedCsv;

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public void transform(){


        try {
            csvParser= new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withHeader().withIgnoreHeaderCase());
            csvParser.getHeaderMap().keySet();

            for (CSVRecord record: csvParser){
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
