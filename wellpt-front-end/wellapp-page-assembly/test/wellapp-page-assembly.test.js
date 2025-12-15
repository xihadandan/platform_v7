'use strict';

const mock = require('egg-mock');

describe('test/wellapp-page-assembly.test.js', () => {
  let app;
  before(() => {
    app = mock.app({
      baseDir: 'apps/wellapp-page-assembly-test',
    });
    return app.ready();
  });

  after(() => app.close());
  afterEach(mock.restore);

  it('should GET /', () => {
    return app.httpRequest()
      .get('/')
      .expect('hi, wellappPageAssembly')
      .expect(200);
  });
});
