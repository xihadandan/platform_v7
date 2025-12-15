'use strict';

const mock = require('egg-mock');

describe('test/wellapp-dyform.test.js', () => {
  let app;
  before(() => {
    app = mock.app({
      baseDir: 'apps/wellapp-dyform-test',
    });
    return app.ready();
  });

  after(() => app.close());
  afterEach(mock.restore);

  it('should GET /', () => {
    return app.httpRequest()
      .get('/')
      .expect('hi, wellappDyform')
      .expect(200);
  });
});
