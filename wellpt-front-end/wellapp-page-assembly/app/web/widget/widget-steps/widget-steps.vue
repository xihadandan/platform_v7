<template>
  <div :class="['widget-steps', `widget-steps-${configuration.direction}`]">
    <a-steps
      v-model="current"
      :type="configuration.type"
      :direction="configuration.direction"
      :labelPlacement="configuration.labelPlacement"
      :size="configuration.size"
    >
      <template v-for="item in steps">
        <a-step
          v-if="item.style.icon"
          :key="item.id"
          :title="getI18nByItem(item, 'title')"
          :subTitle="getI18nByItem(item, 'subTitle')"
          :description="getI18nByItem(item, 'description')"
          :status="item.status"
          :disabled="item.disabled"
        >
          <template slot="icon">
            <Icon :type="item.style.icon" />
          </template>
        </a-step>
        <a-step
          v-else
          :key="item.id"
          :title="getI18nByItem(item, 'title')"
          :subTitle="getI18nByItem(item, 'subTitle')"
          :description="getI18nByItem(item, 'description')"
          :status="item.status"
          :disabled="item.disabled"
        />
      </template>
    </a-steps>
    <div class="steps-content-wrapper">
      <div :class="['steps-content-container']" v-for="(t, i) in steps" :key="t.id">
        <template v-if="!vHiddenSteps.includes(t.id)">
          <div
            :id="t.id"
            v-if="!t.loading"
            :key="t.key"
            :class="['step-content-panel', i === current ? 'step-content-panel-active' : 'step-content-panel-inactive']"
          >
            <PerfectScrollbar :style="{ height: stepContentScrollHeight }">
              <template
                v-if="
                  t.configuration &&
                  t.configuration.eventHandler &&
                  (t.configuration.eventHandler.pageId != undefined || t.configuration.eventHandler.url != undefined)
                "
              >
                <WidgetVpage
                  v-if="t.configuration.eventHandler.pageId != undefined"
                  :height="stepContentHeight"
                  :pageId="t.configuration.eventHandler.pageId"
                  :widget="t.configuration.eventHandler.pageWidget"
                  :parent="widget"
                />
                <iframe
                  :id="t.id"
                  v-else-if="t.configuration.eventHandler.url != undefined"
                  :src="t.configuration.eventHandler.url"
                  :style="{ minHeight: stepContentHeight, border: 'none', width: '100%' }"
                ></iframe>
              </template>
              <template v-else-if="t.configuration.widgets && t.configuration.widgets.length">
                <template v-for="(wgt, index) in t.configuration.widgets">
                  <component
                    :key="wgt.id"
                    :is="resolveWidgetType(wgt)"
                    :widget="wgt"
                    :parent="widget"
                    :index="index"
                    :widgetsOfParent="t.configuration.widgets"
                    v-bind="wgt.props"
                  ></component>
                </template>
              </template>
              <iframe
                v-else-if="t.configuration.url"
                :src="t.configuration.url"
                :style="{ minHeight: stepContentHeight, border: 'none', width: '100%' }"
              ></iframe>
            </PerfectScrollbar>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import './css/index.less';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { generateId, expressionCompare } from '@framework/vue/utils/util';
import formMixin from '@dyform/app/web/widget/mixin/form-common.mixin';
import stepsMixin from './mixins/index';

export default {
  name: 'WidgetSteps',
  mixins: [widgetMixin, formMixin, stepsMixin],
  inject: ['containerStyle', 'dyform', 'unauthorizedResource'],
  data() {
    let ruleVisible = {},
      emitVisible = {};
    this.widget.configuration.steps.forEach(item => {
      ruleVisible[item.id] = undefined;
      emitVisible[item.id] = undefined;
    });
    return {
      current: 0,
      steps: [],
      stepContentHeight: 'auto',
      stepContentScrollHeight: 'auto',
      hidden: false,
      ruleVisible,
      emitVisible
    };
  },
  computed: {
    defaultEvents() {
      return [
        {
          id: 'stepNext',
          title: '下一步骤',
          codeSnippet: `
          /**
           * 下一步骤
           */
          this.pageContext.emitEvent({{事件编码}});
          `
        },
        {
          id: 'stepPrev',
          title: '上一步骤',
          codeSnippet: `
          /**
           * 上一步骤
           */
          this.pageContext.emitEvent({{事件编码}});
          `
        }
      ];
    },
    vHidden() {
      if (this.vHiddenSteps.length == this.steps.length) {
        return true;
      }
      return this.hidden;
    },
    vHiddenSteps() {
      let steps = this.steps,
        ids = [];
      for (let i = 0, len = steps.length; i < len; i++) {
        // 优先按规则
        if (this.ruleVisible[steps[i].id] != undefined) {
          if (!this.ruleVisible[steps[i].id]) {
            ids.push(steps[i].id);
          }
          continue;
        }
        // 一旦通过事件触发，就不再受条件变化进行显隐控制
        if (this.emitVisible[steps[i].id] != undefined) {
          if (!this.emitVisible[steps[i].id]) {
            ids.push(steps[i].id);
          }
          continue;
        }

        // 根据表达式判断
        let configuration = steps[i].configuration,
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
            let _compareData = { ...this.vPageState, ...(_showByData || {}) };
            for (let i = 0, len = configuration.defaultVisibleVar.conditions.length; i < len; i++) {
              let { code, operator, value } = configuration.defaultVisibleVar.conditions[i];
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
          if (!visible) {
            ids.push(steps[i].id);
          }
        }
      }
      return ids;
    }
  },
  watch: {
    containerStyle: {
      deep: true,
      handler(v) {
        this.resizeStepContentHeight(v.height);
      }
    },
    vHiddenSteps: {
      handler(v) {
        // if (this.activeKey == undefined || v.includes(this.activeKey)) {
        //   for (let i = 0, len = this.tabs.length; i < len; i++) {
        //     if (!v.includes(this.tabs[i].id)) {
        //       this.activeKey = this.tabs[i].id;
        //       break;
        //     }
        //   }
        // }
      }
    }
  },
  created() {
    this.getSteps(this.configuration.options);
  },
  beforeMount() {
    if (this.widget.configuration.height == 'auto' || this.widget.configuration.height == undefined) {
      this.stepContentHeight = 'auto';
      this.stepContentScrollHeight = 'auto';
    } else {
      let padding =
        this.widget.configuration.style != undefined && this.widget.configuration.style.padding != undefined
          ? this.widget.configuration.style.padding[0] + this.widget.configuration.style.padding[2]
          : 0;
      this.stepContentHeight = `calc( ${this.widget.configuration.height} - ${padding}px )`;
      this.stepContentScrollHeight = `calc( ${this.widget.configuration.height} )`;
    }
  },
  methods: {
    getI18nByItem(item, type) {
      if (this.optionType === 'selfDefine') {
        return this.$t('Widget.' + this.widget.id + '.' + item.id + '.' + type, item[type]);
      }
      return item[type];
    },
    getSteps(options, dataModel = this.configuration.dataModel) {
      if (this.optionType === 'selfDefine') {
        this.steps = this.configuration.steps;
        this.steps.forEach(item => {
          if (item.status === 'wait') {
            item.disabled = true;
          }
        });
      } else if (this.optionType === 'dataStore') {
        this.fetchDataStore(options, res => {
          this.formatDataStore(res, options);
        });
      } else if (this.optionType === 'dataModel') {
        this.fetchDataModel(dataModel, res => {
          this.formatDataModel(res, dataModel);
        });
      }
    },
    stepNext() {
      if (this.current >= this.steps.length - 1) {
        return;
      }
      this.current++;
      this.changeStep();
    },
    stepPrev() {
      if (this.current <= 0) {
        return;
      }
      this.current--;
      this.changeStep();
    },
    changeStep(value = this.current) {
      this.steps.forEach((item, index) => {
        if (index < value) {
          item.status = 'finish';
        } else if (index === value) {
          item.status = 'process';
        } else {
          item.status = 'wait';
        }
      });
    },
    resizeStepContentHeight(height) {
      let _style = window.getComputedStyle(this.$el),
        marginPaddingHeight =
          parseInt(_style.paddingBottom) + parseInt(_style.paddingTop) + parseInt(_style.marginBottom) + parseInt(_style.marginTop);

      if (this.widget.configuration.asWindow || (this.widget.configuration.style && this.widget.configuration.style.height === '100%')) {
        let $topBar = this.$el.querySelector('.ant-steps'),
          topBarHeight = 0;
        if ($topBar) {
          topBarHeight = $topBar.getBoundingClientRect().height + parseFloat(window.getComputedStyle($topBar).marginBottom);
        }

        // padding 值
        let padding = 0;
        let $ps = this.$el.querySelector('.step-content-panel .ps');
        if ($ps) {
          let psStyle = window.getComputedStyle($ps);
          padding += parseInt(psStyle.paddingBottom) + parseInt(psStyle.paddingTop);
        }

        if (typeof height == 'number') {
          this.stepContentHeight = height - topBarHeight - padding + 'px';
          this.stepContentScrollHeight = height - topBarHeight + 'px';
        } else if (height.indexOf('calc(') == 0) {
          let _temp = height.substring(height.indexOf('calc(') + 5, height.length - 1).trim();
          this.stepContentHeight = `calc(${_temp} - ${topBarHeight + padding + 5}px)`;
          this.stepContentScrollHeight = `calc(${_temp} - ${topBarHeight}px)`;
        } else {
          this.stepContentHeight = parseInt(height) - topBarHeight - padding + 'px';
          this.stepContentScrollHeight = parseInt(height) - topBarHeight + 'px';
        }
      }
    }
  }
};
</script>
