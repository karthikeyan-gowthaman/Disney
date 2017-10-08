package com.ibm.isccs.custom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.isccs.pca.om.business.payment.OMODPaymentGatewayImpl;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.sterlingcommerce.ui.web.framework.SCUIException;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.utils.SCUIContextHelper;
import com.sterlingcommerce.ui.web.framework.utils.SCUIJSONUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.ui.backend.servlets.BaseServlet;

public class IPGResponseServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	private static YFCLogCategory _logger = YFCLogCategory
			.instance(IPGResponseServlet.class);

	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		SCUIContext uiContext = SCUIContextHelper.getUIContext(request,
				response);

		Map<String, String> params = new HashMap<>();
		Enumeration paramsEnum = request.getParameterNames();

		while (paramsEnum.hasMoreElements()) {
			String paramName = (String) paramsEnum.nextElement();
			String paramValue = request.getParameter(paramName);
			params.put(paramName, paramValue);
		}

		Document docResponse = null;
		if ("SUU".equals(params.get("Redirection"))) {

			try {

				// invoke GET method web service to get Session Details
				Document docGetRequest = XmlUtils.createDocument("GetRequest");
				docGetRequest.getDocumentElement().setAttribute("SessionId",
						params.get("sessionId"));
				OMODPaymentGatewayImpl omodPaymentGatewayImpl = new OMODPaymentGatewayImpl();
				docResponse = omodPaymentGatewayImpl.getSessionDetails(null,
						docGetRequest);

				NodeList nlTRXList = docResponse.getElementsByTagName("TRX");
				int iLength = nlTRXList.getLength();
				for (int iCount = 0; iCount < iLength; iCount++) {

					// get transaction id.
					Element eleTRX = (Element) nlTRXList.item(iCount);
					String strXID = XmlUtils.getChildElement(eleTRX, "XID")
							.getTextContent();

					// invoke GET method web service to get authorization
					// details.
					docGetRequest = XmlUtils.createDocument("GetRequest");
					docGetRequest.getDocumentElement().setAttribute("XID",
							strXID);
					Document docAuthorizationDetails = omodPaymentGatewayImpl
							.getTransactionDetails(null, docGetRequest);

					// append authorization details
					Element eleAuthorizationDetails = (Element) eleTRX
							.getElementsByTagName("AUT").item(0);
					eleTRX.removeChild(eleAuthorizationDetails);
					eleAuthorizationDetails = (Element) docResponse.importNode(
							docAuthorizationDetails.getDocumentElement(), true);
					eleTRX.appendChild(eleAuthorizationDetails);
				}

			} catch (ParserConfigurationException | FactoryConfigurationError
					| KeyManagementException | UnrecoverableKeyException
					| NoSuchAlgorithmException | KeyStoreException
					| CertificateException | YIFClientCreationException e1) {

				_logger.error(e1);
				throw new SCUIException("Exception while processing request",
						e1);
			}
		} else if ("FAU".equals(params.get("Redirection"))) {

			try {

				Document docGetRequest = XmlUtils.createDocument("GetRequest");
				docGetRequest.getDocumentElement().setAttribute("SessionId",
						params.get("sessionId"));
				OMODPaymentGatewayImpl omodPaymentGatewayImpl = new OMODPaymentGatewayImpl();
				docResponse = omodPaymentGatewayImpl.getSessionDetails(null,
						docGetRequest);

			} catch (ParserConfigurationException | FactoryConfigurationError
					| KeyManagementException | UnrecoverableKeyException
					| NoSuchAlgorithmException | KeyStoreException
					| CertificateException | YIFClientCreationException e1) {

				_logger.error(e1);
				throw new SCUIException("Exception while processing request",
						e1);
			}
		} else {

			try {
				docResponse = XmlUtils.createDocument("SES");
				Element eleSES = docResponse.getDocumentElement();
				XmlUtils.createChild(eleSES, "SID").setTextContent(
						params.get("sessionId"));

			} catch (ParserConfigurationException | FactoryConfigurationError e1) {

				_logger.error(e1);
				throw new SCUIException("Exception while processing request",
						e1);
			}
		}

		response.setContentType("application/json");

		String requestURL = uiContext.getRequest().getRequestURL().toString();
		String origin = requestURL.substring(0,
				requestURL.indexOf("/", requestURL.indexOf("//") + 2));

		XmlUtils.createChild(docResponse.getDocumentElement(), "RED")
				.setTextContent(params.get("Redirection"));
		String jsonOut = SCUIJSONUtils.getJSONFromXML(docResponse, uiContext);

		response.setContentType("text/html");
		try {
			PrintWriter out = response.getWriter();
			out.write("<script type=\"text/javascript\">");
			out.write("parent.postMessage(JSON.stringify(" + jsonOut + "), '"
					+ origin + "');");
			out.write("</script>");
			out.flush();
			out.close();
		} catch (IOException e) {

			_logger.error(e);
			throw new SCUIException("Exception while processing request", e);
		}

	}
}