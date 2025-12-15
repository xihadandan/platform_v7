export default {
  functional: true,
  name: 'Icon',
  props: {
    type: String,
    size: {
      validator: function validator(val) {
        return !val || typeof val === 'number' || ['small', 'large', 'default'].includes(val);
      }
    },
    color: {
      type: String,
      default: 'var(--w-text-color-dark)'
    }
  },
  render(h, context) {
    let type = context.props.type,
      clickEvent = context.listeners.click || function () {},
      iconNode;

    if (!type || typeof type !== 'string') {
      return <i></i>;
    }

    const isValidJSON = text => {
      try {
        if (!text) {
          return false;
        }
        JSON.parse(text);
        return true;
      } catch (e) {
        return false;
      }
    };
    const props = context.data;
    let hasStyle = isValidJSON(type);
    let config = undefined;
    if (hasStyle) {
      config = JSON.parse(type);
      type = config.iconClass;

      if (!config.showBackground || !context.props.size) {
        props.style = config.cssStyle;
      }
    }
    if (type.indexOf('pticon ') === 0 || type.indexOf('uniicons ') === 0) {
      // 使用iconfont样式class
      iconNode = <i class={type} onClick={clickEvent} {...props} />;
    } else if (type.indexOf('iconfont ') === 0) {
      iconNode = <i class={'pticon ' + type} onClick={clickEvent} {...props} />;
    } else if (type.indexOf('ant-iconfont ') === 0) {
      iconNode = <i class={type} onClick={clickEvent} {...props} />;
    } else if (type.indexOf('glyphicon ') > -1) {
      iconNode = <i class={'pticon ' + type} onClick={clickEvent} {...props} />;
    } else if (type.startsWith('svg-icon-')) {
      iconNode = (
        <i class="iconfont">
          <svg-icon symbol-id={type} onClick={clickEvent} {...props} />
        </i>
      );
    } else {
      // 使用ant-design-vue定义的icon
      iconNode = <a-icon type={type} onClick={clickEvent} {...props}></a-icon>;
    }
    if (config && config.showBackground && context.props.size) {
      const size = context.props.size;
      const style = Object.assign({ backgroundColor: '#ffffff', color: context.props.color }, config.cssStyle);
      iconNode = (
        <a-avatar style={style} size={size}>
          <template slot="icon">{iconNode}</template>
        </a-avatar>
      );
    }

    return iconNode;
  }
};
