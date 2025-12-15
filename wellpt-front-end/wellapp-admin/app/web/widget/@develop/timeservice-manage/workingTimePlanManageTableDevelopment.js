import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class workingTimePlanManageTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '工作时间方案管理表格二开',
      hook: {
        setDefault: "设为默认/取消默认",
        deleteItem: "删除行数据"
      }
    };
  }

  mounted() {
    let _this = this;
    this.widget = this.getWidget();
    // 监听变化
    this.widget.pageContext.handleEvent('refetchWorkingTimePlanManangeTable', () => {
      this.widget.refetch();
    });
  }

  setDefault(arg) {
    $axios
      .get('/api/ts/work/time/plan/setAsDefault', { params: { uuid: arg.meta.uuid } })
      .then(({ data }) => {
        if (data.code == 0) {
          this.widget.$message.success('设置成功！');
          this.widget.refetch();
        } else {
          this.widget.$message.success('设置失败！');
        }
      })
  }

  delete() {
    const ids = _.map(this.$widget.selectedRows, "uuid");
    if (ids.length == 0) {
      this.widget.$message.error('请选择记录！')
      return false;
    }
    this.deleteReq(ids);
  }

  deleteItem($event) {
    this.deleteReq([$event.meta.uuid]);
  }

  isUsedReq(ids, callback) {
    $axios.post('/api/ts/work/time/plan/isUsed', { uuids: ids }).then(({ data }) => {
      if (data.data) {
        this.widget.$message.error(data.msg || '工作时间方案正被使用，无法删除！');
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
            .post('/api/ts/work/time/plan/deleteAll', { uuids: ids })
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

export default workingTimePlanManageTableDevelopment;
