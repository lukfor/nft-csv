package lukfor.nf.test.csv;

import tech.tablesaw.api.Table;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Methods {

	public static TableWrapper csv(Path path, Map<String, Object> options) throws Exception {
		InputStream stream = new FileInputStream(path.toFile());
		return TableWrapperBuilder.loadCsv(stream, options);
	}

	public static TableWrapper csv(Path path) throws Exception {
		return csv(path, new HashMap<>());
	}

	public static TableWrapper csv(String filename, Map<String, Object> options) throws Exception {
		InputStream stream = null;
		if (filename.startsWith("https://")) {
			stream = new URL(filename).openStream();
		} else {
			stream = new FileInputStream(filename);
		}
		return TableWrapperBuilder.loadCsv(stream, options);
	}

	public static TableWrapper csv(String filename) throws Exception {
		return csv(filename, new HashMap<>());
	}

	public static TableWrapper csv(Table table) throws Exception {
		return new TableWrapper(table);
	}

	public static void assertArrayEquals(List<Object> table1, List<Object> table2) throws Exception {
		double precision = 0.00001;
		TableComparator.equalsArrays(table1, table2, precision);
	}

	public static void assertArrayEquals(List<Object> table1, List<Object> table2, double precision) throws Exception {
		TableComparator.equalsArrays(table1, table2, precision);
	}

	public static void assertTableEquals(TableWrapper table1, TableWrapper table2) throws Exception {
		double precision = 0.00001;
		TableComparator.equalsTables(table1.getTable(), table2.getTable(), precision);
	}

	public static void assertTableEquals(TableWrapper table1, TableWrapper table2, double precision) throws Exception {
		TableComparator.equalsTables(table1.getTable(), table2.getTable(), precision);
	}

}
