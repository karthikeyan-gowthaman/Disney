package com.ibm.rds.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//import com.ibm.rds.prints.api.FossilUPSShipConfirmAPI;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class RDSUPSConnect
{

   private static final YFCLogCategory log = YFCLogCategory.instance( RDSUPSConnect.class.getName() );

   public static Document connect( YFSEnvironment yfsEnv, Document docInXML )
   {
      Document doc = null;
      Document shipCartonUEdoc = null;
      String outputStr = null;
      OutputStream outputStream = null;
      // String xmlInputString1 = "<?xml version=\"1.0\"?><AccessRequest xml:lang=\"en-US\"><AccessLicenseNumber>BCB33D6CFE546B26</AccessLicenseNumber><UserId>fossilcom</UserId><Password>Fossl123</Password></AccessRequest>";
      //
      // //Ship Label with small package and custom address
      // String xmlInputString2 =
      // "<?xml version=\"1.0\"?><ShipmentConfirmRequest xml:lang=\"en-US\"><Request><RequestAction>ShipConfirm</RequestAction><RequestOption>nonvalidate</RequestOption></Request><Shipment><Description></Description><Shipper><Name>Fossil  Inc.</Name><ShipperNumber>R78A73</ShipperNumber><TaxIdentificationNumber>1234567877</TaxIdentificationNumber><PhoneNumber>18008428621</PhoneNumber><Address><AddressLine1>10615 Sanden Dr</AddressLine1><City>Dallas</City><StateProvinceCode>TX</StateProvinceCode><PostalCode>75238</PostalCode><CountryCode>US</CountryCode></Address></Shipper><ShipTo><CompanyName>Big  Company</CompanyName><AttentionName>John Doe</AttentionName><PhoneNumber>1231231234</PhoneNumber><Address><AddressLine1>1 Main Avenue</AddressLine1><City>Big City</City><StateProvinceCode>CT</StateProvinceCode><PostalCode>06810</PostalCode><CountryCode>US</CountryCode></Address></ShipTo><ShipFrom><CompanyName>Fossil Inc. </CompanyName><AttentionName>Jack Eagle</AttentionName><TaxIdentificationNumber>1234567877</TaxIdentificationNumber><TaxIDType><Code>1234</Code><Description>dummy value</Description></TaxIDType><PhoneNumber>1234567890</PhoneNumber><Address><AddressLine1>10615 Sanden Dr</AddressLine1><City>Dallas</City><StateProvinceCode>TX</StateProvinceCode><PostalCode>75238</PostalCode><CountryCode>US</CountryCode></Address></ShipFrom><PaymentInformation><Prepaid><BillShipper><AccountNumber>R78A73</AccountNumber></BillShipper></Prepaid></PaymentInformation><RateInformation><NegotiatedRatesIndicator></NegotiatedRatesIndicator><RateChartIndicator></RateChartIndicator></RateInformation><Service><Code>14</Code><Description>11</Description></Service><Package><Description>Package Description</Description><PackagingType><Code>02</Code><Description>Customer Supplied</Description></PackagingType><PackageWeight><UnitOfMeasurement><Code></Code></UnitOfMeasurement><Weight>2.0</Weight></PackageWeight><LargePackageIndicator></LargePackageIndicator><ReferenceNumber><Code>00</Code><Value>Y123456789</Value></ReferenceNumber><AdditionalHandling>0</AdditionalHandling></Package></Shipment><LabelSpecification><LabelPrintMethod><Code>GIF</Code><Description>gif file</Description></LabelPrintMethod><HTTPUserAgent>Mozilla/4.5</HTTPUserAgent><LabelImageFormat><Code>GIF</Code><Description>gif</Description></LabelImageFormat></LabelSpecification></ShipmentConfirmRequest>";
      //
      // String xmlInputString = xmlInputString1+xmlInputString2;
      try
      {
         outputStr = RDSConstant.shipAcceptStubOutput;
         log.info( "Accept Response is:" + outputStr );

         DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         InputSource is = new InputSource();
         is.setCharacterStream( new StringReader( outputStr ) );
         doc = db.parse( is );
         
         // String xmlInputString3 =
         // "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><ShipmentAcceptRequest><Request><TransactionReference><CustomerContext>Customer Comment</CustomerContext></TransactionReference><RequestAction>ShipAccept</RequestAction><RequestOption>1</RequestOption></Request><ShipmentDigest>"+strDigest+"</ShipmentDigest></ShipmentAcceptRequest>";
         //
         // String xmlInputAcceptString = xmlInputString1+xmlInputString3;

         NodeList nodes = doc.getElementsByTagName( "GraphicImage" );
         String strImage = "";
         for ( int i = 0; i < nodes.getLength(); i++ )
         {
            Element element = (Element) nodes.item( i );
            strImage = element.getTextContent();
            log.info( "Image:" + strImage + "\n" );
            log.debug( "Image:" + strImage + "\n" );
         }

         shipCartonUEdoc = RDSXmlUtil.createDocument( "ShipCarton" );
         shipCartonUEdoc.createAttribute( "PierbridgeLabelURL" );
         shipCartonUEdoc.createAttribute( "TrackingNumber" );
         Element shipCartonUEele = shipCartonUEdoc.getDocumentElement();
         log.info( "strImage.substring(0, 10000)==" + strImage.substring( 0, 10000 ) );
         log.debug( "strImage.substring(0, 10000)==" + strImage.substring( 0, 10000 ) );
         shipCartonUEele.setAttribute( "PierbridgeLabelURL", "data:image/gif;base64," + strImage.substring( 0, 10000 ) );

         shipCartonUEele.setAttribute( "TrackingNumber", "1231231231" );
         Element eleReturnTrackingDetails = RDSXmlUtil.createChild( shipCartonUEele, "ReturnTrackingDetails" );
         RDSXmlUtil.createChild( eleReturnTrackingDetails, "ReturnTrackingDetail" );

         log.debug( "shipCartonUEdoc inside stub==" + RDSXmlUtil.getXMLString( shipCartonUEdoc ) );
         log.info( "shipCartonUEdoc inside stub==" + RDSXmlUtil.getXMLString( shipCartonUEdoc ) );

      }
      catch ( Exception e )
      {
         log.info( "Error sending data to server" );
         log.error(e.getMessage());
      }
      finally
      {
         if ( outputStream != null )
         {
            try
            {
               outputStream.close();
            }
            catch ( IOException e )
            {
              log.error(e.getMessage());
            }
            outputStream = null;
         }
      }

      log.info( "just before returning" );
      return shipCartonUEdoc;

   }

   public static String readURLConnection( URLConnection uc ) throws Exception
   {
      StringBuffer buffer = new StringBuffer();
      BufferedReader reader = null;
      try
      {
         reader = new BufferedReader( new InputStreamReader( uc.getInputStream() ) );
         int letter = 0;
         reader.readLine();
         while ( ( letter = reader.read() ) != -1 )
         {
            buffer.append( (char) letter );
         }
         reader.close();
      }
      catch ( Exception e )
      {
         log.info( "Could not read from URL: " + e.toString() );
         throw e;
      }
      finally
      {
         if ( reader != null )
         {
            reader.close();
            reader = null;
         }
      }
      return buffer.toString();
   }

   public static void main( String[] args )
   {
      connect( null, null );
   }

}
