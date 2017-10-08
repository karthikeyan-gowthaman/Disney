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

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.SocketTimeoutException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

//import com.sterlingcommerce.shared.sbcod.SBCODErrorCodes;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import org.apache.http.conn.HttpHostConnectException;

public class XMLOverHttpClient {

public static final String COPYRIGHT = "Copyright IBM Corporation 2011.";


	private static YFCLogCategory _Log = YFCLogCategory.instance(XMLOverHttpClient.class.getName());

	public Document post(Document inDoc, MessageContext msgContext) {
		_Log.beginTimer("post");

		Document responseDoc = postHttpRequest(inDoc, msgContext);
		if (YFCObject.isVoid(responseDoc)) {
			_Log.debug("Response Document Is Null");
			//throw new YFCException(SBCODErrorCodes.SBCOD_NULL_RESPONSE_DOCUMENT);
		}

		_Log.endTimer("post");

		try {
			YFCDocument.getDocumentFor(responseDoc);
		} catch (Exception e) {
			_Log.debug("Response Document Is Not Well Formed.");
			//throw new YFCException(SBCODErrorCodes.SBCOD_XML_NOT_WELLFORMED_DOCUMENT);
		}

		return responseDoc;
	}

	/**
	 * Method process the incoming request and post the Request to the remote
	 * URL with other parameters. Response from the remote URL is processed and
	 * return in form of document.
	 * 
	 * @param inDoc
	 * @param msgContext
	 * @return
	 */
	protected Document postHttpRequest(Document inDoc, MessageContext msgContext) {

		_Log.beginTimer("postHttpRequest");

		Document respDoc = null;
		StringWriter stringOutStream = new StringWriter();
		String reqString = new String();
		if (YFCObject.isVoid(inDoc)) {
			_Log.debug("Null Input Document Passed to the HttpPost Method.");
			//throw new YFCException(SBCODErrorCodes.SBCOD_NULL_INDOC_TO_HTTPPOST);
		}
		try {
			PostConfigurationManager postConfigManager = new PostConfigurationManager(
					msgContext);
			int timeout = msgContext.getTimeout();
			if (timeout != 0)
				postConfigManager.setRequestTimeout(new Integer(timeout));
			// Serialize the inDoc
			_Log.debug("***Document To Post***");
			if (_Log.isDebugEnabled()) _Log.debug(YFCDocument.getDocumentFor(inDoc).getDocumentElement()
					.toString());

			String contentType = msgContext.getContentType();
			OutputFormat outFormat = new OutputFormat(inDoc,
					msgContext.getEncoding(), true);
			XMLSerializer xmlSerialObj = new XMLSerializer(stringOutStream,
					outFormat);
			xmlSerialObj.asDOMSerializer();
			xmlSerialObj.serialize(inDoc);
			reqString = stringOutStream.toString();
			if ((contentType == null) || (contentType.length() == 0)) {
				_Log.debug("Posting with default Content Type set.");
				respDoc = postConfigManager.postString(reqString);
			} else {
				if (_Log.isDebugEnabled()) _Log.debug("Posting with Content Type set." + contentType);
				respDoc = postConfigManager.postString(reqString, contentType);
			}
		} catch (SocketTimeoutException e) {
			_Log.error(
					"Time Out Exception!!!No Response Received from Remote JVM in Simulated Time.",
					e);
			//throw new YFCException(e, SBCODErrorCodes.SBCOD_ERROR_TIMEOUT);

		} catch (FileNotFoundException e) {
			_Log.error(
					"Exception in Serializing the input Document in Http Post.",
					e);
		}

		catch (YFCException e) {
			_Log.error("caught YFCException : For HttpPostConnectException");
			throw e;
		}

		catch (Exception e) {
			_Log.error("Posting message to " + msgContext.getMsgURL()
					+ " failed", e);
		}

		if (_Log.isDebugEnabled()) {
			if (_Log.isDebugEnabled())
				_Log.debug("Response message received:");
		}

		_Log.debug("***Response Document Of Http Post**");
		// _Log.debug(YFCDocument.getDocumentFor(respDoc).getDocumentElement().toString());
		_Log.endTimer("postHttpRequest");

		return respDoc;
	}

}
