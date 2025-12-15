var wellOfficeExt = wellOfficeExt || {}; // eslint-disable-line
											// no-use-before-define

wellOfficeExt.Options = wellOfficeExt.Options || {};
wellOfficeExt.Options.animationSpeed = 100;

$(function() {
	var hash = window.location.hash;

	wellOfficeExt.Options.initialize();

	wellOfficeExt.Storage.getItem("option", function(option) {
		// If the hash is set
		if (hash) {
			$("a", $(hash)).tab("show");
			wellOfficeExt.Storage.setItem("option", hash);
		} else if (option) {
			$("a", $("#" + option)).tab("show");
		}
	});

	$("li", $(".nav-tabs")).on("click", wellOfficeExt.Options.changeTab);
});

// Handles a tab change
wellOfficeExt.Options.changeTab = function() {
	wellOfficeExt.Storage.setItem("option", $(this).attr("id"));
};

// Closes the option
wellOfficeExt.Options.closeOption = function(options, form, clearCallback) {
	form.slideUp(wellOfficeExt.Options.animationSpeed, function() {
		$(".table-container", options).slideDown(wellOfficeExt.Options.animationSpeed);

		clearCallback();

		form.removeData("position");
	});
};

// Initializes the options
wellOfficeExt.Options.initialize = function() {
	wellOfficeExt.Options.localize();
	wellOfficeExt.Options.initializeGeneralTab();
	wellOfficeExt.Options.initializeAdvancedTab();
};

// Initializes the advanced tab
wellOfficeExt.Options.initializeAdvancedTab = function() {
	wellOfficeExt.Storage.getItem("webapp_address", function(item) {
		$("#webapp_address").val(item).on("change", wellOfficeExt.Options.updatePopulateEmailAddress);
	});
};

// Initializes the general tab
wellOfficeExt.Options.initializeGeneralTab = function() {
	wellOfficeExt.Storage.getItem("display_overlay_with", function(item) {
		$("#display_overlay_with").val(item).on("change", wellOfficeExt.Options.updateDisplayOverlayWith);
	});
};

// Localizes the options
wellOfficeExt.Options.localize = function() {
	var deleteDialog = $("#delete-dialog");

	$("title").text(wellOfficeExt.Locales.getString("extensionName") + " " + wellOfficeExt.Locales.getString("options"));
	$(".navbar-brand").text(wellOfficeExt.Locales.getString("options"));
	$(".navbar-text").text(wellOfficeExt.Locales.getString("extensionName"));

	$("a", $("#advanced-tab")).append(wellOfficeExt.Locales.getString("advanced"));
	$("a", $("#general-tab")).append(wellOfficeExt.Locales.getString("general"));

	$(".modal-footer > .btn-default", deleteDialog).text(wellOfficeExt.Locales.getString("cancel"));
	$(".btn-danger", deleteDialog).append(wellOfficeExt.Locales.getString("delete"));

	wellOfficeExt.Options.localizeGeneralTab();
	wellOfficeExt.Options.localizeAdvancedTab();
};

// Localizes the advanced tab
wellOfficeExt.Options.localizeAdvancedTab = function() {
	$('[for="webapp_address"]').text(wellOfficeExt.Locales.getString("webappAddress"));
};

// Localizes the general tab
wellOfficeExt.Options.localizeGeneralTab = function() {
	$('[for="display_overlay_with"]').text(wellOfficeExt.Locales.getString("displayOverlayWith"));

	$('[value="icons_text"]').text(wellOfficeExt.Locales.getString("iconsText"));
	$('[value="icons"]').text(wellOfficeExt.Locales.getString("icons"));
	$('[value="text"]').text(wellOfficeExt.Locales.getString("text"));
};

// Resets the option form
wellOfficeExt.Options.resetOptionForm = function(form) {
	$(".has-error", form).removeClass("has-error");
	$(".help-block", form).text("");
};

// Submits the option
wellOfficeExt.Options.submitOption = function(option, options, position) {
	// If the position is set
	if (position) {
		$("tbody > tr:eq(" + (position - 1) + ")", options).replaceWith(option);
	} else {
		$("tbody", options).append(option);
	}
};

// Updates the display overlay with setting
wellOfficeExt.Options.updateDisplayOverlayWith = function() {
	wellOfficeExt.Storage.setItem("display_overlay_with", $("#display_overlay_with").val());
};

// Updates the populate email address
wellOfficeExt.Options.updatePopulateEmailAddress = function() {
	wellOfficeExt.Storage.setItem("webapp_address", $("#webapp_address").val());
};

// Returns true if the tool is valid
wellOfficeExt.Options.validateTool = function() {
	var valid = true;

	wellOfficeExt.Options.resetOptionForm($("#tool-form"));

	return valid;
};
