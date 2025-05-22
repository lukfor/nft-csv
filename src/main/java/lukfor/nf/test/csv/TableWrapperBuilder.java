package lukfor.nf.test.csv;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class TableWrapperBuilder {

    public static TableWrapper loadCsv(Path path, Map<String, Object> options) throws Exception {
        InputStream stream = new FileInputStream(path.toFile());
        return loadCsv(stream, options);
    }

    public static TableWrapper loadCsv(InputStream stream, Map<String, Object> options) throws Exception {

        // Create an InputStream for the gzipped file
        if (options.containsKey("decompress")) {
            if((boolean) options.get("decompress") && !(stream instanceof  GZIPInputStream)) {
                stream = new GZIPInputStream(stream);
            }
        }

        CsvReadOptions.Builder optionsBuilder = CsvReadOptions.builder(stream);
        if (options.containsKey("separator")) {
            optionsBuilder.separator((char) options.get("separator"));
        }

        if (options.containsKey("sep")) {
            optionsBuilder.separator(options.get("sep").toString().charAt(0));
        }

        if (options.containsKey("header")) {
            optionsBuilder.header((boolean) options.get("header"));
        }

        if (options.containsKey("quote")) {
            optionsBuilder.quoteChar(options.get("quote").toString().charAt(0));
        }

        if (options.containsKey("commentPrefix")) {
            Object prefixObj = options.get("commentPrefix");
            Character prefix = (prefixObj instanceof Character) ? (Character)prefixObj : prefixObj.toString().charAt(0);
            optionsBuilder.commentPrefix(prefix);
        }

        //TODO: add skip n lines and limit?

        CsvReadOptions csvReadOptions = optionsBuilder.build();
        Table table = Table.read().usingOptions(csvReadOptions);
        return new TableWrapper(table);
    }

}