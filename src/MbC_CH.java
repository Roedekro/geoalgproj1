import java.util.ArrayList;


public class MbC_CH {

	
	
	
	
	
	
	
	/*
	 * 1-dimensional Linear Program
	 * Input: p defines a line along which we solve
	 * pList defines constraints on p, reducing the feasability region
	 * m is the x value along which we want to minimize the program
	 * Output: Point2D x = a, y = b.
	 * 
	 * Example the point 1,1 and m = 2, defines an equation
	 * 1 => a * 1 + b
	 * > 1 = a * 1 + b
	 * > b = a * -1 + 1
	 * This forms a line in the coordinate set of (a,b)
	 * Along which we place constraints.
	 * This is all bounded by a=-100 to a=100.
	 */
	
	public Point2D lp1(ArrayList<Point2D> pList, Point2D p, float m) {
		
		// b = a * -x + y
		float x = p.x * -1;
		float y = p.y;
		
		float vmin = -100;
		float vmax = 100;
		// Run through all the points, making lines that represents them
		// Find the intersections of these lines with our main line
		// Update vmin and vmax as we go
		for(Point2D point : pList) {
			float pX = point.x * -1;
			float pY = point.y;
			
			// a = (y - pY) / (pX - x)
			float intersection = (y - pY) / (pX - x);
			System.out.println("Intersection found "+intersection);
			if(point.x > p.x && intersection > vmin) vmin = intersection;
			else if(point.x < p.x && intersection < vmax) vmax = intersection;

		}
		
		// Determine which of vmax and vmin minimizes	
		// C = a * m + b
		System.out.println("Max="+vmax);
		System.out.println("Min="+vmin);
		float bmin = vmin*x+y;
		float bmax = vmax*x+y;
		float cMin = vmin * m + bmin;
		float cMax = vmax * m + bmax;
		System.out.println("CMax="+cMax);
		System.out.println("CMin="+cMin);
		
		Point2D ret;
		if(cMin > cMax) ret = new Point2D(vmax,bmax,1);
		else ret = new Point2D(vmin,bmin,1);
		
		return ret;
		
	}
}
