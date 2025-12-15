import { deepClone, generateId } from '../utils/util';
import { sortBy, isEmpty } from 'lodash';

/**
 * 设计器
 * @param {vue 实例}} vueInstance
 * @returns
 */
export function createDesigner(vueInstance) {
  const themeSpecifyColorConfig = {};
  if (EASY_ENV_IS_BROWSER) {
    // 拉取主题配色配置
    $axios
      .get(`/proxy/api/theme/specify/getEnabled`, { params: {} })
      .then(({ data }) => {
        // console.log('获取主题规范', data);
        if (data.code == 0) {
          let specifyDefJson = JSON.parse(data.data.defJson);
          for (let key in specifyDefJson.colorConfig) {
            themeSpecifyColorConfig[key] = specifyDefJson.colorConfig[key];
          }
        }
      })
      .catch(error => {});
  }

  return {
    vueInstance: vueInstance,
    widgets: [], // 拖拽构建的组件集合
    widgetTree: [],
    widgetTreeMap: {}, // 组件树节点Map
    widgetIdMap: {}, // 组件ID map，快速找到对应的组件
    widgetsOfParent: {}, // 组件ID map， 快速找到对应父节点的所有子组件
    widgetDefaultEvents: {}, // 组件默认事件
    selectedId: null,
    selectedWidget: null,
    selectedIdHistory: [],
    cuttingId: null,
    secondarySelectedId: null,
    secondarySelectedWidget: null, // 二级组件: 用于二级页类组件，比如浮层组件、表单数据管理类设计组件等等
    selectedDrawerWidget: null, // 覆盖组件：用于二级浮层组件
    focusedId: null,
    focusedWidget: null,
    selectedWidgetName: null,
    errorWidgetIds: [],
    parentOfSelectedWidget: null,
    widgetJsonDrawerVisible: false,
    currentCanDragGroup: 'dragGroup',
    terminalType: 'pc',
    isUniAppDesign: false,
    cutWidgetInfo: {
      widget: null,
      widgetsOfParent: []
    },
    dragoverWidgetId: null,
    dragging: false,
    draggingWidgetId: null,
    dragoverSide: false,
    draggedWidget: null,
    unselectableWidgetIds: [],
    languageOptions: [],
    buildHistory: {
      canUndo: false,
      canRedo: false,
      index: -1, //index: 0,
      maxStep: 20,
      steps: [],
      initIndex: -1
    },
    undoOrRedoLoading: false,
    isUndoClick: false,
    isRedoClick: false,
    widgetSelectDrawerVisible: false,
    widgetTreeVisible: false,
    functionElements: {}, //功能元素集合
    pageVars: {}, // 页面变量
    pageJsModule: undefined, // 页面脚本
    themeSpecifyColorConfig,
    copyNewWidget(origin) {
      let newWidget = deepClone(origin);
      // 处理同一组件的不同实例化：比如输入框组件是基本组件，可以实现为密码输入、数字输入组件，但是基本组件代码同一份
      // 可以通过定义组件类型格式为：[组件类型$子类型] 的方式进行不同实例化
      let splits = newWidget.wtype.split('$');
      if (splits.length == 2) {
        newWidget.wtype = splits[0];
        newWidget.subtype = splits[1];
      }
      newWidget.id = generateId();
      newWidget.title = origin.name; //初始标题为组件名称
      // 删除无用的属性
      delete newWidget.thumbnail;
      delete newWidget.description;
      delete newWidget.iconClass;
      delete newWidget.icon;
      return newWidget;
    },

    emitEvent() {
      //用于兄弟组件发射事件
      if (this.vueInstance) this.vueInstance.$emit.apply(this.vueInstance, arguments);
    },

    handleEvent(evtName, callback) {
      //用于兄弟组件接收事件
      if (this.vueInstance)
        this.vueInstance.$on(evtName, function () {
          callback.apply(this, arguments);
        });
    },

    offEvent(evtName) {
      if (this.vueInstance) this.vueInstance.$off(evtName);
      return this;
    },

    widgetMoveUp(parentList, indexOfParentList, parent) {
      if (!!parentList) {
        this.undoOrRedo = true;
        let tempWidget = parentList[indexOfParentList];
        parentList.splice(indexOfParentList, 1);
        parentList.splice(indexOfParentList - 1, 0, tempWidget);
        for (let i = 0, len = parentList.length; i < len; i++) {
          this.widgetTreeMap[parentList[i].id].index = i;
        }
      }
    },

    widgetMoveDown(parentList, indexOfParentList, parent) {
      if (!!parentList) {
        this.undoOrRedo = true;
        let tempWidget = parentList[indexOfParentList];
        parentList.splice(indexOfParentList, 1);
        parentList.splice(indexOfParentList + 1, 0, tempWidget);
        for (let i = 0, len = parentList.length; i < len; i++) {
          this.widgetTreeMap[parentList[i].id].index = i;
        }
      }
    },
    addToSelectedWidget(wgt) {
      let newWidget = deepClone(wgt);
      newWidget.id = generateId();
      if (this.selectedWidget) {
        if (!this.selectedWidget.configuration.widgets) {
          this.selectedWidget.configuration.widgets = [];
        }
        this.selectedWidget.configuration.widgets.push(newWidget);
      }
    },

    selectedByID(id) {
      this.setSelected(this.widgetIdMap[id]);
    },

    setSelected(selected, parent) {
      if (!selected) {
        this.clearSelected();
        return;
      }
      // if (selected.level === 2) {
      //   // 二级页组件选择
      //   this.secondarySelectedWidget = selected;
      //   this.secondarySelectedId = selected.id;
      //   return;
      // }
      this.selectedWidget = selected;
      this.parentOfSelectedWidget = parent;
      if (!!selected.id) {
        if (this.selectedId !== selected.id) {
          this.selectedIdHistory.push(selected.id);
        }
        this.selectedId = selected.id;
        this.selectedWidgetName = selected.name;
      }
    },

    toTree(widget, widgetChildren, parent, index) {
      let node = null;
      if (this.widgetTreeMap[widget.id]) {
        node = this.widgetTreeMap[widget.id];
      } else {
        node = {
          key: widget.id,
          title: widget.title,
          wtype: widget.wtype,
          category: widget.category,
          index: undefined
        };
        this.vueInstance.$set(this.widgetTreeMap, node.key, node);
      }
      if (parent != undefined) {
        this.vueInstance.$set(node, 'parentKey', parent.id);
      } else {
        this.vueInstance.$delete(node, 'parentKey');
      }
      if (index != undefined) {
        node.index = index;
      }
      if (widgetChildren != undefined) {
        for (let k in this.widgetTreeMap) {
          if (this.widgetTreeMap[k].parentKey == node.key) {
            delete this.widgetTreeMap[k];
          }
        }
        for (let i = 0, len = widgetChildren.length; i < len; i++) {
          let n = widgetChildren[i];
          if (this.widgetIdMap[n.id] == undefined) {
            this.widgetIdMap[n.id] = n;
          }
          let child = {
            key: n.id,
            title: n.title,
            wtype: n.wtype,
            index: i,
            category: n.category,
            parentKey: node.key
          };
          this.vueInstance.$set(this.widgetTreeMap, child.key, child);
        }
      }
    },

    getWidgetTree() {
      let widgetTree = [];
      const v = this.widgetTreeMap;
      // 重新构建树型数据
      let map = {};
      for (let k in v) {
        let node = {
          key: k,
          title: v[k].title,
          index: v[k].index || 0,
          wtype: v[k].wtype
        };
        map[k] = node;
        if (v[k].parentKey == undefined) {
          // 一级节点
          widgetTree.push(node);
        }
      }
      widgetTree = sortBy(widgetTree, a => {
        return a.index;
      });
      for (let k in v) {
        if (v[k].parentKey && map[v[k].parentKey]) {
          let parent = map[v[k].parentKey];
          if (parent.children == undefined) {
            parent.children = [];
          }
          parent.children.push(map[k]);
          parent.children = sortBy(parent.children, a => {
            return a.index;
          });
        }
      }
      return widgetTree;
    },

    deleteWidget(widgetId, cancelUndoOrRedo) {
      if (!cancelUndoOrRedo) {
        this.undoOrRedo = true;
      }
      this.vueInstance.$delete(this.widgetIdMap, widgetId);
      this.vueInstance.$delete(this.widgetTreeMap, widgetId);

      this.deleteChildren(widgetId);
    },

    deleteChildren(widgetId) {
      for (let wid in this.widgetTreeMap) {
        let child = this.widgetTreeMap[wid];
        if (child.parentKey == widgetId) {
          this.vueInstance.$delete(this.widgetIdMap, child.key);
          this.vueInstance.$delete(this.widgetTreeMap, child.key);

          this.deleteChildren(child.key);
        }
      }
    },

    setFocused(focused) {
      if (!focused) {
        this.focusedWidget = {};
        this.focusedId = null;
        return;
      }
      this.focusedWidget = focused;
      this.focusedId = focused.id;
    },

    clearSelected() {
      this.secondarySelectedId = null;
      this.secondarySelectedWidget = null;
      this.selectedId = null;
      this.selectedWidgetName = null;
      this.selectedWidget = {};
      this.parentOfSelectedWidget = {};
    },

    clear() {
      this.undoOrRedo = true;
      this.clearSelected();
      this.widgets = [];
      this.widgetTree = [];
      this.widgetTreeMap = {};
      this.widgetIdMap = {};
      this.widgetsOfParent = {};
    },
    // 添加历史记录，超过最大值时，移除第一项记录
    emitHistoryChange() {
      if (this.buildHistory.index === this.buildHistory.maxStep - 1) {
        this.buildHistory.steps.shift();
      } else {
        this.buildHistory.index++;
      }
      // 初始会有一个历史记录，所以有一个历史记录时不可进行撤回操作
      if (this.buildHistory.index >= 1) {
        this.buildHistory.canUndo = true;
      } else {
        this.buildHistory.canUndo = false;
      }

      this.buildHistory.steps[this.buildHistory.index] = {
        widgets: deepClone(this.widgets),
        widgetTreeMap: deepClone(this.widgetTreeMap),
        FieldWidgets: deepClone(this.FieldWidgets),
        SimpleFieldInfos: deepClone(this.SimpleFieldInfos),
        widgetIdMap: deepClone(this.widgetIdMap)
      };

      if (this.isUndoClick || this.isRedoClick) {
        this.isUndoClick = false;
        this.isRedoClick = false;
      } else if (this.buildHistory.index < this.buildHistory.steps.length - 1) {
        this.buildHistory.steps = this.buildHistory.steps.slice(0, this.buildHistory.index + 1);
      }
    },

    undo() {
      if (this.buildHistory.index < 1) {
        return;
      }
      this.undoOrRedoLoading = true;
      this.undoOrRedo = true;
      this.buildHistory.canRedo = true; // 做过撤销动作，则允许恢复
      let index = this.buildHistory.index;
      index--;
      if (this.buildHistory.index <= 0) {
        this.buildHistory.canUndo = false; // 已经撤销到第一步，则无法再撤销了
        if (isEmpty(this.widgets)) {
          this.clear();
          setTimeout(() => {
            this.undoOrRedoLoading = false;
          }, 500);
          return;
        }
      }
      // emitHistory会加1
      this.buildHistory.index = this.buildHistory.index - 2;
      this.isUndoClick = true;
      this.setBuildHistoryData(index);
      setTimeout(() => {
        this.undoOrRedoLoading = false;
      }, 500);
    },
    redo() {
      this.undoOrRedoLoading = true;
      this.undoOrRedo = true;
      this.buildHistory.canUndo = true;
      let index = this.buildHistory.index;
      if (this.buildHistory.index !== this.buildHistory.steps.length - 1) {
        index++;
        if (this.buildHistory.index == this.buildHistory.steps.length - 2) {
          this.buildHistory.canRedo = false; // 已经恢复到最后一步，则无法恢复
        }
      } else {
        this.buildHistory.canRedo = false; // 已经恢复到最后一步，则无法恢复
      }
      this.isRedoClick = true;
      this.setBuildHistoryData(index);
      setTimeout(() => {
        this.undoOrRedoLoading = false;
      }, 500);
    },
    setBuildHistoryData(index) {
      this.widgets = deepClone(this.buildHistory.steps[index].widgets);
      this.widgetTreeMap = deepClone(this.buildHistory.steps[index].widgetTreeMap);
      this.FieldWidgets = deepClone(this.buildHistory.steps[index].FieldWidgets);
      this.SimpleFieldInfos = deepClone(this.buildHistory.steps[index].SimpleFieldInfos);
      this.widgetIdMap = deepClone(this.buildHistory.steps[index].widgetIdMap);
    },

    addFunctionElements(wgtId, func) {
      if (!this.functionElements.hasOwnProperty(wgtId)) {
        this.functionElements[wgtId] = [];
      }
      this.functionElements[wgtId].push(func);
    },

    deleteFunctionElement(wgtId, func) {
      if (!this.functionElements.hasOwnProperty(wgtId)) {
      }
    },

    cascadeObjectKeyPath(obj, keyPaths, parentKeyPath) {
      for (let k in obj) {
        keyPaths.push(parentKeyPath ? `${parentKeyPath}.${k}` : k);
        if (['string', 'number', 'boolean'].includes(typeof obj[k])) {
          continue;
        }
        if (!Array.isArray(obj[k])) {
          // 非数组对象则继续遍历路径
          this.cascadeObjectKeyPath(obj[k], keyPaths, parentKeyPath ? `${parentKeyPath}.${k}` : k);
        }
      }
    },

    // 获取页面变量路径组
    pageVarKeyPaths() {
      let keyPaths = [];
      this.cascadeObjectKeyPath(this.pageVars, keyPaths);
      return keyPaths;
    },
    // 判断设计区内是否存在id：widgetId的组件，没有就删除它
    isExistWidget(widgetId) {
      const jsonString = JSON.stringify(this.widgets);
      let hasExist = new RegExp(`"id":"${widgetId.replace(/"/g, '\\"')}"(,|}|\\])`).test(jsonString);
      if (!hasExist) {
        this.deleteWidget(widgetId);
      }
      return hasExist;
    },

    getWidgetElements(extraWidgets) {
      let functionElements = {};
      let appWidgetDefinitionElements = [];
      if (this.widgets.length != 0 || (extraWidgets != undefined && extraWidgets.length > 0)) {
        //解析各个组件配置的功能元素、以及生成后端保存的定义信息
        let convertWidget = wgt => {
          if (wgt) {
            let type = `${wgt.wtype}Configuration`;
            if (window.Vue.options.components[type] && window.Vue.options.components[type].options.methods.getFunctionElements) {
              Object.assign(functionElements, window.Vue.options.components[type].options.methods.getFunctionElements(wgt));
            }
            if (window.Vue.options.components[type] && window.Vue.options.components[type].options.methods.getWidgetDefinitionElements) {
              appWidgetDefinitionElements = appWidgetDefinitionElements.concat(
                window.Vue.options.components[type].options.methods.getWidgetDefinitionElements(wgt)
              );
            } else {
              appWidgetDefinitionElements.push({
                wtype: wgt.wtype,
                title: wgt.title,
                id: wgt.id,
                definitionJson: JSON.stringify(wgt)
              });
            }

            let i18nMap = this.getWidgetI18nObjects(wgt),
              rows = [];
            for (let wgtId in i18nMap) {
              let i18n = i18nMap[wgtId];
              for (let i = 0, len = i18n.length; i < len; i++) {
                for (let opt of this.languageOptions) {
                  if (i18n[i][opt.value]) {
                    for (let code in i18n[i][opt.value]) {
                      rows.push({ elementId: wgtId, code, locale: opt.value, content: i18n[i][opt.value][code] });
                    }
                  }
                }
              }
            }

            appWidgetDefinitionElements[appWidgetDefinitionElements.length - 1].i18ns = rows;
          }
        };
        for (let id in this.widgetIdMap) {
          convertWidget(this.widgetIdMap[id]);
        }
        if (extraWidgets != undefined && extraWidgets.length > 0) {
          for (let wgt of extraWidgets) {
            convertWidget(wgt);
          }
        }

        // 剔除作为功能元素存在的组件定义元素（比如标签页组件的页签不需要同步为组件定义元素）
        let wgIds = Object.keys(this.widgetIdMap);
        for (let wid in functionElements) {
          let elements = functionElements[wid];
          for (let e of elements) {
            if (wgIds.includes(e.id)) {
              for (let i = 0, len = appWidgetDefinitionElements.length; i < len; i++) {
                if (appWidgetDefinitionElements[i].id == e.id) {
                  appWidgetDefinitionElements.splice(i, 1);
                  break;
                }
              }
            }
          }
        }
      }

      return { functionElements, appWidgetDefinitionElements };
    },

    getWidgetI18nObjects(widget) {
      let _this = this;
      function findI18nObjects(json, i18n, nestedWidget) {
        function traverse(obj, i18n, nestedWidget) {
          if (typeof obj !== 'object' || obj === null) {
            return;
          }
          if (obj.wtype && obj.id && obj.configuration && _this.widgetIdMap[obj.id] == undefined) {
            traverse(obj.configuration, i18n, obj);
            return;
          }
          if (Array.isArray(obj)) {
            for (let item of obj) {
              traverse(item, i18n);
            }
          } else {
            for (let key in obj) {
              if (key === 'i18n' && obj[key] != undefined && obj[key].zh_CN != undefined) {
                let targetWgt = nestedWidget || widget;
                if (i18n[targetWgt.id] == undefined) {
                  i18n[targetWgt.id] = [];
                }
                i18n[targetWgt.id].push(obj[key]);
                continue;
              }
              traverse(obj[key], i18n, nestedWidget);
            }
          }
        }
        traverse(json, i18n, nestedWidget);
      }
      const i18n = {};
      findI18nObjects(widget.configuration, i18n);
      return i18n;
    }
  };
}
