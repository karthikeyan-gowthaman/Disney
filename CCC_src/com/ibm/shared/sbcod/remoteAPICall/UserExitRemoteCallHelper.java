package com.ibm.shared.sbcod.remoteAPICall;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

//import com.sterlingcommerce.pca.sbc.business.extensibilty.SBCExtensibilityUtils;
//import com.sterlingcommerce.pca.sbc.business.remoteAPICall.UserExitDataProcessor;
//import com.sterlingcommerce.shared.sbcod.SBCODErrorCodes;
import com.ibm.shared.sbcod.httppost.MessageContext;
import com.ibm.shared.sbcod.httppost.XMLOverHttpClient;
//import com.yantra.shared.dbi.YFS_User_Exit_Exn_Config;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.ycp.common.YCPDomUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.util.YFSUserExistConsts;

public class UserExitRemoteCallHelper {


	private static final Map<String,String> UEOrgXPath=new HashMap<String,String>();
	

	static {
		//Item Details UE
		UEOrgXPath.put("ycm.ue.getItemDetails.class", "xml:/Item/@OrganizationCode");
		//Opportunity User Exit
		UEOrgXPath.put("yfs.ue.getOpportunityID.class", "xml:/Opportunity/@EnterpriseCode");
		//Order UE
		UEOrgXPath.put("yfs.ue.beforeCreateOrder.class", "xml:/Order/@EnterpriseCode");
		UEOrgXPath.put("yfs.ue.getOrderNo.class", "xml:/ExtendedUserExit/@EnterpriseCode");
		UEOrgXPath.put("yfs.ue.verifyOrder.class", "xml:/ExtendedUserExit/@EnterpriseKey");

		//Quote UE
		UEOrgXPath.put("yfs.ue.beforeChangeOrder.class", "xml:/Order/@EnterpriseCode");
		UEOrgXPath.put("yfs.ue.beforeCreateOrder.class", "xml:/Order/@EnterpriseCode");
		
		// Payment Collection UE
		UEOrgXPath.put(YFSUserExistConsts.YFS_COLLECTION_CREDIT_CARD_UE, "xml:/Payment/@EnterpriseCode");
		UEOrgXPath.put(YFSUserExistConsts.YFS_VALIDATE_INVOKED_COLLECTION_UE,"xml:/Payment/@EnterpriseCode");
		//PayPal UE
		UEOrgXPath.put(YFSUserExistConsts.YFS_COLLECTION_OTHERS_UE, "xml:/Payment/@EnterpriseCode");

	}

	private static YFCLogCategory cat = YFCLogCategory.instance(UserExitRemoteCallHelper.class.getName());
	private final static String USER_EXIT_ENABLED = "Y";
	private static String originalOrganizationCode = null;



	/**
	 * Method is used to Post the User Exit XML to the Remote Server.
	 * Configurations(Remote URL, Timeout value) for the User Exit are retrieved
	 * from YFS_USER_EXIT_EXN_CONFIG based on UE Property Name. In case of No
	 * Response from REmote Server within the Timeout value, Exception will be
	 * thrown.
	 * 
	 * @param env
	 * @param inDoc
	 * @param uePropertyName
	 * @return
	 */
	public static Document processUserExitDataToHttpPost(YFSEnvironment env,Document inDoc, String uePropertyName) {

		cat.beginTimer("processUserExitDataToHttpPost");	
		if (cat.isDebugEnabled()) cat.debug("User Exit PropertyName : "+uePropertyName);
		YFCElement inElem = YFCDocument.getDocumentFor(inDoc).getDocumentElement();

		if (cat.isDebugEnabled()) cat.debug("@@@@ Input Element : User Exit @@@"+inElem);
		if (cat.isDebugEnabled()) cat.debug("@@@@ UE Map Size : User Exit @@@ "+UEOrgXPath.size());
		if (cat.isDebugEnabled()) cat.debug("@@@@ xpath String : User Exit @@@ "+UEOrgXPath.get(uePropertyName));
		Document responseDoc = null;
		YFSContext ctx = (YFSContext)env;

		String entpriseCode=YCPDomUtils.getAttributeFromPath(inElem,UEOrgXPath.get(uePropertyName));

		if(entpriseCode==null){
			cat.debug(" Enterprise Code or Organization Code are Mandatory");	
			//YFCException ex = new YFCException(SBCODErrorCodes.SBCOD_MISSING_MANDATORY_PARAMS_IN_EVENT_TEMPLATE);
			//throw ex;	
		}

		ctx.setOrganizationCode(entpriseCode);

		/**
		 * Get the Extended User Exit Configuration based on UE Property Name.
		 */
		//YFS_User_Exit_Exn_Config extendedUEDBObject = SBCExtensibilityUtils.getExtendedUserExitConfigByPropertyName(ctx, uePropertyName);

		/*if(YFCObject.isVoid(extendedUEDBObject)) {
			cat.error("Did not find any User Exit Configuration for the Property Name");
			return inDoc;
		}	*/	

		/**
		 * If User Exit is : 
		 *  Enabled - Post the UE XML to Remote URL.
		 *  Disabled - Return the UE XML(Input) back. 
		 */
		//if(USER_EXIT_ENABLED.equalsIgnoreCase(extendedUEDBObject.getIs_Active())) {
		if(USER_EXIT_ENABLED.equalsIgnoreCase("Y")) {

			/**
			 * Set the Remote URL and Timeout value in MessageContext
			 */
			MessageContext msgCtx = new MessageContext();
			//msgCtx.setMsgURL(extendedUEDBObject.getRemote_Url());
			//msgCtx.setTimeout(Integer.valueOf(extendedUEDBObject.getTimeoutValue()));

			/**
			 * Set Type="UserExit" and UserExitName in the XML being posted.
			 */
			//inDoc = stampExtendedUserExitData(inDoc,extendedUEDBObject.getuser_Exit_Name(),extendedUEDBObject.getUser_Exit_Description());

			/**
			 * Save the Incoming Organization Code present in Input XML being posted.
			 */
			setOriginalOrganizationCode(inDoc);

			/**
			 * Set EnterpriseCode in XML being posted if not present
			 */
			if(YFCObject.isVoid(originalOrganizationCode)) {
				YFCElement docElement = YFCDocument.getDocumentFor(inDoc).getDocumentElement();
				if(!YFCObject.isVoid(ctx.getOrganizationCode())) {
					docElement.setAttribute("EnterpriseCode", ctx.getOrganizationCode());
				} else {
					//docElement.setAttribute("EnterpriseCode", extendedUEDBObject.getOrganization_Code());
				}
			}

			try{
				/**
				 * Post the XML data through HTTPPost
				 */
				XMLOverHttpClient postUEData = new XMLOverHttpClient();
				responseDoc = postUEData.post(inDoc, msgCtx);

			}
			catch(YFCException e){	
				cat.error("Time Out Exception!!!No Response Received from Remote JVM in Simulated Time.",e);	
				throw e;
			}

			if(YFCObject.isNull(responseDoc)) {
				cat.debug("Response Object is Null.");
			} else {
				/**
				 * Stamp the (Incoming)Organization Code which was saved prior to posting the XML.
				 */
				responseDoc = stampOriginalOrganizationCode(responseDoc,uePropertyName);



				/**
				 * Check for Failure Messages (if any)  
				 */
				responseDoc = processUERespone(responseDoc);
			}


		} else {
			/**
			 * If Extended User Exit is DISABLED, return the Input UE XML as response. 
			 */
			responseDoc = inDoc;
		}		

		cat.endTimer("processUserExitDataToHttpPost");	

		return responseDoc;
	}

	/**
	 * 
	 * @param responseDoc
	 */
	private static Document processUERespone(Document responseDoc) {

		cat.beginTimer("processUERespone");

		YFCDocument userExitDoc = YFCDocument.getDocumentFor(responseDoc);		
		YFCElement userExitElement = userExitDoc.getDocumentElement();
		String failureMsg = userExitElement.getAttribute("Status");
		if(!YFCObject.isVoid(failureMsg) && "failure".equalsIgnoreCase(failureMsg)) {
			cat.error("Exception occured in Extension JVM : "+failureMsg);
			//YFCException ex = new YFCException(SBCODErrorCodes.SBCOD_REMOTE_RESPONSE_FAILED);
			//throw ex;	
		}

		cat.endTimer("processUERespone");

		return responseDoc;

	}

	/**
	 * Method stamps the (Incoming)Organization Code which was saved prior to
	 * posting the UE XML to Remote Server. This is to ensure that the same
	 * Organization Code is returned back to the API which invoked this User
	 * Exit.
	 * 
	 * @param responseDoc
	 * @return Document 
	 */
	private static Document stampOriginalOrganizationCode(Document responseDoc,String uePropertyName) {

		cat.beginTimer("stampOriginalOrganizationCode");

		YFCDocument userExitDoc = YFCDocument.getDocumentFor(responseDoc);		
		YFCElement userExitElement = userExitDoc.getDocumentElement();
		Set attributeKeySet = userExitElement.getAttributes().keySet();

		if (attributeKeySet.contains("EnterpriseCode")
				&& !YFCObject.equals(originalOrganizationCode,
						userExitElement.getAttribute("EnterpriseCode"))) {
			userExitElement.setAttribute("EnterpriseCode",
					originalOrganizationCode);
		} else if (attributeKeySet.contains("EnterpriseKey")
				&& !YFCObject.equals(originalOrganizationCode,
						userExitElement.getAttribute("EnterpriseKey"))) {
			userExitElement.setAttribute("EnterpriseKey",
					originalOrganizationCode);
		} else if (attributeKeySet.contains("OrganizationCode")
				&& !YFCObject.equals(originalOrganizationCode,
						userExitElement.getAttribute("OrganizationCode"))) {
			userExitElement.setAttribute("OrganizationCode",
					originalOrganizationCode);
		}


		cat.endTimer("stampOriginalOrganizationCode");


		return responseDoc;

	}

	/**
	 * Method saves the (Incoming)Organization Code in the Input UE XML. This
	 * Organization Code is later on stamped on the Response Document received
	 * from Remote Server.
	 * 
	 * @param inDoc
	 */
	private static void setOriginalOrganizationCode(Document inDoc) {

		cat.beginTimer("setOriginalOrganizationCode");

		YFCDocument userExitDoc = YFCDocument.getDocumentFor(inDoc);
		YFCElement userExitElement = userExitDoc.getDocumentElement();
		Set attributeKeySet = userExitElement.getAttributes().keySet();

		if(attributeKeySet.contains("EnterpriseCode")) {
			originalOrganizationCode = userExitElement.getAttribute("EnterpriseCode");
		} else if(attributeKeySet.contains("EnterpriseKey")) {
			originalOrganizationCode = userExitElement.getAttribute("EnterpriseKey");
		} else if(attributeKeySet.contains("OrganizationCode")) {
			originalOrganizationCode = userExitElement.getAttribute("OrganizationCode");
		}

		if (cat.isDebugEnabled()) cat.debug("Incoming Organization Code Saved : "+originalOrganizationCode);

		cat.endTimer("stampOriginalOrganizationCode");


	}

	/**
	 * Method stamps Type="UserExit" and UserExitName in the Document being
	 * posted to Remote Server
	 * 
	 * @param inDoc
	 * @return Document
	 */
	private static Document stampExtendedUserExitData(Document inDoc, String userExitName, String userExitDesc) {

		YFCElement docElement = YFCDocument.getDocumentFor(inDoc).getDocumentElement();
		docElement.setAttribute("Type", "UserExit");
		//docElement.setAttribute(YFS_User_Exit_Exn_Config.USER_EXIT_NAME, userExitName);
		//docElement.setAttribute(YFS_User_Exit_Exn_Config.USER_EXIT_DESCRIPTION, userExitDesc);

		return docElement.getOwnerDocument().getDocument();

	}



}
