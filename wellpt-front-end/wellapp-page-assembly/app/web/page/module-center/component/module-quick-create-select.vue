<template>
  <Modal
    :title="null"
    :ok="e => confirmCreate(e)"
    :cancel="reset"
    :width="900"
    :maxHeight="600"
    v-model="modalVisible"
    wrapperClass="module-create-modal"
  >
    <template slot="content">
      <div class="modal-title">{{ titleMap[createType] || '选择创建方式' }}</div>
      <div>
        <div class="module-create-type-select" v-if="step == 1">
          <div @click="createType = 'newBlankModule'" :class="createType == 'newBlankModule' ? 'selected' : null">
            <img src="/static/image/c-blank-bg.png" class="item-bg" />
            <div class="item-content">
              <div class="item-title">从空白创建</div>
              <div class="item-remark">使用空白模板, 从0到1装配模块</div>
            </div>
          </div>
          <div
            @click="createType = 'newModuleFromTemplate'"
            shape="square"
            :class="createType == 'newModuleFromTemplate' ? 'selected' : null"
          >
            <img src="/static/image/c-template-bg.png" class="item-bg" />
            <div class="item-content">
              <div class="item-title">从模板创建</div>
              <div class="item-remark">从模板中心选择模块模板进行复制创建</div>
            </div>
          </div>
          <div @click="createType = 'newModuleFromBiz'" :class="createType == 'newModuleFromBiz' ? 'selected' : null">
            <img src="/static/image/c-modal-bg.png" class="item-bg" />
            <div class="item-content">
              <div class="item-title">从模型创建</div>
              <div class="item-remark">选择业务模型，逐步装配模块</div>
            </div>
          </div>
          <div @click="clickImportModule" :class="createType == 'importModule' ? 'selected' : null">
            <img src="/static/image/c-import-bg.png" class="item-bg" />
            <div class="item-content">
              <div class="item-title">导入定义</div>
              <div class="item-remark">导入本地模块定义，生成模块</div>
            </div>
          </div>
        </div>
        <template v-else-if="step == 2">
          <ModuleDetail ref="moduleDetail" v-if="createType == 'newBlankModule'" />
        </template>
        <ImportDef ref="importDef" />
      </div>
    </template>
  </Modal>
</template>
<style lang="less">
.module-create-modal {
  .ant-modal-body {
    padding: var(--w-padding-2xs) var(--w-padding-md);
  }
  .modal-title {
    font-size: var(--w-font-size-lg);
    font-weight: bold;
    color: var(--w-text-color-dark);
    line-height: 32px;
  }
  .ant-modal-close-x {
    height: 48px;
    line-height: 48px;
  }
  .ant-modal-footer {
    border-top-color: transparent;
  }
}
.module-create-type-select {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--w-padding-2xs) 0 0;

  > div {
    width: 200px;
    height: 213px;
    border-radius: 4px;
    outline: 1px solid var(--w-border-color-base);
    cursor: pointer;
    &:hover {
      outline: 2px solid var(--w-primary-color);
    }
    > .item-bg {
      height: 112px;
      width: 100%;
    }
    > .item-content {
      padding: var(--w-padding-2xs) 14px;
      text-align: center;
      .item-remark {
        color: var(--w-text-color-light);
        font-size: var(--w-font-size-sm);
        padding-top: var(--w-padding-3xs);
      }
      .item-title {
        font-size: var(--w-font-size-base);
        font-weight: bold;
        color: var(--w-text-color-dark);
        line-height: 32px;
      }
    }
  }
  > div.selected {
    outline: 2px solid var(--w-primary-color);
  }
}
</style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ModuleDetail from './module-detail.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';

export default {
  name: 'ModuleQuickCreateSelect',
  model: {
    prop: 'visible'
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    afterCreate: Function
  },
  components: { ModuleDetail, Modal, ImportDef },
  computed: {},
  data() {
    return {
      modalVisible: this.visible,
      createType: 'newBlankModule',
      step: 1,
      vModel: this.$listeners.input != undefined,
      titleMap: {
        newBlankModule: '从空白创建模块',
        newModuleFromTemplate: '从模板创建模块',
        newModuleFromBiz: '从业务模型创建模块',
        importModule: '导入模块'
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    clickImportModule() {
      this.createType = 'importModule';
    },
    reset() {
      this.step = 1;
      this.createType = undefined;
    },
    hideModal() {
      this.modalVisible = false;
      this.$emit('input', false);
    },

    confirmCreate(e) {
      let _this = this;
      if (this.createType == 'importModule') {
        _this.reset();
        _this.hideModal();
        this.$refs.importDef.show();
        return;
      }
      if (this.step == 1) {
        if (this.createType) {
          if (['newModuleFromTemplate', 'newModuleFromBiz'].indexOf(this.createType) > -1) {
            this.$info({
              title: '提示',
              content: '开发中，敬请期待',
              icon: h => '', // 不要图标
              onOk() {}
            });
          } else {
            this.step += 1;
          }
        }
      } else if (this.step == 2) {
        if (this.createType === 'newBlankModule') {
          this.$refs.moduleDetail.onSave(result => {
            _this.reset();
            _this.hideModal();
            window.open(`/module/assemble/${result.uuid}`, '_blank');
            _this.$emit('createDone', true);
            if (typeof _this.afterCreate == 'function') {
              _this.afterCreate(result);
            }
          });
        } else {
          _this.hideModal();
        }
      }
    }
  },
  watch: {
    visible: {
      handler(v) {
        if (v) {
          this.modalVisible = v;
          this.createType = 'newBlankModule';
        }
      }
    },
    modalVisible: {
      handler(v) {
        if (this.vModel) {
          this.$emit('input', v);
        }
      }
    }
  }
};
</script>
