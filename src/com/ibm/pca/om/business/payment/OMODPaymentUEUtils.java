package com.ibm.pca.om.business.payment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.pca.om.business.utils.OMODFoundationBridge;
import com.ibm.shared.omod.OMODErrorCodes;
import com.ibm.shared.omod.OMODLiterals;
import com.yantra.shared.dbclasses.YFS_Charge_TransactionDBHome;
import com.yantra.shared.dbi.YFS_Charge_Transaction;
import com.yantra.shared.dbi.YFS_Order_Header;
import com.yantra.shared.dbi.YFS_Upload_Job;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dblayer.PLTQueryBuilder;
import com.yantra.yfc.dblayer.PLTQueryOperator;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;

public class OMODPaymentUEUtils {

	public static YFCLogCategory cat = YFCLogCategory.instance(OMODPaymentUEUtils.class);
	private static OMODFoundationBridge bridge = OMODFoundationBridge.getInstance();


	public static YFCDocument getPaymentInputDocFromStruct(YFSEnvironment oEnv, YFSExtnPaymentCollectionInputStruct paymentInputStruct){

		YFSContext oCtx = (YFSContext)oEnv; 	
		YFCDocument paymentInputDoc = OMODPaymentUEXMLManager.convertPaymentInputStructToXML(paymentInputStruct);
		String sOrderHeaderKey = paymentInputStruct.orderHeaderKey;

		YFS_Order_Header oOrder = bridge.getOrderHeader(oCtx, sOrderHeaderKey, null, null, null, 
				OMODLiterals.YFS_SELECT_METHOD_WAIT, false);

		paymentInputDoc.getDocumentElement().setAttribute(YFS_Order_Header.ORDER_NO, oOrder.getOrder_No());

		return paymentInputDoc ;
	}
	
	/**
	 * 
	 * @param env
	 * @param paymentDoc
	 * @return
	 */
	public static boolean IsAuthAllowedForPayPal(YFSEnvironment env, String orderHeaderKey) {
		
		boolean isAuthAllowed = true;
		
		List<YFS_Charge_Transaction> payPalTransList = getChargeTransactionListByOrderHeaderKeyAndChargeType(
				(YFSContext) env, orderHeaderKey,
				OMODLiterals.OMOD_AUTH_CHARGE_TYPE);
		
		if(payPalTransList != null && payPalTransList.size() > 1) {
			cat.error("PayPal Transactions cannot be Authorized more than once");
			isAuthAllowed = false;
		} 
		
		return isAuthAllowed;
		
		
	}
	
	/**
	 * 
	 * @param paymentDoc
	 * @return
	 */
	public static String getChargeTypeForPaymentDoc(YFCDocument paymentDoc) {
		
		YFCElement paymentGatewayInput=paymentDoc.getDocumentElement().getChildElement("PaymentGatewayInput");
		YFCElement transactionElement=paymentGatewayInput.getChildElement("TRX");
		
		return transactionElement.getElementsByTagName("SVC").item(0).getChildNodes().item(0).getNodeValue();
		
	}
	
	public static String getRequestAmountDoc(YFCDocument paymentDoc) {
		
		YFCElement paymentGatewayInput=paymentDoc.getDocumentElement().getChildElement("PaymentGatewayInput");
		YFCElement transactionElement=paymentGatewayInput.getChildElement("TRX");
		
		return transactionElement.getElementsByTagName("NET").item(0).getChildNodes().item(0).getNodeValue();
		
	}
	
	
	/**
	 * 
	 * @param ctx
	 * @param orderHeaderKey
	 * @param chargeType
	 * @return
	 */
	public static List<YFS_Charge_Transaction> getChargeTransactionListByOrderHeaderKeyAndChargeType(YFSContext ctx, String orderHeaderKey, String chargeType) {
		
		List<YFS_Charge_Transaction> payPalTransList = null;
		
		try{
			
			PLTQueryBuilder payPalTransListQueryBuilder = new PLTQueryBuilder(true);
			payPalTransListQueryBuilder.appendString(YFS_Charge_TransactionDBHome.getInstance().getColumnName(YFS_Charge_Transaction.ORDER_HEADER_KEY), PLTQueryOperator.EQUALS, orderHeaderKey);
			payPalTransListQueryBuilder.appendAND();
			payPalTransListQueryBuilder.appendString(YFS_Charge_TransactionDBHome.getInstance().getColumnName(YFS_Charge_Transaction.CHARGE_TYPE), PLTQueryOperator.EQUALS, chargeType);
			
			Set<String> columnSet = new HashSet<String>();
			columnSet.add(YFS_Charge_Transaction.CHARGE_TRANSACTION_KEY);
			
			payPalTransList = YFS_Charge_TransactionDBHome.getInstance().listWithWhere(ctx, payPalTransListQueryBuilder, 100, columnSet);  
			
			if(payPalTransList !=null && cat.isDebugEnabled()) {
				cat.debug("payPalTransList size : "+payPalTransList.size());
			}
			
		} catch (YFCException e) {
			e.setErrorDescription(OMODErrorCodes.OMOD_DB_EXCEPTION);
			throw e;
		} catch (Exception e) {
			throw new YFCException(OMODErrorCodes.OMOD_EXCEPTION_INITIALIZING_CONTEXT);
		}
		
		return payPalTransList;
		
	}
}
