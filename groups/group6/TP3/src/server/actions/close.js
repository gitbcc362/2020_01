'use strict';

const fs = require('fs');

const { log } = require('../../utils');

exports.onClose = function onClose() {
  fs.writeFileSync('log-result.txt', log.toString());
};
