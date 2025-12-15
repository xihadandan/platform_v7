// Load modules.
var passport = require('passport-strategy'),
  OAuth2 = require('oauth').OAuth2,
  util = require('util'),
  AuthorizationError = require('passport-oauth2').AuthorizationError,
  TokenError = require('passport-oauth2').TokenError,
  InternalOAuthError = require('passport-oauth2').InternalOAuthError,
  lookup = require('passport-local/lib/utils').lookup;

function OAuth2PasswordStrategy(options, verify) {
  if (typeof options == 'function') {
    verify = options;
    options = undefined;
  }
  options = options || {};

  if (!verify) {
    throw new TypeError('OAuth2PasswordStrategy requires a verify callback');
  }
  if (!options.tokenURL) {
    throw new TypeError('OAuth2PasswordStrategy requires a tokenURL option');
  }
  if (!options.clientID) {
    throw new TypeError('OAuth2PasswordStrategy requires a clientID option');
  }
  if (!options.clientSecret) {
    throw new TypeError('OAuth2PasswordStrategy requires a clientSecret option');
  }

  passport.Strategy.call(this);
  this.name = 'oauth2-password';
  this._verify = verify;
  this._oauth2 = new OAuth2(options.clientID, options.clientSecret, '', options.authorizationURL, options.tokenURL, options.customHeaders);

  this._callbackURL = options.callbackURL;
  this._scope = options.scope;
  this._scopeSeparator = options.scopeSeparator || ' ';
  this._passReqToCallback = options.passReqToCallback;
  this._skipUserProfile = options.skipUserProfile === undefined ? false : options.skipUserProfile;
  this._userNameField = options.userNameField || 'username';
  this._passwordField = options.passwordField || 'password';
}

util.inherits(OAuth2PasswordStrategy, passport.Strategy);

OAuth2PasswordStrategy.prototype.authenticate = function (req, options) {
  options = options || {};
  var self = this;
  var username = lookup(req.body, this._userNameField) || lookup(req.query, this._userNameField);
  var password = lookup(req.body, this._passwordField) || lookup(req.query, this._passwordField);

  if (!username || !password) {
    return this.fail({ message: options.badRequestMessage || 'Missing credentials' }, 400);
  }

  if (req.query && req.query.error) {
    if (req.query.error == 'access_denied') {
      return this.fail({ message: req.query.error_description });
    } else {
      return this.error(new AuthorizationError(req.query.error_description, req.query.error, req.query.error_uri));
    }
  }

  var params = self.tokenParams(options);
  params.grant_type = 'password';
  params.username = username;
  params.password = password;

  self._oauth2.getOAuthAccessToken(null, params, function (err, accessToken, refreshToken, params) {
    if (err) {
      return self.fail(self._createOAuthError('Failed to obtain access token', err));
      // return self.error(self._createOAuthError('Failed to obtain access token', err));
    }

    self._loadUserProfile(accessToken, function (err, profile) {
      if (err) {
        return self.error(err);
      }

      function verified(err, user, info) {
        if (err) {
          return self.error(err);
        }
        if (!user) {
          return self.fail(info);
        }
        self.success(user, info);
      }

      try {
        if (self._passReqToCallback) {
          var arity = self._verify.length;
          if (arity == 6) {
            self._verify(req, accessToken, refreshToken, params, profile, verified);
          } else {
            // arity == 5
            self._verify(req, accessToken, refreshToken, profile, verified);
          }
        } else {
          var arity = self._verify.length;
          if (arity == 5) {
            self._verify(accessToken, refreshToken, params, profile, verified);
          } else {
            // arity == 4
            self._verify(accessToken, refreshToken, profile, verified);
          }
        }
      } catch (ex) {
        return self.error(ex);
      }
    });
  });
};

/**
 * Retrieve user profile from service provider.
 *
 * OAuth 2.0-based authentication strategies can overrride this function in
 * order to load the user's profile from the service provider.  This assists
 * applications (and users of those applications) in the initial registration
 * process by automatically submitting required information.
 *
 * @param {String} accessToken
 * @param {Function} done
 * @api protected
 */
OAuth2PasswordStrategy.prototype.userProfile = function (accessToken, done) {
  return done(null, {});
};

/**
 * Return extra parameters to be included in the authorization request.
 *
 * Some OAuth 2.0 providers allow additional, non-standard parameters to be
 * included when requesting authorization.  Since these parameters are not
 * standardized by the OAuth 2.0 specification, OAuth 2.0-based authentication
 * strategies can overrride this function in order to populate these parameters
 * as required by the provider.
 *
 * @param {Object} options
 * @return {Object}
 * @api protected
 */
OAuth2PasswordStrategy.prototype.authorizationParams = function (options) {
  return {};
};

/**
 * Return extra parameters to be included in the token request.
 *
 * Some OAuth 2.0 providers allow additional, non-standard parameters to be
 * included when requesting an access token.  Since these parameters are not
 * standardized by the OAuth 2.0 specification, OAuth 2.0-based authentication
 * strategies can overrride this function in order to populate these parameters
 * as required by the provider.
 *
 * @return {Object}
 * @api protected
 */
OAuth2PasswordStrategy.prototype.tokenParams = function (options) {
  return {};
};

/**
 * Parse error response from OAuth 2.0 endpoint.
 *
 * OAuth 2.0-based authentication strategies can overrride this function in
 * order to parse error responses received from the token endpoint, allowing the
 * most informative message to be displayed.
 *
 * If this function is not overridden, the body will be parsed in accordance
 * with RFC 6749, section 5.2.
 *
 * @param {String} body
 * @param {Number} status
 * @return {Error}
 * @api protected
 */
OAuth2PasswordStrategy.prototype.parseErrorResponse = function (body, status) {
  var json = JSON.parse(body);
  if (json.error) {
    return new TokenError(json.error_description, json.error, json.error_uri);
  }
  return null;
};

/**
 * Load user profile, contingent upon options.
 *
 * @param {String} accessToken
 * @param {Function} done
 * @api private
 */
OAuth2PasswordStrategy.prototype._loadUserProfile = function (accessToken, done) {
  var self = this;

  function loadIt() {
    return self.userProfile(accessToken, done);
  }
  function skipIt() {
    return done(null);
  }

  if (typeof this._skipUserProfile == 'function' && this._skipUserProfile.length > 1) {
    // async
    this._skipUserProfile(accessToken, function (err, skip) {
      if (err) {
        return done(err);
      }
      if (!skip) {
        return loadIt();
      }
      return skipIt();
    });
  } else {
    var skip = typeof this._skipUserProfile == 'function' ? this._skipUserProfile() : this._skipUserProfile;
    if (!skip) {
      return loadIt();
    }
    return skipIt();
  }
};

/**
 * Create an OAuth error.
 *
 * @param {String} message
 * @param {Object|Error} err
 * @api private
 */
OAuth2PasswordStrategy.prototype._createOAuthError = function (message, err) {
  var e;
  if (err.statusCode && err.data) {
    try {
      e = this.parseErrorResponse(err.data, err.statusCode);
    } catch (_) {}
  }
  if (!e) {
    e = new InternalOAuthError(message, err);
  }
  return e;
};

module.exports = OAuth2PasswordStrategy;
