<template>
  <Scroll style="height: calc(100vh - 170px)">
    <a-form-model :label-col="{ style: { width: '23%' } }" :wrapper-col="{ style: { width: '75%' } }">
      <a-form-model-item>
        <template slot="label">选择字段</template>
        <a-select
          v-model="sortColumn.dataIndex"
          :options="fieldSelectOptions"
          :style="{ width: '100%' }"
          @change="changeField"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        />
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">排序规则</template>
        <a-radio-group size="small" v-model="sortColumn.sortType" button-style="solid">
          <a-radio-button value="asc">升序</a-radio-button>
          <a-radio-button value="desc">降序</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <template v-if="sortColumn.dataIndex && column">
        <a-form-model-item label="排序方式">
          <a-select
            v-model="column.sortable.alogrithmType"
            :options="alogrithmTypeOptions"
            :style="{ width: '100%' }"
            :getPopupContainer="getPopupContainerByPs()"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="日期格式" v-show="column.sortable.alogrithmType === 'orderByDate'">
          <a-select
            v-model="column.sortable.datePattern"
            :options="datePatternOptions"
            :style="{ width: '100%' }"
            :getPopupContainer="getPopupContainerByPs()"
          ></a-select>
        </a-form-model-item>
        <a-form-model-item label="排序算法" v-show="column.sortable.alogrithmType === 'orderByDefine'">
          <WidgetCodeEditor @save="value => (column.sortable.script = value)" :value="column.sortable.script" helpTipWidth="400px">
            <a-button icon="code">编写代码</a-button>
            <template slot="help">
              <div>
                <div>入参说明：</div>
                <ul>
                  <li>
                    <a-tag>value</a-tag>
                    : 比较的两行列值
                  </li>
                  <li>
                    <a-tag>row</a-tag>
                    : 比较的两行显示列的数据
                  </li>
                  <li>
                    <a-tag>formData</a-tag>
                    : 比较的两行对应表单数据, row上的往往是显示需要的字符内容, 如果需要比较真实的表单字段值, 则需要使用formData
                  </li>
                  <li>
                    <a-tag>dataIndex</a-tag>
                    : 当前比较的字段编码
                  </li>
                </ul>
                <div>返回说明：如果value[0]的数据比value[1]小则为负数, 如果value[0]的数据比value[1]大则为正数; 相等的时候返回 0</div>
                <div>例如：</div>
                <div>return Number(value[0]) > Number(value[1]) ? 1 : (Number(value[0]) < Number(value[1])?-1:0)</div>
              </div>
            </template>
          </WidgetCodeEditor>
        </a-form-model-item>
      </template>
    </a-form-model>
  </Scroll>
</template>

<script type="text/babel">
import { datePatterns } from '../../commons/constant';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';

export default {
  name: 'ColumnSortConfiguration',
  props: {
    widget: Object,
    designer: Object,
    sortColumn: Object,
    fieldSelectOptions: Array,
    columnIndexOptions: Object
  },
  components: {},
  computed: {},
  data() {
    let datePatternOptions = [];
    for (let i = 0, len = datePatterns.length; i < len; i++) {
      datePatternOptions.push({
        label: datePatterns[i],
        value: datePatterns[i]
      });
    }
    return {
      column: null,
      datePatternOptions,
      alogrithmTypeOptions: [
        { label: '按日期排序', value: 'orderByDate' },
        { label: '按数字排序', value: 'orderByNumber' },
        { label: '按拼音排序', value: 'orderByPinYin' },
        { label: '按字符排序', value: 'orderByChar' },
        { label: '自定义排序', value: 'orderByDefine' }
      ]
    };
  },
  created() {
    this.getConfiguatioanColumn();
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    changeField(value) {
      this.$emit('change', value);
      this.getConfiguatioanColumn();
    },
    getConfiguatioanColumn() {
      if (this.sortColumn.dataIndex) {
        this.column = this.widget.configuration.columns.find(item => item.dataIndex == this.sortColumn.dataIndex);
        if (!this.column) {
          this.column = this.sortColumn;
        }
      } else {
        this.column = undefined;
      }
    }
  }
};
</script>
