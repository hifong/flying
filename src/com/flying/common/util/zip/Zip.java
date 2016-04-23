package com.flying.common.util.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;

public class Zip {
    private static final int BUFFER = 2048;

    public static void zip(String destZipFile, String source, String ignorePattern) throws Exception {
        FileOutputStream destOut = new FileOutputStream(destZipFile);
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(destOut));
        zipDirectory(zipOut, "", source, ignorePattern);
        zipOut.close();
        destOut.close();
    }
    
    public static void zipDirectory(ZipOutputStream zipOut, String baseDir, String directory, String ignorePattern) throws Exception {
    	Pattern pattern = null;
    	if(ignorePattern != null) pattern = Pattern.compile(ignorePattern);
    	
    	File dir = new File(directory);
    	if(!dir.isDirectory()) {
    		zipFile(zipOut, baseDir, dir);
    		return;
    	}
    	File[] files = dir.listFiles();
    	for(File file: files) {
    		if(pattern != null && pattern.matcher(file.getName()).matches()) continue;
    		if(file.isDirectory()) {
    			String subBaseDir = StringUtils.isEmpty(baseDir)?file.getName(): baseDir + "/" + file.getName();
    			zipDirectory(zipOut, subBaseDir , file.getAbsolutePath(), ignorePattern);
    			continue;
    		} else {
    			zipFile(zipOut, baseDir, file);
    		}
    	}
    }
    
    public static void zipFile(ZipOutputStream zipOut, String baseDir, File file) throws Exception {
    	String entryName = StringUtils.isEmpty(baseDir)?file.getName(): baseDir + "/" + file.getName();
        ZipEntry entry = new ZipEntry(entryName);
        zipOut.putNextEntry(entry);
        
        FileInputStream fi = new FileInputStream(file);
        BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
        
        int len = 0;
        byte[] buff = new byte[BUFFER];
        while((len = origin.read(buff)) > 0 ) {
        	zipOut.write(buff, 0, len);
        }
        
        origin.close();
        fi.close();
    }
    
    public static void main(String argv[]) throws Exception {
    	zip("E:\\tmp\\myfiles.zip", "E:\\osp\\framework\\WebRoot\\", ".*\\.svn.*");
    }
}