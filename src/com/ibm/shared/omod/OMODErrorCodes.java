/*******************************************************************************
 * IBM Confidential
 *   OCO Source Materials
 *   5725-G69
 *   Copyright IBM Corporation 2011,2012
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 ******************************************************************************/
/**
 * 
 */
package com.ibm.shared.omod;

public interface OMODErrorCodes {

public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";

	
	public static final String OMOD_UNABLE_FETCH_ENTERPRISEKEY_FOR_HD = "OMOD0001";
	public static final String OMOD_UNABLE_FETCH_PROPERTIES_FROM_COMMONCODE = "OMOD0002";
	public static final String OMOD_MANDATORY_ORGANIZATIONCODE = "OMOD0003";
	
	
	public static final String OMOD_INVALID_COLLECTION_RESPONSE_CODE="OMOD0004";
	public static final String HTTP_ERROR_RESP_FROM_PG="OMOD0005" ;
	public static final String OMOD_NULL_INDOC_TO_HTTPPOST="OMOD0006";
	public static final String KEYSTORE_NOT_SET="OMOD0007";
	public static final String KEYSTORE_PASSWORD_NOT_SET="OMOD0008";
	public static final String OMOD_INVALID_REMOTE_URL="OMOD0009";
	public static final String OMOD_FIELD_MANDATORY="OMOD0010" ;
	public static final String OMOD_EXCEPTION_INITIALIZING_CONTEXT="OMOD0011" ;
	public static final String OMOD_DB_EXCEPTION="OMOD0012";
	
	//ava related taxes
	public static final String OMOD_AVA_ACCOUNTID_INVALID="OMOD0015" ;
	public static final String OMOD_AVA_LICENSEKEY_INVALID="OMOD0016" ;
	public static final String OMOD_AVA_ENDPOINT_URL_NOT_CONFIGURED="OMOD0017";
	public static final String OMOD_AVA_INVLID_TAX_RESULT="OMOD0018";
	public static final String OMOD_AVA_USER_ACCOUNT_NOT_AUTH="OMOD0019";
	public static final String OMOD_AVA_HTTP400="OMOD0020";
	public static final String OMOD_AVA_REQ_TIMEOUT="OMOD0021";
	public static final String OMOD_AVA_CONNECT_EXCEPTION="OMOD0022";
	public static final String OMOD_AVA_COMPANY_NOT_FOUND_ERROR="OMOD0023";
	public static final String OMOD_AVA_DOCUMENT_NOT_FOUND_ERROR="OMOD0024";
	public static final String OMOD_AVA_REGIONCODEERROR="OMOD0025";
	public static final String OMOD_AVA_COUNTRYERROR="OMOD0026";
	public static final String OMOD_AVA_TAXADDRESSERROR="OMOD0027";
	public static final String OMOD_AVA_TAXSVCERROR="OMOD0028";
	public static final String OMOD_AVA_JURISDICTIONERROR="OMOD0029";
	public static final String OMOD_AVA_JURISDICTIONNOTFOUNDERROR="OMOD0030";
	public static final String OMOD_AVA_ADDRESSRANGEERROR="OMOD0031";
	public static final String OMOD_AVA_INACTIVECOMPANYERROR="OMOD0032";
	public static final String OMOD_AVA_DESTINATIONJURISDICTIONERROR="OMOD0033";
	public static final String OMOD_AVA_ORIGINJURISDICTIONERROR="OMOD0034";
	public static final String OMOD_AVA_DUPLICATELINENOERROR="OMOD0035";
	public static final String OMOD_AVA_TAXREGIONERROR="OMOD0036";
	public static final String OMOD_AVA_ADDRESSERROR="OMOD0037";
	public static final String OMOD_AVA_INSUFFICIENTADDRESSERROR="OMOD0038";
	public static final String OMOD_AVA_POSTALCODEERROR="OMOD0039";
	public static final String OMOD_AVA_UNSUPPORTEDCOUNTRYERROR="OMOD0040";
	public static final String OMOD_AVA_DATERANGEERROR="OMOD0041";
	public static final String OMOD_AVA_REQUIREDERROR="OMOD0042";
	public static final String OMOD_AVA_UNIQUECONSTRAINTERROR="OMOD0043";
	public static final String OMOD_AVA_ADDRESSSTANDARDIZEDERROR="OMOD0044";
	public static final String OMOD_AVA_MULTIPLEADDRESSMATCHERROR="OMOD0045";
	public static final String OMOD_AVA_MULTIPLEADDRESSTIEERROR="OMOD0046";
	public static final String OMOD_AVA_ADDRESSNOSTREETERROR="OMOD0047";
	public static final String OMOD_AVA_ADDRESSUNKNOWNSTREETERROR="OMOD0048";
	public static final String OMOD_AVA_MAXCOUNTEXCEEDEDERROR="OMOD0049";
	public static final String OMOD_AVA_ACCOUNTEXPIREDERROR="OMOD0050";
	public static final String OMOD_AVA_ACCOUNTNOTFOUNDERROR="OMOD0051";
	public static final String OMOD_AVA_ACCOUNTIDERROR="OMOD0052";
	public static final String OMOD_AVA_BASESERVICEERROR="OMOD0053";
	public static final String OMOD_AVA_DATEERROR="OMOD0054";
	public static final String OMOD_DOC_STATUS_ERROR = "OMOD0055";
	public static final String OMOD_AVA_LENGTHERRORMESSAGE="OMOD0056";
	public static final String OMOD_AVA_RANGEERROR="OMOD0057";
	public static final String OMOD_AVA_RANGECOMPAREERROR="OMOD0058";
	public static final String OMOD_AVA_RANGESETERROR="OMOD0059";
	public static final String OMOD_AVA_PROFILEERROR="OMOD0061";
	public static final String OMOD_AVA_ADAPTERVERSIONERROR="OMOD0062";
	public static final String OMOD_AVA_EMAILERROR="OMOD0063";
	public static final String OMOD_AVA_FILTEREXPRESSIONERROR="OMOD0064";
	public static final String OMOD_AVA_SORTEXPRESSIONERROR="OMOD0065";
	public static final String OMOD_AVA_FIELDUNKNOWNERROR="OMOD0066";
	public static final String OMOD_AVA_ENTITYNOTFOUNDERROR="OMOD0067";
	public static final String OMOD_AVA_ACCESSDENIEDERROR="OMOD0068";
	public static final String OMOD_AVA_ENTITYDELETEERROR="OMOD0069";
	public static final String OMOD_AVA_DOCUMENTSAVEEXCEPTION="OMOD0070";
	public static final String OMOD_AVA_ADDRESSDATAACCESSEXCEPTION="OMOD0071";
	public static final String OMOD_AVA_ADDRESSDATAEXPIREDEXCEPTION="OMOD0072";
	public static final String OMOD_AVA_ADDRESSDEMOMODEEXCEPTION="OMOD0073";
	public static final String OMOD_AVA_ADDRESSINITIALIZATIONEXCEPTION="OMOD0074";
	public static final String OMOD_AVA_ADDRESSCANADAEXCEPTION="OMOD0075";
	public static final String OMOD_AVA_ADDRESSDPVOFFLINEEXCEPTION="OMOD0076";
	public static final String OMOD_AVA_ADDRESSVALIDATIONEXCEPTION="OMOD0077";
	public static final String OMOD_AVA_DATAACCESSEXCEPTION="OMOD0078";
	public static final String OMOD_AVA_ENTITYNOTFOUNDEXCEPTION="OMOD0079";
	
	public static final String OMOD_PROPERTIES_MISSING_FOR_ORGANIZATION="OMOD0004";
	public static final String OMOD_ATTRIBUTES_MISSING_EVENT_XML="OMODD0080";
	public static final String OMOD_PROPERTIES_MISSING_FOR_PAYMENT="OMOD0081";
	public static final String OMOD_OUTPUT_DIRECTORY_NOT_CONFIGURED = "OMOD0082";
}
