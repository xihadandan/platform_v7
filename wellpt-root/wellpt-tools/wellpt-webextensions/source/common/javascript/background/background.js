var wellOfficeExt = wellOfficeExt || {}; // eslint-disable-line
// no-use-before-define

wellOfficeExt.Background = wellOfficeExt.Background || {};


// Initializes a generated tab
wellOfficeExt.Background.initializeGeneratedTab = function(url, data, locale) {
	var extensionTab = null;
	var tabs = browser.extension.getViews({
		type : "tab"
	});

	// Loop through the tabs
	for (var i = 0, l = tabs.length; i < l; i++) {
		extensionTab = tabs[i];

		// If the tab has a matching URL and has not been initialized
		if (extensionTab.location.href == url && !extensionTab.wellOfficeExt.Generated.initialized) {
			extensionTab.wellOfficeExt.Generated.initialized = true;

			extensionTab.wellOfficeExt.Generated.initialize(data, locale);
		}
	}
};

// Opens a generated tab
wellOfficeExt.Background.openGeneratedTab = function(tabURL, tabIndex, data, locale) {
	// Need to clone the data and locale to workaround Firefox dead object
	// memory clean up
	var generatedData = JSON.parse(JSON.stringify(data));
	var generatedLocale = JSON.parse(JSON.stringify(locale));

	browser.tabs.create({
		index : tabIndex + 1,
		url : tabURL
	}).then(function(openedTab) {
		var tabLoaded = function(tabId, tabInformation) {
			// If this is the opened tab and it finished loading
			if (tabId == openedTab.id && tabInformation.status && tabInformation.status == "complete") {
				wellOfficeExt.Background.initializeGeneratedTab(tabURL, generatedData, generatedLocale);

				browser.tabs.onUpdated.removeListener(tabLoaded);
			}
		};
		browser.tabs.onUpdated.addListener(tabLoaded);
	});
};

// 处理消息
browser.runtime.onMessage.addListener(function(message, sender, sendResponse) {
	return new Promise(function (resolve, reject) {
		wellOfficeExt.Common.doFilter(message, sender, {
			sendResponse : function(result, reqest){
				resolve(result);
			},
			position : wellOfficeExt.Common.messageDir.background
		});
	});
})

// 配合page.js，实现下载逻辑
wellOfficeExt.Common.addFilter("saveAs", function(reqest, response) {
	/*
	 * chrome不支持取消回调，直接返回，firefox支持promise回调
	 * https://developer.chrome.com/extensions/downloads#method-download
	 * https://developer.mozilla.org/en-US/docs/Mozilla/Add-ons/WebExtensions/API/downloads/download
	 */
	var options = reqest.getParam();
	options.saveAs = true;
	var downloading = browser.downloads.download(options);
	downloading.then(function(id){
		reqest.success(id);
	}).catch(function(error){
		reqest.error(error.message || "download error");
	});
});
