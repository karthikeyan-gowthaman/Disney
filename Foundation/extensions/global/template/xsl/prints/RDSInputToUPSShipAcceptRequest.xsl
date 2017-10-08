<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
<xsl:template match="/*">
<ShipmentAcceptRequest>
	<Request>
		<RequestAction>
			<xsl:text>ShipAccept</xsl:text>
		</RequestAction>
		<RequestOption>
			<xsl:text>1</xsl:text>
		</RequestOption>
	</Request>
	<ShipmentDigest>
		<xsl:value-of select="/ShipmentConfirmResponse/ShipmentDigest/text()"/>
	</ShipmentDigest>
	<ShipmentAcceptRequest>
		<Request>
			<RequestAction>
				<xsl:text>ShipAccept</xsl:text>
			</RequestAction>
			<RequestOption>
				<xsl:text>1</xsl:text>
			</RequestOption>
		</Request>
		<ShipmentDigest>
			<xsl:value-of select="/ShipmentConfirmResponse/ShipmentConfirmResponse/ShipmentDigest/text()"/>
		</ShipmentDigest>
	
	</ShipmentAcceptRequest>
</ShipmentAcceptRequest>
</xsl:template>

</xsl:stylesheet>