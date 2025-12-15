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
    prop: String
  },
  components: { VariableDefineTemplate },
  data() {
    return {
      variableInfo: {
        value: this.formData[this.prop],
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
          ['流程名称', '流程ID', '流程编号']
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
          this.formData[this.prop] = newValue.value;
        } else {
          this.formData[this.prop] = '';
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
