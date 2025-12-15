<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title"></a-input>
        </a-form-model-item>
        <template v-if="widget.configuration.textType == 'static'">
          <a-form-model-item label="文本">
            <a-input v-model="widget.configuration.text"></a-input>
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              跑马灯
              <a-checkbox v-model="widget.configuration.enableCarousel" />
            </template>

            <a-input-group
              v-show="widget.configuration.enableCarousel"
              compact
              style="display: flex; align-items: center; justify-content: flex-end"
            >
              滚动速度
              <a-input-number style="margin-left: 5px" v-model="widget.configuration.carouselSeconds" :min="1" />
              <a-button>秒</a-button>
            </a-input-group>
          </a-form-model-item>
        </template>
        <template v-if="widget.configuration.textType == 'datetime'">
          <a-form-model-item label="展示方式">
            <a-radio-group v-model="widget.configuration.datetimeDisplayType" button-style="solid" size="small">
              <a-radio-button value="current">显示当前日期</a-radio-button>
              <a-radio-button value="countdown">倒计时</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="结束日期" v-if="widget.configuration.datetimeDisplayType == 'countdown'">
            <a-date-picker
              style="width: 100%"
              format="yyyy年MM月DD日 HH时mm分ss秒"
              :show-time="true"
              v-model="endDate"
              allowClear
              @change="onChangeEndDate"
            />
          </a-form-model-item>
          <a-form-model-item label="日期格式" v-if="widget.configuration.datetimeDisplayType == 'current'">
            <a-select v-model="widget.configuration.datetimePattern" style="width: 100%" option-label-prop="label">
              <template v-for="(opt, i) in vDatePatternOptions">
                <a-select-option :value="opt.value" :label="widget.configuration.fixZero ? opt.label : opt.labelNo0">
                  <div>
                    <div>{{ widget.configuration.fixZero ? opt.label : opt.labelNo0 }}</div>
                    <div style="font-size: 11px; color: #aeaeae">{{ widget.configuration.fixZero ? opt.eg : opt.egNo0 }}</div>
                  </div>
                </a-select-option>
              </template>
            </a-select>
            <a-checkbox v-model="widget.configuration.fixZero">日期时间补0显示</a-checkbox>
          </a-form-model-item>
        </template>

        <a-collapse :bordered="false" expandIconPosition="right">
          <a-collapse-panel key="textStyle" header="文本样式">
            <a-form-model-item label="字体大小">
              <a-input-number :min="12" v-model="widget.configuration.style.fontStyle.fontSize" />
            </a-form-model-item>
            <a-form-model-item label="字体权重">
              <a-input-number :min="100" :step="100" v-model="widget.configuration.style.fontStyle.fontWeight" />
            </a-form-model-item>
            <a-form-model-item label="字体颜色">
              <ColorPicker v-model="widget.configuration.style.fontStyle.color" />
            </a-form-model-item>
            <a-form-model-item label="文字间距">
              <a-input-number :min="1" v-model="widget.configuration.style.fontStyle.letterSpacing" />
            </a-form-model-item>
          </a-collapse-panel>
        </a-collapse>
      </a-tab-pane>

      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration
          :widget="widget"
          :designer="designer"
          :allowRegisterEvent="false"
          :allowLifecycleEvent="false"
        ></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import moment from 'moment';

export default {
  name: 'WidgetBgTextConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  components: { ColorPicker },
  computed: {
    textStyle() {
      let style = {},
        fontStyle = this.widget.configuration.style.fontStyle;
      style.color = fontStyle.color;
      return style;
    },
    vDatePatternOptions() {
      let options = [],
        pattern = [
          'yyyy年MM月DD日 HH时mm分ss秒',
          'yyyy年MM月DD日 HH时mm分',
          'yyyy年MM月DD日 HH时',
          'yyyy年MM月DD日',
          'MM月DD日 HH时mm分ss秒',
          'MM月DD日 HH时mm分ss秒 ddd',
          'MM月DD日 HH时mm分ss秒 d'
        ],
        now = moment();
      for (let i = 0, len = pattern.length; i < len; i++) {
        let patternNo0 = pattern[i].replace(/MM/g, 'M').replace(/DD/g, 'D').replace(/HH/g, 'H').replace(/mm/g, 'm').replace(/ss/g, 's');
        options.push({
          label: pattern[i],
          labelNo0: patternNo0,
          value: pattern[i],
          valueNo0: patternNo0,
          eg: now.format(pattern[i]),
          egNo0: now.format(patternNo0)
        });
      }
      return options;
    }
  },
  data() {
    return {
      endDate: this.widget.configuration.endDate ? moment(this.widget.configuration.endDate, 'yyyy年MM月DD日 HH时mm分ss秒') : undefined
    };
  },
  beforeCreate() {},
  created() {
    if (this.widget.configuration.domEvents == undefined) {
      this.$set(this.widget.configuration, 'domEvents', [
        {
          id: 'onClick',
          title: '点击触发',
          codeSource: 'codeEditor',
          jsFunction: undefined,
          widgetEvent: [],
          customScript: undefined // 事件脚本
        }
      ]);
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeEndDate(v) {
      this.widget.configuration.endDate = undefined;
      if (v) {
        this.widget.configuration.endDate = v.format('yyyy年MM月DD日 HH时mm分ss秒');
      }
    }
  },
  configuration(widget) {
    let conf = {
      text: '请输入文本',
      enableCarousel: false,
      carouselSeconds: 5,
      textType: 'static',
      style: {
        width: 100,
        height: 30,
        zIndex: 2,
        fontStyle: {
          color: '#fff',
          justifyContent: 'center',
          letterSpacing: 1,
          fontSize: 14
        }
      }
    };
    if (widget != undefined) {
      if (widget.subtype == 'currentTime') {
        conf.textType = 'datetime';
        conf.datetimePattern = 'yyyy年MM月DD日 HH时mm分ss秒';
        conf.fixZero = true;
        conf.style.width = 300;
      }

      if (widget.subtype == 'currentTime') {
        conf.textType = 'datetime';
        conf.datetimePattern = 'yyyy年MM月DD日 HH时mm分ss秒';
        conf.fixZero = true;
        conf.style.width = 300;
        conf.datetimeDisplayType = 'current';
        conf.endDate = undefined;
      }
    }

    return conf;
  }
};
</script>
