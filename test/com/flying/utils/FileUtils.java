package com.flying.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileUtils {
	static String dest = "C:\\Temp\\img\\";
	static int folderIndex;
	
	public static void main(String[] args) throws Exception {
		File root = new File("C:\\Users\\海峰\\Downloads\\p\\");
		handle(root, 0);
	}
	
	private static void handle(File f, int fileIndex) {
		if(f.isDirectory()) {
			File[] fs = f.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".jpg") || name.indexOf(".") < 0;
				}
				
			});
			int fi = 0;
			folderIndex ++;
			for(File f2: fs) {
				fi ++;
				handle(f2, fi);
			}
		} else {
			String fileName = "IMG" + str(folderIndex, 3) + "-" + str(fileIndex, 3) + ".jpg";
			f.renameTo(new File(dest + fileName));
		}
	}
	
	static String str(int val, int len) {
		String res = String.valueOf(val);
		while(res.length() < len)
			res = "0" + res;
		return res;
	}
}
