package com.ibm.rds.util;

/**
 * Holds XML literal constants
 * @author IBM
 *
 */
public interface XMLLiterals {
	public static final String PROG_ID = "progId";
	public static final String A_USER_ID = "UserId";
	public static final String E_COMMON_CODE = "CommonCode";
	public static final String E_COMMON_CODE_LIST = "CommonCodeList";
	public static String A_ORGANIZATION_CODE = "OrganizationCode";
	public static String A_CODE_TYPE = "CodeType";
	public static String API_GET_COMMON_CODE_LIST = "getCommonCodeList";
	public static String A_CODE_VALUE = "CodeValue";
	public static String A_CODE_SHORT_DESCRIPTION = "CodeShortDescription";
	public static String strXpath_Element = "Order/OrderLines/OrderLine";
		/* Constants added for Address verification */
	public static String E_PERSON_INFO_SHIP_TO = "PersonInfoShipTo";
	public static String A_CARRIER_SERVICE_CODE = "CarrierServiceCode";
	public static String A_SHIP_NODE = "ShipNode";
	public static String A_ZIPCODE = "ZipCode";
	public static String A_COUNTRY = "Country";
	/* Constants added for Address verification */

	// Added for Address Verification System by Domnic.
	public static final String A_ID = "ID";
	public static final String E_ADDRESS_VERIFICATION_RESP_MSGS = "AddressVerificationResponseMessages";
	public static final String E_ADDRESS_VERIFICATION_RESP_MSG = "AddressVerificationResponseMessage";
	
	//Added for returns - Start
	public static final String E_EXTN = "Extn";
	public static final String A_ORDER_LINE_KEY = "OrderLineKey";
	public static final String E_DERIVED_FROM = "DerivedFrom";
	public static final String A_IS_RETURN_LABEL_USED = "ExtnIsReturnLabelUsed";
	public static final String A_CALLING_ORG_CODE = "CallingOrganizationCode";
	public static final String A_ENTERPRISE_CODE = "EnterpriseCode";
	public static final String A_STATUS = "Status";
	public static final String A_STATUS_QTY = "StatusQty";
	public static final String E_LINE_CHARGES = "LineCharges";
	public static final String E_LINE_CHARGE = "LineCharge";
	public static final String E_LINE_TAXES = "LineTaxes";
	public static final String E_LINE_TAX = "LineTax";
	public static final String A_CHARGE_AMOUNT = "ChargeAmount";
	public static final String A_CHARGE_CATEGORY = "ChargeCategory";
	public static final String A_CHARGE_NAME = "ChargeName";
	public static final String A_TAX_NAME = "TaxName";
	public static final String A_ORDERED_QTY = "OrderedQty";
	public static final String A_CHARGE_PER_LINE = "ChargePerLine";
	public static final String A_TAX = "Tax";
	public static final String A_DERIVED_FROM_ORDERLINE_KEY = "DerivedFromOrderLineKey";
	public static final String A_DERIVED_FROM_ORDERHEADER_KEY = "DerivedFromOrderHeaderKey";
	public static final String E_HEADER_CHARGES = "HeaderCharges";
	public static final String E_HEADER_TAXES = "HeaderTaxes";
	public static final String E_HEADER_CHARGE = "HeaderCharge";
	public static final String E_LINE_PRICE_INFO ="LinePriceInfo";
	public static final String A_IS_DISCOUNT = "IsDiscount";
	public static final String A_UNIT_PRICE = "UnitPrice";
	public static final String A_ACTUAL_PRICING_QTY =	"ActualPricingQty";
	public static final String A_ENTRY_TYPE = "EntryType";
	public static final String A_PROCESS_PAYMENT_ON_RO = "ProcessPaymentOnReturnOrder";
	public static final String A_POS_ORDER_NO ="ohd_order_nbr";
	public static final String A_POS_RETURN_ORDER_NO ="ra_nbr";
	public static final String A_POS_ACTION_RESULT = "action_result";
	public static final String A_POS_SEND_RESPONSE = "send_response";
	public static final String A_POS_SUPRESS_REFUND = "suppress_refund";
	public static final String A_STERLING_ORDER_NO = "ecom_order_nbr";
	public static final String A_POS_ORDERLINE_NO ="odt_seq_nbr";
	public static final String A_POS_RETURN_QTY = "qty";
	public static final String A_SHIP_TO_NBR = "ship_to_nbr";
	public static final String E_ORDER = "Order";
	public static final String E_ORDER_LINES = "OrderLines";
	public static final String E_ORDER_STATUSES = "OrderStatuses";
	public static final String E_ORDER_LINE = "OrderLine";
	public static final String A_PRIME_LINE_NO = "PrimeLineNo";
	public static final String A_DOCUMENT_TYPE = "DocumentType";
	public static final String A_RETURN_QTY = "ReturnQty";
	public static final String A_COMPANY = "company";
	public static final String A_ORDER_DATE ="OrderDate";
	public static final String A_ORDER_TYPE ="OrderType";
	public static final String E_OVERALL_TOTALS ="OverallTotals";
	public static final String A_RATIO = "Ratio";
	public static final String A_EXTENDED_PRICE = "ExtendedPrice";
	public static final String A_DISPLAY_EXTENDED_PRICE = "DisplayExtendedPrice";
	public static final String A_PAYMENT_STATUS = "PaymentStatus";
	public static final String A_PAYMENT_RULE_ID = "PaymentRuleId";
	public static final String A_UNLIMITED_CHARGES = "UnlimitedCharges";
	public static final String A_LAST_NAME = "LastName";
	public static final String A_FIRST_NAME = "FirstName";
	public static final String A_CHARGE_SEQUENCE = "ChargeSequence";
	public static final String A_PAYMENT_REFERENCE1 = "PaymentReference1";
	public static final String E_PAYMENT_DETAILS_LIST = "PaymentDetailsList";
	public static final String E_PAYMENT_DETAILS = "PaymentDetails";
	public static final String A_PROCESSED_AMOUNT = "ProcessedAmount";
	public static final String E_PERSON_INFO_BILL_TO = "PersonInfoBillTo";
	public static final String A_LINE_TOTAL = "LineTotal";
	//Added for returns - End
	
	//Added for Payment Processing - Start
	public static final String A_REQUEST_AMOUNT = "RequestAmount";
	public static final String A_CHARGE_TYPE = "ChargeType";
	public static final String A_PAYMENT_TYPE = "PaymentType";
	public static final String A_TOTAL_AMOUNT = "TotalAmount";
	public static final String A_ORDER_HEADER_KEY = "OrderHeaderKey";
	public static final String A_ORDER_NO = "OrderNo";
	public static final String E_PRICE_INFO = "PriceInfo";
	public static final String E_PAYMENT_METHODS = "PaymentMethods";
	public static final String E_PAYMENT_METHOD = "PaymentMethod";
	public static final String E_CHARGE_TXN_DETAILS = "ChargeTransactionDetails";
	public static final String E_CHARGE_TXN_DETAIL = "ChargeTransactionDetail";
	//Added for Payment Processing - End
	
	/* Order Fulfillment - Start */
	public static final String A_REMAINING_CHARGE_AMOUNT = "RemainingChargeAmount";
	public static final String A_EXTN_DW_SHIPMENT_GROUP = "ExtnDWShipmentGroup";
	/* Order Fulfillment - End */

       public static final String E_PROMISE_LINES = "PromiseLines";
	public static final String E_SHIP_NODES = "ShipNodes";
	public static final String E_SHIP_NODE = "ShipNode";
	public static final String A_NODE = "Node";
	public static final String E_PROMISE = "Promise";
	public static final String E_SUGGESTED_OPTION = "SuggestedOption";
	public static final String E_OPTION = "Option";
	public static final	String A_NODE_QTY = "NodeQty";
	public static final String A_ITEM_ID = "ItemID";
	public static final String E_ASSIGNMENTS = "Assignments";
	public static final String E_INVENTORY = "Inventory";
	public static final String A_QUANTITY = "Quantity";
	public static final String A_AVAILABLE_QTY = "AvailableQuantity";
	public static final String E_PROMISE_LINE = "PromiseLine";
	public static final String E_AVAILABILITY = "Availability";
	public static final String E_AVAILABLE_INVENTORY = "AvailableInventory";
	public static final String E_SHIP_NODE_AVAILABLE_INV = "ShipNodeAvailableInventory";
	public static final String E_ERROR = "Error";
	public static final String A_ERROR_RELATED_MORE_INFO_ATTRIBUTE = "ErrorRelatedMoreInfo";
	public static final	String A_REQUIRED_QTY = "RequiredQty";
	
	public static final String A_TAX_PERCENTAGE = "TaxPercentage";
	public static final String A_EXTN_DW_TAX = "ExtnDemandWareTax";
	public static final String A_EXTN_VERTEX_TAX = "ExtnVertexTax";
	public static final String E_SHIP_NODE_PERSON_INFO = "ShipNodePersonInfo";
	public static final String E_SHIPMENT = "Shipment";
	public static final String E_LINE_OVERALL_TOTALS = "LineOverallTotals";
	public static final String A_LINE_TOTAL_WITHOUT_TAX = "LineTotalWithoutTax";
	
	/* Gift Card Fulfillment - Start */
	public static final String E_GIFT_CARD = "GiftCard";
	public static final String E_CARD = "Card";
	public static final String E_ISSUE_AMOUNT = "IssueAmount";
	public static final String E_TRANSACION = "Transaction";
	public static final String E_SHIPMENT_TAG_SERIAL = "ShipmentTagSerial";
	public static final String E_ISSUE_GC_RETURN = "issueGiftCardReturn";
	
	public static final String A_EOV_DATE = "EOVDate";
	public static final String A_CARD_CURRENCY = "CardCurrency";
	public static final String A_CARD_NUMBER = "CardNumber";
	public static final String A_PIN = "PIN";
	public static final String A_AMOUNT = "Amount";
	public static final String A_CURRENCY = "Currency";
	public static final String A_MERCHANT_NAME = "MerchantName";
	public static final String A_MERCHANT_NO = "MerchantNumber";
	public static final String A_STORE_NO = "StoreNumber";
	public static final String A_DIVISION = "Division";
	public static final String A_DATE = "Date";
	public static final String A_INVOICE_NO = "InvoiceNumber";
	public static final String A_ROUTING_ID = "RoutingID";
	public static final String A_STAN = "Stan";
	public static final String A_CHECK_FOR_DUPLICATE = "CheckForDuplicate";
	public static final String A_APPROVED_AMOUNT = "ApprovedAmount";
	public static final String A_BALANCE_AMOUNT = "BalanceAmount";
	public static final String A_CARD_NO = "CardNo";
	public static final String A_ERROR_CODE = "ErrorCode";
	public static final String A_ERROR_DESCRIPTION = "ErrorDescription";
	/* Gift Card Fulfillment - End */
	
	public static final String A_EXP_SHIP_DATE = "ExpectedShipmentDate";
	public static final String E_HEADER_TAX = "HeaderTax";
	public static final String A_PROCESS_PAYMENT_RO = "ProcessPaymentOnReturnOrder";
	
	public static final String A_ORDER_RELEASE_KEY = "OrderReleaseKey";
	public static final String A_SUB_LINE_NO = "SubLineNo";
	
	public static final String A_ADDRESS_LINE_1 = "AddressLine1";
	public static final String A_ADDRESS_LINE_2 = "AddressLine2";
	public static final String A_STATE = "State";
	public static final String A_EMAIL_ID = "EMailID";
	
	public static final String E_EXTN_Pepperjam_Feed = "EXTNPepperjamFeed";
	public static final String A_IS_PUBLISHED = "IsPublished";
	public static final String A_REMAINING_QTY = "RemainingQty";
	public static final String E_ITEM = "Item";
	public static final String A_DISCOUNT = "Discount";
	public static final String A_ORIGINAL_ORDERED_QTY = "OriginalOrderedQty";
	
	public static final String A_RETURNABLE_QTY = "ReturnableQty";
	public static final String A_MAX_LINE_STATUS = "MaxLineStatus";
	public static final String E_CONTAINERS = "Containers";
	public static final String A_EXT_REF_1 = "ExternalReference1";
	public static final String A_TRACKING_NO = "TrackingNo";
}
