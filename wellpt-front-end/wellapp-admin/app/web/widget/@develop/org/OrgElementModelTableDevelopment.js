import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class OrgElementModelTableDevelopment extends WidgetTableDevelopment {
  deleteOrgElementModel(uuid) {
    let _this = this;
    $axios.get('/proxy/api/org/elementModel/delete', { params: { uuid } }).then(({ data }) => {
      if (data.code == 0) {
        if (data.data) {
          // 刷新表格
          _this.$widget.refetch();
        } else {
          _this.$widget.$message.error('单元模型已被使用, 无法删除');
        }
      }
    });
  }

  enable(row, enable) {
    let _this = this;
    this.$widget.$set(row, 'loading', true);
    $axios.get('/proxy/api/org/elementModel/enable', { params: { uuid: row.uuid, enable } }).then(({ data }) => {
      _this.$widget.$set(row, 'loading', false);
      if (data.code !== 0) {
        row.enable = !enable;
      }
    });
  }

  mounted() {
    this.addClass('org-element-model-table');
  }

  // 添加样式
  addClass(className) {
    if (className) {
      let $el = this.$widget.$el;
      let classList = $el.classList;
      if (!classList.contains(className)) {
        classList.add(className);
      }
    }
  }

  get META() {
    return {
      name: '组织单元模型表格二开',
      hook: {
        enable: '启用或停用组织单元模型',
        deleteOrgElementModel: '删除组织单元模型'
      }
    };
  }
}

export default OrgElementModelTableDevelopment;
