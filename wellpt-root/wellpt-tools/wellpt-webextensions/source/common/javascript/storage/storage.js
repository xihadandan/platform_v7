var wellOfficeExt = wellOfficeExt || {}; // eslint-disable-line no-use-before-define

wellOfficeExt.Storage           = wellOfficeExt.Storage || {};
wellOfficeExt.Storage.storageId = "well-office-ext";

// Clears the features on a tab
wellOfficeExt.Storage.clearTabFeatures = function(tabProperties, tabId, updateBadgeText)
{
  // If there are no tab properties, no status or the status is loading
  if(!tabProperties || !tabProperties.status || tabProperties.status == "loading")
  {
    wellOfficeExt.Storage.removeItem(tabId);

    // If the badge text should be updated
    if(updateBadgeText)
    {
      wellOfficeExt.Storage.updateBadgeText(tabId);
    }
  }
};

// Returns the list of features on a tab
wellOfficeExt.Storage.getFeaturesOnTab = function(tabId, callback)
{
  wellOfficeExt.Storage.getItem(tabId, function(featuresOnTab)
  {
    // If there are features on the tab
    if(featuresOnTab)
    {
      callback(featuresOnTab.split(","));
    }
    else
    {
      callback(null);
    }
  });
};

// Returns an item
wellOfficeExt.Storage.getItem = function(item, callback)
{
  browser.storage.local.get(item.toString()).then(function(storageItem)
  {
    // If the item was found
    if(item in storageItem)
    {
      callback(storageItem[item]);
    }
    else
    {
      callback(null);
    }
  });
};

// Returns multiple items
wellOfficeExt.Storage.getItems = function(items, callback)
{
  browser.storage.local.get(items).then(function(storageItems)
  {
    callback(storageItems);
  });
};

// Returns true if a feature is on a tab
wellOfficeExt.Storage.isFeatureOnTab = function(feature, tab, callback)
{
  var isFeatureOnTab = false;
  var tabId          = tab.id;

  wellOfficeExt.Storage.getItem(tabId, function(featuresOnTab)
  {
    // If there are features on the tab
    if(featuresOnTab)
    {
      var featuresOnTabArray = featuresOnTab.split(",");

      // Loop through the features on the tab
      for(var i = 0, l = featuresOnTabArray.length; i < l; i++)
      {
        // If the feature is on the tab
        if(featuresOnTabArray[i] == feature)
        {
          isFeatureOnTab = true;
        }
      }
    }

    callback(isFeatureOnTab);
  });
};

// Removes an item
wellOfficeExt.Storage.removeItem = function(item)
{
  browser.storage.local.remove(item.toString());
};

// Sets an item
wellOfficeExt.Storage.setItem = function(item, value)
{
  var storageItem = {};

  storageItem[item] = value;

  browser.storage.local.set(storageItem);
};

// Sets an item if it is not already set
wellOfficeExt.Storage.setItemIfNotSet = function(item, value)
{
  wellOfficeExt.Storage.getItem(item, function(existingItem)
  {
    // If the item is not already set
    if(!existingItem)
    {
      wellOfficeExt.Storage.setItem(item, value);
    }
  });
};

// Handles a tab being activated
wellOfficeExt.Storage.tabActivated = function(tabInfo)
{
  wellOfficeExt.Storage.updateBadgeText(tabInfo.tabId);
};

// Handles a tab being removed
wellOfficeExt.Storage.tabRemoved = function(tabId, properties)
{
  wellOfficeExt.Storage.clearTabFeatures(properties, tabId, false);
};

// Handles a tab updating
wellOfficeExt.Storage.tabUpdated = function(tabId, properties)
{
  wellOfficeExt.Storage.clearTabFeatures(properties, tabId, true);
};

// Toggles a feature on a tab
wellOfficeExt.Storage.toggleFeatureOnTab = function(feature, tab)
{
  var featureTabId = tab.id;

  wellOfficeExt.Storage.getItem(featureTabId, function(currentFeaturesOnTab)
  {
    var newFeaturesOnTab = null;

    // If there are features on the tab
    if(currentFeaturesOnTab)
    {
      var featureOnTab = false;

      newFeaturesOnTab = currentFeaturesOnTab.split(",");

      // Loop through the features on the tab
      for(var i = 0, l = newFeaturesOnTab.length; i < l; i++)
      {
        // If the feature is on the tab
        if(newFeaturesOnTab[i] == feature)
        {
          featureOnTab = true;

          newFeaturesOnTab.splice(i, 1);
        }
      }

      // If the feature is on the tab
      if(featureOnTab)
      {
        newFeaturesOnTab = newFeaturesOnTab.join(",");
      }
      else
      {
        newFeaturesOnTab = currentFeaturesOnTab + "," + feature;
      }
    }
    else
    {
      newFeaturesOnTab = feature;
    }

    wellOfficeExt.Storage.setItem(featureTabId, newFeaturesOnTab);

    wellOfficeExt.Storage.updateBadgeText(featureTabId);
  });
};

// Updates the badge text for a tab
wellOfficeExt.Storage.updateBadgeText = function(featureTabId)
{
  var badgeText    = "";
  var badgeTooltip = "@name@";

  wellOfficeExt.Storage.getFeaturesOnTab(featureTabId, function(featuresOnTab)
  {
    // If there are features on the tab
    if(featuresOnTab)
    {
      var featureCount       = featuresOnTab.length;
      var featureDescription = "features";

      // If there is only one feature count
      if(featureCount == 1)
      {
        featureDescription = "feature";
      }

      badgeText     = featureCount.toString();
      badgeTooltip += "\n" + badgeText + " active " + featureDescription + " on this tab";
    }

    browser.browserAction.setBadgeText({ text: badgeText, tabId: featureTabId });
    browser.browserAction.setTitle({ title: badgeTooltip, tabId: featureTabId });
  });
};

browser.tabs.onActivated.addListener(wellOfficeExt.Storage.tabActivated);
browser.tabs.onRemoved.addListener(wellOfficeExt.Storage.tabRemoved);
browser.tabs.onUpdated.addListener(wellOfficeExt.Storage.tabUpdated);

browser.browserAction.setBadgeBackgroundColor({ color: [0, 200, 0, 255] });
