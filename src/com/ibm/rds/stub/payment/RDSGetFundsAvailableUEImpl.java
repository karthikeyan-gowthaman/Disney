package com.ibm.rds.stub.payment;


import org.w3c.dom.Document;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetFundsAvailableUE;

/**
 * @author balaaraj
 *
 */

public class RDSGetFundsAvailableUEImpl implements YFSGetFundsAvailableUE{
	
	YFCLogCategory logger = YFCLogCategory.instance(this.getClass());


	/**
	 * Overridden method implementing Stub for YFSCollectionStoredValueCardUE.collectionStoredValueCard
	 * Returns a default output - always approved
	 * 
	 */
	
	public Document getFundsAvailable(YFSEnvironment arg0, Document arg1) throws YFSUserExitException {
		logger.debug("Input Document "+SCXmlUtil.getString(arg1));
		String requestedAmnt = arg1.getDocumentElement().getAttribute("MaxChargeLimit");
		if(Double.parseDouble(requestedAmnt)==0){
			requestedAmnt = "20000.00";
		}
		Document outDoc = SCXmlUtil.createDocument("PaymentMethod");
		outDoc.getDocumentElement().setAttribute("FundsAvailable", requestedAmnt);
		logger.debug("Output Document "+SCXmlUtil.getString(outDoc));
		return outDoc;
	}
	
	
}
