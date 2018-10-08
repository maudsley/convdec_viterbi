package convdec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringEncDec {
	StringEncDec() {
		for (int i = 0; i < lut_.length(); ++i) {
			rlut_.put((int)lut_.charAt(i), i);
		}
	}
	
	List<Integer> fromString(String msg) {
		List<Integer> r = new ArrayList<Integer>();
		for (int i = 0; i < msg.length(); ++i) {
			int c = msg.charAt(i);
			int v = 0;
			if (rlut_.containsKey(c)) {
				v = rlut_.get(c);
			}
			for (int j = 0; j < 6; ++j) {
				int k = 5 - j;
				int bit = (v >> k) & 1;
				r.add(bit);
			}
		}
		return r;
	}
	
	String toString(List<Integer> msg) {
		String r = "";
		Integer state = 0;
		int count = 0;
		for (Integer n : msg) {
			state = (state << 1) | n;
			count++;
			if (count == 6) {
				r += lut_.charAt(state);
				state = 0;
				count = 0;
			}
		}
		if (count != 0) {
			r += lut_.charAt(state);
		}
		return r;
	}
	
	Map<Integer, Integer> rlut_ = new HashMap<Integer, Integer>();
	String lut_ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
}
