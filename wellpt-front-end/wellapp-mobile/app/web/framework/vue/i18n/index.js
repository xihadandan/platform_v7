const context = require.context('./', false, /[a-z0-9-_]+\.js$/i);
export default context
  .keys()
  .filter(key => key != './index.js')
  .map(key => ({ key, locale: key.match(/[a-z0-9-_]+/i)[0] }))
  .reduce((prev, { key, locale }) => {
    prev[locale] = context(key).default;
    return prev;
  }, {});
