export default {
  methods: {
    getWidgetActionElements(wgt, designer) {
      let actionElements = [];
      actionElements.push(...this.resolveDefineEventToActionElement(wgt, designer));
      actionElements.push(...this.resolveDomEventToActionElement(wgt, designer));
      if (wgt.configuration.jsModules) {
        actionElements.push(...this.resolveJsModuleAsActionElement(wgt.configuration.jsModules, wgt, designer));
      }
      return actionElements;
    },
    resolveDomEventToActionElement(wgt, designer) {
      let domEvents = wgt.configuration.domEvents;
      let actionElements = [];
      if (domEvents) {
        for (let d of domEvents) {
          let codeSource = d.codeSource;
          if (codeSource == 'widgetEvent') {
            let widgetEvent = d.widgetEvent;
            for (let w of widgetEvent) {
              let act = {
                actionType: 'widgetEvent', // 动作类型
                actionSource: w, // 动作事件源
                triggerName: d.id == 'onChange' ? '值变更时候触发' : '点击触发',
                actionObject: {
                  // 执行对象
                  type: 'widgetEvent',
                  name: undefined,
                  sourcePath: 'eventWid' // 对象路径，用于从事件源解析对象值，可以在管理端同步修改对应值
                }
              };

              let wevents = designer.widgetDefaultEvents[w.eventWid];
              for (let i = 0, len = wevents.length; i < len; i++) {
                if (wevents[i].id == w.eventId) {
                  act.actionTypeName = wevents[i].title;
                  break;
                }
              }
              actionElements.push(act);
            }
          } else if (codeSource == 'codeEditor') {
            let act = {
              actionType: 'jsFunction',
              actionSource: d,
              triggerName: d.id == 'onChange' ? '值变更时候触发' : '点击触发',
              actionObject: {
                type: 'jsFunction',
                name: undefined,
                sourcePath: 'customScript',
                codeEditor: true
              }
            };
            actionElements.push(act);
          } else if (codeSource == 'developJsFileCode') {
            let act = {
              actionType: 'jsFunction',
              actionSource: d,
              triggerName: d.id == 'onChange' ? '值变更时候触发' : '点击触发',
              actionObject: {
                type: 'jsFunction',
                name: undefined,
                sourcePath: 'jsFunction'
              }
            };
            actionElements.push(act);
          }
        }
      }
      return actionElements;
    },
    resolveJsModuleAsActionElement(jsModule, wgt, designer) {
      let actionElements = [];
      if (Array.isArray(jsModule) ? jsModule.length > 0 : jsModule != undefined) {
        let act = {
          actionType: 'jsModule', // 动作类型
          actionSource: Array.isArray(jsModule) ? jsModule : [jsModule], // 动作事件源
          triggerName: '主动执行',
          actionObject: {
            // 执行对象
            type: 'jsModule'
          }
        };
        actionElements.push(act);
      }
      return actionElements;
    },
    resolveDefineEventToActionElement(wgt, designer) {
      let actionElements = [];
      let defineEvents = wgt.configuration.defineEvents;
      if (defineEvents) {
        for (let e of defineEvents) {
          // 默认事件
          if (['created', 'beforeMount', 'mounted'].includes(e.id)) {
            if (e.customScript) {
              let act = {
                elementName: undefined, // 元素名称
                elementTypeName: undefined, // 元素类型名称
                actionType: 'lifecycleEvent', // 动作类型
                actionSource: e, // 动作事件源
                actionTypeName: e.title, // 动作类型名称
                actionObject: {
                  // 执行对象
                  type: 'jsFunction',
                  name: '自定义代码',
                  codeEditor: true,
                  sourcePath: 'customScript' // 对象路径，用于从事件源解析对象值，可以在管理端同步修改对应值
                }
              };
              actionElements.push(act);
            }
          }
        }
      }
      return actionElements;
    },

    resolveEventHandlerToActionElement(element, e, wgt, designer) {
      let actionElements = [];
      if (e == undefined || e.actionType == undefined) {
        return [];
      }
      if (e) {
        let act = {
          elementName: element.elementName, // 元素名称
          elementTypeName: element.elementTypeName, // 元素类型名称
          actionType: e.actionType, // 动作类型
          actionSource: e, // 动作事件源
          actionTypeName: undefined, // 动作类型名称
          triggerName: element.triggerName || '点击触发',
          actionObject: {
            // 执行对象
            type: 'jsFunction',
            name: undefined,
            sourcePath: undefined // 对象路径，用于从事件源解析对象值，可以在管理端同步修改对应值
          }
        };

        if (e.actionType == 'jsFunction') {
          if (e.customScript) {
            let copy = JSON.parse(JSON.stringify(act));
            copy.actionSource = e;
            copy.actionObject.name = '自定义代码';
            copy.actionObject.sourcePath = 'customScript';
            copy.actionObject.codeEditor = true;
            actionElements.push(copy);
          }
          if (e.jsFunction) {
            act.actionObject.name = '二开脚本';
            act.actionObject.sourcePath = 'jsFunction';
          } else {
            return actionElements;
          }
        } else if (e.actionType == 'redirectPage') {
          // 打开页面
          act.actionObject.sourcePath = e.pageType == 'page' ? 'pageId' : 'url';
          if (e.pageType == 'page') {
            act.actionObject.name = e.pageName;
          } else {
            act.actionObject.name = e.url;
          }
        } else if (e.actionType == 'workflow') {
          // 发起流程
          act.actionObject.sourcePath = 'workflowId';
          act.actionObject.name = e.workflowName;
        } else if (e.actionType == 'dataManager') {
          act.actionObject.sourcePath = 'dmsId';
        } else if (e.actionType == 'widgetEvent') {
          act.actionObject.sourcePath = 'eventWid';
          let wevents = designer.widgetDefaultEvents[e.eventWid];
          if (wevents) {
            for (let i = 0, len = wevents.length; i < len; i++) {
              if (wevents[i].id == e.eventId) {
                act.actionTypeName = wevents[i].title;
                // a.actionValuePath = 'eventWid';
                // a.actionValueName = designer.widgetIdMap[e.eventWid].title;
                break;
              }
            }
          }
        }
        actionElements.push(act);
      }
      return actionElements;
    }
  }
};
