package utility;

import java.util.Comparator;

import datastructures.TermMetric;

public class TermMetricComparator<T> implements Comparator<T> {

	@Override
	public int compare(T c1, T c2) {

		if (!(c1 instanceof TermMetric) || !(c2 instanceof TermMetric)) {
			System.err.println("Either of the two objects is NOT TermMetric!");
			return 0;
		}

		double metric1 = ((TermMetric) c1).getMetric();
		double metric2 = ((TermMetric) c2).getMetric();

		// Avoid testing the equality of two doubles
		if (metric1 > metric2)
			return 1;
		else if (metric1 < metric2)
			return -1;

		long lMetric1 = Double.doubleToLongBits(metric1);
		long lMetric2 = Double.doubleToLongBits(metric2);

		return (lMetric1 == lMetric2 ? 0 : (lMetric1 < lMetric2 ? -1 : 1));
	}

	public static void main(String[] args) {
		TermMetricComparator<TermMetric> comparator = new TermMetricComparator<TermMetric>();
		int result = comparator.compare(new TermMetric("a", 1.0),
				new TermMetric("b", 1.0));
		System.err.println(result);

		result = comparator.compare(new TermMetric("a", 0.50), new TermMetric(
				"b", 0.49));
		System.err.println(result);

		result = comparator.compare(new TermMetric("a", 0.49), new TermMetric(
				"b", 0.50));
		System.err.println(result);
	}
}
