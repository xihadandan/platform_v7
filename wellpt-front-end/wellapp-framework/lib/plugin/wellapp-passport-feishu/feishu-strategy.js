var util = require('util')
    , Strategy = require('passport-strategy')
    , lookup = require('passport-local/lib/utils').lookup
    , passport = require('passport-strategy');

function FeishuStrategy(options, verify) {
    if (typeof options == 'function') {
        verify = options;
        options = {};
    }
    if (!verify) { throw new TypeError('FeishuStrategy requires a verify callback'); }

    passport.Strategy.call(this);
    this.name = 'feishu-token';
    this._verify = verify;
    this._passReqToCallback = options.passReqToCallback;
}

FeishuStrategy.prototype.authenticate = function (req, options) {
    options = options || {};
    var accessToken = lookup(req.body, 'feishuUserAccessToken') || lookup(req.query, 'feishuUserAccessToken');

    if (!accessToken) {
        return this.fail({ message: options.badRequestMessage || 'Missing accessToken' }, 400);
    }

    var self = this;

    function verified(err, user, info) {
        if (err) { return self.error(err); }
        if (!user) { return self.fail(info); }
        self.success(user, info);
    }

    try {
        if (self._passReqToCallback) {
            this._verify(req, accessToken, verified);
        } else {
            this._verify(accessToken, verified);
        }
    } catch (ex) {
        return self.error(ex);
    }
}


util.inherits(FeishuStrategy, Strategy);


module.exports = FeishuStrategy;
