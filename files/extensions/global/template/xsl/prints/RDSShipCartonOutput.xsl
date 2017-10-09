<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
<xsl:template match="/*">
<ShipCarton>
	<xsl:attribute name="PierbridgeLabelURL">
		<xsl:text>data:image/gif;base64,</xsl:text>
		<xsl:value-of select="/ShipmentAcceptResponse/ShipmentResults/PackageResults/LabelImage/GraphicImage/text()"/>
	</xsl:attribute>
	<xsl:attribute name="TrackingNumber">
		<xsl:value-of select="/ShipmentAcceptResponse/ShipmentResults/PackageResults/TrackingNumber/text()"/>
	</xsl:attribute>
	<ReturnTrackingDetails>
		<ReturnTrackingDetail>
			<xsl:attribute name="PierbridgeReturnLabelURL">
				<xsl:text>data:image/gif;base64,</xsl:text>
				<xsl:value-of select="/ShipmentAcceptResponse/ShipmentAcceptResponse/ShipmentResults/PackageResults/LabelImage/GraphicImage/text()"/>
			</xsl:attribute>
			<xsl:attribute name="ReturnTrackingNumber">
				<xsl:value-of select="/ShipmentAcceptResponse/ShipmentAcceptResponse/ShipmentResults/PackageResults/TrackingNumber/text()"/>
			</xsl:attribute>
		
		</ReturnTrackingDetail>
	</ReturnTrackingDetails>
</ShipCarton>
</xsl:template>

</xsl:stylesheet>