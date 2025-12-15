//Any top-level functions or variables here will be exposed in target scope specified in mozIJSSubScriptLoader.loadSubScript

//The constructor is to mimic the interface of URLLauncher XPCOM.
function URLLauncher() {
}

URLLauncher.prototype = {
    open: function (url) {
        return launchURL(url);
    },

    isMicrosoftOfficeInstalled: function (ext) {
        return checkMicrosoftOffice(ext);
    }
}
