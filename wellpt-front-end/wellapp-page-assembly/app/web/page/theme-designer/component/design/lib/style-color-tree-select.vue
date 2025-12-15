<template>
  <span :style="vStyle" class="style-color-select">
    <a-input-group compact @mouseenter="onmouseenter" @mouseleave="onmouseleave">
      <a-button style="padding: 4px" v-show="showPreview">
        <i
          :style="{
            outline: '1px solid #dddddd',
            borderRadius: '2px',
            width: '21px',
            height: '21px',
            backgroundColor: code ? 'var(' + code + ')' : 'transparent'
          }"
        ></i>
      </a-button>
      <template v-if="displayModal">
        <a-input
          :style="{
            width: `${showPreview ? 'calc(100% - 30px)' : '100%'}`
          }"
          v-model="code"
          :readOnly="true"
          @click.stop="modalVisible = true"
        >
          <template slot="suffix">
            <a-icon type="close-circle" theme="filled" v-if="hoverInput && code != undefined" @click="onChange(undefined)" />
            <a-icon :type="modalVisible ? 'folder-open' : 'folder'" v-else @click="modalVisible = true" />
          </template>
        </a-input>
      </template>

      <a-tree-select
        v-else
        v-model="code"
        show-search
        :placeholder="placeholder"
        :style="{
          width: `${showPreview ? 'calc(100% - 30px)' : '100%'}`
        }"
        :dropdown-style="{ maxHeight: '400px', overflow: 'auto', maxWidth: '200px', ...vStyle }"
        allow-clear
        dropdownClassName="style-color-tree-select ps__child--consume"
        @change="onChange"
        :getPopupContainer="getPopupContainer()"
        ref="colorTreeSelect"
      >
        <a-tree-select-node key="themeColor" value="themeColor" title="主题色" :selectable="false">
          <a-tree-select-node :key="colorConfig.themeColor.classify[0].code" :value="colorConfig.themeColor.classify[0].code">
            <template slot="title">
              <i
                class="color-bk"
                :style="{
                  backgroundColor: 'var(' + colorConfig.themeColor.classify[0].code + ')'
                }"
              ></i>
              {{ colorConfig.themeColor.classify[0].code }}
              <div class="remark" style="padding-left: 16px; color: #a8a8a8">基础主题色</div>
            </template>
          </a-tree-select-node>
          <a-tree-select-node v-for="(derive, i) in colorConfig.themeColor.classify[0].derive" :key="derive.code" :value="derive.code">
            <label slot="title">
              <i
                class="color-bk"
                :style="{
                  [derive.code]: derive.value,
                  backgroundColor: 'var(' + derive.code + ')'
                }"
              ></i>
              {{ derive.code }}
              <div class="remark" v-if="derive.remark" style="padding-left: 16px; color: #a8a8a8">
                {{ derive.remark }}
              </div>
            </label>
          </a-tree-select-node>
        </a-tree-select-node>
        <template v-for="(key, k) in ['neutralColor', 'functionColor']">
          <a-tree-select-node :key="key" :value="key" :title="colorConfig[key].title" :selectable="false">
            <a-tree-select-node
              v-for="(classify, i) in colorConfig[key].classify"
              :title="classify.title"
              :selectable="false"
              :key="key + 'TreeNode' + i"
              :value="key + 'TreeNode' + i"
            >
              <a-tree-select-node v-for="(derive, j) in classify.derive" :key="derive.code" :value="derive.code">
                <template slot="title">
                  <i
                    class="color-bk"
                    :style="{
                      [derive.code]: derive.value,
                      backgroundColor: 'var(' + derive.code + ')'
                    }"
                  ></i>
                  {{ derive.code }}
                  <div class="remark" v-if="derive.remark" style="padding-left: 16px; color: #a8a8a8">
                    {{ derive.remark }}
                  </div>
                </template>
              </a-tree-select-node>
            </a-tree-select-node>
          </a-tree-select-node>
        </template>
      </a-tree-select>
    </a-input-group>
    <a-modal v-model="modalVisible" :getContainer="getModalContainer" :width="700" :footer="null">
      <template slot="title">
        <div style="height: 26px">
          选择颜色
          <template v-if="code != undefined">
            :
            <a-tag closable @close="onChange(undefined)">
              {{ code }}
            </a-tag>
          </template>
        </div>
      </template>

      <Scroll style="height: 500px; padding-right: 16px">
        <a-collapse :defaultActiveKey="['themePrimaryColor']" :bordered="false" style="background: #fff" expandIconPosition="right">
          <a-collapse-panel header="主题色" key="themePrimaryColor">
            <a-row :gutter="[16, 16]" style="padding: 12px 12px">
              <a-col
                v-for="(derive, i) in colorConfig.themeColor.classify[0].derive"
                :span="12"
                :key="'themePcolor_' + i"
                @click="e => onClickCardGrid(derive.code)"
              >
                <div :style="{ position: 'relative', height: '70px', outline: '1px solid #f0f0f0', padding: '12px' }">
                  <a-checkbox :checked="derive.code == code" style="position: absolute; top: 5px; right: 5px" />
                  <label style="display: flex; align-items: center">
                    <i
                      class="color-bk"
                      :style="{
                        [derive.code]: derive.value,
                        backgroundColor: 'var(' + derive.code + ')'
                      }"
                    ></i>
                    {{ derive.code }}
                    <div class="remark" v-if="derive.remark" style="padding-left: 16px; color: #a8a8a8">
                      {{ derive.remark }}
                    </div>
                  </label>
                </div>
              </a-col>
            </a-row>
          </a-collapse-panel>
          <template v-for="(key, k) in ['neutralColor', 'functionColor']">
            <template v-for="(classify, i) in colorConfig[key].classify">
              <a-collapse-panel :header="classify.title" :key="key + i">
                <a-row :gutter="[16, 16]" style="padding: 12px 12px">
                  <a-col
                    v-for="(derive, i) in classify.derive"
                    :span="12"
                    :key="'themePcolor_' + key + i"
                    @click="e => onClickCardGrid(derive.code)"
                  >
                    <a-card
                      size="small"
                      :hoverable="true"
                      :bodyStyle="{ position: 'relative', height: '50px', display: 'flex', alignItem: 'center' }"
                    >
                      <a-checkbox :checked="derive.code == code" style="position: absolute; top: 5px; right: 8px" />
                      <label style="display: flex; align-items: center">
                        <i
                          class="color-bk"
                          :style="{
                            [derive.code]: derive.value,
                            backgroundColor: 'var(' + derive.code + ')'
                          }"
                        ></i>
                        {{ derive.code }}
                        <div class="remark" v-if="derive.remark" style="padding-left: 16px; color: #a8a8a8">
                          {{ derive.remark }}
                        </div>
                      </label>
                    </a-card>
                  </a-col>
                </a-row>
              </a-collapse-panel>
            </template>
          </template>
        </a-collapse>
      </Scroll>
    </a-modal>
  </span>
</template>
<style lang="less">
.style-color-select {
  text-align: left;
  .ant-select-tree-title {
    display: inline-flex;
    align-items: center;
    flex-wrap: wrap;
  }
  .color-bk {
    width: 16px;
    height: 16px;
    outline: 1px solid #dddddd;
    border-radius: 2px;
    display: inline-block;
    margin-right: 5px;
  }
}
.style-color-tree-select {
  .ant-select-tree-title {
    display: inline-flex;
    align-items: center;
    flex-wrap: wrap;
  }
  .color-bk {
    width: 16px;
    height: 16px;
    outline: 1px solid #dddddd;
    border-radius: 2px;
    display: inline-block;
    margin-right: 5px;
  }
}
</style>
<script type="text/babel">
import { getPopupContainerNearestPs } from '@framework/vue/utils/function.js';
export default {
  name: 'StyleColorTreeSelect',
  props: {
    colorConfig: Object,
    value: String,
    popupContainer: Function,
    displayModal: {
      type: Boolean,
      default: false
    },
    showPreview: {
      type: Boolean,
      default: true
    },
    placeholder: String,
    dropdownStyle: {
      type: Object,
      default: () => {}
    },
    autoDisplay: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    vStyle() {
      let colorVars;
      if (this.autoDisplay) {
        colorVars = { width: '100%', ...this.dropdownStyle };
      } else {
        colorVars = { display: 'inline-block', width: '100%', ...this.dropdownStyle };
      }
      colorVars[this.colorConfig.themeColor.classify[0].code] = this.colorConfig.themeColor.classify[0].value;
      let derive = this.colorConfig.themeColor.classify[0].derive;
      for (let i = 0, len = derive.length; i < len; i++) {
        colorVars[derive[i].code] = derive[i].value;
      }
      for (let key of ['neutralColor', 'functionColor']) {
        let classify = this.colorConfig[key].classify;
        for (let i = 0, len = classify.length; i < len; i++) {
          for (let j = 0, jlen = classify[i].derive.length; j < jlen; j++) {
            colorVars[classify[i].derive[j].code] = classify[i].derive[j].value;
          }
        }
      }

      return colorVars;
    }
  },
  watch: {
    value: {
      handler(value, oldValue) {
        if (this.$listeners.input != undefined) {
          this.code = value;
        }
      }
    }
  },
  data() {
    return { code: this.value, modalVisible: false, hoverInput: false };
  },
  methods: {
    clearSelector() {
      //  inject: ['vcTreeSelect'],
      // onSelectorBlur()
      // this.$refs.colorTreeSelect.$children[0].triggerChange([], []);
      // this.$refs.colorTreeSelect.$children[0].onDropdownVisibleChange(false);
      // this.$refs.colorTreeSelect.$children[0].onSelectorClear();
    },
    onmouseenter() {
      this.hoverInput = true;
    },
    onmouseleave() {
      this.hoverInput = false;
    },
    getModalContainer() {
      return this.$el;
    },
    getPopupContainer() {
      return typeof this.popupContainer == 'function' ? this.popupContainer : getPopupContainerNearestPs.apply(this, Array.from(arguments));
    },
    onClickCardGrid(value) {
      this.onChange(this.code == value ? undefined : value);
    },
    onChange(value, label, extra) {
      let colorValue = '';
      if (extra && extra.triggerNode) {
        const styles = extra.triggerNode.$el.querySelector('.color-bk').getAttribute('style');
        colorValue = styles.split(' ')[1];
        colorValue = colorValue.substring(0, colorValue.length - 1);
      }
      this.$emit('input', value);
      this.$emit('change', value, colorValue);
      this.code = value;
    }
  }
};
</script>
