<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
   <xsl:template match="/">
      <Inbox>
         <xsl:attribute name="ActiveFlag">
            <xsl:value-of select="'Y'" />
         </xsl:attribute>

         <xsl:attribute name="EnterpriseKey">
            <xsl:value-of select="//MonitorConsolidation/Order/@EnterpriseCode" />
         </xsl:attribute>

         <xsl:attribute name="OrderNo">
            <xsl:value-of select="//MonitorConsolidation/Order/@OrderNo" />
         </xsl:attribute>

         <xsl:attribute name="OrderHeaderKey">
            <xsl:value-of select="//MonitorConsolidation/Order/@OrderHeaderKey" />
         </xsl:attribute>

         <xsl:attribute name="ExceptionType">
            <xsl:value-of select="'Returns Receipt Delayed'" />
         </xsl:attribute>
          <xsl:attribute name="QueueId">
            <xsl:value-of select="'ReturnsReceiptDelayedQueue'" />
         </xsl:attribute>
         <xsl:attribute name="Description">
            <xsl:value-of select="'Return Order Stuck in Release Status from last 2 or more days'" />
         </xsl:attribute>
         <xsl:attribute name="DetailDescription">
            <xsl:value-of select="'Return Order Stuck in Release Status from last 2 or more days'" />
         </xsl:attribute>

         <Order>
            <xsl:attribute name="EnterpriseKey">
               <xsl:value-of select="//MonitorConsolidation/Order/@EnterpriseCode" />
            </xsl:attribute>

            <xsl:attribute name="OrderNo">
               <xsl:value-of select="//MonitorConsolidation/Order/@OrderNo" />
            </xsl:attribute>

            <xsl:attribute name="OrderHeaderKey">
               <xsl:value-of select="//MonitorConsolidation/Order/@OrderHeaderKey" />
            </xsl:attribute>
            <xsl:attribute name="DocumentType">
               <xsl:value-of select="//MonitorConsolidation/Order/@DocumentType" />
            </xsl:attribute>
         </Order>
      </Inbox>
   </xsl:template>
</xsl:stylesheet>

