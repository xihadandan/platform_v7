//frame script

addEventListener("DOMContentLoaded", function (event) {
    function sendMessage(method, data) {
        var results = sendSyncMessage(method, data);
        if (results.length > 0) {
            return results[0];
        }
        return "";
    }

    function launchURL(url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = content.location.protocol + "//" + content.location.host + url;
        }

        return sendMessage("launchURL", {url: url});
    }

    function checkMicrosoftOffice(ext) {
        return sendMessage("checkMicrosoftOffice", {ext: ext});
    }

    //export the functions for the use in web page script, i.e. editInOffice.js
    Components.utils.exportFunction(launchURL, content.window, {defineAs: "launchURL"})
    Components.utils.exportFunction(checkMicrosoftOffice, content.window, {defineAs: "checkMicrosoftOffice"})

    //load a script to create URLLauncher function constructor for backward compatibility
    var scriptLoader = Components.classes["@mozilla.org/moz/jssubscript-loader;1"]
        .getService(Components.interfaces.mozIJSSubScriptLoader);
    scriptLoader.loadSubScript("chrome://urlload/content/page-script.js", content.window);
});

