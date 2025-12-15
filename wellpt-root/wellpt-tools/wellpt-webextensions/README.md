# 浏览器办公助手



## 支持功能 ##

1、浏览器另存为接口 
支持浏览器：firefox、opera、chrome、qqbrowser、360se、edge(Chromium)、其他基于Chromium内核浏览器
https://developer.mozilla.org/en-US/docs/Mozilla/Add-ons/WebExtensions/Browser_support_for_JavaScript_APIs
2、

## 检测方法 ##
```
var wellOfficeAssistant = window.wellOfficeAssistant;
if(wellOfficeAssistant && wellOfficeAssistant.saveAs){
	// 有另存为功能支持
}
```

## Firefox中调试 ##

npm install web-ext -g
cd ../wellpt-webextensions
gulp temporary-install-firefox

## 参考文档 ##

For information about how to write browser extensions check out the Chrome, Firefox and Opera documentation:

* Chrome: [http://code.google.com/chrome/extensions/docs.html](http://code.google.com/chrome/extensions/docs.html)
* Firefox: [https://developer.mozilla.org/en-US/addons](https://developer.mozilla.org/en-US/addons)
* Opera: [https://dev.opera.com/extensions/](https://dev.opera.com/extensions/)

## 开发者中心 ##

* Chrome: [https://chrome.google.com/webstore/devconsole?hl=zh_CN&authuser=0](https://chrome.google.com/webstore/devconsole?hl=zh_CN&authuser=0)
* Firefox: [https://addons.mozilla.org/zh-CN/developers/](https://addons.mozilla.org/zh-CN/developers/)
* Opera: [https://dev.opera.com/extensions/](https://dev.opera.com/extensions/)

## 构建发布 ##

The extension uses the [Gulp build system](http://gulpjs.com/).
	cd ./wellpt-webextensions
	npm install

Once Gulp is installed the extension is built by running the following:

	gulp

To build the extension for Chrome, Firefox or Opera only run `gulp chrome`, `gulp firefox` or `gulp opera` respectively.

When the extension is built it creates `build/chrome`, `build/firefox` and `build/opera` directories as well as packaged versions of the extensions `build/wellpt-webextensions-chrome.zip`, `build/wellpt-webextensions-firefox.xpi`, `build/wellpt-webextensions-opera.nex`.

The extension can be installed in Chrome or Opera by loading the unpacked extension in `build/chrome` or `build/opera` respectively as described in the [Chrome](http://code.google.com/chrome/extensions/getstarted.html#load-ext) and [Opera](https://dev.opera.com/extensions/testing/) documentation.
The extension can be installed in Firefox by installing `build/wellpt-webextensions-firefox.xpi` like a regular extension.

## 第三方依赖 ##

The following libraries are used by and included in the extension as-is:

* Bootstrap from Twitter: [http://twitter.github.com/bootstrap/](http://twitter.github.com/bootstrap/)
* Font Awesome: [http://fontawesome.io/](http://fontawesome.io/)
* jQuery: [http://jquery.com/](http://jquery.com/)
* webextension-polyfill: [https://github.com/mozilla/webextension-polyfill/](https://github.com/mozilla/webextension-polyfill/)

They should not be altered apart from to update to their latest versions for maintenance reasons.
The latest versions of Bootstrap from Twitter or jQuery can be automatically merged into the repository using `gulp merge-bootstrap` or `gulp merge-jquery` respectively.


## 待办列表 ##

1、完善回调功能(20200227)
2、上传google store，5美金
