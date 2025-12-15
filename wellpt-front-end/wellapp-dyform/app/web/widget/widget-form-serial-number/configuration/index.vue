<template>
  <div>
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      :label-col="{ span: 8 }"
      labelAlign="left"
      :wrapper-col="{ span: 15, style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" />
          <FieldLengthInput :widget="widget" />
          <a-form-model-item label="应用于">
            <FieldApplySelect v-model="widget.configuration.applyToDatas" />
          </a-form-model-item>
          <a-form-model-item label="默认状态">
            <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState">
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
              <a-radio-button value="hidden">隐藏</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="不可编辑状态">
            <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState">
              <a-radio-button value="label">纯文本</a-radio-button>
              <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="流水号生成方式" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
            <a-radio-group size="small" v-model="widget.configuration.generateMode">
              <a-radio-button value="auto">自动生成</a-radio-button>
              <a-radio-button value="custom">选择规则生成</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <div v-if="widget.configuration.generateMode == 'auto'">
            <a-form-model-item label="指定流水号">
              <a-tree-select
                :allowClear="true"
                v-model="widget.configuration.serialNumberDefinitionId"
                :defaultValue="widget.configuration.serialNumberDefinitionId"
                treeDataSimpleMode
                :replaceFields="{ key: 'id', title: 'name', value: 'data' }"
                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                :multiple="false"
                show-search
                :filterTreeNode="filterTreeNode"
                :dropdownMatchSelectWidth="true"
                :tree-data="serialNumberDefinitionTreeData"
              ></a-tree-select>
            </a-form-model-item>
            <a-form-model-item label="补号设置">
              <a-radio-group size="small" v-model="widget.configuration.serialNumberFill">
                <a-radio-button value="none">不补号</a-radio-button>
                <a-radio-button value="auto">自动补号</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="计数占用设置">
              <a-radio-group size="small" v-model="widget.configuration.occupyMode">
                <a-radio-button value="auto">生成即占用</a-radio-button>
                <a-radio-button value="save">保存即占用</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
          </div>
          <div v-else>
            <a-form-model-item label="指定流水号">
              <a-tree-select
                :allowClear="true"
                v-model="widget.configuration.serialNumberDefinitionId"
                :defaultValue="widget.configuration.serialNumberDefinitionId"
                treeDataSimpleMode
                :replaceFields="{ key: 'id', title: 'name', value: 'data' }"
                :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                :multiple="true"
                show-search
                :filterTreeNode="filterTreeNode"
                :dropdownMatchSelectWidth="true"
                :tree-data="serialNumberDefinitionTreeData"
              ></a-tree-select>
            </a-form-model-item>
            <a-form-model-item label="补号设置" :label-col="{ span: 5 }" :wrapper-col="{ span: 18, style: { textAlign: 'right' } }">
              <a-radio-group size="small" v-model="widget.configuration.serialNumberFill">
                <a-radio-button value="none">不补号</a-radio-button>
                <a-radio-button value="auto">自动补号</a-radio-button>
                <a-radio-button value="custom">选择补号</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="计数占用设置">
              <a-radio-group size="small" v-model="widget.configuration.occupyMode">
                <a-radio-button value="auto">生成即占用</a-radio-button>
                <a-radio-button value="save">保存即占用</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="窗口设置"></a-form-model-item>
            <a-form-model-item label="弹窗标题" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
              <a-input v-model="widget.configuration.dialogTitle">
                <template slot="addonAfter">
                  <WI18nInput :widget="widget" :designer="designer" code="dialogTitle" v-model="widget.configuration.dialogTitle" />
                </template>
              </a-input>
            </a-form-model-item>
            <a-form-model-item
              label="流水号分类选择项标题"
              :label-col="{ span: 9 }"
              :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
            >
              <a-input v-model="widget.configuration.dialogSnDefSelectLabel">
                <template slot="addonAfter">
                  <WI18nInput
                    :widget="widget"
                    :designer="designer"
                    code="dialogSnDefSelectLabel"
                    v-model="widget.configuration.dialogSnDefSelectLabel"
                  />
                </template>
              </a-input>
            </a-form-model-item>
            <a-form-model-item label="流水号选择项标题" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
              <a-input v-model="widget.configuration.dialogSnSelectLabel">
                <template slot="addonAfter">
                  <WI18nInput
                    :widget="widget"
                    :designer="designer"
                    code="dialogSnSelectLabel"
                    v-model="widget.configuration.dialogSnSelectLabel"
                  />
                </template>
              </a-input>
            </a-form-model-item>
            <a-form-model-item label="流水号标题" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
              <a-input v-model="widget.configuration.dialogSnLabel">
                <template slot="addonAfter">
                  <WI18nInput :widget="widget" :designer="designer" code="dialogSnLabel" v-model="widget.configuration.dialogSnLabel" />
                </template>
              </a-input>
            </a-form-model-item>
          </div>
          <a-form-model-item label="流水号重复提示" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
            <a-input v-model="widget.configuration.serialNumberTips">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" code="serialNumberTips" v-model="widget.configuration.serialNumberTips" />
              </template>
            </a-input>
          </a-form-model-item>
          <!-- 按规则生成流水号，才有提示语 -->
          <a-form-model-item
            label="输入框提示语"
            :label-col="{ span: 9 }"
            :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
            v-if="widget.configuration.generateMode == 'custom'"
          >
            <a-input placeholder="输入框提示语" v-model="widget.configuration.placeholder">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" code="placeholder" v-model="widget.configuration.placeholder" />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item
            label="显示边框"
            class="item-lh"
            :wrapper-col="{ style: { textAlign: 'right' } }"
            v-if="designer.terminalType == 'mobile'"
          >
            <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.uniConfiguration.bordered" />
          </a-form-model-item>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration
            :widget="widget"
            :trigger="true"
            :required="true"
            :unique="true"
            :regExp="true"
            :validatorFunction="true"
          />
        </a-tab-pane>
        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import { each as forEach, isArray, isEmpty } from 'lodash';
export default {
  name: 'WidgetFormSerialNumberConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true }
      },
      serialNumberDefinitionTreeData: []
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', { bordered: false });
    }
  },
  methods: {
    fetchSerialNumberDefinitionTTreeData(treeNode) {
      var _this = this;
      return new Promise(resolve => {
        setTimeout(() => {
          $axios
            .post('/json/data/services', {
              serviceName: 'snSerialNumberDefinitionFacadeService',
              methodName: 'loadSerialNumberTree'
            })
            .then(({ data }) => {
              let nodes = data.data;
              // 自动生成时流水号不可选择分类
              if (_this.widget.configuration.generateMode == 'auto') {
                forEach(nodes, function (node) {
                  if (node.children.length) {
                    // 有子结构的项，不可选
                    node.selectable = false;
                  }
                });
              }
              _this.serialNumberDefinitionTreeData = nodes;
            });
          resolve();
        }, 300);
      });
    },

    // 生成后端的功能元素数据
    getFunctionElements(widget) {
      let functionElements = {};
      let definitionIds = [];
      if (isArray(widget.configuration.serialNumberDefinitionId)) {
        definitionIds = widget.configuration.serialNumberDefinitionId;
      } else if (!isEmpty(widget.configuration.serialNumberDefinitionId)) {
        definitionIds.push(widget.configuration.serialNumberDefinitionId);
      }
      let functions = [];
      definitionIds.forEach(definitionId => {
        functions.push({
          id: definitionId,
          name: '新版流水号定义_' + definitionId,
          functionType: 'snSerialNumberDefinition',
          ref: true
        });
      });
      functionElements[widget.id] = functions;
      return functionElements;
    },
    filterTreeNode(inputValue, treeNode) {
      return treeNode.data.props.title.toLowerCase().includes(inputValue.toLowerCase());
    }
  },
  mounted() {
    this.fetchSerialNumberDefinitionTTreeData();
  }
};
</script>
