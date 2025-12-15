import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class holidayManageTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '节假日管理表格二开',
      hook: {
        delete: "删除选中项",
        deleteItem: "删除行数据"
      }
    };
  }

  mounted() {
    let dataSourceProvider = this.getWidget().getDataSourceProvider();
    let widget = this.getWidget();
    let _this = this;
    // 监听变化
    widget.pageContext.handleEvent('refetchHolidayManangeTable', ({ classifyUuid }) => {
      widget.refetch();
    });
  }


  delete() {
    const ids = _.map(this.$widget.selectedRows, "uuid");
    if (ids.length == 0) {
      this.getWidget().$message.error('请选择记录！')
      return false;
    }
    this.deleteReq(ids);
  }

  deleteItem($event) {
    this.deleteReq([$event.meta.uuid]);
  }

  isUsedReq(ids, callback) {
    let widget = this.getWidget();
    $axios.post('/api/ts/holiday/isUsed', { uuids: ids }).then(({ data }) => {
      if (data.data) {
        _.each(data.data, (item, index) => {
          if (ids.indexOf(item.uuid) > -1) {
            ids.splice(ids.indexOf(item.uuid), 1);
          }
        })
        if (typeof callback == "function" && ids.length) {
          callback(ids, '确定要删除所选记录中未使用的记录吗？', data.msg);
        } else {
          this.getWidget().$message.error(data.msg);
        }
      } else {
        if (typeof callback == "function") {
          callback(ids);
        }
      }
    })
  }

  deleteReq(ids) {
    let _this = this;
    let widget = this.getWidget();
    _this.isUsedReq(ids, function (ids, title, content) {
      widget.$confirm({
        title: title || '确定要删除所选记录吗？',
        content: h => <div style="white-space:pre-wrap">{content}</div>,
        onOk() {
          $axios
            .post('/api/ts/holiday/deleteAll', { uuids: ids })
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
    })

  }

}

export default holidayManageTableDevelopment;
