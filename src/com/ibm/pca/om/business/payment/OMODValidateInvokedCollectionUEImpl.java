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
import com.ibm.shared.omod.OMODLiterals;
import com.yantra.shared.dbi.YFS_Order_Header;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSValidateInvokedCollectionUE;

public class OMODValidateInvokedCollectionUEImpl implements YFSValidateInvokedCollectionUE{

public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";


	public static YFCLogCategory cat = YFCLogCategory.instance(OMODValidateInvokedCollectionUEImpl.class);
	private static OMODFoundationBridge bridge = OMODFoundationBridge.getInstance();
	
	public YFSExtnPaymentCollectionOutputStruct validateInvokedCollection(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct paymentInputStruct) throws YFSUserExitException {
		
		YFSContext oCtx = (YFSContext)env; 		    
		YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = new YFSExtnPaymentCollectionOutputStruct();

		try	{
			
			YFCDocument paymentInputDoc = OMODPaymentUEXMLManager.convertPaymentInputStructToXML(paymentInputStruct);
			
			String sOrderHeaderKey = paymentInputDoc.getDocumentElement().getAttribute(YFS_Order_Header.ORDER_HEADER_KEY);
			YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, 
					OMODLiterals.YFS_SELECT_METHOD_WAIT, false);

			paymentInputDoc.getDocumentElement().setAttribute(YFS_Order_Header.ORDER_NO, oOrder.getOrder_No());

			YFCDocument outDoc = executeCreditCardService(oCtx,OMODLiterals.PAYMENT_VERIFICATION_SERVICE, paymentInputDoc);	
			
			YFCElement collectionOutputElem=outDoc.getDocumentElement().getChildElement("Payment");
			
			if (cat.isDebugEnabled()) cat.debug("Collection Output XML = " + collectionOutputElem.getString());	
			
			OMODCollectionUEHelper oCollHelper=new OMODCollectionUEHelper();
			paymentOutputStruct=oCollHelper.processCollectionResponse(oCtx, paymentInputStruct, collectionOutputElem, oOrder, "");			
			
		
		}catch	(YFCException e){
			throw e;            
		} catch	(Exception e){
			YFSUserExitException ex = new YFSUserExitException(e.getMessage());
			throw ex;            
		} finally	{
			cat.endTimer("OMODValidateInvokedCollectionUEImpl");	    
			cat.debug("Exiting OMODValidateInvokedCollectionUEImpl");
		}
		
		return paymentOutputStruct;
	}
	


	private YFCDocument executeCreditCardService(YFSContext oCtx, String sServiceName, YFCDocument serviceInputDoc){
		return bridge.executeService(oCtx, sServiceName, serviceInputDoc);
	}

}
