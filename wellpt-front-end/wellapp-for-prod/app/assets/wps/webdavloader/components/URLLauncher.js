// Copyright 2008 Benryan Software Inc.
// All rights reserved.
// THIS IS NOT OPEN SOURCE SOFTWARE
// Unless you are a Word/DAV licensee, Benryan Software explicitly denies you the right to copy, modify, 
// create derivative works, or re-distribute this software. Only licensees of 
// Word/DAV for Confluence can distribute this software for use with Word/DAV for Confluence.

const nsISupports = Components.interfaces.nsISupports;
const nsIClassInfo = Components.interfaces.nsIClassInfo;
const nsIUrlOpener = Components.interfaces.nsIUrlOpener;


const CLASS_ID = Components.ID("CCC68874-E4B6-11DC-818D-337A55D89593");
const CLASS_NAME = "Launches an app to open a url. Used for editing files in a webdav repository";
const CONTRACT_ID = "@benryan.com/url/launch;1";


function URLLauncher() {
}


URLLauncher.prototype = {
  // for nsISupports
  QueryInterface: function(aIID)
  {
    if (!aIID.equals(nsISupports) && !aIID.equals(nsIUrlOpener) && !aIID.equals(nsIClassInfo))
        throw Components.results.NS_ERROR_NO_INTERFACE;
    return this;
  },

  open : function(urlStr)
  {
    var clazz = Components.classes["@mozilla.org/file/local;1"];
	var file = clazz.createInstance(Components.interfaces.nsILocalFile);
	var ext;

	var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator);
	var browserWindow = wm.getMostRecentWindow("navigator:browser");
	var isMac = browserWindow.navigator.appVersion.indexOf("Mac") != -1;

	var dotIdx = urlStr.lastIndexOf(".");
	if (dotIdx >= 0 && dotIdx < urlStr.length - 1)
	{
            var semiIdx = urlStr.indexOf(";", dotIdx);
            if (semiIdx > dotIdx)
            {
			ext = urlStr.substr(dotIdx + 1, semiIdx - (dotIdx + 1));
            }
            else
            {
			ext = urlStr.substr(dotIdx + 1);
            }
	}

    var path;
	if (ext)
	{
		path = this.getLauncherForExtension(ext, browserWindow.navigator.appVersion);
	}

    var prompts = Components.classes["@mozilla.org/embedcomp/prompt-service;1"].getService(Components.interfaces.nsIPromptService);
	if (path)
	{
		var pathExt = null;
		if (path.length > 4)
		{
			pathExt = path.substr(path.length - 4, 4);
		}
		if (isMac && pathExt == ".app")
		{
			path = this.findMacAppPath(path);
		}
		file.initWithPath(path);

		// create an nsIProcess
		var class1 = Components.classes["@mozilla.org/process/util;1"];
		var process = class1.createInstance(Components.interfaces.nsIProcess);
		process.init(file);

		// Run the process.
		var args = [urlStr];
		var result =  prompts.confirm(browserWindow, "Warning!", "The following location is going to be opened on your " +
	            "computer:\n\n" + urlStr + "\n\nBy the program:\n\n" + path + "\n\nThis poses a security risk. If you didn't "+
                "initiate this action or you don't trust the source of the file, please click Cancel.");
		if (result)
		{
			process.run(false, args, args.length);
		}
	}else{
		prompts.alert(browserWindow, "Warning",
            "Application is not found for this document. Please refer to below link and configure the add-on properly:"
			+ "\n\nhttps://confluence.atlassian.com/display/DOC/Installing+the+Firefox+Add-On+for+the+Office+Connector");
	}
  },
  findMacAppPath:function(path)
  {
	  var exeName = null;
	  if (!exeName)
	  {
		  var plstPath = path + "/Contents/Info.plist";
		  var clazz = Components.classes["@mozilla.org/file/local;1"];
		  var file = clazz.createInstance(Components.interfaces.nsILocalFile);

		  file.initWithPath(plstPath);

		  var fileIn = Components.classes["@mozilla.org/network/file-input-stream;1"].createInstance(Components.interfaces.nsIFileInputStream);
		  fileIn.init(file, -1,-1, Components.interfaces.nsIFileInputStream.CLOSE_ON_EOF);
		  var parser = Components.classes["@mozilla.org/xmlextras/domparser;1"].createInstance(Components.interfaces.nsIDOMParser);
		  var dom = parser.parseFromStream(fileIn, null, file.fileSize,"text/xml");

		  // should be only one
		  var dictList = dom.getElementsByTagName("dict");
		  if (dictList.length > 0)
		  {
			  var dict = dictList.item(0);
			  var node = dict.firstChild;
			  while (node)
			  {
				  if (node.nodeName == "key")
				  {
					  var keyTxtNode = node.firstChild;
					  if (keyTxtNode && keyTxtNode.nodeValue.toLowerCase() == "cfbundleexecutable")
					  {
						  var valNode = node.nextSibling;
						  while (valNode && valNode.nodeType != Components.interfaces.nsIDOMNode.ELEMENT_NODE)
						  {
							  valNode = valNode.nextSibling;
						  }
						  if (valNode && valNode.firstChild)
						  {
							exeName = path + "/Contents/MacOS/" + valNode.firstChild.nodeValue;
							break;
						  }
					  }
				  }
				  node = node.nextSibling;
			  }
		  }
	  }
	  return exeName;


  },
 getPrefsObject:function ()
{
	 var prefservice = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService);
   	 var prefs = prefservice.getBranch("benryan.urlLoader");
	 return prefs;
},
isMicrosoftOfficeInstalled : function(extension) {
	var prefs = this.getPrefsObject();
	if (prefs.prefHasUserValue("extInfo." + extension)) {
		return prefs.getCharPref("extInfo." + extension).indexOf("Microsoft") > -1;
	}
	return false;
},
getLauncherForExtension:function(extension, appVersion)
{
	var prefs = this.getPrefsObject();
    var supportedExtentions = ["doc", "xls", "ppt",
        "docx", "dot", "dotx",
        "xlsx", "xlt", "xlst", "xlsm",
        "pptx", "ppsx", "pot", "potx", "pptm"];

	if (prefs.prefHasUserValue("extInfo." + extension))
	{
		return prefs.getCharPref("extInfo." + extension);
	}
	else if (supportedExtentions.indexOf(extension) != -1)
	{
		var launchPath = null;
		// try and find launcher on Windows
		var classInst = Components.classes["@mozilla.org/windows-registry-key;1"];
		if (classInst)
		{
			var wrk = classInst. createInstance(Components.interfaces.nsIWindowsRegKey);
			if (wrk)
			{
				wrk.open(wrk.ROOT_KEY_CLASSES_ROOT, "." + extension, wrk.ACCESS_READ);
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
					launchPath = fileName[0];
				}
			}
		}
		if(!launchPath && appVersion.indexOf("Mac") != -1)
		{
			launchPath = "/Applications/NeoOffice.app";
		}
		if (!launchPath && appVersion.indexOf("Linux") != -1)
		{
			launchPath = "/usr/bin/ooffice";
		}
		if (launchPath)
		{
			 var clazz = Components.classes["@mozilla.org/file/local;1"];
			 var file = clazz.createInstance(Components.interfaces.nsILocalFile);
			 file.initWithPath(launchPath);
			 if (file.exists())
			 {
				  prefs.setCharPref("extInfo." + extension, launchPath);
				  var extList = "";
				  if (prefs.prefHasUserValue("ext"))
				  {
					  extList = prefs.getCharPref("ext");
				  }
				  extList = extList + " " + extension;
				  prefs.setCharPref("ext", extList);
				  return launchPath;
			 }
		}
	}
	return null;
},
  getInterfaces: function(aCount)
  {
    var array = [nsIUrlOpener,
                 nsIClassInfo];
    aCount.value = array.length;
    return array;
  },

   getHelperForLanguage: function(language)
   {
	return null;
   },

   get contractID()
  {
    return CONTRACT_ID;
  },

  get classDescription()
  {
    return CLASS_NAME;
  },

  get classID()
  {
    return CLASS_ID;
  },

  get implementationLanguage()
  {
    return Components.interfaces.nsIProgrammingLanguage.JAVASCRIPT;
  },

  get flags()
  {
    return Components.interfaces.nsIClassInfo.DOM_OBJECT;
  }

};

//=================================================
// Note: don't want to edit anything
// below this 
//
// Factory
var URLLauncherFactory = {
  createInstance: function (aOuter, aIID)
  {
    if (aOuter != null)
      throw Components.results.NS_ERROR_NO_AGGREGATION;
    return (new URLLauncher()).QueryInterface(aIID);
  }
};

// Module
var URLLauncherModule = {
  registerSelf: function(aCompMgr, aFileSpec, aLocation, aType)
  {

    aCompMgr = aCompMgr.QueryInterface(Components.interfaces.nsIComponentRegistrar);
    aCompMgr.registerFactoryLocation(CLASS_ID, CLASS_NAME, CONTRACT_ID, aFileSpec, aLocation, aType);

  },

  unregisterSelf: function(aCompMgr, aLocation, aType)
  {
    aCompMgr = aCompMgr.QueryInterface(Components.interfaces.nsIComponentRegistrar);
    aCompMgr.unregisterFactoryLocation(CLASS_ID, aLocation);
  },

  getClassObject: function(aCompMgr, aCID, aIID)
  {
    if (!aIID.equals(Components.interfaces.nsIFactory))
      throw Components.results.NS_ERROR_NOT_IMPLEMENTED;

    if (aCID.equals(CLASS_ID))
      return URLLauncherFactory;

    throw Components.results.NS_ERROR_NO_INTERFACE;
  },

  canUnload: function(aCompMgr) { return true; }
};

//module initialization
function NSGetModule(aCompMgr, aFileSpec) { return URLLauncherModule; }

function NSGetFactory(cid) {return URLLauncherFactory;}

