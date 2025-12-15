'use strict';

const mock = require('egg-mock');

describe('test/wellapp-test.test.js', () => {
  let app;
  before(() => {
    app = mock.app({
      baseDir: 'apps/wellapp-test-test'
    });
    return app.ready();
  });

  after(() => app.close());
  afterEach(mock.restore);

  it('should GET /', () => {
    return app.httpRequest().get('/').expect('hi, wellappTest').expect(200);
  });
});
