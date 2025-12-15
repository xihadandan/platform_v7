<template>
  <div :style="{ textAlign: widget.configuration.align }" :class="['no-form-item-widget', widgetClass]" v-show="!vHidden">
    <a-dropdown v-if="widget.configuration.groupType == 'dropdown'">
      <a-menu slot="overlay">
        <template v-for="(btn, i) in groupBtns">
          <a-menu-item v-if="!btn.hidden" :key="btn.id" @click="e => onClick(e, btn)">
            <Icon :type="btn.icon" v-if="btn.icon" :size="20" />
            {{ $t(btn.id, btn.title) }}
            <Icon :type="btn.suffixIcon" v-if="btn.suffixIcon" :size="20" />
          </a-menu-item>
        </template>
      </a-menu>
      <a-button
        :type="widget.configuration.type"
        :size="widget.configuration.size"
        :block="widget.configuration.block"
        :shape="widget.configuration.shape"
      >
        {{ $t(widget.id, widget.title) }}
        <a-icon type="down" />
      </a-button>
    </a-dropdown>
    <template v-else-if="widget.configuration.groupType == 'buttonGroup'">
      <a-space v-if="widget.configuration.enableSpace" :size="widget.configuration.buttonSpace || 8">
        <template v-for="(btn, i) in groupBtns">
          <a-button
            :key="btn.id"
            :type="widget.configuration.type"
            v-if="!btn.hidden"
            @click="e => onClick(e, btn)"
            :size="widget.configuration.size"
          >
            <Icon :type="btn.icon" v-if="btn.icon" :size="iconSize" />
            {{ btn.textHidden ? '' : $t(btn.id, btn.title) }}
            <Icon :type="btn.suffixIcon" v-if="btn.suffixIcon" />
          </a-button>
        </template>
      </a-space>
      <a-button-group v-else :size="widget.configuration.size">
        <template v-for="(btn, i) in groupBtns">
          <a-button :key="btn.id" v-if="!btn.hidden" :type="widget.configuration.type" @click="e => onClick(e, btn)">
            <Icon :type="btn.icon" v-if="btn.icon" :size="iconSize" />
            {{ btn.textHidden ? '' : $t(btn.id, btn.title) }}
            <Icon :type="btn.suffixIcon" v-if="btn.suffixIcon" :size="iconSize" />
          </a-button>
        </template>
      </a-button-group>
    </template>

    <a-button
      v-else-if="widget.configuration.type !== 'switch'"
      :type="widget.configuration.type"
      :block="widget.configuration.block"
      :size="widget.configuration.size"
      :ghost="widget.configuration.ghost"
      :shape="widget.configuration.shape"
      @click="e => onClick(e, widget.configuration)"
    >
      <Icon :type="widget.configuration.icon" v-if="widget.configuration.icon" :size="iconSize" />
      {{ widget.configuration.textHidden ? '' : $t(widget.id, widget.title) }}
      <Icon :type="widget.configuration.suffixIcon" v-if="widget.configuration.suffixIcon" :size="iconSize" />
    </a-button>
    <a-switch
      v-else
      :size="widget.configuration.size"
      :checked="widget.configuration.switch.defaultChecked"
      :checked-children="widget.configuration.switch.checkedText || null"
      :un-checked-children="widget.configuration.switch.UnCheckedText || null"
      @click="e => onClick(e, widget.configuration)"
    />
  </div>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import { expressionCompare, executeJSFormula } from '@framework/vue/utils/util';
import { some, merge } from 'lodash';

export default {
  name: 'WidgetButton',
  mixins: [widgetMixin],
  inject: [],
  data() {
    return {};
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {
    hasGroupType() {
      return this.widget.configuration.groupType != null;
    },
    vHidden() {
      if (!this.designMode) {
        // 按钮组
        if (this.hasGroupType) {
          // some 遇到真值就停止，返回true
          let hasShow = some(this.groupBtns, item => {
            return !item.hidden;
          });
          return !hasShow;
        }
        return !this.vShow;
      }
      return false;
    },
    groupBtns() {
      let btns = this.widget.configuration.group;
      for (let i = 0, len = btns.length; i < len; i++) {
        // 根据表达式判断
        let configuration = btns[i],
          visible = true;
        if (!this.designMode && configuration.defaultVisible != undefined) {
          visible = configuration.defaultVisible;
          if (
            // 根据条件判断显隐
            configuration.defaultVisibleVar &&
            configuration.defaultVisibleVar.enable &&
            configuration.defaultVisibleVar.conditions != undefined &&
            configuration.defaultVisibleVar.conditions.length > 0
          ) {
            // 多组条件判断
            let match = configuration.defaultVisibleVar.match == 'all';
            let _showByData = {};
            if (this._vShowByData) {
              _showByData = this._vShowByData;
            } else if (this.dyform != undefined) {
              _showByData = this.dyform.formData;
            }
            let _compareDataSource = this.widgetDependentVariableDataSource();
            let _compareData = { ...this.vPageState, ..._compareDataSource, ...(_showByData || {}) };
            for (let i = 0, len = configuration.defaultVisibleVar.conditions.length; i < len; i++) {
              let { code, operator, value, valueType } = configuration.defaultVisibleVar.conditions[i];
              if (valueType == 'variable') {
                try {
                  value = get(_compareData, value);
                } catch (error) {
                  console.error('无法解析变量值', value);
                }
              }
              let result = expressionCompare(_compareData, code, operator, value);
              if (configuration.defaultVisibleVar.match == 'all' && !result) {
                match = false;
                break;
              }
              if (configuration.defaultVisibleVar.match == 'any' && result) {
                match = true;
                break;
              }
            }
            visible = match ? visible : !visible;
          }
          btns[i].hidden = !visible;
        }
      }
      return btns;
    },
    iconSize() {
      return { small: 18, default: 20, large: 24 }[this.widget.configuration.size];
    }
  },
  created() {},
  methods: {
    onClick(evt, button) {
      if (this.designMode) {
        return;
      }
      let _this = this,
        eventHandler = button.eventHandler;
      let _parent = this.parentWidget != undefined ? this.parentWidget : undefined;
      if (typeof this.parentWidget === 'function') {
        _parent = this.parentWidget();
      }
      eventHandler.pageContext = this.pageContext;
      eventHandler.$evtWidget = this;
      if (this.eventWidget != undefined) {
        if (typeof this.eventWidget === 'function') {
          eventHandler.$evtWidget = this.eventWidget(_parent);
        } else {
          eventHandler.$evtWidget = this.eventWidget;
        }
      }
      // 元数据通过事件传递
      eventHandler.meta = merge({}, this.meta || {}, this.widgetDependentVariableDataSource());
      eventHandler.$evt = evt;
      eventHandler.key = button.id || this.widget.id;
      let developJs = typeof this.developJsInstance === 'function' ? this.developJsInstance() : this.developJsInstance;
      if (developJs == undefined) {
        developJs = {};
      }
      if (this.$pageJsInstance != undefined) {
        developJs[this.$pageJsInstance._JS_META_] = this.$pageJsInstance;
      }
      eventHandler.$developJsInstance = developJs;
      if (button.style && button.style.type === 'switch') {
        // 开关按钮需要有loading效果
        eventHandler.before = () => {
          _this.loading = true;
        };
        eventHandler.after = success => {
          _this.loading = false;
          if (typeof success === 'boolean' && success) {
            // 开关执行成功回调通知变更状态
            button.switch.checked = !button.switch.checked;
          }
        };
      }

      if (eventHandler.actionType) {
        if (button.behaviorLogConfig && button.behaviorLogConfig.enable) {
          let calculateDataSource = {
            ...(this.widgetDependentVariableDataSource() || {})
          };
          this._logger.commitBehaviorLog({
            type: 'click',
            element: {
              tag: 'BUTTON',
              text: button.title,
              id: button.id
            },
            page: {
              url: window.location.href,
              title: this.vPage != undefined ? this.vPage.title || document.title : undefined,
              id: this.vPage != undefined ? this.vPage.pageId || this.vPage.pageUuid : undefined
            },
            businessCode: button.behaviorLogConfig.businessCode
              ? executeJSFormula(button.behaviorLogConfig.businessCode.value, calculateDataSource)
              : undefined,
            description: button.behaviorLogConfig.description
              ? executeJSFormula(button.behaviorLogConfig.description.value, calculateDataSource)
              : undefined,
            extraInfo: button.behaviorLogConfig.extraInfo
              ? executeJSFormula(button.behaviorLogConfig.extraInfo.value, calculateDataSource)
              : undefined
          });
        }
        new DispatchEvent(eventHandler).dispatch();
      }
    }
  },
  mounted() {}
};
</script>
