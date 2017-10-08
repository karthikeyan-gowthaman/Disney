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

import java.util.Enumeration;
import java.util.Properties;

import org.w3c.dom.Document;

import com.ibm.pca.om.business.utils.OMODFoundationBridge;
import com.ibm.shared.omod.OMODErrorCodes;
import com.ibm.shared.omod.OMODLiterals;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.util.YFCUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;

public class OMODServiceInvokeProxy implements YIFCustomApi, OMODLiterals {

public static final String COPYRIGHT = "Copyright IBM Corporation 2011,2012.";

	
	private String OMOD_PROXY_SERVICE_PARAMETER_NAME=OMODLiterals.OMOD_PROXY_SERVICE_PARAMETER_NAME;
	private Properties properties = null;
	private static YFCLogCategory cat =	YFCLogCategory.instance(OMODServiceInvokeProxy.class.getName());


	/* (non-Javadoc)
	 * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties prop){
		properties = prop;
		Enumeration keys = properties.propertyNames();
		cat.verbose("OMODServiceInvokeProxy properties...");
		while (keys.hasMoreElements()) {
			String propertyName = (String) keys.nextElement();
			String value = properties.getProperty(propertyName);
			if (cat.isVerboseEnabled()) cat.verbose("Property [" + propertyName + ":" + value + "]");
		}

	}

	public Document invoke(YFSEnvironment env, Document inXML)
	{

		String servicename = null;
		YFCDocument outDoc = null;
		try {

			cat.beginTimer("OMODServiceInvokeProxy invoke()");

			YFCDocument inDoc = YFCDocument.getDocumentFor(inXML);
			cat.debug("OMODServiceInvokeProxy.invoke()");

			if (properties != null)
				servicename = properties.getProperty(OMOD_PROXY_SERVICE_PARAMETER_NAME);

			if (YFCUtils.isVoid(servicename))
			{
				YFCException ex= new YFCException(OMODErrorCodes.OMOD_FIELD_MANDATORY);
				ex.setAttribute(OMOD_PROXY_SERVICE_PARAMETER_NAME,"");
				throw ex;
			}

			if (cat.isDebugEnabled()) cat.debug("Executing Service ["+servicename+"]");

			outDoc = OMODFoundationBridge.getInstance().executeService((YFSContext)env, servicename, inDoc);
			if (outDoc == null){
				outDoc = inDoc;
			}

			if (cat.isDebugEnabled()) cat.debug("Execution of " + servicename + " Completed.");

		} finally {
			cat.endTimer("OMODServiceInvokeProxy invoke()");
		}
		return outDoc.getDocument();
	}    

	public Document send(YFSEnvironment env, Document inXML)
	{
		String servicename = null;
		try {

			cat.beginTimer("OMODServiceInvokeProxy send()");

			YFCDocument inDoc = YFCDocument.getDocumentFor(inXML);
			cat.debug("OMODServiceInvokeProxy.send()");

			if (properties != null)
				servicename = properties.getProperty(OMOD_PROXY_SERVICE_PARAMETER_NAME);

			if (YFCUtils.isVoid(servicename))
			{
				YFCException ex= new YFCException(OMODErrorCodes.OMOD_FIELD_MANDATORY);
				ex.setAttribute(OMOD_PROXY_SERVICE_PARAMETER_NAME,"");
				throw ex;
			}

			if (cat.isDebugEnabled()) cat.debug("Executing Service ["+servicename+"]");

			OMODFoundationBridge.getInstance().executeService((YFSContext)env, servicename, inDoc);

			if (cat.isDebugEnabled()) cat.debug("Execution of " + servicename + " Completed.");

		} finally {
			cat.endTimer("OMODServiceInvokeProxy send()");
		}
		return inXML;
	}

}

