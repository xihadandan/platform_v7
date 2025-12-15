import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class DmsLibraryCategoryTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '数据管理_文件库_库分类表格二开',
      hook: {
        edit: '编辑库分类',
        delete: '删除库分类',
      }
    };
  }

  /**
   * 编辑库分类
   *
   * @param {*} evt
   * @returns
   */
  edit(evt) {
    let _this = this;
    let formData = evt.meta || {};
    let title = (evt.eventParams && evt.eventParams.title) || '编辑库分类';

    _this.getPageContext().emitEvent('TMgVURMYxfDAInDGmUnJBFdshVLfYhRx:showModal', null, null, title);
    _this.$widget.$nextTick(() => {
      let formWidget = _this.getVueWidgetById('gqdkqNtzvMorChoWCyFVnekFguMvIKcI');
      formWidget.setFormData(formData);
    });
  }

  /**
   * 删除库分类
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
      content: `确定删除库分类[${selectedRows.map(row => row.name)}]吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post(`/proxy/api/dms/library/category/deleteAll?uuids=${uuids}`)
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新库分类
              _this.$widget.pageContext.emitEvent('RDGnOnFlLdLBWvtttmjAIxsJScKifTrt:refetch');
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(result.msg);
            }
          })
          .catch(({ response }) => {
            _this.$widget.$message.error(
              (response && response.data && response.data.msg) || '删除失败'
            );
          });
      }
    });
  }

}

export default DmsLibraryCategoryTableDevelopment;
