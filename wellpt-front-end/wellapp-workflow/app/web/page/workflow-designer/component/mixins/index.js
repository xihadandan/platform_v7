import constant, { availableBizOrgOptions } from '../designer/constant';

export default {
  inject: ['designer', 'workFlowData', 'graph'],
  data() {
    return {
      availableBizOrgOptions
    };
  },
  computed: {
    orgVersionIds() {
      return this.workFlowData.property.orgVersionIds || [];
    },
    orgVersionId() {
      return this.workFlowData.property.orgVersionId;
    },
    // 默认行政组织
    defaultOrgData() {
      let orgData = {};
      if (this.designer.defaultOrgData) {
        orgData = this.designer.defaultOrgData;
      }
      return orgData;
    },
    // 使用组织Id
    useOrgId() {
      let orgId = this.defaultOrgData.id;
      const property = this.workFlowData.property;
      if (property.useDefaultOrg === '0') {
        orgId = property.orgId;
      }
      return orgId;
    },
    useOrgData() {
      let orgData = {}
      const info = this.designer.getOrgInfoById(this.useOrgId);
      if (info) {
        orgData = info
      }
      return orgData
    },
    // 使用组织树
    useOrgTreeData() {
      let treeData = [];
      const orgId = this.useOrgId;
      const property = this.workFlowData.property;

      const getTreeNode = ({ orgId, bizOrgId, availableBizOrg }) => {
        let node;
        if (availableBizOrg === this.availableBizOrgOptions[0]['value']) {
          bizOrgId = ''
          node = this.designer.getBizOrgTreeNode(orgId, bizOrgId, this.parentSelect);
          node.bizOrgs = []
        } else if (availableBizOrg === this.availableBizOrgOptions[1]['value']) {
          bizOrgId = ''
          node = this.designer.getBizOrgTreeNode(orgId, bizOrgId, this.parentSelect);
        } else {
          node = this.designer.getBizOrgTreeNode(orgId, bizOrgId, this.parentSelect);
        }
        return node;
      };

      if (orgId) {
        const node = getTreeNode(property);
        if (node) {
          treeData.push(node);
        }
      }
      if (property.enableMultiOrg === '1' && property.multiOrgs && property.multiOrgs.length) {
        property.multiOrgs.forEach(item => {
          const node = getTreeNode(item);
          if (node) {
            const hasIndex = treeData.findIndex(f => node.uuid === f.uuid)
            if (hasIndex > -1) {
              // const hasNode = treeData[hasIndex]
              // if (property.availableBizOrg !== this.availableBizOrgOptions[0]['value']) {
              //   // 业务组织可用时
              //   let bizOrgs = hasNode.bizOrgs.concat(node.bizOrgs) // 合并去重
              //   const unique = bizOrgs.reduce((accumulator, currentValue) => {
              //     if (accumulator.findIndex(a => a.uuid === currentValue.uuid) === -1) {
              //       accumulator.push(currentValue)
              //     }
              //     return accumulator
              //   }, [])
              //   hasNode.bizOrgs = unique
              // }
            } else {
              treeData.push(node);
            }
          }
        });
      }
      return treeData;
    },
    // {orgId: [bizOrgId]}
    useOrgIdMap() {
      let orgMap = {}
      orgMap[this.useOrgId] = []
      this.useOrgTreeData.forEach(item => {
        let bizOrgs
        if (item.bizOrgs) {
          bizOrgs = item.bizOrgs.map(b => b.id)
        }
        if (bizOrgs) {
          orgMap[item.id] = bizOrgs
        } else {
          orgMap[item.id] = []
        }
      });
      return orgMap
    },
    // 是否发起环节
    isStartTask() {
      let show = false;
      if (this.graph.instance) {
        const directionData = this.graph.instance.directionsData;
        const hasIndex = directionData.findIndex(item => {
          return item.toID === this.formData.id && item.fromID === constant.StartFlowId;
        });
        if (hasIndex > -1) {
          show = true;
        }
      }
      return show;
    },
    bizOrgIdRoleMap() {
      return this.designer.bizOrgIdRoleMap
    },
    bizOrgIdRoleOptions() {
      return this.designer.bizOrgIdRoleOptions
    }
  },
  methods: {
    draggable(data, parentSelector, dragHandleClass, onEvent) {
      let _this = this;
      this.$nextTick(() => {
        import('sortablejs').then(Sortable => {
          Sortable.default.create(parentSelector, {
            handle: dragHandleClass,
            onChoose: e => { },
            onUnchoose: (onEvent && onEvent.onUnchoose) || function () { },
            onMove: (onEvent && onEvent.onMove) || function () { },
            onEnd:
              (onEvent && onEvent.onEnd) ||
              function (e) {
                let temp = data.splice(e.oldIndex, 1)[0];
                data.splice(e.newIndex, 0, temp);
                _this.draggable(data, parentSelector, dragHandleClass, onEvent);
                if (onEvent && onEvent.afterOnEnd) {
                  onEvent.afterOnEnd();
                }
              }
          });
        });
      });
    },
    setSwimlaneStyleByTagName(tagName, prop, value) {
      const tdEls = document.querySelectorAll(`.node-swimlane-table ${tagName}`);
      for (let index = 0; index < tdEls.length; index++) {
        const item = tdEls[index];
        item.style.cssText += `;${prop}: ${Number(value)}px`;
      }
    },
  }
}