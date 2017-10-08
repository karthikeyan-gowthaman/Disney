package com.ibm.rds.order.ue;
import java.util.Properties;

import org.w3c.dom.Document;

import com.ibm.cs.utils.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * This is YFSOrderRepricingUE implementation of Sales Orders.
 * */
public class RDSVertexStubServiceAPI{
	
	private static YFCLogCategory logger = YFCLogCategory.instance(RDSVertexStubServiceAPI.class);
	private YIFApi api = null;
	
	Properties props;
	/**
	 * This method is overridden from interface.
	 * 
	 * @param properties
	 *            Properties set
	 */
	public void setProperties(Properties props) {
		logger.debug("props:"+props);
		this.props = props;
	}
	
	/**
	 * This method recalculates the header level and line level charges.
	 * 
	 * @param env - Environment Object
	 * @param inDoc - Input Document
	 * @return - Output Document 
	 * @throws YFSUserExitException
	 */
	//public static void main(String[] args) throws Exception{
	public Document getVertexResponse(YFSEnvironment env, Document inDoc) throws Exception{
		logger.debug("RDSVertexStubServiceAPI - inDoc----"+SCXmlUtil.getString(inDoc));
		String filePath = props.getProperty("VERTEX_RESPONSE_FILE");  
		logger.debug("filePath---------"+(filePath));
		Document vertexResponse = XMLUtil.getDocument(filePath);
		logger.debug("vertexResponse----"+SCXmlUtil.getString(vertexResponse));
		return vertexResponse;
	}


}
