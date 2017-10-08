package com.ibm.rds.shipment.store.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.ycs.japi.ue.YCSdeleteCartonUserExit;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * This class is invoked by YCSdeleteCartonUserExit. This class has below logic
 * 1. Returns DeleteCartonContinue Boolean as false
 * 
 */

public class RDSDeleteCartonUserExitImpl implements YCSdeleteCartonUserExit
{

   private static final YFCLogCategory log = YFCLogCategory.instance( RDSDeleteCartonUserExitImpl.class.getName() );

   public String DeleteCarton( YFSContext context, String input ) throws YFSUserExitException
   {

      Document docInXML = null;
      try
      {
         Element eleInXML = docInXML.getDocumentElement();
         log.debug( "eleInXML: " + SCXmlUtil.getString( eleInXML ) );
      }
      catch ( Exception e )
      {
         throw new YFSUserExitException( e.getLocalizedMessage() );
      }
      return input;
   }

   public boolean DeleteCartonContinue( YFSContext context, String input ) throws YFSUserExitException
   {
      return false;
   }
}
