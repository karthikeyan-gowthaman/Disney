/*******************************************************************************
 * IBM Confidential
 *   OCO Source Materials
 *   5725-G69
 *   Copyright IBM Corporation 2011,2012
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 ******************************************************************************/
package com.ibm.pca.om.business.payment;

import com.ibm.pca.om.business.utils.OMODFoundationBridge;
import com.ibm.pca.sbc.business.utils.SBCApiUtils;
import com.ibm.rds.stub.RDSCreditCardStub;
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
import com.yantra.yfs.japi.ue.YFSCollectionCreditCardUE;

public class OMODCollectionCreditCardUEImpl implements YFSCollectionCreditCardUE {

	public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";
	
	public static YFCLogCategory cat = YFCLogCategory.instance(OMODCollectionCreditCardUEImpl.class);
	private static OMODFoundationBridge bridge = OMODFoundationBridge.getInstance();

	public YFSExtnPaymentCollectionOutputStruct collectionCreditCard(
			YFSEnvironment oEnv, YFSExtnPaymentCollectionInputStruct paymentInputStruct)
	throws YFSUserExitException {

		cat.debug("In collectionCreditCard :: OMODCollectionCreditCardUEImpl");		    
		YFSContext oCtx = (YFSContext)oEnv; 		    
		YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = new YFSExtnPaymentCollectionOutputStruct();

		try	{

			YFCDocument paymentInputDoc = OMODPaymentUEXMLManager.convertPaymentInputStructToXML(paymentInputStruct);
			cat.debug("paymentInputDoc = "+ paymentInputDoc.toString());
			
			String sOrderHeaderKey = paymentInputStruct.orderHeaderKey;

			YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, 
					OMODLiterals.YFS_SELECT_METHOD_WAIT, false);

			paymentInputDoc.getDocumentElement().setAttribute(YFS_Order_Header.ORDER_NO, oOrder.getOrder_No());

			//Check for the stub mode
			YFCDocument outDoc = null;
			String isStubmode = SBCApiUtils.getCommonCodeValue(oEnv, "STUB_MODE");
			// Included for RDS3.0 - to apply hold to auth failed order. Hold type is configurable as its fetched from Service argument.
			String holdType = SBCApiUtils.getCommonCodeValue(oEnv, "AUTH_HOLD_TYPE");
			if("Y".equalsIgnoreCase(isStubmode)){
				outDoc = RDSCreditCardStub.processPaymentRequest(oCtx, paymentInputDoc,holdType);
			}else{
				 outDoc = executeCreditCardService(oCtx,OMODLiterals.PAYMENT_EXECUTION_SERVICE, paymentInputDoc);
			}

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
			cat.endTimer("OMODCollectionCreditCardUEImpl");	    
			cat.debug("Exiting OMODCollectionCreditCardUEImpl");
		}
		cat.debug("Out collectionCreditCard :: OMODCollectionCreditCardUEImpl");
		return paymentOutputStruct;
	}

	private YFCDocument executeCreditCardService(YFSContext oCtx, String sServiceName, YFCDocument serviceInputDoc) {
		return bridge.executeService(oCtx, sServiceName, serviceInputDoc);
	}	

}
