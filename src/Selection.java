import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Selection {

	// Find the x-value of the ith element in linear time.
	// Goal is in this case always size/2 rounded down,
	// also known as the median, but the implementation
	// works for any i.
	public float select(List<Point2D> pList, int goal) {
		
		if(pList.size() == 1) {
			return pList.get(0).x;
		}
		
		// Find the median of median, which guarantees a 30/70 to 70/30 split
		float split = medianOfMedians(pList);
		//System.out.println(split);
		
		// Partition the points into <= split and > split.
		ArrayList<Point2D> left = new ArrayList<Point2D>();
		ArrayList<Point2D> right = new ArrayList<Point2D>();
		for(Point2D p : pList) {
			if(p.x <= split) {
				left.add(p);
			}
			else {
				right.add(p);
			}
		}
		
		/*System.out.println("Goal="+goal+" LeftSize="+left.size()+" RightSize="+right.size() + 
				" Split="+split);
		if(left.size() == 3) {
			System.out.println(left.get(0).x+" "+left.get(1).x+" "+left.get(2).x);
		}*/
		
		// We could be lucky and have found the ith element already.
		if(left.size() == goal) return split;
		// But its unlikely, so recurse!
		if(goal < left.size()) {
			return select(left,goal);
		}
		else {
			return select(right,goal-left.size());
		}	
	}
	
	// Median of medians guarantees between a 30/70 split and a 70/30 split.
	// Runs in linear time. The recursion T(n) = T(n/5) + O(n) solves to
	// O(n) by case 3 of the master theorem.
	public float medianOfMedians(List<Point2D> pList) {
		
		// If size <= 5 sort and return median
		if(pList.size() <= 5) {
			Collections.sort(pList);
			int item = pList.size() / 2;
			if(pList.size() % 2 != 0) {
				item = item + 1;
			}
			//System.out.println("item="+item+" size="+pList.size());
			return pList.get(item-1).x;
		}
		
		// Divide the points into groups of five, 
		// Sort each group and retrieve the median
		ArrayList<Point2D> temp = new ArrayList<Point2D>();
		ArrayList<Point2D> medians= new ArrayList<Point2D>();
		int j = 0;
		for(int i = 0; i < pList.size(); i++) {
			j++;
			temp.add(pList.get(i));
			if(j == 5) {
				Collections.sort(temp);
				int item = temp.size() / 2;
				if(temp.size() % 2 != 0) {
					item = item + 1;
				}
				Point2D median = temp.get(item-1);
				medians.add(median);
				temp = new ArrayList<Point2D>();
				j = 0;
			}
		}
		
		// Recurse
		return medianOfMedians(medians);
	}
	
}
