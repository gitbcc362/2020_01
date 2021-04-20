'use strict';

exports.readBibleCap = function readBibleCap(filePath) {
  const jsonFile = require(`../../${filePath}`);

  return jsonFile.reduce((acc,capitulo) => {
    const versiculos = Object.values(capitulo);

    versiculos.forEach(frases => {
      Object.values(frases).forEach(frase => acc.push(frase));
    });

    return acc;
  } ,[]);
};
