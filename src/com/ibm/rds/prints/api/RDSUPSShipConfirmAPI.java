package com.ibm.rds.prints.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.rds.common.util.RDSCommonUtil;
import com.ibm.rds.common.util.RDSConstant;
import com.ibm.rds.common.util.RDSXMLLiterals;
import com.ibm.rds.common.util.RDSXmlUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class is invoked by service FossilUPSLabelsSyncService. This class has below logic
 * 1. Look up customer overrides to retrieve UPS Access information
 * 2. Concatenate access XML with the ship confirm request XML
 * 2. Invokes shipConfirm API from above XML by doing HTTP post
 * 3. Return the ship confirm response message
 * 
 */

public class RDSUPSShipConfirmAPI {

	private static final YFCLogCategory log = YFCLogCategory.instance(RDSUPSShipConfirmAPI.class.getName());

	
	/**
	 * Method 
	 * 1. Retrieves Access Request details from Property
	 * 2. Creates Access Request Doc
	 * 3. Removes Return Ship Confirm Request
	 * 4. Invokes Ship Confirm for Ship Label with the request
	 * 5. Invokes Ship Confirm for Return Label with the request
	 * 
	 * @param yfsEnv
	 * @param docInXML
	 * @return API output
	 * @throws Exception
	 * 
	 * Sample input to the method
	 * <ShipmentConfirmRequest>
    	<Request>
        	<RequestAction>ShipConfirm</RequestAction>
        	<RequestOption>nonvalidate</RequestOption>
    	</Request>
    	<Shipment>
        <Shipper>
            <Name>FOSSIL INC</Name>
            <Address/>
        </Shipper>
        <ShipTo>
            <CompanyName>Fname Lname</CompanyName>
            <AttentionName>Fname Lname</AttentionName>
            <PhoneNumber>2366506148</PhoneNumber>
            <Address>
                <AddressLine1>faDyg</AddressLine1>
                <City>Dallas</City>
                <CountryCode>US</CountryCode>
                <PostalCode>75041</PostalCode>
                <StateProvinceCode>TX</StateProvinceCode>
            </Address>
        </ShipTo>
        <ShipFrom>
            <CompanyName>FOSSIL INC</CompanyName>
            <Address/>
        </ShipFrom>
        <PaymentInformation>
            <Prepaid>
                <BillShipper>
                    <AccountNumber>R78A73</AccountNumber>
                </BillShipper>
            </Prepaid>
        </PaymentInformation>
        <Service>
            <Code>03</Code>
            <Description>Ground</Description>
        </Service>
        <Package>
            <PackagingType>
                <Code>02</Code>
                <Description>Customer Supplied</Description>
            </PackagingType>
            <Dimensions>
                <UnitOfMeasurement>
                    <Code>IN</Code>
                </UnitOfMeasurement>
                <Length>0.00</Length>
                <Width>0.00</Width>
                <Height>0.00</Height>
            </Dimensions>
            <PackageWeight>
                <Weight>10.00</Weight>
                <UnitOfMeasurement>
                    <Code>LBS</Code>
                </UnitOfMeasurement>
            </PackageWeight>
            <ReferenceNumber>
                <Code>00</Code>
                <Value>2015120916142060031</Value>
            </ReferenceNumber>
            <Description>FOSSIL PACKAGE</Description>
        </Package>
    	</Shipment>
    	<LabelSpecification>
        <LabelPrintMethod>
            <Code>GIF</Code>
            <Description>gif file</Description>
        </LabelPrintMethod>
        <HTTPUserAgent>Mozilla/4.5</HTTPUserAgent>
        <LabelImageFormat>
            <Code>GIF</Code>
            <Description>gif</Description>
        </LabelImageFormat>
    	</LabelSpecification>
		</ShipmentConfirmRequest>
	 */
	
	public Document shipConfirm(YFSEnvironment yfsEnv, Document docInXML) throws Exception {
		log.info("Inside shipConfirm***");
		log.info("Inside shipConfirm***");
		log.verbose("Inside shipConfirm***");
		
		String strUpsUsername = YFSSystem.getProperty((String)"UPS_USERNAME");
        String strUpsPassword = YFSSystem.getProperty((String)"UPS_PASSWORD");
        String strUpsLicenseNumber = YFSSystem.getProperty((String)"UPS_LICENSE_NUMBER");
        
        Boolean bIgnoreRoot = false;
		
        Document docAccessRequest = RDSXmlUtil.createDocument(RDSXMLLiterals.E_ACCESS_REQUEST);
        Element eleAccessRequest = docAccessRequest.getDocumentElement();
        RDSXmlUtil.appendTextChild(docAccessRequest, eleAccessRequest, RDSXMLLiterals.A_ACCESS_LICENSE_NUMBER, strUpsLicenseNumber, null);
        RDSXmlUtil.appendTextChild(docAccessRequest, eleAccessRequest, RDSXMLLiterals.A_USER_ID, strUpsUsername, null);
        RDSXmlUtil.appendTextChild(docAccessRequest, eleAccessRequest, RDSXMLLiterals.A_PASSWORD, strUpsPassword, null);
        
        Element eleShipConfirmRequest = docInXML.getDocumentElement();
        
        Document docShipLabelConfirmResponse = shipLabelConfirmRespone(yfsEnv, docInXML, eleShipConfirmRequest, docAccessRequest);
        log.info("docShipLabelConfirmResponse: " + SCXmlUtil.getString(docShipLabelConfirmResponse.getDocumentElement()));
        Document docReturnLabelConfirmResponse = returnLabelConfirmRespone(yfsEnv, docInXML, eleShipConfirmRequest, docAccessRequest);
        log.info("docReturnLabelConfirmResponse: " + SCXmlUtil.getString(docReturnLabelConfirmResponse.getDocumentElement()));
        Document docUPSShipConfirmResponse = RDSXmlUtil.addDocument(docShipLabelConfirmResponse, docReturnLabelConfirmResponse, bIgnoreRoot);
        
        log.info("just before calling stub code***");
		log.info("just before calling stub code***");
		log.verbose("just before calling stub code***");
        
        //Document docUPSShipConfirmResponse=RDSUPSConnect.connect(yfsEnv, docInXML);
        log.info("docUPSShipConfirmResponse: " + SCXmlUtil.getString(docUPSShipConfirmResponse.getDocumentElement()));
		return docUPSShipConfirmResponse;
	}
	
	/**
	 * Method 
	 * 1. Retrieves Ship Confirm Request details for Return Label
	 * 2. Invokes Ship Confirm for Return Label by doing HTTP Post
	 * 3. Returns Ship Confirm Response for Return Label
	 * 
	 * @param yfsEnv
	 * @param docInXML
	 * @param eleShipConfirmRequest
	 * @param docAccessRequest
	 * @return API output
	 * @throws Exception
	 * 
	 * 
	 */
	
	private Document returnLabelConfirmRespone(YFSEnvironment yfsEnv, Document docInXML, Element eleShipConfirmRequest, Document docAccessRequest) throws Exception {
		
		log.info("Inside returnLabelConfirmRespone method");
		Map<String, String> mapCommonCode = new HashMap<String, String>();
		String strUpsShipperNumber = YFSSystem.getProperty((String)"UPS_SHIPPER_NUMBER");
		
		Document docReturnShipConfirmResponse = null;
		
		try {
			Element eleShipment = RDSXmlUtil.getChildElement(eleShipConfirmRequest, RDSXMLLiterals.E_SHIPMENT);
			Element eleEnterpriseCode = RDSXmlUtil.getChildElement(eleShipment, RDSXMLLiterals.A_ENTERPRISE_CODE);
			String strEnterpriseCode = eleEnterpriseCode.getTextContent();
			log.info("strEnterpriseCode: " + strEnterpriseCode);
			
			Document docCommonCodeList = RDSCommonUtil.getCommonCodeList(yfsEnv, RDSConstant.RETURN_LABEL_ADDRESS, strEnterpriseCode);
			Element eleCommonCodeList = docCommonCodeList.getDocumentElement();
			//Invoke getCommonCodeList to retrieve Return To Address
			mapCommonCode = createCommonCodeMap(eleCommonCodeList, mapCommonCode);
						
			String strAddressLine1 = mapCommonCode.get(RDSXMLLiterals.A_ADDRESS_LINE_1);
			log.info("strAddressLine1: " + strAddressLine1);
			String strAddressLine2 = mapCommonCode.get(RDSXMLLiterals.A_ADDRESS_LINE_2);
			log.info("strAddressLine2: " + strAddressLine2);
			String strAddressLine3 = mapCommonCode.get(RDSXMLLiterals.A_ADDRESS_LINE_3);
			log.info("strAddressLine3: " + strAddressLine3);
			String strCity = mapCommonCode.get(RDSXMLLiterals.A_CITY);
			log.info("strCity: " + strCity);
			String strStateProvCode = mapCommonCode.get(RDSXMLLiterals.A_STATE_PROV_CODE);
			log.info("strStateProvCode: " + strStateProvCode);
			String strPostalCode = mapCommonCode.get(RDSXMLLiterals.A_POSTAL_CODE);
			log.info("strPostalCode: " + strPostalCode);
			String strCountryCode = mapCommonCode.get(RDSXMLLiterals.A_COUNTRY_CODE);
			log.info("strCountryCode: " + strCountryCode);
			
			String strCompanyName = mapCommonCode.get(RDSXMLLiterals.A_COMPANY_NAME);
			log.info("strCompanyName: " + strCompanyName);
			/*String strAttentionName = mapCommonCode.get(RDSXMLLiterals.A_ATTENTION_NAME);
			log.info("strAttentionName: " + strAttentionName);*/
			String strPhoneNo = mapCommonCode.get(RDSXMLLiterals.A_PHONE_NUMBER);
			log.info("strPhoneNo: " + strPhoneNo);
			
				
			
			Element elePackage = RDSXmlUtil.getChildElement(eleShipment, RDSXMLLiterals.E_PACKAGE);
			
			//For Return Label, get Ship To values from input and load them in Shipper and Ship From
			Element eleShipTo = RDSXmlUtil.getChildElement(eleShipment, RDSXMLLiterals.E_SHIP_TO);
			Element eleShipToAddress = RDSXmlUtil.getChildElement(eleShipTo, RDSXMLLiterals.E_ADDRESS);
			Element eleShipToCompanyName = RDSXmlUtil.getChildElement(eleShipTo, RDSXMLLiterals.A_COMPANY_NAME);
			String strShipToCompanyName = eleShipToCompanyName.getTextContent();
			/*Element eleShipToAttName = RDSXmlUtil.getChildElement(eleShipTo, RDSXMLLiterals.A_ATTENTION_NAME);
			String strShipToAttName = eleShipToAttName.getTextContent();*/
			Element eleShipToPhoneNumber = RDSXmlUtil.getChildElement(eleShipTo, RDSXMLLiterals.A_PHONE_NUMBER);
			String strShipToPhoneNumber = eleShipToPhoneNumber.getTextContent();
			
			Element eleShipper = RDSXmlUtil.getChildElement(eleShipment, RDSXMLLiterals.E_SHIPPER);
			RDSXmlUtil.removeChild(eleShipment, eleShipper);
			Element eleShipperReturn = RDSXmlUtil.createChild(eleShipment, RDSXMLLiterals.E_SHIPPER);
			RDSXmlUtil.appendTextChild(docInXML, eleShipperReturn, RDSXMLLiterals.A_NAME, strShipToCompanyName, null);
			/*RDSXmlUtil.appendTextChild(docInXML, eleShipperReturn, RDSXMLLiterals.A_ATTENTION_NAME, strShipToAttName, null);*/
			RDSXmlUtil.appendTextChild(docInXML, eleShipperReturn, RDSXMLLiterals.A_PHONE_NUMBER, strShipToPhoneNumber, null);
			RDSXmlUtil.appendTextChild(docInXML, eleShipperReturn, RDSXMLLiterals.A_SHIPPER_NUMBER, strUpsShipperNumber, null);
			Element eleShipperReturnAddress = RDSXmlUtil.createChild(eleShipperReturn, RDSXMLLiterals.E_ADDRESS);
			RDSXmlUtil.copyElement(docInXML, eleShipToAddress, eleShipperReturnAddress);
			log.info("eleShipperReturn: " + SCXmlUtil.getString(eleShipperReturn));
			
			Element eleShipFrom = RDSXmlUtil.getChildElement(eleShipment, RDSXMLLiterals.E_SHIP_FROM);
			RDSXmlUtil.removeChild(eleShipment, eleShipFrom);
			Element eleShipFromReturn = RDSXmlUtil.createChild(eleShipment, RDSXMLLiterals.E_SHIP_FROM);
			RDSXmlUtil.appendTextChild(docInXML, eleShipFromReturn, RDSXMLLiterals.A_COMPANY_NAME, strShipToCompanyName, null);
			/*RDSXmlUtil.appendTextChild(docInXML, eleShipFromReturn, RDSXMLLiterals.A_ATTENTION_NAME, strShipToAttName, null);*/
			RDSXmlUtil.appendTextChild(docInXML, eleShipFromReturn, RDSXMLLiterals.A_PHONE_NUMBER, strShipToPhoneNumber, null);
			Element eleShipFromReturnAddress = RDSXmlUtil.createChild(eleShipFromReturn, RDSXMLLiterals.E_ADDRESS);
			RDSXmlUtil.copyElement(docInXML, eleShipToAddress, eleShipFromReturnAddress);
			log.info("eleShipFromReturn: " + SCXmlUtil.getString(eleShipFromReturn));
			
			RDSXmlUtil.removeChild(eleShipment, eleShipTo);
			Element eleShipToReturn = RDSXmlUtil.createChild(eleShipment, RDSXMLLiterals.E_SHIP_TO);
			Element eleCompanyName = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturn, RDSXMLLiterals.A_COMPANY_NAME, strCompanyName, null);
			/*Element eleAttentionName = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturn, RDSXMLLiterals.A_ATTENTION_NAME, strAttentionName, null);*/
			Element elePhoneNo = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturn, RDSXMLLiterals.A_PHONE_NUMBER, strPhoneNo, null);
			
			Element eleShipToReturnAddress = RDSXmlUtil.createChild(eleShipToReturn, RDSXMLLiterals.E_ADDRESS);
			Element eleAddressLine1 = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturnAddress, RDSXMLLiterals.A_ADDRESS_LINE_1, strAddressLine1, null);
			Element eleAddressLine2 = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturnAddress, RDSXMLLiterals.A_ADDRESS_LINE_2, strAddressLine2, null);
			Element eleAddressLine3 = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturnAddress, RDSXMLLiterals.A_ADDRESS_LINE_3, strAddressLine3, null);
			Element eleCity = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturnAddress, RDSXMLLiterals.A_CITY, strCity, null);
			Element eleStateProvCode = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturnAddress, RDSXMLLiterals.A_STATE_PROV_CODE, strStateProvCode, null);
			Element elePostalCode = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturnAddress, RDSXMLLiterals.A_POSTAL_CODE, strPostalCode, null);
			Element eleCountryCode = RDSXmlUtil.appendTextChild(docInXML, eleShipToReturnAddress, RDSXMLLiterals.A_COUNTRY_CODE, strCountryCode, null);
			log.info("eleShipToReturn: " + SCXmlUtil.getString(eleShipToReturn));
			
			Element eleReturnService = RDSXmlUtil.createChild(eleShipment, RDSXMLLiterals.A_RETURN_SERVICE);
			RDSXmlUtil.appendTextChild(docInXML, eleReturnService, RDSXMLLiterals.A_CODE, RDSConstant.NINE, null);
			RDSXmlUtil.removeChild(eleShipment, eleEnterpriseCode);
			
			log.info("docInXMLReturnLabel: " + SCXmlUtil.getString(docInXML.getDocumentElement()));
			log.info("docAccessRequestReturnLabel: " + SCXmlUtil.getString(docAccessRequest.getDocumentElement()));			
			String strReturnShipConfirmResponse = httpPost(yfsEnv, docAccessRequest, docInXML);
			log.info("strReturnShipConfirmResponse: " + strReturnShipConfirmResponse);
			docReturnShipConfirmResponse = RDSXmlUtil.getDocument(strReturnShipConfirmResponse);
			
			
		} catch (Exception e) {
			
			throw e;
		}

		return docReturnShipConfirmResponse;
	}
	
	/**
	 * Method 
	 * 1. Retrieves Ship Confirm Request details for Ship Label
	 * 2. Invokes Ship Confirm for Ship Label by doing HTTP Post
	 * 3. Returns Ship Confirm Response for Ship Label
	 * 
	 * @param yfsEnv
	 * @param docInXML
	 * @param eleShipConfirmRequest
	 * @param docAccessRequest
	 * @return API output
	 * @throws Exception
	 * 
	 * 
	 */
	
	private Document shipLabelConfirmRespone(YFSEnvironment yfsEnv, Document docInXML, Element eleShipConfirmRequest, Document docAccessRequest) throws Exception{
		
		Map<String, String> mapCommonCode = new HashMap<String, String>();
		Map<String, String> mapCommonCodeOut = new HashMap<String, String>();
		Map<String, String> mapGetShipmentInfo = new HashMap<String, String>();
		Map<String, String> mapGetShipmentInfoOut = new HashMap<String, String>();
		String strUpsShipperNumber = YFSSystem.getProperty((String)"UPS_SHIPPER_NUMBER");
		Document docShipConfirmResponse = null;
		
		try {
			
			Element eleShipment = RDSXmlUtil.getChildElement(eleShipConfirmRequest, RDSXMLLiterals.E_SHIPMENT);
			Element elePackage = RDSXmlUtil.getChildElement(eleShipment, RDSXMLLiterals.E_PACKAGE);
			
			//Retrieve Order Number from Shipment Key
			Element eleReferenceNo = RDSXmlUtil.getChildElement(elePackage, RDSXMLLiterals.E_REFERENCE_NO);
			log.info("eleReferenceNo: " + SCXmlUtil.getString(eleReferenceNo));
			Element eleReferenceValue = RDSXmlUtil.getChildElement(eleReferenceNo, RDSXMLLiterals.E_VALUE);
			log.info("eleReferenceValue: " + SCXmlUtil.getString(eleReferenceValue));
			String strShipmentKey = eleReferenceValue.getTextContent();
			
			mapGetShipmentInfoOut = getOrderNo(yfsEnv,strShipmentKey, mapGetShipmentInfo);
			String strOrderNo = mapGetShipmentInfoOut.get(RDSXMLLiterals.A_ORDER_NO);
			
			RDSXmlUtil.removeChild(eleReferenceNo, eleReferenceValue);
			log.info("eleReferenceNoValRemoved: " + SCXmlUtil.getString(eleReferenceNo));

			RDSXmlUtil.appendTextChild(docInXML, eleReferenceNo, RDSXMLLiterals.E_VALUE, strOrderNo, null);
			log.info("eleReferenceNo: " + SCXmlUtil.getString(eleReferenceNo));
			
			String strEnterpriseCode = mapGetShipmentInfoOut.get(RDSXMLLiterals.A_ENTERPRISE_CODE);
			log.info("strEnterpriseCode: " + strEnterpriseCode);
			
			Document docCommonCodeList = RDSCommonUtil.getCommonCodeList(yfsEnv, RDSConstant.SHIP_FROM_ADDRESS, strEnterpriseCode);
			Element eleCommonCodeList = docCommonCodeList.getDocumentElement();
			log.info("eleCommonCodeList: " + SCXmlUtil.getString(eleCommonCodeList));
			
			//Invoke getCommonCodeList to retrieve Ship From Address
			mapCommonCodeOut = createCommonCodeMap(eleCommonCodeList, mapCommonCode);
						
			String strAddressLine1 = mapCommonCodeOut.get(RDSXMLLiterals.A_ADDRESS_LINE_1);
			log.info("strAddressLine1: " + strAddressLine1);
			String strCity = mapCommonCodeOut.get(RDSXMLLiterals.A_CITY);
			log.info("strCity: " + strCity);
			String strStateProvCode = mapCommonCodeOut.get(RDSXMLLiterals.A_STATE_PROV_CODE);
			log.info("strStateProvCode: " + strStateProvCode);
			String strPostalCode = mapCommonCodeOut.get(RDSXMLLiterals.A_POSTAL_CODE);
			log.info("strPostalCode: " + strPostalCode);
			String strCountryCode = mapCommonCodeOut.get(RDSXMLLiterals.A_COUNTRY_CODE);
			log.info("strCountryCode: " + strCountryCode);
			
			String strCompanyName = mapCommonCodeOut.get(RDSXMLLiterals.A_COMPANY_NAME);
			log.info("strCompanyName: " + strCompanyName);
			/*String strAttentionName = mapCommonCodeOut.get(RDSXMLLiterals.A_ATTENTION_NAME);
			log.info("strAttentionName: " + strAttentionName);*/
			String strPhoneNo = mapCommonCodeOut.get(RDSXMLLiterals.A_PHONE_NUMBER);
			log.info("strPhoneNo: " + strPhoneNo);
			
			//Append Address values retrieved from common code
			Element eleShipper = RDSXmlUtil.getChildElement(eleShipment, RDSXMLLiterals.E_SHIPPER);
			RDSXmlUtil.appendTextChild(docInXML, eleShipper, RDSXMLLiterals.A_SHIPPER_NUMBER, strUpsShipperNumber, null);
			Element eleShipperAddress = RDSXmlUtil.getChildElement(eleShipper, RDSXMLLiterals.E_ADDRESS);
			
			Element eleCompanyName = RDSXmlUtil.appendTextChild(docInXML, eleShipper, RDSXMLLiterals.A_COMPANY_NAME, strCompanyName, null);
			/*Element eleAttentionName = RDSXmlUtil.appendTextChild(docInXML, eleShipper, RDSXMLLiterals.A_ATTENTION_NAME, strAttentionName, null);*/
			Element elePhoneNo = RDSXmlUtil.appendTextChild(docInXML, eleShipper, RDSXMLLiterals.A_PHONE_NUMBER, strPhoneNo, null);
			
			Element eleAddressLine1 = RDSXmlUtil.appendTextChild(docInXML, eleShipperAddress, RDSXMLLiterals.A_ADDRESS_LINE_1, strAddressLine1, null);
			Element eleCity = RDSXmlUtil.appendTextChild(docInXML, eleShipperAddress, RDSXMLLiterals.A_CITY, strCity, null);
			Element eleStateProvCode = RDSXmlUtil.appendTextChild(docInXML, eleShipperAddress, RDSXMLLiterals.A_STATE_PROV_CODE, strStateProvCode, null);
			Element elePostalCode = RDSXmlUtil.appendTextChild(docInXML, eleShipperAddress, RDSXMLLiterals.A_POSTAL_CODE, strPostalCode, null);
			Element eleCountryCode = RDSXmlUtil.appendTextChild(docInXML, eleShipperAddress, RDSXMLLiterals.A_COUNTRY_CODE, strCountryCode, null);
			log.info("eleShipper: " + SCXmlUtil.getString(eleShipper));
			
			Element eleShipFrom = RDSXmlUtil.getChildElement(eleShipment, RDSXMLLiterals.E_SHIP_FROM);
			Element eleShipFromAddress = RDSXmlUtil.getChildElement(eleShipFrom, RDSXMLLiterals.E_ADDRESS);
			RDSXmlUtil.copyElement(docInXML, eleShipperAddress, eleShipFromAddress);
			RDSXmlUtil.appendTextChild(docInXML, eleShipFrom, RDSXMLLiterals.A_COMPANY_NAME, strCompanyName, null);
			/*RDSXmlUtil.appendTextChild(docInXML, eleShipFrom, RDSXMLLiterals.A_ATTENTION_NAME, strAttentionName, null);*/
			RDSXmlUtil.appendTextChild(docInXML, eleShipFrom, RDSXMLLiterals.A_PHONE_NUMBER, strPhoneNo, null);
			log.info("eleShipFrom: " + SCXmlUtil.getString(eleShipFrom));
			
			log.info("docInXML: " + SCXmlUtil.getString(docInXML.getDocumentElement()));
			log.info("docAccessRequest: " + SCXmlUtil.getString(docAccessRequest.getDocumentElement()));
			String strShipConfirmResponse = httpPost(yfsEnv, docAccessRequest, docInXML);
			log.info("strShipConfirmResponse: " + strShipConfirmResponse);
			
			//For Return Label Enterprise Code reference
			RDSXmlUtil.appendTextChild(docInXML, eleShipment, RDSXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode, null);
			
			docShipConfirmResponse = RDSXmlUtil.getDocument(strShipConfirmResponse);
			
		} catch (Exception e) {
			
			throw e;
		}
		
		
		return docShipConfirmResponse;
	}
	
	/**
	 * This method retrieves OrderNo from getShipmentList API based on Shipment Key
	 * @param mapGetShipmentInfo 
	 */
	
	private Map<String, String> getOrderNo(YFSEnvironment yfsEnv, String strShipmentKey, Map<String, String> mapGetShipmentInfo) throws Exception{
		
		String strOrderNumber = null;
		String strEnterpriseOrgCode = null;
		try {
			Document docGetShipmentListOut = null;
			
			Document docGetShipmentListIn = RDSXmlUtil.createDocument(RDSXMLLiterals.E_SHIPMENT);
			Element eleGetShipmentListIn = docGetShipmentListIn.getDocumentElement();
			log.info("strShipmentKey: " + strShipmentKey);
			eleGetShipmentListIn.setAttribute(RDSXMLLiterals.A_SHIPMENT_KEY, strShipmentKey);
			log.info("eleGetShipmentListIn: " + SCXmlUtil.getString(eleGetShipmentListIn));
		
			docGetShipmentListOut =  RDSCommonUtil.invokeAPI(yfsEnv,RDSConstant.GET_SHIPMENT_LIST_TEMPLATE, RDSConstant.API_GET_SHIPMENT_LIST, docGetShipmentListIn);
			Element eleShipment = RDSXmlUtil.getElementByXpath(docGetShipmentListOut, RDSXMLLiterals.XPATH_SHIPMENT);
			Element eleShipmentLine = RDSXmlUtil.getElementByXpath(docGetShipmentListOut, RDSXMLLiterals.XPATH_SHIPMENT_ORDER_NO);
			log.info("eleShipmentLine: " + SCXmlUtil.getString(eleShipmentLine));
			strOrderNumber = eleShipmentLine.getAttribute(RDSXMLLiterals.A_ORDER_NO);
			log.info("strOrderNumber: " + strOrderNumber);
			strEnterpriseOrgCode = eleShipment.getAttribute(RDSXMLLiterals.A_ENTERPRISE_CODE);
			log.info("strEnterpriseOrgCode: " + strEnterpriseOrgCode);
			mapGetShipmentInfo.put(RDSXMLLiterals.A_ORDER_NO,strOrderNumber);
			mapGetShipmentInfo.put(RDSXMLLiterals.A_ENTERPRISE_CODE,strEnterpriseOrgCode);
		} catch (Exception e) {
			
			throw e;
		}
		 return mapGetShipmentInfo;
	}

	private String httpPost(YFSEnvironment yfsEnv, Document docAccessRequest,
			Document docInXML) throws Exception {
		
		String strShipConfirmResponse = null;
		try{
			
			String strDocAccessRequest = RDSXmlUtil.getXMLString(docAccessRequest);
			log.info("strDocAccessRequest: " + strDocAccessRequest);
			String strShipConfirmRequest = RDSXmlUtil.getXMLString(docInXML);
			log.info("strShipConfirmRequest: " + strShipConfirmRequest);
			String strRequestInput = strDocAccessRequest + strShipConfirmRequest;
			log.info("strRequestInput: " + strRequestInput);
			log.info("strRequestInput: " + strRequestInput);
			log.info("strRequestInput::"+strRequestInput);
			String upsShipConfirmURL = YFSSystem.getProperty((String)"UPS_SHIPCONFIRM_URL");
			URL url = new URL(upsShipConfirmURL);	
			OutputStream outputStream = null;
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			// Setup HTTP POST parameters
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			
			
			outputStream = connection.getOutputStream();
			outputStream.write(strRequestInput.getBytes());
			outputStream.flush();
			outputStream.close();
			log.info("Http status = " + connection.getResponseCode() + " " + connection.getResponseMessage());
			
			strShipConfirmResponse = readURLConnection(connection);	
			log.info("strShipConfirmResponse: " + strShipConfirmResponse);
		
		} catch (Exception e) {
			throw e;
		}
		return strShipConfirmResponse;
	}
	
	/**
	 * This method reads all of the data from a URL connection to a String
	 */

	public static String readURLConnection(URLConnection uc) throws Exception {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			int letter = 0;			
			reader.readLine();
			while ((letter = reader.read()) != -1){
				buffer.append((char) letter);
			}
			reader.close();
		} catch (Exception e) {
			
			throw e;
		} finally {
			if(reader != null){
				reader.close();
				reader = null;
			}
		}
		return buffer.toString();
	}
	
	private Map<String, String> createCommonCodeMap(Element eleCommonCodeList, Map<String, String> mapCommonCode) {
		
		Element eleCommonCode = null;
		Iterator<Element> iterCommonCodes = RDSXmlUtil.getChildren(eleCommonCodeList);
		while (iterCommonCodes.hasNext()) {
			eleCommonCode = iterCommonCodes.next();
			String strCodeValue = eleCommonCode.getAttribute(RDSXMLLiterals.A_CODE_VALUE);
			log.info("strCodeValue: " + strCodeValue);
			String strCodeShortDesc = eleCommonCode.getAttribute(RDSXMLLiterals.A_CODE_SHORT_DESCRIPTION);
			log.info("strCodeShortDesc: " + strCodeShortDesc);
			mapCommonCode.put(strCodeValue,strCodeShortDesc);
		}
		return mapCommonCode;
	}
	
	
	
}
