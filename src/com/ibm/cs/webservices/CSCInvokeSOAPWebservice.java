package com.ibm.cs.webservices;

import java.io.ByteArrayOutputStream;
//import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
//import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import com.yantra.yfc.core.YFCObject;
//import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfc.util.YFCLocale;

public class CSCInvokeSOAPWebservice {

	private static final YFCLogCategory LOGGER = YFCLogCategory.instance(CSCInvokeSOAPWebservice.class);
	private static boolean isLogVerboseEnabled = LOGGER.isVerboseEnabled();

	String strOperationName = "";

	public SOAPMessage invokeSoapWebservice(SOAPMessage requestSOAPMessage, String endPointURL, String soapAction,
			String soapOperation, String uid, String pwd) throws Exception {
		
		
		LOGGER.verbose("SoapWebservice inDoc : " + requestSOAPMessage.toString());
		if (isLogVerboseEnabled) {
			LOGGER.verbose("SoapWebservice inDoc : " + requestSOAPMessage.toString());
		}

		SOAPMessage responseSOAPMessage = null;
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection connection = soapConnectionFactory.createConnection();
			if (!YFCObject.isVoid(uid) && !YFCObject.isVoid(pwd) && !YFCCommon.isVoid(soapAction) && !YFCCommon.isVoid(soapOperation) ) {
				this.addAuth(requestSOAPMessage, uid, pwd);
			}
			LOGGER.verbose("requestSOAPMessage::::"+requestSOAPMessage);
			System.out.println("requestSOAPMessage::::"+requestSOAPMessage.toString());
			
			responseSOAPMessage = connection.call(requestSOAPMessage, endPointURL);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			handleException(endPointURL, soapAction, exception);
		}
		System.out.println("SoapWebservice outDoc : " + responseSOAPMessage.toString());
		if (isLogVerboseEnabled) {
			LOGGER.verbose("SoapWebservice outDoc : " + responseSOAPMessage.toString());
		}
		return responseSOAPMessage;
	}
	

	/**
	 * This method is used for handling exceptions.
	 * 
	 * @param endPointURL
	 * @param soapAction
	 * @param soapActionURI
	 * @param soapEnvStr
	 * @param properties
	 * @param requestId
	 * @param exception
	 */
	private static void handleException(String endPointURL, String soapAction, Exception exception) throws Exception {

		String excepResponse = exception.getMessage();
		LOGGER.verbose("exception is :: handleException :: InvokeSOAPWebService " + excepResponse);
		// log the exception in case of errors
		YFCException yfcException;
		if (exception instanceof WebServiceException) {
			yfcException = new YFCException("Web Services Runtime Issues");
		} else if (exception instanceof SOAPFaultException) {
			yfcException = new YFCException("Fault occured while invoking the Service");
		} else {
			yfcException = new YFCException(exception.getMessage());
			yfcException.setErrorDescription(
					YFCException.getDescription(YFCLocale.getSysYFCLocale(), "Unknown error occured while invoking service, check stack trace for details"));
		}

		Map<String, String> mapWebServiceDetails = new HashMap<String, String>();
		mapWebServiceDetails.put("ErrorRelatedMoreInfo", exception.getMessage());
		mapWebServiceDetails.put("EndPointURL", endPointURL);
		mapWebServiceDetails.put("SoapAction", soapAction);

		yfcException.setAttributes(mapWebServiceDetails);
		yfcException.setStackTrace(exception.getStackTrace());

		throw yfcException;
	}

	private final SOAPMessage addAuth(final SOAPMessage message, String username, String password) {

		CSSecurityHdr csSecurityHdr = new CSSecurityHdr();
		try {
			SOAPElement soapElement = csSecurityHdr.generateHeader(username, password);
			message.getSOAPHeader().addChildElement(soapElement);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			message.writeTo(baos);
			String result = baos.toString();
			LOGGER.verbose("Response from Webservice Util is ::: "+ result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return message;
	}

}
