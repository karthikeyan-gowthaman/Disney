package com.ibm.isccs.custom.mashups;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.isccs.pca.om.business.payment.OMODPaymentGatewayImpl;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.extensions.ISCUIMashup;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;

public class IPGIntegration implements ISCUIMashup {

	private static YFCLogCategory _logger = YFCLogCategory
			.instance(IPGIntegration.class);

	@Override
	public Object execute(Object arg0, SCUIMashupMetaData arg1,
			SCUIContext context) {

		String csrfToken = (String) context.getRequest().getSession()
				.getAttribute("SCUI_CSRF_SECURITY_TOKEN");

		Element eleOrder = (Element) arg0;
		String strOrderNo = eleOrder.getAttribute("OrderNo");
		Element elePriceInfo = XmlUtils.getChildElement(eleOrder, "PriceInfo");
		Element elePersonInfoShipTo = XmlUtils.getChildElement(eleOrder,
				"PersonInfoShipTo");
		Element elePersonInfoBillTo = XmlUtils.getChildElement(eleOrder,
				"PersonInfoBillTo");
		Element eleChargeTransactionDetails = XmlUtils.getChildElement(
				eleOrder, "ChargeTransactionDetails");
		String strTotal = eleChargeTransactionDetails
				.getAttribute("RemainingAmountToAuth");

		Document docResponse = null;
		try {
			Document docTRX = XmlUtils.createDocument("TRX");
			Element eleTRX = docTRX.getDocumentElement();
			XmlUtils.createChild(eleTRX, "SVC").setTextContent(
					"CheckoutStartSession");
			XmlUtils.createChild(eleTRX, "PRJ").setTextContent("IBMINDIA");
			this.addChildElement(eleTRX, "CTY", elePersonInfoBillTo, "Country");
			XmlUtils.createChild(eleTRX, "COM").setTextContent("Phone");
			this.addChildElement(eleTRX, "ORD", eleOrder, "OrderNo");
			this.addChildElement(eleTRX, "CUR", elePriceInfo, "Currency");
			XmlUtils.createChild(eleTRX, "CVM").setTextContent("true");
			XmlUtils.createChild(eleTRX, "NET").setTextContent(strTotal);
			XmlUtils.createChild(eleTRX, "TAX").setTextContent("0");
			XmlUtils.createChild(eleTRX, "GRS").setTextContent(strTotal);

			Element eleCUS = XmlUtils.createChild(eleTRX, "CUS");
			this.addChildElement(eleCUS, "FNM", elePersonInfoBillTo,
					"FirstName");
			this.addChildElement(eleCUS, "LNM", elePersonInfoBillTo, "LastName");
			this.addChildElement(eleCUS, "AD1", elePersonInfoBillTo,
					"AddressLine1");
			this.addChildElement(eleCUS, "CIT", elePersonInfoBillTo, "City");
			this.addChildElement(eleCUS, "ZIP", elePersonInfoBillTo, "ZipCode");
			this.addChildElement(eleCUS, "CTY", elePersonInfoBillTo, "Country");
			this.addChildElement(eleCUS, "STA", elePersonInfoBillTo, "State");
			this.addChildElement(eleCUS, "EMA", elePersonInfoBillTo, "EMailID");
			this.addChildElement(eleCUS, "TEL", elePersonInfoBillTo, "DayPhone");
			this.addChildElement(eleCUS, "CID", elePersonInfoBillTo,
					"AddressID");
			this.addChildElement(eleCUS, "CNM", elePersonInfoBillTo, "Company");

			Element eleSHT = XmlUtils.createChild(eleTRX, "SHT");
			this.addChildElement(eleSHT, "FNM", elePersonInfoShipTo,
					"FirstName");
			this.addChildElement(eleSHT, "LNM", elePersonInfoShipTo, "LastName");
			this.addChildElement(eleSHT, "AD1", elePersonInfoShipTo,
					"AddressLine1");
			this.addChildElement(eleSHT, "CIT", elePersonInfoShipTo, "City");
			this.addChildElement(eleSHT, "ZIP", elePersonInfoShipTo, "ZipCode");
			this.addChildElement(eleSHT, "CTY", elePersonInfoShipTo, "Country");
			this.addChildElement(eleSHT, "STA", elePersonInfoShipTo, "State");
			this.addChildElement(eleSHT, "EMA", elePersonInfoShipTo, "EMailID");
			this.addChildElement(eleSHT, "TEL", elePersonInfoShipTo, "DayPhone");
			this.addChildElement(eleSHT, "CNM", elePersonInfoShipTo, "Company");

			String requestURL = context.getRequest().getRequestURL().toString();
			int indexOfFirstSlash = requestURL.indexOf("//");
			int indexOfSecondSlash = requestURL.indexOf("/",
					indexOfFirstSlash + 2);
			int indexOfThirdSlash = requestURL.indexOf("/",
					indexOfSecondSlash + 1);
			String strRedirectUrl = requestURL.substring(0, indexOfThirdSlash);
			strRedirectUrl = strRedirectUrl
					.concat("/IPGResponseServlet?scCSRFToken=")
					.concat(csrfToken).concat("&OrderNo=").concat(strOrderNo)
					.concat("&Redirection=");

			Element eleSES = XmlUtils.createChild(eleTRX, "SES");
			XmlUtils.createChild(eleSES, "ACT").setTextContent("Authorize");
			XmlUtils.createChild(eleSES, "LAN").setTextContent("en-US");
			XmlUtils.createChild(eleSES, "S3L").setTextContent("true");
			XmlUtils.createChild(eleSES, "SUU").setTextContent(
					strRedirectUrl.concat("SUU"));
			XmlUtils.createChild(eleSES, "FAU").setTextContent(
					strRedirectUrl.concat("FAU"));
			XmlUtils.createChild(eleSES, "CAU").setTextContent(
					strRedirectUrl.concat("CAU"));
			XmlUtils.createChild(eleSES, "MPY").setTextContent("VISA");
			XmlUtils.createChild(eleSES, "MPY").setTextContent("MASTERCARD");
			XmlUtils.createChild(eleSES, "MPY").setTextContent("DISCOVER");
			XmlUtils.createChild(eleSES, "MPY").setTextContent("AMEX");

			OMODPaymentGatewayImpl omodPaymentGatewayImpl = new OMODPaymentGatewayImpl();
			docResponse = omodPaymentGatewayImpl.postCheckoutStartSession(null,
					docTRX);
		} catch (ParserConfigurationException | FactoryConfigurationError
				| KeyManagementException | UnrecoverableKeyException
				| NoSuchAlgorithmException | KeyStoreException | IOException
				| YIFClientCreationException | CertificateException e) {

			_logger.error(e);

			try {
				docResponse = XmlUtils.createDocument("Exception");
				docResponse.getDocumentElement().setAttribute(
						"Exception Details", e.toString());
			} catch (ParserConfigurationException | FactoryConfigurationError e1) {

				_logger.error(e1);
			}

		}

		return docResponse.getDocumentElement();
	}

	private void addChildElement(Element eleParent, String strChildName,
			Element eleSource, String strAttribute) {

		String strValue = eleSource.getAttribute(strAttribute);
		if (!YFCCommon.isVoid(strValue)) {

			XmlUtils.createChild(eleParent, strChildName).setTextContent(
					strValue);
		}
	}
}
