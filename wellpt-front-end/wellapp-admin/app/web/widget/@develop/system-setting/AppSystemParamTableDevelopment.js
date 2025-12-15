import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { generateId } from '@framework/vue/utils/util';
import moment from 'moment';
import ProductPageList from '@pageAssembly/app/web/page/product-center/component/manager/product-page-list.vue'

class AppSystemParamTableDevelopment extends WidgetTableDevelopment {


  deleteRows(e) {
    let _this = this, selectedRows = e.meta.selectedRows;
    let ids = [];
    if (selectedRows != undefined) {
      for (let i = 0, len = selectedRows.length; i < len; i++) {
        ids.push(selectedRows[i].UUID);
      }
    } else {
      ids = [e.meta.UUID];
    }
    if (ids.length > 0) {
      this.$widget.$confirm({
        title: '确定要删除吗?',
        onOk() {
          _this.$widget.$loading();
          $axios
            .post(`/proxy/api/system/deleteAppSystemParam`, ids)
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$widget.$message.success('删除成功');
                _this.$widget.$loading(false);
                _this.refetch()
              } else {
                _this.$widget.$message.success('删除异常');
              }
            })
            .catch(error => {
              _this.$widget.$message.error('删除异常');
            });


        }
      })
    }


  }






  get META() {
    return {
      name: '系统参数表格二开',
      hook: {
        deleteRows: '删除',
      }
    }
  }
}
export default AppSystemParamTableDevelopment;
