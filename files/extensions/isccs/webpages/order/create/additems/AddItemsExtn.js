scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/create/additems/AddItemsExtnUI","scbase/loader!sc/plat/dojo/utils/BaseUtils",
"scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!isccs/utils/OrderUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnAddItemsExtnUI, _scBaseUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _isccsOrderUtils
){ 
	return _dojodeclare("extn.order.create.additems.AddItemsExtn", [_extnAddItemsExtnUI],{
	// custom code here
	addItem: function(
        event, bEvent, ctrl, args) {
			var targetModel = null;
            var mashupRefObj = null;
            var mashupContext = null;
            var ordermodel = null;
            var enterpriseCode = null;
            var itemID = null;
			var invOrgModel = null;
            this.itemIdFieldErrorMsg = null;
            _scWidgetUtils.clearFieldinError(
            this, "txt_ItemID");
            if (!(
            _scWidgetUtils.isWidgetVisible(
            this, "bottomPanel"))) {
                _scWidgetUtils.showWidget(
                this, "bottomPanel", true, null);
            }
            targetModel = _scBaseUtils.getTargetModel(
            this, "getCompleteItemList_input", null);
			invOrgModel = _scScreenUtils.getModel(this, "extn_getInventoryOrganization_output");
			var orgList = _scModelUtils.getModelListFromPath("OrganizationList.Organization", invOrgModel);
			var invOrg = null;
			invOrg = _scModelUtils.getStringValueFromPath("InventoryOrganizationCode", orgList[0]);
			var distRuleId = null;
			distRuleId = _scScreenUtils.getString(this, "extn_Matrix-R_DistributionRuleID");
			_scModelUtils.setStringValueAtModelPath("Item.DistributionRuleId", distRuleId, targetModel);
			_scModelUtils.setStringValueAtModelPath("Item.CallingOrganizationCode", invOrg, targetModel);
			
            itemID = _scModelUtils.getStringValueFromPath("Item.BarCode.BarCodeData", targetModel);
            if (
            _scBaseUtils.isVoid(
            itemID)) {
                var sMessage = null;
                sMessage = _scScreenUtils.getString(
                this, "CSR_AddItems_productIdRequired");
                _scWidgetUtils.markFieldinError(
                this, "txt_ItemID", sMessage, true);
            } else {
                var currentItemId = null;
                var isItemIdFieldInError = true;
                currentItemId = _scBaseUtils.getNewModelInstance();
                _scModelUtils.setStringValueAtModelPath("Item.ItemID", itemID, currentItemId);
                _scScreenUtils.setModel(
                this, "currentItemId", currentItemId, null);
                isItemIdFieldInError = _scScreenUtils.isValid(
                this, "getCompleteItemList_input");
                if (
                _scBaseUtils.equals(
                isItemIdFieldInError, true)) {
                    _scModelUtils.setStringValueAtModelPath("Item.ItemIDQryType", "FLIKE", targetModel);
                    if (
                    _isccsOrderUtils.checkNoOfNewLines(
                    this)) {
                        this.callGetCompleteItemListApi(
                        targetModel, null);
                    }
                }
            }
        }
});
});

