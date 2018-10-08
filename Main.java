package convdec;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

	public static void main(String[] args) throws Exception {
	    Random rng = new Random();

	    Double noises[] = {
	      0.0,
	      0.01,
	      0.03,
	      0.05,
	      0.1,
	      0.15,
	      0.2,
	      0.23,
	      0.25,
	      0.3,
	      0.35,
	    };
	    
	    for (Double n : noises) {
	      int test_length = 1024 * 10;
	      List<Integer> message = new ArrayList<Integer>();
	      for (int i = 0; i < test_length; ++i) {
	        message.add(rng.nextInt(2));
	      }
	      Encoder enc = new Encoder();
	      List<Integer> code = enc.encode(message);
	      int flip_count = 0;
	      for (int i = 0; i < code.size(); ++i) {
	        if (rng.nextFloat() < n) {
	          code.set(i, code.get(i) ^ 1); /* bit flip */
	          flip_count++;
	        }
	      }
	      
	      Decoder dec = new Decoder(64);
	      List<Integer> decoded = dec.decode(code);
	      if (decoded.size() != message.size()) {
	        throw new Exception("Decoded size incorrect (Got " + decoded.size() + " expected " + message.size() + ")");
	      }
	      int error_count = 0;
	      int erasure_count = 0;
	      for (int i = 0; i < decoded.size(); ++i) {
	        if (decoded.get(i) != message.get(i)) {
	        	error_count++;
	        }
	        if (decoded.get(i) == null) {
	        	erasure_count++;
	        }
	      }
	      System.out.println("Failed to correct " + error_count + " errors (inc. " + erasure_count + " erasures) out of " + flip_count + " simulated errors (p=" + n + ")");
	    }
	}

}
