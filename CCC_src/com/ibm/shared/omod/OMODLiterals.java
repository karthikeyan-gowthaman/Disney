/*******************************************************************************
 * IBM Confidential
 *   OCO Source Materials
 *   5725-G69
 *   Copyright IBM Corporation 2011,2012
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 ******************************************************************************/
package com.ibm.shared.omod;

import com.yantra.shared.ycp.YCPConstants;

public interface OMODLiterals  extends YCPConstants {

	public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";

	public static final String OMOD_COLL_RESPONSE_CODE="CollectionResponseCode" ;
	public static final String OMOD_COLL_RESPONSE_APPROVED="Approved";
	public static final String OMOD_COLL_RESPONSE_DECLINE="Declined" ;
	public static final String OMOD_COLL_RESPONSE_BANK_HOLD="Hold" ;
	public static final String OMOD_COLL_RESPONSE_SRVC_UNAVL="ServiceUnavailable" ;
	public static final String OMOD_INVALID_COLLECTION_RESPONSE_CODE="InvalidResponse" ;
	public static final String ENTERPRISE_CODE="EnterpriseCode" ;
	public static final String DOCUMENT_TYPE="DocumentType" ;
	public static final String HTTP_POST_REQUEST="POST";
	public static final String HTTP_GET_REQUEST="GET";
	public static final String OMOD_REQUEST_URL="RequestURL";
	public static final String OMOD_KEYSTORE_PATH="KeystorePath";
	public static final String OMOD_KEYSTORE_PW="KeystorePassword";
	public static final String OMOD_PROXY_SERVICE_PARAMETER_NAME="ServiceName";
	public static final String OMOD_PG_CARD_AUTHORIZE="CardAuthorize" ;
	public static final String OMOD_EVENT_PUBLISHED_TO_WC="Sent_To_WC";
	public static final String OMOD_PG_COLLECT="Collect" ;
	public static final String OMOD_PG_CARD_VOID="CardVoid" ;
	public static final String OMOD_PG_REFUND="Refund";
	// Event Name 
	public static final String PAYMENT_EXECUTION_SERVICE="OMOD_ExecuteCreditCardCollectionProxy";
	public static final String OMOD_PAYMENT_FAILURE_EVENT="CHARGE_FAILED_XML";
	public static final String PAYMENT_EXECUTION_TRANID="PAYMENT_EXECUTION";
	public static final String PAYMENT_GATEWAY_AUTH_URL="aut/";
	public static final String PAYMENT_GATEWAY_TRAN_URL="tran/";
	public static final String OMOD_CREDIT_CARD_PAYMENT_GRP="CREDIT_CARD";
	public static final String PAYMENT_VERIFICATION_SERVICE="OMOD_PaymentVerificationServiceProxy" ;
	public static final String OMOD_PAYMENT_REFERENCE="PAYMENT_REFERENCE";
	public static final String OMOD_CHARGE_TYPE_AUTH="AUTHORIZATION";
	public static final String OMOD_DB_EXCEPTION="DBEXCEPTION";
	public static final String OMOD_IS_HTTP_ERROR="IsHttpError";
	
	//Property Management
	public static final String OM_COMMONCODE_PROPERTY_CODETYPE = "CAAS_PROPERTIES";
	public static final String OM_COMMONCODE_BILLING_PROPERTY_CODETYPE = "CAAS_BILLING_PROP";
	//AvaTax related
	public static final String OM_AVA_PROFILE_NAME = "CAAS";
	public static final String OM_AVA_CLIENT = "9.1.2";
	public static final String GUEST = "Guest";
	//charge categories related
	public static final String SHIPPING_CHARGE_CAT = "Shipping";
	public static final String SHIPPING_DISCOUNT_CHARGE_CAT = "ShippingDiscount";
	//shipping tax item id and desc for avatax
	public static final String OMOD_AVA_SHIPPING_ITEM_ID = "SHIP";
	public static final String OMOD_AVA_SHIPPING_ITEM_DESC = "Shipping";
	
	public static final String OMOD_CANADA_COUNTRY_CODE="CA";
	public static final String OMOD_INV_WRITE_TO_QUEUE_SERVICE="OMOD_WriteInvoiceDetailsToQueue";
	
	//PayPal
	public static final String PAYPAL_COLLECTION_SERVICE="OMOD_PayPalCollectionOtherServiceProxy";
	public static final String OMOD_PG_PAYPAL_RE_AUTHORIZE="PayPalReauthorize";
	public static final String OMOD_PAYMENT_TYPE="PaymentType";
	public static final String OMOD_PAYPAL_PAYMENT_TYPE="PAYPAL";
	public static final String OMOD_PAYPAL_COLLECTION_REAUTH_FAILED="ReAuthFailed";
	public static final String OMOD_PAYPAL_REAUTH_FAILED_MSG="Full Authorization of the PayPal payment request for the unshipped items on this order has expired. " +
			"There are 23 days remaining in which payment can be requested but payment is no longer guaranteed by PayPal.";
	public static final String OMOD_AUTH_CHARGE_TYPE = "AUTHORIZATION";
	public static final int OMOD_PAYPAL_AUTH_EXPIRATION_DAYS = 28;
	public static final String OMOD_PAYPAL_ORDER_CANCEL_MSG="Order is Cancelled since orders with PayPal payment method must be shipped within 29 days.";
	public static final String OMOD_PAYPAL_ORDER_CANCEL_MSG_BUNDLE_KEY="OMOD_PAYPAL_ORDER_CANCEL";
	public static final String OMOD_PAYPAL_REAUTH_FAILURE_MSG_BUNDLE_KEY="OMOD_PAYPAL_REAUTH_FAILURE";
		
	public static final String OMOD_PG_ERROR_0="Service succeeded";
	public static final String OMOD_PG_ERROR_10001="Authorization declined";
	public static final String OMOD_PG_ERROR_20000="Empty input";
	public static final String OMOD_PG_ERROR_20001= "Syntax error in XML";
	public static final String OMOD_PG_ERROR_20002= "Unexpected element";
	public static final String OMOD_PG_ERROR_20003= "Unexpected attribute";
	public static final String OMOD_PG_ERROR_20004= "Unexpected content";
	public static final String OMOD_PG_ERROR_20005= "Invalid element";
	public static final String OMOD_PG_ERROR_20006= "Invalid value for element";
	public static final String OMOD_PG_ERROR_20007= "Syntax error in XML";
	public static final String OMOD_PG_ERROR_20008= "Input value is too short";
	public static final String OMOD_PG_ERROR_20009= "Input value is too long";
	public static final String OMOD_PG_ERROR_20012= "Decimal value has too many digits";
	public static final String OMOD_PG_ERROR_20013= "Decimal value has too many decimals";
	public static final String OMOD_PG_ERROR_20014= "Zero value is not allowed";
	public static final String OMOD_PG_ERROR_20015= "Positive value is not allowed";
	public static final String OMOD_PG_ERROR_20016= "Negative value is not allowed";
	public static final String OMOD_PG_ERROR_20017= "Invalid input";
	public static final String OMOD_PG_ERROR_22014="Card expired";
	public static final String OMOD_PG_ERROR_30001="Invalid card number";
	public static final String OMOD_PG_ERROR_30005="Invalid CVC";
	public static final String OMOD_PG_ERROR_30002="Invalid card number";
	public static final String OMOD_PG_ERROR_40001="Authorization has already been used";
	public static final String OMOD_PG_ERROR_40002="Authorization has expired";
	public static final String OMOD_PG_ERROR_40003="Capture amount exceeds high limit";
	public static final String OMOD_PG_ERROR_40005="No valid authorization found";
	public static final String OMOD_PG_ERROR_40006="No valid charge found";
	public static final String OMOD_PG_ERROR_40010="Authorization has been cancelled";
	public static final String OMOD_PG_ERROR_90001="Technical error";
	public static final String OMOD_PG_ERROR_90002="Transaction timed out";
		
	//GiftCard
	public static final String OMOD_GIFTCARD_PAYMENT_TYPE="GIFT_CARD";
	public static final String GIFTCARD_COLLECTION_SERVICE="OMOD_ExecuteStoreValuedCardCollectionProxy";
	public static final String OMOD_GIFTCARD_AUTHORIZE="GiftCardAuthorize";
	
	//Stub
	public static final String CHARGE_TYPE="ChargeType";
	public static final String CREDIT_CARD_NO="CreditCardNo";
	public static final String SVC_NO="SvcNo";
	public static final String AUTHORIZATION="AUTHORIZATION";
	public static final String CHARGE="CHARGE";
	public static final String RETURN="RETURN";
}
