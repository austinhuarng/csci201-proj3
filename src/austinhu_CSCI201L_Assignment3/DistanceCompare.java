package austinhu_CSCI201L_Assignment3;

import java.util.Comparator;

public class DistanceCompare implements Comparator<Restaurant> {

	public int compare(Restaurant o1, Restaurant o2) {
		return Double.compare(o1.getDistance(), o2.getDistance());
	}

}
