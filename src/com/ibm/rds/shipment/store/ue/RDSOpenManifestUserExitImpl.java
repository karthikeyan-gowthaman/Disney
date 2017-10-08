package com.ibm.rds.shipment.store.ue;

import com.yantra.shared.ycp.YFSContext;
import com.yantra.ycs.japi.ue.YCSopenManifestUserExit;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * This class is invoked by YCSopenManifestUserExit. This class has below logic
 * 1. Returns openManifestContinue Boolean as false 
 * 
 */

public class RDSOpenManifestUserExitImpl implements YCSopenManifestUserExit {
    public String openManifest(YFSContext arg0, String arg1) throws YFSUserExitException {
        return arg1;
    }

    public boolean openManifestContinue(YFSContext arg0, String arg1) throws YFSUserExitException {
        return false;
    }
}

