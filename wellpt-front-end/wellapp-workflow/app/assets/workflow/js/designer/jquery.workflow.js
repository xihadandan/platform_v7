(function($) {
	$.fn
			.extend({
				"selectSingleNode" : function(value) {
					var nodes = this.selectNodes(value);
					if (nodes == null) {
						return null;
					}
					return $(nodes[0]);
				},
				"selectNodes" : function(value) {
					if (value == undefined) {
						return null;
					}
					// 获取子结点等于指定值的父节点
					if ((value.indexOf("[") != -1)
							|| (value.indexOf("]") != -1)) {
						// "/diction/categorys/category[sn='123']"
						var start = value.indexOf("[");
						var parent = value.substring(0, start);
						// alert(value);
						parent = parent.replace("./", " ");
						while (parent.indexOf("/") != -1) {
							parent = parent.replace("/", " ");
						}
						parent = jQuery.trim(parent);
						// alert(parent);
						var child = value.substring((start + 1), value
								.indexOf("="));
						// alert(child);
						var vstart = value.indexOf("='") + 2;
						var vend = value.indexOf("']");
						var childValue = value.substring(vstart, vend);

						var parentNode = [];
						var parentNodes = this.find(parent);
						var isAttr = value.indexOf("[@") != -1;
						parentNodes.each(function() {
							var text = "";
							// 从属性取值
							if (isAttr == true) {
								text = $(this).attr(child.substring(1));
								// if (text == childValue) {
								// parentNode = $(this);
								// }
							} else {// 从子元素取值
								text = $(this).children(child).text();
								// if (text == childValue) {
								// parentNode = $(this);
								// }
							}
							if (text == childValue) {
								parentNode.push($(this));
							}
						});
						return parentNode.length == 0 ? null : $(parentNode);
					}// 获取指定路径节点
					else if ((value.indexOf("./") != -1)
							|| (value.indexOf("/") != -1)) {
						var path = value.replace("./", " ");
						while (path.indexOf("/") != -1) {
							path = path.replace("/", " ");
						}
						path = jQuery.trim(path);

						var node = null;
						try {
							node = this.find(path);
						} catch (e) {
							node = $(this.get(0).selectSingleNode(value));
						}
						return (node == null || node.length == 0) ? null : node;
					} else {
						var node = this.find(value);
						return node.length == 0 ? null : node;
					}
				},
				"getAttribute" : function(name) {
					return this.attr(name);
				},
				"setAttribute" : function(name, value) {
					return this.attr(name, value);
				},
				"appendChild" : function(child) {
					return this.append(child);
				},
				"removeChild" : function(child) {
					// 使用XML对象接口删除
					return this[0].removeChild(child[0]);
				},
				"cloneNode" : function(deepCopy) {
					return this.clone(deepCopy);
				}
			});
})(jQuery);