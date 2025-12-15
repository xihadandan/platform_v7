<template>
  <a-card :style="{ width: vWidth, height: vHeight }" size="small" class="data-model-collect-node" :id="id">
    <template slot="title">
      <a-avatar icon="appstore" shape="square" class="ant-avatar" :style="{ backgroundColor: iconbgColor }"></a-avatar>
      {{ title }}
    </template>
    <template slot="extra">
      <a-button type="icon" size="small" @click="onSetting" title="设置"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
      <a-button type="icon" size="small" @click="onDeleteNode" title="删除">
        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
      </a-button>
    </template>

    <!-- <a-divider orientation="left" style="position: absolute; top: -30px; left: 0px">
      <a-icon type="appstore" />
      {{ title }}
    </a-divider> -->
  </a-card>
</template>
<style lang="less">
.data-model-collect-node {
  .ant-card-head {
    padding: 0 1px 0 var(--w-padding-2xs);
  }
  .ant-avatar {
    border-radius: 4px;
    height: 18px;
    width: 18px;
    line-height: 14px;

    .ant-avatar-string {
      transform: scale(1) translateX(-50%) !important;
    }
    i {
      font-size: 12px;
    }
  }
  .ant-card-head {
    background-color: var(--w-danger-color-1);
  }
  .ant-card-head-title {
    font-size: var(--w-font-size-base);
    font-weight: bold;
  }
}
</style>
<script type="text/babel">
export default {
  name: 'DataModelCollect',
  inject: ['getNode', 'getGraph'],
  mixins: [],
  data() {
    return {
      id: undefined,
      title: undefined,
      width: 500,
      height: 500,
      iconbgColor: 'var(--w-danger-color)'
    };
  },
  watch: {},
  beforeCreate() {},
  computed: {
    vWidth() {
      return this.width + 'px';
    },
    vHeight() {
      return this.height + 'px';
    }
  },
  created() {},
  methods: {
    onSetting() {
      let node = this.getNode();
      this.getGraph()
        .getDesigner()
        .select({
          id: node.id,
          component: 'DataModelCollectSet',
          data: { ...node.getData() }
        });
      this.getGraph().getDesigner().showDrawer = true;
    },
    onDeleteNode() {
      const node = this.getNode();
      if (node.id == this.getGraph().getDesigner().selectId) {
        this.getGraph().getDesigner().showDrawer = false;
      }

      node.remove();
    },
    updateZindex(node, pZindex) {
      let _this = this;
      node.zIndex = pZindex + 1;
      if (node._children) {
        for (let c of node._children) {
          if (c.shape === 'data-model-collect') {
            _this.updateZindex(c, node.zIndex);
          }
        }
      }
    }
  },
  beforeMount() {
    const node = this.getNode();
    const { title } = node.getData();
    this.title = title;
    this.id = node.id;
    let _this = this;
    let size = node.size();
    this.width = size.width;
    this.height = size.height;
    node.on('change:size', data => {
      _this.width = data.current.width;
      _this.height = data.current.height;
    });
    node.on('change:data', data => {
      _this.title = data.current.title;
    });

    node.on('change:resetColumns', () => {
      let columns = [],
        nodeData = node.getData();
      if (nodeData.unionType == undefined) {
        let beforeUpdatedColumn = {};
        if (nodeData.columns) {
          for (let i = 0, len = nodeData.columns.length; i < len; i++) {
            beforeUpdatedColumn[nodeData.columns[i].alias] = nodeData.columns[i];
          }
        }
        let children = node.getChildren(),
          alias = [];
        if (children) {
          for (let cell of children) {
            if (cell) {
              let cellData = cell.getData(),
                cellColumns = cellData.columns;
              if (cellColumns) {
                for (let c of cellColumns) {
                  if (c.return === true) {
                    let col = JSON.parse(JSON.stringify(c));
                    col.title = cellData.title + '_' + c.title;
                    if (!alias.includes(col.column)) {
                      col.alias = col.column;
                      alias.push(col.alias);
                    } else {
                      col.alias = cellData.alias + '_' + col.column;
                      if (col.alias.length >= 30) {
                        col.alias = col.alias.substring(0, 30);
                      }
                    }
                    col.location = nodeData.alias + '.' + col.alias;
                    col.return = beforeUpdatedColumn[col.alias] ? beforeUpdatedColumn[col.alias].return : true;
                    columns.push(col);
                  }
                }
              }
            }
          }
        }

        // nodeData.columns = columns;
        // nodeData.timestamp = new Date().getTime();
        node.updateData({ columns });
      }
    });

    node.on('change:childColumnsChange', data => {
      let childNode = data.node;
      let childData = childNode.getData();
      let collectNode = _this.getNode(),
        collectNodeData = collectNode.getData();
      if (collectNodeData.unionType != undefined) {
        // 数据合并的列，由数据合并设置指定
        return;
      }
      let existColumns = [],
        alias = [];
      for (let i = 0, len = collectNodeData.columns.length; i < len; i++) {
        let c = collectNodeData.columns[i];
        // 追加新列
        // 已经存在的列
        existColumns.push(c.nid + c.column);
        alias.push(c.alias);
      }
      let columns = collectNodeData.columns;
      if (childData.columns) {
        for (let i = 0, len = childData.columns.length; i < len; i++) {
          let c = childData.columns[i];
          // 追加新列
          if (c.return === true && !existColumns.includes(c.nid + c.column)) {
            let col = JSON.parse(JSON.stringify(c));
            col.title = childData.title + '_' + c.title;
            if (!alias.includes(col.column)) {
              col.alias = col.column;
              alias.push(col.alias);
            } else {
              col.alias = childData.alias + '_' + col.column;
              if (col.alias.length >= 30) {
                col.alias = col.alias.substring(0, 30);
              }
            }
            col.location = collectNodeData.alias + '.' + col.alias;
            col.return = true; // 默认集合的列都返回
            existColumns.push(c.nid + c.column);
            columns.push(col);
          }
        }
      }
      // _this.getGraph().trigger('cell:change:data', {});
      node.updateData({ columns });
    });
    node.on('change:children', data => {
      let { cell, current, previous } = data;
      let nodeData = cell.getData();

      if (nodeData.unionType != undefined) {
        // 数据合并的列，由数据合并设置指定
        return;
      }

      if (current.length == 0) {
        node.updateData({ columns: [] });
        return;
      }
      let columns = [],
        alias = [];
      for (let id of current) {
        let cell = _this.getGraph().getCellById(id);
        if (cell) {
          let cellData = cell.getData();
          if (cellData && cellData.columns) {
            let cellColumns = cellData.columns;
            for (let c of cellColumns) {
              if (c.return === true) {
                let col = JSON.parse(JSON.stringify(c));
                col.title = cellData.title + '_' + c.title;
                if (!alias.includes(col.column)) {
                  col.alias = col.column;
                  alias.push(col.alias);
                } else {
                  col.alias = cellData.alias + '_' + col.column;
                  if (col.alias.length >= 30) {
                    col.alias = col.alias.substring(0, 30);
                  }
                }
                col.location = nodeData.alias + '.' + col.alias;
                col.return = true; // 默认集合的列都返回
                columns.push(col);
              }
            }
          }
        }

        if (cell.shape == 'data-model-collect') {
          _this.updateZindex(cell, node.zIndex);
        }
      }
      console.log(current.title + '的列属性变更: ', columns);
      node.updateData({ columns });
    });
  },
  mounted() {},
  destroyed() {},
  updated() {}
};
</script>
