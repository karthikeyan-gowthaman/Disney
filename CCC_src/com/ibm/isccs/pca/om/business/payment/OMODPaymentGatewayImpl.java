/*******************************************************************************
 * IBM Confidential
 *   OCO Source Materials
 *   5725-G69
 *   Copyright IBM Corporation 2011,2012
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 ******************************************************************************/
package com.ibm.isccs.pca.om.business.payment;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import org.w3c.dom.Document;

import com.ibm.pca.om.business.utils.XMLOverHttpsHelper;
import com.ibm.shared.omod.OMODErrorCodes;
import com.ibm.shared.omod.OMODLiterals;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCConfigurator;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;

public class OMODPaymentGatewayImpl extends XMLOverHttpsHelper {

	public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";

	public static YFCLogCategory cat = YFCLogCategory
			.instance(OMODPaymentGatewayImpl.class);

	/* private static final String PG_REQUEST_URI = "https://ips-preprod.ihost.com:50443/trx";
	private static final String PG_KEYSTORE_PATH = "c:/IBM95/keystore/DEMO-TEST-0002.jks";
	private static final String PG_KEYSTORE_PW = "HBnx7T7uy2aH";
	private static final String PG_BASIC_REQUEST_URI = "https://ips-preprod.ihost.com:50443/";*/
	private static final String PG_REQUEST_URI   = YFCConfigurator.getInstance().getProperty("omod.DEFAULT.Payment.URL");
	private static final String PG_KEYSTORE_PATH = YFCConfigurator.getInstance().getProperty("omod.DEFAULT.PaymentGateway.KeystorePath");
	private static final String PG_KEYSTORE_PW   = YFCConfigurator.getInstance().getProperty("omod.DEFAULT.PaymentGateway.KeystorePassword");	
	private static final String PG_BASIC_REQUEST_URI=YFCConfigurator.getInstance().getProperty("omod.DEFAULT.PaymentGateway.BasicGetURL");

	Properties _properties = setProperties();
	boolean isAuthRequest = false;
	String sTransactionReference = "";

	public Document postCheckoutStartSession(YFSEnvironment env, Document inDoc)
			throws IOException, YIFClientCreationException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			KeyManagementException, UnrecoverableKeyException {
		cat.beginTimer("OMODPaymentGatewayImpl.post");

		_properties.setProperty(OMODLiterals.OMOD_REQUEST_URL, PG_REQUEST_URI);
		super.setProperties(_properties);
		YFCDocument inpDoc = YFCDocument.getDocumentFor(inDoc);

		YFCDocument respDoc = null;

		respDoc = super.post(inpDoc.getOwnerDocument());

		return respDoc.getDocument();

	}

	public Document getSessionDetails(YFSEnvironment env, Document inDoc)
			throws IOException, YIFClientCreationException,
			KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, UnrecoverableKeyException {
		cat.beginTimer("OMODPaymentGatewayImpl.get");

		super.setProperties(_properties);

		String strSessionId = inDoc.getDocumentElement().getAttribute(
				"SessionId");
		String sURLToPost = PG_BASIC_REQUEST_URI.concat("ses/")
				.concat(strSessionId).concat("?view=status");
		YFCDocument respDoc = super.get(sURLToPost);

		cat.endTimer("OMODPaymentGatewayImpl.get");

		return respDoc.getDocument();
	}

	public Document getTransactionDetails(YFSEnvironment env, Document inDoc)
			throws IOException, YIFClientCreationException,
			KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, CertificateException, UnrecoverableKeyException {
		cat.beginTimer("OMODPaymentGatewayImpl.get");

		super.setProperties(_properties);

		String strXID = inDoc.getDocumentElement().getAttribute("XID");
		String sURLToPost = PG_BASIC_REQUEST_URI.concat("aut/").concat(strXID);
		YFCDocument respDoc = super.get(sURLToPost);

		cat.endTimer("OMODPaymentGatewayImpl.get");

		return respDoc.getDocument();
	}

	public Document postTokenisation(YFSEnvironment env, Document inDoc)
			throws IOException, YIFClientCreationException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			KeyManagementException, UnrecoverableKeyException {
		cat.beginTimer("OMODPaymentGatewayImpl.post");

		_properties.setProperty(OMODLiterals.OMOD_REQUEST_URL,
				PG_BASIC_REQUEST_URI.concat("tkn"));
		super.setProperties(_properties);
		YFCDocument inpDoc = YFCDocument.getDocumentFor(inDoc);

		YFCDocument respDoc = null;

		respDoc = super.post(inpDoc);

		return respDoc.getDocument();

	}

	private Properties setProperties() {
		Properties properties = new Properties();
		if (YFCObject.isVoid(PG_REQUEST_URI)
				|| YFCObject.isVoid(PG_KEYSTORE_PATH)
				|| YFCObject.isVoid(PG_KEYSTORE_PW)) {
			throw new YFCException(
					OMODErrorCodes.OMOD_PROPERTIES_MISSING_FOR_PAYMENT);
		}
		properties.setProperty(OMODLiterals.OMOD_REQUEST_URL, PG_REQUEST_URI);
		properties.setProperty(OMODLiterals.OMOD_KEYSTORE_PATH,
				PG_KEYSTORE_PATH);
		properties.setProperty(OMODLiterals.OMOD_KEYSTORE_PW, PG_KEYSTORE_PW);
		return properties;
	}

	@Override
	public YFCDocument getXMLToPost(YFCDocument inpDoc) {
		return null;
	}

	@Override
	public YFCDocument massageOutputToUEImpl(YFCDocument respDoc,
			YFCElement paymentInfo) {
		return null;
	}
}
