
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/common/paymentCapture/PaymentCapture_StoredValueExtnUI", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare, _extnPaymentCapture_StoredValueExtnUI,_isccsBaseTemplateUtils, _isccsOrderUtils, _isccsUIUtils, _isccsWidgetUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils
){ 
	return _dojodeclare("extn.common.paymentCapture.PaymentCapture_StoredValueExtn", [_extnPaymentCapture_StoredValueExtnUI],{
	// custom code here
	
	// Override OOB method to handle SSDCS
	initializeScreen: function(
        event, bEvent, ctrl, args) {
			widget = this.getWidgetByUId("txtPaymentReference1");
					   if(widget){
						   widget.set("label","PIN");
					   }
            var screenInput = null;
            screenInput = _scScreenUtils.getInitialInputData(
            this);
            _scScreenUtils.setModel(
            this, "PaymentCapture_output", screenInput, null);
            if (
            _scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethod", screenInput, false)) {
                _scWidgetUtils.disableWidget(
                this, "txtDisplaySvcNo", false);
                _scWidgetUtils.showWidget(
                this, "txtDisplaySvcNo", false, null);
            }
            var sReturnDocumentType = "";
            sReturnDocumentType = _isccsOrderUtils.getReturnOrderDocumentType();
            if (
            _scBaseUtils.equals(
            sReturnDocumentType, _scModelUtils.getStringValueFromPath("Order.DocumentType", screenInput, false))) {
                _scWidgetUtils.hideWidget(
                this, "txtMaxChargeLimit", false);
            }
			
            var paymentCaptureScreen = null;
            paymentCaptureScreen = _isccsUIUtils.getParentScreen(
            this, true);
            var PaymentCaptureModel = null;
            PaymentCaptureModel = _scBaseUtils.getTargetModel(
            paymentCaptureScreen, "PaymentCapture_input", null);
            var paymentType = null;
            paymentType = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentType", PaymentCaptureModel);
            if (
            _scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethod", screenInput, false)) {
                _scWidgetUtils.disableWidget(
                this, "txtDisplaySvcNo", false);
                _scWidgetUtils.hideWidget( this, "extn_txtCardNo", false);
                _scWidgetUtils.showWidget(
                this, "txtDisplaySvcNo", false, null);
            } else {
				_scWidgetUtils.setWidgetMandatory(	this, "extn_txtCardNo");
            }
        },
		
	invokeTokenization: function(
        event, bEvent, ctrl, args) {

			var cardNo = _scWidgetUtils.getValue( this, "extn_txtCardNo");
			if( !_scBaseUtils.isVoid(cardNo)) {

				var paymentType = _scWidgetUtils.getValue( this.ownerScreen, "cmbPaymentType");
				var paymentMethod = new Object();
				_scModelUtils.setStringValueAtModelPath("PaymentMethod.CardNumber", cardNo, paymentMethod);
				_scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentType", paymentType, paymentMethod);
				_isccsUIUtils.callApi( this, paymentMethod, "extn_PaymentCardNoTokenization", null);
			}
		},
		
		checkGCBalance: function(
        event, bEvent, ctrl, args) {
			var parentScreen = this.ownerScreen.ownerScreen;
			var orderModel = null;
			orderModel = _scScreenUtils.getModel(
                parentScreen, "paymentConfirmation_getCompleteOrderDetails_Output");
			var giftCardNo = _scWidgetUtils.getValue( this, "extn_txtCardNo");
			var pin = _scWidgetUtils.getValue( this, "txtPaymentReference1");
			if( !_scBaseUtils.isVoid(giftCardNo) && !_scBaseUtils.isVoid(pin)) {
				var gcBalanceModel = null;
				gcBalanceModel = _scModelUtils.createNewModelObjectWithRootKey("GiftCard");
				var cardModel = _scBaseUtils.getNewModelInstance();
				_scModelUtils.addStringValueToModelObject("CardNumber", giftCardNo, cardModel);
				_scModelUtils.addStringValueToModelObject("PIN", pin, cardModel);
				_scModelUtils.addModelToModelPath("GiftCard.Card", cardModel, gcBalanceModel);
				_isccsUIUtils.callApi(this, gcBalanceModel, "extn_GCBalanceCheck", null);
			}
		},
		handleMashupOutput : function (
			mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
			if (
            _scBaseUtils.equals(
            mashupRefId, "extn_GCBalanceCheck")) {
                if (!_scBaseUtils.isVoid(modelOutput)){
					
					var isGCApproved = null;
					isGCApproved = _scModelUtils.getStringValueFromPath("GiftCard.Card.IsGCApproved", modelOutput);
					if(!_scBaseUtils.isVoid(isGCApproved) && _scBaseUtils.equals(isGCApproved,'N')){
						_isccsBaseTemplateUtils.showMessage(this, "extn_Error_Invalid_GiftCard", "error", null);
						_scWidgetUtils.setValue(this, "txtPaymentReference1", "", false);
					}
				}
            }
		}
});
});

