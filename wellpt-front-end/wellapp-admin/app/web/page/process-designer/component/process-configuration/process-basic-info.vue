<template>
  <a-form-model
    class="basic-info"
    :model="processDefinition"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 8 }"
    :wrapper-col="{ span: 16, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-collapse accordion :bordered="false" expandIconPosition="right" defaultActiveKey="1">
      <a-collapse-panel key="1" header="基本信息">
        <a-form-model-item label="名称" prop="name">
          <a-input v-model="processDefinition.name" />
        </a-form-model-item>
        <a-form-model-item label="ID" prop="id">
          <a-input v-model="processDefinition.id" :readOnly="processDefinition.uuid != null" />
        </a-form-model-item>
        <a-form-model-item label="编号" prop="code">
          <a-input v-model="processDefinition.code" />
        </a-form-model-item>
        <a-form-model-item label="状态" prop="enabled">
          <a-switch v-model="processDefinition.enabled" checked-children="启用" un-checked-children="禁用" />
        </a-form-model-item>
        <a-form-model-item prop="businessId">
          <template slot="label">
            <a-space>
              所属业务
              <a-popover>
                <template slot="content">
                  用于关联相同业务的业务事项定义，关联后业务
                  <br />
                  事项定义表单的数据才能在当前业务流程定义中引用
                </template>
                <a-icon type="info-circle" />
              </a-popover>
            </a-space>
          </template>
          <a-tree-select
            v-model="processDefinition.businessId"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="businessTreeData"
            placeholder="请选择"
            tree-default-expand-all
          ></a-tree-select>
        </a-form-model-item>
        <a-form-model-item label="事件监听" prop="listener">
          <a-select
            v-model="processDefinition.listener"
            mode="multiple"
            show-search
            style="width: 100%"
            :filter-option="filterSelectOption"
          >
            <a-select-option v-for="d in listenerOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="业务标签" prop="tagId">
          <a-select v-model="processDefinition.tagId" show-search style="width: 100%" :filter-option="filterSelectOption">
            <a-select-option v-for="d in bizTagOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="备注" prop="remark">
          <a-textarea v-model="processDefinition.remark" />
        </a-form-model-item>
      </a-collapse-panel>
      <a-collapse-panel key="2" header="业务主体">
        <ProcessEntitySettings
          :processDefinition="processDefinition"
          :entityConfig="processDefinition.entityConfig"
        ></ProcessEntitySettings>
      </a-collapse-panel>
      <a-collapse-panel v-if="false" key="3" header="业务流程办理单">
        <ProcessFormSettings :processDefinition="processDefinition" :formConfig="processDefinition.formConfig"></ProcessFormSettings>
      </a-collapse-panel>
    </a-collapse>
  </a-form-model>
</template>

<script>
import ProcessEntitySettings from './process-entity-settings.vue';
import ProcessFormSettings from './process-form-settings.vue';
export default {
  props: {
    processDefinition: Object
  },
  components: { ProcessEntitySettings, ProcessFormSettings },
  inject: ['filterSelectOption', 'designer'],
  data() {
    if (this.processDefinition.entityConfig == null) {
      this.$set(this.processDefinition, 'entityConfig', {});
    }
    if (this.processDefinition.formConfig == null) {
      this.$set(this.processDefinition, 'formConfig', { configType: '2' });
    }
    return {
      businessTreeData: [],
      listenerOptions: [],
      bizTagOptions: [],
      rules: {
        name: {
          required: true,
          message: '名称不能为空！'
        },
        id: {
          required: true,
          message: 'ID不能为空！'
        },
        businessId: {
          required: true,
          message: '所属业务不能为空！'
        }
      }
    };
  },
  watch: {
    'processDefinition.name': {
      handler(newVal, oldVal) {
        this.designer.updateRootNodeTitle(newVal);
      }
    }
  },
  mounted() {
    this.loadBusinessTreeData();
    this.handleListenerSearch();
    this.handleBizTagSearch();
  },
  methods: {
    validate() {
      return this.$refs.basicForm.validate();
    },
    loadBusinessTreeData() {
      let _this = this;
      let convertATreeSelectData = function (nodes) {
        nodes.forEach(node => {
          node.label = node.name;
          node.value = (node.data && node.data.id) || node.id;
          node.selectable = node.type == 'business';
          if (node.children) {
            convertATreeSelectData(node.children);
          }
        });
        return nodes;
      };
      this.$axios
        .post('/json/data/services', {
          serviceName: 'bizBusinessFacadeService',
          methodName: 'getBusinessTree'
        })
        .then(({ data }) => {
          if (data.data) {
            _this.businessTreeData = convertATreeSelectData([data.data]);
          }
        });
    },
    handleListenerSearch(value = '') {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'bizProcessDefinitionFacadeService',
          queryMethod: 'listBizProcessListenerSelectData',
          searchValue: value
        })
        .then(({ data }) => {
          if (data.results) {
            _this.listenerOptions = data.results;
          }
        });
    },
    handleBizTagSearch(value = '') {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'bizTagFacadeService',
          searchValue: value
        })
        .then(({ data }) => {
          if (data.results) {
            _this.bizTagOptions = data.results;
          }
        });
    }
  }
};
</script>

<style></style>
