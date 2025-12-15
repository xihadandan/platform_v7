import { getCookie, queryString } from '@framework/vue/utils/util';
import { toPng } from '@modules/html-to-image';

export const getQueryString = function (name, defaultValue) {
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
  var values = window.location.search.substr(1).match(reg);
  if (values != null) {
    return decodeURIComponent(values[2]);
  }
  if (defaultValue != null) {
    return defaultValue;
  }
  return '';
};

export const getUrlParams = () => {
  return queryString(window.location.search.substr(1));
}

export const getCurrentUserId = () => {
  return getCookie('cookie.current.userId')
}

// 下载流程图
// this.graphItem.graph.toPNG(
//   dataUri => {
//     import('@antv/x6-common/es/datauri/index').then(res => {
//       res.DataUri.downloadDataUri(dataUri, picName);
//       copyEle.remove();
//       this.downloadLoading = false;
//     });
//   },
//   {
//     copyStyles: true,
//     padding: 20
//   }
// );
// if (this.graphItem) {
//   this.graphItem.exportPNG(`${this.workFlow.property.name}.png`, { padding: 20 });
// }
// graph.zoomToFit({
//   viewportArea: {
//     x: 0,
//     y: 0,
//     width: area.width,
//     height: area.height
//   },
//   padding: 20
// });
export const downloadFlow = ({ el = '#graph-container', picName = '流程图' } = {}) => {
  const ele = document.querySelector(el);
  // require('@modules/html-to-image')
  //   .toPng(ele)
  //   .then(dataUrl => {
  return new Promise((resolve, reject) => {
    // const copyEle = ele.cloneNode(true); // 包括子节点
    // const swimlaneIcon = copyEle.querySelector('.node-swimlane-icon')
    // if (swimlaneIcon) {
    //   swimlaneIcon.remove();
    // }
    // copyEle.style.cssText += `;
    //   position: absolute;
    //   top: 0;
    //   left: 0;
    //   z-index: -1;
    // `
    // document.querySelector('#design-main').append(copyEle)
    const timer = setTimeout(() => {
      // 延迟执行生成图片，防止节点太多造成页面假死
      clearTimeout(timer)
      toPng(ele).then(dataUrl => {
        const link = document.createElement('a');
        link.download = `${picName}.png`;
        link.href = dataUrl;
        link.click();
        resolve()
        const timerLink = setTimeout(() => {
          clearTimeout(timerLink)
          // copyEle.remove();
          link.remove();
        }, 500);
      }, err => {
        reject(err)
      });
    }, 500)

  })
}

export const toPngDom = (element, picName, domtoimage) => {
  // top.appModal.showMask('流程图生成中...');
  // var position = getTaskArea();
  var position = {};
  var wrap = document.createElement('div');
  var width = position.right - position.left + 300;
  var height = position.bottom - position.top + 300;
  wrap.style.width = width + 'px';
  wrap.style.height = height + 'px';
  wrap.style.overflow = 'hidden';
  // wellapp-web\app\public\js\dom-to-image
  domtoimage.toPng(element).then(function (dataUrl) {
    var img = document.createElement('img');
    img.src = dataUrl;
    img.style.marginTop = '-' + (position.top - 100) + 'px';
    img.style.marginLeft = '-' + (position.left - 100) + 'px';
    wrap.appendChild(img);
    document.body.appendChild(wrap);
    domtoimage
      .toPng(wrap, {
        width: width,
        height: height
      })
      .then(function (dataUrl2) {
        var a = document.createElement('a');
        a.href = dataUrl2;
        a.download = picName || '';
        // top.appModal.hideMask();
        a.click();
        a.remove();
      });
  });
}
