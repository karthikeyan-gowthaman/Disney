package com.ibm.rds.shipment.store.ue;

import org.w3c.dom.Document;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.ycs.japi.ue.YCSshipCartonUserExit;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSUserExitException;
//import com.ibm.ezics.backend.util.EzicsCommonUtil;
//import com.ibm.rds.common.util.backend.util.EzicsXmlUtil;
import com.ibm.rds.common.util.RDSCommonUtil;
import com.ibm.rds.common.util.RDSXmlUtil;

/**
 * This class is invoked by YCSshipCartonUserExit. This class has below logic
 * 1. Invokes AbcUPSLabelsSyncService service
 * 2. Appends Package Level detail from UE input to the service output
 * 3. UE Output has the labels and tracking numbers
 */

public class RDSShipCartonUserExitImpl implements YCSshipCartonUserExit
{

   private static final YFCLogCategory log = YFCLogCategory.instance( RDSShipCartonUserExitImpl.class.getName() );

   public String shipCarton( YFSContext env, String arg1 ) throws YFSUserExitException
   {
      return arg1;
   }

   public boolean shipCartonContinue( YFSContext env, String arg1 ) throws YFSUserExitException
   {
      return false;
   }

   public String shipCartonOutXML( YFSContext env, String arg1 ) throws YFSUserExitException
   {
      Document docShipCartonOutXML = null;
      try
      {
         log.info( "Inside RDSShipCartonUserExitImpl*****************" );

         docShipCartonOutXML = RDSCommonUtil.invokeService( env, "RDS_ShipCartonService", arg1 );
         log.info( "stub output xml: " + RDSXmlUtil.getXMLString( docShipCartonOutXML ) );

      }
      catch ( Exception e )
      {
         throw new YFSUserExitException( e.getLocalizedMessage() );
      }
      log.info( "docShipCartonOutXML: " + SCXmlUtil.getString( docShipCartonOutXML.getDocumentElement() ) );
      log.info( "RDSXmlUtil.getXMLString(docShipCartonOutXML): " + RDSXmlUtil.getXMLString( docShipCartonOutXML ) );

      return RDSXmlUtil.getXMLString( docShipCartonOutXML );
   }
}
