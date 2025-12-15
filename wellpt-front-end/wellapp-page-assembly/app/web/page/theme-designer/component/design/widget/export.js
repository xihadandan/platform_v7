
const components = require.context('./', true, /\w+\.vue$/);
const comps = {};
components.keys().map(fileName => {
  let comp = components(fileName).default;
  comps[comp.name] = comp;
});

export {
  comps
};
