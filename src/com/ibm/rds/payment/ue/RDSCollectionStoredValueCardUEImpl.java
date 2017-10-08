package com.ibm.rds.payment.ue;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.ibm.pca.sbc.business.utils.SBCApiUtils;
import com.ibm.rds.api.ApiInvoker;
import com.ibm.rds.payment.api.RDSSVSAuthorizeAPI;
import com.ibm.rds.payment.api.RDSSVSSettlementAPI;
import com.ibm.rds.util.RDSConstants;
import com.ibm.rds.util.PaymentProcessingUtil;
import com.ibm.rds.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.util.YFCUtils;
import com.yantra.yfc.date.YTimestamp;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionStoredValueCardUE;

//com.ibm.pca.om.business.payment.OMODCollectionStoredValueCardUEImpl
public class RDSCollectionStoredValueCardUEImpl implements YFSCollectionStoredValueCardUE,RDSConstants{

	public static YFCLogCategory log = YFCLogCategory.instance(RDSCollectionStoredValueCardUEImpl.class);
	RDSSVSAuthorizeAPI rdsSVSAuthorizeAPI = new RDSSVSAuthorizeAPI();
	RDSSVSSettlementAPI rdsSVSSettlementAPI=new RDSSVSSettlementAPI();
	public YFSExtnPaymentCollectionOutputStruct collectionStoredValueCard(
			YFSEnvironment env, YFSExtnPaymentCollectionInputStruct yfsExtnPaymentCollectionInputStruct)
			throws YFSUserExitException {
		YFSExtnPaymentCollectionOutputStruct yfsExtnPaymentCollectionOutputStruct = new YFSExtnPaymentCollectionOutputStruct();
		try {		
			Document paymentInputDoc = PaymentProcessingUtil.convertInStructToXml(yfsExtnPaymentCollectionInputStruct);
			log.verbose("RDSCollectionStoredValueCardUEImpl:: User Exit Input XML" + SCXmlUtil.getString(paymentInputDoc));					 	
			String strChargeType = yfsExtnPaymentCollectionInputStruct.chargeType;
			log.verbose("RDSCollectionStoredValueCardUEImpl:: Charge Type =" + strChargeType);
			
			if (AUTHORIZATION.equalsIgnoreCase(strChargeType)) {
			
				String isStubmode = SBCApiUtils.getCommonCodeValue(env, SVS_STUB_MODE);
				 
				if(YES.equalsIgnoreCase(isStubmode)){
					yfsExtnPaymentCollectionOutputStruct =  authorize(env, paymentInputDoc,yfsExtnPaymentCollectionInputStruct);
				}else{
					
					//invoking actual SVS authorization flow..
					yfsExtnPaymentCollectionOutputStruct =  rdsSVSAuthorizeAPI.authorize(env, paymentInputDoc,yfsExtnPaymentCollectionOutputStruct);
				}
				
				//yfsExtnPaymentCollectionOutputStruct =  authorize(env, yfsExtnPaymentCollectionInputStruct); 
				return yfsExtnPaymentCollectionOutputStruct;
			} else if (CHARGE .equalsIgnoreCase(strChargeType)) {
				
				String isStubmode = SBCApiUtils.getCommonCodeValue(env, SVS_STUB_MODE);//pass this string to Settlement method for enable or disable SVS Stub mode.
				
				yfsExtnPaymentCollectionOutputStruct = settlement(env, yfsExtnPaymentCollectionInputStruct,isStubmode);
				
				return yfsExtnPaymentCollectionOutputStruct;
			}
			else {
				throw new Exception("RDSCollectionStoredValueCardUEImpl.collectionCreditCard() :: Invalid chargeType for credit card" +
						"\n orderHeaderKey = " + yfsExtnPaymentCollectionInputStruct.orderHeaderKey +
						"\n chargeType = " +  yfsExtnPaymentCollectionInputStruct.chargeType +
						"\n");                   	
			}
		} catch (YFCException e) {
			log.error("An Exception occured while processing CollectionOthersUEImpl.collectionOthers()" +
					"\n orderHeaderKey = " + yfsExtnPaymentCollectionInputStruct.orderHeaderKey +
					"\n chargeType = " +  yfsExtnPaymentCollectionInputStruct.chargeType +
					"\n paymentType = " +  yfsExtnPaymentCollectionInputStruct.paymentType +
    				"\n");
			throw new YFSUserExitException(e.toString());	
		} catch (Exception e) {
			log.error("An Exception occured while processing CollectionOthersUEImpl.collectionOthers()" +
	     			"\n orderHeaderKey = " + yfsExtnPaymentCollectionInputStruct.orderHeaderKey +
	     			"\n chargeType = " +  yfsExtnPaymentCollectionInputStruct.chargeType +
	     			"\n paymentType = " +  yfsExtnPaymentCollectionInputStruct.paymentType +
	    			"\n");
			throw new YFSUserExitException(e.toString());
		}
		//return yfsExtnPaymentCollectionOutputStruct;
	}
	
	/**
	 * Authorize.
	 * @param env
	 * @param inStruct 
	 * @return outStruct
	 * @throws YFSUserExitException
	 */

	private YFSExtnPaymentCollectionOutputStruct authorize(final YFSEnvironment env, Document paymentInputDoc,final YFSExtnPaymentCollectionInputStruct inputStruct)
	throws YFSUserExitException  {	
		log.verbose("Begin : CollectionCreditCardUEImpl:authorize()");			
		YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
		initializeOutStruct(outStruct);
		String authorizationExpirationDate = PaymentProcessingUtil.getAuthExpirationDate(env, inputStruct.paymentType, inputStruct.enterpriseCode);//corrected with PaymentType than chargeType.
		try {
			if (inputStruct.requestAmount < 0.0)  {
				 
				outStruct = reverseAuth(env , inputStruct);
				return outStruct;
			}
			if(inputStruct.authorizationId.isEmpty()){
				 
				Document docStubResponse = ApiInvoker.invokeService(env,RDS_SVS_STUB_RESPONSE_SERVICE, paymentInputDoc);
				
				
				outStruct.authorizationAmount = inputStruct.requestAmount;
				outStruct.authorizationId = docStubResponse.getDocumentElement().getAttribute(AuthorizationId);		
				outStruct.authCode = docStubResponse.getDocumentElement().getAttribute(AuthCode);
				outStruct.tranAmount = inputStruct.requestAmount;
				outStruct.tranReturnCode = docStubResponse.getDocumentElement().getAttribute(TranReturnCode);
				outStruct.tranReturnFlag = docStubResponse.getDocumentElement().getAttribute(TranReturnFlag);
				outStruct.requestID = docStubResponse.getDocumentElement().getAttribute(RequestID);	  
				outStruct.retryFlag = docStubResponse.getDocumentElement().getAttribute(RetryFlag);    		
				outStruct.internalReturnCode = docStubResponse.getDocumentElement().getAttribute(InternalReturnCode);
				outStruct.authorizationExpirationDate = authorizationExpirationDate;
				outStruct.authTime = YTimestamp.newMutableTimestamp().getString(YFCDate.XML_DATE_FORMAT);
			 	outStruct.tranType= inputStruct.chargeType;
				
			}
			return outStruct;
		    
			
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("An Exception occured while processing CollectionCreditCardUEImpl.authorize() method", exception);
			throw new YFSUserExitException("An Exception occured while processing CollectionCreditCardUEImpl.authorize() method" + exception.getMessage());
		}  
	}
	
	private YFSExtnPaymentCollectionOutputStruct reverseAuth(
			YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inputStruct) throws YFSUserExitException {	
		//log.verbose("Begin : RDSCollectionStoredValueCardUEImpl:reverseAuth");			
		YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
		initializeOutStruct(outStruct);
		try{
		Document docRevAuthInput = PaymentProcessingUtil.convertInStructToXml(inputStruct);
		outStruct = rdsSVSAuthorizeAPI.reverseAuth(env, docRevAuthInput,outStruct);
		return outStruct;
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("An Exception occured while processing RDSCollectionStoredValueCardUEImpl.reverseAuth() method", exception);
			throw new YFSUserExitException("An Exception occured while processing RDSCollectionStoredValueCardUEImpl.reverseAuth() method" + exception.getMessage());
		}  
	
}

	/**
     * This method initializes the OutStruct object.
     * @param outStruct outStruct
     * @throws YFSUserExitException in case of any exceptions
     */
    private void initializeOutStruct(YFSExtnPaymentCollectionOutputStruct outStruct)
        throws YFSUserExitException
    {
        try
        {
           //system date
            Date sysDate = new Date();
            //system date in yantra format
            //  String yantraDateFormat = DateUtil.convertDate(sysDate);
            //initialize OutStruct attributes
            outStruct.tranReturnCode = "INITIALIZED";
            outStruct.tranReturnFlag = "";
            outStruct.retryFlag = "N";
            outStruct.holdOrderAndRaiseEvent = false;
            outStruct.internalReturnCode = "";
            outStruct.tranAmount = 0.0;
            outStruct.authorizationAmount = 0.0;
            outStruct.authorizationId = "";
            outStruct.collectionDate = sysDate;          
        }
        catch (Exception exception)
        {
            //log the exception
            //log.verbose("YFSException occured in RDSCollectionStoredValueCardUEImpl.initializeOutStruct", exception);
            //throw new Exception with the specified code, description
            //and the Exception
            throw new YFSUserExitException( "An Exception occured while processing RDSCollectionStoredValueCardUEImpl.initializeOutStruct method"+ exception);
        }
    }   
 

	/**
	 * This method is used to set the details of the settlement to the out struct.
	 * 
	 * @param yfsExtnPaymentCollectionInputStruct input Struct
	 * @param yfsExtnPaymentCollectionOutputStruct output Struct
	 * @return 
	 */
	private YFSExtnPaymentCollectionOutputStruct settlement(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct yfsExtnPaymentCollectionInputStruct,String isStubmode) throws YFSUserExitException {
		String enteredBy = null;

		Map<String, String> mapAuthIdCardNo = new HashMap<String, String>();
		YFSExtnPaymentCollectionOutputStruct settleOutStruct = new YFSExtnPaymentCollectionOutputStruct();
		initializeOutStruct(settleOutStruct);
		
		try {
		
			// For Orders Paid using Gif cards, the Refund process is issuing a new Gift Card (RFO). And 
			//This is achieved using payment type configuration in SBC and hence commenting following Refund method invocation logic.
			
			/*if (yfsExtnPaymentCollectionInputStruct.requestAmount < 0.0)  {
			settleOutStruct = refund(env , yfsExtnPaymentCollectionInputStruct);
			return settleOutStruct;
			}*/
		
		
		Document docSettlementInput = PaymentProcessingUtil.convertInStructToXml(yfsExtnPaymentCollectionInputStruct);
		// checking is Stubmode enabled and doing dummy settlement process Else invoking actual SVS soap based settlemnt process.
		if(YES.equalsIgnoreCase(isStubmode))
		{
			
	     Document docStubResponse = ApiInvoker.invokeService(env,RDS_SVS_STUB_RESPONSE_SERVICE, docSettlementInput);
	     
		settleOutStruct.authorizationAmount = yfsExtnPaymentCollectionInputStruct.requestAmount;
		log.debug("settleOutStruct.authorizationAmount: " + settleOutStruct.authorizationAmount);
		settleOutStruct.authorizationId = yfsExtnPaymentCollectionInputStruct.authorizationId;		
		settleOutStruct.tranAmount = yfsExtnPaymentCollectionInputStruct.requestAmount;
		settleOutStruct.tranType = yfsExtnPaymentCollectionInputStruct.chargeType;
		settleOutStruct.tranReturnCode = docStubResponse.getDocumentElement().getAttribute(TranReturnCode_settle);
		settleOutStruct.tranReturnFlag = docStubResponse.getDocumentElement().getAttribute(TranReturnFlag_settle);					
		settleOutStruct.internalReturnCode = docStubResponse.getDocumentElement().getAttribute(InternalReturnCode_settle); 
		}
		
		//invoking actual SVS soap based settlemnt process.
		else{ 
			if ("DUMMY".equalsIgnoreCase(yfsExtnPaymentCollectionInputStruct.authorizationId)) {
				mapAuthIdCardNo(yfsExtnPaymentCollectionInputStruct.orderHeaderKey, env, mapAuthIdCardNo);
				yfsExtnPaymentCollectionInputStruct.authorizationId = mapAuthIdCardNo.get(yfsExtnPaymentCollectionInputStruct.creditCardNo);
			}
			settleOutStruct = RDSSVSSettlementAPI.charge(env, docSettlementInput,settleOutStruct);
		}
		
		return settleOutStruct;
		
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("An Exception occured while processing RDSCollectionStoredValueCardUEImpl.settlement() method", exception);
			throw new YFSUserExitException("An Exception occured while processing RDSCollectionStoredValueCardUEImpl.settlement() method" + exception.getMessage());
		}	
		
	}
	
	private YFSExtnPaymentCollectionOutputStruct refund(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inStruct) 
	throws YFSUserExitException
	{	
		log.verbose("Begin : RDSCollectionStoredValueCardUEImpl:refund()");
		String enteredBy = "";
		YFSExtnPaymentCollectionOutputStruct settleOutStruct = new YFSExtnPaymentCollectionOutputStruct();
		try {
			enteredBy = getEnteredBy(inStruct.orderHeaderKey, env);
			Document docSettlementInput = PaymentProcessingUtil.convertInStructToXml(inStruct);
			settleOutStruct = RDSSVSSettlementAPI.refund(env, docSettlementInput,settleOutStruct);
			return settleOutStruct;
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error("An Exception occured while processing RDSCollectionStoredValueCardUEImpl.refund() method", exception);
			try{
				ApiInvoker.invokeAPI(env, "createException", "<Inbox AutoResolvedFlag='N' AssignedToUserId='"+enteredBy +"' InboxType='Payment Failures' " +
					"QueueId='YCD_PAYMENT_DECLINED' ErrorType=''  ExceptionType='PAYMENTEXCEPTION' " + "ExceptionTypeDescription='PAYMENTEXCEPTION' " +
					"OrderHeaderKey='"+inStruct.orderHeaderKey+"' OrderNo='"+inStruct.orderNo+"' Description='An Exception occured while processing RDSCollectionStoredValueCardUEImpl.refund() method' > " +
					"<InboxReferencesList><InboxReferences Name='ApiName' ReferenceType='TEXT' Value='RDSCollectionStoredValueCardUEImpl.class'/>" +
					"<InboxReferences Name='ERRORDESCRIPTION' ReferenceType='TEXT' Value='"+
					"An Exception occured while processing RDSCollectionStoredValueCardUEImpl.refund() method" +"'/></InboxReferencesList></Inbox>");
			}catch(Exception e)
			{
				log.error("Exception occured while raising an alert.");
			}
			throw new YFSUserExitException("An Exception occured while processing RDSCollectionStoredValueCardUEImpl.refund() method" + exception.getMessage());
		}         
  	}
	
	   /**
	 * To get Entered by value
	 * @param orderHeaderKey
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private String getEnteredBy(String orderHeaderKey,YFSEnvironment env)  throws Exception 
	{
		Document outDoc = null;
		String enteredBy = "";
		String apiName = "getOrderList";
		Element orderNode = null;
		String inDocStr = "<Order OrderHeaderKey='" + orderHeaderKey + "'/>";
		try{
			String apiTemplate = "<OrderList><Order EnteredBy='' OrderHeaderKey='' OrderNo=''></Order></OrderList>";
			env.setApiTemplate(apiName, XMLUtil.getDocument(apiTemplate));
			outDoc = ApiInvoker.invokeAPI(env, apiName, inDocStr);
			env.clearApiTemplate(apiName);
			NodeList orderNodeList = XMLUtil.getNodeList(outDoc, "/OrderList/Order");
			for(int i = 0; i < orderNodeList.getLength() ; i++)
			{
				orderNode = (Element)orderNodeList.item(i);
				if(orderNode.getAttribute("OrderHeaderKey").equals(orderHeaderKey) )
				{
					enteredBy = orderNode.getAttribute("EnteredBy");
					break;
				}
			}
		}catch(Exception e)
		{
			log.error("Error on getting enteredBy");
			enteredBy = "";
		}
		log.debug("enteredBy : "+enteredBy);
		return enteredBy;
	}
	
	/**
	 * To get OrderDetails
	 * @param orderHeaderKey
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private Element getOrderList(String orderHeaderKey,YFSEnvironment env)
	{
		Document outDoc = null;
		Element eleOrderList = null;
		String apiName = "getOrderList";
		String inDocStr = "<Order OrderHeaderKey='" + orderHeaderKey + "'/>";
		try{
			String apiTemplate = "<OrderList><Order OrderHeaderKey='' OrderNo='' BillToID=''></Order></OrderList>";
			env.setApiTemplate(apiName, XMLUtil.getDocument(apiTemplate));
			outDoc = ApiInvoker.invokeAPI(env, apiName, inDocStr);
			env.clearApiTemplate(apiName);
			if (!XmlUtils.isVoid(outDoc)) {
				eleOrderList = outDoc.getDocumentElement();
			}
		}catch(Exception e)
		{
			log.error("Error on getting OrderDetails");
			
		}
		
		return eleOrderList;
	}
	
	/**
	 * To get authorization ID
	 * @param orderHeaderKey
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> mapAuthIdCardNo(String orderHeaderKey,YFSEnvironment env, Map<String, String> mapAuthIdCardNo)
	{
		Document outDoc = null;
		Element elePaymentInquiryDetails = null;
		Element eleChargeTransactionDetail = null;
		Element elePaymentMethod = null;
		String strChargeType = null;
		String strAuthId = null;
		String strCardNo = null;
		Element elePaymentInquiryDetailsOut = null;
		String apiName = "getPaymentInquiryDetails";
		String inDocStr = "<Order OrderHeaderKey='" + orderHeaderKey + "'/>";
		try{
			String apiTemplate = "<Order OrderHeaderKey='' OrderNo='' BillToID=''><ChargeTransactionDetails><ChargeTransactionDetail><PaymentMethod/></ChargeTransactionDetail></ChargeTransactionDetails></Order>";
			env.setApiTemplate(apiName, XMLUtil.getDocument(apiTemplate));
			outDoc = ApiInvoker.invokeAPI(env, apiName, inDocStr);
			env.clearApiTemplate(apiName);
			if (!XmlUtils.isVoid(outDoc)) {
				elePaymentInquiryDetails = outDoc.getDocumentElement();
			}
			log.debug("elePaymentInquiryDetails: "+SCXmlUtil.getString(elePaymentInquiryDetails));		
			Element eleChargeTransactionDetails = XMLUtil.getChildElement(elePaymentInquiryDetailsOut, "ChargeTransactionDetails");
			log.debug("eleChargeTransactionDetails: "+SCXmlUtil.getString(eleChargeTransactionDetails));			
			Iterator<Element> iterChargeTransactionDetails = XMLUtil.getChildren(eleChargeTransactionDetails);
			while (iterChargeTransactionDetails.hasNext()) {
				eleChargeTransactionDetail = iterChargeTransactionDetails.next();
				log.debug("eleChargeTransactionDetail: "+SCXmlUtil.getString(eleChargeTransactionDetail));
				strChargeType = eleChargeTransactionDetail.getAttribute("ChargeType");				
				strAuthId = eleChargeTransactionDetail.getAttribute("AuthorizationID");				
				elePaymentMethod = XMLUtil.getChildElement(eleChargeTransactionDetail, "PaymentMethod");
				log.debug("elePaymentMethod: "+SCXmlUtil.getString(elePaymentMethod));
				strCardNo = elePaymentMethod.getAttribute("SvcNo");				
				if(strChargeType == "AUTHORIZATION" && strAuthId != "DUMMY"){
					mapAuthIdCardNo.put(strCardNo,strAuthId);
				}			
			}
		}catch(Exception e)
		{
			log.error("Error on getting Payment Inquiry Details");
			
		}

		return mapAuthIdCardNo;
	}
	
}
