<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" class="pt-form">
    <a-tabs
      defaultActiveKey="1"
      tabPosition="left"
      class="ant-tabs-menu-style"
      style="height: 500px"
      :tabBarStyle="{ '--w-tab-menu-style-width': tabsWidth ? tabsWidth : '', '--w-tab-menu-style-nav-container-padding': '0 24px 0 0' }"
    >
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item label="名称" prop="name" v-show="type !== 'unit'">
          <a-input v-model="form.name">
            <template slot="addonAfter">
              <WI18nInput :target="form" code="name" v-model="form.name" />
            </template>
          </a-input>
        </a-form-model-item>

        <a-form-model-item label="ID" v-if="uuid">
          {{ form.id }}
        </a-form-model-item>
        <a-form-model-item label="编码" prop="code">
          <a-input v-model="form.code" />
        </a-form-model-item>
        <a-form-model-item label="成员">
          <OrgSelect v-model="form.member" @change="memberChange" :showBizOrgUnderOrg="false" />
        </a-form-model-item>
        <a-form-model-item label="使用者">
          <OrgSelect v-model="form.owner" :showBizOrgUnderOrg="false" />
        </a-form-model-item>

        <a-form-model-item label="备注" prop="remark">
          <a-textarea v-model="form.remark" />
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="角色信息">
        <RoleSelectPanel v-model="form.roleUuids" panel-height="400px" />
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script type="text/babel">
import OrgRoleInfo from './authroize-role/org-role-info.vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import RoleSelectPanel from './authroize-role/role-select-panel.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'OrgGroupDetails',
  props: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    let uuid = this.vPageState && this.vPageState.orgUuid ? this.vPageState.orgUuid : undefined;
    if (!uuid) {
      uuid = this.$event && this.$event.meta && this.$event.meta.uuid ? this.$event.meta.uuid : undefined;
    }

    let rules = {
      name: { required: true, message: '名称必填', trigger: ['blur', 'change'] },
      code: { required: true, message: '编号必填', trigger: ['blur', 'change'] }
    };

    return {
      uuid,
      // loading: !uuid,
      form: {
        member: [],
        memberPath: [],
        roleUuids: [],
        owner: []
      },

      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      rules
    };
  },
  watch: {},
  beforeCreate() {},
  components: { OrgRoleInfo, OrgSelect, RoleSelectPanel, WI18nInput },
  computed: {},
  created() {
    this.getOrgGroupDetails();
  },
  methods: {
    memberChange({ value, label, nodes }) {
      this.form.memberPath = nodes.map(node => node.keyPath);
    },
    getOrgGroupDetails() {
      let _this = this;
      if (this.uuid)
        $axios.get(`/proxy/api/org/organization/orgGroup/details/${this.uuid}`, { params: { fetchI18ns: true } }).then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.form = data.data;
            if (data.data.i18ns) {
              let i18n = {};
              for (let item of data.data.i18ns) {
                if (i18n[item.locale] == undefined) {
                  i18n[item.locale] = {};
                }
                i18n[item.locale][item.dataCode] = item.content;
              }
              _this.$set(_this.form, 'i18n', i18n);
            }
          }
        });
    },
    saveOrgGroup(event) {
      let form = this.form,
        _this = this;
      this.$refs.form.validate(valid => {
        if (valid) {
          if (form.uuid == undefined) {
            form.system = this._$SYSTEM_ID;
          }
          if (form.i18n) {
            let i18ns = [];
            for (let locale in form.i18n) {
              for (let key in form.i18n[locale]) {
                if (form.i18n[locale][key]) {
                  i18ns.push({
                    locale: locale,
                    content: form.i18n[locale][key],
                    dataCode: key
                  });
                }
              }
            }
            form.i18ns = i18ns;
          }
          $axios
            .post('/proxy/api/org/organization/orgGroup/save', form)
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('保存成功');
                // 组织列表刷新表格
                _this.pageContext.emitEvent('klkNXMwghuIQEeIXhpTcxngzYznsVlpC:refetch');
                event.$evtWidget.closeModal(); // 关闭弹窗
              }
            })
            .catch(error => {
              _this.$message.error('服务异常');
            });
        }
      });
    }
  },
  beforeMount() {
    // this.getOrgGroupDetails();
  },
  mounted() {}
};
</script>
