<template>
  <div style="" class="org-version-constuct">
    <a-row type="flex" class="org-version-constuct-setting">
      <a-col flex="auto">
        <a-form-model layout="inline">
          <a-form-model-item label="显示组织层级">
            <a-slider
              class="pt-slider"
              :style="{ width: '200px', '--w-pt-slider-rail-height': '6px', '--w-pt-slider-handle-size': '12px' }"
              :min="1"
              :default-value="orgMaxLevel"
              @afterChange="onChangeShowOrgLevel"
              :max="orgMaxLevel"
            />
          </a-form-model-item>
        </a-form-model>
      </a-col>
      <a-col flex="100px" style="text-align: right">
        <a-button type="link" icon="download" @click.stop="downloadAsImage" :loading="downloading">下载架构图</a-button>
      </a-col>
    </a-row>
    <div :id="id" :style="{ width: '100%' }"></div>
    <!-- 用于多节点的树导出 -->
    <div :id="id + '_SimpleTree'" :style="{ width: '100%', display: 'none' }"></div>
  </div>
</template>

<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
export default {
  name: 'OrgVersionConstuct',
  inject: ['pageContext', 'currentOrgVersion', 'getOrgElementTreeData', 'getOrgElementTreeNodeMap', 'orgSetting'],
  props: {},
  data() {
    return {
      id: generateId(),
      treeData: [],
      orgMaxLevel: 0,
      treeFetched: false,
      form: {},
      downloading: false
    };
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    downloadAsImage() {
      let _this = this;
      this.downloading = true;
      setTimeout(() => {
        try {
          if (this.totalCount <= 100) {
            _this.graph.zoomTo(1);
            _this.graph.fitCenter();
            _this.graph.downloadFullImage('组织架构', 'image/png', {
              backgroundColor: 'white',
              padding: [30, 15, 15, 15]
            });
          } else {
            this.$message.info('由于组织机构的节点数量过多, 优化下载中, 将采用纵向紧凑树样式下载');
            // 采用简单树导出大图
            _this.simpleTreeGraph.downloadFullImage('组织架构', 'image/png', {
              backgroundColor: 'white',
              padding: [30, 15, 15, 15]
            });
          }
        } catch (error) {}

        _this.downloading = false;
      }, 600);
    },
    onChangeShowOrgLevel(v) {
      this.graph.changeData(this.rootNodeWrapper(this.limitTreeData(v)));
      this.graph.fitCenter();
      this.graph.translate(0, -100);
    },
    rootNodeWrapper(treeData) {
      if (treeData.length > 1) {
        return {
          label: this.currentOrgVersion.organization.name,
          id: '-1',
          children: treeData
        };
      }
      return treeData[0];
    },
    limitTreeData(limitLevel) {
      let convert = (nodes, level) => {
        if (nodes != undefined) {
          let removeChildren = limitLevel != undefined && limitLevel === level;
          for (let i = 0, len = nodes.length; i < len; i++) {
            if (removeChildren) {
              delete nodes[i].children;
              continue;
            }
            convert(nodes[i].children, level + 1);
          }
        }
      };
      let treeData = JSON.parse(JSON.stringify(this.treeData));
      convert(treeData, 1);
      return treeData;
    },
    createSimpleTree() {
      let simple = {
        id: this.treeData[0].label,
        children: []
      };
      let count = 0;
      let cascadeNode = (children, parent) => {
        if (children) {
          for (let i = 0, len = children.length; i < len; i++) {
            let _p = { id: children[i].label, children: [] };
            parent.children.push(_p);
            count++;
            cascadeNode(children[i].children, _p);
          }
        }
      };

      cascadeNode(this.treeData[0].children, simple);
      this.totalCount = count;
      if (count > 100) {
        const container = document.getElementById(this.id + '_SimpleTree');
        const width = container.scrollWidth;
        const height = container.scrollHeight || 500;
        import('@antv/g6').then(G6 => {
          const graph = new G6.TreeGraph({
            container: this.id + '_SimpleTree',
            width,
            height,
            modes: {
              default: ['drag-canvas', 'zoom-canvas']
            },
            defaultNode: {
              size: 26,
              anchorPoints: [
                [0, 0.5],
                [1, 0.5]
              ]
            },
            defaultEdge: {
              type: 'cubic-horizontal'
            },
            layout: {
              type: 'dendrogram',
              direction: 'LR',
              nodeSep: 30,
              rankSep: 100
            }
          });
          this.simpleTreeGraph = graph;
          graph.node(function (node) {
            return {
              label: node.id,
              labelCfg: {
                offset: 10,
                position: node.children && node.children.length > 0 ? 'left' : 'right'
              }
            };
          });

          graph.data(simple);
          graph.render();
        });
      }
    }
  },

  beforeMount() {
    this.treeData = JSON.parse(JSON.stringify(this.getOrgElementTreeData()));
    if (this.orgSetting.ORG_MANAGE_JOB_VISIBLE == undefined || !this.orgSetting.ORG_MANAGE_JOB_VISIBLE.enable) {
      let max = 1;
      let cascadeRemoveJob = (list, parent) => {
        if (list) {
          for (let i = 0; i < list.length; i++) {
            let l = list[i];
            l.level = parent == undefined ? 1 : parent.level + 1;
            if (l.data.type == 'job') {
              let job = list.splice(i--, 1)[0];
              // 把职位的子节点上移
              if (job.children && job.children.length) {
                for (let j = 0, jlen = job.children.length; j < jlen; j++) {
                  let c = job.children[i];
                  c.parentUuid = l.parentUuid;
                  c.level = l.level;
                  list.push(c);
                }
              }
            } else {
              cascadeRemoveJob.call(this, l.children, l);
              if (l.level > max) {
                max = l.level;
              }
            }
          }
        }
      };
      cascadeRemoveJob.call(this, this.treeData);
      this.orgMaxLevel = max;
    } else if (this.treeData.length) {
      let treeKeyNodeMap = this.getOrgElementTreeNodeMap();
      let max = 1;
      for (let k in treeKeyNodeMap) {
        if (treeKeyNodeMap[k].level > max) {
          max = treeKeyNodeMap[k].level;
        }
      }
      this.orgMaxLevel = max;
      // let cascadeSetJobStyle = list => {
      //   if (list) {
      //     for (let i = 0; i < list.length; i++) {
      //       let l = list[i];
      //       if (l.data.type == 'job') {
      //         l.style = { fill: '#50a14f', stroke: '#50a14f' };
      //         l.labelCfg = {
      //           style: {
      //             fill: '#fff'
      //           }
      //         };
      //       }
      //       if (l.children && l.children.length) {
      //         cascadeSetJobStyle(l.children);
      //       }
      //     }
      //   }
      // };
      // cascadeSetJobStyle(this.treeData);
    }
    // console.log('组织架构图节点: ', this.treeData, this.orgMaxLevel);
  },
  mounted() {
    let _this = this;
    import('@framework/vue/lib/g6/draw-tree.js').then(({ draw }) => {
      if (this.treeData.length) {
        this.graph = draw(this.id, this.rootNodeWrapper(this.treeData), true);
        this.graph.translate(0, -100);

        // console.log(simple);
        this.createSimpleTree();
      }
    });
  }
};
</script>
<style lang="less">
.org-version-constuct {
  .org-version-constuct-setting {
    position: sticky;
    top: 0;
    background-color: var(--w-gray-color-3);
    align-items: center;
    border-radius: 4px;
    padding: 12px 16px;
    height: 70px;
    margin-bottom: 16px;
  }
}
</style>
