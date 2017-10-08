package com.ibm.rds.payment.ue;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnLineTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateLineTaxUE;
/*
 * This UE is implemented to return tax as received in input
 */

public class RDSRecalculateLineTaxUEImpl implements YFSRecalculateLineTaxUE{
	public static YFCLogCategory log = YFCLogCategory.instance(RDSRecalculateLineTaxUEImpl.class);
	
	public YFSExtnTaxCalculationOutStruct recalculateLineTax(YFSEnvironment env,
			YFSExtnLineTaxCalculationInputStruct yfsExtnLineTaxCalculationInputStruct)
			throws YFSUserExitException {
		log.debug("******************************Inside RDSRecalculateLineTaxUEImpl *******************************************");
		
		YFSExtnTaxCalculationOutStruct yfsExtnTaxCalculationOutStruct = new YFSExtnTaxCalculationOutStruct();
			
				yfsExtnTaxCalculationOutStruct.colTax = yfsExtnLineTaxCalculationInputStruct.colTax;
				yfsExtnTaxCalculationOutStruct.tax = yfsExtnLineTaxCalculationInputStruct.tax;
			    yfsExtnTaxCalculationOutStruct.taxPercentage = yfsExtnLineTaxCalculationInputStruct.taxPercentage;
				return yfsExtnTaxCalculationOutStruct;
			
	}
	
}

