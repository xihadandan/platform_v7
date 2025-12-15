define([ "commons", "constant", "server" ], function(commons, constant, server) {
	var GetCount = function() {
	};

	/**
	 * 返回数量
	 */
	GetCount.prototype.getCount = function(options) {
		options.callback(options, 0);
	};
	GetCount.prototype.applyCallback = function(options, count) {
		var abbreviationCount = GetCount.changeCount(count);
		options.callback(abbreviationCount, count, options);
	};
	/**
	 * 静态方法，如果获取数量来源非单一，可以通过该方法做转换，达到获取缩略值保持相同逻辑。
	 */
	GetCount.changeCount = function(count) {
		var abbreviationCount = count
		if (count > 1000 && count < 10000) {
			if (count % 100 > 0) {
				abbreviationCount = (parseInt(count / 100) * 100) + "+";
			}
		} else if (count >= 10000 && count < 100000) {
			var w = parseInt(count / 1000);
			abbreviationCount = w / 10 + "万";
			if (count % 1000 > 0) {
				abbreviationCount = abbreviationCount + "+";
			}
		} else if (count >= 100000 && count < 1000000) {
			var w = parseInt(count / 10000);
			abbreviationCount = w + "万";
			if (count % 10000 > 0) {
				abbreviationCount = abbreviationCount + "+";
			}
		} else if (count == 1000000) {
			abbreviationCount = "100万";
		} else if (count > 1000000) {
			abbreviationCount = "100万+";
		}
		return abbreviationCount;
	}
	return GetCount;
});