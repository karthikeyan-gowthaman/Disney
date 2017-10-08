/*******************************************************************************
 * IBM Confidential
 * OCO Source Materials
 * 5725-F55
 * Copyright IBM Corporation 2011 
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 ******************************************************************************/

package com.ibm.pca.sbc.business.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.shared.dbclasses.YFS_Document_ParamsDBHome;
import com.yantra.shared.dbi.YFS_Document_Params;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dblayer.PLTQueryBuilder;
import com.yantra.yfc.dblayer.PLTQueryOperator;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class SBCApiUtils {

public static final String COPYRIGHT = "Copyright IBM Corporation 2011.";
private static YFCLogCategory cat = YFCLogCategory.instance(SBCApiUtils.class.getName());
	
	public static YFSContext  getContext(YFSEnvironment env) {
		if ( env instanceof YFSContext )
			return (YFSContext)env;
		else
			return null;
	}
	
	
public static YFCDocument invokeAPI(YFSEnvironment env, String sMethodName, YFCDocument inDoc,boolean isService) {
		
        try {
        	YIFApi oApi = YIFClientFactory.getInstance().getApi();
            Document outDoc = null;
            if(isService){
            	outDoc = oApi.executeFlow(env, sMethodName, inDoc.getDocument());                                             
            }else{
            	outDoc = oApi.invoke(env, sMethodName, inDoc.getDocument());
            }
            return (YFCObject.isNull(outDoc)) ? null : YFCDocument.getDocumentFor(outDoc);
        }catch(YFSException yfsEx) {
        	throw new YFCException(yfsEx, yfsEx.getErrorCode());
        }catch (YFCException yfcEx) {
        	throw yfcEx;
        }catch (Exception ex) {
        	throw new YFCException(ex);
        }
	}

public static Map<String, List<String>> getDocumentAttrMap(YFSEnvironment env,  String documentType) 
{	
	cat.beginTimer("getDocumentAttrMap");
	if (cat.isDebugEnabled()) cat.debug("getDocumentAttrMap :: documentType:" + documentType);
	
	YFSContext ctx                                  = SBCApiUtils.getContext(env);
	YFS_Document_ParamsDBHome documentParamDBObject = YFS_Document_ParamsDBHome.getInstance();
	YFS_Document_Params documentParams              = null;
	List<YFS_Document_Params> lRecords              = null;
	
	Map<String, List<String>> docMap = new HashMap<String, List<String>>();
	String docType        = null;
	String docDescription = null;
	String docParamsKey   = null;
	
	if (documentType != null)
	{
  	  try
	  {
            PLTQueryBuilder pltQuery = new PLTQueryBuilder();
        
            pltQuery.clear().appendWHERE().appendString(
        		     documentParamDBObject.getColumnName(YFS_Document_Params.DOCUMENT_TYPE),
      		             PLTQueryOperator.EQUALS,documentType);
        
    	    if (cat.isDebugEnabled()) cat.debug("WHERE Clause for getDocumentAttrMap "+ pltQuery.getReadableWhereClause());
    	
    	    documentParams = documentParamDBObject.selectWithWhere(ctx, pltQuery);
    	    lRecords       = documentParamDBObject.listWithWhere(SBCApiUtils.getContext(env), pltQuery, Integer.MAX_VALUE);	
	  }
  	  catch(Exception e) 
	  {
		cat.error("Exception Occur while Creating Document Attributes Map" + e);
		cat.endTimer("getDocumentAttrMap");
	  }
	
	  if(  /*!YFCObject.isVoid(lRecords) &&*/  documentParams != null)
	  {
  		  docType        = documentParams.getDocument_Type();
		  docDescription = documentParams.getDescription();
		  docParamsKey   = documentParams.getDocument_Params_Key();
		  
		  if (cat.isDebugEnabled()) cat.debug("getDocumentAttrMap::docType =" + docType);
		  if (cat.isDebugEnabled()) cat.debug("getDocumentAttrMap::docDesc =" + docDescription);
		  if (cat.isDebugEnabled()) cat.debug("getDocumentAttrMap::docParamsKey =" + docParamsKey);
	  }
	  else
	  {
	      cat.debug("ERROR: getDocumentAttrMap::NULL Document  Attributes !!");
	  }
	  
    }// end if (documentType != null)
	
	List<String> docAttrsList = new ArrayList<String>();
	docAttrsList.add(docDescription);
	docAttrsList.add(docParamsKey);

    //Put element to MAP
	docMap.put(docType, docAttrsList);
	
		
	cat.endTimer("getDocumentAttrMap");	
	return docMap;

}


private static Document getTemplateForDocList(){

	String templateDocList="<DocumentParamsList><DocumentParams DocumentType=\"\" Description=\"\" /></DocumentParamsList>";
	return YFCDocument.getDocumentFor(templateDocList).getDocument();
}

/**
 * Method getCommonCodeValue fetches the common code value
 * @param env
 * @param codeType
 * @return commonCodeValue
 * @throws Exception
 */
public static String getCommonCodeValue(YFSEnvironment env,String codeType) throws Exception {
	YFCDocument getCommonCodeListInDoc = YFCDocument.createDocument("CommonCode");
	YFCElement getCommonCodeListInEle = getCommonCodeListInDoc.getDocumentElement();
	getCommonCodeListInEle.setAttribute("CodeType", codeType);
	YFCDocument getCommonCodeListOutDoc = invokeAPI(env, "getCommonCodeList", getCommonCodeListInDoc, false);
	
	if(getCommonCodeListOutDoc.getElementsByTagName("CommonCode").getLength()==0){
		return "";
	}

	YFCElement commonCodeListEle = getCommonCodeListOutDoc.getDocumentElement();
	String code = commonCodeListEle.getChildElement("CommonCode").getAttribute("CodeValue");
	return code;
}

}
