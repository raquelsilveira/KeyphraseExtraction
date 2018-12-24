package test;

import java.util.Comparator;
import java.util.Map;

public class MyComparator implements Comparator {

	public int compare(Object obj1, Object obj2) {

		int result = 0;
		Map.Entry e1 = (Map.Entry) obj1;

		Map.Entry e2 = (Map.Entry) obj2;// Sort based on values.

		Double value1 = (Double) e1.getValue();
		Double value2 = (Double) e2.getValue();

		if (value1.compareTo(value2) == 0) {

			String word1 = (String) e1.getKey();
			String word2 = (String) e2.getKey();

			// Sort String in an alphabetical order
			result = word1.compareToIgnoreCase(word2);

		} else {
			// Sort values in a descending order
			result = value1.compareTo(value2);
		}

		return result;
	}
}
