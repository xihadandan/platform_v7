<template>
  <a-card
    :style="{ width: vWidth, height: vHeight, backgroundColor: vBackgroundColor }"
    size="small"
    :class="['data-model-node-card', isViewNode ? 'view-node' : '']"
    :bodyStyle="{ padding: '8px' }"
  >
    <span slot="title" :title="title + '(' + subTitle + ')'">
      <a-avatar
        :icon="loading ? 'loading' : 'database'"
        shape="square"
        class="ant-avatar"
        :style="{ backgroundColor: iconbgColor }"
      ></a-avatar>
      {{ title }}
      <label class="sub-title">{{ subTitle }}</label>
    </span>
    <template slot="extra">
      <a-button type="icon" size="small" @click="onSetting" title="设置"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
      <a-button type="icon" size="small" @click="onDeleteNode" title="删除">
        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
      </a-button>
    </template>
    <Scroll v-show="!collapsed" style="max-height: 300px">
      <a-row v-for="(item, i) in items" :key="'item-' + i">
        <a-col :title="item.title" :span="12" class="left-text">
          {{ item.title }}
        </a-col>
        <a-col :span="12" :title="item.column" class="right-text">
          {{ item.column }}
        </a-col>
      </a-row>
    </Scroll>
    <!-- <div v-show="!collapsed" style="max-height: 300px; overflow-y: auto; overflow-x: hidden">

    </div> -->
    <div style="position: relative">
      <a-button block type="link" @click="onClickCollapse" v-if="!loading" :title="collapsed ? '展开' : '收起'">
        <a-icon type="double-right" :rotate="collapsed ? 90 : -90"></a-icon>
      </a-button>
      <label v-if="moduleName != undefined || category == 'entity'" class="sub-remark">
        {{ category == 'entity' ? '平台内置对象' : moduleName }}
      </label>
    </div>
  </a-card>
</template>
<style lang="less">
.data-model-node-card {
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
  .ant-card-body {
    background-color: #ffffff;
  }
  .ant-card-head-title {
    font-size: var(--w-font-size-base);
    font-weight: bold;
  }
  .sub-title {
    color: var(--w-text-color-light);
    font-weight: normal;
  }
  .sub-remark {
    position: absolute;
    left: 0px;
    color: rgb(219, 219, 219);
    top: 19px;
    max-width: 250px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 12px;
  }
  .left-text,
  .right-text {
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    font-size: var(--w-font-size-base);
    line-height: 32px;
    color: var(--w-text-color-light);
  }
  .right-text {
    text-align: right;
    color: var(--w-text-color-dark);
    padding-left: 8px;
  }
}
</style>
<script type="text/babel">
export default {
  name: 'DataModelNode',
  inject: ['getNode', 'getGraph'],
  mixins: [],
  data() {
    return {
      title: undefined,
      subTitle: undefined,
      category: undefined,
      items: [],
      collapsed: true,
      width: 240,
      height: 90,
      isViewNode: false,
      moduleName: undefined,
      loading: true,
      iconbgColor: ''
    };
  },
  watch: {},
  beforeCreate() {},
  computed: {
    vBackgroundColor() {
      if (this.category == 'entity' || this.moduleName != undefined) {
        this.iconbgColor = 'var(--w-warning-color)';
        return 'var(--w-warning-color-1)';
      }
      if (this.isViewNode) {
        this.iconbgColor = 'var(--w-success-color)';
        return 'var(--w-success-color-1)';
      }
      this.iconbgColor = 'var(--w-primary-color)';
      return 'var(--w-primary-color-1)';
    },
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
      this.getGraph().getDesigner().showDrawer = true;
      this.getGraph().getDesigner().select({
        id: node.id,
        component: 'DataModelNodeSet',
        data: node.getData()
      });
    },
    onDeleteNode() {
      const node = this.getNode();
      if (node.id == this.getGraph().getDesigner().selectId) {
        this.getGraph().getDesigner().showDrawer = false;
      }
      node.remove();
    },
    fetchDataModelDetails(id) {
      let _this = this,
        node = this.getNode();
      if (this.category === 'entity') {
        $axios.get(`/proxy/api/dm/getExposedColumns`, { params: { table: id } }).then(({ data }) => {
          this.loading = false;
          if (data.data) {
            let columns = [];
            let nodeData = node.getData(),
              alias = nodeData.alias,
              columnMap = {};
            for (let i = 0, len = data.data.length; i < len; i++) {
              let col = data.data[i],
                title = col.comment || col.code;
              columnMap[col.code] = col;
              columns.push({
                title,
                dataType: _this.convertDataType(col.dataType),
                column: col.code,
                nid: node.id,
                alias: col.code,
                location: alias + '.' + col.code,
                fullTitle: nodeData.title + '.' + title,
                return: true
              });
            }
            let columnCodes = Object.keys(columnMap);
            if (nodeData.columns != undefined && nodeData.columns.length > 0) {
              for (let i = 0; i < nodeData.columns.length; i++) {
                if (columnMap[nodeData.columns[i].alias] == undefined) {
                  nodeData.columns.splice(i--, 1);
                  continue;
                }
                nodeData.columns[i].dataType = columnMap[nodeData.columns[i].alias].dataType;
                columnCodes.splice(columnCodes.indexOf(nodeData.columns[i].alias), 1);
              }
              if (columnCodes.length > 0) {
                // 添加新的字段
                for (let i = 0, len = columnCodes.length; i < len; i++) {
                  nodeData.columns.push(columnMap[columnCodes[i]]);
                }
              }
            }
            _this.items = JSON.parse(JSON.stringify(columns));
            node.updateData({ columns });
          }
        });
      } else {
        $axios.get(`/proxy/api/dm/getDetails`, { params: { id } }).then(({ data }) => {
          this.loading = false;
          if (data.data) {
            _this.items = JSON.parse(data.data.columnJson);
            // TODO: 存在增删改字段变化，进行提示

            let columns = JSON.parse(data.data.columnJson);
            let nodeData = node.getData(),
              alias = nodeData.alias,
              isView = data.data.type == 'VIEW';
            if (nodeData.columns.length == 0) {
              // 设置列别命
              for (let c of columns) {
                if (!isView) {
                  // 非视图情况下，别名=列名
                  c.alias = c.column;
                  c.return = true; // 默认返回字段
                }
                c.nid = node.id;
                c.location = alias + '.' + c.alias; // 列定位：表别名.列别名
                c.fullTitle = nodeData.title + '.' + c.title;
              }
              if (isView) {
                nodeData.sql = data.data.sql;
                nodeData.sqlObjJson = data.data.sqlObjJson;
                nodeData.sqlParameter = data.data.sqlParameter ? JSON.parse(data.data.sqlParameter) : {};
              }
              node.updateData({ columns });
              let _parent = node.getParent();
              if (_parent) {
                _parent.trigger('change:childColumnsChange', {
                  node
                });
              }
            }
          }
        });
      }
    },
    convertDataType(dataType) {
      let type = dataType.toLowerCase();
      if (type.indexOf('timestamp') != -1) {
        return 'timestamp';
      } else if (type.indexOf('varchar') != -1) {
        return 'varchar';
      } else if (type.indexOf('number') != -1) {
        return 'number';
      } else if (type == 'clob') {
        return 'clob';
      }
      return 'varchar';
    },
    onClickCollapse() {
      this.collapsed = !this.collapsed;
      const node = this.getNode();
      if (!this.collapsed) {
        this.$nextTick(() => {
          this.height = this.$el.scrollHeight + 2;
          node.setSize({ width: this.width, height: this.height });
        });
      } else {
        this.height = 90;
        node.setSize({ width: this.width, height: this.height });
      }
    }
  },
  beforeMount() {
    const node = this.getNode();
    const { title, id, type, category, moduleName } = node.getData();
    this.title = title;
    if (category != 'entity') {
      this.subTitle = `( ${id} )`;
    }
    this.id = id;
    this.isViewNode = type === 'VIEW';
    this.category = category;
    this.moduleName = moduleName;
    node.setSize({ width: this.width, height: this.height });
  },
  mounted() {
    this.fetchDataModelDetails(this.id);
  },
  destroyed() {}
};
</script>
