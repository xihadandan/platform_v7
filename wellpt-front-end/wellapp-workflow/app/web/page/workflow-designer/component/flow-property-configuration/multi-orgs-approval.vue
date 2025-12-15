<template>
  <!-- 流程属性-高级设置-多组织审批 -->
  <div>
    <a-form-model-item class="form-item-vertical" label="添加多组织">
      <div class="message-templates-container">
        <div class="message-template-item" v-for="(record, index) in dataList" :key="index">
          <div class="message-template-name">{{ record.name ? record.name : '&nbsp;' }}</div>

          <!-- <a-tag v-if="record.expired && !record.enable" color="red">已停用/已失效</a-tag> -->
          <a-tag v-if="record.status === 'del'" color="var(--w-gray-color-7)">组织不存在</a-tag>
          <a-tag v-else-if="record.expired" color="red">已失效</a-tag>
          <a-tag v-else-if="record.enable === false" color="red">已停用</a-tag>

          <div class="message-template-btn-group record-btn-group">
            <a-button type="link" size="small" @click="setItem(record, index)" v-if="record.status !== 'del'">
              <Icon type="pticon iconfont icon-ptkj-shezhi" />
            </a-button>
            <a-button type="link" size="small" @click="delItem(index, record)">
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>
          </div>
        </div>
      </div>
      <a-button type="link" size="small" @click="addItem" icon="plus">添加</a-button>
    </a-form-model-item>
    <a-modal
      :title="currentIndex === -1 ? '添加多组织' : '编辑多组织'"
      :visible="visible"
      @ok="saveItem"
      @cancel="visible = false"
      :width="600"
      wrapClassName="flow-timer-modal-wrap"
      :getContainer="getContainer"
      :destroyOnClose="true"
    >
      <a-form-model
        v-if="currentItem"
        ref="form"
        :model="currentItem"
        :rules="rules"
        :colon="false"
        labelAlign="left"
        :label-col="{ span: 5 }"
        :wrapper-col="{ span: 19, style: { textAlign: 'right' } }"
      >
        <a-form-model-item prop="orgId" label="选择组织">
          <w-select
            v-model="currentItem.orgId"
            @change="changeOrgId"
            :options="organizationList"
            :replaceFields="{
              title: 'text',
              key: 'id',
              value: 'id'
            }"
          >
            <template slot="placeholder">请选择组织</template>
          </w-select>
        </a-form-model-item>
        <a-form-model-item :label-col="{ span: 7 }" :wrapper-col="{ span: 16, style: { textAlign: 'right' } }">
          <template slot="label">
            <a-tooltip placement="topRight" :arrowPointAtCenter="true">
              <div slot="title">如果本流程需要同时使用多个组织架构，或跨组织架构审批，可开启多组织审批。</div>
              <label>
                可用业务组织
                <a-icon type="exclamation-circle" />
              </label>
            </a-tooltip>
          </template>
          <a-radio-group v-model="currentItem.availableBizOrg" size="small" button-style="solid" @change="changeBizOrgType">
            <a-radio-button v-for="item in availableBizOrgOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="currentItem.availableBizOrg === availableBizOrgOptions[2]['value']">
          <div class="ant-form-item">
            <w-select
              v-model="currentItem.bizOrgId"
              mode="multiple"
              :options="bizOrgsList"
              :replaceFields="{
                title: 'name',
                key: 'id',
                value: 'id'
              }"
            />
          </div>
        </template>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import OrganizationSelect from '../commons/organization-select';
import WSelect from '../components/w-select';
import mixins from '../mixins';
import { availableBizOrgOptions } from '../designer/constant';

export default {
  name: 'MultiOrgsApproval',
  inject: ['designer', 'pageContext', 'graph'],
  mixins: [mixins],
  props: {
    value: {
      type: Array,
      default: () => []
    }
  },
  components: {
    WSelect,
    OrganizationSelect
  },
  data() {
    return {
      availableBizOrgOptions,
      dataSource: this.value,
      currentIndex: -1,
      currentItem: undefined,
      currentOrgId: '',
      visible: false,
      createItem: () => {
        return {
          orgId: '',
          availableBizOrg: 'all',
          bizOrgId: ''
        };
      },
      rules: {
        orgId: { required: true, message: '请选择组织' }
      },
      bizOrgsList: []
    };
  },
  computed: {
    organizationList() {
      let list = [];
      if (this.designer.organizationList) {
        this.designer.organizationList.forEach(item => {
          const hasIndex = this.value.findIndex(v => {
            if (this.currentOrgId === item.id) {
              return false;
            }
            return v.orgId === item.id;
          });
          if (hasIndex === -1) {
            list.push(item);
          }
        });
      }

      return list;
    },
    dataList() {
      if (this.value) {
        return this.value.map(item => {
          let info = this.designer.getOrgInfoFromAllOrgById(item.orgId);
          if (!info) {
            info = {
              orgId: item.orgId,
              status: 'del'
            };
          }
          return info;
        });
      } else {
        return [];
      }
    }
  },
  methods: {
    setItem(record, index) {
      this.currentIndex = index;
      this.currentItem = JSON.parse(JSON.stringify(this.dataSource[index]));
      this.currentOrgId = this.currentItem.orgId;
      this.bizOrgsList = this.designer.getBizOrgsListByOrgId(this.currentItem.orgId);
      this.visible = true;
    },
    delItem(index, org) {
      const executeDel = () => {
        this.dataSource.splice(index, 1);
        this.setOrgVersionIds();
        this.$emit('input', this.dataSource);
      };
      if (this.useOrgId === org.id) {
        executeDel();
        return;
      }
      const userKeys = ['users', 'transferUsers', 'copyUsers', 'emptyToUsers', 'monitors', 'decisionMakers'];
      let delTaskUsers = {};
      if (this.graph.instance) {
        let nodes = this.graph.instance.getNodes();
        for (let index = 0; index < nodes.length; index++) {
          const node = nodes[index];
          const nodeData = node.data;
          const shape = node.shape;
          if (shape === 'NodeTask' || shape === 'NodeCollab') {
            userKeys.forEach(uk => {
              if (nodeData[uk]) {
                nodeData[uk].forEach((item, i) => {
                  if (item.orgId && item.orgId === org.id) {
                    if (!delTaskUsers[index]) {
                      delTaskUsers[index] = [];
                    }
                    delTaskUsers[index].push({
                      userKey: uk,
                      userIndex: i
                    });
                  }
                });
              }
            });
          }
        }
        if (Object.keys(delTaskUsers).length) {
          this.$confirm({
            title: '提示',
            content: '流程环节中已配置该组织机构为办理人，删除该组织时将同时清除流程环节中关于该组织的配置',
            onOk: () => {
              for (const key in delTaskUsers) {
                delTaskUsers[key].forEach(d => {
                  if (nodes[key]['data'][d.userKey]) {
                    nodes[key]['data'][d.userKey].splice(d.userIndex, 1);
                  }
                });
              }
              executeDel();
            }
          });
        } else {
          executeDel();
        }
      }
    },
    addItem() {
      this.currentIndex = -1;
      this.currentItem = this.createItem();
      this.currentOrgId = '';
      this.visible = true;
    },
    saveItem(callback) {
      this.$refs.form.validate((valid, error) => {
        if (valid) {
          if (this.currentIndex === -1) {
            this.dataSource.push(this.currentItem);
          } else {
            this.dataSource.splice(this.currentIndex, 1, this.currentItem);
          }
          if (typeof callback === 'function') {
            callback(true);
          } else {
            this.visible = false;
          }
          this.setOrgVersionIds();
          this.$emit('input', this.dataSource);
        }
      });
    },
    setOrgVersionIds() {
      const orgIds = this.dataSource.map(item => item.orgId);
      this.pageContext.emitEvent('getMultiOrgVersionId', { orgIds });
    },
    changeOrgId(orgId) {
      this.currentItem.bizOrgId = '';
      this.bizOrgsList = this.designer.getBizOrgsListByOrgId(orgId);
    },
    changeBizOrgType(event) {
      const value = event.target.value;
      if (value === this.availableBizOrgOptions[2]['value']) {
        this.bizOrgsList = this.designer.getBizOrgsListByOrgId(this.currentItem.orgId);
      }
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    }
  }
};
</script>
