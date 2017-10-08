<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/Order">
	<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:vertexinc:o-series:tps:7:0">
	 <soapenv:Header/>
	 <soapenv:Body>
      <urn:VertexEnvelope>
         <urn:Login>
			<urn:UserName>
				<xsl:value-of select="@VertexUsername"/>
			</urn:UserName>
			<urn:Password>
				<xsl:value-of select="@VertexPassword"/>
			</urn:Password>
			<!--<urn:TrustedId>
				<xsl:value-of select="@TrustedId"/>
			</urn:TrustedId>-->
		 </urn:Login>
		 <InvoiceRequest>
			<xsl:attribute name="documentNumber"><xsl:value-of select="@OrderNo" /></xsl:attribute>
			<xsl:attribute name="documentDate"><xsl:value-of select="@OrderDate" /></xsl:attribute>
			<xsl:attribute name="returnAssistedParametersIndicator">false</xsl:attribute>
			<xsl:attribute name="transactionType">SALE</xsl:attribute>
			<Currency>
				<xsl:attribute name="isoCurrencyCodeAlpha">
					<xsl:value-of select="PriceInfo/@EnterpriseCurrency" />
				</xsl:attribute>
			</Currency>
			<Customer>
				<xsl:if test="PersonInfoShipTo">
					<Destination>
						<StreetAddress1><xsl:value-of select="PersonInfoShipTo/@AddressLine1"/></StreetAddress1> 
						<StreetAddress2><xsl:value-of select="PersonInfoShipTo/@AddressLine2"/></StreetAddress2>
						<City><xsl:value-of select="PersonInfoShipTo/@City"/></City> 
						<MainDivision><xsl:value-of select="PersonInfoShipTo/@State"/></MainDivision> 
						<PostalCode><xsl:value-of select="PersonInfoShipTo/@ZipCode"/></PostalCode> 
						<Country><xsl:value-of select="PersonInfoShipTo/@Country"/></Country>  
					</Destination>
				</xsl:if>
			</Customer>
			<xsl:for-each select="OrderLines/OrderLine">
				<LineItem>
					<xsl:if test="OrderStatuses/OrderStatus/Details/@ExpectedShipmentDate">
						<xsl:attribute name="taxDate">
							<xsl:value-of select="OrderStatuses/OrderStatus/Details/@ExpectedShipmentDate"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="lineItemNumber">
						<xsl:value-of select="@PrimeLineNo"/>
					</xsl:attribute>
					<Product>
						<xsl:attribute name="productClass">
							<xsl:value-of select="Item/@TaxProductCode"/>
						</xsl:attribute>
						<xsl:value-of select="Item/@ItemID"/>
					</Product> 
					<xsl:if test="LineOverallTotals/@LineTotalWithoutTax">
						<UnitPrice>
						<xsl:value-of select="LineOverallTotals/@LineTotalWithoutTax div @OrderedQty"/>
						</UnitPrice>
					</xsl:if>
					<Quantity>
					<xsl:attribute name="unitOfMeasure">EA</xsl:attribute>
					<!--<xsl:value-of select="substring(Item/@UnitOfMeasure,1,3)"/>-->
						<xsl:value-of select="@OrderedQty"/>
					</Quantity>	
				<FlexibleFields>
					<FlexibleDateField>
						<xsl:attribute name="fieldId">
							<xsl:value-of select="'1'"/>
						</xsl:attribute>
						<xsl:value-of select="/Order/@OrderDate"/>
					</FlexibleDateField>
					<xsl:if test="/Order/@DocumentType = '0001'">
						<FlexibleCodeField>	
								<xsl:attribute name="fieldId">
									<xsl:value-of select="'2'"/>
								</xsl:attribute>
							<xsl:value-of select="'N'"/>
						</FlexibleCodeField>
					</xsl:if>
					<xsl:if test="/Order/@DocumentType != '0001'">
						<FlexibleCodeField>	
								<xsl:attribute name="fieldId">
									<xsl:value-of select="'2'"/>
								</xsl:attribute>
							<xsl:value-of select="'Y'"/>
						</FlexibleCodeField>	
					</xsl:if>
					<FlexibleCodeField>	
						<xsl:attribute name="fieldId">
							<xsl:value-of select="'3'"/>
						</xsl:attribute>
						<xsl:value-of select="/Order/PersonInfoShipTo/@FirstName"/>
					</FlexibleCodeField>
					<FlexibleCodeField>	
						<xsl:attribute name="fieldId">
							<xsl:value-of select="'4'"/>
						</xsl:attribute>
						<xsl:value-of select="substring(Item/@ItemID,1,40)"/>
					</FlexibleCodeField>
				</FlexibleFields>
				</LineItem>
			</xsl:for-each>
			<xsl:for-each select="HeaderCharges/HeaderCharge">
				<xsl:if test="@IsDiscount != 'Y'">
					<LineItem>
						<xsl:if test="/Order/OrderStatuses/OrderStatus/Details/@ExpectedShipmentDate">
							<xsl:attribute name="taxDate">
								<xsl:value-of select="/Order/OrderStatuses/OrderStatus/Details/@ExpectedShipmentDate"/>
							</xsl:attribute>
						</xsl:if>
						<Product>
						<xsl:choose>
							<xsl:when test="@ChargeCategory = 'CR_SHIP_CHRG'">
								<xsl:attribute name="productClass">40000</xsl:attribute>
							</xsl:when>
							<!-- <xsl:when test="@ChargeCategory = 'CR_GIFTWRAP_CHRG'">
								<xsl:attribute name="productClass">18450</xsl:attribute>
							</xsl:when> -->
						</xsl:choose>
							<xsl:value-of select="@ChargeName"/>
						</Product>
						<UnitPrice>
							<xsl:value-of select="@ChargeAmount"/>
						</UnitPrice>
						<Quantity>1</Quantity>
						<FlexibleFields>
							<xsl:if test="/Order/@DocumentType = '0001'">
								<FlexibleCodeField>	
										<xsl:attribute name="fieldId">
											<xsl:value-of select="'2'"/>
										</xsl:attribute>
									<xsl:value-of select="'N'"/>
								</FlexibleCodeField>
							</xsl:if>
							<xsl:if test="/Order/@DocumentType != '0001'">
								<FlexibleCodeField>	
										<xsl:attribute name="fieldId">
											<xsl:value-of select="'2'"/>
										</xsl:attribute>
									<xsl:value-of select="'Y'"/>
								</FlexibleCodeField>	
							</xsl:if>
							<xsl:if test="/Order/PersonInfoShipTo/@FirstName">
								<FlexibleCodeField>	
									<xsl:attribute name="fieldId">
										<xsl:value-of select="'3'"/>
									</xsl:attribute>
									<xsl:value-of select="/Order/PersonInfoShipTo/@FirstName"/>
								</FlexibleCodeField>
							</xsl:if>
							<xsl:if test="@ChargeName">
								<FlexibleCodeField>	
									<xsl:attribute name="fieldId">
										<xsl:value-of select="'4'"/>
									</xsl:attribute>
									<xsl:value-of select="@ChargeName"/>
								</FlexibleCodeField>
							</xsl:if>
						</FlexibleFields>
					</LineItem>
				</xsl:if>
			</xsl:for-each>
		 </InvoiceRequest>
		 </urn:VertexEnvelope></soapenv:Body></soapenv:Envelope></xsl:template></xsl:stylesheet>