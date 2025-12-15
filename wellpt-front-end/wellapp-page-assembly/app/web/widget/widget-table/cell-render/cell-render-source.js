const requireComponent = require.context('./', true, /\w+-render\.vue$/);

let sourceContext = {}

requireComponent.keys().forEach(filePath => {
  let comp = requireComponent(filePath).default;
  const fileName = filePath.replace(/^.\//, '');
  // const raw = require(`./${fileName}?raw`).default;
  const raw = require(`!!raw-loader!./${fileName}`).default;

  sourceContext[comp.name] = raw;
});

export default sourceContext;
