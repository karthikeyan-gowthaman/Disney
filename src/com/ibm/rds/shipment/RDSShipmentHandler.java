package com.ibm.rds.shipment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class RDSShipmentHandler{
	
	private static YFCLogCategory logger = YFCLogCategory.instance(RDSShipmentHandler.class);
	private YIFApi api = null;
	
	public Document flagConfirmShipment(YFSEnvironment env,Document inDoc){
		  
		  logger.debug("&&&&&&&&&&&&&&&&&&&&& inside the method flagConfirmShipment "+SCXmlUtil.getString(inDoc));
		  Element orderRelEle = inDoc.getDocumentElement();
	      logger.debug( "Event document is : " + SCXmlUtil.getString(orderRelEle));
	      String oHeaderKey = orderRelEle.getAttribute( "OrderHeaderKey" );
	      String oReleaseKey = orderRelEle.getAttribute( "OrderReleaseKey" );
	      String relNo = orderRelEle.getAttribute( "ReleaseNo" );
	      
	      long millis = System.currentTimeMillis();

	      Document shipInp = SCXmlUtil.createDocument( "Shipment");
	      Element shipEle = shipInp.getDocumentElement();
	      shipEle.setAttribute( "ConfirmShip", "Y" );
	      shipEle.setAttribute( "TrackingNo", String.valueOf(millis));

	      Element orderReleasesEle = SCXmlUtil.createChild(shipEle, "OrderReleases"); 
	      Element orderReleaseEle = SCXmlUtil.createChild(orderReleasesEle, "OrderRelease");

	      orderReleaseEle.setAttribute( "OrderHeaderKey", oHeaderKey );
	      orderReleaseEle.setAttribute( "OrderReleaseKey", oReleaseKey );
	      orderReleaseEle.setAttribute( "ReleaseNo", relNo );
	      try
	      {
	    	 api = YIFClientFactory.getInstance().getApi();
	    	 api.invoke( env, "createShipment", shipInp);
	      }
	      catch ( Exception e )
	      {
	    	  logger.debug( "Exception occurred during createShipment : " + e.getMessage());
	      }
		return inDoc;
	}
}
