package com.ibm.rds.payment.api;

import java.util.Properties;

import org.w3c.dom.Document;

import com.ibm.rds.util.RDSConstants;
import com.ibm.rds.util.BaseCustomAPI;
import com.ibm.rds.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class RDSSVSStubResponseCode  extends BaseCustomAPI implements RDSConstants{

			private static final YFCLogCategory log = YFCLogCategory.instance(RDSSVSStubResponseCode.class.getName());
			
			Properties props;
			/**
			 * This method is overridden from interface.
			 * 
			 * @param properties
			 *            Properties set
			 */
			public void setProperties(Properties props) {
				log.debug("props:"+props);
				this.props = props;
			}

			public Document stubResponse(YFSEnvironment env, Document docInput) throws Exception {
				log.debug("RDSSVSStubResponseCode - Enter");

				docInput.getDocumentElement().setAttribute("authorizationId", props.getProperty(AuthorizationId));
				docInput.getDocumentElement().setAttribute("internalReturnCode", props.getProperty(InternalReturnCode));
				docInput.getDocumentElement().setAttribute("authCode", props.getProperty(AuthCode));
				docInput.getDocumentElement().setAttribute("tranReturnCode", props.getProperty(TranReturnCode));
				docInput.getDocumentElement().setAttribute("tranReturnFlag", props.getProperty(TranReturnFlag));
				docInput.getDocumentElement().setAttribute("tranReturnCode", props.getProperty(RequestID));
				docInput.getDocumentElement().setAttribute("retryFlag", props.getProperty(RetryFlag));
				docInput.getDocumentElement().setAttribute("internalReturnCode_settle", props.getProperty(InternalReturnCode_settle));
				docInput.getDocumentElement().setAttribute("tranReturnFlag_settle", props.getProperty(TranReturnFlag_settle));
				docInput.getDocumentElement().setAttribute("retryFlag_settle", props.getProperty(RetryFlag_settle));
	    		 
				log.debug("RDSSVSStubResponseCode - Exit"+XMLUtil.getXMLString(docInput));
		return docInput;		
}
			
		}