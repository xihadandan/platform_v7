<template>
  <div class="search-scope">
    <a-button type="primary" @click="openDrawer">
      <a-icon type="setting" />
      设置
    </a-button>
    <drawer v-model="visible" title="搜索范围设置" :width="600" :container="getContainer" :mask="true" wrapClassName="search-scope-drawer">
      <template slot="content">
        <a-table rowKey="uuid" :pagination="false" :columns="columns" :dataSource="dataSource" ref="roleTable">
          <span class="pt-title-vertical-line" slot="title">选择需全文检索的数据分类</span>
          <template slot="nameSlot" slot-scope="text, record">
            <div style="display: inline-block">
              <Icon type="pticon iconfont icon-ptkj-tuodong" class="drag-handler" :style="{ cursor: 'move' }" />
              <span>{{ text }}</span>
            </div>
          </template>
          <template slot="titleSlot" slot-scope="text, record, index">
            <div style="display: flex; align-items: center">
              <template v-if="record.uuid === currentEditKey">
                <a-input v-model="record.title" @blur="blurTitle(index)" :autoFocus="true" />
              </template>
              <template v-else>
                <div class="action-title">{{ text }}</div>
              </template>
              <a-button type="link" size="small" @click="handleEditTitle(index, record, $event)">
                <Icon type="pticon iconfont icon-ptkj-bianji" />
              </a-button>
              <!-- <w-i18n-input :target="record" :code="record.uuid" v-model="record.title" v-show="record.name != record.title" /> -->
            </div>
          </template>
          <template slot="visibleSlot" slot-scope="text, record">
            <a-switch v-model="record.visible" />
          </template>
        </a-table>
      </template>
      <template slot="footer">
        <a-button type="primary" @click="handleConfirm">确定</a-button>
        <a-button @click="closeDrawer">取消</a-button>
      </template>
    </drawer>
  </div>
</template>

<script>
import draggable from '@framework/vue/designer/draggable';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';
import { generateId } from '@framework/vue/utils/util';
import { fetchListCategoryBySystem } from '../api';

export default {
  name: 'SearchScope',
  mixins: [draggable],
  props: {
    value: {
      type: Array,
      default: () => []
    }
  },
  components: {
    Drawer,
    WI18nInput
  },
  data() {
    return {
      visible: false,
      currentEditKey: '',
      readyDraggable: false,
      dataSource: [],
      columns: [
        { title: '数据分类', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '显示名称', dataIndex: 'title', width: 220, scopedSlots: { customRender: 'titleSlot' } }, // 能修改的名称
        {
          title: '开启',
          dataIndex: 'visible',
          width: 80,
          scopedSlots: { customRender: 'visibleSlot' }
          // align: 'left'
        }
      ]
    };
  },
  watch: {
    value: {
      immediate: true,
      handler(value) {
        // this.dataSource = value.map(item => {
        //   if (!item.uuid) {
        //     item.uuid = generateId();
        //   }
        //   return item;
        // });
      }
    }
  },
  created() {
    this.getListBySystem();
  },
  methods: {
    /* 
    根据系统ID获取全文检索分类
    排序：存在的优先
    */
    getListBySystem() {
      fetchListCategoryBySystem().then(res => {
        const original = JSON.parse(JSON.stringify(this.value));
        let scopeList = [];
        this.value.map(data => {
          if (!data) {
            return;
          }
          if (!data.uuid) {
            data.uuid = generateId();
          }
          if (data.value === 'workflow' || data.value === 'dms_file') {
            scopeList.push(data);
          }
          // 分类还存在
          const hasIndex = res.findIndex(f => f.uuid === data.uuid);
          if (hasIndex > -1) {
            data.name = res[hasIndex].name;

            if (data.children) {
              let childrens = [];
              data.children.map(children => {
                const _index = res.findIndex(f => f.uuid === children.uuid);
                if (_index > -1) {
                  // 更新子类名称
                  children.name = res[_index].name;
                  childrens.push(children);
                }
              });
              if (childrens.length) {
                data.children = childrens;
              } else {
                delete data.children;
              }
            }
            scopeList.push(data);
          }
        });

        res.map(data => {
          const hasIndex = original.findIndex(f => {
            if (f && f.uuid === data.uuid) {
              return true;
            }
            return false;
          });
          if (hasIndex === -1) {
            data.visible = false;
            data.value = data.uuid;
            data.title = data.name;
            scopeList.push(data);
          }
        });
        let dataSource = [];
        // 设置父级
        scopeList.map(item => {
          if (!item.parentUuid || item.parentUuid === '0') {
            dataSource.push(item);
          }
        });
        // 设置子级
        scopeList.map(item => {
          if (item.parentUuid && item.parentUuid !== '0') {
            const findItem = dataSource.find(f => f.uuid === item.parentUuid);
            if (findItem) {
              if (!findItem.children) {
                findItem.children = [];
              }
              const hasIndex = findItem.children.findIndex(f => f.uuid === item.uuid);
              if (hasIndex === -1) {
                findItem.children.push(item);
              }
            }
          }
        });

        this.dataSource = dataSource;
      });
    },
    getContainer() {
      return document.body;
    },
    blurTitle(index) {
      this.currentEditKey = '';
    },
    handleEditTitle(index, item, event) {
      this.currentEditKey = item.uuid;
    },
    handleConfirm(callBack) {
      this.$emit('input', this.dataSource);
      if (typeof callBack === 'function') {
        callBack(true);
      }
      this.closeDrawer();
    },
    openDrawer() {
      this.visible = true;
      if (!this.readyDraggable) {
        this.readyDraggable = true;
        this.$nextTick(() => {
          this.tableDraggable(this.dataSource, this.$refs.roleTable.$el.querySelector('tbody'), '.drag-handler');
        });
      }
    },
    closeDrawer() {
      this.visible = false;
    }
  }
};
</script>
