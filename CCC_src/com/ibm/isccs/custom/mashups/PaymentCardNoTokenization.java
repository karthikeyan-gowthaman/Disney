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

public class PaymentCardNoTokenization implements ISCUIMashup {

	private static YFCLogCategory _logger = YFCLogCategory
			.instance(PaymentCardNoTokenization.class);

	@Override
	public Object execute(Object arg0, SCUIMashupMetaData arg1, SCUIContext arg2) {

		Document docResponse = null;

		Element elePaymentMethod = (Element) arg0;
		String strPaymentType = elePaymentMethod.getAttribute("PaymentType");
		String strCardNo = elePaymentMethod.getAttribute("CardNumber");
		if ("CREDIT_CARD".equals(strPaymentType)) {

			// invoke IPG
			try {
				Document docTKN = XmlUtils.createDocument("TKN");
				Element eleTKN = docTKN.getDocumentElement();
				XmlUtils.createChild(eleTKN, "TGP").setTextContent(
						"IBMINDIATKG");
				XmlUtils.createChild(eleTKN, "ACN").setTextContent(strCardNo);
				XmlUtils.createChild(eleTKN, "PMT").setTextContent("CARD");

				OMODPaymentGatewayImpl omodPaymentGatewayImpl = new OMODPaymentGatewayImpl();
				docResponse = omodPaymentGatewayImpl.postTokenisation(null,
						docTKN);
			} catch (ParserConfigurationException | KeyManagementException
					| UnrecoverableKeyException | NoSuchAlgorithmException
					| KeyStoreException | CertificateException | IOException
					| YIFClientCreationException e) {

				_logger.error(e);

				try {
					docResponse = XmlUtils.createDocument("Exception");
					docResponse.getDocumentElement().setAttribute(
							"Exception Details", e.toString());
				} catch (ParserConfigurationException
						| FactoryConfigurationError e1) {

					_logger.error(e1);
				}

			}
		} else {

			// invoke SVS
			return docResponse;
		}

		return docResponse.getDocumentElement();
	}

}
