package com.ibm.cs.payment.ue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.cs.payments.alerts.CSPaymentsAlertsAPI;
import com.ibm.cs.utils.CSCommonUtil;
import com.ibm.cs.utils.CSConstants;
import com.ibm.cs.utils.CSXMLConstants;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionCreditCardUE;

public class CSCollectionCreditCardUE implements YFSCollectionCreditCardUE {

	private static final YFCLogCategory LOGGER = YFCLogCategory.instance(CSCollectionCreditCardUE.class.getName());
	private static boolean isLogVerboseEnabled = LOGGER.isVerboseEnabled();
	
	/**
	 * This method authorizes, Reverse authorizes, Settlement ,Refund for Credit card tender type
	 * 
	 * @param env Required. Environment handle required by the API
	 * @param yfsExtnPaymentCollectionInputStruct Required. Input Structure is generated by the product
	 * @throws YFSUserExitException
	 */
	
	public YFSExtnPaymentCollectionOutputStruct collectionCreditCard(
			YFSEnvironment env, YFSExtnPaymentCollectionInputStruct yfsExtnPaymentCollectionInputStruct)
			throws YFSUserExitException {
		
		if(isLogVerboseEnabled)LOGGER.verbose("CSCollectionCreditCardUE:: collectionCreditCard():: BEGIN" + yfsExtnPaymentCollectionInputStruct.toString());
		YFSExtnPaymentCollectionOutputStruct yfsExtnPaymentCollectionOutputStruct = new YFSExtnPaymentCollectionOutputStruct();
		
		// invoke collection/refunds
		try {
			
				this.processPayment(env,
						yfsExtnPaymentCollectionInputStruct,
						yfsExtnPaymentCollectionOutputStruct);
			
		} catch (Exception exception) {
			if(exception instanceof YFSUserExitException){
				YFSUserExitException yfsuee = new YFSUserExitException();
				yfsuee.setErrorCode(((YFSUserExitException) exception).getErrorCode());
				yfsuee.setErrorDescription(((YFSUserExitException) exception).getErrorDescription());	
				throw yfsuee;
			} else {
				LOGGER.verbose(exception);
				YFSUserExitException yfsuee = new YFSUserExitException();
				yfsuee.setErrorCode("2070");
				yfsuee.setErrorDescription("Error Occurred while Payment Processing :: "+ exception.getMessage());	
				throw yfsuee;
			}
			
		}
		if(isLogVerboseEnabled){
    		LOGGER.verbose("CSCollectionCreditCardUE:: collectionCreditCard():: END");
		}
	
		return yfsExtnPaymentCollectionOutputStruct;
	}

	
	/**
	 * This method gets the orderdetails and prepare input for cybersource webservice call and process the webservice response
	 * checks for authReturnMessage(decision) for ACCEPT/REJECT
	 * If decision is ACCEPT it sets all the authorization information in yfsExtnPaymentCollectionOutputStruct (internally calls change order)
	 * to persist the authorization information.
     * If decision is REJECT then it will throw an error message.
     * If transactionStatus is not success it will give an error message
     * 
	 * @param env Required. Environment handle required by the API
	 * @param yfsExtnPaymentCollectionInputStruct  Required. Input Structure is generated by the product
	 * @param yfsExtnPaymentCollectionOutputStruct Required. Has to be initialized in code
	 * @throws Exception
	 */
	private void processPayment(YFSEnvironment env, 
			YFSExtnPaymentCollectionInputStruct yfsExtnPaymentCollectionInputStruct, 
			YFSExtnPaymentCollectionOutputStruct yfsExtnPaymentCollectionOutputStruct) throws Exception {
		
		if(isLogVerboseEnabled){
			LOGGER.verbose("In getAuthFraudCheckForPayment :: getAuthFraudCheckForPayment(): BEGIN");
		}
		
		
		Properties _props = null;
		StringBuffer sb = null;
		Document docWebServiceResponse = null;
		String requestId= null;
		String transactionCode = null;
    	String transactionMessage = null;
    	String cvvAuthCode = null;
		String tranReturnFlag = null;
		String tranReturnCode = null;
		String authReturnMessage = null;
		String authReturnCode = null;
		String authCode = null;
		String chargeType = null;
		String authorizationExpirationDate = null;
		String authorizationId = null;
		String requestAmount = null;
		Document docGetOrderListOutput = null;
		String strCodeValue = null;
		Element eleOrder = null;
		String strSuccessCode = null;
		String strRejectCode = null;
		String strFinanceFailCode = null;
		String strTechFailCode = null;
		Double authorizationAmount = 0.0;
		
		String transacitonFailedData = null;
		boolean reversedueToExpiry = false;
		
		CSPaymentsAlertsAPI csAlertUtil = new CSPaymentsAlertsAPI();
		        
		// First get the orderdetails with paymentmethod from env if orderdetails is null then call getOrderDetails and prepare paymentInput
		docGetOrderListOutput = this.getInputForServicesEnablement(env, yfsExtnPaymentCollectionInputStruct,
				docGetOrderListOutput);
		
	
		
		
		
		NodeList paymentElementlist = SCXmlUtil.getXpathNodes(docGetOrderListOutput.getDocumentElement(), "//OrderList/Order/PaymentMethods/PaymentMethod[@PaymentType='CREDIT_CARD' and @CreditCardNo !="+yfsExtnPaymentCollectionInputStruct.creditCardNo+"]");
		int lent = paymentElementlist.getLength();
		for (int i=0;i<lent;i++){
			Element paymentEle = (Element) paymentElementlist.item(i);
			paymentEle.getParentNode().removeChild(paymentEle);
		}
		
		

		if(("AUTHORIZATION").equalsIgnoreCase(yfsExtnPaymentCollectionInputStruct.chargeType) && yfsExtnPaymentCollectionInputStruct.requestAmount<0){
			Element paymentElementEle = SCXmlUtil.getXpathElement(docGetOrderListOutput.getDocumentElement(), "//OrderList/Order/PaymentMethods/PaymentMethod[@PaymentType='CREDIT_CARD' and @CreditCardNo ="+yfsExtnPaymentCollectionInputStruct.creditCardNo+"]");
			String paymentKey = SCXmlUtil.getAttribute(paymentElementEle, "PaymentKey");
			eleOrder = SCXmlUtil.getChildElement(docGetOrderListOutput.getDocumentElement(), "Order");
			Element reverseAuthEle = SCXmlUtil.getXpathElement(eleOrder, 
					"//Order[@DocumentType='0001']/ChargeTransactionDetails/ChargeTransactionDetail[@ChargeType='AUTHORIZATION' and @Status='CHECKED' and " +
					"@PaymentKey="+paymentKey+" and " +
							"@AuthorizationID="+yfsExtnPaymentCollectionInputStruct.authorizationId+"]");
			String strAuthExpiry = reverseAuthEle.getAttribute("AuthorizationExpirationDate");
			boolean isAuthExpired = isAuthExpired(strAuthExpiry);
			if(!isAuthExpired){
				String strAuthExpiryID = reverseAuthEle.getAttribute("AuthorizationID");
				eleOrder.setAttribute("AuthorizationID", strAuthExpiryID);
			}else{
				reversedueToExpiry = true;
			}
		}

		if(isLogVerboseEnabled){
    		LOGGER.verbose("CSCollectionCreditCardUE:: collectionCreditCard():: "+SCXmlUtil.getString(eleOrder));
		}
		
			if(("CHARGE").equalsIgnoreCase(yfsExtnPaymentCollectionInputStruct.chargeType) && yfsExtnPaymentCollectionInputStruct.requestAmount<0){
				
			eleOrder = SCXmlUtil.getChildElement(docGetOrderListOutput.getDocumentElement(), "Order");
			NodeList chargeTranlist  = SCXmlUtil.getXpathNodes(eleOrder, 
					"//Order/ChargeTransactionDetails/ChargeTransactionDetail[@ChargeType='CHARGE' and @Status='CHECKED' and PaymentMethod/@CreditCardNo='" + 
			yfsExtnPaymentCollectionInputStruct.creditCardNo + "']");
			for (int i=0;i<chargeTranlist.getLength();i++){
				Element chargeTranEle = (Element) chargeTranlist.item(i);
				
				if(isLogVerboseEnabled){
		    		LOGGER.verbose("CSCollectionCreditCardUE:: collectionCreditCard():: "+SCXmlUtil.getString(chargeTranEle));
				}
				String strSettledAmount = chargeTranEle.getAttribute("SettledAmount");
				double strRequestAmount =  (-1) * yfsExtnPaymentCollectionInputStruct.requestAmount;
				if (Double.parseDouble(strSettledAmount)>=strRequestAmount){
					String strRequestId = SCXmlUtil.getXpathAttribute(chargeTranEle, "CreditCardTransactions/CreditCardTransaction/@RequestId");
					if(isLogVerboseEnabled){
						LOGGER.verbose("CSCollectionCreditCardUE::strRequestId::"+strRequestId);
						}
					eleOrder.setAttribute("RequestId", strRequestId);
				}
				
			}
		
			}
		// getting the response from web service
		if (!YFCObject.isVoid(docGetOrderListOutput)){
			
			eleOrder = SCXmlUtil.getChildElement(docGetOrderListOutput.getDocumentElement(), CSXMLConstants.E_ORDER);		
			eleOrder.setAttribute(CSXMLConstants.A_CHARGE_TRANSACTION_KEY, yfsExtnPaymentCollectionInputStruct.chargeTransactionKey);
			docWebServiceResponse = CSCommonUtil.invokeService(env, _props.getProperty(CSConstants.CS_CREDITCARD_PAYMENT_PROCESSING_SERVICE), docGetOrderListOutput);
			
			if(isLogVerboseEnabled){
				LOGGER.verbose("CSCollectionCreditCardUE:: getAuthFraudCheckForPayment():: payment webservice response :: "+SCXmlUtil.getString(docWebServiceResponse));
			}
		}//if getOrderList out doc
		
		String strPaymentKey = SCXmlUtil.getXpathAttribute(docGetOrderListOutput.getDocumentElement(), 
				"//OrderList/Order/ChargeTransactionDetails/ChargeTransactionDetail[@ChargeTransactionKey='"+ yfsExtnPaymentCollectionInputStruct.chargeTransactionKey+"']/@PaymentKey");
		String strDisplayCreditCardNo = SCXmlUtil.getXpathAttribute(docGetOrderListOutput.getDocumentElement(), 
				"//OrderList/Order/PaymentMethods/PaymentMethod[@PaymentKey='"+strPaymentKey+"']/@DisplayCreditCardNo");
		String strCreditCardType = yfsExtnPaymentCollectionInputStruct.creditCardType;
		
		if (!YFCObject.isVoid(docWebServiceResponse))
		{			 
		 			if(docWebServiceResponse.getDocumentElement() != null){
		 				Element eleWebServiceResponse = docWebServiceResponse.getDocumentElement();
			 			transactionCode = SCXmlUtil.getXpathAttribute(eleWebServiceResponse, "//PaymentMethods/PaymentMethod/PaymentDetails/@TransactionCode");
			 			transactionMessage = SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@TransactionMessage");
						authReturnMessage = SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@AuthReturnMessage");
						authReturnCode = SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@AuthReturnCode");
						chargeType = SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@ChargeType");
						authorizationExpirationDate =  SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@AuthorizationExpirationDate");
						requestAmount = SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@RequestAmount");
						requestId = SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@RequestId");
						if(!YFCObject.isVoid(SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@TotalAuthorized")))
						authorizationAmount = Double.parseDouble(SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@TotalAuthorized"));
						authorizationId = SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@AuthorizationID");
						transacitonFailedData = SCXmlUtil.getXpathAttribute(eleWebServiceResponse,"//PaymentMethods/PaymentMethod/PaymentDetails/@TransactionFailedData");
						
						String strLongDescription = getCommonCodeDesc(env,CSConstants.CS_CC_ERROR_CODE,transactionCode);
						
						
												
						if(isLogVerboseEnabled)
						{
							sb = new StringBuffer();
							sb.append("transactionCode :");
							sb.append(transactionCode);
							sb.append(" transactionMessage :");
							sb.append(transactionMessage);
							sb.append(" authReturnCode :");
							sb.append(authReturnCode);
							sb.append(" authReturnMessage :");
							sb.append(authReturnMessage);
							sb.append(" authCode :");
							sb.append(authCode);
							sb.append(" chargeType :");
							sb.append(chargeType);
							sb.append(" authorizationExpirationDate :");
							sb.append(authorizationExpirationDate);
							sb.append(" requestAmount :");
							sb.append(requestAmount);
							LOGGER.verbose(sb.toString());
						}
						
						//get payment system response code list
						Document commoncodelistDoc = SCXmlUtil.createDocument(CSXMLConstants.E_COMMON_CODE);
				        Element inEle = commoncodelistDoc.getDocumentElement();
				        inEle.setAttribute(CSXMLConstants.A_CODE_TYPE,CSConstants.CS_PAYMENT_PROCESS);
				        
						Document docGetStatuCodesList = CSCommonUtil.getCommonCodeList(env, commoncodelistDoc, CSConstants.GET_COMMON_CODE_LIST_TEMPLATE);
						
						NodeList nodeCommonCodeList = docGetStatuCodesList.getElementsByTagName(CSXMLConstants.E_COMMON_CODE);
						for (int nodeCount = 0; nodeCount < nodeCommonCodeList.getLength(); nodeCount++) {
							Element ele = (Element)nodeCommonCodeList.item(nodeCount);
							if(ele != null) 
								strCodeValue =ele.getAttribute(CSXMLConstants.A_CODE_VALUE);
							
							if (!YFCObject.isVoid(strCodeValue)){
								if (CSConstants.SUCCESS_CODE.equals(strCodeValue)){
									strSuccessCode = ele.getAttribute(CSXMLConstants.A_CODE_LONG_DESCRIPTION);
								}else if(CSConstants.REJECT_CODE.equals(strCodeValue)){
									strRejectCode =  ele.getAttribute(CSXMLConstants.A_CODE_LONG_DESCRIPTION);
								}else if (CSConstants.FF_CODE.equals(strCodeValue)){
									strFinanceFailCode =  ele.getAttribute(CSXMLConstants.A_CODE_LONG_DESCRIPTION);
								}else if(CSConstants.TF_CODE.equals(strCodeValue)){
									strTechFailCode =  ele.getAttribute(CSXMLConstants.A_CODE_LONG_DESCRIPTION);
								}
							}//success code
				                 
						}//for loop common code list
						

						if(!YFCObject.isVoid(transactionCode)){
							
							
							
							if(!YFCObject.isVoid(strSuccessCode) && transactionCode.equalsIgnoreCase(strSuccessCode)){
									
								if(isLogVerboseEnabled){
						    		LOGGER.verbose("CSCollectionCreditCardUE:: getAuthFraudCheckForPayment():: payment processing is SUCCESS");
								}
									
								
					    			yfsExtnPaymentCollectionOutputStruct.authCode = authCode;
									yfsExtnPaymentCollectionOutputStruct.authReturnCode = authReturnCode;
									yfsExtnPaymentCollectionOutputStruct.authReturnMessage = authReturnMessage;
									yfsExtnPaymentCollectionOutputStruct.tranReturnFlag = tranReturnFlag;
									yfsExtnPaymentCollectionOutputStruct.tranReturnCode = tranReturnCode;
									yfsExtnPaymentCollectionOutputStruct.tranReturnMessage = CSConstants.SUCCESS;
									yfsExtnPaymentCollectionOutputStruct.sCVVAuthCode = cvvAuthCode;
									yfsExtnPaymentCollectionOutputStruct.tranType = yfsExtnPaymentCollectionInputStruct.chargeType;
									if(yfsExtnPaymentCollectionInputStruct.requestAmount<0){
										yfsExtnPaymentCollectionOutputStruct.authorizationAmount = (-1) * authorizationAmount;									
										yfsExtnPaymentCollectionOutputStruct.tranAmount = (-1) * authorizationAmount;
									}else{
										yfsExtnPaymentCollectionOutputStruct.authorizationAmount = authorizationAmount;
										yfsExtnPaymentCollectionOutputStruct.tranAmount = authorizationAmount;
									}
									yfsExtnPaymentCollectionOutputStruct.authorizationExpirationDate = setAuthExpiryDate(env);
									yfsExtnPaymentCollectionOutputStruct.authorizationId = authorizationId;
									yfsExtnPaymentCollectionOutputStruct.HoldAgainstBook = "Y";
									yfsExtnPaymentCollectionOutputStruct.requestID = requestId;
									yfsExtnPaymentCollectionOutputStruct.PaymentReference9 = "0";
									
									
					    	} else if((getHardDeclineCode(env, transactionCode, strFinanceFailCode))  ) {
				    		
				    		if(isLogVerboseEnabled){
					    		LOGGER.verbose("CSCollectionCreditCardUE:: getAuthFraudCheckForPayment():: Web service responded with REJECT");
							}
				    		 
				    		String strPaymnetDec = "Payment settlement/Authorization for the payment method " + strCreditCardType +" "+ strDisplayCreditCardNo +
									" is failed due to Cybersource  error code "+ transactionCode +" "+ strLongDescription ;
				    		if(("AUTHORIZATION").equals(yfsExtnPaymentCollectionInputStruct.chargeType) && yfsExtnPaymentCollectionInputStruct.requestAmount<0){
				    			
				    			createAsyncRequest(env,docGetOrderListOutput);
				    			yfsExtnPaymentCollectionOutputStruct.tranReturnMessage = CSConstants.SUCCESS;
								yfsExtnPaymentCollectionOutputStruct.tranType = yfsExtnPaymentCollectionInputStruct.chargeType;
								yfsExtnPaymentCollectionOutputStruct.authorizationAmount = yfsExtnPaymentCollectionInputStruct.requestAmount;									
								yfsExtnPaymentCollectionOutputStruct.tranAmount = yfsExtnPaymentCollectionInputStruct.requestAmount;
								yfsExtnPaymentCollectionOutputStruct.authorizationExpirationDate = setAuthExpiryDate(env);
								yfsExtnPaymentCollectionOutputStruct.authorizationId = yfsExtnPaymentCollectionInputStruct.authorizationId;
								yfsExtnPaymentCollectionOutputStruct.requestID = requestId; 
								
							}else{
							yfsExtnPaymentCollectionOutputStruct.tranType = yfsExtnPaymentCollectionInputStruct.chargeType;
							yfsExtnPaymentCollectionOutputStruct.tranReturnMessage = authReturnMessage;
				    		yfsExtnPaymentCollectionOutputStruct.tranAmount = 0.00;
				    		yfsExtnPaymentCollectionOutputStruct.tranReturnMessage = "REJECT";
							yfsExtnPaymentCollectionOutputStruct.holdOrderAndRaiseEvent = true;
							yfsExtnPaymentCollectionOutputStruct.holdReason = "CS_PAYMENT_HOLD";
							csAlertUtil.raiseAlert(env, "CS_PAYMENT_FAILURE", yfsExtnPaymentCollectionInputStruct.orderHeaderKey, "CS_PAYMENT_ALERT_Q", strPaymnetDec);
							//invoke service to drop message to email Q
						
							/*Boolean isEmailSuccess = CSSVSPaymentProcessingUtil.dropAuthSettlementEmailToETMessageQueue(env, yfsExtnPaymentCollectionInputStruct.orderNo, 
									yfsExtnPaymentCollectionInputStruct.documentType, yfsExtnPaymentCollectionInputStruct.enterpriseCode, yfsExtnPaymentCollectionInputStruct.chargeType);
							if(!isEmailSuccess)
								throw new YFSException("Error while posting message to email Queue:CSSVSPaymentAPI");*/
						
							}
							
							
						}else if((!YFCObject.isVoid(strRejectCode) && transactionCode.equalsIgnoreCase(strRejectCode)) || getSoftDeclineCode(env, transactionCode, strTechFailCode)){
							
							String strRetryCount = yfsExtnPaymentCollectionInputStruct.paymentReference9;
							String strPaymnetDec = "Payment settlement/Authorization for the payment method " + strCreditCardType +" "+ strDisplayCreditCardNo +
									" is failed due to Cybersource  error code "+ transactionCode +" "+ strLongDescription ;
							
							if(("AUTHORIZATION").equals(yfsExtnPaymentCollectionInputStruct.chargeType) && yfsExtnPaymentCollectionInputStruct.requestAmount<0){
								createAsyncRequest(env,docGetOrderListOutput);
								yfsExtnPaymentCollectionOutputStruct.tranReturnMessage = CSConstants.SUCCESS;
								yfsExtnPaymentCollectionOutputStruct.tranType = yfsExtnPaymentCollectionInputStruct.chargeType;
								yfsExtnPaymentCollectionOutputStruct.authorizationAmount = yfsExtnPaymentCollectionInputStruct.requestAmount;									
								yfsExtnPaymentCollectionOutputStruct.tranAmount = yfsExtnPaymentCollectionInputStruct.requestAmount;
								yfsExtnPaymentCollectionOutputStruct.authorizationExpirationDate = setAuthExpiryDate(env);
								yfsExtnPaymentCollectionOutputStruct.authorizationId = yfsExtnPaymentCollectionInputStruct.authorizationId;
								yfsExtnPaymentCollectionOutputStruct.requestID = requestId;
							}else{
							if(YFCCommon.isVoid(strRetryCount)){
								yfsExtnPaymentCollectionOutputStruct.PaymentReference9 = "1";
								yfsExtnPaymentCollectionOutputStruct.retryFlag = "Y";
								
							}else{
								int retryCount = Integer.parseInt(strRetryCount);
								String strMaxRetry = getCommonCodeDesc(env,CSConstants.CS_MAX_RETRY,"RetryCount");
								if(retryCount>=Integer.parseInt(strMaxRetry)){
									yfsExtnPaymentCollectionOutputStruct.tranType = yfsExtnPaymentCollectionInputStruct.chargeType;
									yfsExtnPaymentCollectionOutputStruct.tranReturnMessage = authReturnMessage;
						    		yfsExtnPaymentCollectionOutputStruct.tranAmount = 0.00;
						    		yfsExtnPaymentCollectionOutputStruct.tranReturnMessage = "REJECT";
									yfsExtnPaymentCollectionOutputStruct.holdOrderAndRaiseEvent = true;
									yfsExtnPaymentCollectionOutputStruct.holdReason = "CS_PAYMENT_HOLD";
									
									
									csAlertUtil.raiseAlert(env, "CS_PAYMENT_FAILURE", yfsExtnPaymentCollectionInputStruct.orderHeaderKey, "CS_IT_SUPPORT_ALERT_QUEUE", strPaymnetDec);
								}else{
									yfsExtnPaymentCollectionOutputStruct.retryFlag = "Y";
									retryCount = retryCount+1;
									yfsExtnPaymentCollectionOutputStruct.PaymentReference9 = String.valueOf(retryCount);
								}
							}
							}
						}
						else{
							yfsExtnPaymentCollectionOutputStruct.tranType = yfsExtnPaymentCollectionInputStruct.chargeType;
			    			yfsExtnPaymentCollectionOutputStruct.tranAmount = 0.00;
							yfsExtnPaymentCollectionOutputStruct.retryFlag = "Y";
							yfsExtnPaymentCollectionOutputStruct.holdOrderAndRaiseEvent = false;
						}
	 				}//transaction code not null
		 		}else{
		 			if(isLogVerboseEnabled){
			    		LOGGER.verbose("CSCollectionCreditCardUE:: getAuthFraudCheckForPayment():: Retry has reached its limits and hence terminating");
					}
					LOGGER.debug("********Payment Gateway Not Reachable********");
					YFSUserExitException yfsuee = new YFSUserExitException();
					yfsuee.setErrorCode("2100");
					yfsuee.setErrorDescription("Credit Card Auth response is null , hence can't proceed");					
					throw yfsuee;
		 		}
		}
		else {
			if(isLogVerboseEnabled){
	    		LOGGER.verbose("CSCollectionCreditCardUE:: getAuthFraudCheckForPayment():: Retry has reached its limits and hence terminating");
			}
			LOGGER.debug("********Payment Gateway Not Reachable********");
			YFSUserExitException yfsuee = new YFSUserExitException();
			yfsuee.setErrorCode("2100");
			yfsuee.setErrorDescription("Credit Card Auth response is null , hence can't proceed");					
			throw yfsuee;
		}
		
		if(isLogVerboseEnabled){
    		LOGGER.verbose("CSCollectionCreditCardUE:: getAuthFraudCheckForPayment():: END");
		}
						
	}


	
	/**
	 * 
	 * Creating Async request for Rever authorization
	 * 
	 */
	private void createAsyncRequest(YFSEnvironment env,
			Document docGetOrderListOutput) throws Exception {
		if(isLogVerboseEnabled)
		{
			LOGGER.verbose("CSCollectionCreditCardUE:: createAsyncRequest():: START");
		}
		
		Document createAsyncRequestInDoc = SCXmlUtil.createDocument(CSXMLConstants.E_CREATE_ASYNC_REQUEST);
		Element createAsyncRequestEle = createAsyncRequestInDoc.getDocumentElement();
		Element createAsyncRequestAPIEle = SCXmlUtil.createChild(createAsyncRequestEle, "API");
		createAsyncRequestAPIEle.setAttribute("IsService","Y");
		createAsyncRequestAPIEle.setAttribute("Name","CSCreditCardReverseAuthSyncService");
		createAsyncRequestEle.appendChild(createAsyncRequestAPIEle);
		Element createAsyncRequestInputEle = SCXmlUtil.createChild(createAsyncRequestAPIEle, "Input");
		SCXmlUtil.importElement(createAsyncRequestInputEle,docGetOrderListOutput.getDocumentElement());
		
		if(isLogVerboseEnabled)
		{
			LOGGER.verbose("CSCollectionCreditCardUE:: createAsyncRequest() createAsyncRequestInDoc Document:: START" + SCXmlUtil.getString(createAsyncRequestInDoc));
		}
		CSCommonUtil.invokeAPI(env, "createAsyncRequest", createAsyncRequestInDoc); 
		
	}


	/**
	 * 
	 * Setting authorization expiry date for CC tender type 
	 * 
	 */

		private String setAuthExpiryDate(YFSEnvironment env) throws Exception {
			if(isLogVerboseEnabled)
			{
				LOGGER.verbose("CSCollectionCreditCardUE:: setAuthExpiryDate():: START");
			}
			
			String authExpiryDt="";
			Document getCommonCodeListInputDoc = YFCDocument.createDocument(CSXMLConstants.E_COMMON_CODE).getDocument();
			Element getCommonCodeListInputEle = getCommonCodeListInputDoc.getDocumentElement();
			getCommonCodeListInputEle.setAttribute(CSXMLConstants.A_CODE_TYPE,  CSXMLConstants.A_AUTH_EXPIRY_DAYS);
			getCommonCodeListInputEle.setAttribute(CSXMLConstants.A_ORG_CODE, CSConstants.HUB_ORGANIZATION);
			getCommonCodeListInputEle.setAttribute(CSXMLConstants.A_CODE_VALUE, CSXMLConstants.A_CREDIT_CARD);

			Document getCommonCodeListOutputDoc = CSCommonUtil.getCommonCodeList(env, getCommonCodeListInputDoc,
					CSConstants.GET_COMMON_CODE_LIST_RETURNS_TEMPLATE);
			Element getCommonCodeListOutputEle = getCommonCodeListOutputDoc.getDocumentElement();

			Element commonCodeEle = SCXmlUtil.getChildElement(getCommonCodeListOutputEle, CSXMLConstants.E_COMMON_CODE);
			if(!YFCObject.isVoid(commonCodeEle)){
				String daysValue = commonCodeEle.getAttribute(CSXMLConstants.A_CODE_SHORT_DESCRIPTION);
				if(!YFCObject.isVoid(daysValue)){
					if(isLogVerboseEnabled)
					{
						LOGGER.verbose("CSCollectionCreditCardUE:: setAuthExpiryDate():: Current date + common code value of days : " + daysValue);
					}
				 int days = Integer.parseInt(daysValue);
				 Calendar cal = Calendar.getInstance();
				 cal.add(Calendar.DATE, days);
				 SimpleDateFormat format1 = new SimpleDateFormat(CSConstants.AUTH_EXPIRY_DT_FMT);
				 authExpiryDt = format1.format(cal.getTime());
				}
			}
			if(isLogVerboseEnabled)
			{
				LOGGER.verbose("CSCollectionCreditCardUE:: setAuthExpiryDate():: END : Auth Expiry Date: " + authExpiryDt);
			}
			return authExpiryDt;
			
		}
	
	
	private String getCommonCodeDesc(YFSEnvironment env, String csCcErrorCode,
			String transactionCode) throws Exception {
		Document commoncodelistDoc = SCXmlUtil.createDocument(CSXMLConstants.E_COMMON_CODE);
        Element inEle = commoncodelistDoc.getDocumentElement();
        inEle.setAttribute(CSXMLConstants.A_CODE_TYPE,csCcErrorCode);
        inEle.setAttribute(CSXMLConstants.A_CODE_VALUE,transactionCode);
        
        
		Document docGetStatuCodesList = CSCommonUtil.getCommonCodeList(env, commoncodelistDoc, CSConstants.GET_COMMON_CODE_LIST_TEMPLATE);
		return SCXmlUtil.getXpathAttribute(docGetStatuCodesList.getDocumentElement(), "//CommonCodeList/CommonCode/@CodeLongDescription");
	}


	private Document getInputForServicesEnablement(
			YFSEnvironment env,
			YFSExtnPaymentCollectionInputStruct yfsExtnPaymentCollectionInputStruct,
			Document docGetOrderListOutput) throws Exception {
		if(isLogVerboseEnabled){
			LOGGER.verbose("CSCollectionCreditCardUE:: getInputForServicesEnablement():: BEGIN ");
		}
		
		YFCDocument ydocServiceEnablementInput = YFCDocument.createDocument(CSXMLConstants.E_ORDER);

		YFCElement eleOrder = ydocServiceEnablementInput.getDocumentElement();
		eleOrder.setAttribute(CSXMLConstants.A_ENTERPRISE_CODE,yfsExtnPaymentCollectionInputStruct.enterpriseCode);
		eleOrder.setAttribute(CSXMLConstants.A_ORDER_NO, yfsExtnPaymentCollectionInputStruct.orderNo);
		eleOrder.setAttribute(CSXMLConstants.A_ORDER_HEADER_KEY,	yfsExtnPaymentCollectionInputStruct.orderHeaderKey);

		if (!YFCObject.isVoid(ydocServiceEnablementInput))
			docGetOrderListOutput= CSCommonUtil.invokeAPI(env, CSConstants.GET_ORDER_LIST_FOR_PAYMENT_PROCESSING_TEMPLATE, 
						CSConstants.API_GET_ORDER_LIST, ydocServiceEnablementInput.getDocument());
			
		if(isLogVerboseEnabled){
			LOGGER.verbose("CSCollectionCreditCardUE:: getInputForServicesEnablement()::Input Document to Cyber Source through UE input is : "
					+ docGetOrderListOutput);
			LOGGER.verbose("CSCollectionCreditCardUE:: getInputForServicesEnablement():: END");
		}
		
		return docGetOrderListOutput;
	}

	
	/**
	 * 
	 * getTechCode call to test if the gateway transaciton code is tech failure.
	 * @param env
	 * @param tranCode - transaction code from gateway
	 * @param comCodeTech - list of tech failure code
	 * @return
	 * @throws Exception
	 */
	private boolean getSoftDeclineCode(YFSEnvironment env,
			String tranCode, String comCodeTech) throws Exception {
		boolean isTechFail = false;
		for (String retval: comCodeTech.split(",")){
			if (retval.equals(tranCode)){	
				if (isLogVerboseEnabled)
				{
					LOGGER.verbose("CSCollectionCreditCardUECSCollectionCreditCardUE :: getTechCode()::: Technical failure code" + tranCode);
				}
				isTechFail = true;
			}
		}
		
		return isTechFail;
	}
	
	/**
	 * 
	 * getFinCode call to test if the gateway transaction code is financial failure.
	 * @param env
	 * @param tranCode - transaction code from gateway
	 * @param comCodeTech - list of financial failure code
	 * @return
	 * @throws Exception
	 */
	private boolean getHardDeclineCode(YFSEnvironment env,
			String tranCode, String comCodeFin) throws Exception {
		boolean isFinFail = false;
		for (String retval: comCodeFin.split(",")){
			if (retval.equals(tranCode)){	
				if (isLogVerboseEnabled)
				{
					LOGGER.verbose("CSCollectionCreditCardUE :: getFinCode()::: Financial failure code" + tranCode);
				}
				isFinFail = true;
			}
		}
		return isFinFail;
	}
	
	/*
	 * Check if auth is expired or not
	 */
	private boolean isAuthExpired(String strAuthExpiry) throws Exception {
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    	Date dt= new Date();
    	String currentDate = sdf.format(dt);
    	Date newDate = sdf.parse(currentDate);
    	
    	Date authDate = sdf.parse(strAuthExpiry);
    	if(authDate.after(newDate)){
    		return  false;
    	}
    	else{
    		return  true;
    	}
	}
}
