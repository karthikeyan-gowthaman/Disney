package com.ibm.cs.utils;

/**
 * This Will include all the XML Constants used in the implementation
 * @author IBM
 *
 */
public interface CSConstants {

	/* Order Fulfillment - Start */
	public static final String SALES_ORDER_DOCUMENT_TYPE = "0001";
	public static final String RETURN_ORDER_DOCUMENT_TYPE = "0003";
		
	/* Order Fulfillment - End */
	public static final String API_GET_ORDER_LIST = "getOrderList";
		
	//Order modification related changes
	public static final String CS_CS_DAV_SERVICE="CSCybersourceDavService";
	public static final String CS_CS_DAV_SERVICE_URN_NAMESPACE="CS_CS_DAV_SERVICE_URN_NAMESPACE";
	public static final String CS_USER_ID="CS_USER_ID";
	public static final String CS_PASSWORD="CS_PASSWORD";
	public static final String CS_MERCHANT_ID="CS_MERCHANT_ID";
	
	//Credit Card changes
	public static final String CHARGE_TYPE_CHARGE = "CHARGE";
	public static final String CHARGE_TYPE_AUTH = "AUTHORIZATION";
	public static final String CS_CREDITCARD_PAYMENT_PROCESSING_SERVICE = "RDSCreditCardPaymentProcessingService";
	public static final String CS_CC_ERROR_CODE = "CS_CS_ERROR_CODE";
	public static final String GET_COMMON_CODE_LIST_TEMPLATE = "/global/template/api/getCommonCodeList.xml";
	public static final String SUCCESS = "SUCCESS";
	public static final String GET_ORDER_LIST_FOR_PAYMENT_PROCESSING_TEMPLATE = "global/template/api/getOrderlistForPaymentProcessing.xml";
	public static final String HUB_ORGANIZATION = "DEFAULT";
	public static final String GET_COMMON_CODE_LIST_RETURNS_TEMPLATE = "<CommonCodeList><CommonCode OrganizationCode='' DocumentType='' CodeValue='' CodeType='' CodeShortDescription='' CodeLongDescription='' CodeLength=''/></CommonCodeList>";
	public static final String AUTH_EXPIRY_DT_FMT = "yyyyMMddHHmmss";
	public static final String SUCCESS_CODE = "Success";
	public static final String REJECT_CODE = "Reject";
	public static final String CS_PAYMENT_PROCESS = "RD_PAYMENT_PROCESS";
	public static final String FF_CODE = "FinanceFailure";
	public static final String TF_CODE = "TechnicalFailure";
	public static final String CS_MAX_RETRY = "CS_MAX_RETRY";

	//Stored value card
	public static final String AUTH_EXP_DATE_FORMAT = "yyyyMMddHHmmss";

	//Order Capture
	public static final String CS_DC0003 = "CS_DC0003";
	public static final String CREDIT_CARD = "CREDIT_CARD";
	public static final String ACCEPT = "ACCEPT";
	public static final String REJECT = "REJECT";
	public static final String REVIEW = "REVIEW";
	public static final String CS_PMT_CARD_EXP_DAYS = "RD_PMT_CARD_EXP_DAYS";
	public static final String AUTH_EXP_DAYS_99999 = "99999";
	public static final String APPROVE = "APPROVE";
	public static final String CS_FRAUDCHK_REJECTD = "CS_FRAUDCHK_REJECTD";
	public static final String CS_FRAUD_REVIEW_HOLD = "CS_FRAUD_REVIEW_HOLD";
	public static final String CS_FRAUD_HOLD = "CS_FRAUD_HOLD";
	public static final String CS_FRAUD_ET = "CS_FRAUD_ET";

	public static final String GET_ORDER_LIST_TEMPLATE_FOR_FRAUD_HOLD = "global/template/api/getOrderListForFraudHold.xml";
	
	public static final String API_GET_COMMON_CODE_LIST = "getCommonCodeList";
	
	public static final String CS_CYBERSOURCE_FOR_FRAUD_WEBSERVICE = "CSCybersourceForFraudWebService";
		
	public static final String CYBERSOURCE_SUCCESS_RESPONSE_CODE = "100";
	public static final String CURRENCY_USD = "USD";
	public static final String CYBERSOURCE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
}