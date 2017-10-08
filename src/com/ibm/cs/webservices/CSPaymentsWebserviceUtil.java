package com.ibm.cs.webservices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.ibm.cs.utils.CSCommonUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class CSPaymentsWebserviceUtil implements YIFCustomApi {
	
	private static final YFCLogCategory LOGGER = YFCLogCategory.instance(CSPaymentsWebserviceUtil.class.getName());
	private Properties _props;

	public void setProperties(Properties arg0) throws Exception {
		_props = arg0;

	}
	
	public Document invokeCybersource(YFSEnvironment env, Document inDoc) throws Exception {
		
		
	
		String endPointURL = _props.getProperty("CS_END_POINT_URL");
		if (!YFCObject.isVoid(endPointURL))
			endPointURL = endPointURL.trim();

		String user = _props.getProperty("CS_USER_ID");
		if (!YFCObject.isVoid(user))
			user = user.trim();

		String password = _props.getProperty("CS_PASSWORD");
		if (!YFCObject.isVoid(password))
			password = password.trim();

		// Getting the soapActionURI
		String soapOperation = _props.getProperty("CS_SOAP_CS_OPERATION");
		if (!YFCObject.isVoid(soapOperation))

			soapOperation = soapOperation.trim();

		String soapAction = _props.getProperty("CS_SOAP_CS_ACTION");
		if (!YFCObject.isVoid(soapAction))

			soapAction = soapAction.trim();
		
		String strmerchId = _props.getProperty("CS_MERCHANT_ID");
		if (!YFCObject.isVoid(strmerchId))

			strmerchId = strmerchId.trim();
		
		String merchRefCode = _props.getProperty("CS_MERCHANT_REFERENCE_CODE");
		if (!YFCObject.isVoid(merchRefCode))

			merchRefCode = merchRefCode.trim();
		
		String strReq = null;
		SOAPMessage bffSOAPInput = null;
		
		
		
		if(!YFCObject.isVoid(strmerchId) && !YFCObject.isVoid(merchRefCode)){
			Transformer t = TransformerFactory.newInstance().newTransformer();
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(inDoc),new StreamResult(s));
			LOGGER.debug(new String(s.toByteArray()));
			
			strReq = "<?xml version=\"1.0\" ?>"+
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:foun=\"http://www.ibm.com/xmlns/prod/commerce/foundation\" " +
			"xmlns:urn=\"urn:schemas-cybersource-com:transaction-data-1.130\">"+
			"<soapenv:Header>"+
			"<wsse:Security soapenv:mustUnderstand=\"1\"  xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"+
			"<wsse:UsernameToken>"+
			"<wsse:Username>"+user+"</wsse:Username>"+
			"<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">"+ password+"</wsse:Password>"+
			"</wsse:UsernameToken>"+
			"</wsse:Security>"+
			"</soapenv:Header>"+
			"<soapenv:Body>" +new String(s.toByteArray())+"</soapenv:Body>"+
			"</soapenv:Envelope> ";
		}
		
		MessageFactory factory = MessageFactory.newInstance();
		if(!YFCObject.isVoid(strReq)){
			bffSOAPInput = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(strReq.getBytes(Charset.forName("UTF-8"))));
		}else{
			String outDocXml = SCXmlUtil.getString(inDoc);
			bffSOAPInput = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(outDocXml.getBytes(Charset.forName("UTF-8"))));
		}
		
		

		MimeHeaders headers = bffSOAPInput.getMimeHeaders();
		if(!YFCObject.isVoid(soapAction) && !YFCObject.isVoid(soapOperation)){
		headers.setHeader("SOAPAction", soapAction);
		headers.setHeader("Operation", soapOperation);
		}
		
		LOGGER.debug(" SOAP Request Message::"+CSCommonUtil.toString(bffSOAPInput));
		CSCInvokeSOAPWebservice hrdsInvokeSOAPWebservice = new CSCInvokeSOAPWebservice();

		SOAPMessage responseMessage = hrdsInvokeSOAPWebservice.invokeSoapWebservice(bffSOAPInput, endPointURL, soapAction, soapOperation, user, password);
		
		LOGGER.debug(" SOAP Request Message::"+CSCommonUtil.toString(responseMessage));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		responseMessage.writeTo(baos);
		String result = baos.toString();
		

		return SCXmlUtil.createFromString(result);
		
	}
}
