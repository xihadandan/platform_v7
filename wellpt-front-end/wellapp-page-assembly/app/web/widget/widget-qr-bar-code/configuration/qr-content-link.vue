<template>
  <div>
    <!-- <a-input :readOnly="true" @click="openModal" /> -->
    <a-button type="link" size="small" @click="openModal">
      设置
      <Icon type="pticon iconfont icon-ptkj-shezhi" />
    </a-button>
    <modal title="链接地址" v-model="visible" :ok="handleSave" okText="保存" :width="600">
      <template slot="content">
        <a-form-model v-if="visible" :colon="false" :labelCol="{ flex: '80px', style: { textAlign: 'left' } }" class="pt-form pt-form-flex">
          <a-form-model-item label="类型" :wrapperCol="{ flex: 'auto' }">
            <a-select :options="typeOptions" v-model="formData.type" @change="changeType" />
          </a-form-model-item>
          <template v-if="formData.type === typeOptions[0]['value']">
            <a-form-model-item :wrapperCol="{ flex: 'auto' }">
              <template slot="label">
                <label>链接地址</label>
                <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                  <div slot="title">如果是外部地址，请填写完整的url(包含http://或https://的前缀) 比如 https://www.baidu.com;</div>
                  <span><a-icon type="exclamation-circle" /></span>
                </a-tooltip>
              </template>
              <a-input v-model="formData.value" />
            </a-form-model-item>
          </template>
          <template v-if="formData.type === typeOptions[1]['value']">
            <a-form-model-item label="链接地址" :wrapperCol="{ flex: 'auto' }">
              <w-tree-select
                v-model="formData.value"
                :treeData="pageTreeData"
                :getPopupContainer="getPopupContainer"
                :replaceFields="{
                  title: 'name',
                  key: 'id',
                  value: 'id'
                }"
              />
            </a-form-model-item>
          </template>
          <template v-if="formData.type === typeOptions[2]['value']">
            <a-form-model-item label="链接地址" :wrapperCol="{ flex: 'auto' }">
              <a-radio-group v-model="formData.workflowType" buttonStyle="solid">
                <a-radio-button v-for="item in workflowTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
              </a-radio-group>
              <template v-if="formData.workflowType === workflowTypeOptions[1]['value']">
                <w-tree-select
                  v-model="formData.value"
                  :treeData="workflowTreeData"
                  :getPopupContainer="getPopupContainer"
                  @change="changeFlowId"
                />
              </template>
            </a-form-model-item>
          </template>
          <template v-if="formData.type === typeOptions[3]['value']">
            <a-form-model-item label="链接地址" :wrapperCol="{ flex: 'auto' }">
              <a-radio-group v-model="formData.formMode" buttonStyle="solid">
                <a-radio-button v-for="item in formModeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
              </a-radio-group>
              <template v-if="formData.formMode === formModeOptions[0]['value']">
                <w-tree-select
                  v-model="formData.value"
                  :treeData="formTreeData"
                  :getPopupContainer="getPopupContainer"
                  :replaceFields="{
                    title: 'name',
                    key: 'uuid',
                    value: 'uuid'
                  }"
                />
              </template>
            </a-form-model-item>

            <template v-if="formData.formMode === formModeOptions[1]['value']">
              <a-form-model-item label="打开类型" :wrapperCol="{ flex: 'auto' }">
                <a-radio-group v-model="formData.formOpenType" buttonStyle="solid">
                  <a-radio-button v-for="item in formOpenTypeOptions" :key="item.value" :value="item.value">
                    {{ item.label }}
                  </a-radio-button>
                </a-radio-group>
                <template v-if="formOpenTypeOptions[1] && formData.formOpenType === formOpenTypeOptions[1]['value']">
                  <w-tree-select
                    v-model="formData.formSpecify"
                    :treeData="formTreeData"
                    :getPopupContainer="getPopupContainer"
                    :replaceFields="{
                      title: 'name',
                      key: 'uuid',
                      value: 'uuid'
                    }"
                  />
                </template>
              </a-form-model-item>
            </template>
          </template>
        </a-form-model>
      </template>
    </modal>
  </div>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { cloneDeep } from 'lodash';
import WTreeSelect from '@workflow/app/web/page/workflow-designer/component/components/w-tree-select';

export default {
  name: 'QrContentLink',
  inject: ['appId'],
  props: {
    value: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Modal,
    WTreeSelect
  },
  data() {
    const formData = cloneDeep(this.value);
    return {
      typeOptions: [
        { label: 'URL', value: 'custom' },
        { label: '页面', value: 'page' },
        { label: '流程', value: 'workflow' },
        { label: '表单', value: 'form' }
      ],
      workflowTypeOptions: [
        { label: '选择流程页面', value: 'start_new_work' },
        { label: '发起流程', value: 'work_new' }
      ],
      formModeOptions: [
        { label: '新建', value: 'new_form' },
        { label: '打开单据', value: 'open_form' }
      ],
      formOpenTypeOptions: [
        { label: '当前数据', value: 'current' }
        // { label: '指定表单', value: 'specify' }
      ],
      formData,
      visible: false,
      pageTreeData: [],
      workflowTreeData: [],
      formTreeData: [],
      treeData: []
    };
  },
  created() {
    if (this._$SYSTEM_ID) {
      // 系统下
    } else {
      // 装配中心下
    }
    this.getFlowTreeDataSync();
    this.getPageTreeData();
    this.getDyformTreeData();
  },
  methods: {
    // 更改类型
    changeType(type, option) {
      this.formData.value = undefined;
    },
    // 同步方式获取流程树
    getFlowTreeDataSync() {
      this.fetchAllFlowAsCategoryTree().then(res => {
        const formatTreeData = arr => {
          if (!arr) {
            return [];
          }
          return arr.map(item => {
            const children = item['children'];

            let node = {
              title: item.name,
              key: item.id,
              label: item.name,
              value: item.id,
              isLeaf: children && children.length ? false : true,
              selectable: children && children.length ? false : true,
              sourceData: item
            };

            if (children) {
              node['children'] = formatTreeData(children);
              return node;
            }
            return node;
          });
        };

        this.workflowTreeData = formatTreeData(res);
      });
    },
    // 更改流程
    changeFlowId(flowDefId, label, extra) {
      if (extra && extra.triggerNode) {
      }
    },
    // 同步方式获取流程树
    fetchAllFlowAsCategoryTree() {
      return new Promise((resolve, reject) => {
        const params = {
          methodName: 'getAllFlowAsCategoryTree',
          serviceName: 'flowSchemeService'
        };
        window
          .$axios({
            method: 'post',
            url: '/json/data/services',
            data: params
            // headers:
          })
          .then(res => {
            if (res.status === 200) {
              if (res.data && res.data.code === 0) {
                const data = res.data.data;
                resolve(data);
              } else {
                reject(res.data);
              }
            } else {
              reject(res);
            }
          });
      });
    },
    // 获取页面树
    getPageTreeData() {
      this.fetchPageDefinition().then(res => {
        this.pageTreeData = res;
      });
    },
    // 请求模块下的页面
    fetchPageDefinition(appIds = [this.appId]) {
      return new Promise((resolve, reject) => {
        $axios({
          method: 'post',
          url: '/proxy/api/webapp/page/definition/queryLatestPageDefinitionByAppIds',
          data: appIds
        }).then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              resolve(data);
            } else {
              reject(res.data);
            }
          } else {
            reject(res);
          }
        });
      });
    },
    // 获取表单树
    getDyformTreeData() {
      this.fetchDyformTreeData().then(res => {
        this.formTreeData = res;
      });
    },
    // 请求模块下的表单
    fetchDyformTreeData() {
      return new Promise((resolve, reject) => {
        $axios.get(`/proxy/api/dyform/definition/queryModuleFormDefinition`, { params: { moduleId: this.appId, vJson: true } }).then(
          res => {
            if (res.status === 200) {
              if (res.data && res.data.code === 0) {
                const data = res.data.data;
                resolve(data);
              } else {
                reject(res.data);
              }
            } else {
              reject(res);
            }
          },
          error => {
            reject(error);
          }
        );
      });
    },

    // 异步加载树
    onLoadTreeData(treeNode) {
      const { key } = treeNode.dataRef;
      return new Promise((resolve, reject) => {
        if (key === 'form') {
          this.getDyformTreeData().then(res => {
            treeNode.dataRef.children = res;
            resolve();
          });
        }
      });
    },
    // 打开弹窗
    openModal() {
      this.visible = true;
    },
    // 保存设置
    handleSave(callback) {
      this.$emit('input', cloneDeep(this.formData));
      callback(true);
    },
    getPopupContainer(triggerNode) {
      return triggerNode.closest('.ant-modal-body');
    },
    // 初始化树
    initTreeData() {
      const treeData = [
        {
          selectable: false,
          key: 'page',
          title: '页面',
          value: 'page',
          children: []
        },
        {
          selectable: false,
          key: 'workflow',
          title: '流程',
          value: 'workflow',
          children: []
        },
        {
          selectable: false,
          key: 'form',
          title: '表单',
          value: 'form',
          children: []
        }
      ];
      this.treeData = treeData;
    }
  }
};
</script>
