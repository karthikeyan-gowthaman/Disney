package com.ibm.rds.payment.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.ibm.rds.api.ApiInvoker;
import com.ibm.rds.util.PaymentProcessingUtil;
import com.ibm.rds.util.XMLLiterals;
import com.ibm.rds.util.XMLUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.shared.ycd.YCDConstants;
import com.yantra.shared.ycp.YCPConstants;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;

public class RDSSVSSettlementAPI {
	private static YFCLogCategory log = YFCLogCategory.instance(RDSSVSSettlementAPI.class);
	public static YFSExtnPaymentCollectionOutputStruct charge(YFSEnvironment env, Document docSettlementInput, YFSExtnPaymentCollectionOutputStruct settleOutStruct) throws YFSException {
		log.verbose("Begin : RDSSVSSettlementAPI:charge()");
		Document docSVSWebserviceOut =  null;
		Element eleSettlementInput = docSettlementInput.getDocumentElement();
		String strRequestAmount = eleSettlementInput.getAttribute("RequestAmount");
		double dRequestAmount = Double.parseDouble(strRequestAmount);
		String strAuthId = eleSettlementInput.getAttribute("AuthorizationId");
		String strChargeType = eleSettlementInput.getAttribute("ChargeType");
		String strOrderHeaderKey = eleSettlementInput.getAttribute(XMLLiterals.A_ORDER_HEADER_KEY);
		String strOrderNo = eleSettlementInput.getAttribute(XMLLiterals.A_ORDER_NO);
		String strRequestID = null;
		//String strReconciliationID = null;
		//String strEnteredBy = null;
		String strSVSResponseStatus = null;
		try{
		//SOAPMessage setRequestMessage = generateSettleInputRequest(env, eleSettlementInput);
		Date javaUtilDate= new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String strDate = formatter.format(javaUtilDate);
		eleSettlementInput.setAttribute("Date",strDate);
		//SVS properties
		String strUserName = YFSSystem.getProperty("RDS_SVS_USER_NAME");
		String strPassword = YFSSystem.getProperty("RDS_SVS_PASSWORD");
		String strMerchantName = YFSSystem.getProperty("RDS_MERCHANT_NAME");
		String strMerchantNumber = YFSSystem.getProperty("RDS_MERCHANT_NUMBER");
		String strStoreNumber = YFSSystem.getProperty("RDS_STORE_NUMBER");
		String strDivision = YFSSystem.getProperty("RDS_DIVISION");
		String strRoutingId = YFSSystem.getProperty("RDS_SVS_ROUTING_ID");
				
		eleSettlementInput.setAttribute("SvsUsername",strUserName);
		eleSettlementInput.setAttribute("SvsPassword",strPassword);
		eleSettlementInput.setAttribute("SvsMerchantName",strMerchantName);
		eleSettlementInput.setAttribute("SvsMerchantNo",strMerchantNumber);
		eleSettlementInput.setAttribute("SvsStoreNo",strStoreNumber);
		eleSettlementInput.setAttribute("SvsDivision",strDivision);
		eleSettlementInput.setAttribute("SvsRoutingID",strRoutingId);
		Document docSoapRequest = ApiInvoker.invokeService(env, "RDSSVSSettlementSoapReqSyncService", docSettlementInput);
			
		log.verbose("RDSSVSSettlementAPI:charge: SVS input XML :: : "+XmlUtils.getString(docSoapRequest.getDocumentElement()));
		
		docSVSWebserviceOut = ApiInvoker.invokeService(env, "RDSSvsWebserviceSyncService", docSoapRequest);
		Element eleDocSVSWebserviceOut = docSVSWebserviceOut.getDocumentElement();
		
		log.verbose("RDSSVSSettlementAPI:charge: SVS output XML :: " + XmlUtils.getString(docSVSWebserviceOut));
		
		Element replyNode =  (Element) eleDocSVSWebserviceOut.getElementsByTagName("preAuthCompleteReturn").item(0);
		
		//Element replyNode =  (Element) docSVSWebserviceOut.getElementsByTagNameNS("*", "replyMessage").item(0);
		//strEnteredBy = getEnteredBy(strOrderHeaderKey, env);
		String responseCode = RDSSVSResponseCodeAPI.getReasonCode(replyNode);
		strSVSResponseStatus = RDSSVSResponseCodeAPI.getDecision(replyNode);
		log.debug("strSVSResponseStatus: " + strSVSResponseStatus);
		strRequestID = RDSSVSResponseCodeAPI.getRequestId(replyNode);
		log.debug("strRequestID: " + strRequestID);
		//strReconciliationID = RDSCyberSourceResponseCodeAPI.getReconciliationID(docSVSWebserviceOut, "c:ccCaptureReply");
	//	String errorResponse = CyberSourceResponseCode.getResponseMessageFromCommonCode(env, responseCode);
		String errorResponse = RDSSVSResponseCodeAPI.svsResponseMap.get(responseCode);
		log.debug("errorResponse: " + errorResponse);	
		if ("Approval".equals(strSVSResponseStatus)) {	
			settleOutStruct.authorizationAmount = dRequestAmount;
			log.debug("settleOutStruct.authorizationAmount: " + settleOutStruct.authorizationAmount);
			settleOutStruct.authorizationId = strRequestID;		
			settleOutStruct.tranAmount = dRequestAmount;
			settleOutStruct.tranType = strChargeType;	
			settleOutStruct.tranReturnCode = "ACCEPT";
			settleOutStruct.tranReturnFlag = "Y";					
			settleOutStruct.internalReturnCode = "CHARGE"; 
			settleOutStruct.requestID = strRequestID;
			settleOutStruct.tranReturnMessage = strRequestID;
			log.debug("settleOutStruct.tranReturnMessage: " + settleOutStruct.tranReturnMessage);
			return settleOutStruct;
		}
	
		/*if ("Host Unavailable".equals(strSVSResponseStatus)) {
			settleOutStruct.authorizationAmount = 0.0D;
			settleOutStruct.tranAmount =  0.0D;	
			settleOutStruct.tranType = strChargeType;	
			settleOutStruct.tranReturnCode = strSVSResponseStatus;
			settleOutStruct.tranReturnFlag = "N";
			settleOutStruct.retryFlag = "Y";
			settleOutStruct.internalReturnCode = "CHARGE"; 
			return settleOutStruct;
		}*/
		/*settleOutStruct.authorizationAmount = 0.0D;	
		settleOutStruct.authorizationId = strAuthId;
		settleOutStruct.tranAmount = dRequestAmount;
		settleOutStruct.tranType = strChargeType;
		settleOutStruct.tranReturnCode = strSVSResponseStatus;
		settleOutStruct.internalReturnMessage = "Payment Settlement Failed : " + strSVSResponseStatus;
		settleOutStruct.holdOrderAndRaiseEvent = true;
		settleOutStruct.holdReason = "PAYMENT_FAILURE_HOLD";		 
		settleOutStruct.retryFlag = "N";
		settleOutStruct.suspendPayment = "Y";*/
		// this will move the payment status to failed charge
				settleOutStruct.holdOrderAndRaiseEvent = true; 
				settleOutStruct.holdReason = "PAYMENT_DECLINED";
				settleOutStruct.authReturnCode = YCDConstants.YCD_COLL_RESPONSE_DECLINED;
				settleOutStruct.asynchRequestProcess = false;
				settleOutStruct.authorizationAmount= 0.0;
				settleOutStruct.authReturnFlag = "F";
				settleOutStruct.authReturnMessage = YCDConstants.YCD_COLL_RESPONSE_DECLINED;
				settleOutStruct.tranAmount = dRequestAmount; 
				settleOutStruct.internalReturnCode = YCDConstants.YCD_COLL_RESPONSE_DECLINED;
				settleOutStruct.internalReturnFlag = "F";
				settleOutStruct.tranReturnCode = YCDConstants.YCD_COLL_RESPONSE_DECLINED;
				settleOutStruct.tranReturnFlag = "F";
				settleOutStruct.tranType = strChargeType; 
				settleOutStruct.suspendPayment = YCPConstants.YFS_NO;
				settleOutStruct.retryFlag = YCPConstants.YFS_NO;
				settleOutStruct.collectionDate = new Date(YFCDate.HIGH_DATE.getTime());
		if (docSVSWebserviceOut != null) {
			log.error("RDSSVSSettlementAPI:charge::Payment settlement Failed :: Status :: " + strSVSResponseStatus + " Error Message :: " + errorResponse);
		}
			ApiInvoker.invokeAPI(env, "createException", "<Inbox AutoResolvedFlag='N' InboxType='Payment Failures' " +
				"QueueId='YCD_PAYMENT_DECLINED' ErrorType=''  ExceptionType='PAYMENTEXCEPTION' " + "ExceptionTypeDescription='PAYMENTEXCEPTION' " +
				"OrderHeaderKey='"+strOrderHeaderKey+"' OrderNo='"+strOrderNo+
				"' ErrorReason='" + "EXTN-CYB-" + responseCode + 
				"' Description='Credit Card Authorization failed' >" +
				"<InboxReferencesList><InboxReferences Name='ApiName' " +
				"ReferenceType='TEXT' Value='PaymentExecution'/>" +
				"<InboxReferences Name='Response Status' ReferenceType='TEXT' Value='"+strSVSResponseStatus+"'/>" +
				"<InboxReferences Name='ERRORDESCRIPTION' ReferenceType='TEXT' Value='"+
				errorResponse +"'/></InboxReferencesList></Inbox>");
			return settleOutStruct;
		
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("An Exception occured while processing RDSSVSSettlementAPI:charge() method", exception);
			try{
				ApiInvoker.invokeAPI(env, "createException", "<Inbox AutoResolvedFlag='N' InboxType='Payment Failures' " +
					"QueueId='YCD_PAYMENT_DECLINED' ErrorType=''  ExceptionType='PAYMENTEXCEPTION' ExceptionTypeDescription='PAYMENTEXCEPTION' " +
					"OrderHeaderKey='"+strOrderHeaderKey+"' OrderNo='"+strOrderNo+"' Description='An Exception occured while processing CollectionCreditCardUEImpl.settlement() method' > " +
					"<InboxReferencesList><InboxReferences Name='ApiName' ReferenceType='TEXT' Value='PaymentExecution'/>" +
					"<InboxReferences Name='ERRORDESCRIPTION' ReferenceType='TEXT' Value='An Exception occured while processing CollectionCreditCardUEImpl.settlement() " +
					"method'/></InboxReferencesList></Inbox>");
			}catch(Exception e)
			{
				log.error("Exception occured while raising the alert.");
			}
			throw new YFSException("An Exception occured while processing RDSSVSSettlementAPI:charge() method" + exception.getMessage());
		}	
		
		
	}

	public static YFSExtnPaymentCollectionOutputStruct refund(
			YFSEnvironment env, Document docRevAuthInput,
			YFSExtnPaymentCollectionOutputStruct settleOutStruct) {
		// TODO Auto-generated method stub
		log.verbose("Begin : RDSSVSSettlementAPI:refund()");
		Element eleInDoc = docRevAuthInput.getDocumentElement();
		String strRequestAmount = eleInDoc.getAttribute("RequestAmount");
		double dRequestAmount = Double.parseDouble(strRequestAmount);
		String strAuthId = eleInDoc.getAttribute("AuthorizationId");
		//String strRequestId = eleInDoc.getAttribute("AuthorizationId");
		String strChargeType = eleInDoc.getAttribute("ChargeType");
		String strEnterpriseCode = eleInDoc.getAttribute("EnterpriseCode");
		
		//String authorizationExpirationDate = PaymentProcessingUtil.getAuthExpirationDate(env, strChargeType, strEnterpriseCode);	 
		//System.out.println("TIME: "+authorizedDateTime +  "Amount:  "+ amount+"Expiration Date" + authorizationExpirationDate);
		settleOutStruct.authorizationAmount = dRequestAmount;	
		settleOutStruct.authorizationId = strAuthId;
		settleOutStruct.tranAmount =  dRequestAmount;
		settleOutStruct.tranType = strChargeType;	
		settleOutStruct.tranReturnCode = "ACCEPT";			
		settleOutStruct.tranReturnFlag = "N";
		settleOutStruct.internalReturnCode = "REFUND"; 
		settleOutStruct.requestID = strAuthId;	      
		settleOutStruct.tranReturnMessage = "";
		return settleOutStruct;
	}
	

		
}
