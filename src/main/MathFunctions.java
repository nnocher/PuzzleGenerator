package main;

/**
 * Contains used MathFunctions
 * @author Nico Nocher
 *
 */

public class MathFunctions {

	public static boolean isPrime(int n) {
		for(int i = 2; i < n; i++) {
			if(n % i == 0) return true;
		}
		return false;
	}
}
