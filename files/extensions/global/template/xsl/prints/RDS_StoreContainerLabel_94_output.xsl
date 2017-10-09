<?xml version="1.0" encoding="UTF-8" ?>
<!--
Licensed Materials - Property of IBM
IBM Sterling Selling and Fulfillment Suite - Foundation
(C) Copyright IBM Corp. 2007, 2012 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" encoding="UTF-8" indent="yes" />
	
	<xsl:variable name="pierbridge_server_url">
		<xsl:value-of select="/MultiApi/API[@Name='getProperty']/Output/GetProperty/@PropertyValue" />
	</xsl:variable>
	<!--xsl:variable name="pierbridge_image_url">
		<xsl:value-of select="replace($pierbridge_server_url, 'xmlservice', 'image')" />
	</xsl:variable-->
	
	<xsl:variable name="pierbridge_image_url">
		<xsl:value-of select="substring-before($pierbridge_server_url,'xmlservice')"/>
		<xsl:value-of select="'image'"/>
		<xsl:value-of select="substring-after($pierbridge_server_url,'xmlservice')"/>
	</xsl:variable>
	
	<xsl:template match="/MultiApi">		
				<xsl:for-each select="API[(normalize-space(@Name) = &quot;getTrackingNoAndPrintLabel&quot;)]">
					<xsl:for-each select="Output/getTrackingNoAndPrintLabel/SuccessfulContainers/Container">
					<html>


			<script disable-output-escaping="yes" type="text/javascript">
			<![CDATA[
				function printPage()
					{								
						setTimeout(function() {				  	
						  window.document.close();
						  window.focus();
						  print(window);	
						 
						}, 1000);			
					}
				]]>		
			</script>


						<body>
								<style>
								 img {
										margin-top:20px;
										width:98mm;
										height:auto;
										margin-left:3mm;
										display: block
										}		
							</style>
							<style type="text/css" media="print">
								@page 
								{
									size: auto;   /* auto is the current printer page size */
									margin: 0mm;  /* this affects the margin in the printer settings */
								}

								body 
								{
									background-color:#FFFFFF; 
									border: solid 1px white ;
									margin: 0px;  /* the margin on the content before printing */
									color:#FFFFFF; 
								}
								img {
									margin-top:16px; /* This pushes the image down off the bottom(In this case since it's reversed the bottom is at the top) to push the image onto the correct location for the tab*/
									width:98mm; /* sets the image width for printing*/
									height:auto; /* allows the imgage to scale correctly based on the desired width without calculations of original size */
									margin-left:3mm; /* pushes the left edge off the movement threshold */
								}
		
							</style>
						<xsl:if test="@PierbridgeLabelURL">
						<image >					
								<xsl:attribute name="src">
									<!--xsl:value-of select="$pierbridge_image_url" />-->
									<!--xsl:text>&#038;</xsl:text-->
									<!--xsl:text>%3F</xsl:text>-->
									<xsl:value-of select="@PierbridgeLabelURL" />
								</xsl:attribute>
						</image> 
												
						</xsl:if>
						
						<xsl:for-each select="ContainerReturnTrackingList/ContainerReturnTracking">
							<xsl:if test="@PierbridgeReturnLabelURL">
							
								<image width="650px" height="550px">
									<xsl:attribute name="src">
										<!--xsl:value-of select="$pierbridge_image_url" />-->
										<!--xsl:text>&#038;</xsl:text-->
										<!--xsl:text>%3F</xsl:text>-->
										<xsl:value-of select="@PierbridgeReturnLabelURL" />
									</xsl:attribute>
								</image>
								
							</xsl:if>
						</xsl:for-each>
						</body>
					</html> 
					</xsl:for-each>
					
					<xsl:for-each select="Output/getTrackingNoAndPrintLabel/FailedContainers/Container">
						<Failed/>
					</xsl:for-each>
				</xsl:for-each>
			
	</xsl:template>
	
</xsl:stylesheet>