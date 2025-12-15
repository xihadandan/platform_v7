<template>
  <VariableDefineTemplate
    ref="template"
    v-model="variableInfo"
    :variableTreeData="variableTreeData"
    :editable="true"
    :enableSysVar="false"
    :loadTreeData="loadDyformTreeData"
    showSnapshot
  ></VariableDefineTemplate>
</template>

<script>
import VariableDefineTemplate from '@dyform/app/web/widget/commons/variable-define-template.vue';
export default {
  props: {
    formData: {
      type: Object,
      default() {
        return {};
      }
    },
    value: {
      type: String
    }
  },
  components: { VariableDefineTemplate },
  data() {
    return {
      variableInfo: {
        value: this.value,
        replaceLabel: { search: '表单字段.', replace: '' }
      },
      variableTreeData: [
        this.getSimpleLabelVariables(
          {
            title: '日期变量',
            key: 'dateVar',
            value: 'dateVar',
            selectable: false,
            children: []
          },
          ['年', '月', '周', '日', '时', '分', '秒', '简年', '大写年', '大写月', '大写日']
        ),
        this.getSimpleLabelVariables(
          {
            title: '流程变量',
            key: 'workflowVar',
            value: 'workflowVar',
            selectable: false,
            children: []
          },
          ['流程名称', '流程ID', '流程编号', '发起人姓名', '发起人所在部门名称', '发起人所在部门名称全路径']
        ),
        this.getSimpleLabelVariables({
          title: '表单变量',
          key: 'dyformVar',
          value: 'dyformVar',
          selectable: false,
          isLeaf: false
        })
      ]
    };
  },
  watch: {
    variableInfo: {
      deep: true,
      handler: function (newValue) {
        if (newValue && newValue.value) {
          this.$emit('input', newValue.value);
          this.$emit('update:modelValue', newValue.value);
        } else {
          this.$emit('input', '');
          this.$emit('update:modelValue', '');
        }
      }
    }
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
        return this.fetchDyformFieldTreeData(dataRef);
      }
      return Promise.resolve();
    },
    fetchDyformTreeData(dataRef) {
      const _this = this;
      let flowInfo = _this.getSelectedFlowInfo();
      return $axios
        .get(
          `/proxy/api/workflow/definition/listFormDefinition?flowCategoryUuids=${flowInfo.flowCategoryUuids}&flowDefIds=${flowInfo.flowDefIds}`,
          { params: { moduleId: this.formData.moduleId, vJson: true } }
        )
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
    getSelectedFlowInfo() {
      let _this = this;
      let flowInfo = {
        flowCategoryUuids: [],
        flowDefIds: []
      };
      let formData = _this.formData;
      let contentConfig = formData.contentConfig;
      if (!contentConfig) {
        return false;
      }
      let values = contentConfig.values || [];
      if (contentConfig.type == 'all') {
        return flowInfo;
      }
      if (contentConfig.type == 'flow') {
        for (let index = 0; index < values.length; index++) {
          let value = values[index];
          if (value && value.startsWith('FLOW_CATEGORY_')) {
            let flowCategoryUuid = value.replace('FLOW_CATEGORY_', '');
            if (flowInfo.flowCategoryUuids.indexOf(flowCategoryUuid) == -1) {
              flowInfo.flowCategoryUuids.push(flowCategoryUuid);
            }
          } else if (value) {
            if (flowInfo.flowDefIds.indexOf(value) == -1) {
              flowInfo.flowDefIds.push(value);
            }
          }
        }
      }
      if (contentConfig.type == 'task') {
        for (let index = 0; index < values.length; index++) {
          let value = values[index];
          if (value && value.indexOf(':') != -1) {
            let flowDefId = value.split(':')[0];
            if (flowInfo.flowDefIds.indexOf(flowDefId) == -1) {
              flowInfo.flowDefIds.push(flowDefId);
            }
          }
        }
      }
      return flowInfo;
    },
    fetchDyformFieldTreeData(dataRef) {
      return $axios
        .post(`/proxy/api/dyform/definition/getFormDefinition?formUuid=${dataRef.key}&justDataAndDef=false`)
        .then(({ data: result }) => {
          if (result.fields) {
            let children = [];
            for (let key in result.fields) {
              let field = result.fields[key];
              children.push({
                title: field.displayName,
                key: field.displayName,
                value: '${表单字段.' + field.displayName + '}',
                isLeaf: true
              });
            }
            dataRef.children = children;
            this.variableTreeData = [...this.variableTreeData];
          }
        })
        .catch(error => {});
    },
    update(value) {
      this.$refs.template.update({
        value
      });
    }
  }
};
</script>

<style></style>
