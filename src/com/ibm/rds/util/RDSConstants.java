package com.ibm.rds.util;


/**
 * This Will include all the XML Constants used in the implementation
 * @author IBM
 *
 */
public interface RDSConstants {
	
	public static final String STUB_MODE = "STUB_MODE";
	public static final String VERTEX_USER_NAME = "VertexUsername";
	public static final String VERTEX_USER_PASSWORD = "VertexPassword";
	public static final String RDS_GET_TAXES_WEB_SERVICE ="RDSVertexGetTaxesWebService";
	public static final String LinePriceInfo ="LinePriceInfo";
	public static final String UnitPrice ="UnitPrice";
	public static final String LineTaxes ="LineTaxes";
	public static final String LineTax ="LineTax";
	public static final String Tax ="Tax";
	public static final String TaxName ="TaxName";
	public static final String LINE_TAX ="LINE_TAX";
	public static final String ChargePerLine ="ChargePerLine";
	public static final String LINE_CHARGE_TAX ="LINE_CHARGE_TAX";
	public static final String ChargeAmount ="ChargeAmount";
	public static final String IsDiscount ="IsDiscount";
	public static final String YES ="Y";
	public static final String HeaderTaxes ="HeaderTaxes";
	public static final String HeaderTax ="HeaderTax";
	public static final String HEADER_TAX ="HEADER_TAX";
	public static final String OrderedQty ="OrderedQty";
	public static final String LineCharges ="LineCharges";
	public static final String LineCharge ="LineCharge";
	public static final String ChargeCategory ="ChargeCategory";
	public static final String ChargeName ="ChargeName";
	public static final String InvoicedChargePerLine ="InvoicedChargePerLine";
	public static final String InvoicedChargeAmount ="InvoicedChargeAmount";
	public static final String SO_DOCTYPE ="0001";
	
	
	/* Order Fulfillment - Start */
	public static final String ENTERPRISE_CODE = "TB_US";
	public static final String SALES_ORDER_DOCUMENT_TYPE = "0001";
	public static final String RETURN_ORDER_DOCUMENT_TYPE = "0003";
	public static final String INVOICE_PARTIAL = "INVOICE_PARTIAL";
	public static final String INVOICE_CANCEL = "INVOICE_CANCEL";
	public static final String INVOICE_COMPLETE = "INVOICE_COMPLETE";
	public static final String GET_ORDER_RELEASE_DETAILS_OUTPUT_TEMPLATE = "global/template/api/getOrderReleaseDetailsOutput.xml";
	public static final String API_GET_ORDER_RELEASE_DETAILS = "getOrderReleaseDetails";
	public static final String API_CONFIRM_SHIPMENT = "confirmShipment";
	public static final String API_GET_ORDER_DETAILS = "getOrderDetails";
	public static final String API_GET_CARRIER_SERVICE_OPTIONS_FOR_ORDERING = "getCarrierServiceOptionsForOrdering";
	public static final String API_CHANGE_ORDER = "changeOrder";
	public static final String API_UNSCHEDULE_ORDER = "unScheduleOrder";
	public static final String BACKLOG_EMAIL = "BACKLOG_EMAIL";
	public static final String BACKLOG_EXCEPTION = "BACKLOG_EXCEPTION";
	public static final String TB_BACKLOG_NOTIFICATION_ALERT_Q = "TBBackLogNotificationAlertQueue";
	public static final String BACKLOG_EXCEPTION_DESC = "Exception for Back Log";
	public static final String EGC = "EGC";
	public static final String SHIPPING = "SHIPPING";
	
	
	
	/**** Order fulfillment *****/
	public static final String GET_COMMON_CODE_LIST_TEMPLATE = "/global/template/api/getCommonCodeList.xml";
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
	public static final String A_CHARGE_CATEGORY_DESCRIPTION = "ChargeCategoryDescription";
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
	public static final String E_CHARGE_CATEGORY_DETAILS = "ChargeCategoryDetails";
	public static final String E_CHARGE_NAME_DETAILS = "ChargeNameDetails";
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
	public static final String E_TB_GC_SERIAL_NO_LIST = "TBGCSerialNoList";
	public static final String E_TB_GC_SERIAL_NO = "TBGCSerialNo";
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
	//public static final String E_LINE_TAX = "LineTax";
	public static final String E_DISCOUNT_CHARGES = "DiscountCharges";
	public static final String E_DISCOUNT_CHARGE = "DiscountCharge";
	public static final String E_NON_DISCOUNT_CHARGES = "NonDiscountCharges";
	public static final String E_NON_DISCOUNT_CHARGE = "NonDiscountCharge";

	public static final String A_ORDER_PURPOSE = "OrderPurpose";
	public static final String A_ORDER_TYPE = "OrderType";
	public static final String A_ORDER_DATE = "OrderDate";
	public static final String A_SUPPRESS_REPRICING_UE = "SuppressRepricingUE";
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
	public static final String A_AUTH_EXPIRY_DAYS = "TB_PMT_CARD_EXP_DAYS";
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
	public static final String A_TB_EMAIL_TYPE = "TBEmailType";
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
	//public static final String E_LINE_TAXES = "LineTaxes";
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
	public static final String A_USER_ENTERED_ADDRESS = "UserEnteredAddress";
	/**** Order Modification *****/

	/**** Mash up elements and attributes *****/
	public static final String E_CAPTURE_PAYMENT = "CapturePayment";
	public static final String E_APPEASEMENT_OFFERS = "AppeasementOffers";
	public static final String E_APPEASEMENT_OFFER = "AppeasementOffer";
	public static final String A_IS_VARIABLE_PERCENTAGE_OPTION = "IsVariablePercentOffer";
	public static final String A_OFFER_TYPE = "OfferType";
	public static final String A_DISCOUNT_PERCENT = "DiscountPercent";
	public static final String A_OFFER_AMOUNT = "OfferAmount";
	public static final String A_APPEASEMENT_OFFER = "AppeasementOffer";
	public static final String A_LINE_OFFER_AMOUNT = "LineOfferAmount";
	public static final String A_APPEASEMENT_AMOUNT = "AppeasementAmount";
	public static final String E_CONTAINER = "Container";
	public static final String E_COMPLEX_QUERY = "ComplexQuery";
	public static final String E_EXP = "Exp";
	public static final String E_AND = "And";
	public static final String E_OR = "Or";
	public static final String E_XML_DATA = "XMLData";
	public static final String E_PERSONINFO_BILL_TO = "PersonInfoBillTo";
	public static final String E_PRICE_INFO = "PriceInfo";
	public static final String E_TO_ADDRESS = "ToAddress";

	public static final String A_TRACKING_NO = "TrackingNo";
	public static final String A_VALUE = "Value";
	public static final String A_QRY_TYPE = "QryType";
	public static final String A_CURRENCY = "Currency";
	public static final String A_CUSTOMER_EMAIL_ID = "CustomerEMailID";
	public static final String A_MAIL_SENT = "MailSent";
	/**** Mash up elements and attributes *****/
	public static final String A_SHIPPING_CHARGE = "ShippingCharge";
	public static final String A_PREFFERED = "Preferred";
	public static final String A_TAX_DISCOUNT = "TaxDiscount";
	public static final String A_HEADER_OFFER_AMOUNT = "HeaderOfferAmount";

	//Start Return and Exchange Order
	public static final String A_LINKED_SOURCE_KEY = "LinkedSourceKey";
	public static final String A_PAYMENT_METHODS = "PaymentMethods";
	public static final String A_PAYMENT_METHOD = "PaymentMethod";
	public static final String A_FIRST_NAME = "FirstName";
	public static final String A_LAST_NAME = "Lastname";
	public static final String A_SVC_NO = "SvcNo";
	public static final String A_CREDIT_CARD_EXP_DATE = "CreditCardExpDate";
	public static final String A_CREDIT_CARD_NAME = "CreditCardName";
	public static final String A_CREDIT_CARD_NO = "CreditCardNo";
	public static final String A_PAYMENT_REFERENCE_1 = "PaymentReference1";
	public static final String A_PAYMENT_REFERENCE_2 = "PaymentReference2";
	public static final String A_PAYMENT_REFERENCE_3 = "PaymentReference3";
	public static final String A_PAYMENT_REFERENCE_4 = "PaymentReference4";
	public static final String A_PAYMENT_REFERENCE_5 = "PaymentReference5";
	public static final String A_PAYMENT_REFERENCE_6 = "PaymentReference6";
	public static final String A_PAYMENT_REFERENCE_7 = "PaymentReference7";
	public static final String A_DISPLAY_CREDIT_CARD_NO = "DisplayCreditCardNo";
	public static final String A_DISPLAY_PAYMENT_REFERENCE_1 = "DisplayPaymentReference1";
	public static final String A_DERIVED_FROM_ORDER_HEADER_KEY = "DerivedFromOrderHeaderKey";
	public static final String A_EXTN_IS_RETURNABLE = "ExtnIsReturnable";
	public static final String A_EXTN_RETURN_WINDOW = "ExtnReturnWindow";
	public static final String A_ENTERPRISE_KEY = "EnterpriseKey";
	public static final String E_MONITOR_CONSOLIATION = "MonitorConsolidation";
	public static final String A_FULFILLMENT_TYPE = "FulfillmentType";
	public static final String A_MIN_LINE_STATUS = "MinLineStatus";
	public static final String A_DERIVED_FROM_ORDER_LINE_KEY = "DerivedFromOrderLineKey";
	public static final String E_SCHEDULES = "Schedules";
	public static final String E_SCHEDULE = "Schedule";
	public static final String E_RETURN_ORDERS_FOR_EXCHANGE = "ReturnOrdersForExchange";
	public static final String E_RETURN_ORDER_FOR_EXCHANGE = "ReturnOrderForExchange";
	public static final String E_REFUND_PAYMENT_METHODS = "RefundPaymentMethods";
	public static final String A_UNIT_OF_MEASURE = "UnitOfMeasure";
	public static final String E_REFUND_FULFILLMENT_DETAILS = "RefundFulfillmentDetails";
	public static final String E_REFUND_PAYMENT_METHOD = "RefundPaymentMethod";
	public static final String A_SELECT_MEHOD = "SelectMethod";
	public static final String A_EXTN_GIFT_RECIPIENT_EMAILID = "ExtnGiftRecipientEmailID";
	public static final String A_RETURN_BY_GIFTRECIPIENT = "ReturnByGiftRecipient";
	public static final String A_RETURN_REASON_LONG_DESC="ReturnReasonLongDesc";
	public static final String TAX_NAME="TaxName";
	public static final String TAX="Tax";
	//End Return and Exchange Order

	//cash star activation
	public static final String A_SHIPMENT_NO = "ShipmentNo";

	// Migration Constants Start
	public static final String A_PIPE_LINE_ID = "PipelineId";
	public static final String A_PIPE_LINE_OWNERKEY = "PipelineOwnerKey";
	public static final String E_DERIVED_FROM = "DerivedFromOrder";
	public static final String E_ORDER_STATUS_CHANGE = "OrderStatusChange";
	public static final String A_BASE_DROP_STATUS = "BaseDropStatus";
	public static final String A_CHANGE_FOR_ALL_QTY = "ChangeForAllAvailableQty";
	public static final String E_DERIVED_FROM_ORDER_LINE = "DerivedFromOrderLine";
	// Migration Constants End
	
	//SAP ERP postings
	public static final	String E_ORDER_INVOICE = "OrderInvoice";
	public static final String GET_ORDER_INVOICE_DETAILS_TEMPLATE_WRAPPER = "global/template/api/getOrderInvoiceDetails_wrapper.xml";
	public static final String API_GET_ORDER_INVOICE_DETAIL = "getOrderInvoiceDetails";
	public static final String E_LINE_DETAIL = "LineDetail";
	public static final String ORDER_LINE = "OrderLine";
	public static final String E_LINE_TAXES = "LineTaxes";
	public static final String E_LINE_TAX = "LineTax"; 

	//SVS Stub constants
	public static final String RDS_SVS_STUB_RESPONSE_SERVICE ="RDSSVSStubResponseSyncService";
	public static final String AuthorizationId = "authorizationId";
	public static final String AuthCode = "authCode";
	public static final String TranReturnCode = "tranReturnCode";
	public static final String TranReturnFlag = "tranReturnFlag";
	public static final String RequestID = "requestID";
	public static final String RetryFlag = "retryFlag";
	public static final String InternalReturnCode = "internalReturnCode";
	public static final String TranReturnCode_settle = "tranReturnCode_settle";
	public static final String TranReturnFlag_settle = "tranReturnFlag_settle";
	public static final String InternalReturnCode_settle = "internalReturnCode_settle";
	public static final String RetryFlag_settle = "retryFlag_settle";
	public static final String SVS_STUB_MODE="SVS_STUB_MODE";
	public static final String AUTHORIZATION="AUTHORIZATION";
	public static final String CHARGE="CHARGE";		
	
}