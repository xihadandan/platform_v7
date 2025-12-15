<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" class="pt-form" :colon="false">
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="form.name" :maxLength="120">
        <template slot="addonAfter">
          <WI18nInput :target="form" code="name" v-model="form.name" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="ID" prop="id">
      <a-input v-model="form.id" :maxLength="64" v-if="form.uuid == undefined"></a-input>
      <label v-else>{{ form.id }}</label>
    </a-form-model-item>
    <a-form-model-item label="图标">
      <WidgetIconLibModal v-model="form.icon" :zIndex="1000" :onlyIconClass="true">
        <a-badge>
          <a-icon
            v-if="form.icon"
            @click.stop="form.icon = undefined"
            slot="count"
            type="close-circle"
            theme="filled"
            style="color: #f5222d; top: 3px; cursor: pointer"
          />
          <a-avatar shape="square" style="color: #000; background-color: #f7f7f7">
            <Icon slot="icon" :type="form.icon" />
          </a-avatar>
        </a-badge>
      </WidgetIconLibModal>
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="form.remark" :maxLength="300" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import { camelCase } from 'lodash';
export default {
  name: 'BizOrgDimension',
  inject: ['$event', 'pageContext', 'currentWindow'],
  props: {},
  components: { WI18nInput, WidgetIconLibModal },
  computed: {},
  data() {
    let rules = {
      name: { required: true, message: '必填', trigger: ['blur', 'change'] }
    };
    let form = {
      uuid: undefined,
      name: undefined,
      id: undefined,
      icon: undefined,
      remark: undefined
    };
    if (this.$event && this.$event.meta) {
      for (let key in this.$event.meta) {
        form[camelCase(key)] = this.$event.meta[key];
      }
    }
    console.log(form);
    if (form.uuid == undefined) {
      rules.id = { required: true, message: '必填', trigger: ['blur', 'change'] };
    }

    return {
      form,
      labelCol: { span: 4 },
      wrapperCol: { span: 20 },
      rules
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.form.uuid != undefined) {
      this.fetchBizOrgDimensionI18ns(this.form.uuid);
    }
  },
  mounted() {},
  methods: {
    fetchBizOrgDimensionI18ns(uuid) {
      this.$axios
        .get(`/proxy/api/org/organization/element/getI18ns`, { params: { uuid } })
        .then(({ data }) => {
          if (data.data) {
            let i18n = {};
            for (let item of data.data) {
              if (i18n[item.locale] == undefined) {
                i18n[item.locale] = {};
              }
              i18n[item.locale][item.dataCode] = item.content;
            }
            this.$set(this.form, 'i18n', i18n);
          }
        })
        .catch(error => {});
    },
    save() {
      return new Promise((resolve, reject) => {
        this.$refs.form.validate(valid => {
          if (valid) {
            if (this.form.i18n) {
              let i18ns = [];
              for (let locale in this.form.i18n) {
                for (let key in this.form.i18n[locale]) {
                  if (this.form.i18n[locale][key]) {
                    i18ns.push({
                      locale: locale,
                      content: this.form.i18n[locale][key],
                      dataCode: key
                    });
                  }
                }
              }
              this.form.i18ns = i18ns;
            }
            this.$axios
              .post(`/proxy/api/org/biz/saveBizOrgDimension`, this.form)
              .then(({ data }) => {
                if (data.code == 0) {
                  this.$message.success('保存成功');
                  if (this.$event) {
                    this.pageContext.emitEvent('JlzeqoHXKADUKgHZjPWWmjbyhFfLfzNI:refetch');
                    if (this.currentWindow != undefined) {
                      this.currentWindow.close();
                    }
                  }
                } else {
                  this.$message.error('保存失败');
                }
              })
              .catch(error => {
                this.$message.error('保存失败');
              });
          }
        });
      });
    }
  },
  META: {
    method: {
      save: '保存业务维度'
    }
  }
};
</script>
