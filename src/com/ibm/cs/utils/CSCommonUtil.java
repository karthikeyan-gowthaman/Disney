package com.ibm.cs.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.icu.math.BigDecimal;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class CSCommonUtil {

		/**
		 * LoggerUtil Instance.
		 */
		private static YFCLogCategory LOGGER = YFCLogCategory.instance(CSCommonUtil.class);
				
		// Utility Class - Mask Constructor
		private CSCommonUtil(){
			
		}
		/**
		 * Instance of YIFApi used to invoke Sterling Commerce APIs or services.
		 */
		private static YIFApi api;

		static {
			try {
				CSCommonUtil.api = YIFClientFactory.getInstance().getApi();
			} catch (Exception e) {
				CSCommonUtil.LOGGER.error("IOM_UTIL_0001", e);
			}
		}
		/**
		 * Invokes a Sterling Commerce API.
		 * 
		 * @param env
		 *            Sterling Commerce Environment Context.
		 * @param templateName
		 *            Name of API Output Template that needs to be set
		 * @param apiName
		 *            Name of API to invoke.
		 * @param inDoc
		 *            Input Document to be passed to the API.
		 * @throws java.lang.Exception
		 *             Exception thrown by the API.
		 * @return Output of the API.
		 */
		public static Document invokeAPI(YFSEnvironment env, String templateName,
				String apiName, Document inDoc) throws Exception {
			env.setApiTemplate(apiName, templateName);
			Document returnDoc = CSCommonUtil.api.invoke(env, apiName, inDoc);
			env.clearApiTemplate(apiName);
			return returnDoc;
		}
		
		/**
		 * Invokes a Sterling Commerce API.
		 * 
		 * @param env
		 *            Sterling Commerce Environment Context.
		 * @param template
		 *            Output template document for the API
		 * @param apiName
		 *            Name of API to invoke.
		 * @param inDoc
		 *            Input Document to be passed to the API.
		 * @throws java.lang.Exception
		 *             Exception thrown by the API.
		 * @return Output of the API.
		 */
		public static Document invokeAPI(YFSEnvironment env, Document template,
				String apiName, Document inDoc) throws Exception {
			env.setApiTemplate(apiName, template);
			Document returnDoc = CSCommonUtil.api.invoke(env, apiName, inDoc);
			env.clearApiTemplate(apiName);
			return returnDoc;
		}
		
		/**
		 * Invokes a Sterling Commerce API.
		 * 
		 * @param env
		 *            Sterling Commerce Environment Context.
		 * @param apiName
		 *            Name of API to invoke.
		 * @param inDoc
		 *            Input Document to be passed to the API.
		 * @throws java.lang.Exception
		 *             Exception thrown by the API.
		 * @return Output of the API.
		 */
		public static Document invokeAPI(YFSEnvironment env, String apiName,
				Document inDoc) throws Exception {
			return CSCommonUtil.api.invoke(env, apiName, inDoc);
		}

		/**
		 * Invokes a Sterling Commerce API.
		 * 
		 * @param env
		 *            Sterling Commerce Environment Context.
		 * @param apiName
		 *            Name of API to invoke.
		 * @param inDocStr
		 *            Input to be passed to the API. Should be a valid XML string.
		 * @throws java.lang.Exception
		 *             Exception thrown by the API.
		 * @return Output of the API.
		 */
		public static Document invokeAPI(YFSEnvironment env, String apiName,
				String inDocStr) throws Exception {
			return CSCommonUtil.api.invoke(env, apiName, YFCDocument.parse(inDocStr)
					.getDocument());
		}

		/**
		 * Invokes a Sterling Commerce Service.
		 * 
		 * @param env
		 *            Sterling Commerce Environment Context.
		 * @param serviceName
		 *            Name of Service to invoke.
		 * @param inDoc
		 *            Input Document to be passed to the Service.
		 * @throws java.lang.Exception
		 *             Exception thrown by the Service.
		 * @return Output of the Service.
		 */
		public static Document invokeService(YFSEnvironment env,
				String serviceName, Document inDoc) throws RemoteException {
			return CSCommonUtil.api.executeFlow(env, serviceName, inDoc);
		}

		/**
		 * Invokes a Sterling Commerce Service.
		 * 
		 * @param env
		 *            Sterling Commerce Environment Context.
		 * @param serviceName
		 *            Name of Service to invoke.
		 * @param inDocStr
		 *            Input to be passed to the Service. Should be a valid XML String.
		 * @throws java.lang.Exception
		 *             Exception thrown by the Service.
		 * @return Output of the Service.
		 */
		public static Document invokeService(YFSEnvironment env,
				String serviceName, String inDocStr) throws Exception {
			return CSCommonUtil.api.executeFlow(env, serviceName, YFCDocument.parse(
					inDocStr).getDocument());
		}

		/**
		 * Invokes a getCommonCodeList API.
		 * 
		 * @param env
		 *            Sterling Commerce Environment Context.
		 * @param Document
		 *            Input XML to API.
		 * @param sTemplate
		 *            Output template.
		 * @throws java.lang.Exception
		 *             Exception thrown by the Service.
		 * @return Document from the API call.
		 */
		public static Document getCommonCodeList(YFSEnvironment env, Document inputXML, String sTemplate)throws Exception{
			LOGGER.beginTimer("start of getCommonCodeList");
			if(!YFCObject.isVoid(sTemplate)) {
				env.setApiTemplate("getCommonCodeList", sTemplate);
			}
			LOGGER.verbose("Input to getCommonCodeList API is : "+SCXmlUtil.getString(inputXML));
			Document docCommonCodeOut = invokeAPI(env, "getCommonCodeList", inputXML);
			env.clearApiTemplate("getCommonCodeList");
			LOGGER.verbose("Output of getCommonCodeList is : "+SCXmlUtil.getString(docCommonCodeOut));
			LOGGER.endTimer("end of getCommonCodeList");
			return docCommonCodeOut;
		}
		
		/**
		 * Invokes a getOrderList API.
		 * 
		 * @param env
		 *            Sterling Commerce Environment Context.
		 * @param Document
		 *            Input XML to API.
		 * @param sTemplate
		 *            Output template.
		 * @throws java.lang.Exception
		 *             Exception thrown by the Service.
		 * @return Document from the API call.
		 */
		public static Document getOrderList(YFSEnvironment env, Document inputXML, String sTemplate)throws Exception{
			LOGGER.beginTimer("start of getOrderList");
			if(!YFCObject.isVoid(sTemplate)) {
				env.setApiTemplate("getOrderList", sTemplate);
			}
			LOGGER.verbose("Input to getOrderList API is : "+SCXmlUtil.getString(inputXML));
			Document docGetOrderListOut = invokeAPI(env, "getOrderList", inputXML);
			env.clearApiTemplate("getOrderList");
			LOGGER.verbose("Output of getOrderList is : "+SCXmlUtil.getString(docGetOrderListOut));
			LOGGER.endTimer("end of getOrderList");
			return docGetOrderListOut;
		}
		
		/**
		 * Method sorts any NodeList by provided attribute.
		 * 
		 * @param nl
		 *            NodeList to sort
		 * @param attributeName
		 *            attribute name to use
		 * @param asc
		 *            true - ascending, false - descending
		 * @param B
		 *            class must implement Comparable and have Constructor(String) -
		 *            e.g. Integer.class , BigDecimal.class etc
		 * @return
		 */
		  public static Node[] sortNodes(NodeList nl, final String attributeName, final boolean asc, final Class<? extends Comparable> B)
		    {        
			  LOGGER.debug("Sorting Nodes Based on Expected Shipment Date Attribute");
		        class NodeComparator<T> implements Comparator<T>
		        {
		            @Override
		            public int compare(T a, T b)
		            {
		                int ret;
		                Comparable bda = null, bdb = null;
		                try{
		                    Constructor bc = B.getDeclaredConstructor(String.class);
		                    bda = (Comparable)bc.newInstance(((Element)a).getAttribute(attributeName));
		                    bdb = (Comparable)bc.newInstance(((Element)b).getAttribute(attributeName));
		                    //LOGGER.debug("bda::"+bda+"::bdb::"+bdb);
		                }
		                catch(Exception e)
		                {
		                  //LOGGER.debug("ExceptionOccured:"+e);
		                	LOGGER.error(e);
		                }
		                ret = bda.compareTo(bdb);
		                return asc ? ret : -ret; 
		            }
		        }

		        List<Node> x = new ArrayList<Node>();
		        for(int i = 0; i < nl.getLength(); i++)
		        {
		            x.add(nl.item(i));
		        }
		        Node[] ret = new Node[x.size()];
		        ret = x.toArray(ret);
		        Arrays.sort(ret, new NodeComparator<Node>());
		        LOGGER.debug("Ended Sorting Nodes Based on Expected Shipment Date Attribute");
		        return ret;
		    }    
	/**
			 * Method sorts any ArrayList of Elements by provided attribute.
			 * 
			 * @param nl
			 *            ArrayList to sort
			 * @param attributeName
			 *            attribute name to use
			 * @param asc
			 *            true - ascending, false - descending
			 * @param B
			 *            class must implement Comparable and have Constructor(String) -
			 *            e.g. Integer.class , BigDecimal.class etc
			 * @return
			 */
		  public static Node[] sortNodes(ArrayList nl, final String attributeName, final boolean asc,
					final Class<? extends Comparable> B) {
			  LOGGER.debug("Sorting Nodes Based on Expected Shipment Date Attribute");
				class NodeComparator<T> implements Comparator<T> {
					@Override
					public int compare(T a, T b) {
						int ret;
						Comparable bda = null, bdb = null;
						try {
							Constructor bc = B.getDeclaredConstructor(String.class);
							bda = (Comparable) bc.newInstance(((Element) a).getAttribute(attributeName));
							bdb = (Comparable) bc.newInstance(((Element) b).getAttribute(attributeName));
							//LOGGER.debug("dba:" + bda + "bdb" + bdb);
						} catch (Exception e) {
							LOGGER.error(e);
						}
						ret = bda.compareTo(bdb);
						return asc ? ret : -ret;
					}
				}

				List<Element> x = new ArrayList<Element>();
				for (int i = 0; i < nl.size(); i++) {
					Object o = nl.get(i);
					if (o instanceof Element)
						x.add((Element) o);
				}
				Element[] ret = new Element[x.size()];
				ret = x.toArray(ret);
				Arrays.sort(ret, new NodeComparator<Element>());
				LOGGER.debug("Ended Sorting Nodes Based on Expected Shipment Date Attribute");
				return ret;
				}

		  public static String toString(SOAPMessage message)  
		    {
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        try {
					message.writeTo(out);
				} catch (SOAPException e) {
					LOGGER.error(e);
				} catch (IOException e) {
					LOGGER.error(e);
				}
		        return out.toString();
		    }
		  
		  public static String getTotalNoOfItemsForOrder(Element eleOrder){
				
				LOGGER.debug("inside getTotalNoOfItemsForOrder"+SCXmlUtil.getString(eleOrder));
				
				double dTotNoOfItems = 0.00;
				
				double dLineQty = 0.00;
				
				NodeList nlOrderLine = SCXmlUtil.getXpathNodes(eleOrder, "OrderLines/OrderLine");
				
				LOGGER.debug("nlOrderLine length"+nlOrderLine.getLength());
				
				for (int olC = 0; olC < nlOrderLine.getLength(); olC++) {

					Element eleOrderLine = (Element) nlOrderLine.item(olC);
					
					String strLineQty = eleOrderLine.getAttribute("OrderedQty");
					
					dLineQty = Double.parseDouble(strLineQty);
					
					dTotNoOfItems = dTotNoOfItems + dLineQty;
					
				}
				
				String strTotNoOfItems = String.valueOf(dTotNoOfItems);
				
				return strTotNoOfItems;
			}
		  
		  public static double roundOffToTwoDecimals(double val)throws Exception{
				BigDecimal a = new BigDecimal(val);
				BigDecimal roundOff = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				LOGGER.verbose(val+" : rounded to : "+roundOff.toString());
				return roundOff.doubleValue();
			}


}