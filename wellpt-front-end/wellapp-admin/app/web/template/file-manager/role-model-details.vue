<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    class="role-model-details pt-form"
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
        <a-form-model-item label="权限组" prop="roleDefValue">
          <a-tag
            v-for="(item, index) in roleDefCheckedOptions"
            :key="index"
            :visible="true"
            closable
            @close="() => removeRoleDefCheckedOption(item)"
          >
            {{ item.label }}
          </a-tag>
          <a-popover trigger="click" placement="bottomLeft">
            <template slot="content">
              <div style="max-height: 300px; overflow: auto">
                <a-checkbox :checked="checkAllRoleDef" @change="onCheckAllRoleDefChange" style="display: block">全选</a-checkbox>
                <a-checkbox-group v-model="formData.roleDefValue">
                  <a-checkbox v-for="(opt, i) in roleDefinitionOptions" :key="i" :value="opt.value">
                    {{ opt.label }}
                  </a-checkbox>
                </a-checkbox-group>
              </div>
            </template>
            <a-tag class="add-tags"><a-icon type="plus" /></a-tag>
          </a-popover>
        </a-form-model-item>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
export default {
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    if (!formData.id) {
      formData.id = 'model_' + generateId('SF');
      formData.builtIn = false;
    }
    if (formData.roleUuids) {
      formData.roleDefValue = formData.roleUuids.split(';');
    } else {
      formData.roleDefValue = [];
    }
    return {
      uuid: formData.uuid,
      formData,
      rules: {
        name: { required: true, message: '不能为空', trigger: ['blur', 'change'] }
      },
      roleDefinitionOptions: [],
      checkAllRoleDef: false
    };
  },
  computed: {
    roleDefCheckedOptions() {
      return this.roleDefinitionOptions.filter(item => this.formData.roleDefValue && this.formData.roleDefValue.includes(item.value));
    }
  },
  created() {
    if (this.uuid) {
      this.loadRoleModel(this.uuid);
    }
    this.loadRoleDefinitions();
  },
  methods: {
    loadRoleModel(uuid) {
      $axios.get(`/proxy/api/dms/role/model/get?uuid=${uuid}`).then(({ data: result }) => {
        if (result.data) {
          let formData = result.data;
          if (formData.roleUuids) {
            formData.roleDefValue = formData.roleUuids.split(';');
          } else {
            formData.roleDefValue = [];
          }
          this.formData = formData;
        }
      });
    },
    loadRoleDefinitions() {
      const _this = this;
      $axios.get('/proxy/api/dms/role/list').then(({ data: result }) => {
        if (result.data) {
          _this.roleDefinitionOptions = result.data.map(item => {
            return { label: item.name, value: item.uuid };
          });
        }
      });
    },
    onCheckAllRoleDefChange(e) {
      const _this = this;
      const checked = e.target.checked;
      _this.checkAllRoleDef = checked;
      if (checked) {
        _this.formData.roleDefValue = _this.roleDefinitionOptions.map(item => item.value);
      } else {
        _this.formData.roleDefValue = [];
      }
    },
    removeRoleDefCheckedOption(item) {
      this.formData.roleDefValue = this.formData.roleDefValue.filter(value => value !== item.value);
    },
    save($evt) {
      const _this = this;
      _this.$loading();

      _this.$refs.form.validate(result => {
        if (result) {
          if (_this.formData.roleDefValue) {
            _this.formData.roleUuids = _this.formData.roleDefValue.join(';');
          }
          $axios
            .post('/proxy/api/dms/role/model/save', _this.formData)
            .then(({ data: result }) => {
              if (result.code == 0) {
                _this.$message.success('保存成功！');
                $evt.$evtWidget && $evt.$evtWidget.closeDrawer && $evt.$evtWidget.closeDrawer();
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
    }
  },
  META: {
    method: {
      save: '保存权限模型'
    }
  }
};
</script>

<style lang="less" scoped>
::v-deep .ant-checkbox-wrapper {
  display: block;
}
</style>
