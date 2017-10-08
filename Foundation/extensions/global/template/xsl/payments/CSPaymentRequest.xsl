<?xml version="1.0" encoding="UTF-8" ?>
<!--
  Licensed Materials - Property of IBM
  Copyright IBM Corp. 2014, 2016
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:pay="http://www.ibm.com/xmlns/prod/commerce/payment" xmlns:wcf="http://www.ibm.com/xmlns/prod/commerce/wcfdation"
	xmlns:xalan="http://xml.apache.org/xslt"
	xmlns:mm="http://WCToSSFSMediationModule"
	xmlns:mediationUtil="xalan://com.ibm.commerce.sample.mediation.util.MediationUtil"
	xmlns="http://www.sterlingcommerce.com/documentation/YFS/createOrder/input"
	xmlns:java="http://xml.apache.org/xslt/java"
	xmlns:udt="http://www.openapplications.org/oagis/9/unqualifieddatatypes/1.1"
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
	xmlns:urn="urn:schemas-cybersource-com:transaction-data-1.130"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:oa="http://www.openapplications.org/oagis/9"
	version="1.0">
	
	<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes" indent="no" />
	<xsl:strip-space elements="*" />
	<xsl:template name="distinctvalues">
        <xsl:param name="values"/>
        <xsl:variable name="firstvalue" select="substring-before($values, ':')"/>
        <xsl:variable name="restofvalue" select="substring-after($values, ':')"/>
        <xsl:if test="contains($values, ':') = false">
            <xsl:value-of select="$values"/>
        </xsl:if>
        <xsl:if test="contains($restofvalue, $firstvalue) = false">
            <xsl:value-of select="$firstvalue"/>
          <xsl:text>,</xsl:text>
        </xsl:if>
        <xsl:if test="$restofvalue != ''">
            <xsl:call-template name="distinctvalues">
                <xsl:with-param name="values" select="$restofvalue" />
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
   <xsl:template match="/">
	<xsl:variable name="test">
	    <xsl:for-each select='//Order/OrderLines/OrderLine'>
                <xsl:value-of select="PersonInfoShipTo/@PersonInfoKey"/>
		<xsl:value-of select="':'"/>
	    </xsl:for-each>	
        </xsl:variable>
	
	<xsl:variable name="distinctPersonInfoKey">
	<xsl:call-template name="distinctvalues">
            <xsl:with-param name="values" select="$test" />
        </xsl:call-template>
	</xsl:variable>
	<xsl:variable name="currency" select="//Order/PriceInfo/@Currency"/>
	<xsl:variable name="chargeTranKey" select="//Order/@ChargeTransactionKey"/>
	<xsl:variable name="amount" 
	select="//Order/ChargeTransactionDetails/ChargeTransactionDetail[@ChargeTransactionKey=$chargeTranKey]/@RequestAmount"/>
	<xsl:variable name="requestID"  select="//Order/PaymentMethods/PaymentMethod[@PaymentType='CREDIT_CARD']/PersonInfoBillTo/@LastName"/>
	<xsl:variable name="expDate" select="//Order/ChargeTransactionDetails/ChargeTransactionDetail[@ChargeTransactionKey=$chargeTranKey]/PaymentMethod/@CreditCardExpDate"/>
	
	<xsl:variable name="month" select="substring-before($expDate, '-')"/>
	<xsl:variable name="year" select="substring-after($expDate, '-')"/>
	<xsl:variable name="isRevAuth" select="//Order/@reverseAuthFlag"/>
	
    

		<xsl:for-each select="//Order/ChargeTransactionDetails/ChargeTransactionDetail[@ChargeTransactionKey=$chargeTranKey]">
		<!-- Value of transaction type -->
		<xsl:variable name="actionValue">
			<xsl:choose>
				<xsl:when test="@ChargeType='AUTHORIZATION' and @RequestAmount > 0 and @AuthorizationID=''">
					<xsl:value-of select="'AUTHORIZATION'"/>
				</xsl:when>
				<xsl:when test="@ChargeType='AUTHORIZATION' and @RequestAmount &lt; 0  and @AuthorizationID !='' ">
					<xsl:value-of select="'REVERSEAUTHORIZATION'"/>
				</xsl:when>
				<xsl:when test="@ChargeType='CHARGE' and @RequestAmount > 0 and @AuthorizationID !=''">
					<xsl:value-of select="'SETTLEMENT'"/>
				</xsl:when>
				<xsl:when test="@ChargeType='CHARGE' and @RequestAmount > 0 and @AuthorizationID=''">
					<xsl:value-of select="'approveAndDeposit'"/>
				</xsl:when>
				<xsl:when test="@ChargeType='CHARGE' and @RequestAmount &lt; 0">
					<xsl:value-of select="'REFUND'"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<urn:requestMessage>
			<urn:merchantID>toryburchadv</urn:merchantID>
			<urn:merchantReferenceCode>S-1234</urn:merchantReferenceCode>
			<xsl:if test="($actionValue = 'AUTHORIZATION') or ($actionValue = 'REFUND') or ($actionValue = 'approveAndDeposit') ">
			<urn:purchaseTotals>
			<urn:currency>USD</urn:currency>
			<urn:grandTotalAmount>
			<xsl:value-of select="($amount >= 0)*$amount - not($amount >= 0)*$amount"/>
			</urn:grandTotalAmount>
			</urn:purchaseTotals>
			<urn:recurringSubscriptionInfo>
				<urn:subscriptionID>
				<xsl:value-of select="//Order/PaymentMethods/PaymentMethod[@PaymentType='CREDIT_CARD']/@CreditCardNo"/>
				</urn:subscriptionID>
			</urn:recurringSubscriptionInfo>
			</xsl:if>
		<xsl:if test="($actionValue = 'AUTHORIZATION') ">
			<urn:ccAuthService run="true">
         </urn:ccAuthService>
		 </xsl:if>
		 <xsl:if test="($actionValue = 'SETTLEMENT') ">
			<urn:purchaseTotals>
				<urn:currency>USD</urn:currency>
				<urn:grandTotalAmount>
				<xsl:value-of select="($amount >= 0)*$amount - not($amount >= 0)*$amount"/>
				</urn:grandTotalAmount>
			</urn:purchaseTotals>
			<urn:ccCaptureService run="true">
				<urn:authRequestID><xsl:value-of select="@AuthorizationID"/></urn:authRequestID>
            </urn:ccCaptureService>
		 </xsl:if>
		 <xsl:if test="($actionValue = 'REVERSEAUTHORIZATION') ">
			<urn:purchaseTotals>
				<urn:currency>USD</urn:currency>
				<urn:grandTotalAmount>
				<xsl:value-of select="($amount >= 0)*$amount - not($amount >= 0)*$amount"/>
				</urn:grandTotalAmount>
			</urn:purchaseTotals>
			<urn:ccAuthReversalService run="true">
                <urn:authRequestID><xsl:value-of select="@AuthorizationID"/></urn:authRequestID>
            </urn:ccAuthReversalService>
            <urn:businessRules>
                <urn:ignoreAVSResult>true</urn:ignoreAVSResult>
            </urn:businessRules>
		 </xsl:if>
		 <xsl:if test="($actionValue = 'REFUND') ">
			<urn:ccCreditService run="true"/>
		 </xsl:if>
		 <xsl:if test="($actionValue = 'approveAndDeposit') ">
			<urn:ccAuthService run="true" />
			<urn:ccCaptureService run="true"/>
		 </xsl:if>
		 </urn:requestMessage>
		</xsl:for-each>
   </xsl:template>
</xsl:stylesheet>