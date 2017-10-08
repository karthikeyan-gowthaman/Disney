package com.ibm.rds.shipment.store.ue;

import com.yantra.shared.ycp.YFSContext;
import com.yantra.ycs.japi.ue.YCScloseManifestUserExit;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * This class is invoked by YCScloseManifestUserExit. This class has below logic
 * 1. Returns closeManifestContinue Boolean as false
 */

public class RDSCloseManifestUserExitImpl implements YCScloseManifestUserExit
{
   public String closeManifest( YFSContext context, String input ) throws YFSUserExitException
   {
      return input;
   }

   public boolean closeManifestContinue( YFSContext context, String input ) throws YFSUserExitException
   {
      return false;
   }
}
