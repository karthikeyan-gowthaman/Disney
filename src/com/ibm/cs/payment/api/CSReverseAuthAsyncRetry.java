package com.ibm.cs.payments.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.cs.payments.alerts.CSPaymentsAlertsAPI;
import com.ibm.cs.utils.CSCommonUtil;
import com.ibm.cs.utils.CSConstants;
import com.ibm.cs.utils.CSXMLConstants;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class CSReverseAuthAsyncRetry implements YIFCustomApi {

	private static final YFCLogCategory LOGGER = YFCLogCategory
			.instance(CSReverseAuthAsyncRetry.class);

	private static boolean isLogVerboseEnabled = LOGGER.isVerboseEnabled();
	
	public Document messageInputforCyberSource(YFSEnvironment env,Document inDoc) throws Exception{
		
		if(isLogVerboseEnabled){
			LOGGER.verbose("CSReverseAuthAsyncRetry:: messageInputforCyberSource():: CSReverseAuthAsyncRetry(): BEGIN" + SCXmlUtil.getString(inDoc));
		}
		env.setTxnObject("GET_ORDER_LIST", inDoc);
		if(isLogVerboseEnabled){
			LOGGER.verbose("CSReverseAuthAsyncRetry:: messageInputforCyberSource():: CSReverseAuthAsyncRetry(): END" + SCXmlUtil.getString(inDoc));
		}
		
		return inDoc;
		
	}
	
	public void reversalAsyncRetry(YFSEnvironment env,Document inDoc) throws Exception{
		
		
		String strCodeValue = null;
		String strSuccessCode = null;
		String strRejectCode = null;
		String strFinanceFailCode = null;
		String strTechFailCode = null;
		
		CSPaymentsAlertsAPI csAlertUtil = new CSPaymentsAlertsAPI();
		if(isLogVerboseEnabled){
			LOGGER.verbose("CSReverseAuthAsyncRetry:: reversalAsyncRetry()::  CSReverseAuthAsyncRetry(): BEGIN" + SCXmlUtil.getString(inDoc));
		}
		
		Element inDocEle = inDoc.getDocumentElement();
		String transactionCode = SCXmlUtil.getXpathAttribute(inDocEle, "//PaymentMethods/PaymentMethod/PaymentDetails/@TransactionCode");
		String strLongDescription = getCommonCodeDesc(env,CSConstants.CS_CC_ERROR_CODE,transactionCode);
		Document commoncodelistDoc = SCXmlUtil.createDocument(CSXMLConstants.E_COMMON_CODE);
        Element inEle = commoncodelistDoc.getDocumentElement();
        inEle.setAttribute(CSXMLConstants.A_CODE_TYPE,CSConstants.CS_PAYMENT_PROCESS);
        Document docOrderList = (Document) env.getTxnObject("GET_ORDER_LIST");
        if(isLogVerboseEnabled){
			LOGGER.verbose("CSReverseAuthAsyncRetry:: reversalAsyncRetry()::  CSReverseAuthAsyncRetry(): BEGIN" + SCXmlUtil.getString(docOrderList));
		}
        
        String strOrderHeaderKey = SCXmlUtil.getXpathAttribute(docOrderList.getDocumentElement(), "//OrderList/Order/@OrderHeaderKey");
        
        if(isLogVerboseEnabled){
			LOGGER.verbose("CSReverseAuthAsyncRetry:: reversalAsyncRetry()::GetCommonCode ListInput  CSReverseAuthAsyncRetry(): BEGIN" + SCXmlUtil.getString(commoncodelistDoc));
		}
		Document docGetStatuCodesList = CSCommonUtil.getCommonCodeList(env, commoncodelistDoc, CSConstants.GET_COMMON_CODE_LIST_TEMPLATE);
		
		NodeList nodeCommonCodeList = docGetStatuCodesList.getElementsByTagName(CSXMLConstants.E_COMMON_CODE);
		for (int nodeCount = 0; nodeCount < nodeCommonCodeList.getLength(); nodeCount++) {
			Element ele = (Element)nodeCommonCodeList.item(nodeCount);
			if(ele != null) 
				strCodeValue =ele.getAttribute(CSXMLConstants.A_CODE_VALUE);
			
			if (!YFCObject.isVoid(strCodeValue)){
				if (CSConstants.SUCCESS_CODE.equals(strCodeValue)){
					strSuccessCode = ele.getAttribute(CSXMLConstants.A_CODE_LONG_DESCRIPTION);
				}else if(CSConstants.REJECT_CODE.equals(strCodeValue)){
					strRejectCode =  ele.getAttribute(CSXMLConstants.A_CODE_LONG_DESCRIPTION);
				}else if (CSConstants.FF_CODE.equals(strCodeValue)){
					strFinanceFailCode =  ele.getAttribute(CSXMLConstants.A_CODE_LONG_DESCRIPTION);
				}else if(CSConstants.TF_CODE.equals(strCodeValue)){
					strTechFailCode =  ele.getAttribute(CSXMLConstants.A_CODE_LONG_DESCRIPTION);
				}
			}//success code
                 
		}
		
		if((getHardDeclineCode(env, transactionCode, strFinanceFailCode))  ) {
			
			String strChargeTransactionKey = SCXmlUtil.getXpathAttribute(docOrderList.getDocumentElement(), "//OrderList/Order/@ChargeTransactionKey");
			String strPaymentKey = SCXmlUtil.getXpathAttribute(docOrderList.getDocumentElement(), 
					"//OrderList/Order/ChargeTransactionDetails/ChargeTransactionDetail[@ChargeTransactionKey='"+strChargeTransactionKey+"']/@PaymentKey");
			String strDisplayCreditCardNo = SCXmlUtil.getXpathAttribute(docOrderList.getDocumentElement(), 
					"//OrderList/Order/PaymentMethods/PaymentMethod[@PaymentKey='"+strPaymentKey+"']/@DisplayCreditCardNo");
			String strCreditCardType = SCXmlUtil.getXpathAttribute(docOrderList.getDocumentElement(), 
					"//OrderList/Order/PaymentMethods/PaymentMethod[@PaymentKey='"+strPaymentKey+"']/@CreditCardType");
			String strPaymnetDec = "Payment Reverse Authorization for the payment method" + strCreditCardType + strDisplayCreditCardNo +
					" is failed due to Cybersource  error code"+ docOrderList + strLongDescription ;
			
			if(isLogVerboseEnabled){
				LOGGER.verbose("CSReverseAuthAsyncRetry:: reversalAsyncRetry():: Payment Declined message  :: CSReverseAuthAsyncRetry(): END" + strPaymnetDec);
			}
			
			csAlertUtil.raiseAlert(env, "CS_PAYMENT_FAILURE",strOrderHeaderKey, "PAYMENT_DECLINED", strPaymnetDec);
			
		}else if(getSoftDeclineCode(env, transactionCode, strTechFailCode)){
			YFSException yfs = new YFSException();
			env.setTxnObject("ERROR_CODE", transactionCode);
			yfs.setErrorCode("EXT_2700");
			
			
			throw yfs;
			
		}
		
		if(isLogVerboseEnabled){
			LOGGER.verbose("CSReverseAuthAsyncRetry:: reversalAsyncRetry()::  :: CSReverseAuthAsyncRetry(): END" + SCXmlUtil.getString(inDoc));
		}
	
	}
	
	private String getCommonCodeDesc(YFSEnvironment env, String csCcErrorCode,
			String transactionCode) throws Exception {
		Document commoncodelistDoc = SCXmlUtil.createDocument(CSXMLConstants.E_COMMON_CODE);
        Element inEle = commoncodelistDoc.getDocumentElement();
        inEle.setAttribute(CSXMLConstants.A_CODE_TYPE,csCcErrorCode);
        inEle.setAttribute(CSXMLConstants.A_CODE_VALUE,transactionCode);
        
        
		Document docGetStatuCodesList = CSCommonUtil.getCommonCodeList(env, commoncodelistDoc, CSConstants.GET_COMMON_CODE_LIST_TEMPLATE);
		String strCodeLongDescription = SCXmlUtil.getXpathAttribute(docGetStatuCodesList.getDocumentElement(), "//CommonCodeList/CommonCode/@CodeLongDescription");
		return strCodeLongDescription;
	}
	
	/**
	 * 
	 * getTechCode call to test if the gateway transaciton code is tech failure.
	 * @param env
	 * @param tranCode - transaction code from gateway
	 * @param comCodeTech - list of tech failure code
	 * @return
	 * @throws Exception
	 */
	private boolean getSoftDeclineCode(YFSEnvironment env,
			String tranCode, String comCodeTech) throws Exception {
		boolean isTechFail = false;
		for (String retval: comCodeTech.split(",")){
			if (retval.equals(tranCode)){	
				if (isLogVerboseEnabled)
				{
					LOGGER.verbose("CSCollectionCreditCardUECSCollectionCreditCardUE :: getTechCode()::: Technical failure code" + tranCode);
				}
				isTechFail = true;
			}
		}
		
		return isTechFail;
	}
	
	/**
	 * 
	 * getFinCode call to test if the gateway transaction code is financial failure.
	 * @param env
	 * @param tranCode - transaction code from gateway
	 * @param comCodeTech - list of financial failure code
	 * @return
	 * @throws Exception
	 */
	private boolean getHardDeclineCode(YFSEnvironment env,
			String tranCode, String comCodeFin) throws Exception {
		boolean isFinFail = false;
		for (String retval: comCodeFin.split(",")){
			if (retval.equals(tranCode)){	
				if (isLogVerboseEnabled)
				{
					LOGGER.verbose("CSCollectionCreditCardUE :: getFinCode()::: Financial failure code" + tranCode);
				}
				isFinFail = true;
			}
		}
		return isFinFail;
	}
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
