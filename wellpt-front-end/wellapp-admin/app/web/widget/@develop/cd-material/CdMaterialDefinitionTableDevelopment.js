import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class CdMaterialDefinitionTableDevelopment extends WidgetTableDevelopment {


  get META() {
    return {
      name: '材料定义表格二开',
      hook: {
        deleteMaterial: '删除材料'
      }
    };
  }

  deleteMaterial(evt) {
    let _this = this;
    let uuid = evt.meta.uuid;
    evt.$evtWidget.$confirm({
      title: "确认框",
      content: "确认删除材料？",
      onOk() {
        $axios
          .post(`/proxy/api/material/definition/delete?uuid=${uuid}`)
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

export default CdMaterialDefinitionTableDevelopment;
