package com.ibm.rds.order.condition;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.rds.util.SterlingUtil;
import com.ibm.rds.util.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * Class CheckNodeTypeOnRelease checks the node type and based on the node type
 * different services are invoked.
 */
public class CheckNodeTypeOnRelease implements YCPDynamicConditionEx
{

   private static final YFCLogCategory log = YFCLogCategory.instance( CheckNodeTypeOnRelease.class );

   /**
    * Method checkNodeType checks the nodetype
    *
    * @param env
    * @param inputDoc
    * @return boolean
    * @throws Exception
    */
   @Override
   public boolean evaluateCondition( YFSEnvironment env, String arg1, Map arg2, Document inputDoc )
   {

      Document docShipNodeInXML = null;
      Document docShipNodeOutXML = null;
      Document docShipNodeTemplate = null;
      Element eleShipNodeRoot = null;
      Element eleShipNodeTemp = null;
      String strNodeType = null;

      try
      {
         docShipNodeTemplate = XMLUtil.createDocument( "ShipNodeList" );
      }
      catch ( ParserConfigurationException e1 )
      {
    	  log.debug( "Exception occurred :" + e1.getMessage() );
      }
      eleShipNodeTemp = XMLUtil.createChild( docShipNodeTemplate.getDocumentElement(), "ShipNode" );
      eleShipNodeTemp.setAttribute( "ShipNode", "" );
      eleShipNodeTemp.setAttribute( "NodeType", "" );

      try
      {
         docShipNodeInXML = XMLUtil.createDocument( "ShipNode" );
      }
      catch ( ParserConfigurationException e1 )
      {
    	  log.debug( "Exception occurred :" + e1.getMessage() );
      }
      eleShipNodeRoot = docShipNodeInXML.getDocumentElement();
      eleShipNodeRoot.setAttribute( "ShipNode", XMLUtil.getAttribute( inputDoc.getDocumentElement(), "ShipNode" ) );

      // Call getShipNodeList
      try
      {
         if ( log.isDebugEnabled() )
         {
            log.debug( "Input XML to getShipNodeList :" + XMLUtil.getXMLString( docShipNodeInXML ) );
         }

         docShipNodeOutXML = SterlingUtil.invokeAPI( env, docShipNodeTemplate, "getShipNodeList", docShipNodeInXML );

         if ( log.isDebugEnabled() )
         {
            log.debug( "Output XML From getShipNodeList :" + XMLUtil.getXMLString( docShipNodeOutXML ) );
         }

         if ( null != docShipNodeOutXML )
         {
            eleShipNodeRoot = docShipNodeOutXML.getDocumentElement();
            strNodeType = ( XMLUtil.getChildElement( eleShipNodeRoot, "ShipNode" ) ).getAttribute( "NodeType" );
         }

         if ( null != strNodeType && "DC".equalsIgnoreCase( strNodeType ) )
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      catch ( Exception e )
      {
         throw new YFSException( e.getLocalizedMessage() );
      }

   }

   @Override
   public void setProperties( Map arg0 )
   {
      // TODO Auto-generated method stub

   }

}
