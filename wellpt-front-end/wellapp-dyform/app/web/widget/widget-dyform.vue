<template>
  <!-- 解析端容器 -->
  <div class="widget-dyform" :style="dyformStyle" :namespace="namespace">
    <div v-show="loading" class="dyform-loading">
      <a-spin :spinning="loading" />
    </div>
    <div v-show="!loading" class="dyform-container" :style="{ background: '#fff' }">
      <div>
        <a-form-model :model="dyform.formData" ref="form" :key="formKey">
          <template v-for="(widget, index) in widgets">
            <component
              :is="widget.wtype"
              :widget="widget"
              :key="widget.id"
              :index="index"
              :widgetsOfParent="widgets"
              v-if="widget.forceRender !== false"
            />
          </template>
          <template v-if="formElementMounted && fieldWidgets && fieldWidgets.length > 0 && renderedFields.length > 0">
            <template v-for="(widget, index) in fieldWidgets">
              <template v-if="!renderedFields.includes(widget.configuration.code)">
                <FormModelItemFieldHidden :widget="widget" />
              </template>
            </template>
          </template>
        </a-form-model>
      </div>
    </div>
  </div>
</template>
<style lang="less">
.widget-dyform {
  // --w-dyform-bg-watermark: none;
  // background: var(--w-dyform-bg-watermark); // 水印
  // document.querySelector('.widget-dyform').style.setProperty('--w-dyform-bg-watermark',`url("data:image/svg+xml,%3Csvg width='200' height='200' xmlns='http://www.w3.org/2000/svg'%3E%3Ctext x='50%25' y='50%25' font-size='14' fill-opacity='0.5' text-anchor='middle' dominant-baseline='middle' transform='rotate(-45, 100 100)'%3E威尔软件%3C/text%3E%3C/svg%3E")`)
  .dyform-loading {
    text-align: center;
    position: absolute;
    top: 40%;
    left: 49%;
  }
}

.widget-dyform-validate-notify {
  .ant-notification-notice-description {
    margin-left: 0px;
  }
}
</style>
<script type="text/babel">
import { createDyform } from '../framework/vue/dyform/dyform';
import { generateId, deepClone, createLocalforage, dropLocalforage, queryString } from '@framework/vue/utils/util';
import { isEmpty, isArray, isFunction, camelCase, set } from 'lodash';
import { debounce, cloneDeep, merge } from 'lodash';
import md5 from '@framework/vue/utils/md5';
import './css/widget-dyform.less';
import FormModelItemFieldHidden from './widget-form-input/form-model-item-field-hidden.vue';

export default {
  name: 'WidgetDyform',
  inject: ['pageContext', 'currentWindow', 'rootWidgetDyformContext', 'locale'],
  props: {
    // 数据UUID
    dataUuid: String,
    // 表单UUID
    formUuid: String,
    // 直接传表单数据
    formDatas: Object,
    // 直接传表单定义
    definitionVjson: Object,
    // 表单元素规则
    formElementRules: Object,
    // 展示状态
    displayState: {
      type: String,
      default: 'edit',
      validator: function (v) {
        return ['edit', 'readonly', 'disable', 'label'].indexOf(v) !== -1;
      }
    },
    // 是否新增表单数据，后端表单数据自动生成的临时表单数据
    isNewFormData: {
      type: Boolean,
      default: false,
      required: false
    },
    isSubform: {
      type: Boolean,
      default: false
    },
    inheritForm: Object,
    dyformStyle: {
      type: Object,
      default: function () {
        return {};
      }
    },
    isVersionDataView: {
      type: Boolean,
      default: false
    },
    broadcastChannel: String // 广播渠道ID
  },
  components: { FormModelItemFieldHidden },
  data() {
    let dyform = this.inheritForm == undefined ? createDyform(this.formUuid) : this.inheritForm;
    dyform.displayState = this.displayState;
    dyform.isNewFormData = this.isNewFormData;
    let widgets = [],
      loading = true,
      formDataFetched = false,
      version = '1.0',
      fieldWidgets = [];
    if (this.definitionVjson && this.definitionVjson.widgets) {
      widgets = this.definitionVjson.widgets;
      fieldWidgets = this.definitionVjson.fields;
      dyform.tableName = this.definitionVjson.tableName;
      loading = false;
      version = this.definitionVjson.version;
    }

    if (this.dataUuid != null && this.formDatas == null) {
      loading = true;
    }

    if (this.dataUuid != null) {
      // 设置数据UUID
      dyform.dataUuid = this.dataUuid;
    }

    if (this.formElementRules != null) {
      dyform.formElementRules = this.formElementRules;
    }

    if (!EASY_ENV_IS_NODE) {
      // 浏览器端绑定vue实例
      dyform.vueInstance = this.$root;
      dyform.version = version;
    }

    return {
      version,
      formKey: this.formUuid || generateId(),
      namespace: this.formUuid || 'DYFORM',
      handleEvents: [],
      loading,
      formDataFetched,
      namedParams: {}, // 运行时参数
      dyform,
      widgets,
      fieldWidgets,
      renderedFields: [],
      formElementMounted: false,
      originFormData: {}, // 原始数据是为了比较变更数据存在的
      refDyforms: [],
      dyformTitleContent: '',
      dyformTitle: ''
    };
  },
  provide() {
    let data = {
      $pageJsInstance: {},
      dyform: this.dyform,
      vPageState: {},
      parentLayContentId: undefined,
      namespace: this.formUuid,
      isNewFormData: this.isNewFormData,
      widgetDyformContext: this,
      refDyforms: this.refDyforms
    };
    if (!this.isSubform) {
      data.rootWidgetDyformContext = this;
      data.$tempStorage = createLocalforage(this.formUuid, '表单级缓存');
      this.dyform.$tempStorage = data.$tempStorage;
    } else {
      this.dyform.$tempStorage = this.rootWidgetDyformContext.$tempStorage;
    }
    return data;
  },
  beforeCreate() {
    this.$developJsInstance = {};
  },
  created() {
    this.executeLifecycleHook('created');
    if (EASY_ENV_IS_BROWSER && this.definitionVjson && this.definitionVjson.jsModule) {
      // 浏览器环境: 加载二开脚本
      this.createPageJsInstance(this.definitionVjson.jsModule);
      this.invokeDevelopmentMethod('created');
    }
    // 初始化传入表单数据情况下，进行设值
    if (this.formDatas != null && EASY_ENV_IS_BROWSER) {
      this.setFormData(this.formDatas, true, true);
    }

    if (this.$store.hasModule(this.namespace)) {
      this._provided.vPageState = this.$store.state[this.namespace];
    }
    this.pageContext.commitRegisterStateIfNotAbsent(this.namespace, {});
    this._provided.vPageState = this.$store.state[this.namespace];

    // 定义数据、表单数据获取
    if (EASY_ENV_IS_BROWSER) {
      this._broadcastChannel = undefined;
      if (this.dataUuid == undefined || this.isNewFormData) {
        if (this.broadcastChannel) {
          this._broadcastChannel = new BroadcastChannel(this.broadcastChannel);
        } else if (this.rootWidgetDyformContext == undefined && location.search) {
          let urlQueryParams = queryString(decodeURIComponent(location.search.substring(1)));
          let __bc = urlQueryParams.__bc || (urlQueryParams.eventParams != undefined ? urlQueryParams.eventParams.__bc : null);
          if (__bc) {
            this._broadcastChannel = new BroadcastChannel(__bc);
          }
        }
      }

      if (this._broadcastChannel) {
        this._copyFormDataFlag = true;
        this._broadcastChannel.onmessage = event => {
          if (event.data.message === 'setFormData') {
            this.$loading(false);
            this.setFormData(event.data.formDatas, false, false);
          } else if (event.data.message == 'loading') {
            this.$loading('数据加载中');
          }
        };
      }
      this.fetchFormDefaultValueData().then(fd => {
        if (this._broadcastChannel) {
          this._broadcastChannel.postMessage({
            source: 'dyform',
            message: 'setFormDefaultValueDone',
            formUuid: this.formUuid,
            dataUuid: this.dataUuid,
            isNewFormData: this.isNewFormData,
            defaultFormData: fd
          });
        }
      });
      if (this.definitionVjson) {
        this.$nextTick(() => {
          this.pageContext.emitEvent('dyformParentAndwidgets', {
            parent: this.$parent,
            widgets: this.widgets
          });
        });
        this.dyformTitleContent = this.definitionVjson.titleContent;
        this.prepareRefDyform(this.definitionVjson.refDyform);
        this.fetchFormData();
      } else if (this.formUuid != undefined) {
        this.fetchingDefinition = true;
        this.fetchDyformDefinition(this.formUuid).then(def => {
          let vjson = JSON.parse(def.definitionVjson) || { widgets: [] };
          this.dyform.tableName = def.tableName;
          this.dyform.version = def.version;
          this.version = def.version;
          this.dyformTitleContent = vjson.titleContent;
          this.widgets = vjson.widgets;
          this.fieldWidgets = vjson.fields;

          if (vjson.jsModule) {
            this.createPageJsInstance(vjson.jsModule);
          }
          this.loading = false;

          this.fetchingDefinition = false;
          this.prepareRefDyform(vjson.refDyform);

          this.setI18nMessage(def.i18ns);
          this.$nextTick(() => {
            this.pageContext.emitEvent('dyformParentAndwidgets', {
              parent: this.$parent,
              widgets: this.widgets
            });
          });
          this.fetchFormData();
        });
      }
    }
  },
  beforeMount() {
    this.executeLifecycleHook('beforeMount');
    this.invokeDevelopmentMethod('beforeMount');
    this.$emit('beforeMount', this);
    let _this = this;
    this.dyform.handleEvent('form-element:mounted', function (el) {
      let widget = el.widget;
      if (widget.configuration.isDatabaseField) {
        _this.renderedFields.push(widget.configuration.code);
      }
    });
  },
  mounted() {
    // if (!this.definitionVjson) {
    //   // 查询表单定义
    //   this.fetchFormDefinition();
    // }
    // this.fetchFormDefaultValueData();
    // this.fetchFormData();
    this.dyform.$form = this.$refs.form;
    let _this = this;
    this.pageContext.handleEvent(`WidgetDyform:originFormDataChange`, function (formUuid, formDatas) {
      _this.originFormData[formUuid] = JSON.parse(JSON.stringify(formDatas));
      if (Array.isArray(formDatas)) {
        for (let i = 0, len = formDatas.length; i < len; i++) {
          _this.originFormData[formDatas[i].uuid] = JSON.parse(JSON.stringify(formDatas[i]));
        }
      }
    });
    this.dyform.handleEvent(`WidgetDyform:${this.namespace}:subformDataChange`, function (data) {
      _this.invokeDevelopmentMethod(`afterSubformDataChanged`, data);
    });
    if (this.definitionVjson) {
      this.loading = false;

      this.executeLifecycleHook('mounted');
      this.$emit('mounted', this);
      this.invokeDevelopmentMethod('mounted');
      this.formElementMounted = true;
    }
    this.pageContext.emitCrossTabEvent(`dyform:mounted`, { dataUuid: this.dataUuid, formUuid: this.formUuid, userId: this._$USER.userId });
  },
  methods: {
    createPageJsInstance(jsModule) {
      // 页面二开脚本实例
      if (jsModule && !EASY_ENV_IS_NODE && this.__developScript[jsModule]) {
        // 服务端不需要执行二开脚本
        this.$pageJsInstance = new this.__developScript[jsModule].default(this);
        this._provided.$pageJsInstance = this.$pageJsInstance;
        this._provided.$pageJsInstance._JS_META_ = jsModule;
        this.$developJsInstance[jsModule] = this.$pageJsInstance;
      }
    },
    setFormDisplayState(v) {
      if (v != undefined) {
        if (['edit', 'readonly', 'disable', 'label'].indexOf(v) !== -1) {
          this.dyform.displayState = v;
          this.formKey = generateId();
        } else {
          throw new Error('不支持的状态');
        }
      }
    },
    commitDyformState(state) {
      this.$store.commit(`${this.namespace}/set`, state);
    },
    clearValidate() {
      this.dyform.clearValidate();
      this.$notification.close('notifyFormValidate');
    },
    validateFormData(callback) {
      let _this = this;
      this.dyform.validate((vali, msg) => {
        let subformValidatePromise = [],
          suid = [];
        if (_this.dyform.$subform) {
          for (let k in _this.dyform.$subform) {
            let sf = _this.dyform.$subform[k];
            if (!suid.includes(sf._uid)) {
              subformValidatePromise.push(sf.validateSubform());
              suid.push(sf._uid);
            }
          }
        }
        let _continue = () => {
          if (!vali) {
            _this.notifyValidateResult(msg);
          } else {
            _this.$notification.close('notifyFormValidate');
          }
          if (typeof callback == 'function') {
            callback(vali, msg);
          }
        };
        if (subformValidatePromise.length) {
          Promise.all(subformValidatePromise)
            .then(res => {
              if (res) {
                res.forEach(r => {
                  if (r && r.validate === false) {
                    Object.assign(msg, r.msg);
                    vali = false;
                  }
                });
              }
              _continue();
            })
            .catch(err => {
              console.log(err);
            });
        } else {
          _continue();
        }
      });
    },
    notifyValidateResult(result) {
      if (Object.keys(result).length) {
        this.$notification.error({
          message: this.$t('WidgetDyform.dyformValidate', '表单数据校验'),
          class: 'widget-dyform-validate-notify',
          key: 'notifyFormValidate',
          top: '50px',
          description: h => {
            const msg = [];
            for (let key in result) {
              let setMsg = dyform => {
                let $field = dyform.$fieldset[key];
                if (key.startsWith('__REF_FORM_DATA__.')) {
                  let parts = key.split('.');
                  $field = dyform.refDyform[parts[1]].$fieldset[parts[2]];
                }
                // 判断是否在从表内
                if ($field == undefined) {
                  for (let formUuid in dyform.subform) {
                    let rows = dyform.subform[formUuid];
                    if (rows.length > 0) {
                      $field = rows[0].$fieldset[key];
                      if ($field) {
                        break;
                      } else if (rows[0].subform && !isEmpty(rows[0].subform)) {
                        // 从表使用原表单时，存在从表内有从表的情况
                        let hasDataSubform = {};
                        rows.forEach(row => {
                          for (let subUuid in row.subform) {
                            // 从表的从表存在数据，取第一个用于字段匹配
                            if (row.subform[subUuid].length > 0 && !hasDataSubform[subUuid]) {
                              hasDataSubform[subUuid] = row.subform[subUuid][0];
                              setMsg(row.subform[subUuid][0]);
                            }
                          }
                        });
                      }
                    }
                  }
                }
                let _msg = [];
                let fieldName;
                if ($field) {
                  for (let i = 0, len = result[key].length; i < len; i++) {
                    _msg.push(result[key][i].message);
                  }
                  fieldName = $field.widget.configuration.name;
                  let fieldNameI18n = this.$t('Widget.' + $field.widget.id + '.' + $field.widget.id, null);
                  if (!fieldNameI18n) {
                    let locale = this.$i18n.locale;
                    if ($field.$parent.widget && $field.$parent.widget.configuration.i18n) {
                      let i18n = Object.keys($field.$parent.widget.configuration.i18n[locale] || {});
                      fieldNameI18n = this.$t('Widget.' + i18n[0]);
                    }
                  }
                  if (fieldNameI18n) {
                    fieldName = fieldNameI18n;
                  }
                }
                if (_msg.length) {
                  msg.push({
                    id: $field.widget.id,
                    name: fieldName,
                    code: key,
                    msg: _msg
                  });
                }
              };
              setMsg(this.dyform);
            }
            const focusWidget = item => {
              // console.log('click', item);
              // TODO: 定位到具体组件位置
            };
            return (
              <div>
                {msg.map(item => (
                  <a-row type="flex" gutter="8">
                    <a-col onClick={e => focusWidget(item)}>{item.name}:</a-col>
                    <a-col style="text-align:left">{item.msg.join(';')}</a-col>
                  </a-row>
                ))}
              </div>
            );
          }
        });
      }
    },
    collectFormData(validate, callback, newVersion) {
      var collect = function (_callback) {
        let data = {
          formUuid: this.formUuid,
          dataUuid: newVersion ? undefined : this.dataUuid,
          dyFormData: {
            formUuid: this.formUuid,
            formDatas: {},
            deletedFormDatas: {},
            addedFormDatas: {},
            updatedFormDatas: {},
            dyformDataOptions: this.dyform.dyformDataOptions || {}
          }
        };
        let formData = cloneDeep(this.dyform.formData);
        if (formData.__REF_FORM_DATA__ != undefined) {
          delete formData.__REF_FORM_DATA__;
        }
        if (newVersion || (this.dataUuid == null && !this.isSubform)) {
          formData.uuid = generateId('SF');
          if (newVersion) {
            data.dyFormData.newOldUuidMap = {
              [this.dataUuid]: formData.uuid
            };
          }
          data.dataUuid = formData.uuid;
          data.dyFormData.addedFormDatas[this.formUuid] = [formData.uuid];
        } else if (this.isNewFormData) {
          // 后端新增的表单数据
          data.dyFormData.addedFormDatas[this.formUuid] = [formData.uuid];
        }
        //主表数据
        data.dyFormData.formDatas[this.formUuid] = [formData];
        this.compareOriginFormData(data.dyFormData.updatedFormDatas, formData, this.formUuid, newVersion ? undefined : this.dataUuid);

        // 从表数据
        if (this.dyform.subform) {
          let dyFormData = data.dyFormData;
          if (this.isSubform) {
            // 从表嵌套从表的情况下
            dyFormData = {
              formUuid: this.formUuid,
              formDatas: {},
              deletedFormDatas: {},
              addedFormDatas: {},
              updatedFormDatas: {}
            };
          }

          this.collectSubFormData(dyFormData, this.dyform, newVersion, data.dyFormData);

          if (this.isSubform) {
            // 从表嵌套从表的情况下
            formData.nestformDatas = JSON.stringify(dyFormData);
          }
        }

        // 引用表数据
        if (this.dyform.refDyform && Object.keys(this.dyform.refDyform).length) {
          for (let key in this.dyform.refDyform) {
            let refDyform = this.dyform.refDyform[key];
            this.compareOriginFormData(data.dyFormData.updatedFormDatas, refDyform.formData, refDyform.formUuid, refDyform.formData.uuid);
            if (
              data.dyFormData.updatedFormDatas[refDyform.formUuid] &&
              data.dyFormData.updatedFormDatas[refDyform.formUuid][refDyform.formData.uuid].length
            ) {
              data.dyFormData.formDatas[refDyform.formUuid] = [refDyform.formData];
            }
          }
        }

        // 其他表数据
        if (this.dyform.relaWidgetDyform && Object.keys(this.dyform.relaWidgetDyform).length) {
          for (let k in this.dyform.relaWidgetDyform) {
            let relaWidgetDyform = this.dyform.relaWidgetDyform[k];
            let relaFormData = relaWidgetDyform.collectFormData(false);
            if (relaFormData != undefined) {
              delete relaFormData.dyFormData.formUuid;
              data.dyFormData = merge(data.dyFormData, relaFormData.dyFormData);
            }
            if (validate) {
              // relaWidgetDyform.validateFormData();
            }
          }
        }
        console.log('收集表单数据: ', data);
        this.dyform.emitEvent(`${this.formUuid}:afterCollectFormData`, data);
        return data;
      };
      if (validate) {
        // 校验
        let _this = this;
        this.validateFormData(function (vali, msg) {
          callback(vali, msg, collect.call(_this));
        });
        return;
      }
      if (typeof callback == 'function') {
        callback(undefined, undefined, collect.call(this));
        return;
      }
      return collect.call(this);
    },
    compareOriginFormData(updatedFormDatas, newFormData, formUuid, dataUuid) {
      if (!isEmpty(this.originFormData) && dataUuid) {
        let originData = this.originFormData[dataUuid];
        if (!originData) {
          return;
        }
        if (updatedFormDatas[formUuid] === undefined) {
          updatedFormDatas[formUuid] = {};
        }
        if (updatedFormDatas[formUuid][dataUuid] === undefined) {
          updatedFormDatas[formUuid][dataUuid] = [];
        }
        let newKeys = Object.keys(newFormData);
        for (let key in originData) {
          if (originData[key] != newFormData[key]) {
            if (isArray(originData[key]) || isArray(newFormData[key])) {
              if (this.compareFileFieldValue(originData[key], newFormData[key])) {
                updatedFormDatas[formUuid][dataUuid].push(key);
              }
            } else {
              // 值为null的字段控件初始化后变为''，不记录为更新字段
              if (!(originData[key] == null && newFormData[key] == '')) {
                updatedFormDatas[formUuid][dataUuid].push(key);
              }
            }
          }
          if (newKeys.includes(key)) {
            newKeys.splice(newKeys.indexOf(key), 1);
          }
        }
        if (newKeys.length) {
          // 定义中的显示值字段已删除
          if (this.formUuid == formUuid) {
            newKeys.forEach(key => {
              if (!this.dyform.$fieldset[key]) {
                newKeys.splice(newKeys.indexOf(key), 1);
              }
            });
          } else {
            let $subform = this.dyform.$subform[formUuid];
            if ($subform) {
              let columns = $subform.widget.configuration.columns || [];
              newKeys.forEach(key => {
                let column = columns.find(col => col.configuration && col.configuration.code === key);
                if (!column) {
                  newKeys.splice(newKeys.indexOf(key), 1);
                }
              });
            }
          }
          if (newKeys.length) {
            updatedFormDatas[formUuid][dataUuid].push(...newKeys);
          }
        }
      }
    },
    compareFileFieldValue(oldFiles = [], newFiles = []) {
      let oldFileIds = (oldFiles && oldFiles.map(file => file.fileID)) || [];
      let newFileIds = (newFiles && newFiles.map(file => file.fileID)) || [];
      if (oldFileIds.length != newFileIds.length) {
        return true;
      }
      oldFileIds.forEach(oldFileId => {
        let index = newFileIds.indexOf(oldFileId);
        if (index != -1) {
          let oldFile = oldFiles.find(file => file.fileID == oldFileId);
          let newFile = newFiles.find(file => file.fileID == oldFileId);
          if (oldFile.fileName == newFile.fileName) {
            newFileIds.splice(index, 1);
          }
        }
      });
      return newFileIds.length != 0;
    },
    collectSubFormData(dyFormData, dyform, newVersion, rootDyFormData) {
      if (dyform.subform) {
        if (dyform.deletedSubformData) {
          Object.assign(dyFormData.deletedFormDatas, dyform.deletedSubformData);
        }

        for (let uuid in dyform.subform) {
          let dyforms = dyform.subform[uuid];
          dyFormData.formDatas[uuid] = [];
          dyFormData.addedFormDatas[uuid] = [];
          for (let i = 0, len = dyforms.length; i < len; i++) {
            let formData = deepClone(dyforms[i].formData);
            formData.sort_order = `${i + 1}`; // 序号
            // 前端或后端添加的从表临时数据
            if (!formData.uuid || this.isNewFormData || newVersion) {
              // 新增的数据
              let uid = newVersion ? generateId('SF') : formData.uuid || formData.__uuid__ || generateId('SF');
              if (newVersion && formData.uuid) {
                rootDyFormData.newOldUuidMap[formData.uuid] = uid;
              }
              formData.uuid = uid;
              dyforms[i].formData.__uuid__ = uid; // 避免重复生成uuid
              dyFormData.addedFormDatas[uuid].push(formData.uuid);
            } else {
              this.compareOriginFormData(dyFormData.updatedFormDatas, formData, uuid, formData.uuid);
            }
            dyFormData.formDatas[uuid].push(formData);

            let _dyformData = {
              formUuid: uuid,
              formDatas: {},
              deletedFormDatas: {},
              addedFormDatas: {},
              updatedFormDatas: {}
            };

            this.collectSubFormData(_dyformData, dyforms[i], newVersion, rootDyFormData);
            formData.nestformDatas = JSON.stringify(_dyformData);
          }
        }
      }
    },

    // 判断表单数据是否变更
    isDyformDataChanged(formData) {
      const _this = this;
      let formDataChanged = false;
      let data = formData || _this.collectFormData();
      let deletedFormDatas = data.dyFormData.deletedFormDatas;
      let addedFormDatas = data.dyFormData.addedFormDatas;
      let updatedFormDatas = data.dyFormData.updatedFormDatas;
      for (let formUuid in deletedFormDatas) {
        let deletedDatas = deletedFormDatas[formUuid];
        for (let index in deletedDatas) {
          // 初始化数据是否存在对应的数据uuid
          if (_this._isExistsOriginData(deletedDatas[index])) {
            formDataChanged = true;
            break;
          }
        }
      }

      if (!formDataChanged) {
        for (let formUuid in addedFormDatas) {
          if (!isEmpty(addedFormDatas[formUuid])) {
            formDataChanged = true;
            break;
          }
        }
      }

      if (!formDataChanged) {
        for (let formUuid in updatedFormDatas) {
          let updatedDatas = updatedFormDatas[formUuid];
          for (let dataUuid in updatedDatas) {
            if (!isEmpty(updatedDatas[dataUuid])) {
              formDataChanged = true;
              break;
            }
          }
        }
      }

      return formDataChanged;
    },

    // 初始化数据是否存在对应的数据uuid
    _isExistsOriginData(dataUuid) {
      const _this = this;
      let originFormData = _this.originFormData;
      for (let key in originFormData) {
        if (key == dataUuid) {
          return true;
        }
        let data = originFormData[key];
        // 从表数据
        if (isArray(data)) {
          for (let index in data) {
            if (data[index].uuid == dataUuid) {
              return true;
            }
          }
        }
      }
      return false;
    },

    invokeDevelopmentMethod() {
      for (let k in this.$developJsInstance) {
        let method = arguments[0],
          args = [];
        if (arguments.length > 1) {
          for (let i = 1, len = arguments.length; i < len; i++) {
            args.push(arguments[i]);
          }
        }
        if (typeof this.$developJsInstance[k][method] === 'function') {
          try {
            this.$developJsInstance[k][method].apply(this.$developJsInstance[k], args);
          } catch (error) {
            console.error('调用二开脚本方法失败：', error);
          }
        }
      }
    },
    validate() {},
    save() {},
    clearFormData(formUuid) {
      if (formUuid == undefined) {
        // 清空所有数据
        for (let key in this.dyform.formData) {
          if (['uuid', 'creator', 'create_time', 'modifier', 'modify_time'].includes(key)) {
            continue;
          }
          this.dyform.setFieldValue(key, null, false);
        }

        if (this.dyform.subform) {
          for (let key in this.dyform.subform) {
            let sf = this.dyform.$subform[key];
            if (sf) {
              sf.clearSubform();
            }
          }
        }
      } else {
        if (this.formUuid == formUuid) {
          // 仅清空主表
          for (let key in this.dyform.formData) {
            if (this.dyform.$fieldset[key]) {
              if (['uuid', 'creator', 'create_time', 'modifier', 'modify_time'].includes(key)) {
                continue;
              }
              this.dyform.setFieldValue(key, null, false);
            }
          }
        } else {
          // 清空从表
          if (this.dyform.subform) {
            let sf = this.dyform.$subform[formUuid];
            if (sf) {
              sf.clearSubform();
            }
          }
        }
      }
    },
    setFormData(formDatas, setOriginalData = true, handleSubformMounted = false) {
      for (let key in formDatas) {
        if (key === this.formUuid) {
          // 主表数据
          if (setOriginalData) {
            this.originFormData[formDatas[key][0].uuid] = JSON.parse(JSON.stringify(formDatas[key][0]));
          }
          let data = formDatas[key][0];
          const dataTemp = deepClone(data);
          for (const key in dataTemp) {
            // this.dyform.formData[key] = dataTemp[key];
            this.$set(this.dyform.formData, key, dataTemp[key]);
          }
          for (let k in data) {
            if (k.toLowerCase() === 'uuid') {
              // 设置数据UUID
              this.dataUuid = data[k];
            }
            if (data[k] != null) {
              this.dyform.setFieldValue(k, data[k], false);
            }
          }
        } else {
          let rowData = JSON.parse(JSON.stringify(formDatas[key]));
          if (setOriginalData) {
            this.originFormData[key] = rowData; // 从表数据
          }

          // 初始化传入表单数据情况下，需等待从表加载完成
          if (handleSubformMounted) {
            (() => {
              let subformMounted = false;
              this.dyform.handleEvent(`WidgetSubform:${key}:mounted`, () => {
                subformMounted = true;
                this.$emit(`WidgetSubform:${key}:addRow`, rowData, true);
              });
              setTimeout(() => {
                if (!subformMounted) {
                  this.$emit(`WidgetSubform:${key}:addRow`, rowData, true);
                }
              }, 500);
            })();
          } else {
            this.$nextTick(() => {
              // this.dyform.emitEvent(`WidgetSubform:${key}:addRow`, rowData);
              this.$emit(`WidgetSubform:${key}:addRow`, rowData, true);
            });
          }
          if (setOriginalData) {
            for (let i = 0, len = formDatas[key].length; i < len; i++) {
              this.originFormData[formDatas[key][i].uuid] = JSON.parse(JSON.stringify(formDatas[key][i]));
            }
          }
        }
      }
      this.loading = false;

      this.formDataFetched = true;
      this.$emit('formDataChanged', true);
    },
    fetchFormData(dataUuid) {
      let _this = this;
      if ((dataUuid != undefined || this.dataUuid != null) && this.formDatas == null) {
        $axios
          .post('/json/data/services', {
            serviceName: 'dyFormFacade',
            methodName: this.isVersionDataView ? 'getVersionFormData' : 'getFormData',
            args: JSON.stringify([this.formUuid, dataUuid || this.dataUuid])
          })
          .then(({ data }) => {
            console.log(`表单数据[${dataUuid || _this.dataUuid}] : `, data);
            // _this.loading = false;
            if (data.code == 0 && data.data) {
              _this.originFormData = {}; // 原始数据
              _this.setFormData(data.data);
              _this.getDyformTitle({
                formUuid: _this.formUuid,
                formDatas: data.data
              });
            }
          });
      } else {
        _this.getDyformTitle({
          formUuid: _this.formUuid
        });
      }
    },
    fetchFormDefaultValueData() {
      let _this = this;
      return new Promise((resolve, reject) => {
        if ((this.dataUuid == null || this.isNewFormData) && this.formUuid) {
          this.$tempStorage.getCache(
            this.formUuid + '_DefaultFormData',
            () => {
              return new Promise((resolve, reject) => {
                $axios.post(`/proxy/api/dyform/data/getDefaultFormData?formUuid=${this.formUuid}`, {}).then(({ data, headers }) => {
                  resolve(data.code == 0 && data.data && Object.keys(data.data).length ? data.data : {});
                });
              });
            },
            data => {
              if (_this.dataUuid) {
                data.uuid = _this.dataUuid;
              }
              _this.setFormData({
                [_this.formUuid]: [data]
              });
              _this.defaultFormData = data;
              resolve(data);
            }
          );
        } else {
          resolve({});
        }
      });
    },

    fetchDyformDefinition(formUuid, formId) {
      return new Promise((resolve, reject) => {
        this.$tempStorage.getItem(formUuid || formId).then(cacheData => {
          console.log(`${formUuid || formId} -> 获取缓存数据`, cacheData);
          if (cacheData == null) {
            $axios
              .post(
                `/proxy/api/dyform/definition/${
                  formUuid ? 'getFormDefinitionByUuid?formUuid=' + formUuid : 'getFormDefinitionById?id=' + formId
                }&i18n=true`,
                {}
              )
              .then(({ data }) => {
                try {
                  this.$tempStorage.setItem(formUuid || formId, data);
                } catch (error) {
                  console.log(error);
                }
                resolve(data);
              })
              .catch(error => {});
          } else {
            resolve(cacheData);
          }
        });
      });
    },

    saveAfterValidate() {},

    // 打印表单
    print(printOptions = {}) {
      const _this = this;
      _this.$loading(true);
      import('modern-screenshot/dist/index.js').then(({ domToForeignObjectSvg }) => {
        domToForeignObjectSvg(this.$el, {
          onCloneNode: node => {
            let textareas = node.querySelectorAll('textarea');
            textareas &&
              textareas.forEach(textarea => {
                if (!textarea.innerText && textarea.value) {
                  textarea.innerText = textarea.value;
                }
              });
            // domToForeignObjectSvg时，使用window打印时，附件切换按钮会出现样式问题，需要调整样式
            let radioBtns = node.querySelectorAll('.view-change-btn>.ant-radio-button-wrapper');
            radioBtns && radioBtns.forEach(radioBtn => (radioBtn.style.paddingTop = '6px'));
            // domToForeignObjectSvg时，页签打印空白问题
            let innerBodies = node.querySelectorAll('body');
            innerBodies &&
              innerBodies.forEach(innerBody => {
                innerBody.removeAttribute('style');
              });
          }
        }).then(foreignObjectSvg => {
          // const link = document.createElement('a');
          // link.download = `${document.title}.png`;
          // link.href = dataUrl;
          // link.click();
          let wrapper = document.createElement('div');
          wrapper.style.display = 'none';
          wrapper.appendChild(foreignObjectSvg);
          _this.$loading(false);
          _this.$el.parentNode.appendChild(wrapper);
          require.ensure(
            [],
            function () {
              const PrintArea = require('../../public/js/print-area/print-area.js');
              let printArea = new PrintArea(foreignObjectSvg, printOptions);
              printArea.print();
              setTimeout(() => {
                wrapper.remove();
              }, 1000);
            },
            'print-area'
          );
        });
      });
    },
    afterFormDataChanged: debounce(function (v) {
      this.$emit('formDataChanged', true);
      this.dyform.emitEvent('formDataChanged', { wDyform: this });
      this.invokeDevelopmentMethod('afterFormDataChanged', v);
    }, 500),
    setI18nMessage(i18ns) {
      let widgetI18ns = undefined;
      if (i18ns && i18ns.length) {
        widgetI18ns = { [this.$i18n.locale]: { Widget: {} } };
        for (let item of i18ns) {
          set(widgetI18ns[this.$i18n.locale].Widget, item.code, item.content);
        }
        this.$i18n.mergeLocaleMessage(this.$i18n.locale, widgetI18ns[this.$i18n.locale]);
      }
    },

    addNamedParams(params) {
      Object.assign(this.namedParams, params);
    },
    clearNamedParams() {
      this.namedParams = {};
    },
    dyformDataOptions: function (_this) {
      var self = this;
      _this.dyform.dyformDataOptions = _this.dyform.dyformDataOptions || {};
      return {
        getOptions: function (key) {
          return _this.dyformDataOptions[key];
        },
        putOptions: function (key, value) {
          if (null == value) {
            delete _this.dyform.dyformDataOptions[key];
          } else {
            _this.dyform.dyformDataOptions[key] = value;
          }
        }
      };
    },
    handleException(exceptionData, options) {
      var subcode = exceptionData.subcode;
      if (subcode === 'SerialNumberOccupy') {
        var dyformDataOptions = (options.dyformDataOptions && options.dyformDataOptions()) || this.dyformDataOptions(this);
        this.$confirm({
          content: exceptionData.title,
          okText: (this.locale && this.locale.Modal && this.locale.Modal.okText) || this.$t('WidgetFormFileUpload.message.ok', '确定'),
          cancelText:
            (this.locale && this.locale.Modal && this.locale.Modal.cancelText) || this.$t('WidgetFormFileUpload.message.cancel', '取消'),
          onOk() {
            dyformDataOptions.putOptions('serialNumberConfirmed-' + exceptionData.fieldName, true);
            isFunction(options.callback) && options.callback.call();
          }
        });
      } else {
        console.error('表单数据保存失败: ', exceptionData);
        try {
          if (exceptionData.msg) {
            this.$message.error(exceptionData.msg);
          }
        } catch (error) {
          this.$message.error('表单数据保存失败！');
        }
      }
    },
    /**
     * 处理异常
     */
    handleError(exceptionData, options) {
      let self = this;

      function errorFun(message) {
        this.$message.error(message || '表单数据保存失败！');
      }

      try {
        let msg = JSON.parse(exceptionData);
        if (msg.errorCode == 'SQLGRAM') {
          this.$message.error('保存时,后台语法错误!!!有可能是人为去修改了表单后台数据库表字段,更详细的信息如下:\n' + msg.msg);
        } else if (msg.errorCode == 'DATA_OUT_OF_DATE') {
          this.$message.error('请重新加载并修改数据:\n' + msg.msg);
        } else if (msg.errorCode == 'SaveData') {
          self.handleException(msg.data, options);
        } else {
          if (msg.msg) {
            errorFun(msg.msg);
          } else {
            errorFun();
          }
        }
      } catch (e) {
        errorFun();
      }
    },
    createRefDyformHolder(id, config) {
      if (this.refDyformHolder == undefined) {
        this.refDyformHolder = {};
      }
      let _this = this;
      class Holder {
        constructor(id, config) {
          this.id = id;
          this.config = config;

          /**
           * 引用表单数据会在主表单数据域下以嵌套形式存在 :
           * { ... 主表单属性值 , __REF_FORM_DATA__ : { 引用表单ID : { 引用表单属性值 } } }
           * 这么处理是为了能够通过表单域校验，避免与主表单属性名冲突的情况
           */
          if (_this.dyform.formData.__REF_FORM_DATA__ == undefined) {
            _this.$set(_this.dyform.formData, '__REF_FORM_DATA__', {});
          }
          _this.$set(_this.dyform.formData.__REF_FORM_DATA__, this.id, {});
          this.form = _this.dyform.createRefDyForm(this.id, undefined);
          this.form.namespace = 'ref-dyform-' + this.id;
          this.form.formData = _this.dyform.formData.__REF_FORM_DATA__[this.id];
          this.loadRefDyformDefinition().then(() => {
            _this.refDyforms.push(this.form);
            this.dependencyWatch();
            if (_this.formDatas || this.dependencyCodes.length == Object.keys(this.dependencyData).length) {
              this.setFormDatas();
            }
          });
        }
        setFormDatas() {
          return new Promise((resolve, reject) => {
            let condition = this.config.condition,
              params = {};
            if (condition != undefined) {
              for (let i = 0, len = condition.length; i < len; i++) {
                if (condition[i].valueType == 'prop' && condition[i].value && condition[i].value.startsWith(':MAIN_FORM_DATA_')) {
                  let code = condition[i].value.split(':MAIN_FORM_DATA_')[1];
                  if (this.dependencyData[code] != undefined && this.dependencyData[code] != '') {
                    params[condition[i].value.substring(1)] = this.dependencyData[code];
                  }
                }
              }
            }
            // 存在通过条件查询关联表单字段数据的，则发起后端条件查询
            if (
              this.dependencyCodes.length == 0 ||
              (Object.keys(params).length == this.dependencyCodes.length && this.dependencyCodes.length > 0)
            ) {
              if (JSON.stringify(this.tempNamedParams) != JSON.stringify(params)) {
                this.tempNamedParams = params;
                this.getFormDataByWhere(condition, params).then(list => {
                  if (list && list.length == 1) {
                    _this.originFormData[list[0].uuid] = JSON.parse(JSON.stringify(list[0]));
                    for (const key in list[0]) {
                      _this.$set(this.form.formData, key, list[0][key]);
                      this.form.setFieldValue(key, list[0][key], false);
                    }
                  } else {
                    for (let key in this.form.formData) {
                      _this.$set(this.form.formData, key, undefined);
                      this.form.setFieldValue(key, undefined, false);
                    }
                  }
                  resolve();
                });
              }
            } else if (this.dependencyCodes.length > 0) {
              this.tempNamedParams = {};
              for (let key in this.form.formData) {
                this.form.setFieldValue(key, undefined, false);
              }
              resolve();
            }
          });
        }

        getFormDataByWhere(where, namedParams = {}) {
          return new Promise((resolve, reject) => {
            $axios
              .post(`/proxy/api/dyform/data/getFormDataByWhere/${this.id}`, {
                where,
                namedParams: { ..._this.namedParams, ...namedParams }
              })
              .then(({ data }) => {
                resolve(data.data);
              })
              .catch(error => {});
          });
        }
        loadRefDyformDefinition() {
          return new Promise((resolve, reject) => {
            _this.fetchDyformDefinition(undefined, this.id).then(def => {
              this.form.formId = this.id;
              this.form.displayState = this.config.displayState;
              this.form.formUuid = def.uuid;
              this.form.formDefinitionJson = JSON.parse(def.definitionVjson);
              this.form.version = this.form.formDefinitionJson.version || '1.0';
              _this.setI18nMessage(def.i18ns);
              resolve();
            });
          });
        }
        dependencyWatch() {
          let condition = this.config.condition;
          this.dependencyData = {};
          if (condition != undefined) {
            this.dependencyCodes = [];
            for (let i = 0, len = condition.length; i < len; i++) {
              if (condition[i].valueType == 'prop' && condition[i].value && condition[i].value.startsWith(':MAIN_FORM_DATA_')) {
                let code = condition[i].value.split(':MAIN_FORM_DATA_')[1];
                if (!_this.dyform.formData.hasOwnProperty(code)) {
                  _this.$set(_this.dyform.formData, code, undefined);
                } else if (
                  _this.dyform.formData[code] != undefined &&
                  _this.dyform.formData[code] != null &&
                  _this.dyform.formData[code] != ''
                ) {
                  this.dependencyData[code] = _this.dyform.formData[code];
                }
                if (!this.dependencyCodes.includes(code)) {
                  this.dependencyCodes.push(code);
                }
              }
            }
            _this.$watch(
              () => {
                let props = [];
                for (let c of this.dependencyCodes) {
                  props.push(_this.dyform.formData[c]);
                }
                return props;
              },
              (newValues, oldValues) => {
                for (let i = 0, len = newValues.length; i < len; i++) {
                  this.dependencyData[this.dependencyCodes[i]] = newValues[i];
                }
                this.setFormDatas();
              },
              { deep: true }
            );
          }
        }
      }
      this.refDyformHolder[id] = new Holder(id, config);
    },
    prepareRefDyform(refDyform) {
      if (refDyform) {
        this.refDyformConfig = refDyform;
        for (let id in refDyform) {
          if (Object.keys(refDyform[id].field).length) {
            this.createRefDyformHolder(id, refDyform[id]);
          }
        }
      }
    },

    // 打包下载所有的附件字段文件，包括从表
    downloadAllAttachmentFieldFiles() {},

    clearDyformCache() {
      return dropLocalforage(this.formUuid);
    },
    executeLifecycleHook(key, params) {
      if (!this.designMode && this.definitionVjson && this.definitionVjson.lifecycleHook && this.definitionVjson.lifecycleHook[key]) {
        this.executeCodeSegment(this.definitionVjson.lifecycleHook[key], params);
      }
    },
    /**
     * 执行js代码片段
     * @param {*} script 代码
     * @param {*} params 执行代码函数块的入参
     * @param {*} $this  执行代码的this指向
     * @param {*} callback 执行代码的回调
     * @returns
     */
    executeCodeSegment(script, params, $this, callback) {
      var paramNames = []; //参数键
      var paramValues = []; //参数值
      var _this = $this ? $this : this;
      if (params) {
        for (var k in params) {
          paramNames.push(k);
          paramValues.push(params[k]);
        }
      }
      var anonymousFunction = new Function(paramNames.join(','), script);
      var rt = anonymousFunction.apply(_this, paramValues);
      if (typeof callback === 'function') {
        //处理执行结果
        callback(rt);
      }
      return anonymousFunction;
    },
    getDyformTitle(formData) {
      if (this.dyformTitleContent) {
        let titleExpression = encodeURIComponent(this.dyformTitleContent);
        $axios.post(`/proxy/api/dyform/data/getDyformTitle?titleExpression=${titleExpression}`, formData).then(({ data }) => {
          this.dyformTitle = data.data;
          this.$emit('dyformTitleChanged', this.dyformTitle);
        });
      }
    }
  },

  destroyed() {},
  beforeDestroy() {
    if (!this.isSubform) {
      dropLocalforage(this.formUuid);
    }
    for (let i = 0, len = this.handleEvents.length; i < len; i++) {
      this.dyform.offEvent(this.handleEvents[i]);
    }
  },
  updated() {
    if (this.fetchingDefinition === false) {
      this.loading = false;

      delete this.fetchingDefinition;
      this.$nextTick(() => {
        this.executeLifecycleHook('mounted');
        this.$emit('mounted');
        this.invokeDevelopmentMethod('mounted');
        this.formElementMounted = true;
      });
    }
  },
  watch: {
    'dyform.formData': {
      deep: true,
      handler(v, o) {
        this.afterFormDataChanged(v);
      }
    },
    displayState: function (newVal) {
      this.setFormDisplayState(newVal);
    }
  }
};
</script>
