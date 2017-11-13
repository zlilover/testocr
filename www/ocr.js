var exec = require("cordova/exec");
module.exports = {
	startOCR: function(content,successCallback, errorCallback){
		exec(
		successCallback,
		errorCallback,
		"IDCardOCRPlugin",//feature name
		"startScanIDcard",//action
		[content]//要传递的参数，json格式
		);
	}
}
