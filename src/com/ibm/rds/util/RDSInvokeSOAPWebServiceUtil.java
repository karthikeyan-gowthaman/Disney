package com.ibm.rds.util;

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
/**
 * This class is the util class which is used to invoke webservice
 * .
 * 
 * @author Shresta NM
 * 
 */
public class RDSInvokeSOAPWebServiceUtil extends BaseCustomAPI{

	private static final YFCLogCategory log = YFCLogCategory.instance(RDSInvokeSOAPWebServiceUtil.class.getName());
	
	Properties props;
	/**
	 * This method is overridden from interface.
	 * 
	 * @param properties
	 *            Properties set
	 */
	public void setProperties(Properties props) {
		log.debug("props:"+props);
		this.props = props;
	}


	public Document invokeWebService(YFSEnvironment env, Document docInput) throws Exception {
		log.debug("RDSInvokeSOAPWebServiceUtil:invokeWebService - Enter");
	
		SOAPMessage soapResponse=null;
		String url = null;
        try {
			
        	//set KEY_SOAP_MESSAGE_FACTORY,KEY_SOAP_CONNECTION_FACTORY
        	System.setProperty(Constants.KEY_SOAP_MESSAGE_FACTORY, Constants.DEFAULT_SOAP_MESSAGE_FACTORY);
        	System.setProperty(Constants.KEY_SOAP_CONNECTION_FACTORY, Constants.DEFAULT_SOAP_CONNECTION_FACTORY);
        	// Create SOAP Connection
            
        	SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            
            url = props.getProperty(Constants.END_POINT_URL);
    		log.debug("URL : "+url);
    		
            //Send SOAP Message to SOAP Server
    		log.debug("Input Document :"+SCXmlUtil.getString(docInput));
            SOAPMessage soapRequest=toSOAPMessage(docInput);
            log.debug("SOAP Request : "+soapRequest);
            
            soapResponse = soapConnection.call(soapRequest,url);
            log.debug("SOAP Response : "+soapResponse);
            
            if(soapResponse.getSOAPBody().hasFault()) {
				log.debug("Response has SOAPFault &&&&&&&&&&&&&&&&&&&");
				SOAPFault soapFault = soapResponse.getSOAPBody().getFault();
				log.debug("Fault response " + SCXmlUtil.getString(soapFault));  
			} else {
				log.debug("########## No Fault in SOAP Response #############");
			}
            
            soapConnection.close();
        } catch (Exception e) {
        	log.debug("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
            env.isRollbackOnly();
            throw e;
        }
        
        Document docout = toDocument(soapResponse);        
        log.debug("WebService ResponseDoc Null/Not-Null? "+docout);
        if (docout != null) {
            log.debug("WebService ResponseDoc Content :\n"+XMLUtil.getXMLString(docout));
        }
        
        log.debug("RDSInvokeSOAPWebServiceUtil:invokeWebService - Exit");
		return docout;
    }
		
	/**
	 * @param doc
	 * @return
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws SOAPException
	 * @throws IOException
	 * 
	 * Converts the Document to SOAPMessage
	 */
	public static SOAPMessage toSOAPMessage(Document doc) throws Exception {
		log.debug("RDSInvokeSOAPWebServiceUtil:toSOAPMessage - Enter");
		DOMSource src = new DOMSource(doc);
		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage soapMsg = mf.createMessage();
		soapMsg.getSOAPPart().setContent(src);
		log.debug("SOAP Message :"+soapMsg);
		
		log.debug("RDSInvokeSOAPWebServiceUtil:toSOAPMessage - Exit");
		return soapMsg;
	}
	
	/**
	 * @param request
	 * @return
	 * @throws SOAPException
	 * @throws ParserConfigurationException
	 * @throws Exception
	 * 
	 * This Method converts the SOAPMessage to Document
	 */
	private static Document toDocument(SOAPMessage request) throws Exception {
		log.debug("RDSInvokeSOAPWebServiceUtil:toDocument - Enter");
		Node root = null;
		Document docRequest = null;
		
		SOAPPart part = request.getSOAPPart();
		log.debug (">>> SOAPPart : "+part);
		
		log.debug (">>> request : "+request);
		
		log.debug (">>> request.getSOAPPart() : "+request.getSOAPPart());
		
		log.debug (">>> request.getSOAPPart().getContent() : "+request.getSOAPPart().getContent());

		Source source = request.getSOAPPart().getContent();
		
		if (source != null) {
			log.debug (">>> Source Type : "+source.getClass().getName());
			
			if (source instanceof DOMSource) {
				root = ((DOMSource) source).getNode();
				log.debug (">>> Root : "+root);
				
				if (root instanceof Document) {
					root = ((Document) root).getDocumentElement();
					//log.debug("root:"+((Document) root).getDocumentElement());
				} else {
					// TODO - Handle this issue after debugging.
					log.debug (">>> Root is not 'Document' type - Webservice call failed.");
				}
				
				Element eleRoot = (Element)root;	
				log.debug("Root Element :"+SCXmlUtil.getString(eleRoot));
				docRequest = SCXmlUtil.createFromString(SCXmlUtil.getString(eleRoot));
			} else {
				// TODO - Handle this issue after debugging.
				log.debug (">>> Source is not 'DOMSource' type - Webservice call failed.");
			}
		} else {
			// TODO - Handle this issue after debugging.
			log.debug (">>> Source is null - Webservice call failed.");
		}
			
		
		log.debug("docRequest:"+SCXmlUtil.getString(docRequest));
		
		log.debug("RDSInvokeSOAPWebServiceUtil:toDocument - Exit");
		return docRequest;

	}

	/**
	 * This method is overridden from interface.
	 * 
	 * @return Properties
	 */
	public Properties getProperties() {
		log.debug("this.mProperties:"+this.mProperties);
		return this.mProperties;
	}
}
