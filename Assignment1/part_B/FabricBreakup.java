import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

public class FabricBreakup {
	static Stack pile = new Stack();
	static int max = 0;

	public static int party() throws Exception {
		if (pile.isEmpty()) {
			return -1;
		} else {
			int[] maxi = (int[]) pile.pop();
			if(!pile.isEmpty()){
				int[] newMax = (int[]) pile.top();
				max = newMax[0];
			}else{
				max = 0;
			}
			return maxi[1];
		}
	}

	public static void addToPile(String tShirt) throws EmptyStackException {
		int val = Integer.parseInt(tShirt);
		if (pile.isEmpty()) {
			int[] t = { val, 0 };
			pile.push(t);
			max = val;
		} else {
			if (val < max) {
				try {
					int[] currentMax = (int[]) pile.pop();
					currentMax[1]++;
					pile.push(currentMax);
				} catch (Exception e) {
					throw new EmptyStackException("Empty");
				}

			} else {
				int[] newMax = { val, 0 };
				max = val;
				pile.push(newMax);
			}
		}
	}

	public static void main(String args[]) throws Exception {
		File file = new File(System.getProperty("user.dir") + "/" + args[0]);
		BufferedReader br = new BufferedReader(new FileReader(file));
		int n = Integer.valueOf(br.readLine());
		for (int i = 0; i < n; i++) {
			String[] input = br.readLine().split(" ");
			if (input[1].equals("1")) {
				addToPile(input[2]);
			} else {
				System.out.println(i + 1 + " " + party());
			}
		}
		br.close();
	}
}