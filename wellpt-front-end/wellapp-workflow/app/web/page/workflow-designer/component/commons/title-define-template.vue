<template>
  <VariableDefineTemplate
    ref="template"
    v-model="variableInfo"
    :variableTreeData="variableTreeData"
    :editable="true"
    :enableSysVar="enableSysVar"
    :loadTreeData="loadDyformTreeData"
    showSnapshot
    :triggerType="triggerType"
    :title="title"
    :valueExpressionIsLable="true"
    height="auto"
  >
    <template v-if="triggerType == 'custom'" slot="trigger">
      <slot name="trigger"></slot>
    </template>
    <div slot="modalTop" style="margin-bottom: var(--w-padding-xs)">
      <a-alert v-if="alert" :message="alert" type="info" class="pt-alert" show-icon />
      <slot name="modalTop"></slot>
    </div>
  </VariableDefineTemplate>
</template>

<script>
import { deepClone } from '@framework/vue/utils/util';
import VariableDefineTemplate from '@dyform/app/web/widget/commons/variable-define-template.vue';
export default {
  name: 'TitleDefineComponent',
  inject: ['designer', 'workFlowData'],
  props: {
    formData: {
      type: Object,
      default() {
        return {};
      }
    },
    enableSysVar: {
      type: Boolean,
      default: false
    },
    prop: String,
    hasDateVar: {
      type: Boolean,
      default: false
    },
    hasWorkflowVar: {
      type: Boolean,
      default: true
    },
    hasDyformVar: {
      type: Boolean,
      default: true
    },
    dateVar: {
      type: Object,
      default() {
        return {
          type: 'union',
          var: []
        };
      }
    },
    workflowVar: {
      type: Object,
      default() {
        return {
          type: 'union',
          var: []
        };
      }
    },
    // 显示表达式快照
    showSnapshot: {
      type: Boolean,
      default: false
    },
    // 自定义触发事件
    triggerType: {
      type: String,
      default: 'custom'
    },
    title: {
      type: String,
      default: '设置'
    },
    alert: {
      type: String,
      default: ''
    },
    isSubflow: {
      type: Boolean,
      default: false
    }
  },
  components: { VariableDefineTemplate },
  data() {
    return {
      variableInfo: { value: this.formData[this.prop] },
      variableTreeData: []
    };
  },
  computed: {
    formID() {
      let id = null;
      if (this.workFlowData && this.workFlowData.property && this.workFlowData.property.formID) {
        id = this.workFlowData.property.formID;
      }
      return id;
    },
    initVariableTreeData() {
      let variableTreeData = [];
      if (this.hasDateVar) {
        variableTreeData.push(
          this.getSimpleLabelVariables(
            {
              title: '日期变量',
              key: 'dateVar',
              value: 'dateVar',
              selectable: false,
              children: []
            },
            _[this.dateVar.type || 'union'](
              ['年', '月', '周', '日', '时', '分', '秒', '简年', '大写年', '大写月', '大写日'],
              this.dateVar.var
            )
          )
        );
      }
      if (this.hasWorkflowVar) {
        const children = [
          '流程名称',
          '流程ID',
          '流程编号',
          '发起人姓名',
          '发起人所在部门名称',
          '发起人所在部门名称全路径',
          '年',
          '简年',
          '月',
          '日',
          '时',
          '分',
          '秒',
          '发起年',
          '发起简年',
          '发起月',
          '发起日',
          '发起时',
          '发起分',
          '发起秒'
        ];
        if (this.isSubflow) {
          children.unshift('子流程实例办理人');
        }
        variableTreeData.push(
          this.getSimpleLabelVariables(
            {
              title: '流程变量',
              key: 'workflowVar',
              value: 'workflowVar',
              selectable: false,
              children: []
            },
            _[this.workflowVar.type || 'union'](children, this.workflowVar.var)
          )
        );
      }
      if (this.hasDyformVar) {
        variableTreeData.push(
          this.getSimpleLabelVariables({
            title: '表单变量',
            key: this.formID || 'dyform', // 表单id
            value: 'dyformVar',
            selectable: false,
            isLeaf: false,
            dataType: 'dyform'
          })
        );
      }
      return variableTreeData;
    }
  },
  watch: {
    variableInfo: {
      deep: true,
      handler: function (newValue) {
        if (newValue && newValue.value) {
          this.formData[this.prop] = newValue.value;
        } else {
          this.formData[this.prop] = '';
        }
        this.$emit('input', this.formData[this.prop]);
        this.$emit('change', { prop: this.prop, value: this.formData[this.prop] });
      }
    },
    initVariableTreeData: {
      immediate: true,
      deep: true,
      handler: function (val) {
        this.variableTreeData = deepClone(val);
      }
    }
  },
  mounted() {
    if (this.hasDyformVar && this.designer && this.designer.dyformVarList) {
      this.$watch(
        () => {
          return this.designer.dyformVarList;
        },
        (newValue, oldValue) => {
          if (newValue && newValue.length) {
            let varMap = {};
            newValue.map(item => {
              varMap[item.value] = item;
            });
            if (this.$refs.template) {
              this.variableInfo.variables = this.$refs.template.parseValueTags(this.formData[this.prop], varMap);
              this.update();
            }
          }
        },
        { immediate: true }
      );
    }
    this.$watch(
      () => {
        return this.formData[this.prop];
      },
      (newValue, oldValue) => {
        this.update(newValue);
      }
    );
  },
  methods: {
    getSimpleLabelVariables(parentNode, labels = []) {
      for (let i = 0, len = labels.length; i < len; i++) {
        parentNode.children.push({
          title: labels[i],
          key: labels[i],
          value: '${' + labels[i] + '}',
          isLeaf: true
        });
      }
      return parentNode;
    },
    loadDyformTreeData(treeNode) {
      let dataRef = treeNode.dataRef || {};
      if (dataRef.children) {
        return Promise.resolve();
      }
      // 表单变量
      if (dataRef.key == 'dyformVar') {
        return this.fetchDyformTreeData(dataRef);
      } else if (dataRef.dataType == 'dyform') {
        if (this.formID) {
          return this.fetchDyformFieldTreeData(dataRef);
        } else if (this.designer) {
          dataRef.children = this.getFieldVarByDesigner();
        }
      }
      return Promise.resolve();
    },
    fetchDyformTreeData(dataRef) {
      return $axios
        .get(`/proxy/api/dyform/definition/queryModuleFormDefinition`, { params: { moduleId: this.formData.moduleId, vJson: true } })
        .then(({ data: result }) => {
          if (result.data) {
            dataRef.children = result.data.map(item => ({
              title: item.name,
              key: item.uuid,
              value: item.uuid,
              selectable: false,
              isLeaf: false,
              dataType: 'dyform'
            }));
            this.variableTreeData = [...this.variableTreeData];
          }
        })
        .catch(error => {});
    },
    fetchDyformFieldTreeData(dataRef) {
      return $axios
        .post(`/proxy/api/dyform/definition/getFormDefinition?formUuid=${dataRef.key}&justDataAndDef=false`)
        .then(({ data: result }) => {
          if (result.fields) {
            let children = [];
            for (let key in result.fields) {
              let field = result.fields[key];
              children.push({ title: field.displayName, key: field.name, value: '${' + field.name + '}', isLeaf: true });
            }
            dataRef.children = children;
            this.variableTreeData = [...this.variableTreeData];
          }
        })
        .catch(error => {});
    },
    getFieldVarByDesigner() {
      let fieldVarNode = [],
        widgetFields = this.designer.WidgetInputs;
      if (!widgetFields) {
        widgetFields = this.designer.WidgetFormInputs;
      }
      if (designer.WidgetFormInputNumbers) {
        widgetFields.concat(designer.WidgetFormInputNumbers);
      }
      if (widgetFields) {
        for (const item of widgetFields) {
          if (item.configuration && item.configuration.code) {
            fieldVarNode.push({
              title: item.title,
              key: item.id,
              value: '${dyform.' + item.configuration.code + '}',
              isLeaf: true
            });
          }
        }
      }
      return fieldVarNode;
    },
    update(value) {
      let v = value || this.formData[this.prop];
      this.$refs.template.update({
        value: v,
        variables: this.variableInfo.variables
      });
    }
  }
};
</script>

<style lang="less" scoped>
.variable-define-template {
  display: inline-block;
}
</style>
