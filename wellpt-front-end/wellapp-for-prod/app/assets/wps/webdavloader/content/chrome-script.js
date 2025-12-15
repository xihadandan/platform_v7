// chrome script

var globalMM = Cc["@mozilla.org/globalmessagemanager;1"].getService(Ci.nsIMessageListenerManager);
globalMM.loadFrameScript("chrome://urlload/content/frame-script.js", true);

var clazz = Components.classes["@benryan.com/url/launch;1"];
var urlOpener = clazz.createInstance(Components.interfaces.nsIUrlOpener);

globalMM.addMessageListener("launchURL", function (msg) {
    try {
        urlOpener.open(msg.json.url);
    } catch (ex) {
        console.error(ex.message)
        return ex.message;
    }

    return "";
});

globalMM.addMessageListener("checkMicrosoftOffice", function (msg) {
    try {
        return urlOpener.isMicrosoftOfficeInstalled(msg.json.ext);
    } catch (ex) {
        console.error(ex.message)
    }

    return false;
});
