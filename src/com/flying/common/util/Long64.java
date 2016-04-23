package com.flying.common.util;

/**
 * 长整数与64进制数据互换
 * @author wanghaifeng
 * 
 */
public class Long64 {
	private final static char[] CHARS64 = {
		'0','1','2','3','4','5','6','7','8','9',
		'A','B','C','D','E','F','G','H','I','J',
		'K','L','M','N','O','P','Q','R','S','T',
		'U','V','W','X','Y','Z','a','b','c','d',
		'e','f','g','h','i','j','k','l','m','n',
		'o','p','q','r','s','t','u','v','w','x',
		'y','z','@', '!'
	};
	
	public static int charAt(char c) {
		if(c >= '0' && c <= '9'){
			return (int)c - 48;
		}
		if(c >= 'A' && c <= 'Z'){
			return (int)c - 65 + 10;
		}
		if(c >= 'a' && c <= 'z'){
			return (int)c - 97 + 10 + 26;
		}
		if(c == '@') return 62;
		if(c == '!') return 63;
		return -1;
	}
	
	public static char[] generatChar64() {
		char[] char64 = new char[64];
		int c = 0;
		for(int i='0'; i<= '9'; i++) {
			char64[c] = (char)i;
			c ++;
		}
		for(int i='A'; i<= 'Z'; i++) {
			char64[c] = (char)i;
			c ++;
		}
		for(int i='a'; i<= 'z'; i++) {
			char64[c] = (char)i;
			c ++;
		}
		char64[c] = '@';
		char64[c++] = '!';
		return char64;
	}
	
	public static String encodeAs64(final long x) {
		int mr = 0;
		for(long tmp = Math.abs(x) >>> 6; tmp > 0; ) {
			tmp = tmp >>> 6;
			mr ++;
		}
		//
		long tmp = Math.abs(x);
		StringBuffer res = new StringBuffer();
		if(x < 0) {
			res.append("-");
		}
		for(int i=mr; i >= 0; i--) {
			if(i > 0) {
				long ll = tmp >>> (6 * i);
				res.append(CHARS64[(int)ll]);
				
				tmp -= ll << (6 * i);
			} else {
				res.append(CHARS64[(int)tmp]);
			}
		}
		return res.toString();
	}
	
	public static long decodeAs64(final String x) {
		long res = 0;
		boolean b = false;
		for(int i = 0; i < x.length(); i++) {
			final char c = x.charAt(i);
			if(c == '-' && i == 0) {
				b = true;
				continue;
			}
			res += charAt(c) * (1 << (6 *  (x.length() - 1 - i)));
		}
		if(b) res *= -1;
		return res;
	}
	
	public static void main(String[] args) throws Exception {
//		long in = Long.MAX_VALUE;
//		for(in = 99912000; in < 120000000; in ++) {
//			String tmp = encodeAs64(in);
//			System.out.println(tmp);
//			long out = decodeAs64(tmp);
//			//System.out.println(out);
//			if(out != in) {
//				System.err.println("IN\t" + in);
//				System.err.println("EN\t" + tmp);
//				System.err.println("OUT\t" + out);
//			}
//		}
		System.out.println(Long64.encodeAs64(24436869));
	}
}
