package convdec;

import java.util.ArrayList;
import java.util.List;

public class Encoder {
	Encoder() {
		state_ = 0;
		cl_ = 5;
		taps_.add(17);
		taps_.add(19);
		taps_.add(21);
		taps_.add(21);
		taps_.add(23);
		taps_.add(25);
		taps_.add(27);
		taps_.add(27);
		taps_.add(29);
		taps_.add(31);
		taps_.add(31);
	}
	
	Encoder(Encoder src) {
		cl_ = src.cl_;
		state_ = src.state_;
		taps_ = src.taps_;
	}
	
	int constraintLength() {
		return cl_;
	}
	
	int codeLength() {
		return taps_.size();
	}
	
	int state() {
		return state_ & ~(1 << cl_);
	}
	
	void setState(int state) {
		state_ = state;
	}
	
	void pushState(int bit) {
		state_ = (state_ << 1) | bit;
	}
	
	List<Integer> getCode() {
		List<Integer> result = new ArrayList<Integer>();
		for (Integer t : taps_) {
			int m = state_ & t;
			int r = 0;
			for (int i = 0; i < cl_; ++i) {
				r ^= (m >> i) & 1;
			}
			result.add(r);
		}
		return result;
	}
	
	List<Integer> encode(List<Integer> stream) {
		state_ = 0;
		List<Integer> bits = new ArrayList<Integer>();
		for (int i = 0; i < stream.size(); ++i) {
			pushState(stream.get(i));
			bits.addAll(getCode());
		}
		/* terminate the stream */
		for (int i = 0; i < cl_ - 1; ++i) {
			pushState(0);
			bits.addAll(getCode());
		}
		return bits;
	}
	
	String encode(String string) {
		List<Integer> bits = new ArrayList<Integer>();
		for (int i = 0; i < string.length(); ++i) {
			int c = string.charAt(i);
			for (int j = 0; j < 8; ++j) {
				int bit = (c >> j) & 1;
				pushState(bit);
				bits.addAll(getCode());
			}
		}
		
		StringEncDec sed = new StringEncDec();
		return sed.toString(bits);
	}
	
	int cl_;
	int state_;
	List<Integer> taps_ = new ArrayList<Integer>();
}
