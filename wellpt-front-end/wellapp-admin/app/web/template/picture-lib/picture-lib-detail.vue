<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-form-model :model="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 17 }" :rules="rules" ref="form">
      <a-form-model-item label="名称" prop="name">
        <a-input v-model="form.name">
          <template slot="addonAfter">
            <WI18nInput :code="form.uuid" :target="form" v-model="form.name" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="图标" prop="useIcon">
        <a-switch v-model="useIcon" @change="switchChange" />
        <template v-if="useIcon">
          <WidgetIconLibModal v-model="form.icon" :zIndex="1000" :onlyIconClass="true">
            <span class="msgIconShow" :style="{ backgroundColor: bgColor }">
              <Icon :type="form.icon || 'pticon iconfont icon-ptkj-qiehuanshitu'" title="选择图标" />
            </span>
          </WidgetIconLibModal>
          <ColorPicker v-model="form.color" @ok="onColorPickOk"></ColorPicker>
        </template>
      </a-form-model-item>
      <a-form-model-item label="编号" prop="code">
        <a-input v-model="form.code" />
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          <a-tooltip overlayClassName="widget-tooltip-overlay">
            <template #title>在提供分类选择和分类展示时，会显示</template>
            分类描述
            <span class="widget-tooltip"><a-icon type="info-circle" /></span>
          </a-tooltip>
        </template>
        <a-input v-model="form.description" type="textarea" />
      </a-form-model-item>
    </a-form-model>
    <div style="text-align: center; margin-bottom: 30px">
      <a-button @click="saveForm" type="primary">保存</a-button>
    </div>
  </a-skeleton>
</template>

<script type="text/babel">
import { deepClone, queryString } from '@framework/vue/utils/util';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import { some } from 'lodash';
export default {
  name: 'PictureLibDetail',
  inject: ['pageContext', '$event', 'vPageState', 'currentWindow'],
  props: [],
  components: { Modal, WidgetIconLibModal, ColorPicker, WI18nInput },
  data() {
    let $event = this.$event;
    return {
      $evtWidget: undefined,
      $dialogWidget: undefined,
      loading: $event && $event.meta && $event.meta.uuid != undefined,
      uuid: $event && $event.meta != undefined ? $event.meta.uuid : undefined,
      form: {},
      useIcon: false,
      defaultColor: '#64B3EA',
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] },
        code: { required: true, message: '编号必填', trigger: ['blur', 'change'] }
      },
      wTemplate: {
        $options: {
          methods: {
            saveForm: this.saveForm
          },
          META: {
            method: {
              saveForm: '保存表单'
            }
          }
        }
      }
    };
  },
  computed: {
    bgColor() {
      return this.form.color;
    }
  },
  beforeMount() {
    let _this = this;
    if (this.$event) {
      let $event = this.$event;
      this.$evtWidget = $event && $event.$evtWidget;
      this.$dialogWidget = this._provided && this._provided.dialogContext;
      if ($event && $event.eventParams) {
        this.uuid = $event.eventParams.uuid || '';
      }
    } else {
      let urlParams = queryString(location.search.substr(1));
      this.uuid = urlParams.uuid || '';
    }
    if (this.uuid) {
      this.loading = true;
      this.getDetails();
    }
  },
  methods: {
    getDetails() {
      let _this = this;
      $axios.get(`/basicdata/img/category/${this.uuid}`, {}).then(({ data }) => {
        _this.loading = false;
        if (data.code == 0 && data.data) {
          this.form = data.data;
          this.useIcon = !!this.form.icon;
          if (this.form.i18ns && this.form.i18ns.length > 0) {
            this.form.i18n = {};
            this.form.i18ns.forEach(i18n => {
              this.form.i18n[i18n.locale] = {};
              this.form.i18n[i18n.locale][this.form.uuid] = i18n.content;
            });
          }
        }
      });
    },
    switchChange(checked) {
      if (checked && !this.form.color) {
        this.$set(this.form, 'color', this.defaultColor);
      }
    },
    onColorPickOk(color) {
      this.$set(this.form, 'color', color);
    },
    isCategoryAlreadyExists(name, uuid, callback) {
      $axios.get(`/basicdata/img/category/queryAllCategory`, {}).then(({ data }) => {
        if (data.code == 0 && data.data) {
          let exists = some(data.data, (item, index) => {
            if (item.name.trim().toLowerCase() === name.trim().toLowerCase() && item.uuid !== uuid) {
              return true;
            }
            return false;
          });

          if (exists) {
            this.$message.error('分类名称已存在，无法保存！');
          } else if (typeof callback == 'function') {
            callback();
          }
        }
      });
    },
    saveForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          let bean = deepClone(this.form);
          let i18ns = [];
          if (bean.i18n) {
            for (let key in bean.i18n) {
              for (let code in bean.i18n[key]) {
                i18ns.push({
                  content: bean.i18n[key][code],
                  locale: key,
                  code: 'uuid'
                });
              }
            }
          }
          bean.i18ns = i18ns;
          if (this.useIcon) {
            if (!bean.icon) {
              bean.icon = 'iconfont icon-ptkj-qiehuanshitu';
            }
          } else {
            bean.icon = '';
            bean.color = '';
          }
          this.isCategoryAlreadyExists(bean.name, bean.uuid, () => {
            this.saveFormData(bean);
          });
        } else {
          return false;
        }
      });
    },
    saveFormData(bean) {
      $axios
        .post('/basicdata/img/category/save', bean)
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('保存成功');
            // 刷新表格
            if (this.$evtWidget) {
              let options = this.$evtWidget.getDataSourceProvider().options;
              this.$evtWidget.refetch && this.$evtWidget.refetch(options);
            }
            if (this.$dialogWidget) {
              // 关闭弹窗
              this.$dialogWidget.close();
            } else if (this.currentWindow) {
              // 关闭浮窗或tab
              this.currentWindow.close();
            } else if (window.opener && window.opener.$app && window.opener.$app.pageContext) {
              window.opener.$app.pageContext.emitEvent(`refetchPictureLibManangeTable`, { saveSuccess: true });
              setTimeout(() => {
                window.opener = null;
                window.close();
              }, 500);
            }
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    }
  },
  META: {
    method: {
      saveForm: '保存表单'
    }
  }
};
</script>

<style>
/* 图片库分类图标 */
.msgIconShow {
  margin: 0 8px;
  width: 24px;
  height: 24px;
  line-height: 24px;
  display: inline-block;
  border-radius: 50%;
  color: #ffffff;
  background-color: var(--w-primary-color);
  -webkit-transform: scale(0.8);
  transform: scale(0.8);
  text-align: center;
  cursor: pointer;
}
</style>
