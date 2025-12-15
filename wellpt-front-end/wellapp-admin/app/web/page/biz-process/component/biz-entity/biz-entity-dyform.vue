<template>
  <a-card class="form-container preview-card" size="small" :bordered="false" bodyStyle="padding:0">
    <template slot="title">
      <span class="title">{{ entity.formName || defaultTitle }}</span>
      <label v-if="entity.formName && entity.formVersion" style="color: var(--w-text-color-light); font-size: var(--w-font-size-base)">
        v{{ entity.formVersion }}
      </label>
      <span v-if="formColor" style="color: red">
        ({{ formState.title }})
        <!-- <a
          v-if="['absent', 'unRef', 'inconsistent', 'idFieldAbsent', 'nameFieldAbsent'].includes(formState.code)"
          type="link"
          @click="openBizProcessDesigner"
        >
          前往配置
        </a> -->
        <a-button
          v-if="formState.code == 'inconsistent'"
          @click="onClickMenu('ChooseDyform', '变更' + defaultTitle)"
          size="small"
          type="link"
        >
          变更{{ defaultTitle }}
        </a-button>
      </span>
    </template>
    <template slot="extra">
      <a-button v-if="existsEntityForm" @click="onClickEditDyform" size="small" type="link">
        <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
        编辑表单
      </a-button>
      <slot name="extra"></slot>
    </template>
    <template v-if="!existsEntityForm">
      <a-row type="flex" class="block-operations">
        <a-col>
          <a-empty :description="defaultTitle + '不存在'" class="pt-empty pt-data-empty" style="margin-top: 50px"></a-empty>
        </a-col>
        <!-- <a-col flex="160px" class="action" @click.native.stop="onClickMenu('CreateDyform', '新建' + defaultTitle)">
          <a-row type="flex">
            <a-col>
              <a-icon type="profile" />
            </a-col>
            <a-col>
              <h1>新建{{ defaultTitle }}</h1>
            </a-col>
          </a-row>
        </a-col>
        <a-col flex="160px" class="action" @click.native.stop="onClickMenu('ChooseDyform', '选择已存在表单')">
          <a-row type="flex">
            <a-col>
              <a-icon type="select" />
            </a-col>
            <a-col><h1>选择已存在表单</h1></a-col>
          </a-row>
        </a-col> -->
      </a-row>
    </template>
    <template v-else>
      <a-empty v-if="emptyDesign" style="padding-top: 240px">
        <template #description>
          暂无设计内容
          <a-button type="link" size="small" @click="openDyformDesigner">前往设计</a-button>
        </template>
      </a-empty>
      <PerfectScrollbar v-show="!emptyDesign" style="height: calc(100vh - 120px)">
        <div style="width: 100%; height: 100%; display: block; position: fixed; z-index: 1"></div>
        <iframe
          :key="pageKey"
          id="pagePreviewIframe"
          :src="'/dyform-viewer/just-dyform?formUuid=' + (entity.formUuid || entity.refFormUuid) + '&iframe=1#iframe'"
          :style="{ minHeight: 'calc(100vh - 130px)', border: 'none', width: '100%' }"
        ></iframe>
      </PerfectScrollbar>
    </template>
    <a-modal
      :title="chooseDyformTitle"
      :visible="chooseDyformVisible"
      @ok="handleChooseDyformOk"
      @cancel="e => (chooseDyformVisible = false)"
    >
      <a-select v-model="chooseFormUuid" show-search style="width: 100%" :filter-option="filterSelectOption">
        <a-select-option v-for="d in formOptions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-modal>
  </a-card>
</template>

<script>
export default {
  props: {
    entity: Object,
    defaultTitle: {
      type: String,
      default: '业务主体表单'
    },
    formStateFunction: Function
  },
  inject: ['assemble', 'filterSelectOption', 'pageContext'],
  data() {
    return {
      pageKey: 'biz_process_entity_dyform_preview',
      emptyDesign: false,
      formOptions: [],
      chooseFormUuid: '',
      chooseDyformTitle: '',
      chooseDyformVisible: false
    };
  },
  computed: {
    existsEntityForm() {
      return this.entity.formUuid;
    },
    formState() {
      return this.formStateFunction ? this.formStateFunction() : this.assemble.getBizFormState();
    },
    formColor() {
      let type = this.formState.type;
      return type == 'error' ? 'red' : type == 'warning' ? 'yellow' : '';
    }
  },
  created() {
    if (this.entity.formUuid && !this.entity.formId) {
      // 只存在业务流程定义数据时，填充业务流程装配数据
      this.handleFormChange(this.entity.formUuid).then(formDefinition => {
        if (formDefinition) {
          this.entity.formUuid = formDefinition.uuid;
          this.entity.formId = formDefinition.id;
          this.entity.formName = formDefinition.name;
          this.entity.formVersion = formDefinition.version;
        }
      });
    }
  },
  mounted() {
    window.addEventListener('message', this.frameListener, false);
  },
  beforeDestroy() {
    window.removeEventListener('message', this.frameListener);
  },
  methods: {
    frameListener(event) {
      let _this = this;
      if (event.origin !== location.origin) {
        return;
      }
      if (event.data == 'dyform mounted') {
        let iframeEle = document.querySelector('#pagePreviewIframe');
        _this.emptyDesign = iframeEle.contentWindow.$app.$refs.wDyform.widgets.length == 0;

        // 配置监听方法的属性值
        // 定义一个监听器
        var observer = new MutationObserver(mutations => {
          for (let item of mutations) {
            if (item.type === 'childList') {
              const scrollHeight = iframeEle.contentWindow.document.body.scrollHeight;
              iframeEle.style.height = `${scrollHeight}px`;
              break;
            }
          }
        });

        observer.observe(iframeEle.contentWindow.document, {
          attributes: true,
          childList: true,
          subtree: true
        });
      }
    },
    onClickMenu(key, title) {
      if (key == 'CreateDyform') {
        this.onClickAddDyform();
      } else if (key == 'ChooseDyform') {
        this.chooseDyformTitle = title;
        this.chooseDyformVisible = true;
        if (this.formOptions.length == 0) {
          this.handleFormSearch();
        }
      }
    },
    handleFormSearch(value = '') {
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'bizProcessDefinitionFacadeService',
          queryMethod: 'listDyFormDefinitionSelectData',
          searchValue: value,
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            this.formOptions = data.results;
          }
        });
    },
    handleFormChange(formUuid) {
      const _this = this;
      return $axios
        .post('/json/data/services', {
          serviceName: 'formDefinitionService',
          methodName: 'getOne',
          args: JSON.stringify([formUuid])
        })
        .then(({ data }) => {
          if (!data.data) {
            console.error('form definition is null', formUuid);
          } else {
            let formDefinition = data.data;
            return formDefinition;
          }
        });
      // return _this.$axios.get(`/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`).then(({ data }) => {
      //   if (!data.data) {
      //     console.error('form definition is null', formUuid);
      //   } else {
      //     let formDefinition = JSON.parse(data.data);
      //     return formDefinition;
      //   }
      // });
    },
    handleChooseDyformOk() {
      if (!this.chooseFormUuid) {
        this.$message.error('请选择' + this.defaultTitle + '！');
        return;
      }
      this.handleFormChange(this.chooseFormUuid).then(formDefinition => {
        if (formDefinition) {
          this.entity.formUuid = formDefinition.uuid;
          this.entity.formId = formDefinition.id;
          this.entity.formName = formDefinition.name;
          this.entity.formVersion = formDefinition.version;
          this.assemble.save();
          this.chooseDyformVisible = false;
        }
      });
    },
    onClickAddDyform() {
      let _this = this;
      _this.pageContext.handleCrossTabEvent(`dyform:design:create`, metadata => {
        if (!_this.entity.formUuid) {
          _this.entity.formUuid = metadata.uuid;
          _this.entity.formId = metadata.id;
          _this.entity.formName = metadata.name;
          _this.entity.formVersion = metadata.version;
          _this.assemble.save();
        }
      });
      window.open(`/dyform-designer/index`, '_blank');
    },
    onClickEditDyform() {
      let _this = this;
      this.pageContext.handleCrossTabEvent(`dyform:design:change:${this.entity.formUuid}`, metadata => {
        _this.entity.formUuid = metadata.uuid;
        _this.entity.formId = metadata.id;
        _this.entity.formName = metadata.name;
        _this.entity.formVersion = metadata.version;
        _this.assemble.save();
        // 不同页面临时存储对象不一样
        let iframeEle = document.querySelector('#pagePreviewIframe');
        if (iframeEle && iframeEle.contentWindow && iframeEle.contentWindow.$app && iframeEle.contentWindow.$app.$tempStorage) {
          iframeEle.contentWindow.$app.$tempStorage.removeItem(_this.entity.formUuid);
        }
        _this.$tempStorage.removeItem(_this.entity.formUuid, () => {
          _this.refresh(true);
        });
      });
      window.open(`/dyform-designer/index?uuid=${this.entity.formUuid}`, '_blank');
    },
    refresh(force) {
      if (force) {
        this.pageKey = 'biz_process_entity_dyform_preview_' + new Date().getTime();
      }
    },
    openBizProcessDesigner() {
      this.assemble.openBizProcessDesigner();
      // const _this = this;
      // let processDefUuid = _this.assemble.processDefinition.uuid;
      // let url = `/web/app/pt-mgr/mod_biz_mgr/app_biz_process_def.html?pageUuid=5064e358-b070-4edb-8a39-e2359050bae5&processDefUuid=${processDefUuid}`;
      // window.open(url);
    },
    openDyformDesigner() {
      this.onClickEditDyform();
    }
  }
};
</script>

<style></style>
