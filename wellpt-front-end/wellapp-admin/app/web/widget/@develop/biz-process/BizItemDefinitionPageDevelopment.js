import VuePageDevelopment from '@develop/VuePageDevelopment';

class BizItemDefinitionPageDevelopment extends VuePageDevelopment {
  get META() {
    return {
      name: '业务事项定义页面二开',
      hook: {
        saveItemDefinition: '保存事项定义'
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
      tableWidget.refetch(true);
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
      tableWidget.refetch(true);
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

  saveItemDefinition(event) {
    this.getVueWidgetById('sCWLldsAGELtUHxKaKhNcVfhnNlDiyLo').$children[0].save(event);
  }
}

export default BizItemDefinitionPageDevelopment;
