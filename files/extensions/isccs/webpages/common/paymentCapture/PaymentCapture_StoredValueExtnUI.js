
scDefine(["dojo/text!./templates/PaymentCapture_StoredValueExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/TextBox","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
 , function(			 
			    templateText
			 ,
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojolang
			 ,
			    _dojotext
			 ,
			    _idxTextBox
			 ,
			    _scplat
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.common.paymentCapture.PaymentCapture_StoredValueExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
		],
		sourceBindingNamespaces :
		[
			{
	  value: 'extn_GCBalanceOutput'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_1'
						
			}
			
		]
	}

	
	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {

local : [

{
	  eventId: 'extn_txtCardNo_onBlur'

,	  sequence: '51'




,handler : {
methodName : "invokeTokenization"

 
 
}
}
,
{
	  eventId: 'txtPaymentReference1_onBlur'

,	  sequence: '51'




,handler : {
methodName : "checkGCBalance"

 
 
}
}

]
}

});
});


