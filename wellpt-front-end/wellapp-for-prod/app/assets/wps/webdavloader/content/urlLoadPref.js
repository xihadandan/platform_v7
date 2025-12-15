// Copyright 2008 Benryan Software Inc.
// All rights reserved.
// THIS IS NOT OPEN SOURCE SOFTWARE
// Benryan Software explicitly denies you the right to copy, modify, 
// create derivative works, or re-distribute this software. Only licensees of 
// Word/DAV for Confluence can distribute this software for use with Word/DAV for Confluence.

function urlLoadPref(){}

urlLoadPref.prototype = {};

urlLoadPref.prototype.init = function()
{
	if (this.checkForWindows())
	{
		var node = document.getElementById("extHBox");
		var regButton = document.createElement("button");
		regButton.setAttribute("label", "Auto");
		regButton.addEventListener("command", this.loadAppPathFromReg, false);
		node.appendChild(regButton);		
	}
	this.loadAllExtensions();
};

urlLoadPref.prototype.loadAppPathFromReg = function()
{
	var extNode = document.getElementById("fileExt");
	var ext;
	if (extNode)
	{
		ext = extNode.value;
	}
	if (ext)
	{
		var wrk = Components.classes["@mozilla.org/windows-registry-key;1"].createInstance(Components.interfaces.nsIWindowsRegKey);
		wrk.open(wrk.ROOT_KEY_CLASSES_ROOT, "." + ext, wrk.ACCESS_READ);
		var formalName = wrk.readStringValue("");
		wrk.close();
		wrk.open(wrk.ROOT_KEY_CLASSES_ROOT, formalName + "\\shell\\Open\\command", wrk.ACCESS_READ);
		var command = wrk.readStringValue("");
        wrk.close();
        var fileName = command.match(/^"[^"]+/);
		if (!fileName)
		{
			fileName = command.match(/^[^\s]+/);
		}
		else
		{
			// get rid of leading quote
			fileName[0] = fileName[0].substr(1);
		}
		if (fileName)
		{
			var pathNode = document.getElementById("appPath");
			if (pathNode)
			{
				pathNode.value = fileName[0];
			}
		}
	}

};

urlLoadPref.prototype.loadSupportedExtensions = function ()
{
	var prefs = this.getPrefsObject();
	var extList;
	if (prefs.prefHasUserValue("ext"))
	{
		var extensions = prefs.getCharPref("ext");
		if (extensions)
		{
			extList = extensions.split(" ");
		}
	}
	return extList;
};
urlLoadPref.prototype.checkForWindows = function()
{
	try
	{
		var wrk = Components.classes["@mozilla.org/windows-registry-key;1"].createInstance(Components.interfaces.nsIWindowsRegKey);
		return true;
	}
	catch(e){}
	return false;
};
urlLoadPref.prototype.getLauncherForExtension = function(extension)
{
	var prefs = this.getPrefsObject();
	if (prefs.prefHasUserValue("extInfo." + extension))
	{
		return prefs.getCharPref("extInfo." + extension);
	}
	return null;	
};

urlLoadPref.prototype.savePrefs = function(extensions, launchers)
{
	var prefs = this.getPrefsObject();
	var strExtensions = "";
	prefs.deleteBranch("extInfo");
	for (var i = 0; i < extensions.length; i++)
	{
		prefs.setCharPref("extInfo." + extensions[i], launchers[i]);
		strExtensions += (extensions[i] + " ");
	}
	prefs.setCharPref("ext", strExtensions);
};
urlLoadPref.prototype.enableDelete = function(tree)
{
	var delButton = document.getElementById("delete");
	if (tree.currentIndex != -1)
	{
		delButton.disabled = false;
	}
	else
	{
		delButton.disabled = true;
	}
};
urlLoadPref.prototype.addExtensionToList = function()
{
	var extNode = document.getElementById("fileExt");
	var ext;
	if (extNode)
	{
		ext = extNode.value;
	}
	var pathNode = document.getElementById("appPath");
	var path;
	if (pathNode)
	{
		path = pathNode.value;
	}
	if (ext && ext.length > 0 && path && path.length > 0)
	{
		var alreadyExists = document.getElementById("supportedExts." + ext);
		if (alreadyExists)
		{
			alreadyExists.firstChild.childNodes[1].setAttribute("label", path);
		}
		else
		{
			var listNode = document.getElementById("supportedExts");
			if (listNode)
			{
				var treeItem = document.createElement("treeitem");
				treeItem.setAttribute("id", "supportedExts." + ext);
				var treeRow = document.createElement("treerow");
				var extCell = document.createElement("treecell");
				extCell.setAttribute("label", ext);
				var pathCell = document.createElement("treecell");
				pathCell.setAttribute("label", path);
				treeRow.appendChild(extCell);
				treeRow.appendChild(pathCell);
				treeItem.appendChild(treeRow);
				listNode.appendChild(treeItem);
			}
		}
	}
};
urlLoadPref.prototype.removeExtensionFromList = function()
{
	var tree = document.getElementById("extList");
	var idx = tree.currentIndex;
	if (idx != -1)
	{
		var listNode = document.getElementById("supportedExts");
		listNode.removeChild(listNode.childNodes[idx]);
	}
};
urlLoadPref.prototype.loadAllExtensions = function()
{
	var exts = this.loadSupportedExtensions();
	var listNode = document.getElementById("supportedExts");
	if (exts && listNode)
	{
		for (var i = 0; i < exts.length; i++)
		{
			var path = this.getLauncherForExtension(exts[i]);
			if (path)
			{
				var treeItem = document.createElement("treeitem");
				treeItem.setAttribute("id", "supportedExts." + exts[i]);
				var treeRow = document.createElement("treerow");
				var extCell = document.createElement("treecell");
				extCell.setAttribute("label", exts[i]);
				var pathCell = document.createElement("treecell");
				pathCell.setAttribute("label", path);
				treeRow.appendChild(extCell);
				treeRow.appendChild(pathCell);
				treeItem.appendChild(treeRow);
				listNode.appendChild(treeItem);
			}
		}
	}
};
urlLoadPref.prototype.saveAllExtensions = function()
{
	var listNode = document.getElementById("supportedExts");
	if (listNode)
	{
		var items = listNode.childNodes;
		var exts = new Array(items.length);
		var paths = new Array(items.length);
		
		for (var i = 0; i < items.length; i++)
		{
			var rowChildren = items[i].firstChild.childNodes;
			exts[i] = rowChildren[0].getAttribute("label");
			paths[i] = rowChildren[1].getAttribute("label");
		}
		this.savePrefs(exts, paths);
	}
};
urlLoadPref.prototype.browseForApp = function()
{
   
   var fpicker = Components.classes["@mozilla.org/filepicker;1"].createInstance(Components.interfaces.nsIFilePicker);
   fpicker.init(window, null, Components.interfaces.nsIFilePicker.modeOpen);  
   
   var rv = fpicker.show();
  
   if (rv == Components.interfaces.nsIFilePicker.returnOK) 
   {
	   
     var pathBox = document.getElementById("appPath");
	 if (pathBox)
	 {
		 
		
		var test1 = fpicker.file.path;
		if (test1 && test1.length > 0)
		{
			pathBox.value=fpicker.file.path;
		}
		else
		{
			pathBox.value=fpicker.file.persistentDescriptor;
		}		 
	 }
   }
};

urlLoadPref.prototype.getPrefsObject = function ()
{
	 var prefservice = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
   	 var prefs = prefservice.getBranch("benryan.urlLoader");
	 return prefs;
};

var gUrlLoadPref = new urlLoadPref();