<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
        version="1.0"  xmlns:urn="urn:vertexinc:o-series:tps:7:0" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"> 
	<xsl:template match="/">
		<Order>
			<xsl:attribute name="OrderNo">
				<xsl:value-of select="soapenv:Envelope/soapenv:Body/urn:VertexEnvelope/urn:InvoiceResponse/@documentNumber" />
			</xsl:attribute>
			<xsl:attribute name="OrderDate">
				<xsl:value-of select="soapenv:Envelope/soapenv:Body/urn:VertexEnvelope/urn:InvoiceResponse/@documentDate" />
			</xsl:attribute>
			<PriceInfo>
				<xsl:attribute name="EnterpriseCurrency">
					<xsl:value-of select="soapenv:Envelope/soapenv:Body/urn:VertexEnvelope/urn:InvoiceResponse/urn:SubTotal/@isoCurrencyCodeAlpha" />
				</xsl:attribute>
			</PriceInfo>
			<HeaderTaxes>
				<xsl:for-each select="soapenv:Envelope/soapenv:Body/urn:VertexEnvelope/urn:InvoiceResponse/urn:LineItem ">
					<xsl:if test="not(@lineItemNumber)">
						<HeaderTax>
							<xsl:attribute name="TaxPercentage">
								<xsl:value-of select="urn:Taxes/urn:EffectiveRate" />
							</xsl:attribute>
							<xsl:attribute name="Tax">
								<xsl:value-of select="urn:TotalTax" />
							</xsl:attribute>
							<xsl:attribute name="ChargeName">
								<xsl:value-of select="urn:Product"/>
							</xsl:attribute>
							<xsl:choose>
								<xsl:when test="urn:Product/@productClass = '40000'">
									<xsl:attribute name="ChargeCategory">Shipping</xsl:attribute>
									<xsl:attribute name="TaxName">Shipping Tax</xsl:attribute>
								</xsl:when>
								<!-- <xsl:when test="urn:Product/@productClass = '18450'">
									<xsl:attribute name="ChargeCategory">Shipping</xsl:attribute>
									<xsl:attribute name="TaxName">Shipping Tax</xsl:attribute>
								</xsl:when> -->
							</xsl:choose>
						</HeaderTax>
					</xsl:if>
				</xsl:for-each>
			</HeaderTaxes>
			<HeaderCharges>
				<xsl:for-each select="soapenv:Envelope/soapenv:Body/urn:VertexEnvelope/urn:InvoiceResponse/urn:LineItem ">
					<xsl:if test="not(@lineItemNumber)">
						<HeaderCharge>
							<xsl:attribute name="ChargeName">
								<xsl:value-of select="urn:Product"/>
							</xsl:attribute>
							<xsl:choose>
								<xsl:when test="urn:Product/@productClass = '40000'">
									<xsl:attribute name="ChargeCategory">Shipping</xsl:attribute>
								</xsl:when>
								 <!--  <xsl:when test="urn:Product/@productClass = '18450'">
								  	<xsl:attribute name="ChargeCategory">Shipping</xsl:attribute>
									
								</xsl:when> -->  
							</xsl:choose>
							<xsl:attribute name="ChargeAmount">
								<xsl:value-of select="urn:UnitPrice"/>
							</xsl:attribute>
						</HeaderCharge>
					</xsl:if>
				</xsl:for-each>
			</HeaderCharges>
			<OrderLines>
				<xsl:for-each select="soapenv:Envelope/soapenv:Body/urn:VertexEnvelope/urn:InvoiceResponse/urn:LineItem">
				<xsl:if test="@lineItemNumber">
					<OrderLine>
						<xsl:attribute name="PrimeLineNo">
							<xsl:value-of select="@lineItemNumber" />
						</xsl:attribute>
						<xsl:attribute name="OrderedQty">
							<xsl:value-of select="urn:Quantity" />
						</xsl:attribute>
						<Item>
							<xsl:attribute name="ItemID">
								<xsl:value-of select="urn:Product" />
							</xsl:attribute>
							<xsl:attribute name="UnitOfMeasure">
								<xsl:value-of select="'EACH'" />
							</xsl:attribute>
							<xsl:attribute name="ProductClass">
								<xsl:value-of select="'GOOD'" />
							</xsl:attribute>
						</Item>
						<LineOverallTotals>
							<xsl:attribute name="LineTotalWithoutTax">
								<xsl:value-of select="urn:UnitPrice * urn:Quantity" />
							</xsl:attribute>
						</LineOverallTotals>
						<LineTaxes>
							<LineTax>
								<xsl:attribute name="Tax">
									<xsl:value-of select="urn:TotalTax"/>
								</xsl:attribute>
								<xsl:attribute name="TaxPercentage">
									<xsl:value-of select="urn:Taxes/urn:EffectiveRate"/>
								</xsl:attribute>
								<xsl:attribute name="TaxName">Shipping Tax</xsl:attribute>
								<xsl:attribute name="ChargeCategory">Shipping</xsl:attribute>
								<xsl:attribute name="ChargeName">Shipping Tax</xsl:attribute>
							</LineTax>
						</LineTaxes>
						<LineCharges>
							<LineCharge>
								<xsl:attribute name="ChargeAmount">
									<xsl:value-of select="'2'"/>
								</xsl:attribute>
								<xsl:attribute name="ChargePerUnit">
									<xsl:value-of select="'1'"/>
								</xsl:attribute>
								<xsl:attribute name="ChargeCategory">Shipping</xsl:attribute>
								<xsl:attribute name="ChargeName">Shipping Charge</xsl:attribute>
							</LineCharge>
						</LineCharges>
					</OrderLine>
				</xsl:if>
			</xsl:for-each>
		</OrderLines>
	</Order>
</xsl:template>
</xsl:stylesheet>
								
								
								
								
								
								
								
								