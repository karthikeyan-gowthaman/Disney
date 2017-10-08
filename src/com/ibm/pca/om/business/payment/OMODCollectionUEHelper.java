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

import java.util.HashMap;
import java.util.Iterator;

import com.ibm.pca.om.business.payment.OMODPaymentUEXMLManager;
import com.ibm.pca.om.business.utils.OMODFoundationBridge;
import com.ibm.pca.sbc.business.utils.SBCApiUtils;
import com.ibm.shared.omod.OMODErrorCodes;
import com.ibm.shared.omod.OMODLiterals;
import com.yantra.shared.dbi.YFS_Order_Header;
import com.yantra.shared.dbi.YFS_Payment;
import com.yantra.shared.ycp.YCPConstants;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.util.YFCUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.date.YDate;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCDataBuf;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfc.util.YFCDateUtils;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;

public class OMODCollectionUEHelper {

	public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";
	
	private static HashMap<String,String> authReturnMsgHashMap = null;
	
	static {
		authReturnMsgHashMap = new HashMap<String,String>();
		authReturnMsgHashMap.put(OMODLiterals.OMOD_PG_ERROR_0, "OMOD_PG_ERROR_0");
		authReturnMsgHashMap.put("OMOD_PG_ERROR_10001", OMODLiterals.OMOD_PG_ERROR_10001);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_22014", OMODLiterals.OMOD_PG_ERROR_22014);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_30005", OMODLiterals.OMOD_PG_ERROR_30005);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_30002", OMODLiterals.OMOD_PG_ERROR_30002);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_30001", OMODLiterals.OMOD_PG_ERROR_30001);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_40001", OMODLiterals.OMOD_PG_ERROR_40001);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_40002", OMODLiterals.OMOD_PG_ERROR_40002);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_40003", OMODLiterals.OMOD_PG_ERROR_40003);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_40005", OMODLiterals.OMOD_PG_ERROR_40005);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_40006", OMODLiterals.OMOD_PG_ERROR_40006);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_40010", OMODLiterals.OMOD_PG_ERROR_40010);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_90001", OMODLiterals.OMOD_PG_ERROR_90001);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_90002", OMODLiterals.OMOD_PG_ERROR_90002);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20001", OMODLiterals.OMOD_PG_ERROR_20001);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20002", OMODLiterals.OMOD_PG_ERROR_20002);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20003", OMODLiterals.OMOD_PG_ERROR_20003);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20004", OMODLiterals.OMOD_PG_ERROR_20004);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20005", OMODLiterals.OMOD_PG_ERROR_20005);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20006", OMODLiterals.OMOD_PG_ERROR_20006);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20007", OMODLiterals.OMOD_PG_ERROR_20007);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20008", OMODLiterals.OMOD_PG_ERROR_20008);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20009", OMODLiterals.OMOD_PG_ERROR_20009);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20012", OMODLiterals.OMOD_PG_ERROR_20012);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20013", OMODLiterals.OMOD_PG_ERROR_20013);		
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20014", OMODLiterals.OMOD_PG_ERROR_20014);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20015", OMODLiterals.OMOD_PG_ERROR_20015);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20016", OMODLiterals.OMOD_PG_ERROR_20016);
		authReturnMsgHashMap.put("OMOD_PG_ERROR_20017", OMODLiterals.OMOD_PG_ERROR_20017);		
	}

	public static YFCLogCategory cat = YFCLogCategory.instance(OMODCollectionUEHelper.class);
	private static OMODFoundationBridge bridge = OMODFoundationBridge.getInstance();

	public YFSExtnPaymentCollectionOutputStruct processCollectionResponse(
			YFSContext oCtx, YFSExtnPaymentCollectionInputStruct paymentInputStruct,YFCElement collectionOutputElem,YFS_Order_Header oOrder,String sCollectionRespCode){
		cat.debug("In processCollectionResponse :: OMODCollectionUEHelper");
		if (cat.isDebugEnabled())
			cat.debug("collectionOutput = "+collectionOutputElem.toString());
		YFCDocument eventDoc=null ;
		String sChargeType=paymentInputStruct.chargeType;
		String sRetryFlag=collectionOutputElem.getAttribute("RetryFlag");
		String sSuspendPayment=collectionOutputElem.getAttribute("SuspendPayment");
		String paymentType = collectionOutputElem.getAttribute(OMODLiterals.OMOD_PAYMENT_TYPE);
		boolean sHoldOrderAndRaiseEvent = collectionOutputElem.getBooleanAttribute("holdOrderAndRaiseEvent");
		String sHoldReason = collectionOutputElem.getAttribute("holdReason");
		
		YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = OMODPaymentUEXMLManager.constructBasicCollectionPaymentOutput(collectionOutputElem);
		
		if(!YFCObject.isVoid(authReturnMsgHashMap)) {
			cat.debug("authReturnMsgHashMap size : "+authReturnMsgHashMap.size());
			cat.debug("Internal Return Message : "+paymentOutputStruct.internalReturnMessage);
		}
		
		if(!YFCObject.isVoid(paymentOutputStruct.internalReturnMessage) && !YFCObject.isVoid(authReturnMsgHashMap) && authReturnMsgHashMap.containsKey(paymentOutputStruct.internalReturnMessage)) {
			paymentOutputStruct.internalReturnMessage = authReturnMsgHashMap.get(paymentOutputStruct.internalReturnMessage);
		}

		if (YFCObject.equals(sCollectionRespCode, OMODLiterals.OMOD_COLL_RESPONSE_DECLINE)){
			paymentOutputStruct.authorizationAmount = 0;
			//TODO added newly for RDS3.0
			paymentOutputStruct.holdOrderAndRaiseEvent = sHoldOrderAndRaiseEvent;
			paymentOutputStruct.holdReason = sHoldReason;
			if(!YFCObject.isVoid(sRetryFlag)){
				paymentOutputStruct.retryFlag = sRetryFlag;
			}else{
				paymentOutputStruct.retryFlag = YCPConstants.YFS_NO;	
			}

			if(!YFCObject.isVoid(sSuspendPayment)){
				paymentOutputStruct.suspendPayment = sSuspendPayment;
			}else{
				paymentOutputStruct.suspendPayment = YCPConstants.YFS_YES;	
			}
						
			/**
			 * Use the below condition to cancel the order 
			 * if PayPal is Authorized twice
			 */
			if( !(paymentOutputStruct.internalReturnMessage).contains("OMOD_PG_ERROR_")) {
				YTimestamp currentAuthExpTS = null;
				if(!YFCCommon.isVoid(paymentInputStruct.currentAuthorizationExpirationDate) && !YFCCommon.isStringVoid(paymentInputStruct.currentAuthorizationExpirationDate.toString()))
					currentAuthExpTS = new YTimestamp(paymentInputStruct.currentAuthorizationExpirationDate.getTime(), false); //Review with Hari
				
				YTimestamp computedAuthExpTS= computeAuthExpirationDate(oCtx,oOrder.getOrder_Date()); //Review with Hari
				
				//Cancel the Order
				if(currentAuthExpTS != null && YFCDateUtils.EQ(currentAuthExpTS.getYDate(), computedAuthExpTS.getYDate(), true)) { //Review with Hari
					
					cat.debug("Cancelling the Order as Authorization is expired");
					
					SBCApiUtils.invokeAPI((YFSEnvironment)oCtx, "changeOrder", getInputDocumentForCancelOrder(oOrder.getOrder_Header_Key()), false);
					
					paymentOutputStruct.internalReturnMessage = OMODLiterals.OMOD_PAYPAL_ORDER_CANCEL_MSG_BUNDLE_KEY;
					
				
				} else {
					
					paymentOutputStruct.authorizationAmount = collectionOutputElem.getDoubleAttribute("AuthorizationAmount");
					//TODO added newly for RDS3.0
					YDate ydate=YDate.newDate(YDate.newDate() , 7);
					ydate.setEndOfDay();
					paymentOutputStruct.authorizationExpirationDate = ydate.getString(YFCUtils.APP_FORMAT); 
							//computedAuthExpTS.getDateString(oCtx.getYFCLocale());//.getDateTimeString(oCtx.getYFCLocale()); //Review with Hari
					
					if(!YFCObject.isVoid(sRetryFlag)){
						paymentOutputStruct.retryFlag = sRetryFlag;
					}else{
						paymentOutputStruct.retryFlag = YCPConstants.YFS_NO;	
					}

					if(!YFCObject.isVoid(sSuspendPayment)){
						paymentOutputStruct.suspendPayment = sSuspendPayment;
					}else{
						paymentOutputStruct.suspendPayment = YCPConstants.YFS_NO;	
					}

					paymentOutputStruct.tranAmount =Double.parseDouble(collectionOutputElem.getAttribute("TranAmount")); 
					
				}
				
			}

			eventDoc = prepareEventDoc(oCtx, oOrder,OMODLiterals.PAYMENT_EXECUTION_TRANID);

			cat.debug(" ######### Raising alert for payment failure ");
			//raiseEvent(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, OMODLiterals.PAYMENT_EXECUTION_TRANID);
			
			
			raiseAlert(oCtx, paymentInputStruct, paymentOutputStruct, eventDoc, collectionOutputElem);

		} else if (YFCObject.equals(sCollectionRespCode, OMODLiterals.OMOD_COLL_RESPONSE_SRVC_UNAVL)){

			paymentOutputStruct.authorizationAmount = 0;
			if(!YFCObject.isVoid(sRetryFlag)){
				paymentOutputStruct.retryFlag = sRetryFlag;
			}else{
				paymentOutputStruct.retryFlag = YCPConstants.YFS_YES;	
			}

			if(!YFCObject.isVoid(sSuspendPayment)){
				paymentOutputStruct.suspendPayment = sSuspendPayment;
			}else{
				paymentOutputStruct.suspendPayment = YCPConstants.YFS_NO;	
			}

			eventDoc = prepareEventDoc(oCtx, oOrder, OMODLiterals.PAYMENT_EXECUTION_TRANID);
			cat.debug(" ######### Raising alert for payment failure ");
			//raiseEvent(oCtx, oOrder, paymentInputStruct, paymentOutputStruct, eventDoc, OMODLiterals.PAYMENT_EXECUTION_TRANID);
			raiseAlert(oCtx, paymentInputStruct, paymentOutputStruct, eventDoc, collectionOutputElem);

		} else if (YFCObject.equals(sCollectionRespCode, OMODLiterals.OMOD_COLL_RESPONSE_APPROVED)){

			paymentOutputStruct.authorizationAmount = collectionOutputElem.getDoubleAttribute("AuthorizationAmount");
			if(sChargeType.equalsIgnoreCase("AUTHORIZATION")){
				paymentOutputStruct.authorizationExpirationDate = collectionOutputElem.getAttribute("AuthorizationExpirationDate");
				paymentOutputStruct.authTime = collectionOutputElem.getAttribute("AuthTime");
				paymentOutputStruct.authCode=collectionOutputElem.getAttribute("AuthCode");
				paymentOutputStruct.authorizationId=collectionOutputElem.getAttribute("AuthCode");

				if(YFCObject.equals(paymentType, OMODLiterals.OMOD_CREDIT_CARD_PAYMENT_GRP)) {
					String sCreditCardNo=collectionOutputElem.getAttribute("CreditCardNo");
					stampNewTransactionNumber(oOrder,sCreditCardNo);
				} else if(YFCObject.equals(paymentType, OMODLiterals.OMOD_PAYPAL_PAYMENT_TYPE) 
						||  YFCObject.equals(paymentType, OMODLiterals.OMOD_GIFTCARD_PAYMENT_TYPE)) {
					String paymentReferenceNo=collectionOutputElem.getAttribute("PaymentReference1");
					stampNewTransactionNumber(oOrder,paymentReferenceNo);
				}
			}
			if(!YFCObject.isVoid(sRetryFlag)){
				paymentOutputStruct.retryFlag = sRetryFlag;
			}else{
				paymentOutputStruct.retryFlag = YCPConstants.YFS_NO;
			}

			if(!YFCObject.isVoid(sSuspendPayment)){
				paymentOutputStruct.suspendPayment = sSuspendPayment;
			}else{
				paymentOutputStruct.suspendPayment = YCPConstants.YFS_NO;	
			}

			paymentOutputStruct.tranAmount =Double.parseDouble(collectionOutputElem.getAttribute("TranAmount")); 
			//paymentOutputStruct.sCVVAuthCode = collectionOutputElem.getAttribute("SCVVAuthCode");

		}
		cat.debug("Out processCollectionResponse :: OMODCollectionUEHelper");
		return paymentOutputStruct;

	}

	private void stampNewTransactionNumber(YFS_Order_Header order, String transactionNo) {

		for(Iterator<YFS_Payment> iter=order.getPaymentMethodList().iterator() ; iter.hasNext();){
			YFS_Payment oPayment=iter.next();
			if(oPayment.getPaymentType().getPayment_Type_Group().equalsIgnoreCase(OMODLiterals.OMOD_CREDIT_CARD_PAYMENT_GRP)){
				oPayment.setCredit_Card_No(transactionNo);
				oPayment.updateNoCheck();
			} else if(oPayment.getPaymentType().getPayment_Type_Group().equalsIgnoreCase(OMODLiterals.OMOD_PAYPAL_PAYMENT_TYPE)
					|| oPayment.getPaymentType().getPayment_Type_Group().equalsIgnoreCase(OMODLiterals.OMOD_GIFTCARD_PAYMENT_TYPE)){
				oPayment.setPayment_Reference1(transactionNo);
				oPayment.updateNoCheck();
			}

		}

	}

	private void raiseEvent(YFSContext oCtx, YFS_Order_Header oOrder, YFSExtnPaymentCollectionInputStruct paymentInputStruct, 
			YFSExtnPaymentCollectionOutputStruct paymentOutputStruct, YFCDocument eventDoc, String sTranID) {
		boolean bEventReqd= bridge.isRaiseEventRequired(oCtx, sTranID, OMODLiterals.OMOD_PAYMENT_FAILURE_EVENT);
		if (bEventReqd)	{
			YFCDataBuf sKeyData = oOrder.getOrderHeaderBuf(oCtx);
			eventDoc.getDocumentElement().setAttribute("PaymentChargeType", paymentInputStruct.chargeType);
			eventDoc.getDocumentElement().setAttribute("PaymentFailureReason", paymentOutputStruct.internalReturnMessage);
			bridge.raiseEvent(oCtx, sKeyData, eventDoc, sTranID, OMODLiterals.OMOD_PAYMENT_FAILURE_EVENT);
		}
	}
	
	private void raiseAlert(YFSContext oCtx, YFSExtnPaymentCollectionInputStruct paymentInputStruct, 
			YFSExtnPaymentCollectionOutputStruct paymentOutputStruct, YFCDocument eventDoc, YFCElement collectionOutputElem) {
			cat.debug("In raiseAlert :: OMODCollectionUEHelper");
			eventDoc.getDocumentElement().setAttribute("PaymentChargeType", paymentInputStruct.chargeType);
			eventDoc.getDocumentElement().setAttribute("PaymentFailureReason", paymentOutputStruct.internalReturnMessage);
			YFCElement CollectionFailureDetails = eventDoc.getDocumentElement().createChild("CollectionFailureDetails");
			CollectionFailureDetails.setAttribute("AuthReturnMessage", paymentOutputStruct.internalReturnMessage);
			CollectionFailureDetails.setAttribute("PaymentReference1", paymentOutputStruct.PaymentReference1);
			CollectionFailureDetails.setAttribute("PaymentReference2", paymentOutputStruct.PaymentReference2);
			CollectionFailureDetails.setAttribute("PaymentReference3", paymentOutputStruct.PaymentReference3);
			CollectionFailureDetails.setAttribute("TranType", paymentInputStruct.chargeType);
			CollectionFailureDetails.setAttribute("PaymentType", collectionOutputElem.getAttribute("PaymentType"));
			CollectionFailureDetails.setAttribute("DisplayCreditCardNo", collectionOutputElem.getAttribute("CreditCardNo"));
			CollectionFailureDetails.setAttribute("CreditCardExpirationDate", collectionOutputElem.getAttribute("CreditCardExpirationDate"));
			CollectionFailureDetails.setAttribute("CreditCardType", collectionOutputElem.getAttribute("CreditCardType"));
			CollectionFailureDetails.setAttribute("DisplaySvcNo", collectionOutputElem.getAttribute("SvcNo"));
			CollectionFailureDetails.setAttribute("RequestAmount", paymentInputStruct.requestAmount);
			if(cat.isDebugEnabled())
				cat.debug("Alert XML = " + eventDoc.getString());
			bridge.executeService(oCtx, "OMOD_PaymentDeclinedAlert", eventDoc);
			cat.debug("Out raiseAlert :: OMODCollectionUEHelper");
	}

	private YFCDocument getTemplate(YFSContext oCtx, YFS_Order_Header oOrder, String sTranID){
		return bridge.getEventTemplate(oCtx, sTranID, OMODLiterals.OMOD_PAYMENT_FAILURE_EVENT, oOrder.getEnterprise_Key(),oOrder.getDocument_Type());
	}

	private YFCDocument prepareEventDoc(YFSContext oCtx, YFS_Order_Header oOrder,String sTranID)	{
		YFCDocument templateDoc = getTemplate(oCtx, oOrder, sTranID);
		return  bridge.getOrderDetails(oCtx, oOrder, templateDoc);
	}

	public YFCDocument getPaymentInputDocFromStruct(YFSEnvironment oEnv, YFSExtnPaymentCollectionInputStruct paymentInputStruct){

		YFSContext oCtx = (YFSContext)oEnv; 	
		YFCDocument paymentInputDoc = OMODPaymentUEXMLManager.convertPaymentInputStructToXML(paymentInputStruct);
		String sOrderHeaderKey = paymentInputStruct.orderHeaderKey;

		YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, 
				OMODLiterals.YFS_SELECT_METHOD_WAIT, false);

		paymentInputDoc.getDocumentElement().setAttribute(YFS_Order_Header.ORDER_NO, oOrder.getOrder_No());
		paymentInputDoc.getDocumentElement().setAttribute(YFS_Order_Header.ENTERPRISE_KEY, oOrder.getEnterprise_Key());

		return paymentInputDoc ;
	}

	public YFSExtnPaymentCollectionOutputStruct processCollectionResponse(YFSContext oCtx,
			YFSExtnPaymentCollectionInputStruct paymentInputStruct,
			YFCDocument outDoc, YFS_Order_Header oOrder) {
		cat.debug("In processCollectionResponse :: OMODCollectionUEHelper");
		if (cat.isDebugEnabled())
			cat.debug("responseDoc = " + outDoc.toString());
		YFSExtnPaymentCollectionOutputStruct paymentOutputStruct = new YFSExtnPaymentCollectionOutputStruct();
		YFCElement collectionOutputElem=outDoc.getDocumentElement().getChildElement("Payment");

		if(cat.isDebugEnabled())
			cat.debug("Collection Output XML = " + collectionOutputElem.getString());	

		if (!YFCObject.isVoid(collectionOutputElem)){		        	
			String sResponseCode = collectionOutputElem.getAttribute(OMODLiterals.OMOD_COLL_RESPONSE_CODE);

			if (!YFCObject.equals(sResponseCode, OMODLiterals.OMOD_COLL_RESPONSE_APPROVED) 
					&&  !YFCObject.equals(sResponseCode, OMODLiterals.OMOD_COLL_RESPONSE_DECLINE)
					&&  !YFCObject.equals(sResponseCode, OMODLiterals.OMOD_COLL_RESPONSE_SRVC_UNAVL)){

				YFCException ex = new YFCException(OMODErrorCodes.OMOD_INVALID_COLLECTION_RESPONSE_CODE);
				ex.setAttribute(OMODLiterals.OMOD_COLL_RESPONSE_CODE, sResponseCode);
				throw ex;
			}

			paymentOutputStruct=processCollectionResponse(oCtx, paymentInputStruct, collectionOutputElem, oOrder, sResponseCode);

		}
		cat.debug("Out processCollectionResponse :: OMODCollectionUEHelper");

		return paymentOutputStruct;
	}
	
	
	/**
	 * //Review with Hari
	 * 
	 * @param ctx
	 * @param order
	 * @return
	 */
	private YTimestamp computeAuthExpirationDate(YFSContext ctx,YFCDate orderCreationDate) {
		
		YTimestamp authExpirationDateTS = YFCDateUtils.getNewDate(orderCreationDate, OMODLiterals.OMOD_PAYPAL_AUTH_EXPIRATION_DAYS).getYTimestamp();
		
		if(cat.isDebugEnabled() && !YFCObject.isVoid(orderCreationDate) && !YFCObject.isVoid(authExpirationDateTS)) {
			cat.debug("Order Creation Date : "+orderCreationDate.toString());
			cat.debug("New Auth Expiration Date : "+authExpirationDateTS.toString());
		}
		
		return	authExpirationDateTS;
	}
	
	
	/**
	 * 
	 * @param orderHeaderKey
	 * @return
	 */
	private YFCDocument getInputDocumentForCancelOrder(String orderHeaderKey) {
		
		YFCDocument changeOrderInputDoc = YFCDocument.createDocument("Order"); 
		YFCElement changeOrderElement = changeOrderInputDoc.getDocumentElement();
		changeOrderElement.setAttribute("Action", "CANCEL");
		changeOrderElement.setAttribute(YFS_Order_Header.ORDER_HEADER_KEY, orderHeaderKey);
		
		return changeOrderInputDoc;
	}

}

