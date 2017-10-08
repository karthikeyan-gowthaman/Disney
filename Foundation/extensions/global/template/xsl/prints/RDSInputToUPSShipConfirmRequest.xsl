<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
<xsl:template match="/*">
<ShipmentConfirmRequest>
	<Request>
		<RequestAction>
			<xsl:text>ShipConfirm</xsl:text>
		</RequestAction>
		<RequestOption>
			<xsl:text>nonvalidate</xsl:text>
		</RequestOption>
	</Request>
	<Shipment>
		<Shipper>
			<Name>
				<xsl:text>FOSSIL INC</xsl:text>
			</Name>
			<Address>
			</Address>
		</Shipper>
		<ShipTo>
			<CompanyName>
				<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneeAttention"/>
			</CompanyName>
			<!--<AttentionName>
				<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneeAttention"/>
			</AttentionName>-->
			<PhoneNumber>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneePhone"/>
				</PhoneNumber>
			<Address>
				<AddressLine1>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneeAddress1"/>
				</AddressLine1>
				<AddressLine2>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneeAddress2"/>
				</AddressLine2>
				<AddressLine3>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneeAddress3"/>
				</AddressLine3>
				<City>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneeCity"/>
				</City>
				<CountryCode>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneeCountry"/>
				</CountryCode>
				
				<PostalCode>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneePostalCode"/>
				</PostalCode>
				<StateProvinceCode>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ConsigneeStateProv"/>
				</StateProvinceCode>
			</Address>
		</ShipTo>
		<ShipFrom>
			<CompanyName>
				<xsl:text>FOSSIL INC</xsl:text>
			</CompanyName>
			<Address>
			</Address>
		</ShipFrom>
		<PaymentInformation>
			<Prepaid>
				<BillShipper>
					<AccountNumber>
						<xsl:text>R78A73</xsl:text>
					</AccountNumber>
				</BillShipper>
			</Prepaid>
		</PaymentInformation>
		<Service>
			<Code>
				<xsl:choose> 
						<xsl:when test="/UPSPLD/PackageLevelDetail/@UPSServiceType ='2nd Day Air'">
							<xsl:text>02</xsl:text>						
						</xsl:when>
						<xsl:when test="/UPSPLD/PackageLevelDetail/@UPSServiceType ='Next Day Air'">
							<xsl:text>01</xsl:text>						
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>03</xsl:text>							
						</xsl:otherwise>
				</xsl:choose>
			</Code>
			<Description>
				<xsl:value-of select="/UPSPLD/PackageLevelDetail/@UPSServiceType"/>
			</Description>
		</Service>
		<Package>
			
			<PackagingType>
				<Code>
					<xsl:text>02</xsl:text>
				</Code>
				<Description>
					<xsl:text>Customer Supplied</xsl:text>
				</Description>
			</PackagingType>
			<Dimensions>
				<UnitOfMeasurement>
					<Code>
						<xsl:value-of select="/UPSPLD/PackageLevelDetail/@UOMDim"/>
					</Code>
				</UnitOfMeasurement>
				<Length>
					<xsl:value-of select="/UPSPLD/AccessorialRecord/@PackageLength"/>
				</Length>
				<Width>
					<xsl:value-of select="/UPSPLD/AccessorialRecord/@PackageWidth"/>
				</Width>
				<Height>
					<xsl:value-of select="/UPSPLD/AccessorialRecord/@PackageHeight"/>
				</Height>
			</Dimensions>
			<PackageWeight>
				<Weight>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@PackageActualWeight"/>
				</Weight>
				<UnitOfMeasurement>
					<Code>
						<xsl:value-of select="/UPSPLD/PackageLevelDetail/@UOMWeight"/>
					</Code>	
				</UnitOfMeasurement>
			</PackageWeight>
			<ReferenceNumber>
				<Code>
					<xsl:text>00</xsl:text>
				</Code>
				<Value>
					<xsl:value-of select="/UPSPLD/PackageLevelDetail/@ShipmentKey"/>
				</Value>
			</ReferenceNumber>
			<Description>
				<xsl:text>FOSSIL PACKAGE</xsl:text>
			</Description>
		</Package>
		
	</Shipment>
	<LabelSpecification>
		<LabelPrintMethod>
			<Code>
				<xsl:text>GIF</xsl:text>
			</Code>
			<Description>
				<xsl:text>gif file</xsl:text>
			</Description>
		</LabelPrintMethod>
		<HTTPUserAgent>
			<xsl:text>Mozilla/4.5</xsl:text>
		</HTTPUserAgent>
		<LabelImageFormat>
			<Code>
				<xsl:text>GIF</xsl:text>
			</Code>
			<Description>
				<xsl:text>gif</xsl:text>
			</Description>
		</LabelImageFormat>
	</LabelSpecification>
</ShipmentConfirmRequest>
</xsl:template>

</xsl:stylesheet>