<template>
  <div class="theme-style-panel">
    <a-page-header title="色彩" class="theme-color-design-container">
      <div slot="subTitle">
        平台采用 HSB 色彩模型设计用户交互界面中使用的色彩体系，可创建包括主题色、中性色、功能色等三类色板，用于设计不同颜色和风格的主题。
      </div>

      <a-card>
        <template slot="title">
          主题色
          <Modal title="添加主题色" slot="extra" :ok="onConformThemeColorItem" :cancel="onCancelNewColorItem">
            <a-button type="link" icon="plus" />
            <template slot="content">
              <a-form-model :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules">
                <a-form-model-item label="名称">
                  <a-input v-model="newColorItem.title" />
                </a-form-model-item>
                <a-form-model-item label="描述">
                  <a-textarea v-model="newColorItem.remark" />
                </a-form-model-item>
              </a-form-model>
            </template>
          </Modal>
        </template>

        <div>
          <a-descriptions
            v-for="(color, c) in config.themeColor.classify"
            :key="'themeColorClassify' + c"
            :colon="false"
            :bordered="false"
            layout="vertical"
            :column="1"
            @click.native.stop="onClickColorBlock(color)"
          >
            <template slot="title">
              {{ color.title }}
              <span v-if="c > 0">
                <Modal title="编辑颜色" slot="extra" :ok="e => onConformThemeColorItem(e, c)" :cancel="onCancelNewColorItem">
                  <a-button type="link" icon="edit" @click="onEditExtThemeColor(color)" />
                  <template slot="content">
                    <a-form-model :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules">
                      <a-form-model-item label="名称">
                        <a-input v-model="newColorItem.title" prop="title" />
                      </a-form-model-item>
                      <a-form-model-item label="描述">
                        <a-textarea v-model="newColorItem.remark" />
                      </a-form-model-item>
                    </a-form-model>
                  </template>
                </Modal>
                <a-popconfirm title="确定要删除吗?" ok-text="确定" cancel-text="取消" @confirm="config.themeColor.classify.splice(c, 1)">
                  <a-button type="link" icon="delete" />
                </a-popconfirm>
              </span>
            </template>
            <a-descriptions-item>
              <template slot="label">基色 {{ color.code }}</template>
              <a-row class="full-line" type="flex">
                <a-col flex="auto">
                  <ColorPicker v-model="color.value" :width="150" @onChangeColor="onChangeColor(color)" />
                  <a-button type="link">推荐基色</a-button>
                </a-col>
                <a-col flex="70px">
                  <a-switch
                    style="width: 68px"
                    checked-children="默认"
                    :checked="color.default"
                    @change="e => onChangeDefaultThemePrimaryColor(e, c, config.themeColor.classify)"
                  />
                </a-col>
              </a-row>
            </a-descriptions-item>
            <a-descriptions-item label="衍生色">
              <div class="preview-color-block" :style="{ '--w-primary-color': color.value }">
                <div
                  class="color-block"
                  v-for="(clr, i) in color.derive"
                  :key="'themeColorDerive_' + c + i"
                  @click.stop="onClickColorBlock(clr)"
                >
                  <div
                    :style="{
                      [clr.code]: clr.value,
                      'background-color': 'var(' + clr.code + ')'
                    }"
                  >
                    <div v-show="c == 0">{{ clr.code }}</div>
                    <div class="hexString" :value="clr.value"></div>
                  </div>
                  <label :title="clr.remark">{{ clr.remark }}</label>
                </div>
              </div>
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>

      <a-card>
        <template slot="title">
          中性色
          <Modal
            title="添加中性色"
            :ok="e => editEditColorClassifyPanelOk(e, true, key, config.neutralColor)"
            :cancel="onCancelEditColorClassify"
          >
            <a-button size="small" type="link" icon="plus" />
            <template slot="content">
              <EditColorClassifyPanel :classify="currentEditColorClassify" />
            </template>
          </Modal>
        </template>
        <div :style="neutralColorStyleVar">
          <a-descriptions :colon="false" :bordered="false" layout="vertical" :column="1">
            <a-descriptions-item v-for="(color, i) in config.neutralColor.classify" :key="'themeNColor' + i">
              <template slot="label">
                {{ color.title }}

                <Modal :title="'添加' + color.title" :ok="e => editVar(e, color)" :cancel="onCancelEditVar">
                  <a-button size="small" type="link" icon="plus" @click="setVarCode(color, color.code)" />
                  <template slot="content">
                    <EditVarPanel :item="varItem" :prefix="color.code" />
                  </template>
                </Modal>
              </template>
              <div class="preview-color-block">
                <div
                  class="color-block bordered"
                  v-for="(clr, i) in color.derive"
                  :key="'themeNColorSubDerive' + i"
                  @click="onClickColorBlock(clr)"
                >
                  <div
                    :style="{
                      [clr.code]: clr.value,
                      'background-color': 'var(' + clr.code + ')'
                    }"
                  >
                    <div>{{ clr.code }}</div>
                    <div class="hexString" :value="clr.value"></div>
                  </div>
                  <label>{{ clr.remark }}</label>
                </div>
              </div>
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>

      <a-card>
        <template slot="title">
          功能色
          <Modal
            title="添加功能色"
            :ok="e => editEditColorClassifyPanelOk(e, true, key, config.functionColor)"
            :cancel="onCancelEditColorClassify"
          >
            <a-button size="small" type="link" icon="plus" />
            <template slot="content">
              <EditColorClassifyPanel :classify="currentEditColorClassify" />
            </template>
          </Modal>
        </template>
        <div :style="funcColorStyleVar">
          <a-descriptions
            :colon="false"
            :bordered="false"
            :column="1"
            v-for="(color, i) in config.functionColor.classify"
            :key="'funccolor' + i"
          >
            <template slot="title">
              {{ color.title }}
              <Modal :title="'添加' + color.title" :ok="e => editVar(e, color)" :cancel="onCancelEditVar">
                <a-button size="small" type="link" icon="plus" @click="setVarCode(color, color.code)" />
                <template slot="content">
                  <EditVarPanel :item="varItem" :prefix="color.code" />
                </template>
              </Modal>
            </template>
            <a-descriptions-item>
              <a-descriptions :colon="false" :bordered="false" layout="vertical" :column="1">
                <a-descriptions-item>
                  <template slot="label">基色 {{ color.code }}</template>
                  <ColorPicker v-model="color.value" :width="150" />
                </a-descriptions-item>

                <a-descriptions-item label="衍生色">
                  <div class="preview-color-block">
                    <div
                      class="color-block"
                      v-for="(clr, i) in color.derive"
                      :key="'themeFuncColorSub' + i"
                      @click="onClickColorBlock(clr)"
                    >
                      <div
                        :style="{
                          [clr.code]: clr.value,
                          'background-color': 'var(' + clr.code + ')'
                        }"
                      >
                        <div>{{ clr.code }}</div>
                        <div class="hexString" :value="clr.value"></div>
                      </div>
                      <label>{{ clr.remark }}</label>
                    </div>
                  </div>
                </a-descriptions-item>
              </a-descriptions>
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import '../../../css/index.less';
import tinycolor from 'tinycolor2';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import EditColorClassifyPanel from '../../edit-color-classify-panel.vue';
import varMixin from './varMixin.js';
import ThemeColorSpecify from '../../theme-color-specify.vue';
export default {
  name: 'ThemeColor',
  mixins: [varMixin],
  props: {
    config: Object
  },
  components: { ColorPicker, Modal, EditColorClassifyPanel },
  computed: {
    neutralColorStyleVar() {
      let vars = {};
      for (let color of this.config.neutralColor.classify) {
        for (let de of color.derive) {
          vars[de.code] = de.value;
        }
      }
      return vars;
    },
    funcColorStyleVar() {
      let vars = {};
      for (let color of this.config.functionColor.classify) {
        vars[color.code] = color.value;
        for (let de of color.derive) {
          vars[de.code] = de.value;
        }
      }
      return vars;
    }
  },
  data() {
    return {
      color: '#fff',
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        title: [{ required: true, message: '名称必填', trigger: 'blur' }]
      },
      newColorItem: { title: undefined, remark: undefined, value: undefined },
      currentEditColorClassify: {
        code: undefined,
        title: undefined,
        remark: undefined
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.computeBgColorHex();
  },
  methods: {
    onClickColorBlock(e) {
      this.$emit('select', e);
    },
    onEditExtThemeColor(ext) {
      this.newColorItem.title = ext.title;
      this.newColorItem.remark = ext.remark;
    },
    onClickDeleteThemeColorExt(i) {
      let _this = this;
      this.$confirm({
        title: '提示',
        content: '确认要删除吗?',
        onOk() {
          _this.config.themeColor.classify.splice(i, 1);
        },
        onCancel() {}
      });
    },
    onChangeDefaultThemePrimaryColor(checked, index, classify) {
      if (checked && classify[index].default === false) {
        for (let i = 0, len = classify.length; i < len; i++) {
          if (i != index) {
            classify[i].default = false;
          }
        }
        classify[index].default = true;
      }
    },
    onCancelNewColorItem() {
      this.newColorItem.title = undefined;
      this.newColorItem.remark = undefined;
    },
    onConformThemeColorItem(callback, i) {
      if (this.newColorItem.title) {
        if (i == undefined) {
          let newColor = {
            title: this.newColorItem.title,
            code: this.config.themeColor.classify[0].code,
            value: this.config.themeColor.classify[0].value, // 新增默认同第一个主色
            remark: this.newColorItem.remark,
            derive: [],
            default: false
          };
          if (this.config.themeColor.formula || this.config.themeColor.formula === undefined) {
            newColor.derive = this.config.themeColor.classify[0].derive; // 根据公式计算
          } else {
            // 色彩计算
            ThemeColorSpecify.methods.reGenerateColorDerive(newColor);
          }
          this.config.themeColor.classify.push(newColor);
        } else {
          let item = this.config.themeColor.classify[i];
          item.title = this.newColorItem.title;
          item.remark = this.newColorItem.remark;
        }
        this.newColorItem = {
          title: undefined,
          remark: undefined
        };
        callback(true);
      }
    },

    onCancelEditColorClassify() {
      this.currentEditColorClassifyIndex = undefined;
      this.currentEditColorClassify = { code: undefined, title: undefined, remark: undefined };
    },
    editEditColorClassifyPanelOk(callback, create, key, tar) {
      if (!this.currentEditColorClassify.code || !this.currentEditColorClassify.title) {
        return;
      }
      delete this.currentEditColorClassify.suffix;
      if (create) {
        tar.classify.push({ ...this.currentEditColorClassify, derive: [] });
      } else {
        let colorClass = tar.classify[this.currentEditColorClassifyIndex];
        colorClass.code = this.currentEditColorClassify.code;
        colorClass.title = this.currentEditColorClassify.title;
        colorClass.remark = this.currentEditColorClassify.remark;
      }

      this.onCancelEditColorClassify();
      callback(true);
    },
    onChangeColor(color) {
      this.onSelectColorOk(color);
    },
    onSelectColorOk(color) {
      if (this.config.themeColor.classify[0].formula || this.config.themeColor.classify[0].formula === undefined) {
        color.derive = this.config.themeColor.classify[0].derive; // 根据公式计算
      } else {
        // 色彩计算
        ThemeColorSpecify.methods.reGenerateColorDerive(color);
      }
      this.onClickColorBlock(color);
    },
    computeBgColorHex() {
      let nodes = document.querySelectorAll('.hexString');
      for (let i = 0, len = nodes.length; i < len; i++) {
        let node = nodes[i],
          value = node.getAttribute('value'),
          hexString = '';
        if (value) {
          let color = tinycolor(value);
          if (color.isValid()) {
            hexString = color.toHexString();
            let isLightColor = color.isLight();
            node.innerHTML = `<label style="color:${isLightColor ? '#000' : '#fff'}">${hexString}</label>`;
            node.previousElementSibling.style.color = isLightColor ? '#000' : '#fff';
          } else {
            let _color = getComputedStyle(node.parentElement)['backgroundColor'];
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
              node.innerHTML = `<label style="color:${isLightColor ? '#000' : '#fff'}">${hexString}</label>`;
              node.previousElementSibling.style.color = isLightColor ? '#000' : '#fff';
            }
          }
        }
      }
    }
  },

  updated() {
    let _this = this;
    setTimeout(() => {
      _this.computeBgColorHex();
    }, 500);
  }
};
</script>
