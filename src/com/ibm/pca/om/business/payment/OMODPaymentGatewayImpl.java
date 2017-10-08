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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;

import com.ibm.pca.om.business.utils.OMODFoundationBridge;
import com.ibm.pca.om.business.utils.XMLOverHttpsHelper;
import com.ibm.shared.omod.OMODErrorCodes;
import com.ibm.shared.omod.OMODLiterals;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.shared.dbclasses.YFS_Charge_TransactionDBHome;
import com.yantra.shared.dbi.YFS_Charge_Transaction;
import com.yantra.shared.dbi.YFS_Credit_Card_Transaction;
import com.yantra.shared.dbi.YFS_Order_Header;
import com.yantra.shared.dbi.YFS_Payment;
import com.yantra.shared.ycp.YCPConstants;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.util.YFCUtils;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.date.YDate;
import com.yantra.yfc.dblayer.YFCDBContext;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCConfigurator;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;

public class OMODPaymentGatewayImpl extends XMLOverHttpsHelper{

	public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";

	private static OMODFoundationBridge bridge = OMODFoundationBridge.getInstance();
	public static YFCLogCategory cat = YFCLogCategory.instance(OMODPaymentGatewayImpl.class);

	private static final String PG_REQUEST_URI   = YFCConfigurator.getInstance().getProperty("omod.DEFAULT.Payment.URL");
	private static final String PG_KEYSTORE_PATH = YFCConfigurator.getInstance().getProperty("omod.DEFAULT.PaymentGateway.KeystorePath"); // Replace with the path to your keystore
	private static final String PG_KEYSTORE_PW   = YFCConfigurator.getInstance().getProperty("omod.DEFAULT.PaymentGateway.KeystorePassword");      // Replace with the password for your keystore	
	private static final String PG_BASIC_REQUEST_URI=YFCConfigurator.getInstance().getProperty("omod.DEFAULT.PaymentGateway.BasicGetURL");
	
	Properties _properties=setProperties();
	boolean isAuthRequest=false ;
	String sTransactionReference="";

	public Document post(YFSEnvironment env, Document inDoc) throws IOException, YIFClientCreationException,  NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException, UnrecoverableKeyException{		
		cat.beginTimer("OMODPaymentGatewayImpl.post");
		super.setProperties(_properties);		
		YFCDocument inpDoc=YFCDocument.getDocumentFor(inDoc);
		
		if(cat.isDebugEnabled())
			cat.debug(" Input to post method is : " + inpDoc);
		
		YFCDocument respDoc=null, outDoc= null;

		YFCElement paymentInfo = inpDoc.getDocumentElement().getChildElement("Payment");
		String paymentType = paymentInfo.getAttribute(OMODLiterals.OMOD_PAYMENT_TYPE);
		
		if(cat.isDebugEnabled()) {
			cat.debug("PaymentType : "+paymentType);
		}
		
		String orderHeaderKey = inpDoc.getDocumentElement().getAttribute("OrderHeaderKey");
		String chargeType = OMODPaymentUEUtils.getChargeTypeForPaymentDoc(inpDoc); 
			
		//PayPal 
		if (YFCObject.equals(paymentType, OMODLiterals.OMOD_PAYPAL_PAYMENT_TYPE)
				&& YFCObject.equals(chargeType, OMODLiterals.OMOD_PG_PAYPAL_RE_AUTHORIZE)
				&& !OMODPaymentUEUtils.IsAuthAllowedForPayPal(env, orderHeaderKey)) {
			
				outDoc = getOutputDocForPaypalAuthFailure();
				return  outDoc.getDocument() ;				
			
		}  else {
			respDoc=super.post(getXMLToPost(inpDoc));
		}
		
		persistTransctionReference((YFSContext)env,respDoc,paymentInfo.getAttribute("ChargeTransactionKey")); // To be used in OMODValidateInvokedCollectionUEImpl

		if(isAuthRequest && !respDoc.getDocumentElement().getBooleanAttribute(OMODLiterals.OMOD_IS_HTTP_ERROR)) {
			paymentInfo=stampAuthAttributes((YFSContext)env,paymentInfo,true);
		}
		
		if(OMODLiterals.OMOD_PG_COLLECT.equals(chargeType) && !respDoc.getDocumentElement().getBooleanAttribute(OMODLiterals.OMOD_IS_HTTP_ERROR)) {
			paymentInfo=stampCollectAttributes((YFSContext)env,paymentInfo,true);
		}

		outDoc=massageOutputToUEImpl(respDoc, paymentInfo) ;
		
		if (cat.isDebugEnabled())
			cat.debug("outDoc = " + outDoc.toString());
		cat.endTimer("OMODPaymentGatewayImpl.post");
		
		return  outDoc.getDocument() ;

	}


	public Document get(YFSEnvironment env, Document inDoc) throws IOException, YIFClientCreationException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException{		
		cat.beginTimer("OMODPaymentGatewayImpl.get");

		super.setProperties(_properties);		
		YFCDocument inpDoc=YFCDocument.getDocumentFor(inDoc);
		YFCDocument outDoc=null ;
		YFCElement paymentInfo=inpDoc.getDocumentElement();
		sTransactionReference=getPaymentTransactionReference((YFSContext)env,paymentInfo.getAttribute("ChargeTransactionKey"));

		if(YFCObject.isVoid(sTransactionReference)){
			paymentInfo.setAttribute("BPreviousInvocationSuccessful", YCPConstants.YFS_NO);
			outDoc=getResponseDocument(null,paymentInfo);
			cat.endTimer("OMODPaymentGatewayImpl.get");
			return outDoc.getDocument();
		}

		String sURLToPost=constructURLForTran();
		YFCDocument respDoc=super.get(sURLToPost);
		persistTransctionReference((YFSContext)env,respDoc,paymentInfo.getAttribute("ChargeTransactionKey"));

		if(OMODLiterals.OMOD_CHARGE_TYPE_AUTH.equalsIgnoreCase(paymentInfo.getAttribute("ChargeType"))) {
			paymentInfo=stampAuthAttributes((YFSContext)env,paymentInfo,false);
		}

		outDoc=massageOutputToUEImpl(respDoc, paymentInfo) ;
		cat.endTimer("OMODPaymentGatewayImpl.get");

		return  outDoc.getDocument();
	}
	
	/**
	 * Stamp TransactionId for Auth, ReAuth
	 * 
	 * @param oCtx
	 * @param paymentInfo
	 * @param bUpdateCreditCardNo
	 * @return
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws UnrecoverableKeyException
	 */
	private YFCElement stampAuthAttributes(YFSContext oCtx,YFCElement paymentInfo,boolean bUpdateCreditCardNo) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException {
		cat.debug(" Making call to payment gateway to get authorization date ... ");

		String paymentType = paymentInfo.getAttribute(OMODLiterals.OMOD_PAYMENT_TYPE);
		String sURL=constructURLForAuth();
			
		_properties.setProperty(OMODLiterals.OMOD_REQUEST_URL, sURL);
		super.setProperties(_properties);

		YFCDocument authResponse=super.get(sURL);
		YFCElement authResponseElem=authResponse.getDocumentElement();

		//YFCNode xid=(YFCNode)authResponseElem.getChildElement("XID");
		if(YFCObject.equals(paymentType, OMODLiterals.OMOD_CREDIT_CARD_PAYMENT_GRP)) {
			//paymentInfo.setAttribute("CreditCardNo", sTransactionReference);
			paymentInfo.setAttribute("PaymentReference1", sTransactionReference);
		} else if(YFCObject.equals(paymentType, OMODLiterals.OMOD_PAYPAL_PAYMENT_TYPE)) {
			paymentInfo.setAttribute("PaymentReference1", sTransactionReference);
		}else if(YFCObject.equals(paymentType, OMODLiterals.OMOD_GIFTCARD_PAYMENT_TYPE)) {
			paymentInfo.setAttribute("PaymentReference1", sTransactionReference);
			//As auth id doesnt come in gift card auth response saving transaction id as auth id
			paymentInfo.setAttribute("AuthorizationId",sTransactionReference);
			paymentInfo.setAttribute("AuthCode",sTransactionReference);
		}

		YFCNode grs=(YFCNode)authResponseElem.getChildElement("GRS");
		if(!YFCObject.isVoid(grs)){
			paymentInfo.setAttribute("TranAmount", grs.getNodeValue());
			paymentInfo.setAttribute("AuthorizationAmount", grs.getNodeValue());
		}

		YFCNode edt=(YFCNode)authResponseElem.getChildElement("EDT");
		if(!YFCObject.isVoid(edt)){
			YDate ydate=YDate.newDate(edt.getNodeValue());
			ydate.setEndOfDay();
			paymentInfo.setAttribute("AuthorizationExpirationDate",ydate.getString(YFCUtils.APP_FORMAT));
		}

		YFCNode auc=(YFCNode)authResponseElem.getChildElement("AUC");
		if(!YFCObject.isVoid(auc)){
			paymentInfo.setAttribute("AuthCode",auc.getNodeValue());
			paymentInfo.setAttribute("AuthorizationId",auc.getNodeValue());
		}
		
		if(bUpdateCreditCardNo){
			String sOrderHeaderKey=paymentInfo.getAttribute("OrderHeaderKey");
			YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, 
					OMODLiterals.YFS_SELECT_METHOD_WAIT, false);

			for(Iterator iter=oOrder.getPaymentMethodList().iterator();iter.hasNext();){
				YFS_Payment oPayment=(YFS_Payment)iter.next();
				if(OMODLiterals.OMOD_CREDIT_CARD_PAYMENT_GRP.equalsIgnoreCase(oPayment.getPayment_Type())) {
					//oPayment.setCredit_Card_No(sTransactionReference);
					oPayment.setPayment_Reference1(sTransactionReference);
				} else if(OMODLiterals.OMOD_PAYPAL_PAYMENT_TYPE.equalsIgnoreCase(oPayment.getPayment_Type())) {
					oPayment.setPayment_Reference1(sTransactionReference);
				}else if(OMODLiterals.OMOD_GIFTCARD_PAYMENT_TYPE.equalsIgnoreCase(oPayment.getPayment_Type())) {
					oPayment.setPayment_Reference1(sTransactionReference);
				}
				oPayment.updateRecursive();
			}
			
		}

		return paymentInfo;
	}
	
	/**
	 * Update TransactionId on PaymentReferece1 for Collect
	 * 
	 * @param oCtx
	 * @param paymentInfo
	 * @param bUpdateCreditCardNo
	 * @return
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws UnrecoverableKeyException
	 */
	private YFCElement stampCollectAttributes(YFSContext oCtx,YFCElement paymentInfo,boolean bUpdateCreditCardNo) {
		cat.debug(" Update TransactionReference for Collect ... ");
		String paymentType = paymentInfo.getAttribute(OMODLiterals.OMOD_PAYMENT_TYPE);
		if(YFCObject.equals(paymentType, OMODLiterals.OMOD_CREDIT_CARD_PAYMENT_GRP)) {
			//paymentInfo.setAttribute("CreditCardNo", sTransactionReference);
			paymentInfo.setAttribute("PaymentReference1", sTransactionReference);
		} else if(YFCObject.equals(paymentType, OMODLiterals.OMOD_PAYPAL_PAYMENT_TYPE)) {
			paymentInfo.setAttribute("PaymentReference1", sTransactionReference);
		}else if(YFCObject.equals(paymentType, OMODLiterals.OMOD_GIFTCARD_PAYMENT_TYPE)) {
			paymentInfo.setAttribute("PaymentReference1", sTransactionReference);
		}
		
		if(bUpdateCreditCardNo){
			String sOrderHeaderKey=paymentInfo.getAttribute("OrderHeaderKey");
			YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, 
					OMODLiterals.YFS_SELECT_METHOD_WAIT, false);

			for(Iterator iter=oOrder.getPaymentMethodList().iterator();iter.hasNext();){
				YFS_Payment oPayment=(YFS_Payment)iter.next();
				if(OMODLiterals.OMOD_CREDIT_CARD_PAYMENT_GRP.equalsIgnoreCase(oPayment.getPayment_Type())) {
					//oPayment.setCredit_Card_No(sTransactionReference);
					oPayment.setPayment_Reference1(sTransactionReference);
				} else if(OMODLiterals.OMOD_PAYPAL_PAYMENT_TYPE.equalsIgnoreCase(oPayment.getPayment_Type())) {
					oPayment.setPayment_Reference1(sTransactionReference);
				}else if(OMODLiterals.OMOD_GIFTCARD_PAYMENT_TYPE.equalsIgnoreCase(oPayment.getPayment_Type())) {
					oPayment.setPayment_Reference1(sTransactionReference);
				}
				oPayment.updateRecursive();
			}
			
		}

		return paymentInfo;
	}

	private Properties setProperties() {
		Properties properties=new Properties();
		if(YFCObject.isVoid(PG_REQUEST_URI) || YFCObject.isVoid(PG_KEYSTORE_PATH) || YFCObject.isVoid(PG_KEYSTORE_PW)){
			throw new YFCException(OMODErrorCodes.OMOD_PROPERTIES_MISSING_FOR_PAYMENT);
		}
		properties.setProperty(OMODLiterals.OMOD_REQUEST_URL, PG_REQUEST_URI);
		properties.setProperty(OMODLiterals.OMOD_KEYSTORE_PATH, PG_KEYSTORE_PATH);
		properties.setProperty(OMODLiterals.OMOD_KEYSTORE_PW, PG_KEYSTORE_PW);
		return properties;
	}

	@Override
	public YFCDocument getXMLToPost(YFCDocument inpDoc) {
		return getPaymentGatewayInput(inpDoc);
	}
	

	@Override
	public YFCDocument massageOutputToUEImpl(YFCDocument respDoc,YFCElement paymentInfo) {
		return getResponseDocument(respDoc,paymentInfo);
	}

	private YFCDocument getPaymentGatewayInput(YFCDocument inpDoc) {
		cat.debug("In getPaymentGatewayInput :: OMODPaymentGatewayImpl");
		if (cat.isDebugEnabled())
			cat.debug("inpDoc = " + inpDoc.toString());
		YFCElement paymentGatewayInput=inpDoc.getDocumentElement().getChildElement("PaymentGatewayInput");
		YFCElement transactionElement=paymentGatewayInput.getChildElement("TRX");
		String scvValue = OMODPaymentUEUtils.getChargeTypeForPaymentDoc(inpDoc);
				
		YFCDocument returnDoc=YFCDocument.createDocument("TRX");
		YFCElement returnDocRootElem=returnDoc.getDocumentElement();
		returnDocRootElem.importNode(transactionElement.getChildElement("SVC").cloneNode(true));

		if (scvValue != null && (scvValue.equalsIgnoreCase(OMODLiterals.OMOD_PG_CARD_AUTHORIZE) || scvValue.equalsIgnoreCase(OMODLiterals.OMOD_PG_PAYPAL_RE_AUTHORIZE)
				|| scvValue.equalsIgnoreCase(OMODLiterals.OMOD_GIFTCARD_AUTHORIZE))) {
			//returnDocRootElem.importNode(transactionElement.getChildElement("XRF").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("PRJ").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("CTY").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("COM").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("INV").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("ORD").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("CUR").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("NET").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("TAX").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("GRS").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("CRD").cloneNode(true));
			isAuthRequest=true ;
		} else if (scvValue != null && (scvValue.equalsIgnoreCase(OMODLiterals.OMOD_PG_COLLECT) || scvValue.equalsIgnoreCase(OMODLiterals.OMOD_PG_REFUND))) {
			returnDocRootElem.importNode(transactionElement.getChildElement("XRF").cloneNode(true));			
			returnDocRootElem.importNode(transactionElement.getChildElement("INV").cloneNode(true));			
			returnDocRootElem.importNode(transactionElement.getChildElement("CUR").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("NET").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("TAX").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("GRS").cloneNode(true));
		} else if (scvValue != null && scvValue.equalsIgnoreCase(OMODLiterals.OMOD_PG_CARD_VOID)) {
			returnDocRootElem.importNode(transactionElement.getChildElement("PRJ").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("CTY").cloneNode(true));
			returnDocRootElem.importNode(transactionElement.getChildElement("XRF").cloneNode(true));
		}
		
		if (cat.isDebugEnabled())
			cat.debug("returnDoc = " + returnDoc.toString());
		cat.debug("Out getPaymentGatewayInput :: OMODPaymentGatewayImpl");
		return returnDoc;
	}


	private YFCDocument getResponseDocument(YFCDocument respDoc,YFCElement inpElem){
		YFCDocument outDoc=YFCDocument.createDocument("PaymentOutput");
		if(!YFCObject.isVoid(respDoc)){
			YFCElement transcationElem=respDoc.getDocumentElement();
			YFCElement res=null;
			YFCNode xid=(YFCNode)transcationElem.getChildElement("XID");
			inpElem.setAttribute("TranReturnCode", xid.getNodeValue());

			if(transcationElem.getNodeName().equalsIgnoreCase("ERR"))
				res=transcationElem;
			else
				res=transcationElem.getChildElement("RES");
			if(!YFCObject.isVoid(res)){
				YFCNode rcd=res.getChildElement("RCD");

				int iReturnCode=Integer.parseInt(rcd.getNodeValue());
				setAdditionalAttributes(inpElem,iReturnCode);		
			}else {
				YFCException ex=new YFCException("Unrecognized response");
				ex.setAttribute("ResponseMessage" , transcationElem.getString());
			}
		}
		outDoc.getDocumentElement().importNode(inpElem.cloneNode(true));
		return outDoc;
	}

	private void setAdditionalAttributes(YFCElement inpElem,int returnCode){
		String sPaymentSystemError="Payment System Error";
		if(returnCode > 80000){
			cat.debug(" Payment has a technical error .... " );

			sPaymentSystemError="OMOD_PG_ERROR_"+returnCode;
			inpElem.setAttribute(OMODLiterals.OMOD_COLL_RESPONSE_CODE, OMODLiterals.OMOD_COLL_RESPONSE_SRVC_UNAVL);
			inpElem.setAttribute("RetryFlag", YCPConstants.YFS_YES);
			inpElem.setAttribute("SuspendPayment", YCPConstants.YFS_NO);
			inpElem.setDoubleAttribute("TranAmount", 0.00);
			inpElem.setAttribute("BPreviousInvocationSuccessful", YCPConstants.YFS_NO);
			inpElem.setAttribute("InternalReturnFlag",YCPConstants.YFS_YES);
			inpElem.setAttribute("InternalReturnMessage",sPaymentSystemError);

		}else if (returnCode > 10000){
			cat.debug(" Payment Declined .... " );
			sPaymentSystemError="OMOD_PG_ERROR_"+returnCode;
			inpElem.setAttribute(OMODLiterals.OMOD_COLL_RESPONSE_CODE, OMODLiterals.OMOD_COLL_RESPONSE_DECLINE);
			inpElem.setAttribute("RetryFlag", YCPConstants.YFS_NO);
			inpElem.setAttribute("SuspendPayment", YCPConstants.YFS_YES);
			inpElem.setDoubleAttribute("TranAmount", 0.00);
			inpElem.setAttribute("InternalReturnFlag",YCPConstants.YFS_YES);
			inpElem.setAttribute("InternalReturnMessage",sPaymentSystemError);
		}
		else if (returnCode == 0){
			cat.debug(" Payment Approved .... " );
			inpElem.setAttribute(OMODLiterals.OMOD_COLL_RESPONSE_CODE, OMODLiterals.OMOD_COLL_RESPONSE_APPROVED);
			inpElem.setAttribute("RetryFlag", YCPConstants.YFS_NO);
			inpElem.setDoubleAttribute("TranAmount", inpElem.getDoubleAttribute("RequestAmount"));
			inpElem.setDoubleAttribute("AuthorizationAmount",inpElem.getDoubleAttribute("RequestAmount"));
			inpElem.setAttribute("TranType",inpElem.getAttribute("ChargeType"));
			inpElem.setAttribute("InternalReturnFlag",YCPConstants.YFS_NO);
			inpElem.setAttribute("TranReturnFlag",YCPConstants.YFS_NO);
			inpElem.setAttribute("AuthReturnFlag",YCPConstants.YFS_NO);	

		}

	}

	private void persistTransctionReference(YFSContext ctx,YFCDocument respDoc, String chargeTranxKey) throws YIFClientCreationException{
		cat.debug("In persistTransctionReference :: OMODPaymentGatewayImpl");
		if (cat.isDebugEnabled())
			cat.debug("respDoc = " + respDoc.toString() + " chargeTranxKey = "+chargeTranxKey);
		YFSContext oCtx= null;
		try {

			oCtx = new YCPContext(ctx.getUserId(), ctx.getProgId());			
			oCtx.getPoolResolver().addAllFacts(ctx.getPoolResolver());

			YFCElement paymentGatewayInput=respDoc.getDocumentElement();			
			sTransactionReference=paymentGatewayInput.getChildElement("XID").getNodeValue();
			YFS_Credit_Card_Transaction oTransaction=getCreditCardTransaction(oCtx,chargeTranxKey);
			oTransaction.setCharge_Transaction_Key(chargeTranxKey);
			oTransaction.setTran_Return_Code(sTransactionReference);
			oTransaction.setTran_Return_Message(OMODLiterals.OMOD_PAYMENT_REFERENCE);

			oTransaction.insert();
			oCtx.commit();
		} catch (YFCException e) {
			e.setErrorDescription(OMODErrorCodes.OMOD_DB_EXCEPTION);
			throw e;
		} catch (Exception e) {
			throw new YFCException(OMODErrorCodes.OMOD_EXCEPTION_INITIALIZING_CONTEXT);
		}finally{
			oCtx.close();
		}
		cat.debug("Out persistTransctionReference :: OMODPaymentGatewayImpl");
	}

	private String getPaymentTransactionReference(YFSContext ctx,String sChargeTransacionKey) {
		String sPaymentTrnxRef=""; 
		YFS_Charge_Transaction oChargeTrn=YFS_Charge_TransactionDBHome.getInstance().selectWithPK(ctx, sChargeTransacionKey);
		if(!YFCObject.isVoid(oChargeTrn.getCredit_Card_TransactionList()) && oChargeTrn.getCredit_Card_TransactionList().size()>0 ){
			for(Iterator<YFS_Credit_Card_Transaction> iter=oChargeTrn.getCredit_Card_TransactionList().iterator();iter.hasNext();){
				YFS_Credit_Card_Transaction oCreditCrdTrxn=iter.next();
				if(OMODLiterals.OMOD_PAYMENT_REFERENCE.equalsIgnoreCase(oCreditCrdTrxn.getTran_Return_Message()))
					sPaymentTrnxRef=oCreditCrdTrxn.getTran_Return_Code();
				break;
			}
		}
		return sPaymentTrnxRef;
	}

	private YFS_Credit_Card_Transaction getCreditCardTransaction(YFSContext newCtx,String sChargeTransacionKey){

		YFS_Charge_Transaction oChargeTrn=YFS_Charge_TransactionDBHome.getInstance().selectWithPK(newCtx, sChargeTransacionKey);
		for(Iterator<YFS_Credit_Card_Transaction> iter=oChargeTrn.getCredit_Card_TransactionList().iterator();iter.hasNext();){
			YFS_Credit_Card_Transaction oCreditCrdTrxnTemp=iter.next();
			if(OMODLiterals.OMOD_PAYMENT_REFERENCE.equalsIgnoreCase(oCreditCrdTrxnTemp.getTran_Return_Message()))
				oCreditCrdTrxnTemp.deleteNoCheck();

			break;
		}

		return YFS_Credit_Card_Transaction.newInstance((YFCDBContext)newCtx);
	}
	private String constructURLForAuth() {
		return constructURL(PG_BASIC_REQUEST_URI,OMODLiterals.PAYMENT_GATEWAY_AUTH_URL,sTransactionReference);
	}

	private String constructURLForTran() {
		return constructURL(PG_BASIC_REQUEST_URI,OMODLiterals.PAYMENT_GATEWAY_TRAN_URL,sTransactionReference);
	}

	private String constructURL(String pg_basic_request_uri2, String payment_gateway_auth_url, String transactionReference) {
		return PG_BASIC_REQUEST_URI+OMODLiterals.PAYMENT_GATEWAY_AUTH_URL+sTransactionReference;
	}
	
	private YFCDocument getOutputDocForPaypalAuthFailure() {
		
		YFCDocument outDoc=YFCDocument.createDocument("PaymentOutput");
		outDoc.getDocumentElement().setAttribute(OMODLiterals.OMOD_COLL_RESPONSE_CODE, OMODLiterals.OMOD_COLL_RESPONSE_DECLINE);
		outDoc.getDocumentElement().setAttribute("RetryFlag", YCPConstants.YFS_NO);
		outDoc.getDocumentElement().setAttribute("SuspendPayment", YCPConstants.YFS_YES);
		outDoc.getDocumentElement().setDoubleAttribute("TranAmount", 0.00);
		outDoc.getDocumentElement().setAttribute("InternalReturnFlag",YCPConstants.YFS_YES);
		outDoc.getDocumentElement().setAttribute("InternalReturnMessage",OMODLiterals.OMOD_PAYPAL_REAUTH_FAILURE_MSG_BUNDLE_KEY);
		
		return outDoc;
		
	}
	
}
