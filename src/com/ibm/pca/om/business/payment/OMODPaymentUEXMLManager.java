/*******************************************************************************
 * IBM Confidential
 *   OCO Source Materials
 *   5725-G69
 *   Copyright IBM Corporation 2011,2012
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 ******************************************************************************/
package com.ibm.pca.om.business.payment;

import java.util.Date;

import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.date.YDate;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;

public class OMODPaymentUEXMLManager {

public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";

	private static YFCLogCategory cat = YFCLogCategory.instance(OMODPaymentUEXMLManager.class.getName());

	public static YFCDocument convertPaymentInputStructToXML(YFSExtnPaymentCollectionInputStruct paymentInputStruct) {
		cat.debug("Entering convertPaymentInputStructToXML");
		YFCDocument paymentInputDoc = YFCDocument.createDocument();
		YFCElement paymentRoot = paymentInputDoc.createElement("Payment");
		paymentRoot.setAttribute("AuthorizationId", paymentInputStruct.authorizationId);
		paymentRoot.setAttribute("BillToAddressLine1", paymentInputStruct.billToAddressLine1);
		paymentRoot.setAttribute("BillToCity", paymentInputStruct.billToCity);
		paymentRoot.setAttribute("BillToCountry", paymentInputStruct.billToCountry);
		paymentRoot.setAttribute("BillToDayPhone", paymentInputStruct.billToDayPhone);
		paymentRoot.setAttribute("BillToEmailId", paymentInputStruct.billToEmailId);
		paymentRoot.setAttribute("BillToFirstName", paymentInputStruct.billToFirstName);
		paymentRoot.setAttribute("BillToId", paymentInputStruct.billToId);
		paymentRoot.setAttribute("BillToKey", paymentInputStruct.billTokey);
		paymentRoot.setAttribute("BillToLastName", paymentInputStruct.billToLastName);
		paymentRoot.setAttribute("BillToState", paymentInputStruct.billToState);
		paymentRoot.setAttribute("BillToZipCode", paymentInputStruct.billToZipCode);
		paymentRoot.setAttribute("bPreviouslyInvoked", String.valueOf(paymentInputStruct.bPreviouslyInvoked ).toString());
		paymentRoot.setAttribute("ChargeTransactionKey", paymentInputStruct.chargeTransactionKey);
		paymentRoot.setAttribute("ChargeType", paymentInputStruct.chargeType);
		paymentRoot.setAttribute("CreditCardExpirationDate", paymentInputStruct.creditCardExpirationDate);
		paymentRoot.setAttribute("CreditCardName", paymentInputStruct.creditCardName);
		paymentRoot.setAttribute("CreditCardNo", paymentInputStruct.creditCardNo);
		paymentRoot.setAttribute("CreditCardType", paymentInputStruct.creditCardType);
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
		paymentInputDoc.appendChild(paymentRoot);
		cat.debug("Exiting convertPaymentInputStructToXML");
		return paymentInputDoc;

	}


	public static YFSExtnPaymentCollectionOutputStruct constructBasicCollectionPaymentOutput(YFCElement paymentOutputElem)	{

		YFSExtnPaymentCollectionOutputStruct paymentOutputStruct =new YFSExtnPaymentCollectionOutputStruct();

		if(!YFCObject.isVoid(paymentOutputElem.getAttribute("AsynchRequestProcess")))
			paymentOutputStruct.asynchRequestProcess = YFCObject.equals("true", paymentOutputElem.getAttribute("AsynchRequestProcess")) ? true : false ;
		else
			paymentOutputStruct.asynchRequestProcess = false;	


		paymentOutputStruct.bPreviousInvocationSuccessful = paymentOutputElem.getBooleanAttribute("BPreviousInvocationSuccessful");

		String strCollectionDate = paymentOutputElem.getAttribute("CollectionDate");
		if(!YFCObject.isVoid(strCollectionDate)){
			YDate ydate=YDate.newDate(strCollectionDate);
			paymentOutputStruct.collectionDate = new Date(ydate.getTime());
		}

		paymentOutputStruct.tranRequestTime = paymentOutputElem.getAttribute("TranRequestTime");
		paymentOutputStruct.authAVS = paymentOutputElem.getAttribute("AuthAVS");
		paymentOutputStruct.authCode = paymentOutputElem.getAttribute("AuthCode");
		paymentOutputStruct.authorizationId = paymentOutputElem.getAttribute("AuthorizationId");
		paymentOutputStruct.authReturnCode = paymentOutputElem.getAttribute("AuthReturnCode");
		paymentOutputStruct.authReturnFlag = paymentOutputElem.getAttribute("AuthReturnFlag");
		paymentOutputStruct.authReturnMessage = paymentOutputElem.getAttribute("AuthReturnMessage");
		paymentOutputStruct.internalReturnCode = paymentOutputElem.getAttribute("InternalReturnCode");
		paymentOutputStruct.internalReturnFlag = paymentOutputElem.getAttribute("InternalReturnFlag");
		paymentOutputStruct.internalReturnMessage = paymentOutputElem.getAttribute("InternalReturnMessage");
		paymentOutputStruct.requestID = paymentOutputElem.getAttribute("RequestID");
		paymentOutputStruct.tranType = paymentOutputElem.getAttribute("TranType");
		paymentOutputStruct.tranReturnCode = paymentOutputElem.getAttribute("TranReturnCode");
		paymentOutputStruct.tranReturnFlag = paymentOutputElem.getAttribute("TranReturnFlag");
		paymentOutputStruct.tranReturnMessage = paymentOutputElem.getAttribute("TranReturnMessage");
		paymentOutputStruct.DisplayPaymentReference1 = paymentOutputElem.getAttribute("DisplayPaymentReference1");

		return paymentOutputStruct;
	}
}