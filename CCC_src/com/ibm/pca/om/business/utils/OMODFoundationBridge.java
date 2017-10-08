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

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.ibm.shared.omod.OMODLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.shared.dbi.YFS_Order_Header;
import com.yantra.shared.omp.OMPFactory;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.ycp.core.YCPTemplateManager;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSEventManager;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class OMODFoundationBridge {

public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";

	private static OMODFoundationBridge _instance = new OMODFoundationBridge();

	public static YFSContext  getContext(YFSEnvironment env) {
		if ( env instanceof YFSContext )
			return (YFSContext)env;
		else
			return null;
	}
	
	public static OMODFoundationBridge getInstance() {
		return _instance;
	}

	public YFCDocument invokeAPI(YFSEnvironment env, String sApiName, YFCDocument inDoc) {
		try {
			YIFApi oApi = YIFClientFactory.getInstance().getApi();
			Document outDoc = oApi.invoke(env, sApiName, inDoc.getDocument());
			return(YFCObject.isNull(outDoc))?null:YFCDocument.getDocumentFor(outDoc);
		}
		catch(YFSException yfsEx) {
			throw new YFCException(yfsEx, yfsEx.getErrorCode());
		}
		catch(YFCException yfcEx) {
			throw yfcEx;
		}
		catch(Exception ex) {
			throw new YFCException(ex);
		}
	}


	public YFCDocument executeService(YFSContext env, String sServiceName, YFCDocument inDoc) throws YFCException	
	{
		try {
			YIFApi oApi = YIFClientFactory.getInstance().getLocalApi();
			Document outDoc = oApi.executeFlow(env,sServiceName,inDoc.getDocument());
			YFCDocument out=null;
			/* not every API/flow returns a document, handle null here */
			if (outDoc!=null) out=YFCDocument.getDocumentFor(outDoc);
			return out;
		} catch (YFSException yfsEx) {
			throw new YFCException(yfsEx, yfsEx.getErrorCode());
		} catch (YFCException yfcEx) {
			throw yfcEx;
		} catch (Exception ex) {
			throw new YFCException(ex);
		} 
	}


	public YFCDocument getEventTemplate(YFSContext env, String tranID, String eventName,String sEnterpriseCode,String sDocumentType) {
		HashMap bufParams = new HashMap();
		if(YFCCommon.isVoid(sEnterpriseCode) && YFCCommon.isVoid(sDocumentType)){
			bufParams = null;
		}else{
			if(!YFCCommon.isVoid(sEnterpriseCode)){
				bufParams.put(OMODLiterals.ENTERPRISE_CODE,sEnterpriseCode);}
			if(!YFCCommon.isVoid(sDocumentType)){
				bufParams.put(OMODLiterals.DOCUMENT_TYPE, sDocumentType);}
		}
		return YCPTemplateManager.getInstance().getEventTemplate(env,tranID,eventName,bufParams);
	}

	public boolean isRaiseEventRequired(YFSContext env, String tranName, String eventName) {
		YFSEventManager eventMgr = new YFSEventManager();
		return eventMgr.isRaiseEventReqd(env,tranName,eventName);
	}

	public void raiseEvent(YFSContext env, Map keyMap, YFCDocument doc, String tranName, String eventID) {
		YFSEventManager eventMgr = new YFSEventManager();
		eventMgr.raiseEvent(env,keyMap,doc,tranName,eventID,"");
	}

	public YFS_Order_Header getOrderHeader(YFSContext env, YFCElement inXML, String selectMethod)	{
		if(inXML == null)	return null;

		return getOrderHeader(env, inXML.getAttribute("OrderHeaderKey"),
				inXML.getAttribute("EnterpriseCode"), inXML.getAttribute("OrderNo"), inXML.getAttribute("DocumentType"),
				selectMethod, !inXML.getBooleanAttribute("IgnorePendingChanges", false) );

	}

	/**
	 * @param env The context with which this is being called
	 * @param orderHeaderKey The order header key to find
	 * @param enterpriseCode The enterprise code associated with this order
	 * @param orderNo The order number
	 * @param docType The document type
	 * @param selectMethod determines whether or not this order should be locked
	 * @return The order header object
	 */
	public YFS_Order_Header getOrderHeader(YFSContext env, String orderHeaderKey, String enterpriseCode, String orderNo, String docType, String selectMethod, boolean ignorePendingChanges) {
		return OMPFactory.getInstance().getOrderHeader(env, orderHeaderKey, enterpriseCode, orderNo, docType, selectMethod, ignorePendingChanges);
	}

	public YFCDocument getOrderDetails(YFSContext ctx, YFS_Order_Header orderHeader, YFCDocument template) {
		String orderHeaderKey = orderHeader.getOrder_Header_Key();

		YFCDocument getOrderDetailsInput = YFCDocument.createDocument("Order");
		getOrderDetailsInput.getDocumentElement().setAttribute("OrderHeaderKey",orderHeaderKey);
		return getOrderDetails(ctx,getOrderDetailsInput,template);
	}

	public YFCDocument getOrderDetails(YFSContext ctx, YFCDocument inXMLDoc, YFCDocument dTemplate) {
		try {
			if(dTemplate != null)	{
				ctx.setApiTemplate("getOrderDetails", dTemplate.getDocument() );
			}
			YIFApi oApi = YIFClientFactory.getInstance().getApi();
			YFCDocument outDoc = YFCDocument.getDocumentFor(oApi.getOrderDetails(ctx, inXMLDoc.getDocument()));
			return outDoc;
		} catch (YFSException yfsEx) {
			throw new YFCException(yfsEx, yfsEx.getErrorCode());
		} catch (YFCException yfcEx) {
			throw yfcEx;
		} catch (Exception ex) {
			throw new YFCException(ex);
		} 
	}
}
