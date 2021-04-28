'use strict';

const listaDeSocketsDeBrokers = new Map();

const customProperties = {
  toList() {
    const lista = [];

    for (const [, obj] of listaDeSocketsDeBrokers.entries()) {
      const { port, host } = obj;

      lista.push({ port, host });
    }

    return lista;
  },
};

module.exports = {
  listaDeSocketsDeBrokers: Object.assign(listaDeSocketsDeBrokers, customProperties),
};
