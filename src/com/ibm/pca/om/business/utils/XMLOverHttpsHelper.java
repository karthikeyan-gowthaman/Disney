/*******************************************************************************
 * IBM Confidential
 *   OCO Source Materials
 *   5725-G69
 *   Copyright IBM Corporation 2011,2012
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 ******************************************************************************/
package com.ibm.pca.om.business.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.ibm.shared.omod.OMODErrorCodes;
import com.ibm.shared.omod.OMODLiterals;
import com.ibm.shared.sbcod.httppost.MessageContext;
import com.ibm.shared.sbcod.httppost.PostConfigurationManager;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;

public abstract class XMLOverHttpsHelper{

public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";

	String REQUEST_URI   = null;
	String KEYSTORE_PATH = null ;
	String KEYSTORE_PW   = null ;

	//static String PROJECT_CODE  = YFCConfigurator.getInstance().getProperty("omod.DEFAULT.PaymentGateway.TestPrjCode");;        // Replace with the project code assigned to you by IBM Payment Gateway
	public static YFCLogCategory cat = YFCLogCategory.instance(XMLOverHttpsHelper.class);

	public abstract YFCDocument getXMLToPost(YFCDocument inpDoc);
	public abstract YFCDocument massageOutputToUEImpl(YFCDocument respDoc,YFCElement paymentInfo);

	public YFCDocument post(YFCDocument inDoc) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException{

		cat.beginTimer("XMLOverHttpsHelper.post");
		checkProperties(REQUEST_URI);
		//setKeystoreProperties();
		// Set up an HTTPS connection

		try {

			MessageContext messCtx=getMessageContext();

			String reqString = new String();
			reqString=getRequestString(inDoc,messCtx);
			HttpsURLConnection conn=getConnection(messCtx.getMsgURL(),OMODLiterals.HTTP_POST_REQUEST,reqString);

			if(cat.isDebugEnabled())
				logHandshake(conn);    // For Testing

			DataOutputStream wstream = new DataOutputStream(conn.getOutputStream());
			wstream.writeBytes(reqString);
			wstream.close();

			return getResponseDocument(conn);

		}finally{
			cat.debug(" Exiting Post Method.... " );
			cat.endTimer("XMLOverHttpsHelper.post");
		}

	}

	public YFCDocument get(String URLToPost) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException{
		cat.beginTimer("XMLOverHttpsHelper.get");
		HttpsURLConnection conn = getConnection(URLToPost,OMODLiterals.HTTP_GET_REQUEST,null); 
		if(cat.isDebugEnabled())
			logHandshake(conn); // For Testing

		cat.endTimer("XMLOverHttpsHelper.get");
		return getResponseDocument(conn);
	}

	private YFCDocument getResponseDocument(HttpsURLConnection conn){

//		Create a read stream and receive the response body
		YFCDocument respDoc=null;
		try{
			DataInputStream rstream = null;
			if (conn.getResponseCode() < 400) {
				rstream = new DataInputStream(conn.getInputStream());
			} else {
				if(cat.isDebugEnabled())
					cat.debug("HTTP ERROR ########## : %d %s", conn.getResponseCode(), conn.getResponseMessage()); 
				rstream = new DataInputStream(conn.getErrorStream());
			}
			if(cat.isDebugEnabled())
				cat.debug("HTTP status: %s", conn.getResponseCode()); 
			/*
			 * cat.debug("HTTP status: %d %s", conn.getResponseCode(), conn.getResponseMessage());
			 * cat.debug("Response body...");
			 */
			StringBuilder responseBody = new StringBuilder(conn.getContentLength());
			for (int c = rstream.read(); c > 0; c = rstream.read()) {
				responseBody.append((char)c);
			}
			if(cat.isDebugEnabled())
				cat.debug(responseBody.toString()); 
			rstream.close();

			respDoc=makeDocument(new StringReader(responseBody.toString()));
			if (conn.getResponseCode() < 400) 
				respDoc.getDocumentElement().setAttribute(OMODLiterals.OMOD_IS_HTTP_ERROR, OMODLiterals.YFS_NO);
			else
				respDoc.getDocumentElement().setAttribute(OMODLiterals.OMOD_IS_HTTP_ERROR, OMODLiterals.YFS_YES);

		}catch (IOException e) {
			throw new YFCException("IO EXception");
		}
		return respDoc;

	}

	private HttpsURLConnection getConnection(String URLToPost,String httpRequestMethod,String requestString) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
		checkProperties(URLToPost);
		//setKeystoreProperties();

		URL url = new URL(URLToPost);

		SSLContext context = SSLContext.getInstance("SSL"); 
		KeyManager[] keyManagers = loadKeyStore(KEYSTORE_PATH, KEYSTORE_PW);
		context.init(keyManagers, null, null);

		return getConnection(context,url,requestString,httpRequestMethod);
	}

	private void checkProperties(String requestURL) {

		if (YFCObject.isVoid(requestURL))
			throw new YFCException(OMODErrorCodes.OMOD_INVALID_REMOTE_URL);

		if(YFCObject.isVoid(KEYSTORE_PW))
			throw new YFCException(OMODErrorCodes.KEYSTORE_PASSWORD_NOT_SET);

		if(YFCObject.isVoid(KEYSTORE_PATH))
			throw new YFCException(OMODErrorCodes.KEYSTORE_NOT_SET);

	}

	public  void setProperties(Properties propObj){
		this.REQUEST_URI=propObj.getProperty(OMODLiterals.OMOD_REQUEST_URL);
		this.KEYSTORE_PATH=propObj.getProperty(OMODLiterals.OMOD_KEYSTORE_PATH);
		this.KEYSTORE_PW=propObj.getProperty(OMODLiterals.OMOD_KEYSTORE_PW);
	}

	private String getRequestString(YFCDocument inDoc,MessageContext msgContext) throws IOException {
		String reqString ;
		StringWriter stringOutStream = new StringWriter();
		if (YFCObject.isVoid(inDoc)) {
			cat.debug("Null Input Document Passed to the HttpPost Method.");
			throw new YFCException(OMODErrorCodes.OMOD_NULL_INDOC_TO_HTTPPOST);
		}

		PostConfigurationManager postConfigManager = new PostConfigurationManager(
				msgContext);
		int timeout = msgContext.getTimeout();
		if (timeout != 0)
			postConfigManager.setRequestTimeout(new Integer(timeout));

		OutputFormat outFormat = new OutputFormat(inDoc.getDocument(), msgContext.getEncoding(), true);
		XMLSerializer xmlSerialObj = new XMLSerializer(stringOutStream, outFormat);
		xmlSerialObj.asDOMSerializer();
		xmlSerialObj.serialize(inDoc.getDocument());
		reqString = stringOutStream.toString();

		return reqString ;
	}

	private HttpsURLConnection getConnection(SSLContext context,URL url,String reqString,String requestMethod) {

		HttpsURLConnection conn = null;
		try {
			conn = (HttpsURLConnection)url.openConnection();
			conn.setSSLSocketFactory(context.getSocketFactory());
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.setRequestMethod(requestMethod);
			if(requestMethod.equalsIgnoreCase("POST")){
				conn.setRequestProperty("Content-Type", "application/xml; charset=\"utf-8\"");
				conn.setRequestProperty("Content-Length", String.valueOf(reqString.length()));
			}
			cat.debug("Connecting to %s", REQUEST_URI);

			conn.setHostnameVerifier(new ServerValidationCallback());
			conn.connect();

		} catch (IOException e) {
			YFCException ex=new YFCException("IO Exception");
			throw ex;
		}

		return conn;
	}


	private MessageContext getMessageContext(){

		MessageContext mesgContxt = new MessageContext();
		mesgContxt.setMsgURL(REQUEST_URI);   
		mesgContxt.setContentType("application/xml; charset=\"utf-8\"");
		mesgContxt.setAuthenticationReq(false);

		return mesgContxt;
	}

	private YFCDocument makeDocument(final StringReader responseString) {
		try {
			cat.beginTimer("makeDocument");

			YFCDocument document = YFCDocument.parse(new InputSource(responseString));

			if (YFCObject.isVoid(document) && cat.isDebugEnabled()) {
				cat.debug("Not able to Parse the outPut Document.");
				cat.debug("Response Document" + document);
			}
			return document;
		} catch (SAXParseException e) {
			cat.warn(e, e); 
			return null;
		} catch (IOException e) {
			cat.warn(e, e); 
			return null;
		} catch (Exception e) {
			cat.warn(e, e); 
			return null;
		} finally {
			cat.endTimer("makeDocument");
		}
	}


	static class ServerValidationCallback implements HostnameVerifier{
		public boolean verify(String hostname, SSLSession session)
		{
			// Return boolean true to accept the server certificate
			return true;
		}
	}

	private void logHandshake(HttpsURLConnection conn) throws CertificateEncodingException, SSLPeerUnverifiedException {
		X509Certificate cert[];
		cert = (X509Certificate[])conn.getServerCertificates();
		if (cat.isDebugEnabled()) cat.debug("Server certificate: %s", cert[0].getSubjectX500Principal().toString());
		if (cat.isDebugEnabled()) cat.debug("Issuer: %s", cert[0].getIssuerX500Principal().toString());
		if (cat.isDebugEnabled()) cat.debug("Fingerprint: %s", getFingerprint(cert[0]));
		cert = (X509Certificate[])conn.getLocalCertificates();
		if (cert != null) {
			if (cat.isDebugEnabled()) cat.debug("Client certificate: %s", cert[0].getSubjectX500Principal().toString());
			if (cat.isDebugEnabled()) cat.debug("Issuer: %s", cert[0].getIssuerX500Principal().toString());
			if (cat.isDebugEnabled()) cat.debug("Fingerprint: %s", getFingerprint(cert[0]));
		}
		cat.debug("SSL handshake ok");

	}

	private String getFingerprint (X509Certificate cert) throws CertificateEncodingException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		}
		catch(NoSuchAlgorithmException e) {
			throw new YFCException ("NoSuchAlgorithmException");
		}
		md.update(cert.getEncoded());
		byte fp[] = md.digest();
		StringBuffer fps = new StringBuffer(fp.length * 3 - 1);
		for (int i = 0; i < fp.length; i++) {
			if (i > 0) {
				fps.append(':');
			}
			fps.append(Integer.toHexString(fp[i] & 0xff).toUpperCase());
		}
		return fps.toString();
	}

	private KeyManager[] loadKeyStore(String fileName, String password)
	throws KeyStoreException, FileNotFoundException, IOException,
	NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {

		FileInputStream fis;
		KeyStore keyStore = KeyStore.getInstance("JKS");
		char[] keyStorePassword = password.toCharArray();
		fis = new FileInputStream(fileName);
		keyStore.load(fis, keyStorePassword);
		fis.close();

		String keyManagerAlg = KeyManagerFactory.getDefaultAlgorithm();

		KeyManagerFactory keyManagerFactory = 
			KeyManagerFactory.getInstance(keyManagerAlg);
		keyManagerFactory.init(keyStore, password.toCharArray()); 

		return keyManagerFactory.getKeyManagers();
	}

}
