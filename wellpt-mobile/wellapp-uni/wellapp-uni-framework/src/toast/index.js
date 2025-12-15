/**
 * 扩展 vue 的实例特性
 * @param {*} Vue
 */
export const install = (Vue) => {
  Vue.prototype.$nToast = (options) =>{
    if(process.env.VUE_APP_PLATFORM !== 'app-plus'){
      return
    }
    try {
      const show = ({ 
        title = '', 
        icon = 'none', 
        align ="center",
        verticalAlign='center',
        disNum=10
      }) => {
        const insertString = (originStr, disNum, insertStr = "<br/>") => originStr.replace(new RegExp("(.{" + disNum + "})", "g"), "$1" + insertStr);

        const icons = {
          success: 'static/images/success.png',
          error: 'static/images/error.png'
        };
        const iconSrc = icons[icon] || '';

        let message = insertString(title,disNum)
        if(iconSrc) {
          message = `<img src="${iconSrc}" width="55"></img><br/>${message}`
        }
        plus.nativeUI.closeToast();

        plus.nativeUI.toast(message,{
          align,
          verticalAlign, //垂直居中
          type: 'richtext',
          richTextStyle: {
            align:"center"
          }
        })
      }
      if (typeof options === 'string') {
        options = { title: options };
      }
      show(options)
    } catch (error) {  
    }
  }
  Vue.prototype.$ptToast = {
    show(options) {
      let pointPageUrl = getCurrentPages()[getCurrentPages().length - 1].route;
      if (pointPageUrl == "/packages/_/pages/toast/toast") return;
      uni.navigateTo({
        url: "/packages/_/pages/toast/toast",
        success: function (res) {
          // 利用事件 通知 目标页面
          res.eventChannel.emit("ptToast",options);
        },
      });
    },
  };
};
