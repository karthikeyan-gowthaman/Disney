
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/common/paymentCapture/PaymentCapture_CreditCardExtnUI", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/CustomerUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare, _extnPaymentCapture_CreditCardExtnUI, _isccsBaseTemplateUtils, _isccsCustomerUtils, _isccsOrderUtils, _isccsUIUtils, _scBaseUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils
){ 
	return _dojodeclare("extn.common.paymentCapture.PaymentCapture_CreditCardExtn", [_extnPaymentCapture_CreditCardExtnUI],{
	// custom code here
	
	// Override OOB method to handle SSDCS	
	initializeScreen: function(
        event, bEvent, ctrl, args) {
            
			var screenInput = null;
            var RulesModel = null;
            var sUseCCName = null;
            screenInput = _scScreenUtils.getInitialInputData(
            this);
            _scScreenUtils.setModel(
            this, "paymentCapture_Output", screenInput, null);
            var paymentCaptureScreen = null;
            paymentCaptureScreen = _isccsUIUtils.getParentScreen(
            this, true);
            var PaymentCaptureModel = null;
            PaymentCaptureModel = _scBaseUtils.getTargetModel(
            paymentCaptureScreen, "PaymentCapture_input", null);
            var paymentType = null;
            paymentType = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentType", PaymentCaptureModel);
            /* var label = null;
            label = _scScreenUtils.getLabelString(
            this, "CardNumber");
            _isccsUIUtils.createPadssWidget(
            this, "padssInput", label, paymentType, "padssHolder"); */
            if (
            _scBaseUtils.equals(
            this.screenMode, "Customer")) {
                var rulesInput = null;
                var callingOrgCode = null;
                callingOrgCode = _scModelUtils.getStringValueFromPath("Customer.OrganizationCode", screenInput);
                if (
                _scBaseUtils.isVoid(
                callingOrgCode)) {
                    callingOrgCode = _scModelUtils.getStringValueFromPath("CustomerPaymentMethod.OrganizationCode", screenInput);
                }
                rulesInput = _scModelUtils.createNewModelObjectWithRootKey("Rules");
                _scModelUtils.setStringValueAtModelPath("Rules.CallingOrganizationCode", callingOrgCode, rulesInput);
                _isccsUIUtils.callApi(
                this, rulesInput, "paymentCapture_getRuleDetails_CCName", null);
            } else {
                RulesModel = _scScreenUtils.getModel(
                this, "getRuleDetails_CCName_Output");
                sUseCCName = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", RulesModel);
                this.enableDisableCreditCardNameWidgets(
                sUseCCName);
            }
            if (
            _scBaseUtils.isVoid(
            RulesModel)) {
                RulesModel = _scScreenUtils.getModel(
                this, "getRuleDetails_CCName_Output");
            }
            sUseCCName = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", RulesModel);
            if (
            _scModelUtils.isKeyPresentInModel("Order", screenInput)) {
                this.initializeOrderScreen(
                screenInput, sUseCCName);
            } else {
                this.initializeCustomerScreen(
                screenInput, sUseCCName);
            }
        },
		
	initializeCustomerScreen: function(
        screenInput, sUseCCName) {
            if (!(
            _scBaseUtils.isVoid(
            _scModelUtils.getStringValueFromPath("CustomerPaymentMethod.CustomerPaymentMethodKey", screenInput)))) {
                _scWidgetUtils.enableReadOnlyWidget(
                this, "cmbCardType");
				_scWidgetUtils.disableWidget( this, "extn_txtCardNo", false);
                _scWidgetUtils.hideWidget( this, "extn_txtCardNo", false);
                _scWidgetUtils.showWidget(
                this, "txtDisplayCreditCardNo", false, null);
                _scWidgetUtils.enableReadOnlyWidget(
                this, "txtDisplayCreditCardNo");
                var EncryptRulesModel = null;
                EncryptRulesModel = _scScreenUtils.getModel(
                this, "getRuleDetails_EncryptAdd_Output");
                var sEncrypt = null;
                sEncrypt = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", EncryptRulesModel);
                if (
                _scBaseUtils.equals(
                sEncrypt, "Y")) {
                    this.disableEnableFields();
                }
            } else {
                var billingAddressModel = null;
                var paymentCapture = null;
                paymentCapture = _scScreenUtils.getModel(
                this, "paymentCapture_Output");
                if (
                _scModelUtils.hasModelObjectForPathInModel("CustomerPaymentMethod.PersonInfoBillTo", paymentCapture)) {
                    billingAddressModel = _scModelUtils.getModelObjectFromPath("CustomerPaymentMethod.PersonInfoBillTo", paymentCapture);
                } else {
                    billingAddressModel = {};
                }
                var customerFormattedName = "";
                customerFormattedName = _isccsCustomerUtils.getFormattedNameDisplay(
                this, billingAddressModel);
                _scWidgetUtils.setValue(
                this, "txtNameOnCard", customerFormattedName, false);
                _scWidgetUtils.setValue(
                this, "txtFirstName", _scModelUtils.getStringValueFromPath("FirstName", billingAddressModel), false);
                _scWidgetUtils.setValue(
                this, "txtMiddleName", _scModelUtils.getStringValueFromPath("MiddleName", billingAddressModel), false);
                _scWidgetUtils.setValue(
                this, "txtLastName", paymentType = _scModelUtils.getStringValueFromPath("LastName", billingAddressModel), false);
            }
            this.defaultCustomerBillingAddress();
        },
		
	initializeOrderScreen: function(
        screenInput, sUseCCName) {
            var paymentType = null;
            if (
            _scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethod", screenInput, false)) {
                _scWidgetUtils.enableReadOnlyWidget(
                this, "cmbCardType");
				_scWidgetUtils.disableWidget( this, "extn_txtCardNo", false);
				_scWidgetUtils.hideWidget( this, "extn_txtCardNo", false);
                _scWidgetUtils.showWidget(
                this, "txtDisplayCreditCardNo", false, null);
                _scWidgetUtils.enableReadOnlyWidget(
                this, "txtDisplayCreditCardNo");
                var EncryptRulesModel = null;
                EncryptRulesModel = _scScreenUtils.getModel(
                this, "getRuleDetails_EncryptAdd_Output");
                var sEncrypt = null;
                sEncrypt = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", EncryptRulesModel);
                if (
                _scBaseUtils.equals(
                sEncrypt, "Y")) {
                    this.disableEnableFields();
                }
            }
            var billingAddressModel = null;
            if (
            _scModelUtils.hasModelObjectForPathInModel("Order.PaymentMethod.PersonInfoBillTo", screenInput)) {
                billingAddressModel = _scModelUtils.getModelObjectFromPath("Order.PaymentMethod.PersonInfoBillTo", screenInput);
            } else {
                billingAddressModel = _scModelUtils.getModelObjectFromPath("Order.PersonInfoBillTo", screenInput);
            }
            _scModelUtils.setStringValueAtModelPath("EnterpriseCode", _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", screenInput), billingAddressModel);
            var newBillingAddressModel = null;
            newBillingAddressModel = _scModelUtils.createNewModelObjectWithRootKey("PersonInfoBillTo");
            _scModelUtils.addModelToModelPath("PersonInfoBillTo", billingAddressModel, newBillingAddressModel);
            _scModelUtils.setBooleanValueAtModelPath("PersonInfoBillTo.EditAddress", _scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethodBillTo", screenInput, true), newBillingAddressModel);
            _scScreenUtils.setModel(
            this, "billingAddress_Output", newBillingAddressModel, null);
            var RealTimeRulesModel = null;
            RealTimeRulesModel = _scScreenUtils.getModel(
            this, "getRuleDetails_RealTimeAuthorization_Output");
            var sRealTimeAuth = null;
            sRealTimeAuth = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", RealTimeRulesModel);
            var draftOrderFlag = null;
            draftOrderFlag = _scModelUtils.getStringValueFromPath("Order.DraftOrderFlag", screenInput);
            if (
            _scBaseUtils.equals(
            sRealTimeAuth, "02")) {
                _scWidgetUtils.showWidget(
                this, "txtCVV", false, null);
            }
            if (!(
            _scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethod", screenInput, false))) {
                if (
                _scBaseUtils.equals(
                sUseCCName, "Y")) {
                    var customerFormattedName = "";
                    customerFormattedName = _isccsCustomerUtils.getFormattedNameDisplay(
                    this, billingAddressModel);
                    _scWidgetUtils.setValue(
                    this, "txtNameOnCard", customerFormattedName, false);
                } else {
                    _scWidgetUtils.setValue(
                    this, "txtFirstName", _scModelUtils.getStringValueFromPath("FirstName", billingAddressModel), false);
                    _scWidgetUtils.setValue(
                    this, "txtMiddleName", _scModelUtils.getStringValueFromPath("MiddleName", billingAddressModel), false);
                    _scWidgetUtils.setValue(
                    this, "txtLastName", paymentType = _scModelUtils.getStringValueFromPath("LastName", billingAddressModel), false);
                }
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
		
	handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "paymentCapture_getRuleDetails_CCName")) {
                _scScreenUtils.setModel(
                this, "getRuleDetails_CCName_Output", modelOutput, null);
                var RulesModel = null;
                RulesModel = _scScreenUtils.getModel(
                this, "getRuleDetails_CCName_Output");
                var sUseCCName = null;
                sUseCCName = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", RulesModel);
                this.enableDisableCreditCardNameWidgets(
                sUseCCName);
            }
			
			else if( _scBaseUtils.equals( mashupRefId, "extn_PaymentCardNoTokenization") ) {
				
				var cardNo = _scWidgetUtils.getValue( this, "extn_txtCardNo");
				var displayCardno = cardNo.substr(cardNo.length - 4);
				var tokenisedCardNo = _scModelUtils.getStringValueFromPath("TKN.TKI", modelOutput);
				_scWidgetUtils.setValue( this, "extn_txtCardNo", tokenisedCardNo, true);
				_scWidgetUtils.setValue( this, "txtDisplayCreditCardNo", displayCardno, true);
			}
				
        },
});
});

