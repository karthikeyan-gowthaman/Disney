package com.ibm.pca.om.business.payment;

import org.w3c.dom.Document;

import com.ibm.pca.om.business.utils.OMODFoundationBridge;
import com.ibm.shared.omod.OMODLiterals;
import com.ibm.shared.sbcod.remoteAPICall.UserExitRemoteCallHelper;
import com.yantra.shared.dbi.YFS_Order_Header;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.util.YFSUserExistConsts;

public class OMODPaymentUERemoteCallImpl {
	
	private static OMODFoundationBridge bridge = OMODFoundationBridge.getInstance();
	
	public static YFSExtnPaymentCollectionOutputStruct invokeExternalPaymentSystem (
			YFSEnvironment oEnv, YFSExtnPaymentCollectionInputStruct paymentInputStruct, String uePropertyName){
		
		YFSContext oCtx = (YFSContext)oEnv; 	
				
		YFCDocument paymentInputDoc = OMODPaymentUEXMLManager.convertPaymentInputStructToXML(paymentInputStruct);
		String sOrderHeaderKey = paymentInputStruct.orderHeaderKey;

		YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, 
				OMODLiterals.YFS_SELECT_METHOD_WAIT, false);

		paymentInputDoc.getDocumentElement().setAttribute(YFS_Order_Header.ORDER_NO, oOrder.getOrder_No());

		Document respDoc = processUserExitDataToHttpPost(oEnv, paymentInputDoc.getDocument(),uePropertyName);		

		YFCDocument outDoc=YFCDocument.getDocumentFor(respDoc);
		OMODCollectionUEHelper oCollHelper=new OMODCollectionUEHelper();
		return oCollHelper.processCollectionResponse(oCtx, paymentInputStruct, outDoc, oOrder);
		
	}
	
	
	private static Document processUserExitDataToHttpPost(YFSEnvironment env,
			Document doc, String uePropertyName) {
		return UserExitRemoteCallHelper.processUserExitDataToHttpPost(env, doc, uePropertyName);
	}


}
