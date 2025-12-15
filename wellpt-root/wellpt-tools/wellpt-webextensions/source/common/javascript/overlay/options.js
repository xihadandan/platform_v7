var wellOfficeExt = wellOfficeExt || {}; // eslint-disable-line no-use-before-define

wellOfficeExt.Overlay         = wellOfficeExt.Overlay || {};
wellOfficeExt.Overlay.Options = wellOfficeExt.Overlay.Options || {};

$(function()
{
  $("#about").append(wellOfficeExt.Locales.getString("aboutMenu")).on("click", wellOfficeExt.Overlay.Options.about);
  $("#options").append(wellOfficeExt.Locales.getString("optionsMenu")).on("click", wellOfficeExt.Overlay.Options.options);
  $("#reset-page").append(wellOfficeExt.Locales.getString("resetPage")).on("click", wellOfficeExt.Overlay.Options.resetPage);
});

// Opens the about page
wellOfficeExt.Overlay.Options.about = function()
{
  wellOfficeExt.Overlay.getSelectedTab(function(tab)
  {
    browser.extension.getBackgroundPage().wellOfficeExt.Background.openGeneratedTab(browser.extension.getURL("/about/about.html"), tab.index, null, wellOfficeExt.Overlay.Options.getAboutLocale());
    wellOfficeExt.Overlay.close();
  });
};

// Returns the locale for the about feature
wellOfficeExt.Overlay.Options.getAboutLocale = function()
{
  var locale = {};

  locale.about                = wellOfficeExt.Locales.getString("about");
  locale.author               = wellOfficeExt.Locales.getString("author");
  locale.buildDate            = wellOfficeExt.Locales.getString("buildDate");
  locale.extensionDescription = wellOfficeExt.Locales.getString("extensionDescription");
  locale.extensionName        = wellOfficeExt.Locales.getString("extensionName");
  locale.followOnTwitter      = wellOfficeExt.Locales.getString("followOnTwitter");
  locale.version              = wellOfficeExt.Locales.getString("version");

  return locale;
};

// Opens the options
wellOfficeExt.Overlay.Options.options = function()
{
  browser.runtime.openOptionsPage();
  wellOfficeExt.Overlay.close();
};

// Resets the page
wellOfficeExt.Overlay.Options.resetPage = function()
{
  wellOfficeExt.Overlay.getSelectedTab(function(tab)
  {
    wellOfficeExt.Overlay.addScriptToTab(tab, { code: "window.location.reload();" }, function()
    {
      wellOfficeExt.Overlay.close();
    });
  });
};
