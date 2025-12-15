<template>
  <div class="row-state-render-configuration" v-if="widget.configuration.defaultDisplayState == 'table'">
    <a-form-model-item>
      <template slot="label">
        <a-tooltip placement="top">
          <template slot="title">
            <ul style="padding-inline-start: 20px; margin-block-end: 0px">
              <li>根据行数据值渲染行样式</li>
            </ul>
          </template>
          行样式渲染
          <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
        </a-tooltip>
        <a-checkbox v-model="widget.configuration.renderRowByState.enable" />
      </template>
      <WidgetDesignDrawer ref="drawerRef" :id="'widgetTableRowStateRender' + widget.id" title="行样式渲染设置" :designer="designer">
        <a-button type="link" size="small" v-if="widget.configuration.renderRowByState.enable">
          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          设置
        </a-button>
        <template slot="content">
          <a-form-model>
            <a-form-model-item>
              <template slot="label">行渲染模式</template>
              <a-radio-group v-model="widget.configuration.renderRowByState.type" button-style="solid" size="small">
                <a-radio-button value="default">默认</a-radio-button>
                <a-radio-button value="function">自定义代码</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <template v-if="widget.configuration.renderRowByState.type == 'default'">
              <div
                v-for="(record, i) in widget.configuration.renderRowByState.stateData"
                :key="'rowState_' + i"
                style="padding: 12px; outline: 1px solid #e8e8e8; border-radius: 4px; position: relative; margin-bottom: 12px"
              >
                <a-form-model-item :label-col="{ style: { width: 'calc(100% - 30px)' } }" :wrapper-col="{ style: { width: '30px' } }">
                  <template slot="label">
                    满足
                    <a-select
                      size="small"
                      :getPopupContainer="getPopupContainer()"
                      :options="[
                        { label: '全部', value: 'all' },
                        { label: '任一', value: 'any' }
                      ]"
                      style="width: 65px"
                      v-model="record.match"
                    />
                    条件时渲染
                    <a-button size="small" icon="plus-square" title="添加条件" type="link" @click="addCondition(i)">添加条件</a-button>
                  </template>
                  <a-button size="small" type="link" @click="widget.configuration.renderRowByState.stateData.splice(i, 1)" title="删除配置">
                    <Icon type="pticon iconfont icon-ptkj-shanchu" />
                  </a-button>
                </a-form-model-item>
                <a-form-model-item :wrapper-col="{ style: { width: '100%' } }">
                  <template v-if="record.conditions.length > 0">
                    <a-row type="flex" :key="'stateCon_' + i + t" style="margin-bottom: 5px" v-for="(item, t) in record.conditions">
                      <a-col flex="calc(100% - 30px)">
                        <a-input-group compact>
                          <a-select
                            :options="columnIndexOptions"
                            :style="{ width: '260px' }"
                            v-model="item.code"
                            show-search
                            :filter-option="filterOption"
                          ></a-select>
                          <a-select :options="operatorOptions" v-model="item.operator" :style="{ width: '120px' }" />
                          <a-input
                            v-model="item.value"
                            v-show="!['true', 'false'].includes(item.operator)"
                            :style="{ width: 'calc(100% - 380px)' }"
                          />
                        </a-input-group>
                      </a-col>
                      <a-col flex="25px">
                        <a-button
                          type="link"
                          size="small"
                          v-if="record.conditions.length > 1"
                          @click="record.conditions.splice(t, 1)"
                          title="删除条件"
                        >
                          <Icon type="pticon iconfont icon-ptkj-shanchu" />
                        </a-button>
                      </a-col>
                    </a-row>
                  </template>
                </a-form-model-item>
                <ColorSelectConfiguration
                  label="背景颜色"
                  v-model="record.style"
                  :onlyValue="true"
                  colorField="backgroundColor"
                  radioSize="small"
                ></ColorSelectConfiguration>
                <ColorSelectConfiguration
                  label="文字颜色"
                  v-model="record.style"
                  :onlyValue="true"
                  colorField="color"
                  radioSize="small"
                  radioStyle="solid"
                ></ColorSelectConfiguration>
                <a-form-model-item label="字体大小">
                  <a-input-number v-model="record.style.fontSize" :min="12" />
                  px
                </a-form-model-item>
                <a-form-model-item label="字重大小">
                  <a-select :options="fontWeightOptions" v-model="record.style.fontWeight" allowClear></a-select>
                </a-form-model-item>
              </div>
              <a-button :block="true" type="link" @click="addStateData">
                <Icon type="pticon iconfont icon-ptkj-jiahao" />
                添加渲染条件配置
              </a-button>
            </template>
            <template v-if="widget.configuration.renderRowByState.type == 'function'">
              <WidgetCodeEditor
                v-model="widget.configuration.renderRowByState.function"
                lang="js"
                width="100%"
                height="500px"
                :hideError="true"
              ></WidgetCodeEditor>
              <div style="margin-bottom: 4px">支持使用函数返回行样式, 入参传入的是row,index参数。</div>
              <div
                class="pt-tip-block"
                @click="e => onClickCopy(e, `return {backgroundColor: '', color: '', fontSize: 14, fontWeight: 'bold'};`)"
                style="margin-bottom: 4px; cursor: pointer"
                title="复制"
              >
                返回示例：return {backgroundColor: '', color: '', fontSize: 14, fontWeight: 'bold'}
              </div>
              <div class="flex f_wrap help-tip-content-list">
                <div v-for="(item, i) in tipParamList" style="margin-bottom: 4px; cursor: pointer">
                  {{ item.label }}
                  <a-tag @click="e => onClickCopy(e, item.value)" :title="'点击复制：' + item.value">{{ item.value }}</a-tag>
                </div>
              </div>
            </template>
          </a-form-model>
        </template>
      </WidgetDesignDrawer>
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone, copyToClipboard } from '@framework/vue/utils/util';
import ColorSelectConfiguration from '@pageAssembly/app/web/widget/commons/color-select-configuration.vue';
export default {
  name: 'RowStateRenderConfiguration',
  props: {
    widget: Object,
    designer: Object,
    configuration: Object,
    columnIndexOptions: Array
  },
  components: { ColorSelectConfiguration },
  computed: {},
  data() {
    return {
      operatorOptions: [
        { value: '>', label: '大于' },
        { value: '>=', label: '大于等于' },
        { value: '<', label: '小于' },
        { value: '<=', label: '小于等于' },
        { label: '等于', value: '==' },
        { label: '不等于', value: '!=' },
        { label: '为真', value: 'true' },
        { label: '为假', value: 'false' },
        { label: '包含于', value: 'in' },
        { label: '不包含于', value: 'not in' },
        { label: '包含', value: 'contain' },
        { label: '不包含', value: 'not contain' }
      ],
      fontWeightOptions: [
        { label: '默认值', value: 'normal' },
        { label: '粗体', value: 'bold' },
        // { label: '更粗', value: 'bolder' },
        { label: '更细', value: 'lighter' }
      ],
      tipParamList: [
        { label: '背景颜色 ', value: 'backgroundColor' },
        { label: '字体颜色 ', value: 'color' },
        { label: '字体大小 ', value: 'fontSize' },
        { label: '字体字重 ', value: 'fontWeight' }
      ]
    };
  },
  computed: {},
  beforeCreate() {},
  mounted() {},
  methods: {
    addCondition(index) {
      this.widget.configuration.renderRowByState.stateData[index].conditions.push({
        dataIndex: '',
        id: generateId(),
        operator: '',
        value: ''
      });
    },
    addStateData() {
      this.widget.configuration.renderRowByState.stateData.push({
        enable: true,
        match: 'all',
        id: generateId(),
        conditions: [
          {
            code: '',
            id: generateId(),
            operator: '',
            value: ''
          }
        ],
        style: {
          backgroundColor: '',
          color: '',
          fontSize: '',
          fontWeight: ''
        }
      });
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    getPopupContainer() {
      return triggerNode => triggerNode.closest('.ant-form-item');
    },
    onClickCopy(e, text) {
      let _this = this;
      copyToClipboard(text, e, function (success) {
        if (success) {
          // message不支持修改样式，代码编辑组件弹框widget-code-editor层级为2000，导致message提示框显示在遮罩下面
          _this.$message.success({
            content: '已复制'
          });
        }
      });
    }
  },
  watch: {}
};
</script>
