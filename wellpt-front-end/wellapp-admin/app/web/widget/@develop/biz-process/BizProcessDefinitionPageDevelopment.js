import VuePageDevelopment from '@develop/VuePageDevelopment';

class BizProcessDefinitionTableDevelopment extends VuePageDevelopment {
  get META() {
    return {
      name: '业务流程定义页面二开',
      hook: {
        deleteCategory: '删除业务分类',
        newBusinessModal: '新增业务弹出框',
        editBusinessModal: '编辑业务弹出框',
        deleteCategoryOrBusiness: '删除分类或业务',
        deleteBusiness: '删除业务'
      }
    };
  }

  mounted() {
    const _this = this;
    this.$widget.pageContext.handleEvent('categorySelect_biz', (selectedKeys, selectedKeysIncludeChildren, categoryUuid, businessId) => {
      // 表格数据过滤
      let tableWidget = _this.getVueWidgetById('CGpnfIUpijyboXQOviBohcvtEQWdPbHU');
      let dataSource = tableWidget.getDataSourceProvider();
      dataSource.removeParam('categoryUuid');
      dataSource.removeParam('businessId');
      if (categoryUuid) {
        dataSource.addParam('categoryUuid', categoryUuid);
      } else if (businessId) {
        dataSource.addParam('businessId', businessId);
      }
      tableWidget.refetch();
    });
    _this.handleEvent('MgdyovMePwClLutBqvNsKweVDJmDUaYk:treeNodeSelected', ({ selectedKeys, selected, node, selectedNodes }) => {
      _this.selectedTreeNodeKeys = selectedKeys;

      // 表格数据过滤
      let nodeData = _this.getSelectedTreeNodeData(selectedKeys);
      let tableWidget = _this.getVueWidgetById('CGpnfIUpijyboXQOviBohcvtEQWdPbHU');
      let dataSource = tableWidget.getDataSourceProvider();
      if (nodeData) {
        dataSource.removeParam('categoryUuid');
        dataSource.removeParam('businessId');
        if (nodeData.type == 'category') {
          dataSource.addParam('categoryUuid', nodeData.uuid);
        } else if (nodeData.type == 'business') {
          dataSource.addParam('businessId', nodeData.id);
        }
      } else {
        dataSource.removeParam('categoryUuid');
        dataSource.removeParam('businessId');
      }
      tableWidget.refetch();
    });
  }

  deleteCategory(data) {
    const _this = this;
    let nodeData = data;
    if (nodeData == null) {
      nodeData = this.getSelectedTreeNodeData(_this.selectedTreeNodeKeys);
      if (nodeData == null || nodeData.type != 'category') {
        _this.$widget.$message.error('请选择业务分类结点');
        return;
      }
    }

    _this.$widget.$confirm({
      title: '确认框',
      content: `确认删除业务分类[${nodeData.title}]？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post(`/proxy/api/biz/category/deleteAll?uuids=${[nodeData.key]}`)
          .then(({ data }) => {
            if (data.code == 0) {
              // 刷新左侧树
              _this.$widget.pageContext.emitEvent('MgdyovMePwClLutBqvNsKweVDJmDUaYk:refetch');
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(data.msg || '删除失败');
            }
          })
          .catch(({ response }) => {
            _this.$widget.$message.error((response.data && response.data.msg) || '删除失败');
          });
      }
    });
  }

  newBusinessModal() {
    const _this = this;
    // console.log("showBusinessModal");
    let title = '新建业务'; //domEvent.target.innerText.trim();
    // 获取树形组件
    let nodeData = _this.getSelectedTreeNodeData(_this.selectedTreeNodeKeys);
    if (nodeData == null || nodeData.type != 'category') {
      _this.$widget.$message.error('请选择业务分类');
      return;
    }
    _this.getPageContext().emitEvent('XXewfSbmvMgHmPJoxhEgZPNqsXkoczYP:showModal', null, null, title);
    _this.$widget.$nextTick(() => {
      let formWidget = _this.getVueWidgetById('piXVCNnyTMuiAnRIhMscpkiWIhJJoYqL');
      formWidget.setField('categoryUuid', nodeData.uuid);
    });
  }

  editBusinessModal({ key, title }) {
    // console.log('editBusinessModal', arguments);
    const _this = this;
    let nodeData = this.getSelectedTreeNodeData(key);
    if (nodeData == null) {
      _this.$widget.$message.error('请选择树结点');
      return;
    }

    // 编辑业务分类
    if (nodeData.type == 'category') {
      // 编辑分类
      _this.getPageContext().emitEvent('ZltyRcgcXmksRaCNPMoAlZeRgMcYYCho:showModal', null, null, title);
      _this.$widget.$nextTick(() => {
        let formWidget = _this.getVueWidgetById('xuIQSBCUypVXOkvkWXKCuUVTzcGGVmLR');
        nodeData.categoryUuid = nodeData.parentUuid;
        formWidget.setFormData(nodeData);
      });
    } else if (nodeData.type == 'business') {
      // 编辑业务
      _this.getPageContext().emitEvent('XXewfSbmvMgHmPJoxhEgZPNqsXkoczYP:showModal', null, null, title);
      _this.$widget.$nextTick(() => {
        let formWidget = _this.getVueWidgetById('piXVCNnyTMuiAnRIhMscpkiWIhJJoYqL');
        nodeData.categoryUuid = nodeData.parentUuid;
        formWidget.setFormData(nodeData);
      });
    } else {
      console.warn('unknow tree node data type', nodeData.type, nodeData);
    }
  }

  deleteCategoryOrBusiness({ key, title }) {
    // console.log('editBusinessModal', arguments);
    const _this = this;
    let nodeData = this.getSelectedTreeNodeData(key);
    if (nodeData == null) {
      _this.$widget.$message.error('请选择树结点');
      return;
    }

    // 删除业务分类
    if (nodeData.type == 'category') {
      _this.deleteCategory({ key, title });
    } else if (nodeData.type == 'business') {
      // 删除业务
      _this.deleteBusiness({ key, title });
    } else {
      console.warn('unknow tree node data type', nodeData.type, nodeData);
    }
  }

  deleteBusiness(data) {
    const _this = this;
    let nodeData = data;
    if (nodeData == null) {
      nodeData = this.getSelectedTreeNodeData(_this.selectedTreeNodeKeys);
      if (nodeData == null || nodeData.type != 'business') {
        _this.$widget.$message.error('请选择业务结点');
        return;
      }
    }

    _this.$widget.$confirm({
      title: '确认框',
      content: `确认删除业务[${nodeData.title}]？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post(`/proxy/api/biz/business/deleteAll?uuids=${[nodeData.key]}`)
          .then(({ data }) => {
            if (data.code == 0) {
              // 刷新左侧树
              _this.$widget.pageContext.emitEvent('MgdyovMePwClLutBqvNsKweVDJmDUaYk:refetch');
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(data.msg || '删除失败');
            }
          })
          .catch(({ response }) => {
            _this.$widget.$message.error((response.data && response.data.msg) || '删除失败');
          });
      }
    });
  }

  getSelectedTreeNodeData(selectedKeys) {
    const _this = this;
    let selectedKey = selectedKeys;
    if (selectedKeys == null) {
      return null;
    } else if (Array.isArray(selectedKeys)) {
      if (selectedKey.length != 1) {
        return null;
      }
      selectedKey = selectedKeys[0];
    } else {
      selectedKey = selectedKeys;
    }

    let treeWidget = _this.getVueWidgetById('MgdyovMePwClLutBqvNsKweVDJmDUaYk');
    let treeNodes = treeWidget.$data.treeNodes || [];
    let getTreeNodeByKey = function (treeNodes, key) {
      for (let i = 0; i < treeNodes.length; i++) {
        let treeNode = treeNodes[i];
        if (treeNode.key === key) {
          return treeNode._originalData;
        } else if (treeNode.children) {
          let data = getTreeNodeByKey(treeNode.children, key);
          if (data != null) {
            return data;
          }
        }
      }
      return null;
    };

    return getTreeNodeByKey(treeNodes, selectedKey);
  }
}

export default BizProcessDefinitionTableDevelopment;
