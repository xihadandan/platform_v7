import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class UserListTableDevelopment extends WidgetTableDevelopment {



  lockUser(evt) {
    this.lockUserAction(evt.meta.UUID);
  }

  lockUserAction(uuid, locked = true) {
    let _this = this;
    $axios
      .get('/proxy/api/user/lockUser', { params: { uuid, locked } })
      .then(({ data }) => {
        if (data.code == 0) {
          // 刷新表格
          _this.$widget.refetch();
          _this.$widget.$message.success(`${locked ? '冻结' : '解除冻结'}成功`);
        }
      })
      .catch(error => {
        _this.$widget.$message.error(`${locked ? '冻结' : '解除冻结'}失败`);
      });
  }


  /**
   * @hook
   * @description 解冻用户
   */
  unlockUser(evt) {
    this.lockUserAction(evt.meta.UUID, false);
  }

  disableUser(evt) {
    this.enableUserAction(evt.meta.UUID, false);
  }

  enableUser(evt) {
    this.enableUserAction(evt.meta.UUID, true);
  }


  enableUserAction(uuid, enable = true) {
    let _this = this;
    $axios
      .get('/proxy/api/user/enableUser', { params: { uuid, enable } })
      .then(({ data }) => {
        if (data.code == 0) {
          // 刷新表格
          _this.$widget.refetch();
          _this.$widget.$message.success(`${enable ? '启用' : '禁用'}成功`);
        }
      })
      .catch(error => {
        _this.$widget.$message.error(`${enable ? '启用' : '禁用'}失败`);
      });
  }


  /**
   * @hook
   * @description 删除用户
   */
  deleteUser(evt) {
    let _this = this,
      uuid = evt.meta.UUID;
    $axios
      .get('/proxy/api/user/delete', { params: { uuid } })
      .then(({ data }) => {
        if (data.code == 0) {
          _this.$widget.$message.success('删除成功');
          _this.$widget.refetch();
        }
      })
      .catch(error => {
        _this.$widget.$message.error('删除失败');
      });
  }

  /**
   * @hook
   * @description 重置密码
   */
  resetPassword(evt) {
    let _this = this,
      uuid = evt.meta.UUID;
    $axios
      .get('/proxy/api/user/resetPassword', { params: { uuid } })
      .then(({ data }) => {
        if (data.code == 0) {
          _this.$widget.$message.success('重置密码成功');
        }
      })
      .catch(error => {
        _this.$widget.$message.error('重置密码失败');
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
        }
      })
      .catch(error => {
        _this.$widget.$message.error('批量删除用户失败');
      });
  }



  get META() {
    return {
      name: '用户管理列表二开',
      hook: {
        enableUser: '启用用户',
        disableUser: '禁用用户',
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



export default UserListTableDevelopment;
