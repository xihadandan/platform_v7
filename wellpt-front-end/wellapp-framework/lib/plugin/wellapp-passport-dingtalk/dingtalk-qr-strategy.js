var util = require('util')
    , Strategy = require('passport-strategy')
    , lookup = require('passport-local/lib/utils').lookup
    , passport = require('passport-strategy');

function DingtalkQRStrategy(options, verify) {
    if (typeof options == 'function') {
        verify = options;
        options = {};
    }
    if (!verify) { throw new TypeError('DingtalkQRStrategy requires a verify callback'); }

    this._codeField = options.codeField || 'code';
    this._ssoField = options.codeField || 'sso';

    passport.Strategy.call(this);
    this.name = 'dingtalk-qr';
    this._verify = verify;
    this._passReqToCallback = options.passReqToCallback;
}

DingtalkQRStrategy.prototype.authenticate = function (req, options) {
    options = options || {};
    var code = lookup(req.body, this._codeField) || lookup(req.query, this._codeField);
    var sso = lookup(req.body, this._ssoField) || lookup(req.query, this._ssoField) || 'false';

    if (!code) {
        return this.fail({ message: options.badRequestMessage || 'Missing code' }, 400);
    }

    var self = this;

    function verified(err, user, info) {
        if (err) { return self.error(err); }
        if (!user) { return self.fail(info); }
        self.success(user, info);
    }

    try {
        if (self._passReqToCallback) {
            this._verify(req, code, sso, verified);
        } else {
            this._verify(code, sso, verified);
        }
    } catch (ex) {
        return self.error(ex);
    }
}


util.inherits(DingtalkQRStrategy, Strategy);


module.exports = DingtalkQRStrategy;