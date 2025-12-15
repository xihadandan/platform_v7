import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class pictureLibMangeTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '图片库分类管理表格二开',
      hook: {
        delete: "删除选中项",
      }
    };
  }

  mounted() {
    let _this = this;
    this.widget = this.getWidget();
    // 监听变化
    this.widget.pageContext.handleEvent('refetchPictureLibManangeTable', (arg) => {
      this.$widget.refetch();
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

  isUsedReq(ids, callback) {
    let widget = this.getWidget();
    $axios.get('/basicdata/img/category/queryAllCategory').then(({ data }) => {
      if (data.data) {
        var data = data.data;

        var hasPicturesCategories = [];
        for (var i = 0; i < this.$widget.selectedRows.length; i++) {
          var category = this.$widget.selectedRows[i];
          var uuid = category.UUID;
          var name = category.NAME;
          for (var j = 0; j < data.length; j++) {
            if (data[j].uuid === uuid && data[j].fileIDs.length > 0) {
              hasPicturesCategories.push(name);
            }
          }
        }
        if (hasPicturesCategories.length) {
          widget.$message.success('以下分类已存在图片：' + hasPicturesCategories.join('、') + '，不允许删除，请重新选择!');
          return;
        }
        if (typeof callback == "function") {
          callback(ids);
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
    _this.isUsedReq(ids, function (ids) {
      widget.$confirm({
        title: '确定要删除所选记录吗？',
        onOk() {
          _.each(ids, (item, index) => {
            $axios
              .delete('/basicdata/img/category/' + item)
              .then(({ data }) => {
                if (data.code == 0) {
                  if (index == ids.length - 1) {
                    widget.$message.success('删除成功');
                    let options = widget.getDataSourceProvider().options;
                    widget.refetch && widget.refetch(options);
                  }
                }
              })
          })
        },
        onCancel() { },
      });
    })
  }

}

export default pictureLibMangeTableDevelopment;
