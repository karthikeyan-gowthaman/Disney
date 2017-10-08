package com.ibm.cs.webservices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class CSWebserviceUtil implements YIFCustomApi {
	
	private static final YFCLogCategory LOGGER = YFCLogCategory.instance(CSWebserviceUtil.class.getName());
	private Properties _props;

	public void setProperties(Properties arg0) throws Exception {
		_props = arg0;

	}
	
	public Document invokeWebservice(YFSEnvironment env, Document inDoc) throws Exception {
		
		
	
		String endPointURL = _props.getProperty("CS_END_POINT_URL");
		if (!YFCObject.isVoid(endPointURL))
			endPointURL = endPointURL.trim();

		String user = _props.getProperty("CS_USER_ID");
		if (!YFCObject.isVoid(user))
			user = user.trim();

		String password = _props.getProperty("CS__PASSWORD");
		if (!YFCObject.isVoid(password))
			password = password.trim();

		// Getting the soapActionURI
		String soapOperation = _props.getProperty("CS_SOAP_OPERATION");
		if (!YFCObject.isVoid(soapOperation))

			soapOperation = soapOperation.trim();

		String soapAction = _props.getProperty("CS_SOAP_ACTION");
		if (!YFCObject.isVoid(soapAction))

			soapAction = soapAction.trim();
		
		LOGGER.verbose("endPointURL::"+endPointURL);
		LOGGER.verbose("user::"+user);
		LOGGER.verbose("password::"+password);
		LOGGER.verbose("soapOperation::"+soapOperation);
		LOGGER.verbose("soapAction::"+soapAction);
		
		String inDocXml = SCXmlUtil.getString(inDoc);
		MessageFactory factory = MessageFactory.newInstance();
		SOAPMessage gcSOAPInput = factory.createMessage(null, new ByteArrayInputStream(inDocXml.getBytes(Charset.forName("UTF-8"))));
		

		MimeHeaders headers = gcSOAPInput.getMimeHeaders();
		headers.setHeader("SOAPAction", soapAction);
		headers.setHeader("Operation", soapOperation);
		CSCInvokeSOAPWebservice csInvokeSOAPWebservice = new CSCInvokeSOAPWebservice();
		LOGGER.verbose("Invoke webservice start");
		SOAPMessage responseMessage = csInvokeSOAPWebservice.invokeSoapWebservice(gcSOAPInput, endPointURL, soapAction, soapOperation, user, password);
		
		LOGGER.verbose("Invoke webservice end");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		responseMessage.writeTo(baos);
		String result = baos.toString();
		
		LOGGER.verbose("result::"+result);
		
		return SCXmlUtil.createFromString(result);
		
	}

}
