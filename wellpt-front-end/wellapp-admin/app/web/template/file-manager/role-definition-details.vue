<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    class="role-definition-details pt-form"
    :label-col="{ span: 6 }"
    labelAlign="left"
    :wrapper-col="{ span: 18 }"
    :colon="false"
  >
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item label="名称" prop="name">
          <a-input v-model="formData.name" />
        </a-form-model-item>
        <a-form-model-item label="ID" prop="id">
          {{ formData.id }}
        </a-form-model-item>
        <a-form-model-item label="描述" prop="remark">
          <a-textarea v-model="formData.remark" />
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="权限配置" forceRender>
        选择权限组包含的权限：
        <RolePermissionConfiguration
          ref="rolePermissionConfiguration"
          :configuration="formData.configuration"
        ></RolePermissionConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import RolePermissionConfiguration from './components/role-permission-configuration.vue';
export default {
  components: { RolePermissionConfiguration },
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    if (!formData.id) {
      formData.id = 'role_' + generateId('SF');
      formData.builtIn = false;
    }
    formData.configuration = RolePermissionConfiguration.methods.definitionJson2Configuration(formData.definitionJson);
    return {
      uuid: formData.uuid,
      formData,
      rules: {
        name: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      }
    };
  },
  created() {
    const _this = this;
    if (_this.uuid) {
      _this.loadRoleDefinition(this.uuid);
    }
  },
  mounted() {
    const _this = this;
    let drawerWidget = _this.pageContext.getVueWidgetById('BdtBwetCBKyKodVOaqciQOdbZrlFpkmm');
    if (drawerWidget && drawerWidget.widget.configuration.footerButton) {
      let hiddenBtns = [];
      let buttons = drawerWidget.widget.configuration.footerButton.buttons || [];
      buttons.forEach(b => {
        drawerWidget.$set(b, 'visible', true);
        drawerWidget.$set(b, 'defaultVisible', true);
      });
      if (!this.uuid) {
        hiddenBtns = buttons.filter(b => ['viewRef', 'restore'].includes(b.code));
      } else if (!(this.formData.builtIn == true || this.formData.builtIn == '1')) {
        hiddenBtns = buttons.filter(b => ['restore'].includes(b.code));
      }
      hiddenBtns.forEach(b => {
        drawerWidget.$set(b, 'visible', false);
        drawerWidget.$set(b, 'defaultVisible', false);
      });
      drawerWidget.widget.configuration.footerHidden = true;
      _this.$nextTick(() => {
        drawerWidget.widget.configuration.footerHidden = false;
      });
    }
  },
  methods: {
    loadRoleDefinition(uuid) {
      $axios.get(`/proxy/api/dms/role/get?uuid=${uuid}`).then(({ data: result }) => {
        if (result.data) {
          let formData = result.data;
          formData.configuration = RolePermissionConfiguration.methods.definitionJson2Configuration(
            formData.definitionJson,
            formData.actions
          );
          this.formData = formData;
        }
      });
    },
    save($evt) {
      const _this = this;
      _this.$loading();

      _this.$refs.form.validate(result => {
        if (result) {
          _this.formData.definitionJson = _this.$refs.rolePermissionConfiguration.configuration2DefinitionJson(
            _this.formData.configuration
          );
          $axios
            .post('/proxy/api/dms/role/save', _this.formData)
            .then(({ data: result }) => {
              if (result.code == 0) {
                _this.$message.success('保存成功！');
                _this.pageContext.emitEvent('BdtBwetCBKyKodVOaqciQOdbZrlFpkmm:closeDrawer');
                _this.$event && _this.$event.$evtWidget && _this.$event.$evtWidget.refetch && _this.$event.$evtWidget.refetch(true);
              } else {
                _this.$message.error(result.msg || '服务异常！');
              }
              _this.$loading(false);
            })
            .catch(({ response }) => {
              _this.$loading(false);
              _this.$message.error((response.data && response.data.msg) || '服务异常！');
            });
        } else {
          _this.$loading(false);
        }
      });
    },
    viewRef($evt) {},
    restore($evt) {
      const _this = this;
      $axios
        .get(`/proxy/api/dms/role/getInitRole?id=${_this.formData.id}`)
        .then(({ data: result }) => {
          if (result.data) {
            let formData = result.data;
            _this.formData.name = formData.name;
            _this.formData.id = formData.id;
            _this.formData.code = formData.code;
            _this.formData.remark = formData.remark;
            Object.assign(
              _this.formData.configuration,
              _this.$refs.rolePermissionConfiguration.definitionJson2Configuration(formData.definitionJson)
            );
            _this.$message.success('已恢复，未保存！');
          } else {
            _this.$message.error('恢复失败！');
          }
        })
        .catch(error => {
          _this.$message.error('恢复失败！');
        });
    }
  },
  META: {
    method: {
      save: '保存权限组',
      viewRef: '查看引用',
      restore: '恢复默认'
    }
  }
};
</script>

<style lang="less" scoped>
.role-definition-details {
}
</style>
