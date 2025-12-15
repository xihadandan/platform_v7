<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" class="pt-form" :colon="false">
    <a-tabs
      defaultActiveKey="1"
      tabPosition="left"
      class="ant-tabs-menu-style"
      style="height: 500px"
      :tabBarStyle="{ '--w-tab-menu-style-width': '140px', '--w-tab-menu-style-nav-container-padding': '0 24px 0 0' }"
    >
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item label="名称" prop="name">
          <a-input v-model="form.name" :maxLength="120" v-if="elementInfoEditable">
            <template slot="addonAfter">
              <WI18nInput :target="form" code="name" v-model="form.name" />
            </template>
          </a-input>
          <template v-else>{{ form.name }}</template>
        </a-form-model-item>
        <a-form-model-item label="类型">
          {{
            form.isDimension ? bizOrgDimension.name : orgElementModelMap[form.elementType] ? orgElementModelMap[form.elementType].name : ''
          }}
          <a-tag>{{ form.isDimension ? '业务维度' : '组织节点' }}</a-tag>
        </a-form-model-item>
        <a-form-model-item label="父级节点" prop="parentUuid" v-if="bizOrgElementParentTreeData.length > 0">
          <a-tree-select
            v-model="form.parentUuid"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            allow-clear
            :tree-data="bizOrgElementParentTreeData"
            :treeExpandedKeys.sync="treeExpandedKeys"
            v-if="elementInfoEditable"
          ></a-tree-select>
          <template v-else>{{ form.parentName }}</template>
        </a-form-model-item>
        <a-form-model-item label="备注" prop="remark">
          <a-textarea v-model="form.remark" :maxLength="300" v-if="elementInfoEditable" />
          <template v-else>{{ form.remark }}</template>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="4" tab="角色信息">
        <RoleSelectPanel v-model="form.roleUuids" panelHeight="400px" />
      </a-tab-pane>
      <a-tab-pane key="5" tab="权限信息" v-if="form.uuid">
        <BizOrgRoleResult :biz-org-element-id="form.id" panelHeight="480px" />
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import RoleSelectPanel from '../authroize-role/role-select-panel.vue';
import BizOrgRoleResult from './biz-org-role-result.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'BizOrgElement',
  props: {
    bizOrgElement: Object,
    orgElementModels: Array,
    bizOrgDimension: Object,
    bizOrgElementTreeData: Array,
    parentAllowedMountType: Object,
    bizOrgConfig: Object,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: { RoleSelectPanel, BizOrgRoleResult, WI18nInput },
  computed: {
    elementInfoEditable() {
      // 同步节点不允许编辑
      return !this.bizOrgConfig.enableSyncOrg || (this.bizOrgConfig.enableSyncOrg && this.form.orgElementId == undefined);
    },
    // vElementTreeData() {
    //   let list = [];
    //   if (this.bizOrgElementTreeData.length) {
    //     let build = (data, scope) => {
    //       for (let i = 0; i < data.length; i++) {
    //         if (data[i].key == this.bizOrgElement.uuid) {
    //           // 排除当前节点
    //           data.splice(i, 1);
    //           i--;
    //           continue;
    //         }
    //         let node = {
    //           value: data[i].key,
    //           key: data[i].key,
    //           label: data[i].title,
    //           disabled: data[i].data.isDimension
    //             ? false
    //             : !(
    //                 this.parentAllowedMountType &&
    //                 this.parentAllowedMountType[this.bizOrgElement.elementType] &&
    //                 this.parentAllowedMountType[this.bizOrgElement.elementType].includes(data[i].data.elementType)
    //               )
    //         };
    //         scope.push(node);

    //         if (data[i].children) {
    //           node.children = [];
    //           build(data[i].children, node.children);
    //         }
    //       }
    //     };
    //     build(JSON.parse(JSON.stringify(this.bizOrgElementTreeData)), list);
    //   }
    //   return list;
    // },
    orgElementModelMap() {
      let map = {};
      for (let m of this.orgElementModels) {
        map[m.id] = m;
      }
      return map;
    },

    editable() {
      return this.displayState == 'edit';
    }
  },
  data() {
    let rules = { name: { required: true, message: '必填', trigger: ['blur', 'change'] } };
    let form = {
      uuid: undefined,
      name: undefined,
      parentUUid: undefined,
      elementType: undefined,
      remark: undefined,
      isDimension: false,
      roleUuids: []
    };
    if (this.bizOrgElement != undefined) {
      form.uuid = this.bizOrgElement.uuid;
      form.name = this.bizOrgElement.name;
      form.id = this.bizOrgElement.id;
      form.remark = this.bizOrgElement.remark;
      form.parentUuid = this.bizOrgElement.parentUuid;
      form.isDimension = this.bizOrgElement.isDimension;
      form.elementType = this.bizOrgElement.elementType;
      form.bizOrgUuid = this.bizOrgElement.bizOrgUuid;
      form.orgElementId = this.bizOrgElement.orgElementId;
      form.parentName = this.bizOrgElement.parentName;
    }

    return {
      form,
      labelCol: { span: 4 },
      wrapperCol: { span: 20 },
      rules,
      treeExpandedKeys: [],
      bizOrgElementParentTreeData: []
    };
  },
  beforeCreate() {},
  created() {
    if (!this.elementInfoEditable) {
      this.rules.name.required = false;
    }
  },
  beforeMount() {
    if (this.bizOrgElement.id != undefined) {
      this.fetchBizOrgElementRelaRoles(this.bizOrgElement.id);
      this.fetchBizOrgElementI18ns(this.bizOrgElement.id);
    }
    this.buildParentTreeData();
  },
  mounted() {},
  methods: {
    fetchBizOrgElementI18ns(id) {
      this.$axios
        .get(`/proxy/api/org/organization/element/getI18ns`, { params: { id } })
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
    buildParentTreeData() {
      if (this.bizOrgElementTreeData.length) {
        let build = (data, scope) => {
          for (let i = 0; i < data.length; i++) {
            if (data[i].key == this.bizOrgElement.uuid) {
              // 排除当前节点
              data.splice(i, 1);
              i--;
              continue;
            }

            let node = {
              value: data[i].key,
              key: data[i].key,
              label: data[i].title
            };

            if (this.bizOrgElement.isDimension) {
              // 业务维度要判断不允许挂载在不可创建子业务维度的节点下
              if (data[i].unAllowedCreateSubDimension === true) {
                node.disabled = true;
              }
            } else {
              // 业务组织不允许多级挂载的情况
              if (data[i].unAllowedCreateSubOrgElement === true) {
                node.disabled = true;
              }
              if (
                !data[i].data.isDimension &&
                // 组织节点不允许挂载其他组织节点类型
                !(
                  this.parentAllowedMountType &&
                  this.parentAllowedMountType[this.bizOrgElement.elementType] &&
                  this.parentAllowedMountType[this.bizOrgElement.elementType].includes(data[i].data.elementType)
                )
              ) {
                node.disabled = true;
              }
            }
            scope.push(node);

            if (data[i].children) {
              node.children = [];
              build(data[i].children, node.children);
            }
          }
        };
        build(JSON.parse(JSON.stringify(this.bizOrgElementTreeData)), this.bizOrgElementParentTreeData);
      }
    },
    fetchBizOrgElementRelaRoles(bizOrgElementId) {
      this.$axios
        .get(`/proxy/api/org/biz/getBizOrgElementRelaRoleUuids`, {
          params: {
            bizOrgElementId
          }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            this.form.roleUuids.push(...data.data);
          }
        })
        .catch(error => {});
    },
    save() {
      return new Promise((resolve, reject) => {
        this.$refs.form.validate(valid => {
          if (valid) {
            let formData = JSON.parse(JSON.stringify(this.form));

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
              formData.i18ns = i18ns;
            }

            this.$axios
              .post(`/proxy/api/org/biz/saveBizOrgElement`, formData)
              .then(({ data }) => {
                if (data.code == 0) {
                  this.$message.success('保存成功');
                  resolve(data.data);
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
  }
};
</script>
