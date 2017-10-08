package com.ibm.rds.shipment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.rds.util.RDSConstants;
import com.ibm.rds.util.SterlingUtil;
import com.ibm.rds.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/*
 * 
 * @author IBM
 * This class checks shipmentConfirmationStatus for either INVOICE_PARTIAL, INVOICE_COMPLETE or INVOICE_CANCEL and based on these values check for isShipmentDelayed
 * and waive off header charges and header taxes based on the mapping of ExtnShippingGroupId attribute from order line and chargeName
 *
 */

public class RDSProcessShipmentsAPI {
	
	private static YFCLogCategory logger = YFCLogCategory.instance(RDSProcessShipmentsAPI.class);

	Element getOrderReleaseDetailsOutEle;
	String extnShippingGroupId;
	String releaseNo;
	
	/**
	 * 
	 * @param env
	 * @param inDoc
	 * @throws Exception
	 */

	public void processShipments(YFSEnvironment env, Document inDoc) throws Exception {

		if(inDoc != null){
			Element inEle = inDoc.getDocumentElement();
			logger.debug("inEle:"+SCXmlUtil.getString(inEle));
			String shipmentConfirmationStatus;
			shipmentConfirmationStatus = inEle.getAttribute(RDSConstants.A_SHIPMENT_CONFIRMATION_STATUS);
			logger.debug("shipmentConfirmationStatus:"+shipmentConfirmationStatus);
			
			if(XMLUtil.isVoid(shipmentConfirmationStatus))
				throw new YFSException("Invalid Shipment Confirmation Status");
			else{
				if(RDSConstants.INVOICE_PARTIAL.equals(shipmentConfirmationStatus)){
					if(isShipmentDelayed(env, inEle)){
						/*Invoke OOB changeOrder API to 
                   	      - update shipping charges for the corresponding shipped line to 0$
                          - update Order Level Notes to Shipping charges $<Charge amount> Waived off since   
                            Order is delayed from WM.. 
						*/
						nullifyTaxesChargesAddNotes(env);
					}
					/*Invoke OOB confirmShipment API with the input*/
					SterlingUtil.invokeAPI(env, RDSConstants.API_CONFIRM_SHIPMENT, inDoc);
				}
				else if(RDSConstants.INVOICE_CANCEL.equals(shipmentConfirmationStatus)){
					/*Prepare Input for UnscheduleOrder using the lines present in the Input xml
		       		  Invoke OOB UnscheduleOrder API*/
					Document getOrderReleaseDetailsOutDoc = prepareOrderReleaseDetails(env, inEle);
					logger.debug("In INVOICE_CANCEL getOrderReleaseDetailsOutDoc:"+SCXmlUtil.getString(getOrderReleaseDetailsOutDoc));
					if(getOrderReleaseDetailsOutDoc != null){
						getOrderReleaseDetailsOutEle = getOrderReleaseDetailsOutDoc.getDocumentElement();
					    unscheduleLines(env, shipmentConfirmationStatus, inEle);
					}
				}
				else if(RDSConstants.INVOICE_COMPLETE.equals(shipmentConfirmationStatus)){
					if(isShipmentDelayed(env, inEle)){
						 /*Invoke OOB changeOrder API to 
                 	      - update shipping charges for the corresponding shipped line to 0$
                          - update Order Level Notes to Shipping charges $<Charge amount> Waived off since   
                            Order is delayed from WM. 
						 */
						 nullifyTaxesChargesAddNotes(env);
					}
					/*Invoke OOB confirmShipment API with the input*/
					SterlingUtil.invokeAPI(env, RDSConstants.API_CONFIRM_SHIPMENT, inDoc);					
					
					unscheduleLines(env, shipmentConfirmationStatus, inEle);					
				}
			}
		}
	}

	private boolean isShipmentDelayed(YFSEnvironment env, Element eleShipment){
		try {
			Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment, RDSConstants.E_SHIPMENT_LINES);
			Element eleShipmentLine = (Element)SCXmlUtil.getChildElement(eleShipmentLines, RDSConstants.E_SHIPMENT_LINE);
			String orderNo = eleShipmentLine.getAttribute(RDSConstants.A_ORDER_NO);
			String enterpriseCode = eleShipmentLine.getAttribute(RDSConstants.A_ENTERPRISE_CODE);
			String documentType = eleShipment.getAttribute(RDSConstants.A_DOCUMENT_TYPE);
			Document getOrderReleaseDetailsOutDoc = prepareOrderReleaseDetails(env, eleShipment);
			logger.debug("getOrderReleaseDetailsOutDoc:"+SCXmlUtil.getString(getOrderReleaseDetailsOutDoc));
			if(getOrderReleaseDetailsOutDoc != null){
				getOrderReleaseDetailsOutEle = getOrderReleaseDetailsOutDoc.getDocumentElement();
				Element eleOrderLines = SCXmlUtil.getChildElement(getOrderReleaseDetailsOutEle, RDSConstants.E_ORDER_LINES);
				Element eleOrderLine;
				Element eleExtn;
				extnShippingGroupId = null;
				
				Element eleOrder = SCXmlUtil.getChildElement(getOrderReleaseDetailsOutEle, RDSConstants.E_ORDER);

				List<Element> eleOrderLineList = SCXmlUtil.getChildren(eleOrderLines, RDSConstants.E_ORDER_LINE);
				Iterator<Element> itrEleOrderLine = eleOrderLineList.iterator();

				List<Element> eleShipmentLineList = SCXmlUtil.getChildren(eleShipmentLines, RDSConstants.E_SHIPMENT_LINE);
				Iterator<Element> itrEleShipmentLine = eleShipmentLineList.iterator();

				Element eleHeaderCharges = SCXmlUtil.getChildElement(eleOrder, RDSConstants.E_HEADER_CHARGES);
				List<Element> eleHeaderChargesList = SCXmlUtil.getChildren(eleHeaderCharges, RDSConstants.E_HEADER_CHARGE);

				List<Element> getCarrierServiceOptionsForOrderingList = new ArrayList<Element>();
				eleShipmentLine = null;
				while(itrEleShipmentLine.hasNext()){
					eleShipmentLine = itrEleShipmentLine.next();
					if(releaseNo == null){
						releaseNo = eleShipmentLine.getAttribute(RDSConstants.A_RELEASE_NO);
						logger.debug("releaseNo:"+releaseNo);
					}
					while(itrEleOrderLine.hasNext()){
						eleOrderLine = itrEleOrderLine.next();
						logger.debug("eleOrderLine:"+SCXmlUtil.getString(eleOrderLine));
						logger.debug("eleShipmentLine:"+SCXmlUtil.getString(eleShipmentLine));
						if(eleShipmentLine.getAttribute(RDSConstants.A_PRIMELINE_NO).equals(eleOrderLine.getAttribute(RDSConstants.A_PRIMELINE_NO)) /*&& eleShipmentLine.getAttribute("ItemID").equals(eleOrderLine.getAttribute("ItemID"))*/){
							eleExtn = SCXmlUtil.getChildElement(eleOrderLine, RDSConstants.E_EXTN);
							logger.debug("eleExtn:"+SCXmlUtil.getString(eleExtn));
							if(eleExtn != null && extnShippingGroupId == null){
								extnShippingGroupId = eleExtn.getAttribute(RDSConstants.A_EXTN_SHIPPING_GROUP_ID);
								logger.debug("extnShippingGroupId:"+extnShippingGroupId);
							}
							getCarrierServiceOptionsForOrderingList.add(eleOrderLine);
							break;
						}
					}
				}

				return validateLines(env, eleHeaderChargesList, extnShippingGroupId, getCarrierServiceOptionsForOrderingList, orderNo, enterpriseCode, documentType);

			}
		} catch (Exception e) {
			logger.error(e);
			throw new YFSException("Error in isShipmentDelayed method of TBProcessShipmentsAPI");
		}

		return false;
	}

	private boolean validateLines(YFSEnvironment env,List<Element> eleHeaderChargesList, String extnShippingGroupId, List<Element> getCarrierServiceOptionsForOrderingList, String orderNo, String enterpriseCode, String documentType){
		Iterator<Element> itrHeaderChargesList = eleHeaderChargesList.iterator();
		Element eleHeaderCharge;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date deliveryStartDate;
		Date expectedDeliveryDate = null;

		while(itrHeaderChargesList.hasNext()){
			eleHeaderCharge = itrHeaderChargesList.next();
			if(RDSConstants.SHIPPING.equals(eleHeaderCharge.getAttribute(RDSConstants.A_CHARGE_CATEGORY)) && extnShippingGroupId.equals(eleHeaderCharge.getAttribute(RDSConstants.A_CHARGE_NAME))){
				if("0.00".equals(eleHeaderCharge.getAttribute(RDSConstants.A_CHARGE_AMOUNT)) || eleHeaderCharge.getAttribute(RDSConstants.A_CHARGE_AMOUNT).equals(eleHeaderCharge.getAttribute("InvoicedChargeAmount"))){
					return false;
				}
				else {
					logger.debug("validateLines in else");
					//logic to find if shipment was delayed
					Document getCarrierServiceOptionsForOrderingInDoc = SCXmlUtil.createDocument(RDSConstants.E_ORDER);
					Element getCarrierServiceOptionsForOrderingInEle = getCarrierServiceOptionsForOrderingInDoc.getDocumentElement();
					getCarrierServiceOptionsForOrderingInEle.setAttribute(RDSConstants.A_DOCUMENT_TYPE, documentType);
					getCarrierServiceOptionsForOrderingInEle.setAttribute(RDSConstants.A_ENTERPRISE_CODE, enterpriseCode);
					getCarrierServiceOptionsForOrderingInEle.setAttribute(RDSConstants.A_ORDER_NO, orderNo);

					Element eleOrderLines = SCXmlUtil.createChild(getCarrierServiceOptionsForOrderingInEle, RDSConstants.E_ORDER_LINES);
					Element eleOrderLine;
					List<Element> eleOrderLinesList;
					Iterator<Element> itrEleOrderLineList;

					Element eleOrderStatuses;
					Element eleOrderStatus;
					List<Element> eleOrderStatusesList;
					Iterator<Element> itrEleOrderStatusesList;


					Element eleCarrierServiceList;
					Element eleCarrierService;
					List<Element> listEleCarrierServiceList;
					Iterator<Element> itrEleCarrierServiceList;


					Iterator<Element> itrGetCarrierServiceOptionsForOrderingList = getCarrierServiceOptionsForOrderingList.iterator();
					while(itrGetCarrierServiceOptionsForOrderingList.hasNext()){
						eleOrderLine = itrGetCarrierServiceOptionsForOrderingList.next();
						SCXmlUtil.importElement(eleOrderLines, eleOrderLine);
					}
					
					logger.debug("validateLines getCarrierServiceOptionsForOrderingInDoc:"+SCXmlUtil.getString(getCarrierServiceOptionsForOrderingInDoc));

					try {
						Document getCarrierServiceOptionsForOrderingOutDoc = SterlingUtil.invokeAPI(env, RDSConstants.API_GET_CARRIER_SERVICE_OPTIONS_FOR_ORDERING, getCarrierServiceOptionsForOrderingInDoc);
						if(getCarrierServiceOptionsForOrderingOutDoc != null){
							Element getCarrierServiceOptionsForOrderingOutEle = getCarrierServiceOptionsForOrderingOutDoc.getDocumentElement();
								logger.debug("getCarrierServiceOptionsForOrderingOutEle:"+SCXmlUtil.getString(getCarrierServiceOptionsForOrderingOutEle));
								eleOrderLines = SCXmlUtil.getChildElement(getCarrierServiceOptionsForOrderingOutEle, RDSConstants.E_ORDER_LINES);
								eleOrderLinesList = SCXmlUtil.getChildren(eleOrderLines, RDSConstants.E_ORDER_LINE);
								itrEleOrderLineList = eleOrderLinesList.iterator();
								while(itrEleOrderLineList.hasNext()){
									eleOrderLine = itrEleOrderLineList.next();
									if(eleOrderLine != null){
										eleCarrierServiceList = SCXmlUtil.getChildElement(eleOrderLine, RDSConstants.E_CARRIER_SERVICE_LIST);
										eleOrderStatuses = SCXmlUtil.getChildElement(eleOrderLine, RDSConstants.E_ORDER_STATUSES);
										eleOrderStatusesList = SCXmlUtil.getChildren(eleOrderStatuses, RDSConstants.E_ORDER_STATUS);
										itrEleOrderStatusesList = eleOrderStatusesList.iterator();
										while(itrEleOrderStatusesList.hasNext()){
											eleOrderStatus = itrEleOrderStatusesList.next();
											if(!XMLUtil.isVoid(eleOrderStatus.getAttribute(RDSConstants.A_SHIP_NODE))){
												logger.debug("validateLines orderStatus shipNode:"+eleOrderStatus.getAttribute(RDSConstants.A_SHIP_NODE));
												expectedDeliveryDate = sdf.parse(eleOrderStatus.getAttribute(RDSConstants.A_EXPECTED_DELIVERY_DATE));
												logger.debug("validateLines expectedDeliveryDate:"+expectedDeliveryDate);
												break;
											}
										}
										if(eleCarrierServiceList != null){
											listEleCarrierServiceList = SCXmlUtil.getChildren(eleCarrierServiceList, RDSConstants.E_CARRIER_SERVICE);
											itrEleCarrierServiceList = listEleCarrierServiceList.iterator();
											while(itrEleCarrierServiceList.hasNext()){
												eleCarrierService = itrEleCarrierServiceList.next();
												logger.debug("validateLines orderLine carrierServiceCode:"+eleOrderLine.getAttribute(RDSConstants.A_CARRIER_SERVICE_CODE));
												logger.debug("validateLines orderLine carrierService:"+eleCarrierService.getAttribute(RDSConstants.A_CARRIER_SERVICE_CODE));
												if(eleOrderLine.getAttribute(RDSConstants.A_CARRIER_SERVICE_CODE).equals(eleCarrierService.getAttribute(RDSConstants.A_CARRIER_SERVICE_CODE))){
													// compare DeliveryStartDate and ExpectedDeliveryDate
													deliveryStartDate = sdf.parse(eleCarrierService.getAttribute(RDSConstants.A_DELIVERY_START_DATE));
													logger.debug("validateLines deliveryStartDate:"+deliveryStartDate);
													if(deliveryStartDate != null && expectedDeliveryDate != null && deliveryStartDate.after(expectedDeliveryDate)){
															logger.debug("validateLines deliveryStartDate > expectedDeliveryDate");
															return true;
													}
													break;
												}
											}
										}
									}
								}
						}
					} catch (Exception e) {
						logger.error(e);
						throw new YFSException("Error occured in method validateLines");
					}
					return false;	
				}			   		
			}
		}
		return false;
	}

	private void nullifyTaxesChargesAddNotes(YFSEnvironment env){
		Document changeOrderInDoc =  SCXmlUtil.createDocument(RDSConstants.E_ORDER);
		Element changeOrderInEle = changeOrderInDoc.getDocumentElement();
		Element eleHeaderCharge;
		Element eleHeaderTax;

		changeOrderInEle.setAttribute(RDSConstants.A_ORDER_NO, SCXmlUtil.getChildElement(getOrderReleaseDetailsOutEle, RDSConstants.E_ORDER).getAttribute(RDSConstants.A_ORDER_NO));
		changeOrderInEle.setAttribute(RDSConstants.A_ENTERPRISE_CODE, getOrderReleaseDetailsOutEle.getAttribute(RDSConstants.A_ENTERPRISE_CODE));
		changeOrderInEle.setAttribute(RDSConstants.A_DOCUMENT_TYPE, getOrderReleaseDetailsOutEle.getAttribute(RDSConstants.A_DOCUMENT_TYPE));
		changeOrderInEle.setAttribute(RDSConstants.A_OVERRIDE, RDSConstants.YES);
		
		Element eleInHeaderCharges = SCXmlUtil.createChild(changeOrderInEle, RDSConstants.E_HEADER_CHARGES);
		Element eleInHeaderTaxes = SCXmlUtil.createChild(changeOrderInEle, RDSConstants.E_HEADER_TAXES);
		
		Element eleOrder = SCXmlUtil.getChildElement(getOrderReleaseDetailsOutEle, RDSConstants.E_ORDER);

		Element eleHeaderCharges = SCXmlUtil.getChildElement(eleOrder, RDSConstants.E_HEADER_CHARGES);
		Element eleHeaderTaxes = SCXmlUtil.getChildElement(eleOrder, RDSConstants.E_HEADER_TAXES);

		List<Element> listEleHeaderCharge = SCXmlUtil.getChildren(eleHeaderCharges, RDSConstants.E_HEADER_CHARGE);
		List<Element> listEleHeaderTax = SCXmlUtil.getChildren(eleHeaderTaxes, RDSConstants.E_HEADER_TAX);

		Iterator<Element> itrListEleHeaderTax = listEleHeaderTax.iterator();
		Iterator<Element> itrListEleHeaderCharge = listEleHeaderCharge.iterator();

		Double totalShippingCharges = 0.00;
		Double totalShippingTaxes = 0.00;

		while(itrListEleHeaderCharge.hasNext()){
			eleHeaderCharge = itrListEleHeaderCharge.next();
			logger.debug("nullifyTaxesChargesAddNotes eleHeaderCharge:"+SCXmlUtil.getString(eleHeaderCharge));
			if(RDSConstants.SHIPPING.equals(eleHeaderCharge.getAttribute(RDSConstants.A_CHARGE_CATEGORY)) && extnShippingGroupId.equals(eleHeaderCharge.getAttribute(RDSConstants.A_CHARGE_NAME))){
				totalShippingCharges += Double.valueOf(eleHeaderCharge.getAttribute(RDSConstants.A_CHARGE_AMOUNT));
				eleHeaderCharge.setAttribute(RDSConstants.A_CHARGE_AMOUNT, "0.00");
				SCXmlUtil.importElement(eleInHeaderCharges, eleHeaderCharge);
			}
		}
		logger.debug("nullifyTaxesChargesAddNotes totalShippingCharges:"+totalShippingCharges);

		while(itrListEleHeaderTax.hasNext()){
			eleHeaderTax = itrListEleHeaderTax.next();
			logger.debug("nullifyTaxesChargesAddNotes eleHeaderTax:"+SCXmlUtil.getString(eleHeaderTax));
			if(RDSConstants.SHIPPING.equals(eleHeaderTax.getAttribute(RDSConstants.A_CHARGE_CATEGORY)) && extnShippingGroupId.equals(eleHeaderTax.getAttribute(RDSConstants.A_CHARGE_NAME))){
				totalShippingTaxes += Double.valueOf(eleHeaderTax.getAttribute(RDSConstants.A_TAX));
				eleHeaderTax.setAttribute(RDSConstants.A_TAX, "0.00");
				SCXmlUtil.importElement(eleInHeaderTaxes, eleHeaderTax);
			}
		}
		logger.debug("nullifyTaxesChargesAddNotes totalShippingTaxes:"+totalShippingTaxes);
		//add notes
		Element eleNotes = SCXmlUtil.createChild(changeOrderInEle, RDSConstants.E_NOTES);
		Element eleNote;

		if(totalShippingCharges > 0.00){
			eleNote = SCXmlUtil.createChild(eleNotes, RDSConstants.E_NOTE);

			eleNote.setAttribute("NoteText", "Shipping charges $"+totalShippingCharges+"  Waived off since Order is delayed from WM");
		}

		if(totalShippingTaxes > 0.00){
			eleNote = SCXmlUtil.createChild(eleNotes, RDSConstants.E_NOTE);

			eleNote.setAttribute("NoteText", "Shipping taxes $"+totalShippingTaxes+"  Waived off since Order is delayed from WM");
		}
		
		if(totalShippingCharges > 0.00 || totalShippingTaxes > 0.00){		
			logger.debug("nullifyTaxesChargesAddNotes changeOrderInEle:"+SCXmlUtil.getString(changeOrderInEle));	
			try {
				SterlingUtil.invokeAPI(env, RDSConstants.API_CHANGE_ORDER, changeOrderInEle.getOwnerDocument());
			} catch (Exception e) {
				logger.error(e);
				throw new YFSException("Error occured while invoking changeOrder API");
			}
		}

	}
	
	private void unscheduleLines(YFSEnvironment env, String shipmentConfirmationStatus, Element eleShipment){
		Element eleOrderLines = SCXmlUtil.getChildElement(getOrderReleaseDetailsOutEle, RDSConstants.E_ORDER_LINES);
		Element eleOrderLine;
		Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment, RDSConstants.E_SHIPMENT_LINES);
		Element eleShipmentLine;
		List<Element> eleOrderLineList = SCXmlUtil.getChildren(eleOrderLines, RDSConstants.E_ORDER_LINE);
		Iterator<Element> itrEleOrderLine = eleOrderLineList.iterator();

		List<Element> eleShipmentLineList = SCXmlUtil.getChildren(eleShipmentLines, RDSConstants.E_SHIPMENT_LINE);
		Iterator<Element> itrEleShipmentLine = eleShipmentLineList.iterator();
		Element eleOrder = SCXmlUtil.getChildElement(getOrderReleaseDetailsOutEle, RDSConstants.E_ORDER);
		Document inDocUnScheduleOrder = SCXmlUtil.createDocument(RDSConstants.E_UN_SCHEDULE_ORDER);
		Element inEleUnScheduleOrder = inDocUnScheduleOrder.getDocumentElement();
		inEleUnScheduleOrder.setAttribute(RDSConstants.A_ORDER_NO, eleOrder.getAttribute(RDSConstants.A_ORDER_NO));
		inEleUnScheduleOrder.setAttribute(RDSConstants.A_DOCUMENT_TYPE, eleOrder.getAttribute(RDSConstants.A_DOCUMENT_TYPE));
		inEleUnScheduleOrder.setAttribute(RDSConstants.A_ENTERPRISE_CODE, eleOrder.getAttribute(RDSConstants.A_ENTERPRISE_CODE));
		inEleUnScheduleOrder.setAttribute("Override", "Y");
		Element eleUnScheduleOrderLines = SCXmlUtil.createChild(inEleUnScheduleOrder, RDSConstants.E_ORDER_LINES);
		String primeLineNo;

		while(itrEleOrderLine.hasNext()){
			eleOrderLine = itrEleOrderLine.next();
			if(RDSConstants.INVOICE_COMPLETE.equals(shipmentConfirmationStatus)){
			    primeLineNo = SCXmlUtil.getXpathAttribute(eleShipment, 
					"//Shipment/ShipmentLines/ShipmentLine[@PrimeLineNo='"+ eleOrderLine.getAttribute(RDSConstants.A_PRIMELINE_NO)+"']/@PrimeLineNo");
			    if(XMLUtil.isVoid(primeLineNo) || "".equals(primeLineNo))
			    	SCXmlUtil.importElement(eleUnScheduleOrderLines, eleOrderLine);
			}
			else if(RDSConstants.INVOICE_CANCEL.equals(shipmentConfirmationStatus)){
				 primeLineNo = SCXmlUtil.getXpathAttribute(eleShipment, 
							"//Shipment/ShipmentLines/ShipmentLine[@PrimeLineNo='"+ eleOrderLine.getAttribute(RDSConstants.A_PRIMELINE_NO)+"']/@PrimeLineNo");
			    if(!XMLUtil.isVoid(primeLineNo) && !"".equals(primeLineNo))
			    	SCXmlUtil.importElement(eleUnScheduleOrderLines, eleOrderLine);
			}
		}
		
		logger.debug("unscheduleLines inDocUnScheduleOrder:"+SCXmlUtil.getString(inDocUnScheduleOrder));
		try {
			eleOrderLines = SCXmlUtil.getChildElement(inEleUnScheduleOrder, RDSConstants.E_ORDER_LINES);
			if(XMLUtil.isVoid(eleOrderLines)){
				logger.debug("No lines to unschedule");
				return;
			}
			eleOrderLineList = SCXmlUtil.getChildren(eleOrderLines, RDSConstants.E_ORDER_LINE);
			if(!eleOrderLineList.isEmpty())
			   SterlingUtil.invokeAPI(env, RDSConstants.API_UNSCHEDULE_ORDER, inDocUnScheduleOrder);
		} catch (Exception e) {
			logger.error(e);
			throw new YFSException("Error occured while invoking unscheduleOrder API");
		}
		
	}
	
	private Document prepareOrderReleaseDetails(YFSEnvironment env, Element eleShipment) throws Exception{
		Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment,RDSConstants.E_SHIPMENT_LINES);
		Element eleShipmentLine = (Element)SCXmlUtil.getChildElement(eleShipmentLines, RDSConstants.E_SHIPMENT_LINE);
		String orderNo = eleShipmentLine.getAttribute(RDSConstants.A_ORDER_NO);
		String enterpriseCode = eleShipmentLine.getAttribute(RDSConstants.A_ENTERPRISE_CODE);
		String documentType = eleShipment.getAttribute(RDSConstants.A_DOCUMENT_TYPE);
		Document getOrderReleaseDetailsInDoc = SCXmlUtil.createDocument(RDSConstants.E_ORDER_RELEASE_DETAIL);
		Element getOrderReleaseDetailsInEle = getOrderReleaseDetailsInDoc.getDocumentElement();
		getOrderReleaseDetailsInEle.setAttribute(RDSConstants.A_ENTERPRISE_CODE, enterpriseCode);
		getOrderReleaseDetailsInEle.setAttribute(RDSConstants.A_ORDER_NO, orderNo);
		getOrderReleaseDetailsInEle.setAttribute(RDSConstants.A_DOCUMENT_TYPE, documentType);
        getOrderReleaseDetailsInEle.setAttribute(RDSConstants.A_RELEASE_NO, eleShipmentLine.getAttribute(RDSConstants.A_RELEASE_NO));
        logger.debug("getOrderReleaseDetailsInEle:"+ SCXmlUtil.getString(getOrderReleaseDetailsInEle));
        return SterlingUtil.invokeAPI(env, RDSConstants.GET_ORDER_RELEASE_DETAILS_OUTPUT_TEMPLATE, RDSConstants.API_GET_ORDER_RELEASE_DETAILS, getOrderReleaseDetailsInDoc);
	}

}
