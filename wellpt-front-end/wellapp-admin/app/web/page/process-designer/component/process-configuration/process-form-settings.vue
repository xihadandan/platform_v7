<template>
  <div>
    <a-form-model-item label="设置方式">
      <a-radio-group size="small" v-model="formConfig.configType">
        <a-radio-button v-show="processDefinition.uuid" value="1">引用模板</a-radio-button>
        <a-radio-button value="2">自定义</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <template v-if="formConfig.configType == '1'">
      <a-form-model-item label="引用模板" prop="templateUuid">
        <ProcessDesignDrawer :id="drawerAddId" title="添加模板" :closeOpenDrawer="false">
          <a-button type="link" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
          <template slot="content">
            <slot name="processFormTemplateAdd">
              <ProcessFormTemplateConfiguration ref="processFormTemplateAdd"></ProcessFormTemplateConfiguration>
            </slot>
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onAddTemplateConfirmOk">确定</a-button>
          </template>
        </ProcessDesignDrawer>
        <ProcessDesignDrawer :id="drawerEditId" title="配置模板" :closeOpenDrawer="false">
          <a-button type="link" :style="{ paddingLeft: '7px' }">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            配置
          </a-button>
          <template slot="content">
            <slot name="processFormTemplateEdit">
              <ProcessFormTemplateConfiguration
                :templateUuid="formConfig.templateUuid"
                ref="processFormTemplateEdit"
              ></ProcessFormTemplateConfiguration>
            </slot>
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onEditTemplateConfirmOk">确定</a-button>
          </template>
        </ProcessDesignDrawer>
        <a-select
          v-model="formConfig.templateUuid"
          show-search
          style="width: 100%"
          :filter-option="filterSelectOption"
          @change="handleTemplateChange"
        >
          <a-select-option v-for="d in templateOptions" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
    </template>
    <slot name="processFormInfo">
      <ProcessFormInfo ref="processFormInfo" :formConfig="formConfig"></ProcessFormInfo>
    </slot>
  </div>
</template>

<script>
import ProcessFormInfo from './process-form-info.vue';
import ProcessDesignDrawer from '../process-design-drawer.vue';
import ProcessFormTemplateConfiguration from './process-form-template-configuration.vue';
export default {
  props: {
    processDefinition: Object,
    formConfig: Object,
    templateType: {
      type: String,
      default: '10'
    }
  },
  components: { ProcessFormInfo, ProcessDesignDrawer, ProcessFormTemplateConfiguration },
  inject: ['pageContext', 'filterSelectOption'],
  data() {
    return {
      templateOptions: [],
      drawerAddId: 'process-form-template-add_' + new Date().getTime(),
      drawerEditId: 'process-form-template-edit_' + new Date().getTime()
    };
  },
  mounted() {
    this.handleTemplateSearch();
  },
  methods: {
    handleTemplateSearch(value = '') {
      let _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'bizDefinitionTemplateFacadeService',
          searchValue: value,
          pageSize: 1000,
          pageNo: 1,
          processDefUuid: _this.processDefinition.uuid,
          templateType: this.templateType,
          selectedTemplateUuid: this.formConfig.templateUuid
        })
        .then(({ data }) => {
          if (data.results) {
            _this.templateOptions = data.results;
          }
        });
    },
    handleTemplateChange(templateUuid) {
      if (templateUuid) {
        $axios.get(`/proxy/api/biz/definition/template/get/${templateUuid}`).then(({ data }) => {
          if (data.data && data.data.definitionJson) {
            this.formConfig = Object.assign(this.formConfig, JSON.parse(data.data.definitionJson));
            this.$refs.processFormInfo.handleFormChange(this.formConfig.formUuid);
          }
        });
      } else {
        this.formConfig.formUuid = '';
        this.$refs.processFormInfo.handleFormChange(this.formConfig.formUuid);
      }
    },
    onAddTemplateConfirmOk() {
      this.$refs.processFormTemplateAdd.save(() => {
        this.$message.success('设置成功！');
        this.handleTemplateSearch();
        this.pageContext.emitEvent('closeDrawer:' + this.drawerAddId);
      });
    },
    onEditTemplateConfirmOk() {
      this.$refs.processFormTemplateEdit.save(() => {
        this.$message.success('设置成功！');
        this.handleTemplateSearch();
        this.handleTemplateChange(this.formConfig.templateUuid);
        this.pageContext.emitEvent('closeDrawer:' + this.drawerEditId);
      });
    }
  }
};
</script>

<style></style>
