package com.flying.common.util.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZip {
    private static final int BUFFER = 2048;
    
    @SuppressWarnings("unchecked")
	public static void unzip(String zipFileName, String destDir) throws Exception {
    	if(!destDir.endsWith("/")) destDir += "/";
    	
        ZipFile zipFile = new ZipFile(zipFileName);
        Enumeration entries = zipFile.entries();
    	while(entries.hasMoreElements()) {
    		ZipEntry entry = (ZipEntry)entries.nextElement();
    		if(entry.isDirectory()) {
                new File(destDir + entry.getName()).mkdirs();
                continue;
    		} else {
                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                File file = new File(destDir + entry.getName());
                File parent = file.getParentFile();
                if(parent != null && (!parent.exists())){
                    parent.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos,BUFFER);           
                
                int count;
                byte data[] = new byte[BUFFER];
                while ((count = bis.read(data, 0, BUFFER)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();
                bis.close();
    		}
    	}
    	zipFile.close();
    }
    
	public static void main(String argv[]) throws Exception {
		unzip("E:\\tmp\\myfiles.zip", "E:\\tmp\\test");
    }
}
