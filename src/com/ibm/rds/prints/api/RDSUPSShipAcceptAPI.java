package com.ibm.rds.prints.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.rds.common.util.RDSXMLLiterals;
import com.ibm.rds.common.util.RDSXmlUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class is invoked by service FossilUPSLabelsSyncService. This class has below logic
 * 1. Look up customer overrides to retrieve UPS Access information
 * 2. Concatenate access XML with the ship accept request XML
 * 2. Invokes shipAccept API from above XML by doing HTTP post
 * 3. Return the ship accept response message
 */

public class RDSUPSShipAcceptAPI {

	private static final YFCLogCategory log = YFCLogCategory.instance(RDSUPSShipAcceptAPI.class.getName());

	
	/**
	 * Method 
	 * 1. Retrieves Access Request details from Property
	 * 2. Creates Access Request Doc
	 * 3. Removes Return Ship Confirm Response
	 * 4. Invokes Ship Accept for Ship Label with the request
	 * 5. Invokes Ship Accept for Return Label with the request
	 * 
	 * @param yfsEnv
	 * @param docInXML
	 * @return API output
	 * @throws Exception
	 * 
	 * Sample input to the method
	 * <ShipmentAcceptRequest>
    		<Request>
        		<RequestAction>ShipAccept</RequestAction>
        		<RequestOption>1</RequestOption>
    		</Request>
    		<ShipmentDigest>RAO...</ShipmentDigest>
    		<ShipmentAcceptRequest>
    			<Request>
        			<RequestAction>ShipAccept</RequestAction>
        			<RequestOption>1</RequestOption>
    			</Request>
    			<ShipmentDigest>TYO...</ShipmentDigest>
    		</ShipmentAcceptRequest>
    	</ShipmentAcceptRequest>
	 */
	
	public Document shipAccept(YFSEnvironment yfsEnv, Document docInXML) throws Exception {
		
		log.info("Inside shipConfirm***");
		log.info("Inside shipConfirm***");
		log.verbose("Inside shipConfirm***");
		
		String strUpsUsername = YFSSystem.getProperty((String)"UPS_USERNAME");
		log.info("strUpsUsername: " + strUpsUsername);
        String strUpsPassword = YFSSystem.getProperty((String)"UPS_PASSWORD");
        log.info("strUpsPassword: " + strUpsPassword);
        String strUpsLicenseNumber = YFSSystem.getProperty((String)"UPS_LICENSE_NUMBER");
        log.info("strUpsLicenseNumber: " + strUpsLicenseNumber);

        Boolean bIgnoreRoot = false;
		//Create Access Request Document
        Document docAccessRequest = RDSXmlUtil.createDocument(RDSXMLLiterals.E_ACCESS_REQUEST);
        Element eleAccessRequest = docAccessRequest.getDocumentElement();
        RDSXmlUtil.appendTextChild(docAccessRequest, eleAccessRequest, RDSXMLLiterals.A_ACCESS_LICENSE_NUMBER, strUpsLicenseNumber, null);
        RDSXmlUtil.appendTextChild(docAccessRequest, eleAccessRequest, RDSXMLLiterals.A_USER_ID, strUpsUsername, null);
        RDSXmlUtil.appendTextChild(docAccessRequest, eleAccessRequest, RDSXMLLiterals.A_PASSWORD, strUpsPassword, null);
        
        Element eleShipAcceptRequest = docInXML.getDocumentElement();
        log.info("eleShipAcceptRequest: " + SCXmlUtil.getString(eleShipAcceptRequest));
        Element eleReturnShipAccept = RDSXmlUtil.getChildElement(eleShipAcceptRequest, RDSXMLLiterals.E_SHIPMENT_ACCEPT_REQUEST);
        log.info("eleReturnShipAccept: " + SCXmlUtil.getString(eleReturnShipAccept));
        
        //Remove the Return Ship Confirm Response for Ship Label call
        RDSXmlUtil.removeChild(eleShipAcceptRequest, eleReturnShipAccept);
        
        Document docShipLabelAcceptResponse = shipLabelAcceptRespone(yfsEnv, docInXML, docAccessRequest);
        log.info("docShipLabelAcceptResponse: " + SCXmlUtil.getString(docShipLabelAcceptResponse.getDocumentElement()));
        
        Document docReturnLabelAcceptResponse = returnLabelAcceptRespone(yfsEnv, eleReturnShipAccept, docAccessRequest);
        log.info("docReturnLabelAcceptResponse: " + SCXmlUtil.getString(docReturnLabelAcceptResponse.getDocumentElement()));
        
        //Merge Ship Label and Return Label Response
        Document docUPSShipAcceptResponse = RDSXmlUtil.addDocument(docShipLabelAcceptResponse, docReturnLabelAcceptResponse, bIgnoreRoot);
        log.info("docUPSShipAcceptResponse: " + SCXmlUtil.getString(docUPSShipAcceptResponse.getDocumentElement()));
		return docUPSShipAcceptResponse;
	}
	
	/**
	 * Method 
	 * 1. Retrieves Ship Accept Request details for Return Label
	 * 2. Invokes Ship Accept for Return Label by doing HTTP Post
	 * 3. Returns Ship Accept Response for Return Label
	 * 
	 * @param yfsEnv
	 * @param eleReturnShipAccept
	 * @param docAccessRequest
	 * @return API output
	 * @throws Exception
	 * 
	 * 
	 */
	
	private Document returnLabelAcceptRespone(YFSEnvironment yfsEnv, Element eleReturnShipAccept, Document docAccessRequest) throws Exception{
		
		Document docReturnShipAcceptResponse = null;
		Document docReturnShipAcceptRequest = null;
		try {
			log.info("eleReturnShipAccept: " + SCXmlUtil.getString(eleReturnShipAccept));
			log.info("docAccessRequest: " + SCXmlUtil.getString(docAccessRequest.getDocumentElement()));
			docReturnShipAcceptRequest = RDSXmlUtil.createDocument(RDSXMLLiterals.E_SHIPMENT_ACCEPT_REQUEST);
			Element eleReturnShipAcceptRequest = docReturnShipAcceptRequest.getDocumentElement();
			RDSXmlUtil.copyElement(docReturnShipAcceptRequest, eleReturnShipAccept, eleReturnShipAcceptRequest);
			log.info("docReturnShipAcceptRequest: " + SCXmlUtil.getString(docReturnShipAcceptRequest.getDocumentElement()));
			String strReturnShipAcceptResponse = httpPost(yfsEnv, docAccessRequest, docReturnShipAcceptRequest);
			log.info("strReturnShipAcceptResponse: " + strReturnShipAcceptResponse);
			docReturnShipAcceptResponse = RDSXmlUtil.getDocument(strReturnShipAcceptResponse);
		
		} catch (Exception e) {
			
			throw e;
		}
		return docReturnShipAcceptResponse;
	}
	
	/**
	 * Method 
	 * 1. Retrieves Ship Accept Request details for Ship Label
	 * 2. Invokes Ship Accept for Ship Label by doing HTTP Post
	 * 3. Returns Ship Accept Response for Ship Label
	 * 
	 * @param yfsEnv
	 * @param docInXML
	 * @param docAccessRequest
	 * @return API output
	 * @throws Exception
	 * 
	 * 
	 */
	
	private Document shipLabelAcceptRespone(YFSEnvironment yfsEnv,Document docInXML, Document docAccessRequest) throws Exception{
		
		Document docShipAcceptResponse = null;
		try {
			log.info("docInXML: " + SCXmlUtil.getString(docInXML.getDocumentElement()));
			log.info("docAccessRequest: " + SCXmlUtil.getString(docAccessRequest.getDocumentElement()));
						
			String strShipAcceptResponse = httpPost(yfsEnv, docAccessRequest, docInXML);
			log.info("strShipAcceptResponse: " + strShipAcceptResponse);
			docShipAcceptResponse = RDSXmlUtil.getDocument(strShipAcceptResponse);
		
		} catch (Exception e) {
			
			throw e;
		}
		return docShipAcceptResponse;
	}
	
	private String httpPost(YFSEnvironment yfsEnv, Document docAccessRequest,
			Document docInXML) throws Exception{
		
		String strShipAcceptResponse = null;
		try{
			
			String strDocAccessRequest = RDSXmlUtil.getXMLString(docAccessRequest);
			log.info("strDocAccessRequest: " + strDocAccessRequest);
			String strShipAcceptRequest = RDSXmlUtil.getXMLString(docInXML);
			log.info("strShipAcceptRequest: " + strShipAcceptRequest);
			String strRequestInput = strDocAccessRequest + strShipAcceptRequest;
			log.info("strRequestInput: " + strRequestInput);
			String upsShipAcceptURL = YFSSystem.getProperty((String)"UPS_SHIPACCEPT_URL");
			URL url = new URL(upsShipAcceptURL);	
			OutputStream outputStream = null;
		
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
			// Setup HTTP POST parameters
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			
			
			outputStream = connection.getOutputStream();
			outputStream.write(strRequestInput.getBytes());
			outputStream.flush();
			outputStream.close();
			log.info("Http status = " + connection.getResponseCode() + " " + connection.getResponseMessage());
			
			strShipAcceptResponse = readURLConnection(connection);	
			log.info("strShipAcceptResponse: " + strShipAcceptResponse);
		
		} catch (Exception e) {
			throw e;
		}
		return strShipAcceptResponse;
	}
	
	/**
	 * This method reads all of the data from a URL connection to a String
	 */

	public static String readURLConnection(URLConnection uc) throws Exception {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			int letter = 0;			
			reader.readLine();
			while ((letter = reader.read()) != -1){
				buffer.append((char) letter);
			}
			reader.close();
		} catch (Exception e) {
			throw e;
		} finally {
			if(reader != null){
				reader.close();
				reader = null;
			}
		}
		return buffer.toString();
	}
}
