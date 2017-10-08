package com.ibm.rds.shipment.store.ue;

import java.util.Properties;

import org.w3c.dom.Document;

import com.ibm.rds.common.util.RDSCommonUtil;
import com.ibm.rds.common.util.RDSConstant;
import com.ibm.rds.common.util.RDSUPSConnect;
import com.ibm.rds.common.util.RDSXmlUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class RDSShipCartonHandler implements YIFCustomApi
{

   protected static YFCLogCategory log        = YFCLogCategory.instance( RDSShipCartonHandler.class.getName() );

   private Properties              properties = null;

   public void setProperties( Properties properties ) throws Exception
   {
      this.properties = properties;
   }

   public Document handleShipCarton( YFSEnvironment env, Document arg1 ) throws YFSUserExitException
   {
      // EnableStub=Y/N
      String stubSwitch = properties.getProperty( "EnableStub" );
      log.debug( "EnableStub property is : " + stubSwitch );
      Document docShipCartonOutXML = null;
      try
      {
         if ( "N".equalsIgnoreCase( stubSwitch ) )
         {
            docShipCartonOutXML = RDSCommonUtil.invokeService( env, RDSConstant.ABC_STORE_UPS_LABEL_SYNC_SERVICE, arg1 );
            log.info( "stub output xml: " + RDSXmlUtil.getXMLString( docShipCartonOutXML ) );
         }
         else
         {
            docShipCartonOutXML = RDSUPSConnect.connect( env, null );
            log.info( "docShipCartonOutXML: " + SCXmlUtil.getString( docShipCartonOutXML.getDocumentElement() ) );
         }

      }
      catch ( Exception e )
      {
         throw new YFSUserExitException( e.getLocalizedMessage() );
      }
      log.info( "docShipCartonOutXML: " + SCXmlUtil.getString( docShipCartonOutXML.getDocumentElement() ) );
      return docShipCartonOutXML;
   }

}
