var wellOfficeExt = wellOfficeExt || {}; // eslint-disable-line no-use-before-define

wellOfficeExt.Cookies = wellOfficeExt.Cookies || {};

// Sanitizes a cookie host
wellOfficeExt.Cookies.sanitizeHost = function(host)
{
  // If the host is set and starts with '.'
  if(host && host.charAt(0) == ".")
  {
    return host.substring(1);
  }

  return host;
};
