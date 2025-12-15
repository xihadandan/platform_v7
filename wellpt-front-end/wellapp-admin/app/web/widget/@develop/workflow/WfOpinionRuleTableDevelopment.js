import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class WfOpinionRuleTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '流程管理_意见校验规则表格二开',
      hook: {
        delete: '删除意见校验规则'
      }
    };
  }

  /**
   * 删除意见校验规则
   *
   * @param {*} evt
   */
  delete(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    let uuids = selectedRows.map(row => row.uuid);
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    _this.$widget.$confirm({
      title: '确认框',
      content: `确定删除意见校验规则[${selectedRows.map(row => row.opinionRuleName)}]吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post(`/api/opinion/rule/delete`, {
            uuids
          })
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新信息格式
              _this.$widget.pageContext.emitEvent('MdtvmHBKiQYcvrOMkYEztwABFJOuyRzH:refetch');
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(result.msg);
            }
          })
          .catch(error => {
            _this.$widget.$message.error('删除失败');
          });
      }
    });
  }
}

export default WfOpinionRuleTableDevelopment;
