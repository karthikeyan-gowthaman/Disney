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

public class MessageContext {

public static final String COPYRIGHT = "Copyright IBM Corporation 2011.";

	public static final String DEFAULT_ENCODING = "UTF-8";
	private String encoding;
	private String contentType;
	private String msgURL;
	private String remoteUserName;
	private String remotePassword;
	private int timeout;
	private boolean isAuthenticationReq;

	public MessageContext() {
		super();
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getMsgURL() {
		return msgURL;
	}

	public void setMsgURL(String msgURL) {
		this.msgURL = msgURL;
	}

	public String getRemotePassword() {
		return remotePassword;
	}

	public void setRemotePassword(String remotePassword) {
		this.remotePassword = remotePassword;
	}

	public String getRemoteUserName() {
		return remoteUserName;
	}

	public void setRemoteUserName(String remoteUserName) {
		this.remoteUserName = remoteUserName;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean getAuthenticationReq() {
		return isAuthenticationReq;
	}

	public void setAuthenticationReq(boolean isAuthenticationReq) {
		this.isAuthenticationReq = isAuthenticationReq;
	}

}
