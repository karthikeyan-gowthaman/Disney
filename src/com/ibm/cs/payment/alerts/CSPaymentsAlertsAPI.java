package com.ibm.cs.payments.alerts;

import java.util.Properties;

import com.ibm.cs.utils.CSCommonUtil;
import com.ibm.cs.utils.CSXMLConstants;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CSPaymentsAlertsAPI implements YIFCustomApi {

	private static final YFCLogCategory LOGGER = YFCLogCategory
			.instance(CSPaymentsAlertsAPI.class);

	private static boolean isLogVerboseEnabled = LOGGER.isVerboseEnabled();
	
	/*
	 * Input to create  alert createException
	 * <Inbox ActiveFlag="Y"  Consolidate="N"

		    ExceptionEscalatedFlag="" ExceptionType="CS_PAYMENT_DECLINED" ExceptionTypeKey=""
		     OrderHeaderKey="2016010818084176314" 
		    QueueId="CS_PAYMENT_QUEUE" >
		    <Order DocumentType="0001" EnterpriseCode="CS_US" OrderHeaderKey="2016010818084176314" OrderNo=""/>
		</Inbox>
		
		*/
	public void raiseAlert(YFSEnvironment aEnvironment,String expType,  String orderHdrKey, String queueID,String strDesc) throws Exception{
		Document inDocInbox = SCXmlUtil.createDocument(CSXMLConstants.E_INBOX);
		Element inEleInbox = inDocInbox.getDocumentElement();
		
		inEleInbox.setAttribute(CSXMLConstants.A_ACTIVE_FLAG, CSXMLConstants.V_YES);
		inEleInbox.setAttribute(CSXMLConstants.A_CONSOLIDATE, CSXMLConstants.V_NO);
		inEleInbox.setAttribute(CSXMLConstants.A_EXCEPTION_TYPE,expType);
		inEleInbox.setAttribute(CSXMLConstants.A_ORDER_HEADER_KEY,orderHdrKey);
		inEleInbox.setAttribute(CSXMLConstants.A_QUEUE_ID,queueID);
		inEleInbox.setAttribute(CSXMLConstants.A_DESCRIPTION,strDesc);
		if(isLogVerboseEnabled){
			LOGGER.verbose("CSPaymentsAlertsAPI:: raiseAlert():: In raiseAlert  createExcpetion Input:: CSPaymentsAlertsAPI(): BEGIN" + SCXmlUtil.getString(inDocInbox));
		}
		Document outDoc = CSCommonUtil.invokeAPI(aEnvironment, CSXMLConstants.API_CREATE_EXCEPTION, inDocInbox);
		if(isLogVerboseEnabled){
			LOGGER.verbose("CSPaymentsAlertsAPI:: raiseAlert():: In raiseAlert  createExcpetion output :: CSPaymentsAlertsAPI(): BEGIN" + SCXmlUtil.getString(outDoc));
		}
		
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
