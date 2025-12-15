var wellOfficeExt = wellOfficeExt || {}; // eslint-disable-line no-use-before-define

wellOfficeExt.Common                 = wellOfficeExt.Common || {};
wellOfficeExt.Overlay                = wellOfficeExt.Overlay || {};
wellOfficeExt.Overlay.animationSpeed = 100;

$(function()
{
  var notification = $("#notification");

  browser.extension.getBackgroundPage().wellOfficeExt.Storage.getItem("display_overlay_with", function(displayOverlayWith)
  {
    wellOfficeExt.Overlay.labelMenu($("#options-toolbar > a"), wellOfficeExt.Locales.getString("options"), displayOverlayWith);

    // If the display overlay with setting is set to text
    if(displayOverlayWith == "text")
    {
    	$(".nav-tabs").addClass("overlay-text").find("a>i[class]").hide();
    }else {
    	$(".nav-tabs").addClass("overlay-text").find("a>i[class]").show();
    }
  });

  browser.extension.getBackgroundPage().wellOfficeExt.Storage.getItem("menu", function(menu)
  {
    // If the menu is not set
    if(!menu)
    {
      menu = $(".nav-tabs > li:visible:first").attr("id");
    }

    // If the menu is set
    if(menu)
    {
      $("a", $("#" + menu)).tab("show");
    }
  });

  wellOfficeExt.Overlay.getSelectedTab(function(tab)
  {
    browser.extension.getBackgroundPage().wellOfficeExt.Storage.getFeaturesOnTab(tab.id, function(featuresOnTab)
    {
      // If there are features on the tab
      if(featuresOnTab)
      {
        // Loop through the features on the tab
        for(var i = 0, l = featuresOnTab.length; i < l; i++)
        {
          $("#" + featuresOnTab[i]).addClass("active");
        }
      }
    });
  });

  $("#confirmation-cancel").on("click", wellOfficeExt.Overlay.closeConfirmation);
  $(".close", notification).on("click", wellOfficeExt.Overlay.closeNotification);
  $("li", $(".nav-tabs")).on("click", wellOfficeExt.Overlay.changeTab);
  $(notification).on("click", "a", wellOfficeExt.Overlay.openURL);
});

// Displays a notification
wellOfficeExt.Common.displayNotification = function(message, parameters)
{
  // If parameters are set
  if(parameters)
  {
    wellOfficeExt.Overlay.displayNotification(wellOfficeExt.Locales.getFormattedString(message, parameters));
  }
  else
  {
    wellOfficeExt.Overlay.displayNotification(wellOfficeExt.Locales.getString(message));
  }
};

// Adds a feature on a tab
wellOfficeExt.Overlay.addFeatureOnTab = function(featureItem, tab, scriptFile, scriptCode)
{
  wellOfficeExt.Overlay.addScriptsToTab(tab, scriptFile, scriptCode, null);
};

// Adds a script to the tab
wellOfficeExt.Overlay.addScriptToTab = function(tab, script, callback)
{
  browser.tabs.executeScript(tab.id, script).then(callback);
};

// Adds scripts to the tab
wellOfficeExt.Overlay.addScriptsToTab = function(tab, scriptFile, scriptCode, callback)
{
  wellOfficeExt.Overlay.addScriptToTab(tab, { file: scriptFile }, function()
  {
    wellOfficeExt.Overlay.addScriptToTab(tab, { code: scriptCode }, callback);
  });
};

// Handles a tab change
wellOfficeExt.Overlay.changeTab = function()
{
  wellOfficeExt.Overlay.closeNotification();

  browser.extension.getBackgroundPage().wellOfficeExt.Storage.setItem("menu", $(this).attr("id"));
};

// Closes the overlay
wellOfficeExt.Overlay.close = function()
{
  window.close();
};

// Closes the confirmation
wellOfficeExt.Overlay.closeConfirmation = function(event, callback)
{
  $("#confirmation").slideUp(wellOfficeExt.Overlay.animationSpeed, callback);

  // If the event is set
  if(event)
  {
    event.preventDefault();
  }
};

// Closes the notification
wellOfficeExt.Overlay.closeNotification = function(event, callback)
{
  $("#notification").slideUp(wellOfficeExt.Overlay.animationSpeed, callback);

  // If the event is set
  if(event)
  {
    event.preventDefault();
  }
};

// Displays a confirmation
wellOfficeExt.Overlay.displayConfirmation = function(title, message, buttonText, buttonIcon, callback)
{
  var confirmation = $("#confirmation");

  wellOfficeExt.Overlay.closeConfirmation(null, function()
  {
    var buttonHTML = buttonText;

    // If the button icon is set
    if(buttonIcon)
    {
      buttonHTML = '<i class="icon-' + buttonIcon + '"></i> ' + buttonText;
    }

    $("span", confirmation).text(message);
    $("#confirmation-cancel").text(wellOfficeExt.Locales.getString("cancel"));
    $(".btn-warning", confirmation).html(buttonHTML).off("click").on("click", callback);
    confirmation.slideDown(wellOfficeExt.Overlay.animationSpeed);
  });
};

// Displays a notification
wellOfficeExt.Overlay.displayNotification = function(message, type)
{
  var notification = $("#notification");

  // If the type is not specified
  if(!type)
  {
    type = "success";
  }

  wellOfficeExt.Overlay.closeNotification(null, function()
  {
    notification.removeClass().addClass("alert alert-dismissable alert-" + type);
    $("span", notification).html(message);
    notification.slideDown(wellOfficeExt.Overlay.animationSpeed);
  });
};

// Returns the selected tab
wellOfficeExt.Overlay.getSelectedTab = function(callback)
{
  browser.tabs.query({ active: true, currentWindow: true }).then(function(tabs)
  {
    callback(tabs[0]);
  });
};

// Returns the selected window
wellOfficeExt.Overlay.getSelectedWindow = function(callback)
{
  browser.windows.getCurrent(callback);
};

// Returns true if this is a valid tab
wellOfficeExt.Overlay.isValidTab = function(tab)
{
  var url = tab.url;

  // If this is a chrome URL
  if(url.indexOf("chrome://") === 0 || url.indexOf("chrome-extension://") === 0 || url.indexOf("moz-extension://") === 0)
  {
    wellOfficeExt.Overlay.displayNotification(wellOfficeExt.Locales.getString("extensionName") + " " + wellOfficeExt.Locales.getString("internalBrowserPagesError"), "danger");

    return false;
  }
  else if(url.indexOf("https://addons.mozilla.org/") === 0 || url.indexOf("https://browser.google.com/extensions/") === 0 || url.indexOf("https://browser.google.com/webstore/") === 0)
  {
    wellOfficeExt.Overlay.displayNotification(wellOfficeExt.Locales.getString("extensionName") + " " + wellOfficeExt.Locales.getString("extensionGalleryError"), "danger");

    return false;
  }

  return true;
};

// Labels a menu
wellOfficeExt.Overlay.labelMenu = function(menu, label, displayOverlayWith)
{
  // If the display overlay with setting is set to icons only
  if(displayOverlayWith == "icons")
  {
    menu.attr("title", label);
  }
  else
  {
    menu.append(label);
  }
};

// Handles any overlay messages
wellOfficeExt.Overlay.message = function(message, sender, sendResponse)
{
  // If the message type is a notification
  if(message.type == "display-notification")
  {
    wellOfficeExt.Common.displayNotification(message.message, message.parameters);
  }

  sendResponse({});
};

// Opens a tab to the URL
wellOfficeExt.Overlay.openTab = function(tabURL)
{
  wellOfficeExt.Overlay.getSelectedTab(function(tab)
  {
    browser.tabs.create({ index: tab.index + 1, url: tabURL });

    wellOfficeExt.Overlay.close();
  });
};

// Opens a URL from the overlay
wellOfficeExt.Overlay.openURL = function(event)
{
  var href = $(this).attr("href");

  wellOfficeExt.Overlay.getSelectedTab(function(tab)
  {
    browser.tabs.create({ index: tab.index + 1, url: href });

    wellOfficeExt.Overlay.close();
  });

  event.preventDefault();
};

// Sets a content setting
wellOfficeExt.Overlay.setContentSetting = function(settingType, currentSetting, newSetting, menu, message)
{
  browser.contentSettings[settingType].clear({}).then(function()
  {
    browser.contentSettings[settingType].get({ primaryUrl: "http://*/*" }).then(function(details)
    {
      // If the setting is still set to the current setting
      if(details.setting == currentSetting)
      {
        browser.contentSettings[settingType].set({ primaryPattern: "<all_urls>", setting: newSetting }).then(function()
        {
          wellOfficeExt.Overlay.updateContentSettingMenu(menu, settingType);
          wellOfficeExt.Overlay.displayNotification(wellOfficeExt.Locales.getString(message));
        });
      }
      else
      {
        wellOfficeExt.Overlay.updateContentSettingMenu(menu, settingType);
        wellOfficeExt.Overlay.displayNotification(wellOfficeExt.Locales.getString(message));
      }
    });
  });
};

// Toggles a content setting
wellOfficeExt.Overlay.toggleContentSetting = function(settingType, menu, enableMessage, disableMessage)
{
  browser.contentSettings[settingType].get({ primaryUrl: "http://*/*" }).then(function(details)
  {
    var currentSetting = details.setting;

    // If the setting is currently set to block
    if(currentSetting == "block")
    {
      wellOfficeExt.Overlay.setContentSetting(settingType, currentSetting, "allow", menu, enableMessage);
    }
    else
    {
      wellOfficeExt.Overlay.setContentSetting(settingType, currentSetting, "block", menu, disableMessage);
    }
  });
};

// Toggles a feature on a tab
wellOfficeExt.Overlay.toggleFeatureOnTab = function(featureItem, tab, scriptFile, scriptCode, closeOverlay)
{
  var feature = featureItem.attr("id");

  wellOfficeExt.Overlay.addScriptsToTab(tab, scriptFile, scriptCode, function()
  {
    browser.extension.getBackgroundPage().wellOfficeExt.Storage.toggleFeatureOnTab(feature, tab);

    featureItem.toggleClass("active");

    // If the overlay should be closed
    if(closeOverlay)
    {
      wellOfficeExt.Overlay.close();
    }
  });
};

// Updates the menu
wellOfficeExt.Overlay.updateContentSettingMenu = function(menu, settingType)
{
  // If content settings exists
  if(browser.contentSettings)
  {
    browser.contentSettings[settingType].get({ primaryUrl: "http://*/*" }).then(function(details)
    {
      // If the setting is currently set to block
      if(details.setting == "block")
      {
        menu.addClass("active");
      }
      else if(menu.hasClass("active"))
      {
        menu.removeClass("active");
      }
    });
  }
};

browser.runtime.onMessage.addListener(wellOfficeExt.Overlay.message);
