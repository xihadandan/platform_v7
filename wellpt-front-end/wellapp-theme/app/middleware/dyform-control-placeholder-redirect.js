'use strict';

module.exports = options => {
  const cache = {};

  const shouldChangeControls = {
    switch: true,
    radio: true,
    progress: true,
    fileupload4icon: true,
    fileupload: true,
    checkbox: true,
    color: true
  };

  return async function dyformControlPlaceholderRedirect(ctx, next) {
    const path = ctx.path;
    const staticPrefix = ctx.app.config.static.prefix;

    if (cache[path]) {
      const themeColor = ctx.cookies.get('themeColor') || 'default';
      ctx.path = ctx.path.replace('placeHolder.jpg', `placeHolder.${themeColor}.jpg`);
    } else if (typeof cache[path] === 'undefined') {
      if (path.startsWith(`${staticPrefix}/dyform/definition/ckeditor/plugins/control4`) && path.endsWith('/images/placeHolder.jpg')) {
        const ctlName = /control4(.+)\/images/.exec(path)[1];
        if (shouldChangeControls[ctlName]) {
          cache[path] = true;
          const themeColor = ctx.cookies.get('themeColor') || 'default';
          ctx.path = ctx.path.replace('placeHolder.jpg', `placeHolder.${themeColor}.jpg`);
        }
      } else {
        cache[path] = false;
      }
    }

    await next();
  };
};
