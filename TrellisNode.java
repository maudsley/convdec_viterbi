package convdec;

import java.util.List;

public class TrellisNode {
	TrellisNode(int state) {
		state_ = state;
	}
	void setCode(List<Integer> code) {
		code_ = code;
	}
	int state() {
		return state_;
	}
	int distance(List<Integer> rhs) throws Exception {
		if (rhs.size() != code_.size()) {
			throw new Exception("Invalid input size for distance");
		}
		int d = 0;
		for (int i = 0; i < code_.size(); ++i) {
			int x = code_.get(i) ^ rhs.get(i);
			d += x;
		}
		return d;
	}
	int state_;
	List<Integer> code_;
}
