<template>
  <OrgSelect
    :orgType="['MyOrg', 'MySystemRole']"
    ref="orgSelect"
    v-model="selectIds"
    :orgTypeExtensions="[{ label: '角色', value: 'MySystemRole' }]"
    :params="params"
    v-if="!loading"
  />
  <div style="text-align: center" v-else>
    <a-spin />
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { compareArrayDifference } from '@framework/vue/utils/util';

export default {
  name: 'SystemPageIndexUserSelect',
  props: {},
  inject: ['$event', 'pageContext'],
  components: { OrgSelect },
  computed: {},
  data() {
    return {
      selectIds: [],
      originalOrgElementIds: [],
      roleUuid: undefined,
      loading: true,
      params: {
        system: this._$SYSTEM_ID
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchRoleMembers();
  },
  mounted() {},
  methods: {
    fetchRoleMembers() {
      this.loading = true;
      return new Promise((resolve, reject) => {
        if (this.$event != undefined && this.$event.meta != undefined) {
          let roleId = 'ROLE_VIEW_PAGE_' + this.$event.meta.ID;
          $axios
            .get(`/proxy/api/security/role/getRoleMembersById`, {
              params: {
                roleId
              }
            })
            .then(({ data }) => {
              if (data.data) {
                console.log('页面角色关联成员信息: ', data.data);
                let role = data.data;

                this.roleUuid = role.uuid;
                this.originalNestedRoles = [];
                $axios
                  .all([
                    $axios.get(`/proxy/api/security/role/getRolesByNestedRole`, { params: { uuid: role.uuid } }),
                    $axios.get(`/proxy/api/org/organization/version/getOrgElementsRelaRole`, {
                      params: {
                        roleUuid: role.uuid,
                        system: this._$SYSTEM_ID
                      }
                    }),
                    $axios.get(`/proxy/api/org/organization/user/getRoleRelaUsers`, {
                      params: { roleUuid: role.uuid, system: this._$SYSTEM_ID }
                    })
                  ])
                  .then(
                    $axios.spread((res1, res2, res3) => {
                      this.originalOrgElementIds = [];
                      let parentRoles = res1.data.data;
                      if (parentRoles) {
                        parentRoles.forEach(item => {
                          if (item.systemDef != '1') {
                            this.originalOrgElementIds.push(item.uuid);
                            this.selectIds.push(item.uuid);
                          }
                        });
                      }
                      let orgElements = res2.data.data;
                      if (orgElements) {
                        orgElements.forEach(item => {
                          this.originalOrgElementIds.push(item.id);
                          this.selectIds.push(item.id);
                        });
                      }
                      let users = res3.data.data;
                      if (users) {
                        users.forEach(item => {
                          this.originalOrgElementIds.push(item.userId);
                          this.selectIds.push(item.userId);
                        });
                      }
                      this.loading = false;
                    })
                  )
                  .catch(error => {});
                resolve(data.data);
              } else {
                // 修复数据：创建首页角色/权限
                this.createPageDefaultRoleAndPrivilege(this.$event.meta);
              }
            })
            .catch(error => {});
        } else {
          resolve();
        }
      });
    },
    createPageDefaultRoleAndPrivilege(page) {
      $axios
        .get(`/proxy/api/security/privilege/getPrivilegeBeanByCode/${'PRIVILEGE_PAGE_' + page.ID}`, {
          params: {}
        })
        .then(({ data }) => {
          if (data.data) {
            let prg = data.data;
            this.createDefaultPageRole(page.ID, page.APP_ID, prg.uuid, page.TENANT == null ? null : page.APP_ID).then(() => {
              this.fetchRoleMembers();
            });
          } else {
            this.createDefaultPagePrivilege(page.ID, page.APP_ID, page.TENANT == null ? null : page.APP_ID).then(() => {
              this.fetchRoleMembers();
            });
          }
        })
        .catch(error => {});
    },
    createDefaultPagePrivilege(pageId, appId, system) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/proxy/api/security/privilege/savePrivilegeResource', {
            appId,
            name: '页面访问权限',
            code: 'PRIVILEGE_PAGE_' + pageId,
            enabled: true,
            systemDef: 1,
            system,
            otherResources: [{ type: 'appPageDefinition', resourceUuid: pageId }]
          })
          .then(({ data }) => {
            this.createDefaultPageRole(pageId, appId, data.data, system).then(() => {
              resolve();
            });
          });
      });
    },
    createDefaultPageRole(pageId, appId, privilegeUuid, system) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/security/role/updateRoleMember`, [
            {
              role: {
                id: 'ROLE_VIEW_PAGE_' + pageId,
                code: 'ROLE_VIEW_PAGE_' + pageId,
                name: '页面访问角色',
                systemDef: 1,
                system,
                appId
              },
              privilegeAdded: [privilegeUuid]
            }
          ])
          .then(({ data }) => {
            resolve();
          })
          .catch(error => {});
      });
    },

    saveConfig() {
      // cxTdeuKtCxGBAZlzvyJxcxrCtTjGOhqC:closeModal
      // 创建首页访问角色，关联首页权限
      let roleResult = compareArrayDifference(this.originalOrgElementIds, this.selectIds);
      let formData = {
        role: {
          uuid: this.roleUuid
        },
        parentRoleRemoved: [],
        parentRoleAdded: [],
        userIdRemoved: [],
        userIdAdded: [],
        orgElementRemoved: [],
        orgElementAdded: []
      };
      for (let key in roleResult) {
        let list = roleResult[key];
        list.forEach(item => {
          if (item.startsWith('U_')) {
            formData[key == 'from' ? 'userIdRemoved' : 'userIdAdded'].push(item);
          } else if (item.indexOf('_') != -1) {
            formData[key == 'from' ? 'orgElementRemoved' : 'orgElementAdded'].push(item);
          } else {
            formData[key == 'from' ? 'parentRoleRemoved' : 'parentRoleAdded'].push(item);
          }
        });
      }

      if (
        formData.parentRoleRemoved.length ||
        formData.parentRoleAdded.length ||
        formData.orgElementRemoved.length ||
        formData.orgElementAdded.length ||
        formData.userIdRemoved.length ||
        formData.userIdAdded.length
      ) {
        $axios
          .post(`/proxy/api/security/role/updateRoleMember`, [formData])
          .then(({ data }) => {
            if (data.data) {
              this.$message.success('保存成功');
              this.pageContext.emitEvent(`cxTdeuKtCxGBAZlzvyJxcxrCtTjGOhqC:closeModal`);
              let labels = [];
              this.$refs.orgSelect.valueNodes.forEach(n => {
                labels.push(n.title);
              });
              this.pageContext.emitEvent(`${this.$event.meta.ID}:refetchPageUsers`, labels);
              $axios.post(`/proxy/api/security/role/publishRoleUpdatedEvent?uuid=${data.data[0]}`);
            }
          })
          .catch(error => {});
      } else {
        this.pageContext.emitEvent(`cxTdeuKtCxGBAZlzvyJxcxrCtTjGOhqC:closeModal`);
      }
    }
  },
  META: {
    method: {
      saveConfig: '保存配置'
    }
  }
};
</script>
