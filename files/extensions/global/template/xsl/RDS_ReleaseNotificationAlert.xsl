<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="/MonitorConsolidation/Order">
	<Inbox>
		<xsl:attribute name="ActiveFlag">Y</xsl:attribute>

		<xsl:attribute name="AutoResolvedFlag">N</xsl:attribute>

		<xsl:attribute name="QueueId">RDS_BACKLOG_RELEASE_QUEUE</xsl:attribute>

		<xsl:attribute name="Status">OPEN</xsl:attribute>

		<xsl:attribute name="ExceptionType">BACKLOG EXCEPTION_FOR_RELEASE</xsl:attribute>

		<xsl:attribute name="Description">Order Held in Released Status and Not Shipped or Cancelled Before 48 Hrs of Expected Delivery date</xsl:attribute>

		<xsl:attribute name="OrderNo">
		<xsl:value-of select="@OrderNo" />
		</xsl:attribute>

		<xsl:attribute name="EnterpriseCode">
		<xsl:value-of select="@EnterpriseCode" />
		</xsl:attribute>

		<xsl:attribute name="OrderHeaderKey">
		<xsl:value-of select="@OrderHeaderKey" />
		</xsl:attribute>
	</Inbox>
</xsl:template>
</xsl:stylesheet>
