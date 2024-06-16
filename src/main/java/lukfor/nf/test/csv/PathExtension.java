package lukfor.nf.test.csv;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PathExtension {

	public static TableWrapper csv(Path self, LinkedHashMap options) throws Exception {
		Map<String, Object> defaults = new HashMap<String, Object>();
		defaults.put("header", true);
		defaults.put("sep", ',');
		defaults.putAll(options);
		return TableWrapperBuilder.loadCsv(self, defaults);
	}

	public static TableWrapper csv(Path self) throws Exception {
		return csv(self,  new LinkedHashMap<String, Object>());
	}

	public static TableWrapper getCsv(Path self) throws Exception {
		return csv(self);
	}

}
