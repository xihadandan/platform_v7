<template>
  <div>
    <div class="spin-loading-center" v-if="loading">
      <a-spin />
    </div>
    <a-collapse
      v-else-if="modules.length > 0"
      :accordion="true"
      :bordered="false"
      expandIconPosition="right"
      :defaultActiveKey="defaultActiveKey"
      class="design-template"
    >
      <template v-for="(mod, i) in modules">
        <a-collapse-panel :key="mod.id">
          <template slot="header">
            <!-- <a-button icon="location" size="small" type="link" @click.stop="() => redirectModuleAssemble(mod.uuid)">
              {{ mod.name }}
            </a-button> -->
            <label class="">{{ mod.name }}</label>
          </template>

          <Scroll :style="{ textAlign: 'center', maxHeight: scrollHeight, marginRight: '-20px', paddingRight: '20px' }">
            <draggable
              v-if="moduleData[mod.id].widgets.length > 0"
              tag="ul"
              :list="moduleData[mod.id].widgets"
              :group="{ name: currentDragGroup, pull: 'clone', put: false }"
              :clone="handleRefFunctionWidgetClone"
              :sort="false"
              class="template-ul"
              style="width: 100%; display: inline-block"
            >
              <li
                v-for="(select, index) in moduleData[mod.id].widgets"
                :key="index"
                :class="['widget-select-item']"
                :title="select.title"
                :style="{
                  textAlign: 'center',
                  width: '100%',
                  margin: '5px 0px',
                  padding: '0px'
                }"
              >
                <div class="item-img">
                  <img :src="select.definitionJson.thumbnail" />
                </div>
                <!-- <a-popover
              placement="right"
              :arrowPointAtCenter="true"
              :zIndex="1000"
              :mouseEnterDelay="0.5"
              overlayClassName="no-arrow"
              :ref="select.uuid + '_popover'"
            >
              <template slot="content">
                <img :src="select.thumbnail" style="height: 600px" />
              </template>
              <img :src="select.thumbnail" style="width: 100px; height: 100px" />
            </a-popover> -->
                <a-row type="flex" class="item-detail">
                  <a-col flex="1px" style="padding: 12px 0px 12px 20px"></a-col>
                  <a-col flex="auto">
                    <div class="flex">
                      <div :title="select.title" class="template-label-title" style="max-width: 100px">
                        {{ select.title }}
                      </div>
                      <div :title="select.remark" class="template-label-remark" style="max-width: 100px">
                        {{ select.remark }}
                      </div>
                    </div>
                  </a-col>
                  <a-col flex="32px">
                    <a-button title="前往设计" type="link" size="small" @click="redirectToWidgetDesign(select)">
                      <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
                    </a-button>
                  </a-col>
                </a-row>
              </li>
            </draggable>
            <a-empty v-else></a-empty>
          </Scroll>
        </a-collapse-panel>
      </template>
    </a-collapse>
    <a-empty v-else style="margin-top: 200px" />
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import draggable from '@framework/vue/designer/draggable';
import { Empty } from 'ant-design-vue';
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'ProdModuleFuncWidgetSelect',
  props: {
    designer: Object,
    prodVersionId: String
  },
  mixins: [draggable],
  components: {},
  computed: {
    currentDragGroup() {
      return this.designer.currentCanDragGroup;
    },
    scrollHeight() {
      let rect = this.$el.getBoundingClientRect();
      let top = rect.top;
      if (this.modules.length) {
        return `calc(100vh - ${top}px - ${40 * this.modules.length}px)`;
      }
      return `calc(100vh - ${top}px)`;
    }
  },
  data() {
    return { modules: [], moduleData: {}, defaultActiveKey: undefined, loading: true, simpleImage: Empty.PRESENTED_IMAGE_SIMPLE };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.fetchProdVersionModules().then(modules => {
      if (modules.length > 0) {
        this.fetchModuleFuncWidgets(modules);
      } else {
        this.loading = false;
      }
    });
  },
  methods: {
    redirectToWidgetDesign(item) {
      window.open(`/module-widget-design/${item.uuid}`, '_blank');
    },
    redirectModuleAssemble(id, target = '_blank') {
      if (id) window.open(`/module/assemble/${id}`, target);
    },
    handleRefFunctionWidgetClone(origin) {
      return {
        id: generateId(),
        title: origin.title,
        wtype: 'WidgetRefrence',
        configuration: {
          refWidgetUuid: origin.uuid
        }
      };
    },

    fetchModuleFuncWidgets(modules) {
      let appId = [];
      for (let m of modules) {
        appId.push(m.id);
      }
      $axios
        .post(`/proxy/api/user/widgetDef/query`, { appId, type: 'FUNCTION_WIDGET' })
        .then(({ data }) => {
          for (let d of data.data.data) {
            if (d.enabled) {
              if (this.moduleData[d.appId] == undefined) {
                this.$set(this.moduleData, d.appId, {
                  widgets: []
                });
              }
              d.definitionJson = JSON.parse(d.definitionJson);
              if (d.definitionJson.items.length != 0) {
                // 设计内容不为空
                this.moduleData[d.appId].widgets.push(d);
              }
            }
          }
          let keys = Object.keys(this.moduleData);
          for (let i = 0; i < modules.length; i++) {
            if (!keys.includes(modules[i].id)) {
              modules.splice(i--, 1);
            }
          }
          this.modules = modules;
          if (this.modules.length > 0) {
            this.defaultActiveKey = this.modules[0].id;
          }
          this.loading = false;
        })
        .catch(error => {});
    },
    fetchProdVersionModules() {
      // 查询页面关联版本的所有模块
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/modules`, { params: { prodVersionId: this.prodVersionId } })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            } else {
              reject();
            }
          })
          .catch(error => {
            reject();
          });
      });
    }
  }
};
</script>
