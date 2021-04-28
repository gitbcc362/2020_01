'use strict';

const fs = require('fs');
const path = require('path');

exports.getJsonsFromFile = function getJsonsFromFile() {
  const dirPath = path.join('node_modules','@joaoderocha', 'redis-pub-sub-nodejs', 'resources', 'biblia_acf');

  return fs.readdirSync(dirPath).map((filePath) => path.join('resources','biblia_acf',filePath));
};
