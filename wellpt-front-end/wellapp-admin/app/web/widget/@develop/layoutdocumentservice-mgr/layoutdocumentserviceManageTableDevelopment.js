import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class layoutdocumentserviceManageTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '版式文档服务配置表格二开',
      hook: {
        delete: "删除选中项",
        deleteItem: "删除当前项"
      }
    };
  }

  mounted() {
    let dataSourceProvider = this.getWidget().getDataSourceProvider();
    let widget = this.getWidget();
    let _this = this;
    // 监听分类点击事件变化
    widget.pageContext.handleEvent('refetchlayoutdocumentserviceManageTable', (arg) => {
      widget.refetch();
      if (arg) {
        if (arg.saveSuccess) {
          widget.$message.success("保存成功");
        }
      }
    });
  }
  delete() {
    const ids = _.map(this.$widget.selectedRows, "UUID");
    if (ids.length == 0) {
      this.getWidget().$message.error('请选择记录！')
      return false;
    }
    this.deleteReq(ids);
  }

  deleteItem($event) {
    this.deleteReq([$event.meta.UUID]);
  }

  deleteReq(ids) {
    let _this = this;
    let widget = this.getWidget();
    widget.$confirm({
      title: '确定要删除所选记录吗？',
      onOk() {
        $axios
          .post('/api/basicdata/layoutDocumentServiceConf/deleteByUuids', { uuids: ids })
          .then(({ data }) => {
            if (data.code == 0) {
              widget.$message.success('删除成功');
              let options = widget.getDataSourceProvider().options;
              widget.refetch && widget.refetch(options);
            }
          })
      },
      onCancel() { },
    });
  }

}

export default layoutdocumentserviceManageTableDevelopment;
