import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class INC_CH {

	
	/*
	 * Incremental Convex Hull, also known as Grahams Scan.
	 * Return an arraylist of the points making up the convex hull,
	 * in clockwhise order.
	 */
	public ArrayList<Point2D> findHull (ArrayList<Point2D> input) {
		
		// 1. Sort the points by x-coordinate (y-coordinate if same x)
		// Arrays.sort uses merge-sort in O(n log n).
		//Arrays.sort(input);
		// Actually arrays.sort uses quicksort (bad), collections.sort uses merge-sort (good!)
		Collections.sort(input);
		
		// 2. Put the points p1 and p2 in lUpper with p1 as the first point
		// lUpper is an arraylist with amortized insertion O(1).
		ArrayList<Point2D> lUpper = new ArrayList<Point2D>();
		lUpper.add(input.get(0));
		lUpper.add(input.get(1));
		
		// 3. Run a for loop iterating over the rest of the elements in p
		for(int i = 2; i < input.size(); i++) {
			
			// 4. Append p[i] to lUpper.
			lUpper.add(input.get(i));
			//System.out.println("Added "+input.get(i).id);
			
			// 5. Run a while loop to check that the last 3 points in lUpper makes a right turn.
			while(lUpper.size() > 2 && !checkRightUpper(lUpper)) {
				
				// 6. Delete the middle of the last 3 points in lUpper
				//System.out.println("Removed "+lUpper.get(lUpper.size()-2).id);
				lUpper.remove(lUpper.size()-2);
				
			}
		}
		
		//System.out.println("LOWER");
		
		// 7. Put the points pn and pn-1 in lLower
		ArrayList<Point2D> lLower = new ArrayList<Point2D>();
		//lUpper.add(input[input.length-1]);
		//lUpper.add(input[input.length-2]);
		lLower.add(input.get(input.size()-1));
		lLower.add(input.get(input.size()-2));
		
		// 8. Run a for loop iterating over the rest of the elements in p
		for(int i = input.size()-3; i >= 0; i--) {
			
			// 9. Append pi to lLower
			lLower.add(input.get(i));
			//System.out.println("Added " + input.get(i).id);
			
			// 10. Run a while loop checking that the last 3 points in lLower turn right.
			while(lLower.size() > 2 && !checkRightLower(lLower)) {
				
				// 11. Delete the middle of the last 3 points in lLower
				//System.out.println("Removed "+lLower.get(lLower.size()-2).id);
				lLower.remove(lLower.size()-2);
			}
		}
		
		/*
		System.out.println("lUpper contains");
		for(int i = 0; i < lUpper.size(); i++) {
			System.out.println(lUpper.get(i).id);
		}
		System.out.println("lLower contains");
		for(int i = 0; i < lLower.size(); i++) {
			System.out.println(lLower.get(i).id);
		}
		*/
		
		// 12. Remove the first and last point of lLower
		lLower.remove(0);
		lLower.remove(lLower.size()-1);
		
		// 13. Append lLower to lUpper
		lUpper.addAll(lLower);
		
		return lUpper;
	}

	/*
	 * Check if the last 3 points in p makes a right turn.
	 * That is, check that the middle point is above
	 * the line between the first and the last point.
	 * This method is for the upper hull.
	 * Returns true if everything is okay.
	 */
	public static boolean checkRightUpper(ArrayList<Point2D> p) {
		
		Point2D p1, p2, p3;
		p1 = p.get(p.size()-3);
		p2 = p.get(p.size()-2);
		p3 = p.get(p.size()-1);
		
		// First find the slope
		float a = (p3.y-p1.y) / (p3.x - p1.x);
		
		// Now use the formula y - y1 = a ( x - x1)
		
		float b = p1.x*-1 * a; // y - y1 = a x - a x1
		b = b + p1.y; // y = a x - a x1 + y1
		// Or y = a x + b;
		
		//System.out.println("a = " + a);
		//System.out.println("b = " + b);
		// Check if p2 is on, above or below
		float y = p2.x * a + b;
		//System.out.println("For point " + p2.id);
		//System.out.println("calculated y = " + y + " and our y = " + p2.y);
		if(p2.y > y) {
			return true; // p2.y is above the corresponding y on the line between p1 and p3.
		}
		else return false;
	}
	
	/*
	 * Check that the last 3 points in the list makes
	 * a right turn. That is, that the middle point
	 * is below the line between p1 and p2.
	 * This method is for the lower hull.
	 * Returns true if everything is okay.
	 */
	public boolean checkRightLower(ArrayList<Point2D> p) {
		Point2D p1, p2, p3;
		p1 = p.get(p.size()-3);
		p2 = p.get(p.size()-2);
		p3 = p.get(p.size()-1);
		
		// First find the slope
		float a = (p3.y-p1.y) / (p3.x - p1.x);
		
		// Now use the formula y - y1 = a ( x - x1)
		
		float b = p1.x*-1 * a; // y - y1 = a x - a x1
		b = b + p1.y; // y = a x - a x1 + y1
		// Or y = a x + b;
		
		//System.out.println("a = " + a);
		//System.out.println("b = " + b);
		// Check if p2 is on, above or below
		float y = p2.x * a + b;
		//System.out.println("For point " + p2.id);
		//System.out.println("calculated y = " + y + " and our y = " + p2.y);
		if(p2.y < y) {
			return true; // p2.y is below the corresponding y on the line between p1 and p3.
		}
		else return false;
	}
}
