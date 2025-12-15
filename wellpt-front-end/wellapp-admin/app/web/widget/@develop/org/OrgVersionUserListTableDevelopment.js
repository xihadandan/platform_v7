import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class OrgUserListTableDevelopment extends WidgetTableDevelopment {
  mounted() {
    let dataSourceProvider = this.getWidget().getDataSourceProvider();
    let widget = this.getWidget();
    let _this = this;

    // 监听组织版本UUID变化
    widget.pageContext.handleEvent('refetchOrgVersionUserList', ({ uuid, orgElementId, orgRoleUuid, queryNoDeptNoJob, queryQuitJobUser }) => {
      dataSourceProvider.clearParams();
      if (!uuid) {
        console.error('组织版本UUID参数丢失');
        return;
      } else {
        dataSourceProvider.addParam('orgVersionUuid', uuid);
      }

      widget.pageContext.emitEvent('refetchAllUserOptions');

      if (orgElementId) {
        dataSourceProvider.addParam('orgElementId', orgElementId);
      }
      if (orgRoleUuid) {
        dataSourceProvider.addParam('orgRoleUuid', orgRoleUuid);
      }
      if (queryNoDeptNoJob) {
        dataSourceProvider.addParam('queryNoDeptNoJob', queryNoDeptNoJob);
      }
      if (queryQuitJobUser) {
        dataSourceProvider.addParam('queryQuitJobUser', queryQuitJobUser);
      }
      widget.refetch();
    });

    // 获取所有用户
    widget.pageContext.handleEvent(`refetchAllUserOptions`, () => {
      $axios
        .get(`/proxy/api/user/org/getAllUser`, {
          params: { orgVersionUuid: _this.getPageState().uuid }
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            let options = [];
            data.data.forEach(d => {
              options.push({ label: d.userName, value: d.userId });
            });
            _this.commitPageState({ allUserOptions: options });
          }
        });
    });

    // 挂载无部门人员按钮
    widget.pageContext.mountVueComponent(
      {
        template: `<a-badge style="margin-right:10px" :count="vPageState.noDeptUserCount"><a-button  @click.stop="clickQuery">无部门人员</a-button></a-badge>`,
        propsData: {
          vPageState: this.getPageState()
        },
        methods: {
          clickQuery() {
            _this.getPageContext().emitEvent(`refetchOrgVersionUserList`, { uuid: _this.getPageState().uuid, queryNoDeptNoJob: true });
          }
        }
      },
      document.querySelector("button[code='noDeptUser']")
    );
  }

  lockUser(evt) {
    this.lockUserAction(evt.meta.uuid);
  }

  lockUserAction(uuid, locked = true) {
    let _this = this;
    $axios
      .get('/proxy/api/user/lockUser', { params: { uuid, locked } })
      .then(({ data }) => {
        if (data.code == 0) {
          // 刷新表格
          _this.$widget.refetch();
          _this.$widget.$message.success('冻结成功');
        }
      })
      .catch(error => {
        _this.$widget.$message.error('冻结失败');
      });
  }


  /**
   * @hook
   * @description 解冻用户
   */
  unlockUser(evt) {
    this.lockUserAction(evt.meta.uuid, false);
  }

  /**
   * @hook
   * @description 删除用户
   */
  deleteUser(evt) {
    let _this = this,
      uuid = evt.meta.uuid;
    $axios
      .get('/proxy/api/user/delete', { params: { uuid } })
      .then(({ data }) => {
        if (data.code == 0) {
          _this.$widget.$message.success('删除成功');
          _this.$widget.refetch();
          _this.emitEvent('refetchUserCountStatics');
        }
      })
      .catch(error => {
        _this.$widget.$message.error('删除成功');
      });
  }

  /**
   * @hook
   * @description 重置密码
   */
  resetPassword(evt) {
    let _this = this,
      uuid = evt.meta.uuid;
    $axios
      .get('/proxy/api/user/resetPassword', { params: { uuid } })
      .then(({ data }) => {
        if (data.code == 0) {
          _this.$widget.$message.success('重置密码成功');
        }
      })
      .catch(error => {
        _this.$widget.$message.error('重置密码成功');
      });
  }

  /**
   * @hook
   * @description 批量重置用户密码
   */
  batchResetPassword() {
    let _this = this,
      uuids = this.getWidget().getSelectedRowKeys();
    $axios
      .post('/proxy/api/user/batchResetPassword', { uuids })
      .then(({ data }) => {
        if (data.code == 0) {
          _this.$widget.$message.success('批量重置密码成功');
        }
      })
      .catch(error => {
        _this.$widget.$message.error('批量重置密码成功');
      });
  }

  /**
   * @hook
   * @description 批量删除用户
   */
  batchDeleteUser() {
    let _this = this,
      uuids = this.getWidget().getSelectedRowKeys();
    $axios
      .post('/proxy/api/user/batchDeleteUser', { uuids })
      .then(({ data }) => {
        if (data.code == 0) {
          //TODO: 删除结果提示，可能存在部分用户删除失败
          _this.$widget.$message.success('批量删除用户成功');
          _this.$widget.refetch();
          _this.emitEvent('refetchUserCountStatics');
        }
      })
      .catch(error => {
        _this.$widget.$message.error('批量删除用户成功');
      });
  }

  beforeDestroy() {
    this.getWidget().pageContext.offEvent('refetchOrgVersionUserList');
  }

  get META() {
    return {
      name: '组织版本用户管理列表二开',
      hook: {
        lockUser: '冻结用户',
        unlockUser: '解冻用户',
        deleteUser: '删除用户',
        resetPassword: '重置密码',
        batchResetPassword: '批量重置密码',
        batchDeleteUser: '批量删除用户'
      }
    }
  }
}



export default OrgUserListTableDevelopment;
