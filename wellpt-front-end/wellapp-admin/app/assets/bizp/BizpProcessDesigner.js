define(
		[ "jquery", "jquery-ui", "bootstrap", "server", "commons", "appContext", "Raphael" ],
		function($, ui, bootstrap, server, commons, appContext, Raphael) {
			var StringUtils = commons.StringUtils;
			var NODE_TYPES = {
				PAPER : "0",
				START : "1",
				PROCESS : "2",
				TASK : "3",
				CONNECTION : "4",
				END : "9"
			};
			// 当前绘制的连接
			var currentDrawingConnection = null;
			// 1、业务流程设计器
			var BizpProcessDesigner = function(element, options) {
				this.element = element;
				this.options = options;
				// 元素是否可拖动
				this.draggable = options.draggable;
				// 过程定义信息
				this.processDefinition = options.processDefinition;
				this.nodes = [];
				this.connections = [];
			};
			// 1.1、初始化
			$.extend(BizpProcessDesigner.prototype, {
				init : function() {
					var _self = this;
					var container = _self.options.container;
					// 画纸
					var paper = new Raphael(container, "100%", 650);
					_self.paper = paper;
					_self.parseProcessDefinition();
					// 绑定事件
					_self.bindEvents();
					// 元素移位
					paper.forEach(function(element) {
						// element.transform("t100,0");
					});
				},
				// 过程定义信息
				getProcessDefinition : function() {
					return this.processDefinition;
				},
				// 获取设计器的画纸
				getPaper : function() {
					return this.paper;
				},
				// 获取结点
				getNode : function(id) {
					var _self = this;
					var nodes = _self.nodes;
					for (var i = 0; i < nodes.length; i++) {
						if (id == nodes[i].getId()) {
							return nodes[i];
						}
					}
					return null;
				},
				hasConnection : function(fromID, toID) {
					var _self = this;
					var connections = _self.connections;
					for (var i = 0; i < connections.length; i++) {
						var connection = connections[i];
						if (fromID == connection.fromNode.getId() && toID == connection.toNode.getId()) {
							return true;
						}
					}
					return false;
				},
				// 获取连接
				getConnection : function(id) {
					var _self = this;
					var connections = _self.connections;
					for (var i = 0; i < connections.length; i++) {
						if (id == connections[i].getId()) {
							return connections[i];
						}
					}
					return null;
				},
				// 添加结点
				addNode : function(node) {
					this.nodes.push(node);
				},
				// 添加连接，已不存不添加
				addConnection : function(connection) {
					var _self = this;
					var dirIdMap = {};
					if (connection.getFromNode() != null) {
						$.each(_self.connections, function() {
							var dirId = this.getFromNode().getId() + "_" + this.getToNode().getId();
							dirIdMap[dirId] = dirId;
						});
						var newDirId = connection.getFromNode().getId() + "_" + connection.getToNode().getId();
						if (dirIdMap[newDirId] == null) {
							_self.connections.push(connection);
						} else {
							console.log("Connection[" + newDirId + "] is exists.");
						}
					} else {
						_self.connections.push(connection);
					}
				},
				// 设置当前要绘制的图形
				setDrawingGraphic : function(drawingGraphic) {
					var _self = this;
					var oldDrawingGraphic = _self.drawingGraphic;
					_self.drawingGraphic = drawingGraphic;
					if (_self.drawingGraphic != oldDrawingGraphic) {
						if (currentDrawingConnection && currentDrawingConnection.direction) {
							currentDrawingConnection.direction.remove();
						}
						currentDrawingConnection = null;
					}
				},
				// 获取当前要绘制的图形
				getDrawingGraphic : function() {
					return this.drawingGraphic;
				},
				// 根据绘制的DOM元素，获取结点
				getNodeByDrawElement : function(element) {
					var _self = this;
					var returnNode = null;
					// 获取结点
					$.each(_self.nodes, function(i, node) {
						if (node.getDrawElement().node == element) {
							returnNode = node;
						}
					});
					return returnNode;
				},
				// 根据绘制的DOM元素，删除结点、连接
				deleteByDrawElement : function(element) {
					var _self = this;
					// 删除结点
					$.each(_self.nodes, function(i, node) {
						if (node.getDrawElement().node == element) {
							_self.deleteNode(node);
						}
					});
					// 删除连接
					$.each(_self.connections, function(i, connection) {
						if (connection.getDrawElement().node == element) {
							_self.deleteConnection(connection);
						}
					});
				},
				// 删除结点
				deleteNode : function(node) {
					var _self = this;
					var tmpNodes = [];
					$.each(_self.nodes, function() {
						if (this.getId() != node.getId()) {
							tmpNodes.push(this);
						}
					});
					_self.nodes = tmpNodes;
					// 获取元素相关联的连接
					var nodeConnections = node.getConnections();
					$.each(nodeConnections, function() {
						_self.deleteConnection(this);
					});
					node.remove();
				},
				// 删除连接
				deleteConnection : function(connection) {
					var _self = this;
					var tmpConnections = [];
					var dirId = connection.getFromNode().getId() + "_" + connection.getToNode().getId();
					$.each(_self.connections, function() {
						var tmpDirId = this.getFromNode().getId() + "_" + this.getToNode().getId();
						if (tmpDirId != dirId) {
							tmpConnections.push(this);
						}
					});

					_self.connections = tmpConnections;
					// 元素结点解除连接
					connection.getFromNode().removeConnection(connection);
					connection.getToNode().removeConnection(connection);
					connection.remove();
				},
				// 解析过程定义
				parseProcessDefinition : function() {
					var _self = this;
					var definition = _self.processDefinition;
					// 结点信息
					var nodeDefinitions = definition.nodes;
					$.each(nodeDefinitions, function() {
						var node = _self.parseNode(this);
						if (node != null) {
							_self.addNode(node);
						} else {
							console.error("无法解析结点定义: " + JSON.stringify(this));
						}
					});
					var connectionDefinitions = definition.connections;
					$.each(connectionDefinitions, function() {
						var connection = new Connection(_self, this);
						if (connection != null) {
							_self.addConnection(connection);
						} else {
							console.error("无法解析结点定义: " + JSON.stringify(this));
						}
					});
					// 绘制结点
					$.each(_self.nodes, function(i, node) {
						node.draw();
						node.mousedown();
						node.mouseup();
					});
					// 绘制连接线
					$.each(_self.connections, function(i, connection) {
						connection.draw();
					});
				},
				// 解析结点
				parseNode : function(nodeDefinition) {
					var _self = this;
					if (NODE_TYPES.START == nodeDefinition.type) {
						return new StartNode(_self, nodeDefinition);
					} else if (NODE_TYPES.PROCESS == nodeDefinition.type) {
						return new ProcessNode(_self, nodeDefinition);
					} else if (NODE_TYPES.TASK == nodeDefinition.type) {
						return new TaskNode(_self, nodeDefinition);
					} else if (NODE_TYPES.END == nodeDefinition.type) {
						return new EndNode(_self, nodeDefinition);
					}
				},
				// 收集业务流程定义
				collectProcessDefinition : function() {
					var _self = this;
					var definition = {};
					var nodeDefinitions = [];
					var connectionDefinitions = [];
					// 基本信息
					definition.uuid = _self.processDefinition.uuid;
					definition.name = _self.processDefinition.name;
					definition.id = _self.processDefinition.id;
					definition.code = _self.processDefinition.code;
					definition.category = _self.processDefinition.category;
					definition.isEnabled = _self.processDefinition.isEnabled;
					definition.remark = _self.processDefinition.remark;
					// 结点信息
					$.each(_self.nodes, function() {
						nodeDefinitions.push(this.getDefinitionJson());
					});
					// 连接信息
					$.each(_self.connections, function() {
						connectionDefinitions.push(this.getDefinitionJson());
					});
					definition.nodes = nodeDefinitions;
					definition.connections = connectionDefinitions;
					return definition;
				},
				// 绑定事件
				bindEvents : function() {
					var _self = this;
					var container = _self.options.container;
					var paper = _self.paper;
					// 根据当前要绘制的结点，在点击时生成图形
					$(container).on("click", function(e) {
						// 点击到元素，不生成对应的图形
						if (e.srcElement != paper.canvas) {
							console.log("click element");
							return;
						}
						var currentGraphic = _self.drawingGraphic;
						if (StringUtils.isBlank(currentGraphic)) {
							return;
						}
						if (NODE_TYPES.START == currentGraphic) {
							return _self.drawStartNode(e);
						} else if (NODE_TYPES.PROCESS == currentGraphic) {
							return _self.drawProcessNode(e);
						} else if (NODE_TYPES.TASK == currentGraphic) {
							return _self.drawTaskNode(e);
						} else if (NODE_TYPES.END == currentGraphic) {
							return _self.drawEndNode(e);
						}
					});
					// 绘制箭头时的鼠标跟随操作
					$(container).on("mousemove", function(e) {
						if (currentDrawingConnection != null) {
							var movingNode = currentDrawingConnection.movingNode;
							if (movingNode == null) {
								movingNode = paper.circle(e.offsetX, e.offsetY, 1);
								movingNode.toBack();
								movingNode.attr("stroke-opacity", "0");
								currentDrawingConnection.movingNode = movingNode;
							}
							currentDrawingConnection.movingNode.attr({
								"cx" : e.offsetX,
								"cy" : e.offsetY
							});
							var fromNode = currentDrawingConnection.fromNode;
							var obj1 = fromNode.getDrawElement();
							var point = getStartEndPoint(obj1, movingNode);
							var arrowPath = getArrow(point, 8);
							if (currentDrawingConnection.direction) {
								currentDrawingConnection.direction.attr({
									path : arrowPath
								});
							} else {
								currentDrawingConnection.direction = paper.path(arrowPath);
							}
						}
					});
					var dblclick = _self.options.dblclick;
					// 双击
					$(container).on("dblclick", function(e) {
						var element = e.srcElement;
						// 双击页面
						if (paper.canvas == element) {
							console.log("双击页面");
							if ($.isFunction(dblclick)) {
								dblclick.call(_self, NODE_TYPES.PAPER, paper, e);
							}
						} else {
							// 双击结点
							$.each(_self.nodes, function(i, node) {
								if (node.getDrawElement().node == element) {
									console.log("双击结点");
									if ($.isFunction(dblclick)) {
										dblclick.call(_self, node.getType(), node, e);
									}
								}
							});
							// 双击连接
							$.each(_self.connections, function(i, connection) {
								if (connection.getDrawElement().node == element) {
									console.log("双击连接");
									if ($.isFunction(dblclick)) {
										dblclick.call(_self, NODE_TYPES.CONNECTION, connection, e);
									}
								}
							});
						}
					});
				},
				// 绘制开始结点
				drawStartNode : function(e) {
					var _self = this;
					var nodeDefinition = {
						name : "开始",
						type : NODE_TYPES.START,
						x : e.offsetX,
						y : e.offsetY,
						r : 20
					};
					var startNode = new StartNode(_self, nodeDefinition);
					_self.nodes.push(startNode);
					startNode.draw();
					startNode.mousedown();
					startNode.mouseup();
				},
				// 绘制过程结点
				drawProcessNode : function(e) {
					var _self = this;
					var nodeDefinition = {
						name : "过程",
						type : NODE_TYPES.PROCESS,
						x : e.offsetX - 100 / 2,
						y : e.offsetY - 50 / 2,
						width : 100,
						height : 50
					};
					var processNode = new ProcessNode(_self, nodeDefinition);
					_self.nodes.push(processNode);
					processNode.draw();
					processNode.mousedown();
					processNode.mouseup();
				},
				// 绘制任务结点
				drawTaskNode : function(e) {
					var _self = this;
					var nodeDefinition = {
						name : "任务",
						type : NODE_TYPES.TASK,
						x : e.offsetX - 100 / 2,
						y : e.offsetY - 50 / 2,
						width : 100,
						height : 50
					};
					var taskNode = new TaskNode(_self, nodeDefinition);
					_self.nodes.push(taskNode);
					taskNode.draw();
					taskNode.mousedown();
					taskNode.mouseup();
				},
				// 绘制结束结点
				drawEndNode : function(e) {
					var _self = this;
					var nodeDefinition = {
						name : "结束",
						type : NODE_TYPES.END,
						x : e.offsetX,
						y : e.offsetY,
						r : 20
					};
					var endNode = new EndNode(_self, nodeDefinition);
					_self.nodes.push(endNode);
					endNode.draw();
					endNode.mousedown();
					endNode.mouseup();
				}
			});

			// 元素定义
			var Element = function(designer, nodeDefinition) {
				this.designer = designer;
				this.definition = nodeDefinition;
				this.connections = [];
			};
			$.extend(Element.prototype, {
				// 获取元素ID
				getId : function() {
					return this.definition.id || this.getDrawElement().id;
				},
				getType : function() {
					return this.definition.type;
				},
				getAvailableToTypes : function() {
					return {
						"2" : NODE_TYPES.PROCESS
					};
				},
				// 绘制元素
				draw : function() {
				},
				// 元素可拖动
				drag : function() {
				},
				// 鼠标按下操作
				mousedown : function() {
					var _self = this;
					_self.getDrawElement().mousedown(function() {
						// 当前绘制键头时，定位箭头开始信息
						if (NODE_TYPES.CONNECTION != _self.designer.getDrawingGraphic()) {
							return;
						}
						// 开始结点只能有一个箭头出去
						if (NODE_TYPES.START == _self.getType() && _self.connections.length > 0) {
							return;
						}
						// 结束结点，没有箭头开始
						if (NODE_TYPES.END == _self.getType()) {
							return;
						}
						if (currentDrawingConnection == null) {
							currentDrawingConnection = {
								fromNode : _self
							};
						} else if (currentDrawingConnection.fromNode != _self) {
							currentDrawingConnection.toNode = _self;
						}
					});
				},
				// 鼠标抬起操作
				mouseup : function() {
					var _self = this;
					_self.getDrawElement().mouseup(function() {
						// 当前绘制键头时，连接箭头的两个结点
						if (NODE_TYPES.CONNECTION != _self.designer.getDrawingGraphic()) {
							return;
						}
						if (currentDrawingConnection == null) {
							return;
						}
						if (currentDrawingConnection.direction == null) {
							return;
						}
						var fromNode = currentDrawingConnection.fromNode;
						if (fromNode == _self) {
							return;
						}
						// 可取可连接结的结点类型
						var vailableToTypes = fromNode.getAvailableToTypes();
						if (vailableToTypes[_self.getType()] == null) {
							return;
						}
						var connectionDefinition = {
							name : "连接",
							fromID : fromNode.getId(),
							toID : _self.getId()
						};
						// 已存在连接，直接返回
						if (_self.designer.hasConnection(connectionDefinition.fromID, connectionDefinition.toID)) {
							return;
						}
						currentDrawingConnection.movingNode.remove();
						currentDrawingConnection.direction.remove();
						currentDrawingConnection = null;

						var connection = new Connection(_self.designer, connectionDefinition);
						connection.draw();
						_self.designer.addConnection(connection);
					});
				},
				// 获取绘制的元素
				getDrawElement : function() {
				},
				// 添加元素相关联的连接
				addConnection : function(connection) {
					this.connections.push(connection);
				},
				// 删除元素相关联的连接
				removeConnection : function(connection) {
					var _self = this;
					var tmpConnections = [];
					$.each(_self.connections, function() {
						if (this != connection) {
							tmpConnections.push(this);
						}
					});
					_self.connections = tmpConnections;
				},
				// 获取元素相关联的连接
				getConnections : function() {
					return this.connections;
				},
				// 获取元素JSON定义信息
				getDefinitionJson : function() {
				},
				// 更新元素JSON定义信息
				updateDefinitionJson : function(definitionJson) {
				},
				// 删除元素
				remove : function() {
					// 删除文本信息
					if (this.text) {
						this.text.remove();
					}
					this.getDrawElement().remove();
				}
			});
			// 开始结点
			var StartNode = function() {
				Element.apply(this, arguments);
			};
			commons.inherit(StartNode, Element, {
				draw : function() {
					var _self = this;
					var paper = _self.designer.getPaper();
					var definition = _self.definition;
					var x = definition.x;
					var y = definition.y;
					var r = definition.r;
					var text = paper.text(x, y, definition.name);
					var circle = paper.circle(x, y, r);
					_self.circle = circle;
					_self.text = text;

					// 填充后才可拖动
					circle.attr("fill", "#F8F8F8");
					circle.attr("fill-opacity", "0");
					// 拖动事件
					var dragging = function(dx, dy) {
						var attr = {
							cx : this.ox + dx,
							cy : this.oy + dy
						};
						this.attr(attr);
						_self.text.attr({
							x : attr.cx,
							y : attr.cy
						});
						// 获取结点相关联的连接并重新绘制
						var connections = _self.getConnections();
						$.each(connections, function() {
							this.draw();
						});
					};
					// 拖动节点开始时的事件
					var dragStart = function() {
						this.ox = this.attr("cx");
						this.oy = this.attr("cy");
						this.animate({
							"fill-opacity" : .2
						}, 500);
					};
					// 拖动结束后的事件
					var dragEnd = function() {
						this.animate({
							"fill-opacity" : 0
						}, 500);
					};
					// 可拖动
					if (_self.designer.draggable != false) {
						circle.drag(dragging, dragStart, dragEnd);
					}
					// circle.dblclick(function() {
					// appModal.info("dblclick")
					// });
				},
				getDefinitionJson : function() {
					var _self = this;
					var drawElm = _self.getDrawElement();
					var definition = {
						name : _self.definition.name,
						type : _self.definition.type,
						id : _self.getId(),
						x : drawElm.attr("cx"),
						y : drawElm.attr("cy"),
						r : _self.definition.r
					};
					return definition;
				},
				getDrawElement : function() {
					return this.circle;
				}
			});
			// 过程结点
			var ProcessNode = function() {
				Element.apply(this, arguments);
			};
			commons.inherit(ProcessNode, Element, {
				getAvailableToTypes : function() {
					return {
						"2" : NODE_TYPES.PROCESS,
						"3" : NODE_TYPES.TASK,
						"9" : NODE_TYPES.END
					};
				},
				draw : function() {
					var _self = this;
					var paper = _self.designer.getPaper();
					var definition = _self.definition;
					var x = definition.x;
					var y = definition.y;
					var width = definition.width;
					var height = definition.height;
					var text = paper.text(x + width / 2, y + height / 2, definition.name);
					var rect = paper.rect(x, y, width, height);
					_self.text = text;
					_self.rect = rect;

					// 填充后才可拖动
					rect.attr("fill", "#F8F8F8");
					rect.attr("fill-opacity", "0");
					// 拖动事件
					var dragging = function(dx, dy) {
						var attr = {
							x : this.ox + dx,
							y : this.oy + dy
						};
						this.attr(attr);
						_self.text.attr({
							x : attr.x + width / 2,
							y : attr.y + height / 2
						});
						// 获取结点相关联的连接并重新绘制
						var connections = _self.getConnections();
						$.each(connections, function() {
							this.draw();
						});
					};
					// 拖动节点开始时的事件
					var dragStart = function() {
						this.ox = this.attr("x");
						this.oy = this.attr("y");
						this.animate({
							"fill-opacity" : .2
						}, 500);
					};
					// 拖动结束后的事件
					var dragEnd = function() {
						this.animate({
							"fill-opacity" : 0
						}, 500);
					};
					// 可拖动
					if (_self.designer.draggable != false) {
						rect.drag(dragging, dragStart, dragEnd);
					}
					// rect.dblclick(function() {
					// appModal.info("dblclick")
					// });
				},
				getDefinitionJson : function() {
					var _self = this;
					var drawElm = _self.getDrawElement();
					var definition = {
						name : _self.definition.name,
						type : _self.definition.type,
						id : _self.getId(),
						code : _self.definition.code,
						timeLimitType : _self.definition.timeLimitType,
						timeLimit : _self.definition.timeLimit,
						majorOrgNames : _self.definition.majorOrgNames,
						majorOrgIds : _self.definition.majorOrgIds,
						minorOrgNames : _self.definition.minorOrgNames,
						minorOrgIds : _self.definition.minorOrgIds,
						superviseOrgNames : _self.definition.superviseOrgNames,
						superviseOrgIds : _self.definition.superviseOrgIds,
						x : drawElm.attr("x"),
						y : drawElm.attr("y"),
						width : drawElm.attr("width"),
						height : drawElm.attr("height")
					};
					return definition;
				},
				updateDefinitionJson : function(definitionJson) {
					var _self = this;
					_self.definition.name = definitionJson.name;
					_self.definition.code = definitionJson.code;
					_self.definition.timeLimitType = definitionJson.timeLimitType;
					_self.definition.timeLimit = definitionJson.timeLimit;
					_self.definition.majorOrgNames = definitionJson.majorOrgNames;
					_self.definition.majorOrgIds = definitionJson.majorOrgIds;
					_self.definition.minorOrgNames = definitionJson.minorOrgNames;
					_self.definition.minorOrgIds = definitionJson.minorOrgIds;
					_self.definition.superviseOrgNames = definitionJson.superviseOrgNames;
					_self.definition.superviseOrgIds = definitionJson.superviseOrgIds;
					// 更新文本内容
					_self.text.attr({
						"text" : _self.definition.name
					});
				},
				getDrawElement : function() {
					return this.rect;
				}
			});
			// 任务结点
			var TaskNode = function() {
				Element.apply(this, arguments);
			};
			commons.inherit(TaskNode, ProcessNode, {
				getAvailableToTypes : function() {
					return {
						"3" : NODE_TYPES.TASK
					};
				},
				getDefinitionJson : function() {
					var _self = this;
					var drawElm = _self.getDrawElement();
					var definition = {
						name : _self.definition.name,
						type : _self.definition.type,
						id : _self.getId(),
						code : _self.definition.code,
						timeLimitType : _self.definition.timeLimitType,
						timeLimit : _self.definition.timeLimit,
						frontTaskNames : _self.definition.frontTaskNames,
						frontTaskIds : _self.definition.frontTaskIds,
						milestone : _self.definition.milestone,
						x : drawElm.attr("x"),
						y : drawElm.attr("y"),
						width : drawElm.attr("width"),
						height : drawElm.attr("height")
					};
					return definition;
				},
				updateDefinitionJson : function(definitionJson) {
					var _self = this;
					_self.definition.name = definitionJson.name;
					_self.definition.code = definitionJson.code;
					_self.definition.timeLimitType = definitionJson.timeLimitType;
					_self.definition.timeLimit = definitionJson.timeLimit;
					_self.definition.frontTaskNames = definitionJson.frontTaskNames;
					_self.definition.frontTaskIds = definitionJson.frontTaskIds;
					_self.definition.milestone = definitionJson.milestone;
					// 更新文本内容
					_self.text.attr({
						"text" : _self.definition.name
					});
				}
			});
			// 结束结点
			var EndNode = function() {
				Element.apply(this, arguments);
			};
			commons.inherit(EndNode, StartNode, {
				getAvailableToTypes : function() {
					return {};
				},
				draw : function() {
					var _self = this;
					// 调用父类提交方法
					_self._superApply(arguments);
				}
			});
			// 连接
			var Connection = function() {
				Element.apply(this, arguments);
			};
			commons.inherit(Connection, Element, {
				draw : function() {
					var _self = this;
					var paper = _self.designer.getPaper();
					var definition = _self.definition;
					var fromID = definition.fromID;
					var toID = definition.toID;
					var fromNode = _self.designer.getNode(fromID);
					var toNode = _self.designer.getNode(toID);
					var obj1 = fromNode.getDrawElement();
					var obj2 = toNode.getDrawElement();
					var point = getStartEndPoint(obj1, obj2);
					var arrowPath = getArrow(point, 8);
					if (_self.direction) {
						_self.direction.attr({
							path : arrowPath
						});
					} else {
						_self.direction = paper.path(arrowPath);
						// _self.direction.click(function() {
						// });
						fromNode.addConnection(this);
						toNode.addConnection(this);
					}
					_self.fromNode = fromNode;
					_self.toNode = toNode;
				},
				// 获取绘制的元素
				getDrawElement : function() {
					return this.direction;
				},
				getDefinitionJson : function() {
					var _self = this;
					var definition = {
						name : _self.definition.name,
						id : _self.getId(),
						fromID : _self.fromNode.getId(),
						toID : _self.toNode.getId(),
					};
					return definition;
				},
				updateDefinitionJson : function(definitionJson) {
					this.definition.name = definitionJson.name;
				},
				getFromNode : function() {
					return this.fromNode;
				},
				getToNode : function() {
					return this.toNode;
				}
			});

			function getStartEndPoint(obj1, obj2) {
				var bb1 = obj1.getBBox(), bb2 = obj2.getBBox();
				var p = [ {
					x : bb1.x + bb1.width / 2,
					y : bb1.y - 1
				}, {
					x : bb1.x + bb1.width / 2,
					y : bb1.y + bb1.height + 1
				}, {
					x : bb1.x - 1,
					y : bb1.y + bb1.height / 2
				}, {
					x : bb1.x + bb1.width + 1,
					y : bb1.y + bb1.height / 2
				}, {
					x : bb2.x + bb2.width / 2,
					y : bb2.y - 1
				}, {
					x : bb2.x + bb2.width / 2,
					y : bb2.y + bb2.height + 1
				}, {
					x : bb2.x - 1,
					y : bb2.y + bb2.height / 2
				}, {
					x : bb2.x + bb2.width + 1,
					y : bb2.y + bb2.height / 2
				} ];
				var d = {}, dis = [];
				for (var i = 0; i < 4; i++) {
					for (var j = 4; j < 8; j++) {
						var dx = Math.abs(p[i].x - p[j].x), dy = Math.abs(p[i].y - p[j].y);
						if ((i == j - 4)
								|| (((i != 3 && j != 6) || p[i].x < p[j].x) && ((i != 2 && j != 7) || p[i].x > p[j].x)
										&& ((i != 0 && j != 5) || p[i].y > p[j].y) && ((i != 1 && j != 4) || p[i].y < p[j].y))) {
							dis.push(dx + dy);
							d[dis[dis.length - 1]] = [ i, j ];
						}
					}
				}
				if (dis.length == 0) {
					var res = [ 0, 4 ];
				} else {
					res = d[Math.min.apply(Math, dis)];
				}
				var result = {};
				result.start = {};
				result.end = {};
				result.start.x = p[res[0]].x;
				result.start.y = p[res[0]].y;
				result.end.x = p[res[1]].x;
				result.end.y = p[res[1]].y;
				return result;
			}
			// 获取组成箭头的三条线段的路径
			function getArrow(point, size) {
				var x1 = point.start.x;
				var y1 = point.start.y;
				var x2 = point.end.x;
				var y2 = point.end.y;
				// 得到两点之间的角度
				var angle = Raphael.angle(x1, y1, x2, y2);
				// 角度转换成弧度
				var a45 = Raphael.rad(angle - 45);
				var a45m = Raphael.rad(angle + 45);
				var x2a = x2 + Math.cos(a45) * size;
				var y2a = y2 + Math.sin(a45) * size;
				var x2b = x2 + Math.cos(a45m) * size;
				var y2b = y2 + Math.sin(a45m) * size;
				var result = [ "M", x1, y1, "L", x2, y2, "L", x2a, y2a, "M", x2, y2, "L", x2b, y2b ];
				return result;
			}
			return BizpProcessDesigner;
		});
