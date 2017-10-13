package com.ibm.rds.payment.ue;

import java.util.ArrayList;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnHeaderTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxBreakup;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateHeaderTaxUE;

public class RDSRecalculateHeaderTaxUE implements YFSRecalculateHeaderTaxUE {

	private static YFCLogCategory cat = YFCLogCategory.instance(RDSRecalculateHeaderTaxUE.class);


	public YFSExtnTaxCalculationOutStruct recalculateHeaderTax(
			YFSEnvironment env,
			YFSExtnHeaderTaxCalculationInputStruct extnInStruct)
			throws YFSUserExitException {
		
		cat.beginTimer("RDSRecalculateHeaderTaxUE.recalculateHeaderTax");
		
		YFSExtnTaxCalculationOutStruct extnOutStruct = new YFSExtnTaxCalculationOutStruct();
		extnOutStruct.colTax = new ArrayList();
		if (extnInStruct.colTax != null && (!extnInStruct.bForInvoice || extnInStruct.bLastInvoice)) {

			for (int i = 0; i < extnInStruct.colTax.size(); ++i) {
				YFSExtnTaxBreakup oTax = (YFSExtnTaxBreakup) extnInStruct.colTax
						.get(i);
				extnOutStruct.colTax.add(oTax);
			}
		}

		cat.endTimer("RDSRecalculateHeaderTaxUE.recalculateHeaderTax");
		return extnOutStruct;
	}
}
