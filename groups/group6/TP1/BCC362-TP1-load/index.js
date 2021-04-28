"use strict";

const {
  publish,
  redisClient,
} = require("@joaoderocha/redis-pub-sub-nodejs/headless/redis/services/pub-sub");
const {
  getJsonsFromFile,
  readBibleCap,
  getRoundRobinIndex,
  sleep,
  messageBuilder,
  getNextStep,
  LOAD,
} = require("@joaoderocha/redis-pub-sub-nodejs/headless/utils");

const loadStep = async () => {
  const filePaths = await getJsonsFromFile();
  let index = 0;

  for (const filePath of filePaths) {
    const linhas = await readBibleCap(filePath);

    for (const linha of linhas) {
      const roundRobinIndex = getRoundRobinIndex(index);

      const message = messageBuilder(linha, roundRobinIndex);

      console.log(getNextStep(`${LOAD}_${roundRobinIndex}`), message);

      publish(getNextStep(`${LOAD}_${roundRobinIndex}`), message);
      await sleep(1000);
      index += 1;
    }
  }
};

(async () => {
  while (true) {
    await loadStep();
  }
})();
