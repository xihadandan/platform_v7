<template>
  <component
    v-if="widget.wtype"
    :is="widget.wtype"
    :widget="widget"
    @valueChange="$evt => onValueChange($evt)"
    @mounted="$evt => onCellComponentMounted($evt)"
    ref="widget"
    :labelHidden="true"
    stopEvent
    :form="form"
  />
</template>
<style lang="less"></style>
<script type="text/babel">
import { expressionCompare, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'SubformProxyFormCell',
  inject: ['widgetSubformContext', 'dyform', 'widgetDyformContext', 'pageContext'],
  props: {
    widget: Object,
    widgetSubform: Object,
    form: Object
  },
  components: {},
  computed: {
    columnConfig() {
      for (let i = 0, len = this.widgetSubform.configuration.columns.length; i < len; i++) {
        if (this.widgetSubform.configuration.columns[i].widget.id === this.widget._id) {
          return this.widgetSubform.configuration.columns[i];
        }
      }
      return null;
    },
    colValueChangeDomEvent() {
      if (this.columnConfig && this.columnConfig.domEvents != undefined) {
        for (let i = 0, len = this.columnConfig.domEvents.length; i < len; i++) {
          if (this.columnConfig.domEvents[i].id == 'onChange') {
            // 仅保留必填事件
            let domEvents = deepClone(this.columnConfig.domEvents[i]);
            if (domEvents && domEvents.enable && domEvents.widgetEvent && domEvents.widgetEvent.length > 0) {
              domEvents.widgetEvent = domEvents.widgetEvent.filter(item => {
                if (!item.event) return false;
                item.event = item.event.filter(eitem => {
                  return eitem.eventId == 'setRequired';
                });
                return item.event.length;
              });
              return domEvents;
            }
            return undefined;
          }
        }
      }
      return undefined;
    }
  },
  data() {
    return {
      value: null
    };
  },
  beforeCreate() {},
  created() {
    if (this.colValueChangeDomEvent != undefined && this.colValueChangeDomEvent.enable) {
      // 去除从表字段组件上的值变更事件
      let domEvents = this.widget.configuration.domEvents;
      if (domEvents != undefined) {
        for (let i = 0; i < domEvents.length; i++) {
          if (domEvents[i].id == 'onChange') {
            domEvents.splice(i, 1);
            break;
          }
        }
      }
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onCellComponentMounted($evt) {
      // 初始化自定义事件处理
      let _this = $evt.$vue;
      for (let k in _this.vDefineEvents) {
        let e = _this.vDefineEvents[k];
        if (e.id == 'setRequired') {
          // 对外开放的事件
          this.pageContext.offEvent(`${this.widget._id}:${e.id}`).handleEvent(`${this.widget._id}:${e.id}`, function () {
            if (!e.default) {
              let args = Array.from(arguments);
              args = [e].concat(args); // 参数合并
              _this.executeEvent.apply(_this, args);
            } else if (_this[e.id]) {
              // 默认的事件，存在对应的方法函数，执行该方法函数
              _this[e.id].apply(_this, arguments);
            } else if (e.codeSource == 'widgetEvent') {
              _this.executeEvent.apply(_this, [e]);
            }
          });
        }
      }
    },
    fireEvent(evt) {
      if (evt.codeSource == 'widgetEvent') {
        let metaData = { CURRENT_ROW: this.form.formData, ...this.dyform.formData };
        let widgetEvent = evt.widgetEvent;
        if (widgetEvent) {
          let events = [widgetEvent];
          if (Array.isArray(widgetEvent)) {
            events = widgetEvent;
          }
          events.forEach(eventItem => {
            let executeEvent = true;
            if (eventItem.condition && eventItem.condition.enable && eventItem.condition.conditions.length > 0) {
              // 判断条件是否成立
              let { conditions, match } = eventItem.condition;
              executeEvent = match == 'all';
              for (let c = 0, clen = conditions.length; c < clen; c++) {
                let { code, operator, value } = conditions[c];
                let isTrue = expressionCompare(metaData, code, operator, value);
                if (match == 'any') {
                  // 满足任一条件就执行
                  if (isTrue) {
                    executeEvent = true;
                    break;
                  }
                } else {
                  // 全部情况下，只要一个条件不满足就不执行
                  if (!isTrue) {
                    executeEvent = false;
                    break;
                  }
                }
              }
            }

            if (executeEvent) {
              let eventArray = eventItem.event != undefined ? eventItem.event : [eventItem];
              for (let i = 0; i < eventArray.length; i++) {
                let { eventId, eventParams, eventWid, wEventParams } = eventArray[i];
                try {
                  if (
                    !this.editable &&
                    eventId == 'setEditable' &&
                    /**
                     * 字段不可编辑情况下，触发字段能进行可编辑状态切换的前提:
                     * 1. 当前整体可编辑
                     * 2. 无规则限制字段不可编辑
                     *
                     * 否则要把编辑切换修改为不可编辑情况进行展示
                     */
                    (this.form.displayState !== 'edit' ||
                      (this.form.formElementRules &&
                        this.form.formElementRules[eventWid] &&
                        this.form.formElementRules[eventWid].editable !== true))
                  ) {
                    let paramGet = false;
                    eventParams = JSON.parse(JSON.stringify(eventParams || []));
                    if (eventParams) {
                      for (let i = 0, len = eventParams.length; i < len; i++) {
                        if (eventParams[i].paramKey == 'editable') {
                          eventParams[i].paramValue =
                            eventParams[i].paramValue == 'true'
                              ? this.editable === false || this.form.displayState !== 'edit'
                                ? 'false'
                                : 'true'
                              : eventParams[i].paramValue;
                          paramGet = true;
                          break;
                        }
                      }
                    }
                    if (!paramGet) {
                      eventParams.push({
                        paramKey: 'editable',
                        paramValue: 'false'
                      });
                    }
                    eventId = 'setEditable';
                  }
                  let handler = {
                    actionType: 'widgetEvent',
                    $evtWidget: this.$refs.widget,
                    meta: metaData,
                    eventWid,
                    eventId,
                    eventParams,
                    wEventParams,
                    pageContext: this.pageContext
                  };

                  this.$refs.widget.dispatchEventHandler(handler);
                } catch (error) {
                  console.error(error);
                }
              }
            }
          });
        }
      } else {
        this.$refs.widget.executeEvent(evt, {
          $widget: this.$refs.widget,
          $widgetSubform: this.widgetSubformContext,
          $widgetDyform: this.widgetDyformContext,
          currentSubForm: this.form
        });
      }
    },
    onValueChange({ newValue, oldValue }) {
      this.value = newValue;
      if (this.colValueChangeDomEvent && this.colValueChangeDomEvent.enable) {
        console.log('colValueChangeDomEvent', this.widget.configuration.code, ':', this.colValueChangeDomEvent);
        this.fireEvent(this.colValueChangeDomEvent);
      }
    }
  }
};
</script>
