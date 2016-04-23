/*------------------------------------------------------------------------------
 * COPYRIGHT Aspire 2011
 *
 * The copyright to the computer program(s) herein is the property of
 * Aspire Inc. The programs may be used and/or copied only with written
 * permission from Aspire Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.flying.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.flying.common.log.Logger;
import com.flying.common.util.StringUtils;

public class XMLUtils {
    private static Logger logger = Logger.getLogger(XMLUtils.class);

    public static Document convertInputStreamToDoc(InputStream in) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(in);
        } catch (java.io.IOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        return doc;
    }
    
    public static Document convertStringToDoc(String xml) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(xml);
        } catch (java.io.IOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        return doc;
    }

    public static String convertDocToString(Document doc) throws IOException {
        // write the document to output stream ("out")
        // ByteArrayOutputStream out = new ByteArrayOutputStream();
        // OutputFormat format = new OutputFormat(doc);
        // XMLSerializer serializer = new XMLSerializer(out, format);
        // serializer.serialize(doc.getDocumentElement());
        // InputStream is = new ByteArrayInputStream(out.toByteArray());
        // StringBuffer stringBuffer = new StringBuffer();
        // byte[] b = new byte[1024];
        // int len = 0;
        // while ((len = is.read(b)) > -1) {
        // for (int i = 0; i < len; i++) {
        // stringBuffer.append((char) b[i]);
        // }
        // }
        // return stringBuffer.toString().substring(39);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TransformerFactory transFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transFactory.newTransformer();
            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.setOutputProperty("indent", "yes");

            DOMSource source = new DOMSource();
            source.setNode(doc.getDocumentElement());
            StreamResult result = new StreamResult();
            result.setOutputStream(out);

            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        InputStream is = new ByteArrayInputStream(out.toByteArray());
        StringBuffer stringBuffer = new StringBuffer();
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = is.read(b)) > -1) {
            for (int i = 0; i < len; i++) {
                stringBuffer.append((char) b[i]);
            }
        }
        return stringBuffer.toString().substring(43);
    }

    public static Node searchSingleNode(String express, Object source) {
        Node result = null;
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
            result = (Node) xpath.evaluate(express, source, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            logger.error(e);
        }

        return result;
    }

    public static NodeList searchNodes(String express, Object source) {
        NodeList result = null;
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        try {
            result = (NodeList) xpath.evaluate(express, source, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.error(e);
        }

        return result;
    }
    
    public static String replaceIllegalChar(String xml) {
    	if(StringUtils.isBlank(xml)){
    		return xml;
    	}
    	return xml.replace("&", "&amp;").replace(">", "&gt;")
    	          .replace("<", "&lt;").replace("\"", "&quot;")
    	          .replace("'", "&apos;");
    }

}
