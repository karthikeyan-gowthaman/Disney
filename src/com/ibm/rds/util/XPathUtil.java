
package com.ibm.rds.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

/**
 *  <B>Important Note:It is strongly recommended to use XPathWrapper instead of this class</B>
 *  An utility class to use the Xpath to access the nodes in a xml document.
 */
public class XPathUtil {

    /**
     * 
     * @return Document
     * @throws Exception Exception
     * @deprecated use XMLUtil.newDocument() instead.
     */
    public static Document getDocument() throws Exception {
        //Create a new Document Bilder Factory instance
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        
        //Create new document builder
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        
        //Create and return document object
        return documentBuilder.newDocument();
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding node.
     *
     * @param node  document context.
     * @param xpath xpath that has to be evaluated.
     * @return node if found
     * @throws Exception exception
     */
    public static Node getNode(Node node, String xpath)
    throws TransformerException {
        if (null == node) {
            return null;
        }
        Node ret = null;
            ret = XPathAPI.selectSingleNode(node, xpath);
        return ret;
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding value as a String.
     *
     * @param node  document context.
     * @param xpath xpath that has to be evaluated.
     * @return String Value of the XPath Execution.
     * @throws Exception exception
     */
    public static String  getString(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        String value = null;
            XObject xobj = XPathAPI.eval(node, xpath);
            value = xobj.toString();
        return value;
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding node list.
     *
     * @param node  document context
     * @param xpath xpath to be evaluated
     * @return nodelist
     * @throws Exception exception
     */
    public static NodeList getNodeList(Node node, String xpath)
    throws TransformerException {
        if (null == node) {
            return null;
        }
        NodeList ret = null;
            ret = XPathAPI.selectNodeList(node, xpath);
        return ret;
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding node iterator.
     *
     * @param node  document context
     * @param xpath xpath to be evaluated
     * @return nodelist
     * @throws Exception exception
     */
    public static NodeIterator getNodeIterator(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        NodeIterator ret = null;
            ret = XPathAPI.selectNodeIterator(node, xpath);
        return ret;
    }
    
    /**
     * @param elName elName
     * @return Document
     * @throws Exception Generic Exception
     * @deprecated use XMLUtil.createDocument(String docElementTag)
     */
    public static Document getEmptyDoc(String elName) throws Exception {
        Document ret = XPathUtil.getDocument();
        Element el = ret.createElement(elName);
        ret.appendChild(el);
        return ret;
    }
}