/*******************************************************************************
 * IBM Confidential
 * OCO Source Materials
 * 5725-F55
 * Copyright IBM Corporation 2011 
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 ******************************************************************************/

package com.ibm.shared.sbcod.httppost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

//import com.sterlingcommerce.shared.sbcod.SBCODErrorCodes;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

public class PostConfigurationManager {

public static final String COPYRIGHT = "Copyright IBM Corporation 2011.";

	private static YFCLogCategory _Log = YFCLogCategory
			.instance(PostConfigurationManager.class.getName());
	public static final String DEFAULT_CONTENT_TYPE = "application/x-icc-xml";
	public static final String DEFAULT_URL = "http://localhost:8080/";
	public static final int DEFAULT_TIMEOUT = 120; // in seconds

	private Integer m_requestTimeout = DEFAULT_TIMEOUT;
	private String m_postURL = DEFAULT_URL;
	private String m_contentType = DEFAULT_CONTENT_TYPE;
	private String m_remoteUserName = null;
	private String m_remotePassword = null;
	private boolean m_isAuthenticationReq = false;

	public PostConfigurationManager() {
	}

	public PostConfigurationManager(MessageContext msgContext) {

		_Log.debug("Inside Constructor PostConfigurationManager");
		m_postURL = msgContext.getMsgURL();
		m_isAuthenticationReq = msgContext.getAuthenticationReq();

		if (_Log.isDebugEnabled()) _Log.debug("URL is: " + m_postURL + " and Authentication:"
				+ m_isAuthenticationReq);
		if (m_isAuthenticationReq) {
			m_remoteUserName = msgContext.getRemoteUserName();
			m_remotePassword = msgContext.getRemotePassword();
		}
	}

	public void setRequestTimeout(Integer requestTimeout) {

		if (_Log.isDebugEnabled()) _Log.debug("Setting Timeout : " + requestTimeout);
		this.m_requestTimeout = requestTimeout;
	}

	public Integer getRequestTimeout() {
		return m_requestTimeout;
	}

	public String getPostURL() {
		return m_postURL;
	}

	public void setPostURL(final String postURL) {
		if (_Log.isDebugEnabled()) _Log.debug("Setting URL : " + postURL);
		m_postURL = postURL;
	}

	public String getContentType() {
		return m_contentType;
	}

	public void setContentType(final String contentType) {
		if (_Log.isDebugEnabled()) _Log.debug("Setting Content Type : " + contentType);
		m_contentType = contentType;
	}

	public Document postString(String message) throws IOException {
		return postString(message, getContentType());
	}

	public Document postString(String message, String contentType)
			throws IOException {
		_Log.beginTimer("postString");
		Document result = null;
		BufferedReader reader = postStringReader(message, contentType);
		if (reader != null) {
			result = makeDocument(reader);
		}
		_Log.endTimer("postString");
		return result;
	}

	public BufferedReader postStringReader(String message, String contentType)
			throws IOException {

		_Log.beginTimer("postStringReader");
		if (_Log.isDebugEnabled()) _Log.debug("Message :" + message + "and contentType:" + contentType);

		StringEntity requestEntity = new StringEntity(message);
		requestEntity.setContentType(contentType);

		_Log.endTimer("postStringReader");
		return postEntityReturnReader(requestEntity);
	}

	private BufferedReader postEntityReturnReader(
			AbstractHttpEntity requestEntity) throws IOException {

		_Log.beginTimer("postEntityReturnReader");

		BufferedReader result = null;
		InputStream inputStream = postEntityReturnStream(requestEntity);
		if (inputStream != null) {
			result = new BufferedReader(new InputStreamReader(inputStream));
		}

		_Log.endTimer("postEntityReturnReader");
		return result;
	}

	private InputStream postEntityReturnStream(
			final AbstractHttpEntity requestEntity) throws IOException {
		 final int codevalue=404;
		_Log.beginTimer("postEntityReturnStream");
		InputStream inputStream = null;
		try {
			HttpClient httpClient = makeHttpClient();
			HttpPost httpPost = makeHttpPost(requestEntity);

			HttpResponse response = httpClient.execute(httpPost);
			final int code = response.getStatusLine().getStatusCode();

			if (_Log.isDebugEnabled()) _Log.debug("Response Code Received:" + code);
			if(code==codevalue){
				//YFCException ex = new YFCException(SBCODErrorCodes.SBCOD_PAGE_NOT_FOUND);
				//throw ex;
			}

		if (code != 0) {//HttpStatus.SC_OK) {
				_Log.warn("Response status: " + code);

			} else {
				HttpEntity responseEntity = response.getEntity();
				if (_Log.isDebugEnabled()) {
					_Log.debug("Response length: "
							+ responseEntity.getContentLength());
					if (!YFCObject.isVoid(responseEntity.getContentType()))
						_Log.debug("Response type: "
								+ responseEntity.getContentType().getValue());

				}
				inputStream = responseEntity.getContent();

			}
		}
		catch (UnknownHostException ex) {
			_Log.debug("got UnknownHostException ");
			//YFCException exp = new YFCException(ex, SBCODErrorCodes.SBCOD_UNKNOWN_HOST_EXCEPTION);
			//throw exp;
		}
		catch (HttpHostConnectException ex) {
			_Log.debug("got HttpHostConnectException ");
			//YFCException exp = new YFCException(ex, SBCODErrorCodes.SBCOD_HOST_CONNECTION_EXCEPTION);
			//throw exp;
		} catch(ConnectException ex){
			_Log.debug("got UnknownHostException ");
			//YFCException exp = new YFCException(ex, SBCODErrorCodes.SBCOD_CONNECT_EXCEPTION);
			//throw exp;
		} catch(ConnectTimeoutException ex) {
			_Log.debug("got Connect Timeout ");
			//YFCException exp = new YFCException(ex, SBCODErrorCodes.SBCOD_ERROR_TIMEOUT);
			//throw exp;
		} finally {
			_Log.endTimer("postEntityReturnStream");
		}
		return inputStream;
	}

	private HttpPost makeHttpPost(final HttpEntity requestEntity)
			throws IOException {
		_Log.beginTimer("makeHttpPost");

		HttpPost httpPost = new HttpPost(m_postURL);
		
		if (m_isAuthenticationReq) {
			httpPost.setHeader("remoteUsername", m_remoteUserName);
			httpPost.setHeader("remotePassword", m_remotePassword);
		}
		httpPost.setEntity(requestEntity);

		_Log.endTimer("makeHttpPost");
		return httpPost;
	}

	private HttpClient makeHttpClient() {
		_Log.beginTimer("makeHttpClient");
		HttpClient httpClient = new DefaultHttpClient();
		final HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params,
				getRequestTimeoutMilliseconds());
		HttpConnectionParams.setSoTimeout(params,
				getRequestTimeoutMilliseconds());
		_Log.endTimer("makeHttpClient");
		return httpClient;
	}

	private int getRequestTimeoutMilliseconds() {
		_Log.beginTimer("getRequestTimeoutMilliseconds");
		Integer timeout = getRequestTimeout();
		if (timeout == null) {
			timeout = 0;
		}
		int timeoutMS = 0;
		if (timeout != 0) {
			timeoutMS = timeout * 1000;
		}
		if (_Log.isDebugEnabled()) {
			_Log.debug("getRequestTimeoutMilliseconds: " + timeoutMS);
		}
		_Log.endTimer("getRequestTimeoutMilliseconds");
		return timeoutMS;
	}

	private Document makeDocument(final BufferedReader reader) {
		try {
			_Log.beginTimer("makeDocument");
			final DocumentBuilder documentBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(new InputSource(reader));

			if (YFCObject.isVoid(document)) {
				_Log.debug("Not able to Parse the outPut Document.");
				if (_Log.isDebugEnabled()) _Log.debug("Response Document" + document);
			}
			return document;
		} catch (Exception e) {
			_Log.warn(e, e);
			return null;
		} finally {
			_Log.endTimer("makeDocument");
		}
	}

}
