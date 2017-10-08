<?xml version="1.0" encoding="UTF-8"?>
<!--
  Licensed Materials - Property of IBM
  Copyright IBM Corp. 2014, 2016
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:java="http://xml.apache.org/xslt/java" 
exclude-result-prefixes="java" 
xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"  xmlns:pay="http://www.ibm.com/xmlns/prod/commerce/payment" 
xmlns:wcf="http://www.ibm.com/xmlns/prod/commerce/wcfdation">
   <xsl:output method="xml" indent="yes" />
     <xsl:template match="/">
      <xsl:variable name="chargeType">
		<xsl:choose>
			<xsl:when test="(//replyMessage/ccCaptureReply/reasonCode !='')
			or (//replyMessage/ccCreditReply/reasonCode != '') ">CHARGE</xsl:when>
			<xsl:when test="(//replyMessage/ccAuthReply/reasonCode != '')">AUTHORIZATION</xsl:when>
			<xsl:when test="(//replyMessage/ccAuthReversalReply/reasonCode != '')">AUTHORIZATION</xsl:when>
		</xsl:choose>
      </xsl:variable>
       <xsl:variable name="fraudStatus">
		<xsl:value-of select="//replyMessage/reasonCode"/>
      </xsl:variable> 
      <xsl:variable name="dataInvalid">
		<xsl:value-of select="//replyMessage/invalidField"/>
      </xsl:variable>
      <xsl:variable name="dataMissing">
		<xsl:value-of select="//replyMessage/missingField"/>
      </xsl:variable>
     
           <PaymentMethods>
	          <PaymentMethod>
			      <PaymentDetails>
				<xsl:choose>
					<xsl:when test="//replyMessage/ccAuthReply/reasonCode != ''">
						<xsl:attribute name="TransactionCode">
							<xsl:choose>
								<xsl:when test="($fraudStatus = 'REVIEW') or ($fraudStatus = 'REJECT')">
									<xsl:value-of select="//replyMessage/reasonCode"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="//replyMessage/ccAuthReply/reasonCode"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="TransactionFailedData">
							<xsl:choose>
								<xsl:when test="$dataInvalid != ''">
									<xsl:value-of select="$dataInvalid"/>
								</xsl:when>
								<xsl:when test="$dataMissing != ''">
									<xsl:value-of select="$dataMissing"/>
								</xsl:when>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="AuthReturnMessage">
							<xsl:value-of select="//replyMessage/decision"/>
						</xsl:attribute>
						<xsl:attribute name="ChargeType"><xsl:value-of select="$chargeType"/></xsl:attribute>
						<xsl:attribute name="TotalAuthorized">
							<xsl:value-of select="//replyMessage/ccAuthReply/amount"/>
						</xsl:attribute>
						<xsl:attribute name="authTime">
							<xsl:value-of select="//replyMessage/ccAuthReply/authorizedDateTime"/>
						</xsl:attribute>
						<xsl:attribute name="AuthReturnCode">
							<xsl:value-of select="//replyMessage/ccAuthReply/authorizationCode"/>
						</xsl:attribute>
						<xsl:attribute name="AuthorizationID">
							<xsl:value-of select="//replyMessage/requestID"/>
						</xsl:attribute>
						
						
					</xsl:when>
					<xsl:when test="//replyMessage/ccCaptureReply/reasonCode != '' ">
						<xsl:attribute name="TransactionCode">
							<xsl:value-of select="//replyMessage/ccCaptureReply/reasonCode"/>
						</xsl:attribute>
						
						<xsl:attribute name="TransactionFailedData">
							<xsl:choose>
								<xsl:when test="$dataInvalid != ''">
									<xsl:value-of select="$dataInvalid"/>
								</xsl:when>
								<xsl:when test="$dataMissing != ''">
									<xsl:value-of select="$dataMissing"/>
								</xsl:when>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="ChargeType"><xsl:value-of select="$chargeType"/></xsl:attribute>
						<xsl:attribute name="TotalAuthorized">
							<xsl:value-of select="//replyMessage/ccCaptureReply/amount"/>
						</xsl:attribute>
						<xsl:attribute name="TranTime">
							<xsl:value-of select="//replyMessage/ccCaptureReply/requestDateTime"/>
						</xsl:attribute>
						<xsl:attribute name="RequestId">
							<xsl:value-of select="//replyMessage/requestID"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="//replyMessage/ccCreditReply/reasonCode != ''">
						<xsl:attribute name="TransactionCode">
							<xsl:value-of select="//replyMessage/ccCreditReply/reasonCode"/>
						</xsl:attribute>
						
						<xsl:attribute name="TransactionFailedData">
							<xsl:choose>
								<xsl:when test="$dataInvalid != ''">
									<xsl:value-of select="$dataInvalid"/>
								</xsl:when>
								<xsl:when test="$dataMissing != ''">
									<xsl:value-of select="$dataMissing"/>
								</xsl:when>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="ChargeType"><xsl:value-of select="$chargeType"/></xsl:attribute>
						<xsl:attribute name="TotalAuthorized">
							<xsl:value-of select="//replyMessage/ccCreditReply/amount"/>
						</xsl:attribute>
						<xsl:attribute name="TranTime">
							<xsl:value-of select="//replyMessage/ccCreditReply/requestDateTime"/>
						</xsl:attribute>
						<xsl:attribute name="RequestId">
							<xsl:value-of select="//replyMessage/requestID"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="(//replyMessage/ccAuthReversalReply/reasonCode != '')">
						<xsl:attribute name="TransactionCode">
							<xsl:value-of select="//replyMessage/ccAuthReversalReply/reasonCode"/>
						</xsl:attribute>
						<xsl:attribute name="TransactionFailedData">
							<xsl:choose>
								<xsl:when test="$dataInvalid != ''">
									<xsl:value-of select="$dataInvalid"/>
								</xsl:when>
								<xsl:when test="$dataMissing != ''">
									<xsl:value-of select="$dataMissing"/>
								</xsl:when>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="ChargeType"><xsl:value-of select="$chargeType"/></xsl:attribute>
						
						<xsl:attribute name="TotalAuthorized">
							<xsl:value-of select="//replyMessage/ccAuthReversalReply/amount"/>
						</xsl:attribute>
						<xsl:attribute name="TranTime">
							<xsl:value-of select="//replyMessage/ccAuthReversalReply/requestDateTime"/>
						</xsl:attribute>
						<xsl:attribute name="RequestId">
							<xsl:value-of select="//replyMessage/requestID"/>
						</xsl:attribute>
						
					</xsl:when>
					<xsl:when test="//replyMessage/reasonCode != ''">
						<xsl:attribute name="TransactionCode">
							<xsl:value-of select="//replyMessage/reasonCode"/>
						</xsl:attribute>
						
					</xsl:when>
					<xsl:when test="//Fault/faultcode != ''">
						<xsl:attribute name="TransactionCode">
							<xsl:value-of select="//Fault/faultcode"/>
						</xsl:attribute>
						<xsl:attribute name="TransactionMessage">
							<xsl:value-of select="//Fault/faultstring"/>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
			     </PaymentDetails>
               </PaymentMethod>
           </PaymentMethods>	
   </xsl:template>
</xsl:stylesheet>