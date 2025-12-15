<template>
  <div style="display: flex; flex-wrap: wrap">
    <a-spin v-if="loading" />
    <template v-else>
      <a-tag v-for="(item, i) in members" class="primary-color" :key="'tag_' + i" style="margin-bottom: 3px">{{ item }}</a-tag>
    </template>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'SystemPageUserTdRender',
  inject: ['pageContext'],
  props: {
    row: Object
  },
  components: {},
  computed: {},
  data() {
    return {
      members: [],
      loading: true
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.fetchPageUser().then(list => {});
    this.pageContext.handleEvent(`${this.row.ID}:refetchPageUsers`, labels => {
      this.members.splice(0, this.members.length);
      this.members.push(...labels);
    });
  },
  methods: {
    fetchPageUser() {
      return new Promise((resolve, reject) => {
        let roleId = 'ROLE_VIEW_PAGE_' + this.row.ID;
        this.members.splice(0, this.members.length);
        $axios
          .get(`/proxy/api/security/role/getRoleMembersById`, {
            params: {
              roleId
            }
          })
          .then(({ data }) => {
            if (data.data) {
              let role = data.data;
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
                    let parentRoles = res1.data.data;
                    if (parentRoles) {
                      parentRoles.forEach(item => {
                        if (item.systemDef != '1') {
                          this.members.push(item.name);
                        }
                      });
                    }
                    this.originalOrgElementIds = [];
                    let orgElements = res2.data.data;
                    if (orgElements) {
                      orgElements.forEach(item => {
                        this.members.push(item.name);
                      });
                    }
                    let users = res3.data.data;
                    if (users) {
                      users.forEach(item => {
                        this.members.push(item.userName);
                      });
                    }
                    this.loading = false;
                  })
                )
                .catch(error => {});
            }
            this.loading = false;
          })
          .catch(error => {});
      });
    }
  }
};
</script>
