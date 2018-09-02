import ParserUtility.Parser;

public final class Main {

    public static void main(String[] args) {
        Parser parser=new Parser();
        parser.loadFile();
        parser.parse();
        parser.getRows();
    }
}
