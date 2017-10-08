package com.ibm.rds.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import com.ibm.rds.api.ApiInvoker;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;

/**
 * This class will be used as helper for Cellection Credit card UE.
 * @author IBM_ADMIN
 *
 */
public class PaymentProcessingUtil implements YIFCustomApi { 

	/**
	 * Instance of logger.
	 */
	private static YFCLogCategory log = YFCLogCategory.instance(PaymentProcessingUtil.class);
	
	private Properties props = null;
	
	private static final String className = "PaymentProcessingUtil";
	/**
	 * setProperties.
	 * @param props -props
	 */
	
	public void setProperties(Properties props) {
		this.props = props;
	}
	
	/**
	 * 
	 * @param env -YFSEnvironment
	 * @param tenderType -tenderType
	 * @return  expirationDate
	 */
	public static String getAuthExpirationDate(final YFSEnvironment env, final String tenderType, final String enterpriseCode) {
		
		Document docCommonCodeListOutput = null;
		Document docAuthExpDaysInput = null;
		long currentTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.AUTH_EXP_DATE_FORMAT);		
		
		try {
			log.verbose("Begin" + className + "getAuthExpirationDate ");			
			log.verbose("tenderType = " + tenderType);
			docAuthExpDaysInput = XmlUtils
					.createDocument("CommonCode");
			docAuthExpDaysInput.getDocumentElement().setAttribute(
					"CodeType", "AUTH_EXP_DAYS");
			docAuthExpDaysInput.getDocumentElement().setAttribute(
					"CodeValue", tenderType);
			docAuthExpDaysInput.getDocumentElement().setAttribute(
					"OrganizationCode",  enterpriseCode);
			docCommonCodeListOutput = ApiInvoker.invokeAPI(env,
					"getCommonCodeList",
					docAuthExpDaysInput);
			log.verbose("outXML of CommonCodeList=" + XmlUtils.getString(docCommonCodeListOutput));
			
			if (!YFCObject.isVoid(docCommonCodeListOutput)) {				

				String expirationDays = XPathUtil.getString(docCommonCodeListOutput, 
				"CommonCodeList/CommonCode/@CodeShortDescription");
				
				log.verbose("expirationDays = " + XmlUtils.getString(docCommonCodeListOutput));
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, Integer.parseInt(expirationDays));
				return sdf.format(cal.getTime());
			}
			 
				
		} catch (Exception e) {
			return sdf.format(currentTime);
		}
		return sdf.format(currentTime);
	}
	
	public static String getText(Document cybResponseDoc, String name) {
		return getText(cybResponseDoc.getDocumentElement(), name);
	}
	
	public static String getText(Element ele, String name) {
		if (ele != null && name != null && !(name.trim().equals(""))){
			Element childele = SCXmlUtil.getChildElement(ele, name);
			if (childele != null){
				log.debug("Element :: element name " + name + "childElement" + SCXmlUtil.getString(childele));
				return childele.getTextContent();
			}
		}
		log.debug("Element :: element name " + name + " is EMPTY in" + SCXmlUtil.getString(ele));
		return "";
	}
	
	/**
	 * Input date format from CyberSource is yyyy-MM-dd'T'HH:mm:ssZ e.g. '2009-09-30T06:48:42Z'
	 * To be converted to XML date format yyyyMMddHHmmss e.g. '20090930064842'
	 * @param time
	 * @return
	 */
	
	public static String getDateInXMLFormat(String time) {
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date date = (Date) formatter.parse(time);
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.AUTH_EXP_DATE_FORMAT);
			return sdf.format(date);
		} catch (ParseException e) {
			return "";
		}
	}
	
    /**
     * 
     * @param paymentInputStruct
     * @return
     * @throws Exception
     */
    
    public static Document convertInStructToXml(YFSExtnPaymentCollectionInputStruct paymentInputStruct)
    throws Exception
	{
	   
	    Document paymentInputDoc = null;
	    
		try {
			paymentInputDoc = XmlUtils.createDocument("Payment");
		    Element paymentRoot = paymentInputDoc.getDocumentElement();
			paymentRoot.setAttribute("BillToFirstName", paymentInputStruct.firstName);
			paymentRoot.setAttribute("BillToLastName", paymentInputStruct.lastName);				

		    paymentRoot.setAttribute("AuthorizationId", paymentInputStruct.authorizationId);
		    paymentRoot.setAttribute("BillToAddressLine1", paymentInputStruct.billToAddressLine1);
		    paymentRoot.setAttribute("BillToCity", paymentInputStruct.billToCity);
		    paymentRoot.setAttribute("BillToCountry", paymentInputStruct.billToCountry);
		    paymentRoot.setAttribute("BillToDayPhone", paymentInputStruct.billToDayPhone);
		    paymentRoot.setAttribute("BillToEmailId", paymentInputStruct.billToEmailId);
		    paymentRoot.setAttribute("BillToId", paymentInputStruct.billToId);
		    paymentRoot.setAttribute("BillToKey", paymentInputStruct.billTokey);
		    paymentRoot.setAttribute("BillToState", paymentInputStruct.billToState);
		    paymentRoot.setAttribute("BillToZipCode", paymentInputStruct.billToZipCode);
		    paymentRoot.setAttribute("bPreviouslyInvoked", String.valueOf(paymentInputStruct.bPreviouslyInvoked).toString());
		    paymentRoot.setAttribute("ChargeTransactionKey", paymentInputStruct.chargeTransactionKey);
		    paymentRoot.setAttribute("ChargeType", paymentInputStruct.chargeType);
		    paymentRoot.setAttribute("CreditCardExpirationDate", paymentInputStruct.creditCardExpirationDate);
		    paymentRoot.setAttribute("CreditCardName", paymentInputStruct.creditCardName);
		    paymentRoot.setAttribute("CreditCardNo", paymentInputStruct.creditCardNo);
		    paymentRoot.setAttribute("Currency", paymentInputStruct.currency);
		    paymentRoot.setAttribute("CustomerAccountNo", paymentInputStruct.customerAccountNo);
		    paymentRoot.setAttribute("CustomerPONo", paymentInputStruct.customerPONo);
		    paymentRoot.setAttribute("DocumentType", paymentInputStruct.documentType);
		    paymentRoot.setAttribute("EnterpriseCode", paymentInputStruct.enterpriseCode);
		    paymentRoot.setAttribute("MerchantId", paymentInputStruct.merchantId);
		    paymentRoot.setAttribute("OrderHeaderKey", paymentInputStruct.orderHeaderKey);
		    paymentRoot.setAttribute("OrderNo", paymentInputStruct.orderNo);
		    paymentRoot.setAttribute("PaymentReference1", paymentInputStruct.paymentReference1);
		    paymentRoot.setAttribute("PaymentReference2", paymentInputStruct.paymentReference2);
		    paymentRoot.setAttribute("PaymentReference3", paymentInputStruct.paymentReference3);
		    paymentRoot.setAttribute("PaymentType", paymentInputStruct.paymentType);
		    paymentRoot.setAttribute("RequestAmount", Double.toString(paymentInputStruct.requestAmount));
		    paymentRoot.setAttribute("ShipToAddressLine1", paymentInputStruct.shipToAddressLine1);
		    paymentRoot.setAttribute("ShipToCity", paymentInputStruct.shipToCity);
		    paymentRoot.setAttribute("ShipToCountry", paymentInputStruct.shipToCountry);
		    paymentRoot.setAttribute("ShipToDayPhone", paymentInputStruct.shipToDayPhone);
		    paymentRoot.setAttribute("ShipToEmailId", paymentInputStruct.shipToEmailId);
		    paymentRoot.setAttribute("ShipToFirstName", paymentInputStruct.shipToFirstName);
		    paymentRoot.setAttribute("ShipToId", paymentInputStruct.shipToId);
		    paymentRoot.setAttribute("ShipTokey", paymentInputStruct.shipTokey);
		    paymentRoot.setAttribute("ShipToLastName", paymentInputStruct.shipToLastName);
		    paymentRoot.setAttribute("ShipToState", paymentInputStruct.shipToState);
		    paymentRoot.setAttribute("ShipToZipCode", paymentInputStruct.shipToZipCode);
		    paymentRoot.setAttribute("SvcNo", paymentInputStruct.svcNo);
		    //  paymentInputDoc.appendChild(paymentRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return paymentInputDoc;
	}
		
    /**
     * 
     * @param rootElement
     * @param nodeName
     * @param nodeTextValue
     * @throws FactoryConfigurationError 
     * @throws ParserConfigurationException 
     * @throws Exception
     */
	public static Element addTextElement(Element rootElement, String nodeName, String nodeTextValue) 
	throws ParserConfigurationException, FactoryConfigurationError {	
		Element eleChild = XmlUtils.createDocument(nodeName).getDocumentElement();
		XMLUtil.createTextNode(eleChild.getOwnerDocument(), eleChild, nodeTextValue);
		XmlUtils.importElement(rootElement,eleChild);
		return eleChild;
	}

	public static String getOrderType(YFSEnvironment env, String orderHeaderKey) throws Exception {
		// TODO Auto-generated method stub
		
		String strOrderType = null;
		Document docGetOrdDetOut = null;
		Document inDoc = XMLUtil.createDocument(XMLLiterals.E_ORDER);
		Element eleInDoc = inDoc.getDocumentElement();
		eleInDoc.setAttribute(XMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);
		String apiTemplate = "<OrderList><Order OrderType=''></Order></OrderList>";
		env.setApiTemplate(Constants.API_GET_ORDER_LIST, XMLUtil.getDocument(apiTemplate));
		docGetOrdDetOut = CommonUtil.invokeAPI(env, Constants.API_GET_ORDER_LIST, inDoc);
		env.clearApiTemplate(Constants.API_GET_ORDER_LIST);
		Element eleGetOrdDetOut = docGetOrdDetOut.getDocumentElement();
		Element eleOrder = XMLUtil.getChildElement(eleGetOrdDetOut, XMLLiterals.E_ORDER);
		strOrderType = eleOrder.getAttribute(XMLLiterals.A_ORDER_TYPE);
		return strOrderType;
	}
}
