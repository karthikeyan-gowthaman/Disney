<?xml version='1.0'?>
<!--
  Licensed Materials - Property of IBM
  5725-G69
  Copyright IBM Corporation 2011,2012. All Rights Reserved.
  US Government Users Restricted Rights- Use, duplication or disclosure
  restricted by GSA ADP Schedule Contract with IBM Corp.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output method="xml" version="1.0" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
   <xsl:variable name="CreditCardPaymentType"><xsl:text>CREDIT_CARD</xsl:text></xsl:variable>
   <xsl:variable name="GiftCardPaymentType"><xsl:text>GIFT_CARD</xsl:text></xsl:variable>
   <xsl:variable name="PayPalPaymentType"><xsl:text>PAYPAL</xsl:text></xsl:variable>
   <xsl:variable name="CreditCardChargeType"><xsl:text>CardAuthorize</xsl:text></xsl:variable>
   <xsl:variable name="PayPalChargeType"><xsl:text>PayPalReauthorize</xsl:text></xsl:variable>
   <xsl:variable name="GiftCardChargeType"><xsl:text>GiftCardAuthorize</xsl:text></xsl:variable>
   <xsl:variable name="CollectChargeType"><xsl:text>Collect</xsl:text></xsl:variable>
   <xsl:variable name="ChargeType"><xsl:text>CHARGE</xsl:text></xsl:variable>
   <xsl:variable name="AuthorizationChargeType"><xsl:text>AUTHORIZATION</xsl:text></xsl:variable>
   <xsl:variable name="CardVoidChargeType"><xsl:text>CardVoid</xsl:text></xsl:variable>
   <xsl:variable name="RefundChargeType"><xsl:text>Refund</xsl:text></xsl:variable>
   <xsl:template match="/">
      <PaymentDoc>
         <xsl:copy-of select="*" />
         <PaymentGatewayInput>
            <xsl:apply-templates select="/Payment" />
         </PaymentGatewayInput>
      </PaymentDoc>
   </xsl:template>

   <xsl:template match="Payment">
      <TRX>
         <xsl:choose>
            <xsl:when test="@ChargeType=$ChargeType">
               <SVC>
			   <xsl:choose>
					<xsl:when test="@RequestAmount &lt; 0">
						<xsl:value-of select="$RefundChargeType"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$CollectChargeType"/>
					</xsl:otherwise>
				</xsl:choose>
			   </SVC>
            </xsl:when>
			<xsl:when test="@ChargeType=$AuthorizationChargeType">
				<xsl:if test="@RequestAmount > 0">
					<xsl:choose>
						<xsl:when test="@PaymentType=$CreditCardPaymentType">
							<SVC><xsl:value-of select="$CreditCardChargeType"/></SVC>
						</xsl:when>
						<xsl:when test="@PaymentType=$GiftCardPaymentType">
							<SVC><xsl:value-of select="$GiftCardChargeType"/></SVC>
						</xsl:when>
					</xsl:choose>
				 </xsl:if>
				<xsl:if test="@RequestAmount &lt; 0">
					<SVC><xsl:value-of select="$CardVoidChargeType"/></SVC>
				</xsl:if>
			</xsl:when>
			<xsl:when test="@PaymentType=$CreditCardPaymentType">
               <SVC><xsl:value-of select="$CreditCardChargeType"/></SVC>
            </xsl:when>
			<xsl:when test="@PaymentType=$GiftCardPaymentType">
               <SVC><xsl:value-of select="$GiftCardChargeType"/></SVC>
            </xsl:when>
            <xsl:otherwise>
               <SVC><xsl:value-of select="$PayPalChargeType"/></SVC>
            </xsl:otherwise>
         </xsl:choose>

		 <PRJ>IBMINDIA</PRJ>
		 
		 <CTY><xsl:value-of select="@BillToCountry" /></CTY>
		 <COM>Internet</COM>
				 		 
		 <xsl:choose>
			<xsl:when test="@PaymentType=$PayPalPaymentType">
                <XRF><xsl:value-of select="@PaymentReference1" /></XRF>
            </xsl:when>
			<xsl:when test="@PaymentType=$GiftCardPaymentType">
                <XRF><xsl:value-of select="@PaymentReference1" /></XRF>
            </xsl:when>
            <xsl:otherwise>
				<xsl:if test="@RequestAmount > 0">
					<xsl:choose>
						<xsl:when test="@ChargeType=$ChargeType">
							<XRF><xsl:value-of select="@PaymentReference1" /></XRF>
						</xsl:when>
						<xsl:otherwise>
							<XRF><xsl:value-of select="@CreditCardNo" /></XRF>
						</xsl:otherwise>
					</xsl:choose>
				 </xsl:if>
				 <xsl:if test="@RequestAmount &lt; 0">
					<XRF><xsl:value-of select="@PaymentReference1" /></XRF>
				 </xsl:if>
			</xsl:otherwise>
         </xsl:choose>
         <INV><xsl:value-of select="@InvoiceNo" /></INV>
         <ORD><xsl:value-of select="@OrderNo" /></ORD>
         <CUR><xsl:value-of select="@Currency" /></CUR>
		    
         <NET>
			<xsl:choose>
				<xsl:when test="@RequestAmount &lt; 0">
					<xsl:value-of select="format-number(0 - @RequestAmount, '0.00')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@RequestAmount"/>
				</xsl:otherwise>
			</xsl:choose>
		 </NET>
         <TAX>0.00</TAX>
         <GRS>
			<xsl:choose>
				<xsl:when test="@RequestAmount &lt; 0">
					<xsl:value-of select="format-number(0 - @RequestAmount, '0.00')" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@RequestAmount"/>
				</xsl:otherwise>
			</xsl:choose>
		 </GRS>
		 <CRD>
			<xsl:choose>
				<xsl:when test="@PaymentType=$CreditCardPaymentType">
					<PAN><xsl:value-of select="@CreditCardNo" /></PAN> 
					<EXP><xsl:value-of select="@CreditCardExpirationDate" /></EXP> 
				</xsl:when>
				<xsl:when test="@PaymentType=$GiftCardPaymentType">
					<GCN><xsl:value-of select="@SvcNo" /></GCN> 
					<GCP><xsl:value-of select="@PaymentReference3" /></GCP> 
				</xsl:when>
			</xsl:choose>
		</CRD>
 
      </TRX>
   </xsl:template>
</xsl:stylesheet>