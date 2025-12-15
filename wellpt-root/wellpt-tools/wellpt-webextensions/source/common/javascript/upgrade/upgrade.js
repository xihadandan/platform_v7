var wellOfficeExt = wellOfficeExt || {}; // eslint-disable-line
                                        // no-use-before-define

wellOfficeExt.Upgrade = wellOfficeExt.Upgrade || {};

// Fixes a content setting
wellOfficeExt.Upgrade.fixContentSetting = function(settingType) {
    browser.contentSettings[settingType].get({
        primaryUrl : "http://*/*"
    }).then(function(details) {
        // If the setting is currently set to allow
        if (details.setting == "allow") {
            browser.contentSettings[settingType].clear({});
        }
    });
};

// Fixes the content settings
wellOfficeExt.Upgrade.fixContentSettings = function() {
    // If content settings exists
    if (browser.contentSettings) {
        var settingTypes = [ "cookies", "images", "javascript", "notifications", "plugins", "popups" ];

        // Loop through the setting types
        for (var i = 0, l = settingTypes.length; i < l; i++) {
            wellOfficeExt.Upgrade.fixContentSetting(settingTypes[i]);
        }
    }
};

// Migrates any legacy settings
wellOfficeExt.Upgrade.migrateLegacySettings = function() {
    // Loop through the legacy settings
    for (var i = 0, l = window.localStorage.length; i < l; i++) {
        var key = window.localStorage.key(i);

        wellOfficeExt.Storage.setItemIfNotSet(key, window.localStorage.getItem(key));
    }

    // window.localStorage.clear();
};

// Opens the upgrade URL
wellOfficeExt.Upgrade.openUpgradeURL = function(version) {
    browser.tabs.create({
        url : "@url@@browser@/installed/" + version + "/"
    });
};

// Removes any deleted settings
wellOfficeExt.Upgrade.removeDeletedSettings = function() {
    wellOfficeExt.Storage.removeItem("icon_color");
};

// Sets up the default options
wellOfficeExt.Upgrade.setupDefaultOptions = function() {
    // Advanced
    wellOfficeExt.Storage.setItemIfNotSet("webapp_address", "http://oa.well-soft.com:8080/newoa");

    // General
    wellOfficeExt.Storage.setItemIfNotSet("display_overlay_with", "icons_text");

};

// Upgrades the extension
wellOfficeExt.Upgrade.upgrade = function(details) {
    // If the extension was installed or updated
    if (/* details.reason === "install" || */details.reason === "update") {
        wellOfficeExt.Upgrade.openUpgradeURL("@version@");

        wellOfficeExt.Storage.getItem("version", function(item) {
            // If the versions do not match
            if (item != "@version@") {
                wellOfficeExt.Storage.setItem("version", "@version@");

                wellOfficeExt.Upgrade.fixContentSettings();
                wellOfficeExt.Upgrade.migrateLegacySettings();
                wellOfficeExt.Upgrade.removeDeletedSettings();

                // Run on a timeout to make sure all the migration has completed
                window.setTimeout(function() {
                    wellOfficeExt.Upgrade.setupDefaultOptions();
                }, 100);
            }
        });
    }
};

browser.runtime.onInstalled.addListener(wellOfficeExt.Upgrade.upgrade);
