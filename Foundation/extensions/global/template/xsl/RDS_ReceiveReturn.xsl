<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
   <xsl:template match="/">
      <Receipt>
         <xsl:attribute name="DocumentType">
            <xsl:value-of select="//OrderRelease/Order/@DocumentType" />
         </xsl:attribute>

         <xsl:attribute name="ReceivingNode">
            <xsl:value-of select="//OrderRelease/@ShipNode" />
         </xsl:attribute>

         <xsl:attribute name="OrderNo">
            <xsl:value-of select="//OrderRelease/Order/@OrderNo" />
         </xsl:attribute>

         <xsl:attribute name="OpenReceiptFlag">
            <xsl:value-of select="'Y'" />
         </xsl:attribute>

         <Shipment>
            <xsl:copy-of select="OrderRelease/Order/@*" />
         </Shipment>

         <ReceiptLines>
            <xsl:for-each select="//OrderRelease/OrderLine">
               <ReceiptLine>
                  <xsl:copy-of select="@*" />

                  <xsl:attribute name="Quantity">
                     <xsl:value-of select="OrderStatuses/OrderStatus/@StatusQty" />
                  </xsl:attribute>
               </ReceiptLine>
            </xsl:for-each>
         </ReceiptLines>
      </Receipt>
   </xsl:template>
</xsl:stylesheet>

