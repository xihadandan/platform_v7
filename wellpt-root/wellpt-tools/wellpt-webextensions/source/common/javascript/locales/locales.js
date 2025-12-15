var wellOfficeExt = wellOfficeExt || {}; // eslint-disable-line no-use-before-define

wellOfficeExt.Locales = wellOfficeExt.Locales || {};

// Returns a formatted string from the locale
wellOfficeExt.Locales.getFormattedString = function(name, parameters)
{
  return browser.i18n.getMessage(name, parameters);
};

// Returns a string from the locale
wellOfficeExt.Locales.getString = function(name)
{
  return browser.i18n.getMessage(name);
};

// Sets up the generated locale
wellOfficeExt.Locales.setupGeneratedLocale = function()
{
  var locale = {};

  locale.collapseAll   = wellOfficeExt.Locales.getString("collapseAll");
  locale.documents     = wellOfficeExt.Locales.getString("documents");
  locale.expandAll     = wellOfficeExt.Locales.getString("expandAll");
  locale.extensionName = wellOfficeExt.Locales.getString("extensionName");
  locale.from          = wellOfficeExt.Locales.getString("from");

  return locale;
};
