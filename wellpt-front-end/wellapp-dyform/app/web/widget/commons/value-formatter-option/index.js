const requireComponent = require.context('./', true, /\w+-formatter-option\.vue$/);

let comps = {};

requireComponent.keys().map(fileName => {
  let comp = requireComponent(fileName).default;
  comps[comp.name] = comp;
});

export default comps;
