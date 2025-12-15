'use strict';

/**
 * egg-winter-passport-local default config
 * @member Config#winterPassportLocal
 * @property {String} SOME_KEY - some description
 */
exports.wellappPassportOauth2 = {
    authorizationURL:'http://localhost:6080/oauth/authorize',
    tokenURL:'http://localhost:6080/oauth/token',
    clientID:'d9448c4e0d698db4f822c79136dc9fcb',
    clientSecret:'95db44bc24f303cbe30ff1239b421759db90c967ff7bae9c57e2107934d02324',
    callbackURL:'http://localhost:7001/login/oauth2/callback',
    scope:['read','write'],
    // state:true
};
