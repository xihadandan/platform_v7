// FIXME: 增加系统上下文地址路径？？
// if (!EASY_ENV_IS_NODE) {
//   (function () {
//     /**
//      * 修改跳转链接，自动附上 app_system_id
//      */

//     document.body.addEventListener('click', e => {
//       let target = e.target;
//       if (target.nodeName.toLowerCase() == 'a') {
//         e.preventDefault();// 阻止链接跳转，增加系统ID参数
//         let href = target.getAttribute('href'), hrefTarget = target.getAttribute('target');
//         if (hrefTarget == '_blank') {
//           window.open(appendAppSystemId(href), '_blank');
//         } else {
//           window.open(appendAppSystemId(href));
//         }
//       }
//     })

//     function appendAppSystemId(url) {
//       let parts = url.split('?'), base = parts[0];
//       if (parts.length > 1) {
//         parts[1] = 'app_system_id=' + window.__INITIAL_STATE__.APP_SYSTEM_ID;
//       } else {
//         parts[0] += '?app_system_id=' + window.__INITIAL_STATE__.APP_SYSTEM_ID;
//       }
//       return parts.join('');
//       // return window.__INITIAL_STATE__.SYS_CONTEXT_PATH ? window.__INITIAL_STATE__.SYS_CONTEXT_PATH + url : url;
//     }

//     const windowOpen = window.open;
//     window.open = function () {
//       let args = Array.from(arguments);
//       let url = arguments[0], idx = url.indexOf('app_system_id=');
//       if (idx == -1) {
//         args[0] = appendAppSystemId(url);
//       }
//       windowOpen.apply(window, args);
//     }


//   })();
// }
