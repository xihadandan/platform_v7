<template>
  <div class="api-operation">
    <a-page-header style="padding: 0px">
      <template slot="title">
        <div>
          <a-row type="flex" :gutter="12">
            <a-col :flex="1">
              <a-input-group compact>
                <a-button style="width: 90px">名称</a-button>
                <a-input style="width: 200px" v-model.trim="apiOperation.name" placeholder="请输入API名称" allow-clear></a-input>
              </a-input-group>
            </a-col>
            <a-col :flex="1">
              <a-input-group compact>
                <a-button style="width: 90px">编码</a-button>
                <a-input style="width: 200px" v-model.trim="apiOperation.code" placeholder="请输入API编码" allow-clear></a-input>
              </a-input-group>
            </a-col>
            <a-col :flex="1">
              <a-input-group compact>
                <a-button style="width: 90px">超时时间</a-button>
                <a-tooltip title="为空时, 使用全局默认的超时时间">
                  <a-input-number v-model="apiOperation.timeout" :min="1000" :max="180000" style="width: 165px" />
                </a-tooltip>
                <a-button>毫秒</a-button>
              </a-input-group>
            </a-col>
          </a-row>
          <a-input-group compact style="margin-top: 12px">
            <a-select
              v-model="apiOperation.method"
              :options="apiLink.protocol == 'REST' ? methodOptions : soapMethodOptions"
              style="width: 120px"
              @change="onChangeHttpMethod"
            ></a-select>
            <a-input
              style="width: calc(100vw - 800px)"
              v-model.trim="apiOperation.path"
              @change="onChangePath"
              :placeholder="apiLink.protocol == 'REST' ? 'API接口路径, “/”起始' : '调用方法'"
            />
          </a-input-group>
        </div>
      </template>
      <template slot="extra">
        <a-button type="primary" @click="onSaveApiOperation" :loading="saving">保存</a-button>
      </template>

      <template slot="footer">
        <a-tabs :default-active-key="defaultActiveKey">
          <a-tab-pane key="Params" tab="查询参数 (Query)" v-if="apiLink.protocol == 'REST'">
            <Scroll style="height: 500px; padding: 12px">
              <a-table
                size="small"
                :columns="queryParamColumns"
                :data-source="queryParams"
                rowKey="id"
                :pagination="false"
                :locale="{ emptyText: '无数据' }"
              >
                <template slot="title">Query 参数</template>
                <template slot="nameSlot" slot-scope="text, record, index">
                  <a-input v-model="record.name" class="parameter-input">
                    <!-- <div slot="suffix">
                      <a-tooltip placement="top" title="是否必需">
                        <a-checkbox v-model="record.isRequired" />
                      </a-tooltip>
                    </div> -->
                  </a-input>
                </template>
                <template slot="exampleValueSlot" slot-scope="text, record, index"><a-input v-model="record.exampleValue" /></template>
                <template slot="remarkSlot" slot-scope="text, record, index">
                  <div style="display: flex; align-items: center">
                    <a-input v-model="record.remark" />
                    <a-button icon="delete" type="link" size="small" @click="deleteParameter(record)">删除</a-button>
                  </div>
                </template>

                <template slot="footer">
                  <a-button
                    type="link"
                    size="small"
                    icon="plus"
                    @click="
                      addParameter({
                        paramType: 'query'
                      })
                    "
                  >
                    Query 参数
                  </a-button>
                </template>
              </a-table>

              <a-table
                size="small"
                v-show="pathVariables.length > 0"
                :columns="queryParamColumns"
                :data-source="pathVariables"
                rowKey="id"
                :pagination="false"
                :locale="{ emptyText: '无数据' }"
                style="margin-top: 12px"
              >
                <template slot="title">路径参数 (Path)</template>
                <template slot="nameSlot" slot-scope="text, record, index">
                  {{ text }}
                </template>
                <template slot="exampleValueSlot" slot-scope="text, record, index"><a-input v-model="record.exampleValue" /></template>
                <template slot="remarkSlot" slot-scope="text, record, index"><a-input v-model="record.remark" /></template>
              </a-table>
            </Scroll>
          </a-tab-pane>
          <a-tab-pane
            key="Body"
            tab="请求体参数 (Body)"
            :forceRender="true"
            v-if="
              apiLink.protocol == 'SOAP' || apiOperation.method == 'POST' || apiOperation.method == 'PUT' || apiOperation.method == 'PATCH'
            "
          >
            <Scroll style="height: 500px; padding: 12px">
              <div
                v-if="apiLink.protocol == 'REST'"
                style="margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid rgb(234, 234, 234)"
              >
                <template v-for="tag in reqFormatTypes">
                  <a-checkable-tag :key="tag" :checked="apiOperation.reqFormatType == tag" @change="onChangeReqFormatType(tag)">
                    {{ tag }}
                  </a-checkable-tag>
                </template>
              </div>
              <ApiBodySchema
                v-show="apiOperation.reqFormatType == 'json' || apiLink.protocol == 'SOAP'"
                ref="reqApiBodySchema"
                :initValue="apiOperation.reqBodySchemaInitValue"
              />
              <a-table
                v-if="apiLink.protocol == 'REST'"
                v-show="apiOperation.reqFormatType == 'form-data' || apiOperation.reqFormatType == 'x-www-form-urlencoded'"
                size="small"
                :columns="queryParamColumns"
                :data-source="bodyParams"
                rowKey="id"
                :pagination="false"
                :locale="{ emptyText: '无数据' }"
              >
                <template slot="nameSlot" slot-scope="text, record, index">
                  <a-input v-model="record.name" class="parameter-input">
                    <div slot="suffix">
                      <!-- <a-tooltip placement="top" title="是否必需">
                        <a-checkbox v-model="record.isRequired" />
                      </a-tooltip> -->

                      <a-popover placement="right">
                        <template slot="content">
                          <div>
                            <a-checkable-tag :checked="record.dataType == 'string'" @change="clickSelectDataType(record, 'string')">
                              字符
                            </a-checkable-tag>
                            <!-- <a-checkable-tag :checked="record.dataType == 'number'" @change="clickSelectDataType(record, 'number')">
                              数字
                            </a-checkable-tag>
                            <a-checkable-tag :checked="record.dataType == 'boolean'" @change="clickSelectDataType(record, 'boolean')">
                              布尔
                            </a-checkable-tag> -->
                            <a-checkable-tag
                              :checked="record.dataType == 'file'"
                              @change="clickSelectDataType(record, 'file')"
                              :style="
                                apiOperation.reqFormatType == 'x-www-form-urlencoded' ? { color: '#c6c6c6', cursor: 'not-allowed' } : {}
                              "
                            >
                              附件
                            </a-checkable-tag>
                          </div>
                        </template>

                        <a-tag
                          :style="{
                            background: '#fff',
                            borderStyle: record.dataType == undefined ? 'dashed' : 'solid',
                            lineHeight: '22px',
                            margin: '0px'
                          }"
                        >
                          {{ { string: '字符', file: '附件', number: '数字', boolean: '布尔' }[record.dataType] || '类型' }}
                        </a-tag>
                      </a-popover>
                    </div>
                  </a-input>
                </template>
                <template slot="exampleValueSlot" slot-scope="text, record, index"><a-input v-model="record.exampleValue" /></template>
                <template slot="remarkSlot" slot-scope="text, record, index">
                  <div style="display: flex; align-items: center">
                    <a-input v-model="record.remark" />
                    <a-button icon="delete" type="link" size="small" @click="deleteParameter(record)">删除</a-button>
                  </div>
                </template>

                <template slot="footer">
                  <a-button
                    type="link"
                    size="small"
                    icon="plus"
                    @click="
                      addParameter({
                        paramType: 'body'
                      })
                    "
                  >
                    参数
                  </a-button>
                </template>
              </a-table>
              <a-empty v-show="apiOperation.reqFormatType == 'none'" description="该请求没有 Body" />
            </Scroll>
          </a-tab-pane>
          <a-tab-pane key="Headers" tab="请求头参数 (Headers)">
            <Scroll style="height: 500px; padding: 12px">
              <a-table
                size="small"
                :columns="queryParamColumns"
                :data-source="headers"
                rowKey="id"
                :pagination="false"
                :locale="{ emptyText: '无数据' }"
              >
                <template slot="title">Headers 参数</template>
                <template slot="nameSlot" slot-scope="text, record, index">
                  <a-auto-complete v-model="record.name" :data-source="commonHeaders" style="width: 200px" :filterOption="true" />
                </template>
                <template slot="exampleValueSlot" slot-scope="text, record, index"><a-input v-model="record.exampleValue" /></template>
                <template slot="remarkSlot" slot-scope="text, record, index">
                  <div style="display: flex; align-items: center">
                    <a-input v-model="record.remark" />
                    <a-button icon="delete" type="link" size="small" @click="deleteParameter(record)">删除</a-button>
                  </div>
                </template>

                <template slot="footer">
                  <a-button
                    type="link"
                    size="small"
                    icon="plus"
                    @click="
                      addParameter({
                        paramType: 'header'
                      })
                    "
                  >
                    Header 参数
                  </a-button>
                </template>
              </a-table>
            </Scroll>
          </a-tab-pane>

          <a-tab-pane key="response" tab="返回响应" :forceRender="true">
            <Scroll style="height: 500px; padding: 12px">
              <div style="margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid rgb(234, 234, 234)">
                <template v-for="tag in resFormatTypes">
                  <a-checkable-tag :key="tag" :checked="apiOperation.resFormatType == tag" @change="apiOperation.resFormatType = tag">
                    {{ tag }}
                  </a-checkable-tag>
                </template>
              </div>

              <ApiBodySchema
                v-show="apiOperation.resFormatType == 'JSON'"
                ref="resApiBodySchema"
                :initValue="apiOperation.resBodySchemaInitValue"
              />
            </Scroll>
          </a-tab-pane>
        </a-tabs>
      </template>
    </a-page-header>
  </div>
</template>
<style lang="less">
.api-operation {
  .ant-page-header-heading {
    background-color: var(--w-bg-color-layout);
    padding: 12px;
  }
  .parameter-input {
    input {
      padding-right: 80px !important;
    }
  }
}
</style>
<script type="text/babel">
import { debounce, cloneDeep } from 'lodash';
import { generateId } from '@framework/vue/utils/util';
import ApiBodySchema from './api-body-schema.vue';

export default {
  name: 'ApiOperation',
  props: {
    operation: Object,
    apiLink: Object
  },
  components: { ApiBodySchema },
  computed: {
    queryParams() {
      return this.apiOperation.parameters.filter(p => p.paramType == 'query');
    },
    headers() {
      return this.apiOperation.parameters.filter(p => p.paramType == 'header');
    },
    pathVariables() {
      return this.apiOperation.parameters.filter(p => p.paramType == 'path');
    },
    bodyParams() {
      return this.apiOperation.parameters.filter(p => p.paramType == 'body');
    }
  },
  data() {
    let apiOperation = {
      apiLinkUuid: this.apiLink.uuid,
      method: 'GET',
      parameters: [],
      reqFormatType: 'json',
      resFormatType: 'JSON',
      reqBodySchemaInitValue: {},
      resBodySchemaInitValue: {}
    };
    if (this.operation) {
      apiOperation = cloneDeep(this.operation);
      if (apiOperation.parameters == undefined) {
        apiOperation.parameters = [];
      }
      apiOperation.reqBodySchemaInitValue = {};
      apiOperation.resBodySchemaInitValue = {};
      if (this.operation.bodySchema) {
        for (let b of this.operation.bodySchema) {
          if (b.applyTo == 'request') {
            if (b.schemaConfig) {
              apiOperation.reqBodySchemaInitValue.rows = JSON.parse(b.schemaConfig);
            }
            if (b.exampleBody) {
              apiOperation.reqBodySchemaInitValue.exampleBody = b.exampleBody;
            }
          } else if (b.applyTo == 'response') {
            if (b.schemaConfig) {
              apiOperation.resBodySchemaInitValue.rows = JSON.parse(b.schemaConfig);
            }
            if (b.exampleBody) {
              apiOperation.resBodySchemaInitValue.exampleBody = b.exampleBody;
            }
          }
        }
      }
    }
    return {
      apiOperation,
      defaultActiveKey: this.apiLink.protocol == 'REST' ? 'Params' : 'Body',
      soapMethodOptions: [
        {
          label: 'GET',
          value: 'GET',
          color: '#17b26a'
        },
        {
          label: 'POST',
          value: 'POST',
          color: '#ef6820'
        }
      ],
      methodOptions: [
        {
          label: 'GET',
          value: 'GET',
          color: '#17b26a'
        },
        {
          label: 'POST',
          value: 'POST',
          color: '#ef6820'
        },
        {
          label: 'DELETE',
          value: 'DELETE',
          color: '#f04438'
        },
        {
          label: 'PUT',
          value: 'PUT',
          color: '#2e90fa'
        },
        {
          label: 'OPTIONS',
          value: 'OPTIONS',
          color: '#2e90fa'
        }
      ],
      reqFormatTypes: ['none', 'json', 'form-data', 'x-www-form-urlencoded'],
      resFormatTypes: ['JSON', 'XML', 'HTML', 'Raw', 'No-Content'],
      queryParamColumns: [
        {
          title: '参数名',
          dataIndex: 'name',
          scopedSlots: { customRender: 'nameSlot' }
        },
        {
          title: '示例值',
          dataIndex: 'exampleValue',
          scopedSlots: { customRender: 'exampleValueSlot' }
        },
        {
          title: '说明',
          dataIndex: 'remark',
          scopedSlots: { customRender: 'remarkSlot' }
        }
      ],
      commonHeaders: [
        // 请求控制
        'Host', // 目标域名（HTTP/1.1 必需字段）
        'Connection', // 控制连接（如 `keep-alive` 或 `close`）
        'Cache-Control', // 缓存策略（如 `no-cache`）

        // 内容协商
        'Accept', // 客户端接受的响应类型（如 `application/json`）
        'Accept-Encoding', // 支持的压缩编码（如 `gzip`）
        'Accept-Language', // 优先语言（如 `en-US`）
        'Accept-Charset', // 优先字符集（如 `utf-8`）

        // 身份认证
        'Authorization', // 凭证（如 `Bearer <token>`）
        'Cookie', // 客户端发送的 Cookie

        // 请求来源
        'Referer', // 来源页面 URL
        'User-Agent', // 客户端标识（如浏览器信息）

        // 请求体相关
        'Content-Type', // 请求体的媒体类型（如 `application/json`）
        'Content-Length', // 请求体的字节长度

        // 代理和调试
        'X-Forwarded-For', // 客户端原始 IP（代理链中使用）
        'X-Request-ID' // 请求唯一标识（用于追踪）
      ],
      saving: false,
      deletedPathParameterMap: {}
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeReqFormatType(tag) {
      let before = this.apiOperation.reqFormatType;
      this.apiOperation.reqFormatType = tag;
      if (tag == 'x-www-form-urlencoded' && this.bodyParams.length > 0) {
        for (let i = 0, len = this.bodyParams.length; i < len; i++) {
          if (this.bodyParams[i].dataType == 'file') {
            this.bodyParams[i].dataType = 'string';
          }
        }
      }
    },
    clickSelectDataType(record, type) {
      if (type == 'file' && this.apiOperation.reqFormatType == 'x-www-form-urlencoded') {
        this.$message.error('该请求格式下不能选择附件类型');
        return;
      }
      if (record.dataType == type) {
        return;
      }
      this.$set(record, 'dataType', type);
    },
    onChangeHttpMethod() {
      if (this.apiOperation.method == 'GET') {
        this.apiOperation.reqFormatType = 'none';
      }
    },
    onSaveApiOperation() {
      if (this.apiOperation.name == undefined || this.apiOperation.name == '') {
        this.$message.error('请输入API名称');
        return;
      }
      if (this.apiOperation.code == undefined || this.apiOperation.code == '') {
        this.$message.error('请输入API编码');
        return;
      }
      if (this.apiOperation.path == undefined || this.apiOperation.path == '') {
        this.$message.error('请输入API接口路径');
        return;
      }
      let formData = { ...this.apiOperation, bodySchema: [] };
      if (this.$refs.reqApiBodySchema) {
        formData.bodySchema.push({
          applyTo: 'request',
          schemaDefinition: JSON.stringify(this.$refs.reqApiBodySchema.getJSONSchema()),
          schemaConfig: JSON.stringify(this.$refs.reqApiBodySchema.rows),
          exampleBody: this.$refs.reqApiBodySchema.exampleBody
        });
      }
      if (this.$refs.resApiBodySchema) {
        formData.bodySchema.push({
          applyTo: 'response',
          schemaDefinition: JSON.stringify(this.$refs.resApiBodySchema.getJSONSchema()),
          schemaConfig: JSON.stringify(this.$refs.resApiBodySchema.rows),
          exampleBody: this.$refs.resApiBodySchema.exampleBody
        });
      }
      this.saving = true;
      this.$axios
        .post(`/proxy/api/apiLink/saveApiOperation`, formData)
        .then(({ data }) => {
          this.saving = false;
          if (data.code == 0) {
            this.$message.success('保存成功');
            formData.uuid = data.data;
            this.$emit('saveOperationSuccess', formData);
          } else {
            this.$message.error('保存失败');
          }
        })
        .catch(error => {
          this.saving = false;
          this.$message.error('保存失败');
        });
    },
    onChangePath: debounce(function () {
      if (this.apiLink.protocol == 'REST') {
        if (this.apiOperation.path) {
          if (!this.apiOperation.path.startsWith('/')) {
            this.apiOperation.path = '/' + this.apiOperation.path;
          }
          // 自动提取里面的路径参数
          const matches = this.apiOperation.path.match(/\{([^}]+)\}/g);
          let pathNames = [],
            _names = [];
          if (matches && matches.length > 0) {
            pathNames = matches.map(p => p.replace(/[{}]/g, ''));
            _names = [...pathNames];
            if (pathNames.length > 0) {
              for (let i = 0; i < this.apiOperation.parameters.length; i++) {
                if (this.apiOperation.parameters[i].paramType == 'path' && pathNames.includes(this.apiOperation.parameters[i].name)) {
                  pathNames.splice(pathNames.indexOf(this.apiOperation.parameters[i].name), 1);
                }
              }
              if (pathNames.length > 0) {
                for (let name of pathNames) {
                  this.apiOperation.parameters.push({
                    id: generateId(),
                    paramType: 'path',
                    dataType: 'string',
                    isRequired: true,
                    name,
                    exampleValue: this.deletedPathParameterMap[name] ? this.deletedPathParameterMap[name].exampleValue : undefined,
                    remark: this.deletedPathParameterMap[name] ? this.deletedPathParameterMap[name].remark : undefined
                  });
                }
              }
            }
          }
          for (let i = 0; i < this.apiOperation.parameters.length; i++) {
            if (this.apiOperation.parameters[i].paramType == 'path' && !_names.includes(this.apiOperation.parameters[i].name)) {
              let deletes = this.apiOperation.parameters.splice(i--, 1);
              for (let d of deletes) {
                this.deletedPathParameterMap[d.name] = d;
              }
            }
          }
        }
      }
    }, 500),

    addParameter(origin) {
      this.apiOperation.parameters.push(
        Object.assign(
          {
            id: generateId(),
            name: undefined,
            remark: undefined,
            exampleValue: undefined,
            dataType: 'string'
          },
          origin
        )
      );
    },
    deleteParameter(item) {
      for (let i = 0, len = this.apiOperation.parameters.length; i < len; i++) {
        if (this.apiOperation.parameters[i].id == item.id) {
          this.apiOperation.parameters.splice(i, 1);
          break;
        }
      }
    }
  }
};
</script>
