import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class OrgUnitTableDevelopment extends WidgetTableDevelopment {
  deleteOrgUnit(evt) {

    let _this = this,
      uuid = evt.meta.uuid;

    _this.checkOrgUnitUsed(uuid).then(validResult => {
      if (validResult.valid) {
        _this.$widget.$confirm({
          title: '确认框',
          content: `确认删除单位——${evt.meta.name}? `,
          onOk() {
            $axios
              .get('/proxy/api/org/unit/delete', { params: { uuid } })
              .then(({ data }) => {
                if (data.code == 0) {
                  // 刷新表格
                  if (data.data) {
                    _this.$widget.refetch(true);
                    _this.$widget.$message.success('删除成功');
                  } else {
                    _this.$widget.$message.error('删除失败');
                  }
                } else {
                  _this.$widget.$message.error('删除失败');
                }
              })
              .catch(error => {
                _this.$widget.$message.error('删除失败');
              });
          }
        });
      } else {
        if (validResult.usedOrgs) {
          _this.$widget.$message.error(`该单位已经在组织${validResult.usedOrgs.map(org => org.name)}中使用，不可删除`);
        } else {
          _this.$widget.$message.error('删除失败');
        }
      }
    });
  }

  checkOrgUnitUsed(uuid) {
    return $axios
      .get('/proxy/api/org/unit/listUsedOrganization', { params: { uuid } })
      .then(({ data }) => {
        if (data.code == 0) {
          // 刷新表格
          if (data.data && data.data.length > 0) {
            return { valid: false, usedOrgs: data.data };
          } else {
            return { valid: true };
          }
        }
        return { valid: false };
      })
      .catch(error => {
        return { valid: false };
      });
  }





  enable(evt, success) {
    let _this = this,
      row = evt.meta,
      $evt = evt.$evt,
      enable = !$evt.currentTarget.__vue__.checked;
    let enableFunc = function () {
      $axios
        .get('/proxy/api/org/unit/enable', { params: { uuid: row.uuid, enable } })
        .then(({ data }) => {
          success(data.code === 0);
        })
        .catch(error => {
          success(false);
        });
    };
    if (!enable) {
      _this.checkOrgUnitUsed(row.uuid).then(validResult => {
        if (validResult.valid) {
          enableFunc.call(_this);
        } else {
          if (validResult.usedOrgs) {
            _this.$widget.$message.error(`该单位已经在组织${validResult.usedOrgs.map(org => org.name)}中使用，不可停用`);
          } else {
            _this.$widget.$message.error('停用失败');
          }
          success(true);
          _this.$widget.refetch();
        }
      });
    } else {
      enableFunc.call(_this);
    }
  }

  get META() {
    return {
      name: '组织单位表格二开',
      hook: {
        enable: '启用或停用单位',
        deleteOrgUnit: '删除单位'
      }
    };
  }
}



export default OrgUnitTableDevelopment;
