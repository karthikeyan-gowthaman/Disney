package com.ibm.rds.order.ue;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.rds.util.RDSConstants;
import com.ibm.rds.util.SterlingUtil;
import com.ibm.rds.util.XPathUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;


public class RDSVertexIntegrationForTax implements  RDSConstants{

	private static YFCLogCategory logger = YFCLogCategory.instance(RDSVertexIntegrationForTax.class);
	private YIFApi api = null;

	Properties props;
	/**
	 * This method is overridden from interface.
	 * 
	 * @param properties
	 *            Properties set
	 */
	public void setProperties(Properties props) {
		logger.debug("props:"+props);
		this.props = props;
	}
	
	/**
	 * This method recalculates the header level and line level charges.
	 * 
	 * @param env - Environment Object
	 * @param inDoc - Input Document
	 * @return - Output Document 
	 * @throws YFSUserExitException
	 */
	public Document reCalculateTaxAndCharge(YFSEnvironment env, Document inDoc) throws YFSUserExitException{

		Document output=null;
		Document getCommonCodeListDoc = null;
		String vertexName = "";
		String vertexpassword = "";
		Element root=null;
		root=inDoc.getDocumentElement();



		String documentType = root.getAttribute("DocumentType");
		logger.debug("Inside latest Order REprice UE $$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		logger.debug("Inside latest Order REprice UE ****$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		if (logger.isDebugEnabled()) {
			logger.debug("RDSSORepricingUEImpl:orderReprice - Enter");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Input :: \n" + SCXmlUtil.getString(inDoc));
		}


		//instead of using hard coded value for stub mode from service, use commoncode values for the type of invocation for TAX( 00 - no integration, 01 - vertex, 02 - Avalara) 

		//Framing getCommonCodeList Input

		Document commonCodeInputDoc = SCXmlUtil.createDocument(RDSConstants.E_COMMON_CODE);
		commonCodeInputDoc.getDocumentElement().setAttribute(RDSConstants.A_ORGANIZATION_CODE, "Matrix-R");

		getCommonCodeListDoc = callGetCommonCodeList(env, commonCodeInputDoc, "TAX_CODE");

		if(!YFCObject.isVoid(getCommonCodeListDoc)){

			logger.verbose("getCommonCodeListDoc ****" + SCXmlUtil.getString(getCommonCodeListDoc));

			String strCodeShortDesc_TAX_CODE = SCXmlUtil.getXpathAttribute(getCommonCodeListDoc.getDocumentElement(), RDSConstants.XPATH_CODE_SHORT_DESC_FROM_COMMONCODE);

			logger.verbose("strCodeShortDesc_TAX_CODE ****" + strCodeShortDesc_TAX_CODE);


			if(!YFCObject.isVoid(strCodeShortDesc_TAX_CODE)){
				if("00".equals(strCodeShortDesc_TAX_CODE)){

					try {
						logger.debug("No Tax Integration ");
						inDoc = stampLinePriceInfo(inDoc,env);
						if(RDSConstants.SO_DOCTYPE.equalsIgnoreCase(documentType)){
							logger.debug("Order Doc Type is *********************************** "+documentType);
							inDoc = addLineCharge(inDoc);
						}
						inDoc = addTax(inDoc);
					} catch (Exception e) {

						logger.error(e.getMessage());
					}

				} else if ("01".equals(strCodeShortDesc_TAX_CODE)) {
					inDoc = stampLinePriceInfo(inDoc,env);
					getCommonCodeListDoc = callGetCommonCodeList(env, commonCodeInputDoc, "STUB_MODE");
					if (!YFCObject.isVoid(getCommonCodeListDoc)) {
						logger.verbose("getCommonCodeListDoc ****" + SCXmlUtil.getString(getCommonCodeListDoc));
						String strCodeShortDesc_STUB_MODE = SCXmlUtil.getXpathAttribute(
								getCommonCodeListDoc.getDocumentElement(),
								RDSConstants.XPATH_CODE_SHORT_DESC_FROM_COMMONCODE);
						logger.verbose("strCodeShortDesc_STUB_MODE ****" + strCodeShortDesc_STUB_MODE);
						if (!YFCObject.isVoid(strCodeShortDesc_STUB_MODE)) {
							if ("Y".equals(strCodeShortDesc_STUB_MODE)) {
								try {
									logger.debug("Stubbing");
									api = YIFClientFactory.getInstance().getApi();
									output = api.executeFlow(env, "RDSVertexTaxStubService", inDoc);
									//	output = XMLUtil.getDocument("/opt/ibm/STERLINGOMS95/Foundation/vertexResponse.xml");
									logger.debug("VertexResponse---"+SCXmlUtil.getString(output));
									inDoc = addChargesAndTax(inDoc, output);
								} catch (Exception e) {

									logger.error(e.getMessage());
								}
							} else {
								try {
									logger.debug("Tax Integration with Vertex System");
									vertexName = YFSSystem.getProperty(VERTEX_USER_NAME);
									vertexpassword = YFSSystem.getProperty(VERTEX_USER_PASSWORD);
									root.setAttribute(VERTEX_USER_NAME, vertexName);
									root.setAttribute(VERTEX_USER_PASSWORD, vertexpassword);
									// todo logic to calculate the unitprice yet
									// to be developed
									logger.debug("Invoke service RDSVertexGetTaxesWebService");
									api = YIFClientFactory.getInstance().getApi();
									output = api.executeFlow(env, RDS_GET_TAXES_WEB_SERVICE, inDoc);
									inDoc = addChargesAndTax(inDoc, output);
								} catch (Exception e) {
									logger.error(e.getMessage());
								}

							}
						}
					}
				}else if("02".equals(strCodeShortDesc_TAX_CODE)){

					try {
						logger.debug("Avalara Tax Integration - development in progress");
					} catch (Exception e) {

						logger.error(e.getMessage());
					}	

				}
			}
		}

		//

		logger.debug("Exiting the order reprice UE sysout $$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		logger.debug("exiting orderReprice &&&&&&&&&&&&&&&&&&&& "+SCXmlUtil.getString(inDoc));

		return inDoc;
	}



	private Document callGetCommonCodeList(YFSEnvironment env, Document commonCodeInputDoc, String CodeType) {
		commonCodeInputDoc.getDocumentElement().setAttribute(RDSConstants.A_CODE_TYPE, CodeType);
		Document getCommonCodeListDoc =null;
		try {
			getCommonCodeListDoc = SterlingUtil.invokeAPI (env, RDSConstants.GET_COMMON_CODE_LIST_TEMPLATE, "getCommonCodeList", commonCodeInputDoc);

		} catch (Exception e1) {
			logger.error(e1);
		}
		return getCommonCodeListDoc;
	}

	public Document addTax(Document inDoc){
		// TODO Auto-generated method stub
		logger.debug("Inside add tax the order reprice UE sysout");
		logger.debug("inside addTax method &&&&&&&&&&&&&&&&&&&& "+SCXmlUtil.getString(inDoc));
		Element root=inDoc.getDocumentElement();
		Element eleLineTaxes=null;
		Element eleLineTax=null;
		Element eleHeaderTax=null;
		String orderedQty  = "1";
		String unitPrice = "";
		double taxpercentage= 0.05 ;
		Element eleLinePriceInfo=null;
		Element eleLineCharge=null;
		NodeList nlOrderLine;
		try {
			nlOrderLine = XPathUtil.getNodeList(root, "/Order/OrderLines/OrderLine");
			int orderLineCount = nlOrderLine.getLength();
			for (int index = 0; index < orderLineCount; index++) {
				Element eleOrderLine = (Element) nlOrderLine.item(index);
				eleLinePriceInfo=SCXmlUtil.getChildElement(eleOrderLine,LinePriceInfo);
				logger.debug("Line price info "+SCXmlUtil.getString(eleLinePriceInfo));
				unitPrice=eleLinePriceInfo.getAttribute(UnitPrice);
				//TODO Uncomment the below line to get the dynamic order qty
				orderedQty=eleOrderLine.getAttribute(OrderedQty);
				//logic to calculate line tax
				double linetax = (Double.parseDouble(unitPrice) * Double.parseDouble(orderedQty))*taxpercentage;

				String strlinetax= Double.toString(linetax); 
				Element tempElementHolder = null;
				if(!YFCCommon.isVoid(eleOrderLine)){

					eleLineTaxes=SCXmlUtil.getChildElement(eleOrderLine,LineTaxes);
					if(YFCCommon.isVoid(eleLineTaxes)){
						eleLineTaxes=SCXmlUtil.createChild(eleOrderLine,LineTaxes);
						eleLineTax=SCXmlUtil.createChild(eleLineTaxes, LineTax);
						eleLineTax.setAttribute(Tax,strlinetax);
						eleLineTax.setAttribute(TaxName,LINE_TAX);
					}
					else if(!YFCCommon.isVoid(eleLineTaxes)){
						if(eleLineTaxes.hasChildNodes()){
							NodeList lineTaxList = eleLineTaxes.getElementsByTagName(LineTax);
							tempElementHolder = (Element) lineTaxList.item(0);
							if(null != tempElementHolder){
								String invoicedTax = tempElementHolder.getAttribute("InvoicedTax");
								if(Double.parseDouble(invoicedTax)>0){
									logger.debug("Order already has invoiced Tax value, skipping Tax logic "+invoicedTax);
									return inDoc;
								}
							}
						}
						eleLineTaxes = removeChildNodes(eleLineTaxes);
						eleLineTax=SCXmlUtil.createChild(eleLineTaxes, LineTax);
						eleLineTax.setAttribute(Tax,strlinetax);
						eleLineTax.setAttribute(TaxName,LINE_TAX);
					}
					Element lineChargesEle = SCXmlUtil.getChildElement(eleOrderLine,"LineCharges");
					List nlLineCharge = SCXmlUtil.getChildren(lineChargesEle,"LineCharge");
					int lineChargeCount = nlLineCharge.size();
					for (int j = 0; j < lineChargeCount; j++) {
						eleLineCharge = (Element) nlLineCharge.get(j);
						String chargePerLine=eleLineCharge.getAttribute(ChargePerLine);
						double linechargetax = Double.parseDouble(chargePerLine)*taxpercentage;
						String strlinechargetax= Double.toString(linechargetax);
						if(!YFCCommon.isVoid(eleLineTaxes)){
							eleLineTax=SCXmlUtil.createChild(eleLineTaxes,LineTax);
							eleLineTax.setAttribute(Tax,strlinechargetax);
							eleLineTax.setAttribute(TaxName,LINE_CHARGE_TAX); 
						}
					}
				}
			}
			String chargeAmount;
			NodeList nlHeaderCharges = XPathUtil.getNodeList(root, "/Order/HeaderCharges/HeaderCharge");
			String isdiscount;
			logger.debug("nlHeaderCharges.getLength()" +nlHeaderCharges.getLength());
			for (int i = 0; i < nlHeaderCharges.getLength(); i++) {

				Element eleHeadercharge= (Element) nlHeaderCharges.item(i);
				logger.debug("eleHeadercharge" + SCXmlUtil.getString(eleHeadercharge));
				if(!YFCCommon.isVoid(eleHeadercharge)){
					chargeAmount=eleHeadercharge.getAttribute(ChargeAmount);
					isdiscount=eleHeadercharge.getAttribute(IsDiscount);
					logger.debug("IsDiscount"+isdiscount);
					if(!YES.equalsIgnoreCase(isdiscount)){
						logger.debug("inside isdiscount");
						double headertax = (Double.parseDouble(chargeAmount))*taxpercentage;
						String strheadertax= Double.toString(headertax); 
						Element eleHeadertaxes=SCXmlUtil.getChildElement(root,HeaderTaxes);
						if(YFCCommon.isVoid(eleHeadertaxes)){
							logger.debug("inside HeaderTaxes is not there");
							eleHeadertaxes=SCXmlUtil.createChild(root,HeaderTaxes);
							eleHeaderTax=SCXmlUtil.createChild(eleHeadertaxes,HeaderTax);
							eleHeaderTax.setAttribute(Tax,strheadertax);
							eleHeaderTax.setAttribute(TaxName,HEADER_TAX);
						}
						else if(!eleHeadertaxes.hasChildNodes()){
							logger.debug("inside HeaderTax Ele is not there");
							eleHeaderTax=SCXmlUtil.createChild(eleHeadertaxes,HeaderTax);
							eleHeaderTax.setAttribute(Tax,strheadertax);
							eleHeaderTax.setAttribute(TaxName,HEADER_TAX);
						}

					}
				}

			}
		} catch (Exception exp) {

			logger.error(exp.getMessage());
		}
		logger.debug("Exiting the addTax method order reprice UE sysout $$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+SCXmlUtil.getString(root));
		logger.debug("exiting addTax "+SCXmlUtil.getString(root));
		return inDoc;
	}

	/**
	 * @param eleLineTaxes
	 * @return 
	 */
	private Element removeChildNodes(Element eleLineTaxes) {
		while (eleLineTaxes.hasChildNodes()){
			eleLineTaxes.removeChild(eleLineTaxes.getFirstChild());
		}
		return eleLineTaxes;
	}

	private Document stampLinePriceInfo(Document inDoc, YFSEnvironment env ){

		logger.debug("Inside stampLinePriceInfo input doc &&&&&&&&&&&&&&&&&&&& "+SCXmlUtil.getString(inDoc));
		String enterpriseCode= SCXmlUtil.getAttribute(inDoc.getDocumentElement(),"EnterpriseCode");
		Element orderLinesInEle = SCXmlUtil.getChildElement(inDoc.getDocumentElement(), "OrderLines");
		NodeList childList = orderLinesInEle.getChildNodes();
		Element orderLineInEle = null;
		for(int i=0;i<childList.getLength();i++){

			orderLineInEle = (Element) childList.item(i);
			Element itemEle = SCXmlUtil.getChildElement(orderLineInEle, "Item");
			String itemId = SCXmlUtil.getAttribute(itemEle, "ItemID");

			Document inputDoc = SCXmlUtil.createDocument("Item");
			inputDoc.getDocumentElement().setAttribute("CallingOrganizationCode", enterpriseCode);
			Element barcodeEle = inputDoc.createElement("BarCode");
			barcodeEle.setAttribute("BarCodeData", itemId);
			barcodeEle.setAttribute("BarCodeType", "Item");
			inputDoc.getDocumentElement().appendChild(barcodeEle);
			Element contextualInfoEle = inputDoc.createElement("ContextualInfo");
			contextualInfoEle.setAttribute("OrganizationCode", enterpriseCode);
			barcodeEle.appendChild(contextualInfoEle);
			Document itemListDoc = null;

			logger.debug("getCompleteItemList api output inputDoc "+SCXmlUtil.getString(inputDoc));
			try {

				env.setApiTemplate("getCompleteItemList", this.getTemplateDoc());
				api = YIFClientFactory.getInstance().getApi();
				itemListDoc = api.invoke(env, "getCompleteItemList", inputDoc);
				env.clearApiTemplate("getCompleteItemList");
				logger.debug("getCompleteItemList api output &&&&&&&&&&&&&&&&&&&& "+SCXmlUtil.getString(itemListDoc));

			} catch (YFSException e) {

				logger.error(e.getMessage());
			} catch (RemoteException e) {

				logger.error(e.getMessage());
			} catch (YIFClientCreationException e) {

				logger.error(e.getMessage());
			}
			
			Element eleLinePriceInfo = SCXmlUtil.getChildElement(orderLineInEle, "LinePriceInfo");

			Element ItemTempEle = SCXmlUtil.getChildElement(itemListDoc.getDocumentElement(), "Item");
			Element computedTempEle = SCXmlUtil.getChildElement(ItemTempEle, "ComputedPrice");
			String unitPrice = computedTempEle.getAttribute("UnitPrice");
			String strlistPrice = computedTempEle.getAttribute("ListPrice");

			Element linePriceInfoEle = inDoc.createElement("LinePriceInfo");
			linePriceInfoEle.setAttribute("UnitPrice", unitPrice);
			linePriceInfoEle.setAttribute("ListPrice", strlistPrice);
			
			SCXmlUtil.removeNode(eleLinePriceInfo);
			SCXmlUtil.importElement(orderLineInEle, linePriceInfoEle);
			//orderLineInEle.appendChild(linePriceInfoEle);
		}
		logger.debug("exiting stampLinePriceInfo return doc &&&&&&&&&&&&&&&&&&&& "+SCXmlUtil.getString(inDoc));
		return inDoc;
	}

	private Document getTemplateDoc(){

		Document templateDoc = SCXmlUtil.createDocument("ItemList");
		Element templateItemEle = templateDoc.createElement("Item");
		templateDoc.getDocumentElement().appendChild(templateItemEle);
		Element computedEle = templateDoc.createElement("ComputedPrice");
		computedEle.setAttribute("UnitPrice", "");
		computedEle.setAttribute("ListPrice", "");
		templateItemEle.appendChild(computedEle);
		return templateDoc;
	}

	public Document addLineCharge(Document inDoc) {
		// TODO Auto-generated method stub

		logger.debug("inside addLineCharge method &&&&&&&&&&&&&&&&&&&& "+ SCXmlUtil.getString(inDoc));
		Element root = inDoc.getDocumentElement();
		NodeList nlOrderLine;
		try {
			nlOrderLine = XPathUtil.getNodeList(root,
					"/Order/OrderLines/OrderLine");
			int orderLineCount = nlOrderLine.getLength();

			for (int index = 0; index < orderLineCount; index++) {
				Element eleOrderLine = (Element) nlOrderLine.item(index);
				Element eleLineCharges = SCXmlUtil.getChildElement(eleOrderLine, LineCharges);
				if(null == eleLineCharges){
					eleLineCharges = SCXmlUtil.createChild(eleOrderLine, LineCharges);
				}
				NodeList nlLineCharge = XPathUtil.getNodeList(eleOrderLine,	"LineCharges/LineCharge");
				int lineChargeCount = nlLineCharge.getLength();
				if (lineChargeCount < 1) {

					Element eleLineCharge = SCXmlUtil.createChild(eleLineCharges, LineCharge);
					eleLineCharge.setAttribute(ChargeAmount, "5.00");
					eleLineCharge.setAttribute(ChargeCategory, "Shipping");
					eleLineCharge.setAttribute(ChargeName, "Shipping Charge");
					//eleLineCharge.setAttribute(InvoicedChargeAmount, "5.00");
					eleLineCharge.setAttribute(ChargePerLine, "5.00");
					//eleLineCharge.setAttribute(InvoicedChargePerLine, "5.00");

				}

			}
		}

		catch (TransformerException e) {

			logger.error(e.getMessage());
		}

		return inDoc;
	}





	private Document addChargesAndTax(Document inDoc, Document output) {

		logger.debug("addChargesAndTax -- input &&&&&&&&&&&&&&&&&&&& "+ SCXmlUtil.getString(inDoc)); 
		logger.debug("addChargesAndTax -- output &&&&&&&&&&&&&&&&&&&& "+ SCXmlUtil.getString(output)); 

		Element inRoot = inDoc.getDocumentElement();
		Element outputRoot = output.getDocumentElement();

		Element inHeaderCharges = null, inHeaderTaxes = null, outHeaderCharges = null, outHeaderTaxes = null;

		if(!YFCObject.isVoid(inRoot) && !YFCObject.isVoid(outputRoot)){
			inHeaderCharges = SCXmlUtil.getChildElement(inRoot, RDSConstants.E_HEADER_CHARGES);
			if(!YFCObject.isVoid(inHeaderCharges)){
				SCXmlUtil.removeNode(inHeaderCharges);
				outHeaderCharges = SCXmlUtil.getChildElement(outputRoot, RDSConstants.E_HEADER_CHARGES);
				if(!YFCObject.isVoid(outHeaderCharges)){
					SCXmlUtil.importElement(inRoot, outHeaderCharges);
				}
			}

			inHeaderTaxes = SCXmlUtil.getChildElement(inRoot, RDSConstants.HeaderTaxes);
			if(!YFCObject.isVoid(inHeaderTaxes)){
				SCXmlUtil.removeNode(inHeaderTaxes);
				outHeaderTaxes = SCXmlUtil.getChildElement(outputRoot, RDSConstants.HeaderTaxes);
				if(!YFCObject.isVoid(outHeaderTaxes)){
					SCXmlUtil.importElement(inRoot, outHeaderTaxes);
				}
			}
		}


		Element InputOrderLines = SCXmlUtil.getChildElement(inRoot, "OrderLines");
		NodeList nlInputOrderLine = InputOrderLines.getElementsByTagName("OrderLine");

		Element OutputOrderLines = SCXmlUtil.getChildElement(outputRoot, "OrderLines");
		NodeList nlOutputOrderLine = OutputOrderLines.getElementsByTagName("OrderLine");

		Element inLineCharges = null, inLineTaxes = null, outLineCharges = null, outLineTaxes = null;

		if (!YFCCommon.isVoid(nlInputOrderLine)) {
			for (int iOrderlineCount = 0; iOrderlineCount < nlInputOrderLine.getLength(); iOrderlineCount++) {
				Element iorderLine = (Element) nlInputOrderLine.item(iOrderlineCount);
				String iPrimeLineNo = iorderLine.getAttribute("PrimeLineNo");
				for (int oOrderlineCount = 0; oOrderlineCount < nlOutputOrderLine.getLength(); oOrderlineCount++) {
					Element oorderLine = (Element) nlOutputOrderLine.item(oOrderlineCount);
					String oPrimeLineNo = oorderLine.getAttribute("PrimeLineNo");
					if (iPrimeLineNo.equals(oPrimeLineNo)) {
						inLineCharges = SCXmlUtil.getChildElement(iorderLine, LineCharges);
						if(!YFCObject.isVoid(inLineCharges)){
							SCXmlUtil.removeNode(inLineCharges);
							outLineCharges= SCXmlUtil.getChildElement(oorderLine, LineCharges);
							if(!YFCObject.isVoid(outLineCharges)){
								SCXmlUtil.importElement(iorderLine, outLineCharges);
							}
						}

						inLineTaxes = SCXmlUtil.getChildElement(iorderLine, LineTaxes);
						if(!YFCObject.isVoid(inLineTaxes)){
							SCXmlUtil.removeNode(inLineTaxes);
							outLineTaxes= SCXmlUtil.getChildElement(oorderLine, LineTaxes);
							if(!YFCObject.isVoid(outLineTaxes)){
								SCXmlUtil.importElement(iorderLine, outLineTaxes);
							}
						}

						break;	
					}

				}
			}
		}

		logger.debug("addChargesAndTax &&&&&&&&&&&&&&&&&&&& return "+ SCXmlUtil.getString(inDoc)); 
		return inDoc;
	}
}
