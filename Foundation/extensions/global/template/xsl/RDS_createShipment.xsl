<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/" >	
 	    <Shipment>
			<OrderReleases>
				<OrderRelease>
				
				<xsl:attribute name="OrderHeaderKey">  
					<xsl:value-of select="/OrderRelease/@OrderHeaderKey" />
				</xsl:attribute>
				<xsl:attribute name="OrderReleaseKey">  
					<xsl:value-of select="/OrderRelease/@OrderReleaseKey" />
				</xsl:attribute>
				<xsl:attribute name="ReleaseNo">  
					<xsl:value-of select="/OrderRelease/@ReleaseNo" />
				</xsl:attribute>
			
				</OrderRelease>
			</OrderReleases>
		</Shipment>		
	</xsl:template>
</xsl:stylesheet>
			