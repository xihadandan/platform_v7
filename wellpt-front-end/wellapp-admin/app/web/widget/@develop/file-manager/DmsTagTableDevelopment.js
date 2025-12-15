import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class DmsLibraryCategoryTableDevelopment extends WidgetTableDevelopment {
  get META() {
    return {
      name: '数据管理_文件库_标签表格二开',
      hook: {
        delete: '删除标签',
      }
    };
  }

  mounted() {
    const _this = this;
    $axios.get('/proxy/api/system/getAppSystemParamByKey?key=file-manager.free-tag.enabled').then(({ data: result }) => {
      if (result.data) {
        _this.createSwitchButton(result.data);
      }
    });
  }

  createSwitchButton(freeTagParam) {
    const _this = this;
    let buttonRightEl = _this.$widget.$el.querySelector('.widget-table-header-button-right');
    if (!buttonRightEl) {
      return;
    }
    buttonRightEl.style.flex = '0 0 150px';
    let btnCnt = document.createElement('div');
    buttonRightEl.appendChild(btnCnt);
    let SwitchButton = Vue.extend({
      template: `<div>
                  自由标签
                  <a-popover placement="left">
                    <template slot="content">允许当前系统下的文档添加和显示自由标签</template>
                    <a-icon type="info-circle" />
                  </a-popover>
                  <a-switch v-model="enabled" @change="changeSwitch"></a-switch>
                </div>`,
      props: {
        freeTagParam: {
          type: Object,
          default() {
            return {
              name: '文件库自由标签',
              propKey: 'file-manager.free-tag.enabled',
              propValue: 'false',
              remark: '文件库自由标签，true开启false关闭，默认关闭'
            }
          }
        }
      },
      data() {
        return {
          enabled: this.freeTagParam.propValue === 'true'
        }
      },
      mounted() {
        setTimeout(() => {
          buttonRightEl.style.flex = '0 0 150px';
        }, 1);
      },
      methods: {
        changeSwitch(checked) {
          if (checked) {
            this.freeTagParam.propValue = 'true';
          } else {
            this.freeTagParam.propValue = 'false';
          }
          this.saveFreeTagParam();
        },
        saveFreeTagParam() {
          $axios.post('/proxy/api/system/saveAppSystemParam', this.freeTagParam);
        }
      }
    });
    new SwitchButton({
      propsData: {
        freeTagParam
      }
    }).$mount(btnCnt);
  }

  onTableRowDataChange(data) {
    let list = data.data || [];
    let rootNodes = list.filter(item => !item.parentUuid);
    let childNodes = list.filter(item => !!item.parentUuid);
    childNodes.forEach(child => {
      let rootNode = rootNodes.find(root => root.uuid === child.parentUuid);
      let children = rootNode.children || [];
      children.push(child);
      rootNode.children = children;
    });
    data.data.length = 0;
    data.data.push(...rootNodes);
    this.$widget.expandedRowKeys = rootNodes.map(node => node.uuid);
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
      content: `确定删除标签[${selectedRows.map(row => row.name)}]吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post(`/proxy/api/dms/tag/deleteAll?uuids=${uuids}`)
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新库分类
              _this.$widget.pageContext.emitEvent('PNSRzVAarKSxuXtChqfBYfVgZoEXcQDo:refetch');
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
