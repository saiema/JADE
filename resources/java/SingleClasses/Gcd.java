package java.SingleClasses;

public class Gcd {
	
	/*@
	@ requires a >= 0 && b >= 0;
	@ ensures \result >= 0;
	@ ensures (\exists int q; \result*q == a) &&
	@ (\exists int q; \result*q == b) &&
	@ (\forall int c;
	@ (\exists int q; c*q == a) && (\exists int q; c*q == b);
	@ (\exists int q; c*q == \result));
	@*/
	public static int gcd(Integer a, Integer b) {

	  if (a == 0) { //mutGenLimit 1
		  return b;
	  }
	  else
	  {
	    while (b != 0) { //mutGenLimit 1
	      if (a > b) {
	        a = a - b;
	      } else {
	        b = b - a;
	      }
	    }
	    return a;
	  }
	} 
	 
	
	
}
