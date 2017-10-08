package com.ibm.cs.webservices;


import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import com.yantra.yfc.log.YFCLogCategory;

public class CSSecurityHdr  {

	private static final String WSU_PREFIX = "wsu";

	private static final String WSSE_PREFIX = "wsse";

	private static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

	private static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

	private static final YFCLogCategory LOGGER = YFCLogCategory.instance(CSSecurityHdr.class);
	private static boolean isLogVerboseEnabled = LOGGER.isVerboseEnabled();
	
	public SOAPElement generateHeader(String userid, String password) throws Exception {

		SOAPFactory soapFactory = SOAPFactory.newInstance();

		SOAPElement wsSecHeaderElm = soapFactory.createElement("Security",
				WSSE_PREFIX, WSSE_NS);

		SOAPElement userNameTokenElm = soapFactory.createElement(
				"UsernameToken", WSSE_PREFIX, WSSE_NS);

		Name userNameTokIdName = soapFactory.createName("Id", WSU_PREFIX,
				WSU_NS);
		userNameTokenElm.addAttribute(userNameTokIdName, "UsernameToken-1");

		SOAPElement userNameElm = soapFactory.createElement("Username",
				WSSE_PREFIX, WSSE_NS);
		userNameElm.addTextNode(userid);

		SOAPElement passwdElm = soapFactory.createElement("Password",
				WSSE_PREFIX, WSSE_NS);
		Name pwdTypeName = soapFactory.createName("Type");
		passwdElm
				.addAttribute(
						pwdTypeName,
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
		passwdElm.addTextNode(password);

		userNameTokenElm.addChildElement(userNameElm);
		userNameTokenElm.addChildElement(passwdElm);

		wsSecHeaderElm.addChildElement(userNameTokenElm);

		return wsSecHeaderElm;
	}
}

