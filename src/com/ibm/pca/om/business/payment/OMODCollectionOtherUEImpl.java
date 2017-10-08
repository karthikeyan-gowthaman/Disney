package com.ibm.pca.om.business.payment;

import com.ibm.pca.om.business.utils.OMODFoundationBridge;
import com.ibm.shared.omod.OMODLiterals;
import com.yantra.shared.dbi.YFS_Order_Header;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionOthersUE;

public class OMODCollectionOtherUEImpl implements YFSCollectionOthersUE {
	
	public static YFCLogCategory cat = YFCLogCategory.instance(OMODCollectionOtherUEImpl.class);
	private static OMODFoundationBridge bridge = OMODFoundationBridge.getInstance();

	/**
	 * @Override
	 * 
	 * 
	 * @param env
	 * @param paymentInputStruct
	 * @return YFSExtnPaymentCollectionOutputStruct
	 */
	public YFSExtnPaymentCollectionOutputStruct collectionOthers(
			YFSEnvironment env, YFSExtnPaymentCollectionInputStruct paymentInputStruct)
			throws YFSUserExitException {

		cat.beginTimer("OMODCollectionOtherUEImpl");		    
		YFSContext oCtx = (YFSContext)env; 		    
		YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = new YFSExtnPaymentCollectionOutputStruct();

		try	{

			YFCDocument paymentInputDoc = OMODPaymentUEXMLManager.convertPaymentInputStructToXML(paymentInputStruct);
			String sOrderHeaderKey = paymentInputStruct.orderHeaderKey;
			
			if (cat.isDebugEnabled()) cat.debug("OrderHeaderKey : "+sOrderHeaderKey);
			
			if (cat.isDebugEnabled()) cat.debug("Payment Input Doc : "+paymentInputDoc);

			YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, 
					OMODLiterals.YFS_SELECT_METHOD_WAIT, false);

			paymentInputDoc.getDocumentElement().setAttribute(YFS_Order_Header.ORDER_NO, oOrder.getOrder_No());
			
			if (cat.isDebugEnabled()) cat.debug("Invoking Service : "+OMODLiterals.PAYPAL_COLLECTION_SERVICE);

			YFCDocument outDoc = executeCreditCardService(oCtx,OMODLiterals.PAYPAL_COLLECTION_SERVICE, paymentInputDoc);	

			OMODCollectionUEHelper oCollHelper=new OMODCollectionUEHelper();
			if(!YFCObject.isVoid(outDoc)){
				paymentOutputStruct=oCollHelper.processCollectionResponse(oCtx, paymentInputStruct, outDoc, oOrder);
			}

		} catch	(YFCException e){
			throw e;            
		} catch	(Exception e){
			YFSUserExitException ex = new YFSUserExitException(e.getMessage());
			throw ex;            
		} finally	{
			cat.endTimer("OMODCollectionOtherUEImpl");	    
			cat.debug("Exiting OMODCollectionOtherUEImpl");
		}
		return paymentOutputStruct;
	}
	
	/**
	 * 
	 * @param oCtx
	 * @param sServiceName
	 * @param serviceInputDoc
	 * @return
	 */
	private YFCDocument executeCreditCardService(YFSContext oCtx, String sServiceName, YFCDocument serviceInputDoc) {
		return bridge.executeService(oCtx, sServiceName, serviceInputDoc);
	}	

}
