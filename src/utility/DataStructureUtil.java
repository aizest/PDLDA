package utility;

import java.util.ArrayList;
import java.util.List;

public class DataStructureUtil {
	public static List<String> cloneStringList(List<String> list) {
		if (list == null)
			return list;

		List<String> copy = new ArrayList<String>();
		for (String str : list) {
			copy.add(str);
		}

		return copy;
	}

}
