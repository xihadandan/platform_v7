<template>
  <div class="theme-specify-panel">
    <a-page-header title="色彩">
      <div slot="subTitle">
        平台采用 HSB 色彩模型设计用户交互界面中使用的色彩体系，可创建包括主题色、中性色、功能色等三类色板，用于设计不同颜色和风格的主题。
      </div>

      <a-descriptions title="基础变量" layout="vertical" :colon="false" :column="4">
        <a-descriptions-item label="白色">--w-color-white</a-descriptions-item>
        <a-descriptions-item label="黑色">--w-color-black</a-descriptions-item>
      </a-descriptions>

      <a-descriptions layout="vertical" :colon="false" :column="4" title="主题色">
        <a-descriptions-item label="色彩生成规则" :span="4">
          <a-radio-group
            size="small"
            v-model="config.themeColor.classify[0].formula"
            button-style="solid"
            @change="e => onChangeFormulaEnable(e, config.themeColor.classify[0])"
          >
            <a-radio-button :value="false">内置算法</a-radio-button>
            <a-radio-button :value="config.themeColor.classify[0].formula | formulaTrue">计算公式</a-radio-button>
          </a-radio-group>
        </a-descriptions-item>

        <a-descriptions-item label="基色">{{ config.themeColor.classify[0].code }}</a-descriptions-item>
        <a-descriptions-item label="值/公式"></a-descriptions-item>
        <a-descriptions-item label="描述"></a-descriptions-item>
        <a-descriptions-item>
          <template slot="label">
            色板预览
            <!-- <a-button
              size="small"
              icon="ant-design"
              type="link"
              title="生成色板"
              @click="reGenerateColorDerive(config.themeColor.classify[0])"
            ></a-button> -->
          </template>
          <ColorPicker v-model="config.themeColor.classify[0].value" @ok="e => onColorPickOk(config.themeColor.classify[0])" />
        </a-descriptions-item>
      </a-descriptions>

      <a-descriptions :colon="false" :column="4">
        <template slot="title">
          衍生色
          <a-button
            v-show="config.themeColor.classify[0].formula || config.themeColor.classify[0].formula === undefined"
            size="small"
            type="link"
            icon="plus"
            @click="addDeriveColor(config.themeColor.classify[0])"
          />
        </template>
        <template v-for="(clr, i) in config.themeColor.classify[0].derive">
          <a-descriptions-item :label="null" :key="i">
            {{ clr.code }}
          </a-descriptions-item>
          <a-descriptions-item :label="null" :key="i">
            <a-input
              v-model="clr.value"
              v-show="config.themeColor.classify[0].formula || config.themeColor.classify[0].formula === undefined"
            />
            <div
              :title="clr.value"
              v-show="!config.themeColor.classify[0].formula && config.themeColor.classify[0].formula !== undefined"
              style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap"
            >
              {{ clr.value }}
            </div>
          </a-descriptions-item>
          <a-descriptions-item :label="null" :key="i">
            <a-input v-model="clr.remark" :style="{ width: '90%' }" />
            <a-popconfirm
              v-if="i > 9"
              title="确定删除吗?"
              ok-text="确定"
              cancel-text="取消"
              @confirm="config.themeColor.classify[0].derive.splice(i, 1)"
            >
              <a-button icon="delete" size="small" type="link" style="float: right" />
            </a-popconfirm>
          </a-descriptions-item>
          <a-descriptions-item :label="null" :key="i">
            <div class="color-derive-preview-block-div">
              <div
                class="color-derive-preview-block"
                :style="{ ...colorCssVar, backgroundColor: clr.value }"
                @click="e => onClickCopyColorValue(e, clr.value)"
                @mouseenter="e => hoverToComputeColorHex(e, clr.code, clr.value, clr)"
                @mouseleave="e => leaveToComputeColorHex(e, clr.code, clr.value, clr)"
              >
                <span class="hexString"></span>
              </div>
            </div>
          </a-descriptions-item>
        </template>
      </a-descriptions>

      <a-descriptions :colon="false" :column="1" v-for="key in ['neutralColor', 'functionColor']" :key="key">
        <template slot="title">
          {{ config[key].title }}
          <Modal title="添加色板" :ok="e => editEditColorClassifyPanelOk(e, true, key)" :cancel="onCancelEditColorClassify">
            <a-button size="small" type="link" icon="plus" />
            <template slot="content">
              <EditColorClassifyPanel :classify="currentEditColorClassify" />
            </template>
          </Modal>
        </template>
        <a-descriptions-item v-for="(item, j) in config[key].classify" :key="'colorCfg' + j">
          <a-descriptions :colon="false" :column="1">
            <template slot="title">
              <div>
                <a-icon type="info" :classify-prefix="item.code" />
                {{ item.title }}
                <Modal title="编辑色板" :ok="e => editEditColorClassifyPanelOk(e, false, key)" :cancel="onCancelEditColorClassify">
                  <a-button size="small" type="link" icon="edit" @click="onClickEditColorClassify(j, key)" />
                  <template slot="content">
                    <EditColorClassifyPanel :classify="currentEditColorClassify" />
                  </template>
                </Modal>
                <a-popconfirm
                  v-if="j > 0 && key === 'neutralColor'"
                  title="确认要删除吗?"
                  ok-text="删除"
                  cancel-text="取消"
                  @confirm="deleteNeutralColor(j)"
                >
                  <a-button size="small" type="link" icon="delete" />
                </a-popconfirm>
                <a-button v-if="key === 'neutralColor'" size="small" type="link" icon="plus" @click="addDeriveColor(item)" />
              </div>

              <div v-show="item.remark" class="sub-title">{{ item.remark }}</div>
            </template>
            <a-descriptions-item :label="null" v-if="key === 'functionColor'">
              <a-descriptions layout="vertical" :colon="false" :column="4">
                <a-descriptions-item label="色彩生成规则" :span="4">
                  <a-radio-group size="small" v-model="item.formula" button-style="solid" @change="e => onChangeFormulaEnable(e, item)">
                    <a-radio-button :value="false">内置算法</a-radio-button>
                    <a-radio-button :value="item.formula | formulaTrue">计算公式</a-radio-button>
                  </a-radio-group>
                </a-descriptions-item>
                <a-descriptions-item label="基色">{{ item.code }}</a-descriptions-item>
                <a-descriptions-item label="值/公式"></a-descriptions-item>
                <a-descriptions-item label="描述">{{ item.remark }}</a-descriptions-item>
                <a-descriptions-item>
                  <template #label>
                    色板预览
                    <!-- <a-button
                      v-if="key == 'functionColor'"
                      size="small"
                      icon="ant-design"
                      type="link"
                      title="生成色板"
                      @click="reGenerateColorDerive(item)"
                    ></a-button> -->
                  </template>
                  <ColorPicker v-model="item.value" @ok="e => onColorPickOk(item)" />
                </a-descriptions-item>
              </a-descriptions>
            </a-descriptions-item>
            <a-descriptions-item :label="null">
              <a-descriptions :colon="false" :column="4">
                <template slot="title" v-if="key === 'functionColor'">
                  衍生色
                  <a-button v-show="item.formula" size="small" type="link" icon="plus" @click="addDeriveColor(item)" />
                </template>
                <template v-for="(clr, i) in item.derive">
                  <a-descriptions-item :label="null" :key="i">
                    <a-input
                      v-if="key == 'neutralColor'"
                      :value="codeSuffix(clr.code, item.code)"
                      @change="e => mergeCodeSuffix(e, clr, item.code)"
                    >
                      <template slot="addonBefore">
                        {{ item.code + '-' }}
                      </template>
                    </a-input>
                    <span v-else>{{ clr.code }}</span>
                  </a-descriptions-item>
                  <a-descriptions-item :label="null" :key="i">
                    <a-input v-model="clr.value" v-if="key == 'neutralColor'" />
                    <template v-else>
                      <a-input v-model="clr.value" v-show="item.formula" />
                      <div :title="clr.value" v-show="!item.formula" style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                        {{ clr.value }}
                      </div>
                    </template>
                  </a-descriptions-item>
                  <a-descriptions-item :label="null" :key="i">
                    <a-input-group>
                      <a-row :gutter="8">
                        <a-col :span="20">
                          <a-input v-model="clr.remark" />
                        </a-col>
                        <a-col :span="4" v-if="(j > 0 || (j == 0 && i > 13)) && key === 'neutralColor'">
                          <a-button size="small" type="link" icon="delete" @click="deleteDeriveColors(item, i)" />
                        </a-col>
                      </a-row>
                    </a-input-group>
                  </a-descriptions-item>
                  <a-descriptions-item :label="null" :key="i">
                    <div class="color-derive-preview-block-div">
                      <div
                        :class="['color-derive-preview-block', key === 'neutralColor' ? 'bordered' : '']"
                        v-show="clr.value"
                        :style="{ ...colorCssVar, backgroundColor: clr.value }"
                        @click="e => onClickCopyColorValue(e, clr.value)"
                        @mouseenter="e => hoverToComputeColorHex(e, clr.code, clr.value, clr)"
                        @mouseleave="e => leaveToComputeColorHex(e, clr.code, clr.value, clr)"
                      >
                        <span class="hexString"></span>
                      </div>
                    </div>
                  </a-descriptions-item>
                </template>
              </a-descriptions>
            </a-descriptions-item>
          </a-descriptions>
          <a-empty v-show="item.derive.length == 0" />
        </a-descriptions-item>
      </a-descriptions>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import EditColorClassifyPanel from './edit-color-classify-panel.vue';
import tinycolor from 'tinycolor2';
// import { generate } from '@ant-design/colors';
import { generateMix } from '../generate.js';
import { copyToClipboard } from '@framework/vue/utils/util';
export default {
  name: 'ThemeColorSpecify',
  props: {
    config: Object
  },
  components: { ColorPicker, Modal, EditColorClassifyPanel },
  computed: {
    colorCssVar() {
      let vars = {
        '--w-primary-color': this.config.themeColor.classify[0].value
      };

      // 主题色
      for (let c of this.config.themeColor.classify[0].derive) {
        vars[c.code] = c.value;
      }

      // 中性色
      for (let color of this.config.neutralColor.classify) {
        for (let c of color.derive) {
          vars[c.code] = c.value;
        }
      }

      // 功能色
      for (let color of this.config.functionColor.classify) {
        if (color.value) {
          vars[color.code] = color.value;
        }
        for (let c of color.derive) {
          vars[c.code] = c.value;
        }
      }

      return vars;
    }
  },
  filters: {
    formulaTrue(formula) {
      // 默认为计算公式
      if (formula === undefined) {
        return undefined;
      }
      return true;
    }
  },
  data() {
    return {
      swatchesDefaultColors: [
        '#000000',
        '#cf1322', // 中国红
        '#fadb14', // 日出黄
        '#5b8c00', // 青柠绿
        '#13c2c2', //明青
        '#0958d9' // 科技蓝
      ],
      currentEditColorClassify: {
        code: undefined,
        title: undefined,
        remark: undefined
      },
      colorVarHexString: {},
      defaultPColorFormula: [
        'color-mix(in srgb, var(--w-primary-color) 5%, white)',
        'color-mix(in srgb, var(--w-primary-color) 10%, white)',
        'color-mix(in srgb, var(--w-primary-color) 20%, white)',
        'color-mix(in srgb, var(--w-primary-color) 50%, white)',
        'color-mix(in srgb, var(--w-primary-color) 80%, white)',
        'var(--w-primary-color)',
        'color-mix(in srgb, var(--w-primary-color) 80%, black)',
        'color-mix(in srgb, var(--w-primary-color) 60%, black)',
        'color-mix(in srgb, var(--w-primary-color) 40%, black)',
        'color-mix(in srgb, var(--w-primary-color) 20%, black)'
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeFormulaEnable(e, color) {
      if (e.target.value) {
        // 自动生成公式
        for (let i = 0, len = color.derive.length; i < len; i++) {
          if (color.derive[i].const) {
            color.derive[i].value = this.defaultPColorFormula[i].replace('--w-primary-color', color.code);
            color.derive[i].const = false;
          }
        }
        if (color.derive.length < this.defaultPColorFormula.length) {
          let fillCount = this.defaultPColorFormula.length - color.derive.length;
          for (let i = 1; i <= fillCount; i++) {
            let index = color.derive.length + 1;
            color.derive.push({
              code: `${color.code}-${index}`,
              const: false,
              value: this.defaultPColorFormula[index - 1].replace('--w-primary-color', color.code)
            });
          }
        }
      } else {
        let _this = this;
        this.$confirm({
          title: '提示',
          content: '是否使用色彩算法立即生成色板?',
          onOk() {
            _this.reGenerateColorDerive(color).then(() => {
              _this.$message.success('已生成色板');
            });
          },
          onCancel() {
            color.formula = true; //取消后选项要改成原来的
          }
        });
      }
    },
    onColorPickOk(color) {
      if (color.formula === false) {
        this.reGenerateColorDerive(color).then(() => {
          this.$message.success('已生成色板');
        });
      }
    },

    // 复制颜色值
    onClickCopyColorValue(evt, clr) {
      let _this = this,
        color = this.getDomColorValue(evt, clr);
      copyToClipboard(color, evt, function (success) {
        if (success) {
          _this.$message.success('已复制 ' + color);
        }
      });
    },
    reGenerateColorDerive(color) {
      // 通过字符串表达式计算得实际值
      // let _less = less(window, {});
      // _less.render('body { --color : lighten(red,10%) } ', null, function (a, result) {
      //   console.log(a, result);
      // });
      return new Promise((resolve, reject) => {
        const antColors = generateMix(color.value); //根据公式计算的颜色
        console.log('生成的ant design 色彩体系色板: ', antColors);
        color.derive = [];
        for (let i = 0, len = antColors.length; i < len; i++) {
          if (color.derive[i] == undefined) {
            color.derive.push({
              code: `${color.code}-${i + 1}`,
              const: true,
              value: antColors[i]
            });
          } else {
            color.derive[i].const = true;
            color.derive[i].value = antColors[i];
          }
        }
      });
    },
    getDomColorValue(e, value) {
      let element = e.target,
        hexString = '';
      if (value) {
        let color = tinycolor(value);
        if (color.isValid()) {
          hexString = color.toHexString();
          let isLightColor = color.isLight();
          return hexString;
        } else {
          let _color = getComputedStyle(element)['backgroundColor'];
          let color = tinycolor(_color);
          if (color.isValid() && !_color.startsWith('color(')) {
            hexString = color.toHexString();
          } else {
            let rgb = _color.substring(_color.indexOf(' ') + 1, _color.length - 1).split(' ');
            color = tinycolor.fromRatio({ r: rgb[0], g: rgb[1], b: rgb[2], a: rgb.length === 5 ? rgb[4] : 1 });
            hexString = color.toHexString();
          }
          return hexString;
        }
      }
    },
    hoverToComputeColorHex(e, key, value, item) {
      let element = e.target,
        hexString = '';
      if (value) {
        let $label = document.createElement('lable');
        let color = tinycolor(value);
        if (color.isValid()) {
          hexString = color.toHexString();
          let isLightColor = color.isLight();
          $label.setAttribute('style', `color:${isLightColor ? '#000' : '#fff'};background-color: ${color}`);
          $label.innerHTML = hexString;
          element.lastChild.append($label);
        } else {
          let _color = getComputedStyle(element)['backgroundColor'];
          let color = tinycolor(_color);
          if (color.isValid() && !_color.startsWith('color(')) {
            hexString = color.toHexString();
          } else {
            let rgb = _color.substring(_color.indexOf(' ') + 1, _color.length - 1).split(' ');
            color = tinycolor.fromRatio({ r: rgb[0], g: rgb[1], b: rgb[2], a: rgb.length === 5 ? rgb[4] : 1 });
            hexString = color.toHexString();
          }
          if (hexString) {
            let isLightColor = color.isLight();
            $label.setAttribute('style', `color:${isLightColor ? '#000' : '#fff'};background-color: ${_color}`);
            $label.innerHTML = hexString;
            element.lastChild.append($label);
          }
          item._value = hexString;
        }
      }
    },
    leaveToComputeColorHex(e, key, value, item) {
      let element = e.target;
      if (element.lastChild.lastChild) {
        element.lastChild.removeChild(element.lastChild.lastChild);
      }
    },
    getCssComputedColorPropValue(key) {
      return EASY_ENV_IS_BROWSER ? getComputedStyle(this.$el).getPropertyValue(key) : undefined;
    },

    mergeCodeSuffix(e, clr, prefix) {
      clr.code = prefix + '-' + e.target.value;
    },
    codeSuffix(code, prefix) {
      return code.split(prefix + '-')[1];
    },
    addDeriveColor(tar) {
      tar.derive.push({
        code: tar.code + '-' + (tar.derive.length + 1),
        value: undefined,
        remark: undefined
      });
    },

    deleteNeutralColor(index) {
      this.config.neutralColor.classify.splice(index, 1);
    },
    deleteDeriveColors(tar, index) {
      if (index == undefined) {
        tar.derive = [];
      } else {
        tar.derive.splice(index, 1);
      }
    },
    onClickEditColorClassify(index, key) {
      let colorClass = this.config[key].classify[index];
      this.currentEditColorClassifyIndex = index;
      this.currentEditColorClassify.code = colorClass.code;
      this.currentEditColorClassify.title = colorClass.title;
      this.currentEditColorClassify.remark = colorClass.remark;
    },
    onCancelEditColorClassify() {
      this.currentEditColorClassifyIndex = undefined;
      this.currentEditColorClassify = { code: undefined, title: undefined, remark: undefined };
    },
    editEditColorClassifyPanelOk(callback, create, key) {
      if (!this.currentEditColorClassify.code || !this.currentEditColorClassify.title) {
        return;
      }
      delete this.currentEditColorClassify.suffix;
      if (create) {
        this.config[key].classify.push({ ...this.currentEditColorClassify, derive: [] });
      } else {
        let colorClass = this.config[key].classify[this.currentEditColorClassifyIndex];
        colorClass.code = this.currentEditColorClassify.code;
        colorClass.title = this.currentEditColorClassify.title;
        colorClass.remark = this.currentEditColorClassify.remark;
        this.currentEditColorClassifyIndex = undefined;
      }

      this.currentEditColorClassify = { code: undefined, title: undefined, remark: undefined };
      callback(true);
    }
  }
};
</script>
