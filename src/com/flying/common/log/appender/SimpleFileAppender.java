/*
 * @(#)SimpleFileAppender.java        1.6.5 05/03/04
 *
 * Copyright (c) 2003-2005 ASPire Technologies, Inc.
 * 6/F,IER BUILDING, SOUTH AREA,SHENZHEN HI-TECH INDUSTRIAL PARK Mail Box:11# 12#.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ASPire Technologies, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Aspire.
 */

package com.flying.common.log.appender;

import org.apache.log4j.*;
import java.io.*;

/**
 * the appender to a fixed file
 *
 * @author x_biran
 * @version 1.0 history: created at 3/11/2007
 */

public class SimpleFileAppender extends FileAppender
{

    private final static String FS = System.getProperty("file.separator");

    public SimpleFileAppender()
    {

    }

    /**
     * only overload this method to set the file in $domain path
     *
     * @param file String
     */
    public void setFile(String file)
    {

        String val = file.trim();
        File domain = new File(".");
        try
        {
            fileName = domain.getCanonicalPath() + FS + val;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

}
