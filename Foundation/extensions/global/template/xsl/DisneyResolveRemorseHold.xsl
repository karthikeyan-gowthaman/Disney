<?xml version="1.0" encoding="UTF-8" ?>
<!--
  Licensed Materials - Property of IBM
  Copyright IBM Corp. 2014, 2016
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xalan="http://xml.apache.org/xslt"
	xmlns="http://www.sterlingcommerce.com/documentation/YFS/createOrder/input"
	xmlns:java="http://xml.apache.org/xslt/java"
	xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1"
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
	xmlns:urn="urn:schemas-cybersource-com:transaction-data-1.130"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:oa="http://www.openapplications.org/oagis/9"
	version="1.0">
	
	<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes" indent="no" />
	<xsl:strip-space elements="*" />
	
    
   <xsl:template match="/">
	 <Order>
	     <xsl:attribute name="OrderNo">
           <xsl:value-of select="//Order/@OrderNo" />
         </xsl:attribute>
         <xsl:attribute name="EnterpriseCode">
           <xsl:value-of select="//Order/@EnterpriseCode" />
         </xsl:attribute>
         <xsl:attribute name="DocumentType">0001</xsl:attribute>
         <xsl:attribute name="Action">MODIFY</xsl:attribute>
		 <OrderHoldTypes>
			<OrderHoldType>
				<xsl:attribute name="HoldType">REMORSE_HOLD</xsl:attribute>
				<xsl:attribute name="ReasonText">Remorse Period has Crossed</xsl:attribute>
				<xsl:attribute name="Status">1300</xsl:attribute>
			</OrderHoldType>
		 </OrderHoldTypes>
	 </Order>
   </xsl:template>
</xsl:stylesheet>