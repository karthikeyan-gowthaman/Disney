package com.ibm.rds.util;

import com.yantra.yfs.core.YFSSystem;

/**
 * This Will include all the XML Constants used in the implementation
 * @author IBM
 *
 */
public interface Constants {
	
	public static final String IOM_UTIL_0001 = "IOM_UTIL_0001"; 
	public static final String OMP 	 = "OMP";
	public static final String YFS_ORDER_HEADER = "YFS_ORDER_HEADER";
	public static final String YES = "Y";
	public static final String NO = "N";
	public static final String KEY_SOAP_MESSAGE_FACTORY = "javax.xml.soap.MessageFactory";
	public static final String KEY_SOAP_CONNECTION_FACTORY = "javax.xml.soap.SOAPConnectionFactory";
	//Set ur default Message factory here , which will override the other settings.
	public static final String DEFAULT_SOAP_MESSAGE_FACTORY = "org.apache.axis.soap.MessageFactoryImpl"; 
	public static final String DEFAULT_SOAP_CONNECTION_FACTORY = "org.apache.axis.soap.SOAPConnectionFactoryImpl"; 
 	public static final String UNDERSCORE = "_"; 
	public static final String COLON = ":"; 
	public static final String API_GET_PROPERTY = "getProperty";
	public static final String END_POINT_URL = "END_POINT_URL";
	public static final String ORDER_VALIDATE_CODES = "ORDER_VALIDATE_CODES";
	public static final String ORDER_TOTAL = "OrderTotal";
	public static final String GET_COMMON_CODE_LIST_API ="getCommonCodeList";
	public static final String ORDER_HOLD_TYPES = "OrderHoldTypes";
	public static final String ORDER_HOLD_TYPE = "OrderHoldType";
	public static final String HOLD_TYPE = "HoldType";
	public static final String CREATE_ORDER_HOLD = "CREATE_ORDER_HOLD";
	public static final String COMMON_CODE = "CommonCode";
	public static final String CODE_TYPE = "CodeType";
	public static final String CODE_VALUE = "CodeValue";
	public static final String AUTH_EXP_DATE_FORMAT = "yyyyMMddHHmmss"; 
	
	//Added for returns - Start
	public static final String RETURN_LABEL_COST = "RETURN_LABEL_COST";
	public static final String CHARLOTTE_RUSSE_ORG = "CHARLOTTE_RUSSE";
	public static final String API_GET_ORDERLINE_LIST = "getOrderLineList";
	public static final String API_GET_ORDER_LIST = "getOrderList";
	public static final String GET_ORDERLINELIST_OUTPUT_TEMPLATE = "global/template/api/returnOrder/getOrderLineListOutput.xml";
	public static final String GET_ORDERLIST_OUTPUT_TEMPLATE = "global/template/api/returnOrder/getOrderListOutput.xml";
	public static final String XPATH_ORDERSTATUS ="OrderLineList/OrderLine/OrderStatuses/OrderStatus";
	public static final String XPATH_ORDERSTATUS_POS ="OrderList/Order/OrderLines/OrderLine/OrderStatuses/OrderStatus";
	public static final String XPATH_LINE_OVERALL_TOTALS = "Order/OrderLines/OrderLine/LineOverallTotals";
	public static final String strXpath_Element_POS = "Order/OrderLines/OrderLine";
	public static final String SHIPPED_STATUS = "3700";
	public static final String RETURN_DOC_TYPE = "0003";
	public static final String RETURN_CREATED_STATUS = "3700.01";
	public static final String XPATH_LINECHARGES = "OrderLineList/OrderLine/LineCharges";
	public static final String XPATH_LINECHARGES_POS = "Order/OrderLines/OrderLine/LineCharges";
	public static final String XPATH_LINETAXES = "OrderLineList/OrderLine/LineTaxes";
	public static final String XPATH_LINETAXES_POS = "Order/OrderLines/OrderLine/LineTaxes";
	public static final String FLAT_RETURN_LABEL_CHARGE = "FLAT_RETURN_LABEL_CHARGE";
	public static final String CALL_CENTER_RETURNS = "Call Center";
	public static final String DC_RETURNS = "DC";
	public static final String POS_RETURNS = "POS";
	public static final String POS_INDOC ="POS_INDOC";
	public static final String SUCCESS ="Success";
	public static final String PAID_STATUS ="PAID";
	public static final String RDS_PAYMENT_RULE = "RDS_PAYMENT_RULE";
	public static final String POS_TENDER = "POS_TENDER";
	public static final String POS_PAYMENT_REF1 = "PaymentReference1";
	public static final String CHARGE = "CHARGE";
	//Added for returns - End
	
	
	//Cybersource Webservice
	String CYBERSOURCE_URN_NAMESPACE = "cybersource.urn.namespace";
	String MERCHANT_ID = "cybersource.account.merchantid";
	String MERCHANT_REF_CODE = "cybersource.merchant.referencecode";
	String MERCHANT_TRANSACTION_KEY = "cybersource.account.transactionkey";
	String ENDPOINT_URL = "cybersource.transactionprocessor.url";
	String CYBERSOURCE_REQUEST_MESSAGE = "urn:schemas-cybersource-com:transaction-data-1.71";
	String CYBERSOURCE_MERCHANT_ID = YFSSystem.getProperties().getProperty(MERCHANT_ID);
	String CYBERSOURCE_FREQUENCY = "On-Demand";
	
	String CYBERSOURCE_REPLY_NS_PREFIX = "c";
	String CYBERSOURCE_URN_PREFIX = "urn";
	String WCS_WEBSERVICE_XML_PREFIX = "soapenv";
	String DEFAULT_CYBERSOURCE_URN_NAMESPACE = "urn:schemas-cybersource-com:transaction-data-1.72";
	String TRANSACTION_KEY = "cybersource.account.transactionkey";
	
	//Added for WebOrderCapture-Start
	public static final String XMLNS = "xmlns";
	public static final String CREATE_ORDER_API ="createOrder";
	public static final String ORDER_HOLD_TYPE_KEY = "OrderHoldTypeKey";
	public static final String ORDER = "Order";
	public static final String ORDER_HEADER_KEY = "OrderHeaderKey";
	public static final String CREATE_TS = "createTS";
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String ACTION = "Action";
	public static final String MODIFY = "MODIFY";
	public static final String OVERRIDE = "Override";
	public static final String REMORSE_HOLD = "REMORSE_HOLD";
	public static final String STATUS = "Status";
	public static final String HOLD_RESOLVE_TIME = "HOLD_RESOLVE_TIME";
	public static final String GET_ORDER_HOLD_TYPE_LIST = "getOrderHoldTypeList";
	public static final String STATUS_1100 = "1100";
	//Added for WebOrderCapture-End

	public static final String STRING_9999 = "9999";
	public static final String ZERO = "0";
	public static final String FIND_INVENTORY_OUTPUT_TEMPLATE =  "/global/template/api/FossilFindInventoryAPI.xml";
	public static final String API_FIND_INVENTORY = "findInventory";
	
	/* Order Fulfillment - Start */
	public static final String DOCUMENT_TYPE_0001 = "0001";
	public static final String STATUS_CONFIRMED = "CONFIRMED";
	public static final String STATUS_INSUFFICIENT_INV = "INSUFFICIENT_INV";
	public static final String STATUS_INVALID = "INVALID";
	/* Order Fulfillment - End */
	
	//Order Inquiry - Start
	public static final String A_FROM_ORDER_DATE = "FromOrderDate";
	public static final String A_TO_ORDER_DATE = "ToOrderDate";
	public static final String A_BILL_TO_ID = "BillToID";
	public static final String A_CUSTOMER_EMAIL_ID = "CustomerEMailID";
	//Order Inquiry - End 
	
	//Added for Vertex-start
	public static final String XMLNS_URN = "xmlns:urn";
	public static final String XMLNS_SOAPENV = "xmlns:soapenv";
	//Added for Vertex-end
	
	/* Order Modification - Start */
	public static final String CHANGE_ORDER_API = "changeOrder";
	public static final String CANCEL_REASON_CODE_01 = "DC_INSUFFICIENT_INV";
	public static final String CANCEL_REASON_CODE_02 = "DC_INVALID";
	public static final String CANCEL_REASON_CODE_03 = "DC_CANCEL";
	
	public static final String CANCEL_REASON_TEXT_01 = "DC Insufficient Inventory";
	public static final String CANCEL_REASON_TEXT_02 = "DC Invalid Order";
	public static final String CANCEL_REASON_TEXT_03 = "Cancelled by DC";
	/* Order Modification - End */
	
	/* Gift Card Fulfillment - Start */
	public static final String SRV_SEND_BUSINESS_EMAIL = "RDSSendBusinessEmailSyncService";
	public static final String SRV_GC_ACTIVATION_FAILURE_ALERT = "RDSGCActivationFailureAlertSyncService";
	public static final String SRV_ISSUE_GIFT_CARD_WEB_SERVICE = "RDSIssueGiftCardWebService";
	public static final String SRV_ISSUE_E_GIFT_CARD_WEB_SERVICE = "RDSIssueVirtualGiftCardWebService";
	public static final String V_CURRENCY_USD = "USD";
	public static final String V_FALSE = "FALSE";
	public static final String V_GC_ACTIVATION_SUCCESS_CODE = "01";
	public static final String SVS_TECHNICAL_ERROR = "999";
	public static final String SVS_CONNECTION_ISSUE_MSG = "Error in SVS connection - Gift Card is not activated.";
	/* Gift Card Fulfillment - End */
	
	public static final String GET_ORDER_DETAILS_OUTPUT_TEMPLATE =  "global/template/api/salesOrder/getOrderDetailsOutput.xml";
	public static final String API_GET_ORDER_DETAILS = "getOrderDetails";
	public static final String GET_SHIPMENT_LIST_OUTPUT_TEMPLATE =  "global/template/api/shipment/getShipmentListOutput.xml";
	public static final String API_GET_SHIPMENT_LIST = "getShipmentList";
	public static final String GET_ORDER_LIST_OUTPUT_TEMPLATE =  "global/template/api/salesOrder/getOrderListOutput.xml";
	public static final String GET_ORDER_LIST_CHARGE_TXN_TEMPLATE = "global/template/api/salesOrder/getOrderListOutput_ChargeTransaction.xml";

	public static final String RETURN_PPL_REAS_CODE = "RETURN_PPL_REAS_CODE";
	
	//Fraud Check Processing - Start
	public static final String ORDER_NO = "OrderNo";
	public static final String DOCUMENT_TYPE = "DocumentType";
	public static final String CS_ORDER_ELE = "Conversion";
	public static final String CS_ORDER_NO = "MerchantReferenceNumber";
	public static final String CS_NEW_DECISION_ELE = "NewDecision";
	public static final String CS_FRAUD_CHK_ACCEPT = "ACCEPT";
	public static final String CS_FRAUD_CHK_REJECT = "REJECT";
	public static final String CANCEL = "CANCEL";
	public static final String MODIFICATION_REASON_CODE = "ModificationReasonCode";
	public static final String REASON_CODE_FRAUD_CHECK_REJECTED = "RDS_FRAUDCHK_REJECTD";
	public static final String HOLD_TYPE_FRAUD_REVIEW_HOLD = "FRAUD_REVIEW_HOLD";
	public static final String STATUS_1300 = "1300";
	public static final String CS_FRAUD_CHECK_EOD_SERVICE_ERROR = "CS_FRAUD_CHECK_EOD_SERVICE_ERROR";
	//Fraud Check Processing - End
	
	//CR17 - Start
	public static final String HOLIDAY_SEASON = "HOLIDAY_SEASON";
	public static final String SO_START_DATE = "SO_START_DATE";
	public static final String SO_END_DATE = "SO_END_DATE";
	public static final String RO_START_DATE = "RO_START_DATE";
	public static final String RO_END_DATE = "RO_END_DATE";	
	public static final String RETURN_WINDOW = "RETURN_WINDOW";
	//CR17 - End
	
	public static final String ONE = "1";
	public static final String PJ_PROGRAM_ID = "5561";
	
	public static final String RETURN_RECEIVED_STATUS = "3700.02";
	public static final String API_CONFIRM_SHIPMENT = "confirmShipment";
}