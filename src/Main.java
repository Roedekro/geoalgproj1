import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		/*ArrayList<Point2D> p = new ArrayList<Point2D>();
		p.add(new Point2D(0, 1, 1));
		p.add(new Point2D(2, 2.1f, 2));
		p.add(new Point2D(4, 3, 3));*/
		
		//boolean b = checkRightUpper(p);
		//System.out.println(b);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Welcome to our Convex Hull program");
		System.out.println("The syntax is as follows:");
		System.out.println("Generate n - Generate all test files up to size n");
		System.out.println("<test> <filename> <n> - Run test A/B/C/G");
		System.out.println("With filename as input, and optinal n");
		System.out.println("If n is given then the test will read n lines from input");
		System.out.println("Otherwhise the entire file will be used as input");
		System.out.println("Generated tests are named test1.txt, test2.txt and test3.txt");
		System.out.println("Type Exit to exit");
		System.out.println("Enjoy!");
		
		String in;
		
		while((in = br.readLine().toLowerCase()) != null) {
			String[] s = in.split("\\s+");
			if(s[0].equals("generate")) {
				generate(Integer.parseInt(s[1]));
				System.out.println("Done!");
			}
			else if(s[0].equals("exit")) {
				return;
			}
			else if(s[0].equals("testlp1")) {
				MbC_CH m = new MbC_CH();
				Point2D p = new Point2D(3,6,1);
				ArrayList<Point2D> pList = new ArrayList<Point2D>();
				pList.add(new Point2D(1,2,1));
				pList.add(new Point2D(3,4,1));
				pList.add(new Point2D(5,4,1));
				
				Point2D ret = m.lp1(pList,4, p, 2);
				System.out.println("LP1 returned a="+ret.x + " and b="+ret.y);
			}
			else {
				
				// Get filename
				String filename = s[1];
				ArrayList<Point2D> input = openFile(filename);
				// Trim input length if n is given
				if(s.length == 3) {
					int n = Integer.parseInt(s[2]);
					int j = input.size();
					for(int i = j; i  > n; i--) {
						input.remove(i-1);
					}
				}
				
				
				if(s[0].equals("a")) {
					
					long time = System.currentTimeMillis();
					INC_CH inc = new INC_CH();
					ArrayList<Point2D> ret = inc.findHull(input);
					time = System.currentTimeMillis() - time;
					System.out.println("INC_CH took " + time + " milliseconds to complete");
					outFile(ret,"incChOutput.txt");
				}
				else if(s[0].equals("b")) {
					
					long time = System.currentTimeMillis();
					QH_CH qh = new QH_CH();
					ArrayList<Point2D> ret = qh.findHull(input);
					time = System.currentTimeMillis() - time;
					System.out.println("QH_CH took " + time + " milliseconds to complete");
					outFile(ret,"qhChOutput.txt");
				}
				else if(s[0].equals("c")) {
					long time = System.currentTimeMillis();
					MbC2_CH mbc = new MbC2_CH();
					ArrayList<Point2D> ret = mbc.findHull(input);
					time = System.currentTimeMillis() - time;
					System.out.println("MbC_CH took " + time + " milliseconds to complete");
					outFile(ret,"MbCOutput.txt");
				}
				
				else if(s[0].equals("g")) {
					long time = System.currentTimeMillis();
					List<Point2D> ret = CSY_CH.findHull(input);
					time = System.currentTimeMillis() - time;
					System.out.println("CSY_CH took " + time + " milliseconds to complete");
					outFile(ret, "csyChOutput.txt");

				}	
			}
		}
		
		br.close();
	}
	
	public static ArrayList<Point2D> openFile(String s) throws IOException {
		
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(s)));
		String line;
		int i = 0;
		while((line = br.readLine()) != null) {
			i++;
			String[] numbers = line.split("\\s+");
			float x = Float.parseFloat(numbers[0]);
			float y = Float.parseFloat(numbers[1]);
			ret.add(new Point2D(x,y,i));
		}
		
		br.close();
		return ret;
		
	}
	
	public static void generate(int n) throws IOException {
		
		String test1 = "test1.txt";
		String test2 = "test2.txt";
		String test3 = "test3.txt";
		
		// Test 1: Random distribution within a square (100x100).
		BufferedWriter wr1 = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(test1)));
		
		Random r = new Random();
		for(int i = 0; i < n; i++) {
			if(i != 0) wr1.newLine();
			float x = r.nextFloat() * 100;
			float y = r.nextFloat() * 100;
			wr1.write(x + " " + y);
		}
		
		wr1.flush();
		wr1.close();
		
		
		// Test 2: Random distribution within a circle.
		BufferedWriter wr2 = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(test2)));
		
		for(int i = 0; i < n; i++) {
			if(i != 0) wr2.newLine();
			float rad = r.nextFloat() * 50;
			float angle = r.nextFloat() * 360;
			float x = (float) (50 + rad * Math.cos(angle));
			float y = (float) (50 + rad * Math.sin(angle));
			wr2.write(x + " " + y);
		}
		
		wr2.flush();
		wr2.close();
		
		
		// Test 3: Random distribution along Y = X ^ 2.
		BufferedWriter wr3 = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(test3)));
		
		for(int i = 0; i < n; i++) {
			if(i != 0) wr3.newLine();
			float x = r.nextFloat() * 100;
			float y = (float) Math.pow(x,2);
			wr3.write(x + " " + y);
		}
		
		wr3.flush();
		wr3.close();
		
	}
	
	public static void outFile(List<Point2D> list, String s) throws IOException {
		
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(s)));
		
		for(int i = 0; i < list.size(); i++) {
			if(i!=0) wr.newLine();
			wr.write(String.valueOf(list.get(i).id));
		}
		
		wr.flush();
		wr.close();
	}
	
}
