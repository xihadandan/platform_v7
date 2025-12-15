import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class WfFlowBusinessDefinitionTableDevelopment extends WidgetTableDevelopment {

  get META() {
    return {
      name: '流程业务定义表格二开',
      hook: {
        deleteFlowBusinessDefinition: '删除流程业务定义'
      }
    };
  }

  deleteFlowBusinessDefinition(evt) {
    let _this = this;
    let uuids = _this.$widget.getSelectedRowKeys();
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录');
      return;
    }
    evt.$evtWidget.$confirm({
      title: "确认框",
      content: "确认删除流程业务定义？",
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .get(`/proxy/api/workflow/business/definition/deleteAll?uuids=${uuids}`)
          .then(({ data }) => {
            if (data.code == 0) {
              // 刷新表格
              _this.$widget.refetch();
              _this.$widget.$message.success('删除成功');
            }
          })
          .catch(error => {
            _this.$widget.$message.error('删除失败');
          });
      }
    })
  }

}

export default WfFlowBusinessDefinitionTableDevelopment;
