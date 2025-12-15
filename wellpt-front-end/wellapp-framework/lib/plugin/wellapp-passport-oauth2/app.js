'use strict';
var passport = require('passport'),
  OAuth2Strategy = require('passport-oauth2').Strategy,
  querystring = require('querystring'),
  OAuth2PasswordStrategy = require('./password-strategy');
module.exports = app => {
  const config = app.config.wellappPassportOauth2;
  config.passReqToCallback = true;
  const clientSecretBase64 = Buffer.from(config.clientID + ':' + config.clientSecret, 'utf8').toString('base64');
  config.customHeaders = {
    Authorization: 'basic ' + clientSecretBase64
  };
  const oAuth2Strategy = new OAuth2Strategy(config, function (req, accessToken, refreshToken, profile, done) {
    profile.token = accessToken;
    profile.provider = 'oauth2';
    app.passport.doVerify(req, profile, done);
  });

  app.passport.verify(async (ctx, user) => {
    if (user && user.provider !== 'oauth2') {
      return null;
    }

    return {
      loginName: user.user_name,
      token: user.token,
      _AUTH: 'access_token',
      _PROVIDER: 'oauth2'
    };
  });

  var userProfileFunction = function (accessToken, done) {
    this._oauth2._request(
      'POST',
      config.tokenURL.replace('/token', '/check_token'),
      {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      querystring.stringify({
        token: accessToken
      }),
      null,
      function (error, data, response) {
        if (error) done(error);
        else {
          var results;
          try {
            results = JSON.parse(data);
          } catch (e) {
            return done(e, null);
          }
          return done(null, results);
        }
      }
    );
  };

  // 加载用户信息
  OAuth2Strategy.prototype.userProfile = userProfileFunction;

  // 授权码模式
  app.passport.use('oauth2', oAuth2Strategy);

  // 密码模式
  const oAuth2PasswordStrategy = new OAuth2PasswordStrategy(config, function (req, accessToken, refreshToken, profile, done) {
    profile.token = accessToken;
    profile.provider = 'oauth2-password';
    app.passport.doVerify(req, profile, done);
  });

  OAuth2PasswordStrategy.prototype.userProfile = userProfileFunction;
  app.passport.use('oauth2-password', oAuth2PasswordStrategy);

  app.passport.verify(async (ctx, user) => {
    let result;
    if (user && user.provider !== 'oauth2-password') {
      return null;
    }
    return {
      loginName: user.user_name,
      userId: user.user_name,
      token: user.token,
      _AUTH: 'access_token',
      _PROVIDER: 'oauth2-password'
    };
  });
};
