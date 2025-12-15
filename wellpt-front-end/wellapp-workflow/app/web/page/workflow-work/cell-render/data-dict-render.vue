<template>
  <div>{{ label.join(';') }}</div>
</template>
<style lang="less"></style>
<script type="text/babel">
import cellRenderMixin from './cellRenderMixin';
import { isArray } from 'lodash';
export default {
  name: 'DataDictRender',
  mixins: [cellRenderMixin],
  title: '数据字典渲染器',
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      label: [],
      value: []
    };
  },
  beforeCreate() {},
  created() {
    if (this.slotOption.options.dataDic && this.text) {
      this.value = this.text.split(';');
      this.fetchCheckboxOptionByDataDic(this.slotOption.options.dataDic);
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    getLabelValueOptionByDataDic(dataDicUuid, callback) {
      let key = `LabelOptionByDataDic:${dataDicUuid}`;
      this.optionsCacheKey = key;
      this.$tempStorage.getCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            $axios
              .post('/json/data/services', {
                serviceName: 'cdDataDictionaryFacadeService',
                methodName: 'listLocaleItemByDictionaryCode',
                args: JSON.stringify([dataDicUuid])
              })
              .then(({ data }) => {
                let options = [];
                if (data.code == 0 && data.data) {
                  options = data.data;
                }
                resolve(options, key);
              });
          });
        },
        data => {
          if (typeof callback === 'function') {
            callback(data, key);
          }
        }
      );
    },
    fetchCheckboxOptionByDataDic(dataDicUuid) {
      let _this = this;
      let length = this.value.length;
      this.getLabelValueOptionByDataDic(dataDicUuid, result => {
        let getLabel = item => {
          if (item.value && _this.value.indexOf(item.value) > -1) {
            _this.label.push(item.label);
            length--;
          }
          if (length) {
            if (isArray(item)) {
              for (let i = 0; i < item.length; i++) {
                getLabel(item[i]);
                if (!length) {
                  break;
                }
              }
            } else if (item.children && item.children.length > 0) {
              for (let i = 0; i < item.children.length; i++) {
                getLabel(item.children[i]);
                if (!length) {
                  break;
                }
              }
            }
          }
        };
        getLabel(result);
      });
    }
  }
};
</script>
