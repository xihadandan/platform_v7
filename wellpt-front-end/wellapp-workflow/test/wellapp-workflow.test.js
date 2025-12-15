'use strict';

const mock = require('egg-mock');

describe('test/wellapp-workflow.test.js', () => {
  let app;
  before(() => {
    app = mock.app({
      baseDir: 'apps/wellapp-workflow-test',
    });
    return app.ready();
  });

  after(() => app.close());
  afterEach(mock.restore);

  it('should GET /', () => {
    return app.httpRequest()
      .get('/')
      .expect('hi, wellappWorkflow')
      .expect(200);
  });
});
