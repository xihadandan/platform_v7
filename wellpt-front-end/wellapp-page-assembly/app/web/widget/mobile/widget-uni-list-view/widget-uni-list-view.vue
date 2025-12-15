<template>
  <div class="w-nui-list-v" :style="mainStyle">
    <div class="w-nui-list-header" v-if="hasListHeader">
      <div class="list-header-box" v-if="enableKeywordQuery">
        <a-input class="keyword-query-input" placeholder="搜索"></a-input>
        <!-- 排序和多选都没有或者排序和多选都有时 -->
        <div class="list-filter" v-if="((showSortOrder && hasCheckbox) || (!showSortOrder && !hasCheckbox)) && enableFieldQuery">
          <span style="margin-right: 4px">筛选</span>
          <Icon type="pticon iconfont icon-ptkj-shaixuan" style="font-size: 16px" />
        </div>
        <div class="table-end-btn-control" v-if="!showSortOrder && hasCheckbox && !enableFieldQuery">
          <span style="margin-right: 4px">多选</span>
          <Icon type="pticon iconfont icon-ptkj-duoxuan" style="font-size: 16px" />
        </div>
      </div>
      <div
        class="list-filter-sort"
        v-if="
          showSortOrder ||
          (!enableKeywordQuery && enableFieldQuery && !((showSortOrder && hasCheckbox) || (!showSortOrder && !hasCheckbox))) ||
          (!enableKeywordQuery && hasCheckbox) ||
          (enableFieldQuery && hasCheckbox)
        "
      >
        <div class="list-sort" v-if="showSortOrder">
          <span style="margin-right: 4px">排序</span>
          <Icon type="pticon iconfont icon-ptkj-qiehuanpaixu" style="font-size: 16px" />
        </div>
        <div
          class="list-filter"
          v-if="enableFieldQuery && (!((showSortOrder && hasCheckbox) || (!showSortOrder && !hasCheckbox)) || !enableKeywordQuery)"
        >
          <span style="margin-right: 4px">筛选</span>
          <Icon type="pticon iconfont icon-ptkj-shaixuan" style="font-size: 16px" />
        </div>
        <div class="table-end-btn-control" :style="{ width: !showSortOrder && !enableFieldQuery ? '100%' : '' }" v-if="hasCheckbox">
          <span style="margin-right: 4px">多选</span>
          <Icon type="pticon iconfont icon-ptkj-duoxuan" style="font-size: 16px" />
        </div>
      </div>
    </div>
    <div style="padding-bottom: 1px">
      <template v-if="unEmpty && templateProperties.length > 0">
        <a-list
          item-layout="vertical"
          :data-source="listData"
          v-if="displayStyle == '1'"
          class="list-container"
          :style="[listBoxStyle, backGroundStyle]"
        >
          <a-list-item slot="renderItem" slot-scope="item, index" :class="{ hasBottom: hasBottom }">
            <div v-if="item.title" class="content-title">
              <Icon
                :type="listData[0].titleIcon"
                v-if="listData[0].titleIcon"
                :size="iconSizeFormat(styleConfiguration.listTitleSize, 16)"
              />
              {{ item.title }}
            </div>
            <div v-if="item.subtitle" class="content-subtitle">
              <Icon
                :type="listData[0].subtitleIcon"
                v-if="listData[0].subtitleIcon"
                :size="iconSizeFormat(styleConfiguration.listSubTitleSize)"
              />
              {{ item.subtitle }}
            </div>
            <div v-if="item.note" class="content-note">
              <Icon :type="listData[0].noteIcon" v-if="listData[0].noteIcon" :size="iconSizeFormat(styleConfiguration.listNoteSize, 12)" />
              {{ item.note }}
            </div>
            <div slot="extra" class="content-righttext" v-if="item.rightText">
              <Icon
                :type="listData[0].rightTextIcon"
                v-if="listData[0].rightTextIcon"
                :size="iconSizeFormat(styleConfiguration.listExtraSize)"
              />
              {{ item.rightText }}
            </div>
          </a-list-item>
          <div v-if="hasBottom" slot="footer" class="item-list-footer">
            <a-row type="flex" v-if="hasBottomData">
              <a-col flex="100px" class="content-label">
                <Icon
                  :type="listData[0].bottomLeftIcon"
                  v-if="listData[0].bottomLeftIcon"
                  :size="iconSizeFormat(styleConfiguration.listContentLabelSize)"
                />
                {{ listData[0].bottomLeft }}
              </a-col>
              <a-col flex="auto" class="content-value">
                <Icon
                  :type="listData[0].bottomRightIcon"
                  v-if="listData[0].bottomRightIcon"
                  :size="iconSizeFormat(styleConfiguration.listContentValueSize)"
                />
                {{ listData[0].bottomRight }}
              </a-col>
            </a-row>
            <a-row
              :gutter="
                styleConfiguration.itemFooterButtonGutter || styleConfiguration.itemFooterButtonGutter === 0
                  ? styleConfiguration.itemFooterButtonGutter
                  : 16
              "
              v-if="hasBottomButton"
              class="item-footer-buttons"
            >
              <a-col :span="24 / rowBottomButtons.length" v-for="btn in rowBottomButtons" :key="btn.id">
                <a-button :type="btn.type" :key="btn.id" block>
                  <Icon :type="btn.icon" v-if="btn.icon" :size="20" />
                  {{ btn.textHidden ? '' : btn.title }}
                </a-button>
              </a-col>
            </a-row>
          </div>
        </a-list>
        <div class="item-card-body-content" :style="[cardBoxStyle, backGroundStyle]" v-else>
          <div :style="cardContentStyle">
            <a-row type="flex" class="item-card-title" v-if="listData[0].title || listData[0].rightText">
              <a-col flex="auto" v-if="listData[0].title">
                <Icon
                  :type="listData[0].titleIcon"
                  v-if="listData[0].titleIcon"
                  :size="iconSizeFormat(styleConfiguration.cardTitleSize, 16)"
                />
                {{ listData[0].title }}
                <div class="content-subtitle" v-if="listData[0].subtitle">
                  <Icon
                    :type="listData[0].subtitleIcon"
                    v-if="listData[0].subtitleIcon"
                    :size="iconSizeFormat(styleConfiguration.cardSubTitleSize)"
                  />
                  {{ listData[0].subtitle }}
                </div>
              </a-col>
              <a-col flex="100px" v-if="listData[0].rightText" class="content-righttext" style="text-align: right">
                <Icon
                  :type="listData[0].rightTextIcon"
                  v-if="listData[0].rightTextIcon"
                  :size="iconSizeFormat(styleConfiguration.cardExtraSize)"
                />
                {{ listData[0].rightText }}
              </a-col>
            </a-row>
            <template v-for="(templateProperty, ctIndex) in contentTemplateProperties">
              <a-row type="flex" :key="ctIndex" class="item-card-content" :justify="styleConfiguration.justifyContent">
                <a-col
                  :flex="styleConfiguration.cardContentLabelWidth ? styleConfiguration.cardContentLabelWidth + 'px' : '100px'"
                  class="content-label"
                >
                  <Icon
                    :type="listData[0][templateProperty.mapColumn + 'Icon']"
                    v-if="listData[0][templateProperty.mapColumn + 'Icon']"
                    :size="iconSizeFormat(styleConfiguration.cardContentLabelSize)"
                  />
                  {{ templateProperty.title }}
                </a-col>
                <a-col flex="auto" class="content-value">{{ listData[0][templateProperty.mapColumn] }}</a-col>
              </a-row>
            </template>
            <div v-if="hasBottom" class="item-card-footer">
              <a-row type="flex" v-if="hasBottomData">
                <a-col
                  :flex="styleConfiguration.cardContentLabelWidth ? styleConfiguration.cardContentLabelWidth + 'px' : '100px'"
                  class="content-label"
                >
                  <Icon
                    :type="listData[0].bottomLeftIcon"
                    v-if="listData[0].bottomLeftIcon"
                    :size="iconSizeFormat(styleConfiguration.cardContentLabelSize)"
                  />
                  {{ listData[0].bottomLeft }}
                </a-col>
                <a-col flex="auto" class="content-value">
                  <Icon
                    :type="listData[0].bottomRightIcon"
                    v-if="listData[0].bottomRightIcon"
                    :size="iconSizeFormat(styleConfiguration.cardContentValueSize)"
                  />
                  {{ listData[0].bottomRight }}
                </a-col>
              </a-row>
              <a-row
                :gutter="
                  styleConfiguration.itemFooterButtonGutter || styleConfiguration.itemFooterButtonGutter === 0
                    ? styleConfiguration.itemFooterButtonGutter
                    : 16
                "
                v-if="hasBottomButton"
                class="item-footer-buttons"
              >
                <a-col :span="24 / rowBottomButtons.length" v-for="btn in rowBottomButtons" :key="btn.id">
                  <a-button :type="btn.type" :key="btn.id" block>
                    <Icon :type="btn.icon" v-if="btn.icon" :size="20" />
                    {{ btn.textHidden ? '' : btn.title }}
                  </a-button>
                </a-col>
              </a-row>
            </div>
          </div>
        </div>
      </template>
      <div v-else style="background-color: #ffffff">
        <div style="text-align: center; padding: var(--w-padding-2xs) 0 0; color: var(--w-text-color-light)">请配置列表的字段定义</div>
        <a-skeleton />
      </div>
    </div>
  </div>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { each, isEmpty, includes, camelCase, isNumber, get, map } from 'lodash';
import { generateId } from '@framework/vue/utils/util';
export default {
  name: 'WidgetUniListView',
  mixins: [widgetMixin],
  props: {
    hiddenButtons: Boolean
  },
  data() {
    const _self = this;
    if (this.widget.wtype == 'WidgetTable') {
      if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
        let showColums = this.widget.configuration.columns.filter(col => !col.hidden);
        let uniDefaultTemplateOptions = [
          { label: '标题', value: 'title' },
          { label: '小标题', value: 'subtitle' },
          { label: '备注', value: 'note' },
          { label: '右侧文本', value: 'rightText' },
          { label: '底部左侧', value: 'bottomLeft' },
          { label: '底部右侧', value: 'bottomRight' }
        ];
        let templateProperties = [];
        if (showColums.length > 0) {
          for (let i = 0; i < showColums.length; i++) {
            let col = showColums[i];
            let templateOption = uniDefaultTemplateOptions[i];
            if (templateOption) {
              templateProperties.push({
                uuid: generateId(),
                title: col.title,
                name: templateOption.value,
                mapColumn: col.dataIndex,
                mapColumnName: col.title,
                renderer: {},
                sortOrder: ''
              });
            } else {
              break;
            }
          }
        }
        this.$set(this.widget.configuration, 'uniConfiguration', {
          displayStyle: '2', //1列表2卡片
          templateProperties,
          readMarker: false,
          backgroundStyle: {
            backgroundColor: '#ffffff', // 白底
            backgroundImage: undefined,
            backgroundImageInput: undefined,
            bgImageUseInput: false,
            backgroundRepeat: undefined,
            backgroundPosition: undefined
          }
        });
      }
    }
    let configuration = _self.widget.configuration || {};
    let uniConfiguration = configuration.uniConfiguration || undefined;
    let styleConfiguration = {};
    if (uniConfiguration) {
      if (uniConfiguration.hasOwnProperty('styleConfiguration')) {
        styleConfiguration = uniConfiguration.styleConfiguration;
      } else {
        styleConfiguration = {
          backgroundStyle: uniConfiguration.backgroundStyle,
          mainStyle: uniConfiguration.mainStyle
        };
      }
    } else {
      if (configuration.hasOwnProperty('styleConfiguration')) {
        styleConfiguration = configuration.styleConfiguration;
      } else {
        styleConfiguration = {
          backgroundStyle: configuration.backgroundStyle,
          mainStyle: configuration.mainStyle
        };
      }
    }
    return {
      templateProperties: uniConfiguration ? uniConfiguration.templateProperties : configuration.templateProperties || [],
      uniConfiguration,
      configuration: configuration,
      styleConfiguration,
      sortOrderConfigs: [],
      tableEndButtons: [],
      cardStyleObject: [
        { name: 'titleTextColor', style: '--w-list-view-card-title-color' },
        { name: 'subTitleColor', style: '--w-list-view-card-subtitle-color' },
        { name: 'rightTextColor', style: '--w-list-view-card-extra-color' },
        { name: 'leftContentTextColor', style: '--w-list-view-card-content-label-color' },
        { name: 'rightContentTextColor', style: '--w-list-view-card-content-value-color' },
        { name: 'justifyContent', style: '--w-list-view-card-content-justify' }
      ],
      listStyleObject: [
        { name: 'titleTextColor', style: '--w-list-view-list-title-color' },
        { name: 'subTitleColor', style: '--w-list-view-list-subtitle-color' },
        { name: 'noteColor', style: '--w-list-view-list-note-color' },
        { name: 'rightTextColor', style: '--w-list-view-list-extra-color' },
        { name: 'leftContentTextColor', style: '--w-list-view-card-content-label-color' },
        { name: 'rightContentTextColor', style: '--w-list-view-card-content-value-color' }
      ]
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    displayStyle() {
      return this.uniConfiguration ? this.uniConfiguration.displayStyle : this.configuration.displayStyle;
    },
    enableKeywordQuery() {
      if (this.uniConfiguration) {
        return (
          (this.widget.configuration.search.type == 'keywordAdvanceSearch' &&
            this.widget.configuration.search.keywordSearchEnable &&
            this.widget.configuration.search.keywordSearchColumns.length > 0) ||
          (this.widget.configuration.search.columnSearchGroupEnable && this.widget.configuration.search.columnSearchGroup.length > 0)
        );
      } else {
        return this.configuration.hasSearch || this.configuration.query.keyword;
      }
    },
    enableFieldQuery() {
      if (this.uniConfiguration) {
        return (
          this.widget.configuration.search.type == 'keywordAdvanceSearch' &&
          this.widget.configuration.search.advanceSearchEnable &&
          this.widget.configuration.search.columnAdvanceSearchGroup.length > 0
        );
      } else {
        return this.configuration.query.fieldSearch && this.queryFields.length > 0;
      }
    },
    listData() {
      let data = [],
        obj = {};
      each(this.templateProperties, item => {
        if (this.displayStyle == '2' && !['title', 'bottomLeft', 'bottomRight', 'rightText', 'subtitle'].includes(item.name)) {
          obj[item.mapColumn] = item.mapColumnName;
          if (item.icon) {
            obj[item.mapColumn + 'Icon'] = item.icon;
          }
        } else {
          obj[item.name] = item.mapColumnName;
          if (item.icon) {
            obj[item.name + 'Icon'] = item.icon;
          }
        }
      });
      data.push(obj);
      return data;
    },
    contentTemplateProperties: function () {
      let properties = [];
      _.each(this.templateProperties, function (property) {
        if (
          property.name == 'title' ||
          property.name == 'bottomLeft' ||
          property.name == 'bottomRight' ||
          property.name == 'rightText' ||
          property.name == 'subtitle'
        ) {
          return;
        }
        properties.push(property);
      });
      return properties;
    },
    queryFields() {
      return this.configuration.query.fields || [];
    },
    unEmpty() {
      let isTrue = false;
      for (let i = 0; i < this.templateProperties.length; i++) {
        if (this.templateProperties[i].mapColumnName) {
          isTrue = isTrue || true;
        }
      }
      this.initSortOrderConfigs();
      return isTrue;
    },
    rowBottomButtons() {
      let buttons = [];
      let btns = [];
      if (this.uniConfiguration) {
        // 默认行下显示
        if (
          !this.uniConfiguration.rowButtonPosition &&
          this.widget.configuration.rowButton.enable &&
          this.widget.configuration.rowButton.buttons.length
        ) {
          each(this.widget.configuration.rowButton.buttons, function (button) {
            if (button.defaultVisible === undefined || button.defaultVisible) {
              btns.push(
                Object.assign(
                  {},
                  {
                    type: button.style && button.style.type,
                    icon: button.style && button.style.icon,
                    title: button.title,
                    textHidden: button.style && button.style.textHidden
                  }
                )
              );
            }
          });
        }
      } else {
        btns = map(this.getRowBottomButtons(), button => {
          return {
            type: button.type,
            icon: button.icon && button.icon.className,
            title: button.text,
            textHidden: button.hiddenText
          };
        });
      }
      if (btns.length > 3) {
        buttons = btns.slice(0, 2);
        let moreType = 'link';
        if (this.uniConfiguration) {
          if (this.uniConfiguration.rowBottomMoreButtonType) {
            moreType = this.uniConfiguration.rowBottomMoreButtonType;
          }
        } else if (this.configuration.rowBottomMoreButtonType) {
          moreType = this.configuration.rowBottomMoreButtonType;
        }
        buttons.push({
          title: '更多',
          type: moreType
        });
      } else {
        return btns;
      }
      return buttons;
    },
    // 行下按钮
    hasBottomButton() {
      return this.rowBottomButtons.length > 0;
    },
    // 是否有表格尾部操作按钮
    hasTableEndButtons: function () {
      if (this.uniConfiguration) {
        if (this.hiddenButtons) {
          // 隐藏
          return false;
        }
        return this.widget.configuration.headerButton.enable && this.widget.configuration.headerButton.buttons.length;
      } else {
        this.tableEndButtons = this.getTableEndButtons();
        return !isEmpty(this.tableEndButtons);
      }
    },
    // 是否多选按钮(表格组件多选，或者，数据列表有底部按钮)
    hasCheckbox() {
      return (
        (this.widget.wtype !== 'WidgetTable' && this.hasTableEndButtons) ||
        (this.widget.wtype == 'WidgetTable' && this.widget.configuration.rowSelectType == 'checkbox')
      );
    },
    hasListHeader() {
      return this.enableKeywordQuery || this.showSortOrder || this.enableFieldQuery || this.hasCheckbox;
    },
    showSortOrder() {
      if (this.uniConfiguration) {
        return this.uniConfiguration.showSortOrder && this.sortOrderConfigs.length > 0;
      } else {
        return this.configuration.showSortOrder && this.sortOrderConfigs.length > 0;
      }
    },
    cardBoxStyle() {
      let style = {};
      if (this.styleConfiguration.margin || this.styleConfiguration.margin === 0) {
        style.margin = this.styleValueFormat(this.styleConfiguration.margin);
      }
      if (this.styleConfiguration.spacing || this.styleConfiguration.spacing === 0) {
        style.padding = this.styleValueFormat(this.styleConfiguration.spacing);
      }
      if (this.styleConfiguration.borderColor) {
        style.borderColor = this.styleConfiguration.borderColor;
      }
      if (this.styleConfiguration.borderRadius || this.styleConfiguration.borderRadius === 0) {
        style.borderRadius = this.styleValueFormat(this.styleConfiguration.borderRadius);
      }
      return style;
    },
    listBoxStyle() {
      let style = {};
      if (this.styleConfiguration.spacing || this.styleConfiguration.spacing === 0) {
        style['--w-list-view-list-item-padding'] = this.styleValueFormat(this.styleConfiguration.spacing);
      }
      return style;
    },
    cardContentStyle() {
      let style = {};
      if (this.styleConfiguration.padding || this.styleConfiguration.padding === 0) {
        style.padding = this.styleValueFormat(this.styleConfiguration.padding);
      }
      style['--w-list-view-value-text-align'] = this.styleConfiguration.justifyContent == 'space-between' ? 'right' : 'left';
      return style;
    },
    backGroundStyle() {
      let style = {};
      if (this.styleConfiguration.backgroundStyle) {
        let {
          backgroundColor,
          backgroundImage,
          backgroundImageInput,
          bgImageUseInput,
          backgroundPosition,
          backgroundRepeat,
          backgroundSize
        } = this.styleConfiguration.backgroundStyle;

        if (backgroundColor) {
          style.backgroundColor = backgroundColor;
        }
        let bgImgStyle = bgImageUseInput ? backgroundImageInput : backgroundImage;
        if (bgImgStyle) {
          let isUrl =
            bgImgStyle.startsWith('data:') ||
            bgImgStyle.startsWith('http') ||
            bgImgStyle.startsWith('/') ||
            bgImgStyle.startsWith('../') ||
            bgImgStyle.startsWith('./');
          style.backgroundImage = isUrl ? `url("${bgImgStyle}")` : bgImgStyle;
        }
        if (backgroundPosition) {
          style.backgroundPosition = backgroundPosition;
        }
        if (backgroundRepeat) {
          style.backgroundRepeat = backgroundRepeat;
        }
        if (backgroundSize) {
          style.backgroundSize = backgroundSize;
        }
      }
      return style;
    },
    mainStyle() {
      let style = {};
      if (this.styleConfiguration.mainStyle) {
        let str = this.styleConfiguration.mainStyle.split(';');
        str.forEach(item => {
          var itemArr = item.split(':');
          if (itemArr.length == 2 && itemArr[0] && itemArr[1]) {
            itemArr[0] = itemArr[0].replace(/[\s\p{C}]/gu, '');
            if (itemArr[0].startsWith('--')) {
              style[itemArr[0]] = itemArr[1].split(';')[0];
            } else {
              style[camelCase(itemArr[0])] = itemArr[1].split(';')[0];
            }
          }
        });
      }
      if (this.displayStyle == '2') {
        this.cardStyleObject.forEach(item => {
          if (this.styleConfiguration[item.name]) {
            style[item.style] = this.styleValueFormat(this.styleConfiguration[item.name]);
          }
        });
        for (let key in this.styleConfiguration) {
          if (key.startsWith('card') || key.startsWith('item')) {
            let name = '--w-list-view-' + key.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
            let value = this.styleValueFormat(this.styleConfiguration[key]);
            if (value) {
              style[name] = value;
            }
          }
        }
        if (this.styleConfiguration.footerHr) {
          style['--w-list-view-card-footer-border-color'] = this.styleConfiguration.footerHrColor || 'var(--w-border-color-lighter)';
          style['--w-list-view-card-footer-margin'] =
            this.styleValueFormat(this.styleConfiguration.footerMargin) || 'var(--w-margin-2xs) 0 0';
          style['--w-list-view-card-footer-padding'] =
            this.styleValueFormat(this.styleConfiguration.footerPadding) || 'var(--w-margin-2xs) 0 0';
        }
      } else {
        this.listStyleObject.forEach(item => {
          if (this.styleConfiguration[item.name]) {
            style[item.style] = this.styleValueFormat(this.styleConfiguration[item.name]);
          }
        });
        for (let key in this.styleConfiguration) {
          if (key.startsWith('list') || key.startsWith('item')) {
            let name = '--w-list-view-' + key.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
            let value = this.styleValueFormat(this.styleConfiguration[key]);
            if (value) {
              style[name] = value;
            }
          }
        }
        if (this.styleConfiguration.footerHr) {
          style['--w-list-view-list-footer-border-color'] = this.styleConfiguration.footerHrColor || 'var(--w-border-color-lighter)';
          style['--w-list-view-list-footer-margin'] =
            this.styleValueFormat(this.styleConfiguration.footerMargin) || 'var(--w-margin-2xs) 0 0';
          style['--w-list-view-list-footer-padding'] =
            this.styleValueFormat(this.styleConfiguration.footerPadding) || 'var(--w-margin-2xs) 0 0';
        }
      }

      return style;
    },
    hasBottomData() {
      let hasFlag = false;
      const properties = this.templateProperties;
      for (let i = 0; i < properties.length; i++) {
        const property = properties[i];
        if (property.name == 'bottomRight' || property.name == 'bottomLeft') {
          if (!hasFlag && property.mapColumn) {
            hasFlag = true;
          }
          break; // 现在可以合法使用 break
        }
      }
      return hasFlag;
    },
    hasBottom() {
      return this.hasBottomData || this.hasBottomButton;
    }
  },
  created() {},
  methods: {
    // 行下按钮
    getRowBottomButtons: function () {
      return this.getButtonByPosition('5');
    },
    getTableEndButtons: function () {
      return this.getButtonByPosition('2');
    },
    getButtonByPosition: function (btnPos) {
      let buttons = this.configuration.buttons || [];
      let btns = [];
      each(buttons, function (button) {
        let position = button.position || [];
        if (position.indexOf(btnPos) != -1 && (button.defaultVisible === undefined || button.defaultVisible)) {
          btns.push(Object.assign({}, button));
        }
      });
      return btns;
    },
    /**
     * 初始化排序配置
     */
    initSortOrderConfigs: function () {
      let _self = this;
      let sortOrderConfigs = [];
      each(this.templateProperties, function (property) {
        if (property.sortOrder == 'asc' || property.sortOrder == 'desc') {
          let ascRemark = '';
          let descRemark = '';
          if (property.dataType == 'Date') {
            ascRemark = '从远到近';
            descRemark = '从近到远';
          } else {
            ascRemark = '升序';
            descRemark = '降序';
          }
          sortOrderConfigs.push({
            sortName: property.mapColumn,
            sortOrder: 'asc',
            text: '按' + property.title + ascRemark,
            id: property.mapColumn + '_asc'
          });
          sortOrderConfigs.push({
            sortName: property.mapColumn,
            sortOrder: 'desc',
            text: '按' + property.title + descRemark,
            id: property.mapColumn + '_desc'
          });
        }
      });
      // 表格的排序配置
      // if (this.widget.configuration.defaultSort && this.widget.configuration.defaultSort.length > 0) {
      //   let ascRemark = '升序';
      //   let descRemark = '降序';
      //   for (let i = 0, len = this.widget.configuration.defaultSort.length; i < len; i++) {
      //     let sortItem = this.widget.configuration.defaultSort[i];
      //     let hasIndex = sortOrderConfigs.findIndex(item => item.sortName == sortItem.dataIndex);
      //     if (hasIndex == -1) {
      //       sortOrderConfigs.push({
      //         sortName: sortItem.dataIndex,
      //         sortOrder: 'asc',
      //         text: '按' + sortItem.title + ascRemark,
      //         id: sortItem.dataIndex + '_asc'
      //       });
      //       sortOrderConfigs.push({
      //         sortName: sortItem.dataIndex,
      //         sortOrder: 'desc',
      //         text: '按' + sortItem.title + descRemark,
      //         id: sortItem.dataIndex + '_desc'
      //       });
      //     }
      //   }
      // }
      _self.sortOrderConfigs = sortOrderConfigs;
    },
    formatBorderRadius(borderRadius) {
      if (borderRadius || borderRadius === 0) {
        if (Array.isArray(borderRadius)) {
          return borderRadius
            .map(item => {
              return (item || 0) + 'px';
            })
            .join(' ');
        } else {
          return borderRadius + 'px';
        }
      }
      return undefined;
    },
    styleValueFormat(value) {
      if (isNumber(value)) {
        return value + 'px';
      } else if (Array.isArray(value)) {
        return value
          .map(item => {
            return (item || 0) + 'px';
          })
          .join(' ');
      }
      return value;
    },
    iconSizeFormat(fontSize, initSize) {
      return fontSize ? fontSize + 8 : (initSize || 14) + 8;
    }
  },
  mounted() {}
};
</script>
<style lang="less" scoped>
.w-nui-list-v {
  background-color: var(--w-gray-color-4);

  --w-list-view-list-title-size: var(--w-font-size-lg);
  --w-list-view-list-title-color: var(--w-text-color-dark);
  --w-list-view-list-title-weight: bold;
  --w-list-view-list-title-padding: 0;
  --w-list-view-list-title-margin: 0;

  --w-list-view-list-subtitle-size: var(--w-font-size-base);
  --w-list-view-list-subtitle-color: var(--w-text-color-base);
  --w-list-view-list-subtitle-weight: normal;
  --w-list-view-list-subtitle-padding: 0;
  --w-list-view-list-subtitle-margin: var(--w-margin-3xs) 0 0;

  --w-list-view-list-note-size: var(--w-font-size-sm);
  --w-list-view-list-note-color: var(--w-text-color-light);
  --w-list-view-list-note-weight: normal;
  --w-list-view-list-note-padding: 0;
  --w-list-view-list-note-margin: var(--w-margin-3xs) 0 0;

  --w-list-view-list-extra-size: var(--w-font-size-base);
  --w-list-view-list-extra-color: var(--w-text-color-light);
  --w-list-view-list-extra-weight: normal;
  --w-list-view-list-extra-padding: 0;
  --w-list-view-list-extra-margin: 0;
  --w-list-view-value-text-align: right;

  --w-list-view-list-item-padding: var(--w-padding-2xs) 0;
  --w-list-view-list-item-border-color: var(--w-border-color-light);

  --w-list-view-list-content-label-size: var(--w-font-size-base);
  --w-list-view-list-content-label-color: var(--w-text-color-base);
  --w-list-view-list-content-label-weight: normal;
  --w-list-view-list-content-value-size: var(--w-font-size-base);
  --w-list-view-list-content-value-color: var(--w-text-color-dark);
  --w-list-view-list-content-value-weight: normal;

  --w-list-view-list-footer-margin: 0;
  --w-list-view-list-footer-padding: var(--w-padding-3xs) 0 0;
  --w-list-view-list-footer-border-color: transparent;

  --w-list-view-card-title-size: var(--w-font-size-lg);
  --w-list-view-card-title-color: var(--w-text-color-dark);
  --w-list-view-card-title-weight: bold;
  --w-list-view-card-title-border-color: var(--w-border-color-dark);
  --w-list-view-card-title-padding: 0 0 var(--w-padding-xs);
  --w-list-view-card-title-margin: 0 0 var(--w-margin-3xs);
  --w-list-view-card-content-label-size: var(--w-font-size-base);
  --w-list-view-card-content-label-color: var(--w-text-color-base);
  --w-list-view-card-content-label-weight: normal;
  --w-list-view-card-content-value-size: var(--w-font-size-base);
  --w-list-view-card-content-value-color: var(--w-text-color-dark);
  --w-list-view-card-content-value-weight: normal;
  --w-list-view-card-content-justify: normal;
  --w-list-view-card-content-padding: var(--w-padding-3xs) 0 0;

  --w-list-view-card-subtitle-size: var(--w-font-size-base);
  --w-list-view-card-subtitle-color: var(--w-text-color-base);
  --w-list-view-card-subtitle-weight: normal;
  --w-list-view-card-subtitle-padding: 0;
  --w-list-view-card-subtitle-margin: var(--w-margin-3xs) 0 0;

  --w-list-view-card-extra-size: var(--w-font-size-base);
  --w-list-view-card-extra-color: var(--w-text-color-light);
  --w-list-view-card-extra-weight: normal;
  --w-list-view-card-extra-padding: 0;
  --w-list-view-card-extra-margin: 0;

  --w-list-view-card-footer-margin: 0;
  --w-list-view-card-footer-padding: var(--w-padding-3xs) 0 0;
  --w-list-view-card-footer-border-color: transparent;

  --w-list-view-item-footer-button-hr-color: var(--w-border-color-light);
  --w-list-view-item-footer-button-hr-height: 18px;
  --w-list-view-item-footer-button-padding: 0;

  .w-nui-list-header {
    padding: 12px;
    background-color: #ffffff;
    color: var(--w-text-color-dark);
    margin-bottom: 12px;
    .list-header-box {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;

      .keyword-query-input {
        flex: 1;
        background-color: var(--w-gray-color-2);
        border-color: 0;
      }
    }

    .keyword-query-input {
      & + .list-filter,
      & + .table-end-btn-control {
        margin-left: 12px;
      }
    }

    .list-header-box + .list-filter-sort {
      margin-top: 12px;
    }

    .table-end-btn-control {
      text-align: right;
    }

    .list-filter-sort {
      display: flex;
      flex-direction: row;
      // justify-content: space-between;
      align-items: center;

      > * {
        flex-grow: 1;
        text-align: center;
      }
    }

    .list-filter {
      & + .table-end-btn-control {
        margin-left: 12px;
      }
    }
  }
  .ant-list-something-after-last-item {
    border-bottom: 1px solid var(--w-list-view-list-item-border-color);
    .ant-spin-nested-loading {
      .ant-spin-container > .ant-list-items > .ant-list-item:last-child {
        border-bottom: 1px solid var(--w-list-view-list-footer-border-color);
      }
    }
    .ant-list-footer {
      padding-bottom: 0px !important;
      padding-top: 0px !important;
      margin: var(--w-list-view-list-footer-margin);
    }
  }
  .ant-list-items {
    .ant-list-item {
      padding: var(--w-list-view-list-item-padding);

      &.hasBottom {
        padding-bottom: 0px;
      }
    }
  }
  .ant-list-footer {
    padding-bottom: 0px;
    padding-top: 0px;
    margin: var(--w-list-view-list-footer-margin);
  }
  .item-card-footer {
    border-top: 1px solid var(--w-list-view-card-footer-border-color);
    padding: var(--w-list-view-card-footer-padding);
    margin: var(--w-list-view-card-footer-margin);
  }
  .item-list-footer {
    padding: var(--w-list-view-list-footer-padding);
  }
  .content-title {
    font-size: var(--w-list-view-list-title-size);
    color: var(--w-list-view-list-title-color);
    font-weight: var(--w-list-view-list-title-weight);
    padding: var(--w-list-view-list-title-padding);
    margin: var(--w-list-view-list-title-margin);
    > i {
      font-size: var(--w-list-view-list-title-size);
    }
  }
  .content-subtitle {
    font-size: var(--w-list-view-list-subtitle-size);
    color: var(--w-list-view-list-subtitle-color);
    font-weight: var(--w-list-view-list-subtitle-weight);
    padding: var(--w-list-view-list-subtitle-padding);
    margin: var(--w-list-view-list-subtitle-margin);
    > i {
      font-size: var(--w-list-view-list-subtitle-size);
    }
  }
  .content-note {
    font-size: var(--w-list-view-list-note-size);
    color: var(--w-list-view-list-note-color);
    font-weight: var(--w-list-view-list-note-weight);
    padding: var(--w-list-view-list-note-padding);
    margin: var(--w-list-view-list-note-margin);
    > i {
      font-size: var(--w-list-view-list-note-size);
    }
  }
  .content-righttext {
    font-size: var(--w-list-view-list-extra-size);
    color: var(--w-list-view-list-extra-color);
    font-weight: var(--w-list-view-list-extra-weight);
    padding: var(--w-list-view-list-extra-padding);
    margin: var(--w-list-view-list-extra-margin);
    > i {
      font-size: var(--w-list-view-list-extra-size);
    }
  }
  .content-label {
    font-size: var(--w-list-view-list-content-label-size);
    color: var(--w-list-view-card-content-label-color);
    font-weight: var(--w-list-view-list-content-label-weight);
    > i {
      font-size: var(--w-list-view-list-content-label-size);
    }
  }
  .content-value {
    width: 0;
    font-size: var(--w-list-view-list-content-value-size);
    color: var(--w-list-view-card-content-value-color);
    font-weight: var(--w-list-view-list-content-value-weight);
    text-align: var(--w-list-view-value-text-align);
    > i {
      font-size: var(--w-list-view-list-content-value-size);
    }
  }
  ::v-deep .ant-list-item-extra {
    align-items: center;
    display: flex;
  }
  .list-container {
    background-color: #ffffff;
    padding: 0px 15px;
  }
  .item-card-body-content {
    margin: 12px;
    background-color: #ffffff;
    border: 1px solid var(--w-border-color-light);
    border-radius: 4px;
    padding: 10px 10px 14px;

    .item-card-title {
      font-size: var(--w-list-view-card-title-size);
      color: var(--w-list-view-card-title-color);
      border-bottom: 1px solid var(--w-list-view-card-title-border-color);
      font-weight: var(--w-list-view-card-title-weight);
      padding: var(--w-list-view-card-title-padding);
      margin: var(--w-list-view-card-title-margin);
      > i {
        font-size: var(--w-list-view-card-title-size);
      }
    }
    .item-card-content {
      padding: var(--w-list-view-card-content-padding);
    }
    .content-label {
      font-size: var(--w-list-view-card-content-label-size);
      color: var(--w-list-view-card-content-label-color);
      font-weight: var(--w-list-view-card-content-label-weight);
      > i {
        font-size: var(--w-list-view-card-content-label-size);
      }
    }
    .content-value {
      font-size: var(--w-list-view-card-content-value-size);
      color: var(--w-list-view-card-content-value-color);
      font-weight: var(--w-list-view-card-content-value-weight);
      > i {
        font-size: var(--w-list-view-card-content-value-size);
      }
    }
    .content-subtitle {
      font-size: var(--w-list-view-card-subtitle-size);
      color: var(--w-list-view-card-subtitle-color);
      font-weight: var(--w-list-view-card-subtitle-weight);
      padding: var(--w-list-view-card-subtitle-padding);
      > i {
        font-size: var(--w-list-view-card-subtitle-size);
      }
    }
    .content-righttext {
      font-size: var(--w-list-view-card-extra-size);
      color: var(--w-list-view-card-extra-color);
      font-weight: var(--w-list-view-card-extra-weight);
      padding: var(--w-list-view-card-extra-padding);
      margin: var(--w-list-view-card-extra-margin);
      > i {
        font-size: var(--w-list-view-card-extra-size);
      }
    }
  }
  .item-footer-buttons {
    padding: var(--w-list-view-item-footer-button-padding);
    .ant-col {
      position: relative;
      &::after {
        position: absolute;
        content: '';
        width: 1px;
        height: var(--w-list-view-item-footer-button-hr-height);
        background-color: var(--w-list-view-item-footer-button-hr-color);
        right: 0;
        top: 50%;
        margin-top: e('calc(0px - var(--w-list-view-item-footer-button-hr-height) / 2)');
      }
      &:last-child::after {
        content: none;
      }
    }
  }
}
</style>
