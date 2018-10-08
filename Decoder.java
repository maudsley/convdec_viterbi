package convdec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Decoder {
	
	static class TrellisPath {
		TrellisPath() {
		}
		TrellisPath(TrellisNode node, Integer cost) {
			vertices_.add(node);
			costs_.add(cost);
		}
		TrellisPath(TrellisPath src) {
			vertices_ = new ArrayList<TrellisNode>(src.vertices_);
			costs_ = new ArrayList<Integer>(src.costs_);
		}
		Integer cost() {
			Integer r = 0;
			for (Integer n : costs_) {
				r += n;
			}
			return r;
		}
		Integer length() {
			return vertices_.size();
		}
		TrellisPath extend(TrellisNode node, Integer cost) {
			TrellisPath r = new TrellisPath(this);
			r.vertices_.add(node);
			r.costs_.add(cost);
			return r;
		}
		TrellisNode pop() {
			TrellisNode first = vertices_.get(0);
			vertices_.remove(0);
			costs_.remove(0);
			return first;
		}
		List<TrellisNode> vertices_ = new ArrayList<TrellisNode>();
		List<Integer> costs_ = new ArrayList<Integer>();
	}
	
	Decoder(int window_width) {
		window_width_ = window_width;
		int state_max = 1 << enc_.constraintLength();
		nodes_ = new ArrayList<TrellisNode>();
		for (int i = 0; i < state_max; ++i) {
			enc_.setState(i);
			TrellisNode node = new TrellisNode(i);
			node.setCode(enc_.getCode());
			nodes_.add(node);
		}
	}
	
	String decode(String string) throws Exception {
		StringEncDec sed = new StringEncDec();
		List<Integer> bits = sed.fromString(string);
		List<Integer> decoded = decode(bits);
		
		String msg = "";
		char value = 0;
		int value_count = 0;
		for (Integer n : decoded) {
			value |= n << value_count;
			value_count++;
			if (value_count == 8) {
				msg += value;
				value = 0;
				value_count = 0;
			}
		}
		
		return msg;
	}
	
	List<Integer> decode(List<Integer> bits) throws Exception {
		bits_ = new ArrayList<Integer>(bits);
		int term_length = (window_width_ - enc_.constraintLength() + 1) * enc_.codeLength();
		for (int i = 0; i < term_length; ++i) {
			bits_.add(0); /* terminate the input stream */
		}
		
		List<Integer> output = new ArrayList<Integer>();
		
		List<TrellisPath> paths = new ArrayList<TrellisPath>();
		TrellisPath initial_path = new TrellisPath();
		for (int i = 0; i < window_width_; ++i) {
			initial_path = initial_path.extend(nodes_.get(0), 0);
		}
		paths.add(initial_path);
		
		while (bits_.size() >= enc_.codeLength()) {
			List<Integer> senseword = bits_.subList(0, enc_.codeLength());
			bits_ = bits_.subList(enc_.codeLength(), bits_.size());
			
			Map<Integer, TrellisPath> new_paths = new HashMap<Integer, TrellisPath>();

			if (paths.isEmpty()) {
				throw new Exception("No paths!");
			}

			for (TrellisPath p : paths) {
				for (int i = 0; i < 2; ++i) {
					TrellisNode n0 = p.vertices_.get(p.vertices_.size() - 1);
					enc_.setState(n0.state());
					enc_.pushState(i);
					TrellisNode n1 = nodes_.get(enc_.state());
					Integer d = n1.distance(senseword);
					TrellisPath new_path = p.extend(n1, d);
					TrellisPath best_path = new_paths.get(enc_.state());
					if (best_path == null || new_path.cost() < best_path.cost()) {
						new_paths.put(enc_.state(), new_path);
					}
				}
			}
			
			paths = new ArrayList<TrellisPath>(new_paths.values());

			boolean erasure = false;
			Integer state = null;
			for (TrellisPath p : paths) {
				TrellisNode node = p.pop();
				if (state == null) {
					state = node.state();
				} else if (state != node.state()) {
					erasure = true;
				}
			}

			if (erasure) {
				output.add(null);
			} else {
				output.add(state & 1);
			}
		}
		
		/* strip the sync header */
		output = output.subList(window_width_, output.size());
		
		return output;
	}
	
	int window_width_;
	Encoder enc_ = new Encoder();
	List<Integer> bits_;
	List<TrellisNode> nodes_;
}
