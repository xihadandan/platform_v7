<template>
  <a-card
    style="width: 100%"
    :tab-list="tabList"
    :active-tab-key="activeTabKey"
    @tabChange="key => onTabChange(key)"
    :bodyStyle="{
      backgroundColor: 'var(--w-bg-color-layout)'
    }"
    class="api-link-detail"
  >
    <template slot="title">
      <a-descriptions bordered :column="2">
        <template slot="title">
          <div style="display: flex; align-items: center; justify-content: space-between">
            基本信息
            <div>
              <ExportDef :uuid="apiLink.uuid" type="apiLink" v-if="apiLink.uuid != undefined" :container="getModalContainer">
                <a-button>导出</a-button>
              </ExportDef>
              <a-button type="primary" @click="save" style="margin-left: 8px">保存</a-button>
            </div>
          </div>
        </template>
        <a-descriptions-item label="服务名称">
          <a-input v-model.trim="apiLink.name" placeholder="请输入服务名称" allow-clear />
        </a-descriptions-item>
        <a-descriptions-item label="服务 ID">
          <a-input v-model.trim="apiLink.id" allow-clear :readOnly="true" />
        </a-descriptions-item>
        <a-descriptions-item label="备注" :span="2"><a-textarea v-model.trim="apiLink.remark" allow-clear /></a-descriptions-item>
      </a-descriptions>
    </template>
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
    <template slot="tabBarExtraContent">
      <a-input-search
        style="position: absolute; right: 0px; width: 180px; top: 10px; z-index: 1"
        v-if="activeTabKey == 'apiMgr'"
        v-model.trim="searchKeyword"
        allow-clear
        placeholder="搜索API"
      />
      <a-button
        style="position: absolute; right: 0px; top: 15px; z-index: 1"
        icon="reload"
        size="small"
        type="link"
        v-if="activeTabKey == 'apiLog'"
        @click="refreshApiInvokeLogs"
      >
        刷新
      </a-button>
    </template>
    <div v-if="activeTabKey === 'basic'">
      <div style="text-align: center; padding-bottom: 12px">
        <a-radio-group v-model="selectedEnv" button-style="solid">
          <a-radio-button value="prod">
            <a-tooltip>
              <template slot="title">当前运行环境</template>
              <a-badge status="success" v-if="currentEnv == 'prod'" />
            </a-tooltip>
            生产环境
          </a-radio-button>
          <a-radio-button value="stag">
            <a-tooltip>
              <template slot="title">当前运行环境</template>
              <a-badge status="success" v-if="currentEnv == 'stag'" />
            </a-tooltip>
            预生产环境
          </a-radio-button>
          <a-radio-button value="test">
            <a-tooltip>
              <template slot="title">当前运行环境</template>
              <a-badge status="success" v-if="currentEnv == 'test'" />
            </a-tooltip>
            测试环境
          </a-radio-button>
          <a-radio-button value="dev">
            <a-tooltip>
              <template slot="title">当前运行环境</template>
              <a-badge status="success" v-if="currentEnv == 'dev'" />
            </a-tooltip>
            开发环境
          </a-radio-button>
        </a-radio-group>
      </div>
      <a-card style="width: 100%; margin-bottom: 12px">
        <a-card-meta description="配置服务相关参数" style="padding-top: 8px">
          <template slot="title">
            <div style="display: flex; justify-content: space-between; align-items: center">服务</div>
          </template>
          <a-avatar shape="square" :size="64" icon="cloud" slot="avatar" class="card-avatar" />
        </a-card-meta>
        <a-divider />
        <a-form-model :model="authForm" ref="authForm" class="pt-form" :colon="false">
          <a-form-model-item>
            <template slot="label">
              服务地址
              <a-tooltip title="指定服务后, API管理的所有API运行时都会使用该服务对应的 “前置 URL”">
                <a-button icon="question-circle" type="link"></a-button>
              </a-tooltip>
            </template>
            <a-input v-model.trim="apiLink.endpoint" v-if="selectedEnv == 'prod'" />
            <a-input v-model.trim="apiLink.testEndpoint" v-else-if="selectedEnv == 'test'" />
            <a-input v-model.trim="apiLink.stagEndpoint" v-else-if="selectedEnv == 'stag'" />
            <a-input v-model.trim="apiLink.devEndpoint" v-else="selectedEnv == 'dev'" />
          </a-form-model-item>
        </a-form-model>
      </a-card>
      <!-- <a-card style="width: 100%">
        <a-card-meta description="用于配置鉴权的使用">
          <template slot="title">
            <div style="display: flex; justify-content: space-between; align-items: center">连接参数</div>
          </template>
          <a-avatar shape="square" :size="64" icon="control" slot="avatar" class="card-avatar" />
        </a-card-meta>
        <a-divider />
        <a-table :columns="linkParamColumns" :data-source="linkParams" rowKey="id" :pagination="false" :locale="{ emptyText: '无数据' }">
          <template slot="paramNameSlot" slot-scope="text, record, index">
            <a-input v-model="record.paramName" />
          </template>
          <template slot="paramValueSlot" slot-scope="text, record, index"><a-input v-model="record.paramValue" /></template>
          <template slot="operationSlot" slot-scope="text, record, index">
            <a-button icon="delete" type="link" size="small" @click="linkParams.splice(index, 1)">删除</a-button>
          </template>
        </a-table>
        <a-button icon="plus" size="small" type="link" @click="onClickAddLinkParam">连接参数</a-button>
      </a-card> -->
      <a-card style="width: 100%; margin-bottom: 12px">
        <a-card-meta description="选择API如何进行鉴权" style="padding-top: 8px">
          <template slot="title">
            <div style="display: flex; justify-content: space-between; align-items: center">鉴权方式</div>
          </template>
          <a-avatar shape="square" :size="64" icon="security-scan" slot="avatar" class="card-avatar" />
        </a-card-meta>
        <a-divider />
        <a-form-model
          :model="authForm[selectedEnv]"
          :label-col="labelCol"
          :wrapper-col="wrapperCol"
          ref="authForm"
          class="pt-form"
          :colon="false"
        >
          <a-form-model-item label="鉴权方式" prop="authType">
            <a-select style="width: 100%" v-model="authForm[selectedEnv].authType">
              <a-select-option value="none">无需鉴权</a-select-option>
              <a-select-option value="BearerToken">Bearer Token</a-select-option>
              <a-select-option value="OAuth2">OAuth 2.0</a-select-option>
            </a-select>
          </a-form-model-item>
          <div v-if="authForm[selectedEnv].authType === 'BearerToken'">
            <a-form-model-item label="Token信息头部名称">
              <a-auto-complete
                v-model="authForm[selectedEnv].tokenHeaderName"
                :data-source="['Authorization', 'Authorization-JWT']"
                style="width: 100%"
                :filterOption="true"
              />
            </a-form-model-item>
            <a-form-model-item label="Token">
              <a-textarea v-model="authForm[selectedEnv].token" v-show="!authForm[selectedEnv].useUserToken" />
              <a-checkbox v-model="authForm[selectedEnv].useUserToken">使用当前用户认证Token</a-checkbox>
            </a-form-model-item>
          </div>
          <div v-if="authForm[selectedEnv].authType === 'OAuth2'">
            <a-form-model-item label="授权模式">
              <a-select style="width: 100%" v-model="authForm[selectedEnv].oauth2.grantType">
                <!-- <a-select-option value="authorization_code">授权码模式</a-select-option> -->
                <a-select-option value="client_credentials">客户端模式</a-select-option>
                <a-select-option value="password">密码模式</a-select-option>
                <a-select-option value="jwt-bearer">JWT断言凭证</a-select-option>
              </a-select>
            </a-form-model-item>
            <a-form-model-item label="客户端ID"><a-input v-model="authForm[selectedEnv].oauth2.clientId" /></a-form-model-item>
            <a-form-model-item label="客户端密钥">
              <a-input-password v-model="authForm[selectedEnv].oauth2.clientSecret" />
            </a-form-model-item>
            <a-form-model-item label="获取Access Token地址">
              <a-input v-model="authForm[selectedEnv].oauth2.accessTokenUrl" />
            </a-form-model-item>
            <div v-show="authForm[selectedEnv].oauth2.grantType === 'authorization_code'">
              <a-form-model-item label="授权认证地址">
                <a-input v-model="authForm[selectedEnv].oauth2.authUrl" />
              </a-form-model-item>
              <a-form-model-item label="刷新Access Token地址">
                <a-input v-model="authForm[selectedEnv].oauth2.refreshTokenUrl" />
              </a-form-model-item>
            </div>
            <a-form-model-item label="授权回调地址" v-show="authForm[selectedEnv].oauth2.grantType === 'authorization_code'">
              <a-input v-model="authForm[selectedEnv].oauth2.callbackUrl" />
            </a-form-model-item>
            <div v-show="authForm[selectedEnv].oauth2.grantType === 'password'">
              <a-form-model-item label="用户名"><a-input v-model="authForm[selectedEnv].oauth2.username" /></a-form-model-item>
              <a-form-model-item label="密码"><a-input-password v-model="authForm[selectedEnv].oauth2.password" /></a-form-model-item>
            </div>
            <div v-show="authForm[selectedEnv].oauth2.grantType === 'jwt-bearer'">
              <a-form-model-item label="算法">
                <a-select :options="algorithmOptions" v-model="authForm[selectedEnv].oauth2.algorithm" style="width: 100%" />
              </a-form-model-item>
              <a-form-model-item label="Secret">
                <a-textarea v-if="!secretKeyHidden[selectedEnv]" v-model="authForm[selectedEnv].oauth2.secretKey" />
                <a-space v-else="secretKeyHidden[selectedEnv]">
                  <label>******</label>
                  <a-button
                    type="link"
                    size="small"
                    @click="
                      secretKeyHidden[selectedEnv] = false;
                      authForm[selectedEnv].oauth2.secretKey = undefined;
                    "
                  >
                    修改
                  </a-button>
                </a-space>
              </a-form-model-item>
              <a-form-model-item label="Payload">
                <WidgetCodeEditor
                  v-model="authForm[selectedEnv].oauth2.payload"
                  width="auto"
                  height="300px"
                  lang="json"
                  ref="jsonEditor"
                ></WidgetCodeEditor>
              </a-form-model-item>
            </div>
            <a-form-model-item>
              <template slot="label">
                <span>
                  scope
                  <a-popover placement="top">
                    <template slot="content">
                      <p>表示客户端请求的权限范围，定义了应用程序希望访问的用户资源的类型和程度, 通常是由空格分隔的权限列表</p>
                    </template>
                    <a-icon type="info-circle" />
                  </a-popover>
                </span>
              </template>
              <a-input v-model="authForm[selectedEnv].oauth2.scope" />
            </a-form-model-item>
          </div>
        </a-form-model>
      </a-card>

      <a-card style="width: 100%">
        <a-card-meta description="API容错保护机制, 当系统出现异常时自动触发保护策略, 保障核心业务可用性" style="padding-top: 8px">
          <template slot="title">
            <div style="display: flex; justify-content: space-between; align-items: center">容错策略</div>
          </template>
          <a-avatar shape="square" :size="64" icon="insurance" slot="avatar" class="card-avatar" />
        </a-card-meta>
        <a-divider style="margin-bottom: 0px" />
        <a-tabs default-active-key="timeout">
          <a-tab-pane key="timeout" tab="请求超时">
            <a-form-model :label-col="labelCol" :wrapper-col="wrapperCol" class="pt-form" :colon="false">
              <a-form-model-item label="全局默认">
                <a-input-number v-model="faultToleranceConfigForm[selectedEnv].timeout" :min="1000" :max="180000" />
                毫秒
              </a-form-model-item>
            </a-form-model>
          </a-tab-pane>
          <!-- <a-tab-pane key="circuitBreaker" tab="熔断保护">
            <a-form-model
              :model="faultToleranceConfigForm[selectedEnv].circuitBreaker"
              :label-col="labelCol"
              :wrapper-col="wrapperCol"
              class="pt-form"
              :colon="false"
            >
              <a-form-model-item label="启用">
                <a-switch v-model="faultToleranceConfigForm[selectedEnv].circuitBreaker.enabled" />
              </a-form-model-item>
            </a-form-model>
          </a-tab-pane> -->
        </a-tabs>
      </a-card>
    </div>
    <div v-else-if="activeTabKey === 'apiMgr'">
      <div v-if="apiLinkOperations.length == 0">
        <a-empty>
          <template slot="description">
            暂无 API 可用, 请
            <Modal
              title="创建 API"
              :container="getModalContainer"
              width="calc(100vw - 500px)"
              maxHeight="700px"
              ref="createApiModal"
              :bodyStyle="{ minHeight: '670px' }"
              :key="createApiOperationKey"
            >
              <template slot="content" slot-scope="{ closeModal }">
                <ApiOperation :apiLink="apiLink" @saveOperationSuccess="e => onSaveOperationSuccess(e, undefined, closeModal)" />
              </template>
              <a-button type="link" size="small">创建 API</a-button>
            </Modal>
          </template>
        </a-empty>
      </div>
      <a-list v-else :data-source="filterApiLinkOperations" class="api-operation-list">
        <a-list-item
          slot="renderItem"
          slot-scope="item, index"
          :style="{
            backgroundColor: '#fff',
            padding: '12px',
            borderRadius: '4px'
          }"
        >
          <a-list-item-meta>
            <template slot="title">
              <div style="display: flex; align-items: center">
                <span :style="{ fontWeight: 'bolder', display: 'inline-block', width: '80px' }">
                  <a-tag :color="methodColor[item.method]" style="">{{ item.method }}</a-tag>
                </span>
                <div style="width: 325px">
                  <div
                    :title="item.name"
                    style="font-weight: bolder; overflow: hidden; width: 325px; text-overflow: ellipsis; white-space: nowrap"
                  >
                    {{ item.name }}
                  </div>
                  <div
                    style="color: var(--w-link-color); overflow: hidden; text-overflow: ellipsis; white-space: nowrap"
                    :title="item.path"
                  >
                    {{ item.path }}
                  </div>
                </div>
              </div>
            </template>
          </a-list-item-meta>
          <span slot="actions">
            <Modal
              title="查看调用日志"
              width="calc(100vw - 500px)"
              maxHeight="700px"
              ref="editApiModal"
              :container="getModalContainer"
              :bodyStyle="{ minHeight: '670px' }"
            >
              <template slot="content" slot-scope="{ closeModal }">
                <ApiInvokeLogs :api-link-uuid="apiLink.uuid" :api-operation-uuid="item.uuid" />
              </template>
              <a-button size="small" type="link" icon="history" title="API 日志"></a-button>
            </Modal>
          </span>
          <span slot="actions">
            <ExportDef :uuid="item.uuid" type="apiOperation" :container="getModalContainer">
              <a-button size="small" type="link" icon="export" title="导出"></a-button>
            </ExportDef>
          </span>
          <span slot="actions">
            <Modal
              title="编辑 API"
              :container="getModalContainer"
              width="calc(100vw - 500px)"
              maxHeight="700px"
              ref="editApiModal"
              :bodyStyle="{ minHeight: '670px' }"
            >
              <template slot="content" slot-scope="{ closeModal }">
                <ApiOperation
                  :apiLink="apiLink"
                  :operation="item"
                  @saveOperationSuccess="e => onSaveOperationSuccess(e, item, closeModal)"
                />
              </template>
              <a-button size="small" type="link" icon="setting" title="配置"></a-button>
            </Modal>
          </span>
          <span slot="actions">
            <a-popconfirm
              placement="topRight"
              title="确定要删除该API吗?"
              ok-text="确定"
              cancel-text="取消"
              @confirm="deleteApiOperation(item)"
            >
              <a-button size="small" type="link" icon="delete" style="color: var(--w-danger-color)" title="删除"></a-button>
            </a-popconfirm>
          </span>
        </a-list-item>
        <template slot="footer">
          <Modal
            title="创建 API"
            :container="getModalContainer"
            width="calc(100vw - 500px)"
            maxHeight="700px"
            ref="createApiModal"
            style="width: 100%"
            :destroyOnClose="true"
            :bodyStyle="{ minHeight: '670px' }"
          >
            <template slot="content" slot-scope="{ closeModal }">
              <ApiOperation :apiLink="apiLink" @saveOperationSuccess="e => onSaveOperationSuccess(e, undefined, closeModal)" />
            </template>
            <a-button :block="true" type="link" icon="plus">创建 API</a-button>
          </Modal>
        </template>
      </a-list>
    </div>
    <div v-else-if="activeTabKey == 'apiLog'" style="position: relative">
      <Scroll style="height: 475px">
        <ApiInvokeLogs :api-link-uuid="apiLink.uuid" :key="apiInvokeLogsKeys" />
      </Scroll>
    </div>
  </a-card>
</template>
<style lang="less">
.api-link-detail {
  .card-avatar {
    background: transparent;
    color: var(--w-primary-color);
    outline: 1px solid var(--w-primary-color);
  }

  .api-operation-list {
    .ant-list-item {
      margin-bottom: 12px;
      &:last-child {
        margin-bottom: 0px;
      }
    }
  }
}
</style>
<script type="text/babel">
import ApiOperation from './component/api-operation.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';
import ApiInvokeLogs from './component/api-invoke-logs.vue';
import { cloneDeep } from 'lodash';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
export default {
  name: 'ApiLinkDetail',
  inject: ['$event', 'currentWindow', 'pageContext'],
  props: {},
  components: { ApiOperation, Modal, WidgetCodeEditor, Drawer, ApiInvokeLogs, ExportDef },
  computed: {
    filterApiLinkOperations() {
      if (this.searchKeyword == undefined || this.searchKeyword == '') {
        return this.apiLinkOperations;
      }
      let options = [];
      for (let i = 0, len = this.apiLinkOperations.length; i < len; i++) {
        if (
          this.apiLinkOperations[i].name.toLowerCase().indexOf(this.searchKeyword) > -1 ||
          this.apiLinkOperations[i].path.toLowerCase().indexOf(this.searchKeyword) > -1
        ) {
          options.push(this.apiLinkOperations[i]);
        }
      }
      return options;
    },
    tabList() {
      let tabs = [
        { key: 'basic', tab: '连接设置' },
        { key: 'apiMgr', tab: `API 管理 (${this.apiLinkOperations.length})` }
      ];
      if (this.apiLink.uuid) {
        tabs.push({ key: 'apiLog', tab: 'API 日志' });
      }
      return tabs;
    },
    methodColor() {
      return {
        GET: '#17b26a',
        POST: '#ef6820',
        DELETE: '#f04438',
        PUT: '#2e90fa',
        OPTIONS: '#2e90fa'
      };
    }
  },
  data() {
    let apiLink = {};
    if (this.$event && this.$event.meta && this.$event.meta.UUID) {
      apiLink.uuid = this.$event.meta.UUID;
      apiLink.name = this.$event.meta.NAME;
      apiLink.id = this.$event.meta.ID;
      apiLink.remark = this.$event.meta.REMARK;
      apiLink.protocol = this.$event.meta.PROTOCOL;
    }
    let algorithmOptions = [];
    for (let a of ['HS256', 'HS384', 'HS512', 'RS256', 'RS384', 'RS512', 'PS256', 'PS384', 'PS512', 'ES256']) {
      algorithmOptions.push({
        label: a,
        value: a
      });
    }

    let basicAuth = {
        authType: 'none',
        oauth2: {
          grantType: 'client_credentials',
          accessTokenUrl: undefined,
          refreshTokenUrl: undefined,
          clientId: undefined,
          clientSecret: undefined,
          callbackUrl: undefined,
          authUrl: undefined,
          username: undefined,
          password: undefined,
          secretKey: undefined,
          payload: undefined,
          algorithm: 'HS256'
        }
      },
      basicFaultToleranceConfig = {
        timeout: 5000, // 超时时间 单位毫秒
        circuitBreaker: {
          /*
            熔断器工作流程示例：
              统计最近60秒(slidingWindowSize)的请求：
              总请求数 ≥ 10次(minimumCalls)
              失败率 > 50%(failureThreshold) 或 超时率 > 30%(timeoutThreshold)
              触发熔断，立即拒绝所有请求
              30秒后(waitDuration)，允许1个试探请求
              若试探成功则关闭熔断，否则重新计时
          */
          enabled: true,
          failureThreshold: 0.5, //错误率
          timeoutThreshold: 0.3, //超时请求占比阈值
          minimumCalls: 10, // 最小统计请求数
          slidingWindowSize: 60, //统计时间窗口大小 单位秒
          waitDuration: 30 // 熔断后等待恢复时间 单位秒
        }
      };
    let authForm = {
        prod: cloneDeep(basicAuth),
        dev: cloneDeep(basicAuth),
        test: cloneDeep(basicAuth),
        stag: cloneDeep(basicAuth)
      },
      faultToleranceConfigForm = {
        prod: cloneDeep(basicFaultToleranceConfig),
        dev: cloneDeep(basicFaultToleranceConfig),
        test: cloneDeep(basicFaultToleranceConfig),
        stag: cloneDeep(basicFaultToleranceConfig)
      };
    return {
      authForm,
      basicAuth,
      faultToleranceConfigForm,
      basicFaultToleranceConfig,
      currentEnv: 'prod',
      selectedEnv: 'prod',
      algorithmOptions,
      activeTabKey: 'basic',
      secretKeyHidden: {
        dev: false,
        prod: false,
        stag: false,
        test: false
      },
      linkParams: [],
      linkParamColumns: [
        {
          title: '参数名',
          dataIndex: 'paramName',
          scopedSlots: { customRender: 'paramNameSlot' }
        },
        {
          title: '参数值',
          dataIndex: 'paramValue',
          scopedSlots: { customRender: 'paramValueSlot' }
        },
        // {
        //   title: '说明',
        //   dataIndex: 'remark',
        //   scopedSlots: { customRender: 'remarkSlot' }
        // },
        {
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      labelCol: { span: 7 },
      wrapperCol: { span: 17 },
      apiLink,
      apiLinkOperations: [],
      loading: false,
      searchKeyword: undefined,
      apiInvokeLogsKeys: new Date().getTime()
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.currentEnv = {
      local: 'dev',
      prod: 'prod',
      unittest: 'test',
      stag: 'stag'
    }[window.__INITIAL_STATE__._CONTEXT_STATE_.ENV];

    if (this.apiLink.uuid) {
      this.loading = true;
      this.fetchApiLinkDetail(this.apiLink.uuid).then(data => {
        this.loading = false;
        for (let key in data) {
          this.$set(this.apiLink, key, data[key]);
        }
        if (data.authConfig) {
          let authConfig = JSON.parse(data.authConfig);
          for (let key in authConfig) {
            this.$set(this.authForm, key, authConfig[key] || cloneDeep(this.basicAuth));
          }
          for (let key of ['dev', 'prod', 'test', 'stag']) {
            if (this.authForm[key].oauth2.secretKey) {
              this.secretKeyHidden[key] = true;
            }
          }
        }
        if (data.faultToleranceConfig) {
          let faultToleranceConfig = JSON.parse(data.faultToleranceConfig);
          for (let key in faultToleranceConfig) {
            this.$set(this.faultToleranceConfigForm, key, faultToleranceConfig[key] || cloneDeep(this.basicFaultToleranceConfig));
          }
        }
        if (data.apiOperations) {
          this.apiLinkOperations.push(...data.apiOperations);
        }
      });
    }
  },
  mounted() {},
  methods: {
    refreshApiInvokeLogs() {
      this.apiInvokeLogsKeys = new Date().getTime();
    },
    getModalContainer() {
      return this.$el;
    },

    deleteApiOperation(item) {
      $axios
        .get(`/proxy/api/apiLink/deleteApiOperation/${item.uuid}`, { params: {} })
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('删除成功');
            for (let i = 0, len = this.apiLinkOperations.length; i < len; i++) {
              if (this.apiLinkOperations[i].uuid == item.uuid) {
                this.apiLinkOperations.splice(i, 1);
                break;
              }
            }
          } else {
            this.$message.error('删除失败');
          }
        })
        .catch(error => {
          this.$message.error('删除失败');
        });
    },
    onSaveOperationSuccess(data, item, hide) {
      if (item != undefined) {
        Object.assign(item, data);
        hide();
      } else {
        this.apiLinkOperations.push(data);
        this.$refs.createApiModal.hide();
      }
    },
    modalContainer() {
      return this.$el;
    },
    fetchApiLinkDetail(uuid) {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/apiLink/details/${uuid}`, { params: {} })
          .then(({ data }) => {
            if (data.data) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },
    onClickAddLinkParam() {
      this.linkParams.push({
        id: new Date().getTime(),
        paramName: undefined,
        paramValue: undefined,
        remark: undefined
      });
    },
    onTabChange(key, type) {
      this.activeTabKey = key;
    },
    save() {
      if (this.apiLink.name == undefined || this.apiLink.name == '') {
        this.$message.error('请填写连接名称');
        return;
      }

      let formData = {
        uuid: this.apiLink.uuid,
        name: this.apiLink.name,
        endpoint: this.apiLink.endpoint,
        remark: this.apiLink.remark,
        id: this.apiLink.id,
        protocol: this.apiLink.protocol,
        testEndpoint: this.apiLink.testEndpoint,
        devEndpoint: this.apiLink.devEndpoint,
        stagEndpoint: this.apiLink.stagEndpoint,
        authConfig: JSON.stringify(this.authForm),
        faultToleranceConfig: JSON.stringify(this.faultToleranceConfigForm)
      };

      this.$axios
        .post(`/proxy/api/apiLink/saveApiLink`, formData)
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('保存成功');
            if (this.currentWindow) {
              this.currentWindow.close();
              if (this.$event) {
                this.pageContext.emitEvent('NWKBEGDALjNhYtXCyZsEWafgBUKaaqCz:refetch');
              }
            }
          } else {
            this.$message.error('保存失败');
          }
        })
        .catch(error => {
          this.$message.error('保存失败');
        });
    }
  }
};
</script>
