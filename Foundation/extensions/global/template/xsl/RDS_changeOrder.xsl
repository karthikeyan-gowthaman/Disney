<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/" >	
 	    <Order>
			<xsl:attribute name="OrderHeaderKey">  
				<xsl:value-of select="/Order/@OrderHeaderKey" />
			</xsl:attribute>
			<OrderLines>
			<xsl:for-each select="Order/OrderLines/OrderLine">
				<OrderLine>
				
				<xsl:attribute name="OrderLineKey">  
					<xsl:value-of select="@OrderLineKey" />
				</xsl:attribute>
				<!--  <xsl:attribute name="FulfillmentType">SHIP_FROM_DC_THEN_STORE</xsl:attribute> -->
				<xsl:attribute name="SCAC">UPSN</xsl:attribute>
				<xsl:attribute name="ScacAndService">UPSNND</xsl:attribute>
				<xsl:attribute name="FreightTerms">PREPAID</xsl:attribute>
			
				</OrderLine>
			</xsl:for-each>
			</OrderLines>
		</Order>		
	</xsl:template>
</xsl:stylesheet>
			