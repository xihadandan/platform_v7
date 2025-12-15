import { isEmpty } from 'lodash';
class BizProcessStartItem {

  constructor($widget) {
    this.$widget = $widget;
  }

  /**
   * 发起事项
   * 
   * @param {*} param0 
   */
  startItem({ processDefId, processNodeId, itemCode, eventParams = {} }) {
    const _this = this;
    _this.eventParams = eventParams;
    if (!isEmpty(itemCode)) {
      // 通过事项发起
      _this.startByItemCode(processDefId, itemCode);
    } else {
      // 通过业务流程定义发起
      _this.startByProcessDefId(processDefId, processNodeId);
    }
  }


  // 通过业务流程定义发起
  startByProcessDefId(processDefId, processNodeId) {
    const _this = this;
    $axios.get(`/proxy/api/biz/process/definition/getById/${processDefId}`).then(({ data }) => {
      if (data.data) {
        let processDefinition = data.data;
        _this.showChooseProcessItemDialogByProcessDefinition(processDefinition, processNodeId);
      }
    });
  }

  showChooseProcessItemDialogByProcessDefinition(processDefinition, processNodeId) {
    const _this = this;
    let processDefinitionJson = JSON.parse(processDefinition.definitionJson);
    if (processNodeId) {
      let node = _this.getNodeDefinitionById(processDefinitionJson.nodes, processNodeId);
      if (node) {
        processDefinitionJson.nodes = [node];
      }
    }

    // 材料选择弹出框
    let Modal = Vue.extend({
      template: `<a-modal title="选择业务事项" :visible="visible" width="900px" @ok="handleOk" @cancel="handleCancel">
      <div style="height: 400px; overflow:auto">
        <BizChooseProcessItem ref="chooseProcessItem" :processDefinition="processDefinition"></BizChooseProcessItem>
      </div>
      </a-modal>`,
      components: { BizChooseProcessItem: () => import('./template/biz-choose-process-item.vue') },
      data: function () {
        return { visible: true, processDefinition: processDefinitionJson };
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          let itemIds = this.$refs.chooseProcessItem.getSelectedItemIds();
          if (isEmpty(itemIds)) {
            this.$messages.error('请选择办理事项！');
            return;
          }
          _this.openStartItemWindow(processDefinition.id, itemIds);

          this.visible = false;
          this.$destroy();
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  getNodeDefinitionById(nodes = [], processNodeId) {
    for (let index = 0; index < nodes.length; index++) {
      let nodeDefinition = nodes[index];
      if (nodeDefinition.id == processNodeId) {
        return nodeDefinition;
      }
      if (nodeDefinition.nodes) {
        let definiton = this.getNodeDefinitionById(nodeDefinition.nodes, processNodeId);
        if (definiton) {
          return definiton;
        }
      }
    }
    return null;
  }

  openStartItemWindow(processDefId, processItemIds) {
    const _this = this;
    let eventParams = _this.eventParams;
    let urlParams = {};
    if (!isEmpty(eventParams.formUuid) && !isEmpty(eventParams.dataUuid)) {
      urlParams.formUuid = eventParams.formUuid;
      urlParams.dataUuid = eventParams.dataUuid;
    }
    let url = `/biz/process/item/instance/new/${processDefId}?processItemIds=${processItemIds.join(
      ';'
    )}&_requestCode=${new Date().getTime()}`;
    // 附加参数
    for (let key in eventParams) {
      if (key.startsWith('ep_')) {
        url += `&${key}=${eventParams[key]}`;
      }
    }
    if (eventParams.useUniqueName == 'true') {
      window.open(this.addSystemPrefix(url), `startItem_${processDefId}`);
    } else {
      window.open(this.addSystemPrefix(url));
    }
  }

  addSystemPrefix(url) {
    const _this = this;
    if (_this.$widget._$SYSTEM_ID && url && !url.startsWith("/sys/")) {
      url = `/sys/${_this.$widget._$SYSTEM_ID}/_${url}`;
    }
    return url;
  }

  // 通过事项发起
  startByItemCode(processDefId, itemCode) {
    const _this = this;
    _this.getStartItems(processDefId, itemCode.split(';')).then(startItems => {
      if (isEmpty(startItems)) {
        _this.$widget.$message.error('事项不存在！');
        return;
      }
      let itemIds = startItems.map(item => item.id);
      _this.openStartItemWindow(processDefId, itemIds);
    });
  }

  getStartItems(processDefId, itemCodes) {
    let getItems = function (processNodes, items) {
      for (let i = 0; i < processNodes.length; i++) {
        let processNode = processNodes[i];
        if (processNode.items) {
          for (let j = 0; j < processNode.items.length; j++) {
            items.push(processNode.items[j]);
          }
        }
        getItems(processNode.nodes || [], items);
      }
    };
    return $axios.get(`/proxy/api/biz/process/definition/listProcessNodeItemById/${processDefId}`).then(({ data }) => {
      let allItems = [];
      if (data.data) {
        getItems(data.data, allItems);
      }
      let items = [];
      itemCodes.forEach(itemcode => {
        let item = allItems.find(item => item.itemCode == itemcode);
        if (item) {
          items.push(item);
        }
      });
      return items;
    });
  }

}

export default BizProcessStartItem;