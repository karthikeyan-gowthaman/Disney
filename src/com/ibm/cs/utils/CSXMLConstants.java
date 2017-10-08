package com.ibm.cs.utils;

/**
 * Holds XML literal constants
 * @author IBM
 *
 */
public interface CSXMLConstants {

	/**** Order fulfillment *****/
	public static final String A_SHIPMENT_CONFIRMATION_STATUS = "ShipmentConfirmationStatus";
	public static final String A_ORDER_NO = "OrderNo";
	public static final String A_ENTERPRISE_CODE = "EnterpriseCode";
	public static final String A_DOCUMENT_TYPE = "DocumentType";
	public static final String A_RELEASE_NO = "ReleaseNo";
	public static final String A_PRIMELINE_NO = "PrimeLineNo";
	public static final String A_EXTN_SHIPPING_GROUP_ID = "ExtnShippingGroupId";
	public static final String A_CHARGE_CATEGORY = "ChargeCategory";
	public static final String A_SHIP_NODE = "ShipNode";
	public static final String A_EXPECTED_DELIVERY_DATE = "ExpectedDeliveryDate";
	public static final String A_CARRIER_SERVICE_CODE = "CarrierServiceCode";
	public static final String A_DELIVERY_START_DATE = "DeliveryStartDate";
	public static final String A_QUANTITY = "Quantity";
	public static final String A_ORDERED_QTY = "OrderedQty";
	public static final String A_NOTE_TEXT = "NoteText";
	public static final String A_REASON_CODE = "ReasonCode";
	public static final String A_VISIBLE_TO_ALL = "VisibleToAll";
	public static final String A_CHARGE_NAME_DESCRIPTION = "ChargeNameDescription";
	public static final String A_CHARGE_AMOUNT = "ChargeAmount";
	public static final String A_INVOICED_CHARGE_AMOUNT = "InvoicedChargeAmount";
	public static final String A_TAX = "Tax";
	public static final String A_AUTO_RESOLVED_FLAG = "AutoResolvedFlag";
	public static final String A_ORDER_HEADER_KEY = "OrderHeaderKey";
	public static final String A_QUEUE_ID = "QueueId";
	public static final String A_EXCEPTION_TYPE = "ExceptionType";
	public static final String A_DESCRIPTION = "Description";
	public static final String A_LINE_TYPE = "LineType";
	public static final String A_MONITOR_CONSOLIDATION_ID = "MonitorConsolidationId";
	public static final String A_GC_SERIAL_NO = "GCSerialNo";
	public static final String A_ITEM_ID = "ItemID";
	public static final String A_CHARGE_NAME = "ChargeName";
	public static final String A_UNIT_PRICE = "UnitPrice";
	public static final String A_EXTN_TESS_ORDERFULFILLMENT_STORE_ID = "ExtnTessOrderFulfillmentStoreID";
	public static final String A_USER_ID = "UserId";
	public static final String A_PASSWORD = "Password";
	public static final String A_CARD_NUMBER = "CardNumber";
	public static final String A_SYSDATE = "Sysdate";
	public static final String A_INVOICE_NUMBER = "InvoiceNumber";
	public static final String A_AMOUNT = "Amount";
	public static final String A_MERCHANT_NAME = "MerchantName";
	public static final String A_MERCHANT_NUMBER = "MerchantNumber";
	public static final String A_STORE_NUMBER = "StoreNumber";
	public static final String A_DIVISION = "Division";
	public static final String A_ROUTING_ID = "RoutingID";
	public static final String A_TRANSACTION_ID = "TransactionID";
	public static final String A_ADJUSTMENT_AMOUNT = "AdjustmentAmount";
	public static final String A_SHIPMENT_KEY = "ShipmentKey";

	public static final String E_EXTN = "Extn";
	public static final String E_ORDER_RELEASE_DETAIL = "OrderReleaseDetail";
	public static final String E_SHIPMENT = "Shipment";
	public static final String E_SHIPMENT_LINES = "ShipmentLines";
	public static final String E_SHIPMENT_LINE = "ShipmentLine";
	public static final String E_ORDER_LINES = "OrderLines";
	public static final String E_ORDER_LINE = "OrderLine";
	public static final String E_ORDER = "Order";
	public static final String E_HEADER_CHARGES = "HeaderCharges";
	public static final String E_HEADER_CHARGE = "HeaderCharge";
	public static final String E_CARRIER_SERVICE_LIST = "CarrierServiceList";
	public static final String E_ORDER_STATUSES = "OrderStatuses";
	public static final String E_ORDER_STATUS = "OrderStatus";
	public static final String E_CARRIER_SERVICE = "CarrierService";
	public static final String E_HEADER_TAXES = "HeaderTaxes";
	public static final String E_HEADER_TAX = "HeaderTax";
	public static final String E_NOTES = "Notes";
	public static final String E_NOTE = "Note";
	public static final String E_UN_SCHEDULE_ORDER = "UnScheduleOrder";
	public static final String E_INBOX = "Inbox";
	public static final String E_INSTRUCTIONS = "Instructions";
	public static final String E_INSTRUCTION = "Instruction";
	public static final String E_CS_GC_SERIAL_NO_LIST = "CSGCSerialNoList";
	public static final String E_CS_GC_SERIAL_NO = "CSGCSerialNo";
	public static final String E_ISSUE_GIFT_CARD = "IssueGiftCard";
	public static final String E_LINEPRICE_INFO = "LinePriceInfo";
	public static final String E_ITEM = "Item";
	/**** Order fulfillment *****/

	/**** Inventory *****/
	public static final String A_EXTN_RECEIVED_FULL = "ExtnReceivedFull";
	public static final String A_STATUS_QTY = "StatusQty";
	/**** Inventory *****/

	/**Tax Adapter**/
	public static final String E_ORGANIZATION = "Organization";
	public static final String E_CORP_PERSON_INFO = "CorporatePersonInfo";
	public static final String E_SHIP_FROM_ADDRESS = "ShipFromAddress";

	public static final String A_ORGANIZATION_CODE = "OrganizationCode";
	public static final String A_ORGANIZATION_KEY = "OrganizationKey";
	public static final String A_CITY = "City";
	public static final String A_STATE = "State";
	public static final String A_COUNTRY = "Country";
	public static final String A_ZIPCODE = "ZipCode";
	public static final String A_ITEMID = "ItemID";
	public static final String A_PRODUCT_CODE = "ProductCode";
	public static final String A_LINE_NUMBER = "LineNumber";
	public static final String A_ORDER_LINE_KEY = "OrderLineKey";

	public static final String A_IS_DISCOUNT = "IsDiscount";
	/**Tax Adapter**/

	public static final String E_ORDER_LINE_KEY = "OrderLineKey";
	public static final String A_PRIME_LINE_NO = "PrimeLineNo";
	public static final String E_TAX_ADAPTOR_INPUT = "TaxAdapterInput";
	public static final String A_CUSTOMER_NAME = "CustomerName";
	public static final String A_TAX_EXEMPT_ID = "TaxExemptId";
	public static final String A_INVOICE_NO = "InvoiceNo";
	public static final String A_INVOICE_DATE = "InvoiceDate";
	public static final String A_INVOKE_FOR_RECORDING = "InvokeForRecording";
	public static final String A_TAX_POINT_DATE = "TaxPointDate";
	public static final String A_FISCAL_DATE = "FiscalDate";
	public static final String A_ORDER_LINE_NUMBER = "LineNumber";
	public static final String E_SHIP_TO_ADDRESS = "ShipToAddress";
	public static final String A_ZIP_CODE = "ZipCode";
	public static final String E_LINE_CHARGES = "LineCharges";
	public static final String E_LINE_CHARGE = "LineCharge";
	public static final String A_LINE_CHARGE_PER_LINE = "ChargePerLine";
	public static final String A_EXTN_TAX_EXEMPT_ID = "TaxExemptId";
	public static final String A_CHARGE_PER_LINE = "ChargePerLine";
	public static final String A_CHARGE_PER_UNIT = "ChargePerUnit";
	public static final String A_CUSTOMER_FIRST_NAME = "CustomerFirstName";
	public static final String A_CUSTOMER_LAST_NAME = "CustomerLastName";
	public static final String A_TAX_NAME = "TaxName";
	public static final String E_LINE_TAX = "LineTax";
	public static final String E_DISCOUNT_CHARGES = "DiscountCharges";
	public static final String E_DISCOUNT_CHARGE = "DiscountCharge";
	public static final String E_NON_DISCOUNT_CHARGES = "NonDiscountCharges";
	public static final String E_NON_DISCOUNT_CHARGE = "NonDiscountCharge";

	public static final String A_ORDER_PURPOSE = "OrderPurpose";
	public static final String A_ORDER_TYPE = "OrderType";
	public static final String A_ORDER_DATE = "OrderDate";
	public static final String A_BY_PASS_PRICING = "BypassPricing";
	public static final String A_PAYMENT_RULE_ID = "PaymentRuleId";

	/* Mash up attributes*/
	public static final String A_GIFT_WRAP = "GiftWrap";
	public static final String A_GIFT_FLAG = "GiftFlag";
	public static final String A_INSTRUCTION_TEXT = "InstructionText";
	public static final String A_INSTRUCTION_TYPE = "InstructionType";
	public static final String A_ACTION = "Action";
	public static final String REMOVE = "REMOVE";

	// Credit Card Changes
	public static final String A_CHARGE_TRANSACTION_KEY = "ChargeTransactionKey";
	public static final String E_COMMON_CODE = "CommonCode";
	public static final String A_CODE_TYPE = "CodeType";
	public static final String A_CODE_VALUE = "CodeValue";
	public static final String A_AUTH_EXPIRY_DAYS = "CS_PMT_CARD_EXP_DAYS";
	public static final String A_ORG_CODE = "OrganizationCode";
	public static final String A_CODE_SHORT_DESCRIPTION = "CodeShortDescription";
	public static final String A_CREDIT_CARD = "CREDIT_CARD";
	public static final String E_CREATE_ASYNC_REQUEST = "CreateAsyncRequest";
	public static final String A_ACTIVE_FLAG = "ActiveFlag";
	public static final String A_CONSOLIDATE = "Consolidate";
	public static final String V_YES = "Y";
	public static final String V_NO = "N";
	public static final String API_CREATE_EXCEPTION = "createException";
	public static final String A_CODE_LONG_DESCRIPTION = "CodeLongDescription";

	//Stored value card
	public static final String E_PAYMENT_METHODS = "PaymentMethods";
	public static final String E_PAYMENT_METHOD = "PaymentMethod";
	public static final String E_PAYMENT_TYPE = "PaymentType";
	public static final String A_PAYMENT_TYPE = "PaymentType";
	public static final String A_PAYMENT_TYPE_DESCRIPTION = "PaymentTypeDescription";
	public static final String A_DISPLAY_SVC_NO = "DisplaySvcNo";
	public static final String A_DETAIL_DESCRIPTION = "DetailDescription";
	public static final String A_PAYMENT_TYPE_GROUP = "PaymentTypeGroup";
	public static final String A_CREDIT_CARD_TYPE = "CreditCardType";
	public static final String A_ERROR_COUNT = "ErrorCount";
	public static final String A_MESSAGE = "Message";

	//Order Capture 
	public static final String A_DATA_KEY = "DataKey";
	public static final String A_DATA_TYPE = "DataType";
	public static final String A_HOLD_FLAG = "HoldFlag";
	public static final String A_HOLD_TYPE = "HoldType";
	public static final String A_AVAILABLE_DATE = "AvailableDate";
	public static final String A_TRANSACTION_Id = "TransactionId";
	public static final String A_TRANSACTION_KEY = "TransactionKey";
	public static final String A_OVERRIDE = "Override";
	public static final String A_STATUS = "Status";
	public static final String A_CS_EMAIL_TYPE = "CSEmailType";
	public static final String A_DECISION = "Decision";
	public static final String A_RESERVATION_MANDATORY = "ReservationMandatory";
	public static final String A_IS_FIRM_PREDEFINED_NODE = "IsFirmPredefinedNode";
	public static final String A_DEPENDENCY_SHIPPING_RULE = "DependencyShippingRule";
	public static final String A_DEPENDENT_ON_PRIMELINE_NO = "DependentOnPrimeLineNo";
	public static final String A_DEPENDENT_ON_SUBLINE_NO = "DependentOnSubLineNo";
	public static final String A_AUTHORIZATION_EXPIRATION_DATE = "AuthorizationExpirationDate";

	public static final String E_TASKQUEUE = "TaskQueue";
	public static final String E_ORDER_HOLD_TYPES = "OrderHoldTypes";
	public static final String E_ORDER_HOLD_TYPE = "OrderHoldType";
	public static final String E_PAYMENT_DETAILS = "PaymentDetails";
	public static final String E_DEPENDENCY = "Dependency";

	public static final String XPATH_ORDERLINE = "OrderLines/OrderLine";
	public static final String XPATH_PAYMENT_METHOD = "PaymentMethods/PaymentMethod";
	public static final String XPATH_CODE_SHORT_DESC_FROM_COMMONCODE = "CommonCode/@CodeShortDescription";
	public static final String XPATH_COUNTRY_FROM_PERSON_INFO_SHIP_TO = "PersonInfoShipTo/@Country";
	public static final String XPATH_ORDERHOLDTYPE_FROM_HOLDTYPESTOPROCESS = "HoldTypesToProcess/OrderHoldType";
	public static final String XPATH_ORDER_HOLD_TYPE = "OrderHoldTypes/OrderHoldType";

	/**** Order Modification *****/
	public static final String E_SHIPPING_GROUPS = "ShippingGroups";
	public static final String E_SHIPPING_GROUP = "ShippingGroup";
	public static final String E_PERSON_INFO_SHIP_TO = "PersonInfoShipTo";
	public static final String E_PERSON_INFO_BILL_TO = "PersonInfoBillTo";
	public static final String E_OVERALL_TOTALS = "OverallTotals";
	public static final String E_LINE_TAXES = "LineTaxes";
	public static final String E_LINE_OVERALL_TOTALS = "LineOverallTotals";
	public static final String E_PERSON_INFO = "PersonInfo";

	public static final String A_IS_NEW_ORDER = "IsNewOrder";
	public static final String A_NAME = "Name";
	public static final String A_EXTN_PROMOTION_TYPE = "ExtnPromotionType";
	public static final String A_EXTN_PROMOTION_CLASS = "ExtnPromotionClass";
	public static final String A_EXTN_PROMOTION_THRESHOLD = "ExtnPromotionThreshold";
	public static final String A_GRAND_TOTAL = "GrandTotal";
	public static final String A_GRAND_TAX = "GrandTax";
	public static final String A_MIN_ORDER_STATUS = "MinOrderStatus";
	public static final String A_STATUS_CANCELLED = "9000";
	public static final String A_REPRICING_QTY = "RepricingQty";
	public static final String A_LINE_TOTAL = "LineTotal";
	public static final String A_EXTENDED_PRICE = "ExtendedPrice";
	public static final String A_CHARGES = "Charges";
	public static final String A_DISCOUNT = "Discount";
	public static final String A_PROCEED_WITH_SINGLE_AVS_RESULT = "ProceedWithSingleAVSResult";
	public static final String A_TOTAL_NUMBER_OF_RECORDS = "TotalNumberOfRecords";
	public static final String A_ADDRESS_LINE_1 = "AddressLine1";
	/**** Order Modification *****/

	/**** Mash up elements and attributes *****/
	public static final String E_CAPTURE_PAYMENT = "CapturePayment";
	public static final String E_CONTAINER = "Container";
	public static final String E_COMPLEX_QUERY = "ComplexQuery";
	public static final String E_EXP = "Exp";
	public static final String E_AND = "And";
	public static final String E_OR = "Or";
	public static final String E_XML_DATA = "XMLData";
	public static final String E_APPEASEMENT_OFFERS = "AppeasementOffers";
	public static final String E_PERSONINFO_BILL_TO = "PersonInfoBillTo";
	public static final String E_PRICE_INFO = "PriceInfo";
	public static final String E_TO_ADDRESS = "ToAddress";

	public static final String A_TRACKING_NO = "TrackingNo";
	public static final String A_VALUE = "Value";
	public static final String A_QRY_TYPE = "QryType";
	public static final String A_DISCOUNT_PERCENT = "DiscountPercent";
	public static final String A_OFFER_AMOUNT = "OfferAmount";
	public static final String A_CURRENCY = "Currency";
	public static final String A_CUSTOMER_EMAIL_ID = "CustomerEMailID";
	public static final String A_MAIL_SENT = "MailSent";
	/**** Mash up elements and attributes *****/

}