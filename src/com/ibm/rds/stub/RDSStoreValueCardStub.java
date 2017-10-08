package com.ibm.rds.stub;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import com.ibm.shared.omod.OMODLiterals;
import com.yantra.util.YFCUtils;
import com.yantra.yfc.date.YDate;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class RDSStoreValueCardStub {

private static final YFCLogCategory log = YFCLogCategory.instance(RDSStoreValueCardStub.class.getName());
	
	/**
	 * Method processPaymentRequest for processing the payment auth settlement and
	 * refund scenarios in stubbed mode.
	 * @param env 
	 * @param paymentInputDoc
	 * @return paymentOutputDoc
	 * @throws YFSException
	 */
	public static YFCDocument processPaymentRequest(YFSEnvironment env, YFCDocument paymentInputDoc) throws YFSException {
		log.verbose("Begin : RDSCreditCardStub:processPaymentRequest()");
		YFCElement paymentInptEle = paymentInputDoc.getDocumentElement();
		YFCDocument paymentOutputDoc= null;
		String strChargeType = paymentInptEle.getAttribute(OMODLiterals.CHARGE_TYPE);
		String strGiftCardNo = paymentInptEle.getAttribute(OMODLiterals.SVC_NO);
		
		//Exception Invalid card scenario
		if(!YFCCommon.isStringVoid(strGiftCardNo) && (strGiftCardNo.startsWith("0000") || strGiftCardNo.endsWith("9999"))){
			paymentOutputDoc =  handleExceptionFlow(paymentInptEle); 
			return paymentOutputDoc;
		}
		
		if (OMODLiterals.AUTHORIZATION.equalsIgnoreCase(strChargeType)) {
			paymentOutputDoc =  authorize(paymentInptEle); 
		} else if (OMODLiterals.CHARGE .equalsIgnoreCase(strChargeType)) {
			paymentOutputDoc = settlement(paymentInptEle);
		}
		
		return paymentOutputDoc;
	}
	
	/**
	 * Method authorize for processing authorization request in
	 * stubbed mode.
	 * @param paymentInputEle
	 * @return paymentOutputDoc
	 * @throws YFSException
	 */
	public static YFCDocument authorize(YFCElement paymentInptEle) throws YFSException {
		
		YFCDocument paymentOutputDoc=YFCDocument.createDocument("PaymentOutput");
		paymentOutputDoc.getDocumentElement().importNode(paymentInptEle.cloneNode(true));
		YFCElement paymentOutputEle=paymentOutputDoc.getDocumentElement().getChildElement("Payment");
		String requestAmount = paymentInptEle.getAttribute("RequestAmount");
		//Dummy code considered returned from IBM Payment gateway for authorization
		SecureRandom random = new SecureRandom();
		String paymentReference1=new BigInteger(65, random).toString(32);
		//Auth Expiration Date
		YDate ydate=YDate.newDate(YDate.newDate() , 7);
		ydate.setEndOfDay();
		
		paymentOutputEle.setAttribute("AuthCode", paymentReference1);
		paymentOutputEle.setAttribute("AuthReturnFlag", "N");
		paymentOutputEle.setAttribute("AuthorizationAmount", requestAmount);
		paymentOutputEle.setAttribute("AuthorizationExpirationDate", ydate.getString(YFCUtils.APP_FORMAT));
		paymentOutputEle.setAttribute("AuthorizationId", paymentReference1);
		paymentOutputEle.setAttribute("CollectionResponseCode", OMODLiterals.OMOD_COLL_RESPONSE_APPROVED);
		paymentOutputEle.setAttribute("InternalReturnFlag", "N");
		paymentOutputEle.setAttribute("RetryFlag", "N");
		paymentOutputEle.setAttribute("TranAmount", requestAmount);
		paymentOutputEle.setAttribute("TranReturnCode", paymentReference1);
		paymentOutputEle.setAttribute("TranReturnFlag", "N");
		paymentOutputEle.setAttribute("TranType", OMODLiterals.AUTHORIZATION);
		paymentOutputEle.setAttribute("PaymentReference1", paymentReference1);
		
		return paymentOutputDoc;
	}
	
	/**
	 * Method settlement for processing settlement request in
	 * stubbed mode.
	 * @param paymentInptEle
	 * @return paymentOutputDoc
	 * @throws YFSException
	 */
	public static YFCDocument settlement(YFCElement paymentInptEle) throws YFSException {
		
		YFCDocument paymentOutputDoc=YFCDocument.createDocument("PaymentOutput");
		paymentOutputDoc.getDocumentElement().importNode(paymentInptEle.cloneNode(true));
		YFCElement paymentOutputEle=paymentOutputDoc.getDocumentElement().getChildElement("Payment");
		String requestAmount = paymentInptEle.getAttribute("RequestAmount");
		//Dummy code considered returned from IBM Payment gateway for settlement
		SecureRandom random = new SecureRandom();
		String paymentReference1=new BigInteger(65, random).toString(32);
		paymentOutputEle.setAttribute("AuthReturnFlag", "N");
		paymentOutputEle.setAttribute("AuthorizationAmount", requestAmount);
		paymentOutputEle.setAttribute("CollectionResponseCode", OMODLiterals.OMOD_COLL_RESPONSE_APPROVED);
		paymentOutputEle.setAttribute("InternalReturnFlag", "N");
		paymentOutputEle.setAttribute("RetryFlag", "N");
		paymentOutputEle.setAttribute("TranAmount", requestAmount);
		paymentOutputEle.setAttribute("TranReturnCode", paymentReference1);
		paymentOutputEle.setAttribute("TranReturnFlag", "N");
		paymentOutputEle.setAttribute("TranType", OMODLiterals.CHARGE);
		paymentOutputEle.setAttribute("PaymentReference1", paymentReference1);
		
		return paymentOutputDoc;
	}
	
	/**
	 * Method handleExceptionalFlow for handling exception flow 
	 * where Invalid Credit card number is passed in the input.
	 * @param paymentInptEle
	 * @return paymentOutputDoc
	 * @throws YFSException
	 */
	public static YFCDocument handleExceptionFlow(YFCElement paymentInptEle) throws YFSException {
		
		YFCDocument paymentOutputDoc=YFCDocument.createDocument("PaymentOutput");
		paymentOutputDoc.getDocumentElement().importNode(paymentInptEle.cloneNode(true));
		YFCElement paymentOutputEle=paymentOutputDoc.getDocumentElement().getChildElement("Payment");
		//Dummy code considered returned from IBM Payment gateway as response for this transaction
		SecureRandom random = new SecureRandom();
		String tranReturnCode=new BigInteger(65, random).toString(32);

		paymentOutputEle.setAttribute("CollectionResponseCode", OMODLiterals.OMOD_COLL_RESPONSE_DECLINE);
		paymentOutputEle.setAttribute("InternalReturnFlag", "Y");
		paymentOutputEle.setAttribute("InternalReturnMessage", "OMOD_PG_ERROR_30001");
		paymentOutputEle.setAttribute("RetryFlag", "N");
		paymentOutputEle.setAttribute("SuspendPayment", "Y");
		paymentOutputEle.setAttribute("TranAmount", "0.00");
		paymentOutputEle.setAttribute("TranReturnCode", tranReturnCode);
		
		return paymentOutputDoc;
	}

}
