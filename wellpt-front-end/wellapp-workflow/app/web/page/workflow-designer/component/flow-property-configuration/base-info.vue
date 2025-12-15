<template>
  <!-- 流程属性-基本属性 -->
  <div>
    <a-form-model-item prop="name" :label="rules['name']['label']">
      <a-input v-model="formData.name">
        <template slot="addonAfter">
          <w-i18n-input :target="formData" code="workflowName" v-model="formData.name" ref="workflowNameI18n" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item prop="id" label="ID">
      <a-input v-model="formData.id" :disabled="!designer.isNewFlow" @change="onInputIdFormater" @blur="onBlurId" />
    </a-form-model-item>
    <a-form-model-item prop="version" label="版本">
      <a-input v-model="formData.version" :disabled="true" />
    </a-form-model-item>
    <a-form-model-item prop="code" label="流程编号">
      <a-input v-model="formData.code" />
    </a-form-model-item>
    <a-form-model-item prop="categorySN" label="流程分类">
      <w-select
        v-model="formData.categorySN"
        :options="categoryOptions"
        :placeholder="rules['categorySN']['message']"
        :replaceFields="{
          title: 'name',
          key: 'uuid',
          value: 'uuid'
        }"
      />
    </a-form-model-item>
    <a-form-model-item prop="moduleId" label="所属模块">
      <w-select v-model="formData.moduleId" :options="moduleOptions" :placeholder="rules['moduleId']['message']" @change="changeModuleId" />
    </a-form-model-item>
    <a-form-model-item prop="formID" label="使用表单">
      <w-select v-model="formData.formID" :options="dyformOptions" :placeholder="rules['formID']['message']" @change="changeFormId">
        <template slot="addonAfter" v-if="formData.formID">
          <a-button type="link" size="small" icon="form" :style="{ margin: '0 -10px' }" @click="openFormDesigner" />
        </template>
      </w-select>
    </a-form-model-item>
    <a-form-model-item label="流程标题">
      <a-radio-group v-model="formData.titleExpressionMode" size="small" button-style="solid" @change="changeTitleMode">
        <a-radio-button v-for="item in titleExpressionConfig" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <div class="ant-form-item" v-if="formData.titleExpressionMode === constCustom">
      <div style="flex: 1">
        <div>
          <title-define-template
            :formData="formData"
            prop="titleExpression"
            title="流程标题设置"
            alert="在下方编辑流程标题表达式，可插入流程内置变量、表单字段和文本。"
            @change="changeTitle"
          >
            <a-button type="link" size="small" slot="trigger">
              <Icon type="pticon iconfont icon-ptkj-shezhi" />
              <span>设置</span>
            </a-button>
          </title-define-template>
          <span style="margin-left: -6px">自定义标题为空时，等同于默认</span>
        </div>
      </div>
    </div>
    <div class="ant-form-item">
      <div class="title-expression-value">
        <template v-if="formData.titleExpressionMode === constDefault">
          {{ defTitleExpression }}
        </template>
        <template v-else>
          {{ customTitleExpression }}
        </template>
      </div>
    </div>
    <div class="ant-form-item">
      <w-checkbox v-model="formData.autoUpdateTitle">自动更新标题</w-checkbox>
    </div>
    <a-form-model-item prop="isActive" label="流程状态">
      <w-switch unCheckedChildren="禁用" v-model="formData.isActive">
        <template slot="checkedChildren">启用</template>
      </w-switch>
    </a-form-model-item>
    <div class="ant-form-item" v-if="formData.isActive === '1'">
      <div class="pc_mobile_show">
        <div class="_title">流程发起设置</div>
        <a-form-model-item prop="pcShowFlag" label="PC端">
          <w-switch checkedChildren="显示" unCheckedChildren="隐藏" v-model="formData.pcShowFlag" />
        </a-form-model-item>
        <a-form-model-item prop="isMobileShow" label="移动端">
          <w-switch checkedChildren="显示" unCheckedChildren="隐藏" v-model="formData.isMobileShow" />
        </a-form-model-item>
      </div>
    </div>
    <a-form-model-item class="form-item-vertical" prop="remark" label="备注">
      <a-textarea
        v-model="formData.remark"
        :rows="3"
        :style="{
          height: 'auto'
        }"
      />
    </a-form-model-item>
  </div>
</template>

<script>
import { debounce } from 'lodash';
import { isExitWorkFlowId, fetchFormDefinitionByUuidJustDataAndDef } from '../api/index';
import { constDefault, constCustom, titleExpressionConfig, yesOrNoConfig } from '../designer/constant';
import WSwitch from '../components/w-switch.js';
import WCheckbox from '../components/w-checkbox.js';
import WSelect from '../components/w-select';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import TitleDefineTemplate from '../commons/title-define-template.vue';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'FlowPropertyBaseInfo',
  inject: ['designer', 'workFlowData', 'graph'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    rules: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    const defTitleExpression = this.designer.flowDefinition.titleExpression;
    const customTitleExpression = this.formData.titleExpression;

    let categoryOptions = [];
    const { diction, modules } = this.designer;
    if (diction) {
      categoryOptions = diction.categorys;
    }
    let moduleOptions = [];
    if (modules) {
      moduleOptions = modules;
    }
    return {
      categoryOptions,
      moduleOptions,
      dyformOptions: [],
      titleExpressionConfig,
      constDefault,
      constCustom,
      customTitleExpression, // 自定义标题
      defTitleExpression, // 默认标题
      yesOrNoConfig
    };
  },
  components: {
    WSwitch,
    WCheckbox,
    WSelect,
    TitleDefineTemplate,
    Modal,
    WI18nInput
  },
  watch: {
    'designer.diction': {
      deep: true,
      handler(diction) {
        if (diction) {
          this.categoryOptions = diction.categorys;
        }
      }
    },
    'designer.modules': {
      deep: true,
      handler(modules) {
        if (modules) {
          // TODO select分页加载
          let modulesCopy = JSON.parse(JSON.stringify(modules));
          // modulesCopy.splice(0, 20);
          this.moduleOptions = modules;
        }
      }
    }
  },
  created() {
    this.getDyformListByModuleId(this.formData.moduleId);
    this.getFormDefinitionByUuid(this.formData.formID);
    this.getDyformVar(this.formData.formID);
  },
  mounted() {
    this.designer.i18nEl['workflowName'] = this.$refs.workflowNameI18n;
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    // 改变流程标题模式
    changeTitleMode(event) {
      const mode = event.target.value;
      if (mode === constCustom) {
        this.formData.titleExpression = this.customTitleExpression;
      } else {
        this.formData.titleExpression = '';
      }
    },
    // 打开表单设计器
    openFormDesigner() {
      let url = `/dyform-designer/index?uuid=${this.formData.formID}`;
      const path = window.location.pathname.split('/_/');
      if (path.length > 1) {
        url = `${path[0]}/_${url}`;
      }
      window.open(url, '_blank');
    },
    onBlurId() {
      if (this.formData.id && this.designer.isNewFlow) {
        this.isExitWorkFlowId();
      }
    },
    onInputIdFormater: debounce(function (e) {
      if (!/^[a-zA-Z_0-9]+$/.test(this.formData.id)) {
        this.formData.id = '';
        this.$message.error('流程ID只能包含英文大小写字母、整数和下划线！', 2);
        return;
      }
    }, 300),
    isExitWorkFlowId: debounce(function (e) {
      isExitWorkFlowId(this.formData.id).then(exit => {
        if (exit) {
          this.formData.id = '';
          this.$message.error('流程ID已存在,请重新输入！');
        }
      });
    }, 300),
    // 更改模块
    changeModuleId(moduleId) {
      this.formData.formID = undefined;
      if (moduleId) {
        this.getDyformListByModuleId(moduleId);
      } else {
        this.dyformOptions = [];
      }
    },
    // 从模块获取表单
    getDyformListByModuleId(moduleId) {
      if (!moduleId) {
        return;
      }
      const params = {
        args: JSON.stringify([moduleId]),
        serviceName: 'dyFormFacade',
        methodName: 'getDyFormDefinitionIncludeRefDyFormByModuleId',
        validate: false
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.dyformOptions = data.map(item => {
                const label = `${item.name}(V${item.version})`;
                return {
                  value: item.uuid,
                  label,
                  title: label
                };
              });
            }
          }
        });
    },
    // 更改表单
    changeFormId(uuid) {
      this.resetDisplayFormID();
      this.getFormDefinitionByUuid(uuid);
      this.getDyformVar(uuid);
    },
    // 重置展示表单
    resetDisplayFormID() {
      if (this.graph.instance) {
        const cells = this.graph.instance.getCells();
        for (let index = 0; index < cells.length; index++) {
          const cell = cells[index];
          const cellData = cell.data;
          const shape = cell.shape;
          if (cellData.formID) {
            cellData.formID = undefined;
          }
        }
      }
    },
    // 获取表单配置
    getFormDefinitionByUuid(uuid) {
      if (!uuid) {
        return;
      }
      const params = {
        uuid
      };
      this.$axios
        .get('/pt/dyform/definition/getFormDefinitionByUuid', {
          params
        })
        .then(res => {
          if (res.status === 200 && res.data) {
            this.designer.setFormDefinition(res.data);
          }
        });
    },
    getDyformVar(uuid) {
      if (!uuid) {
        return;
      }
      fetchFormDefinitionByUuidJustDataAndDef({ uuid }).then(res => {
        this.designer.setDyformVarList(res);
      });
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    // 自定义标题设置
    changeTitle(arg) {
      this.customTitleExpression = arg.value;
    }
  }
};
</script>
