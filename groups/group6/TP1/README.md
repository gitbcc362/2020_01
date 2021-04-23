# Pub/Sub – Redis

## Redis

<p align="left">
    <img src="../.github/redisLogo.png" width="300px">
</p>

### O que é o Redis?

O redis é um middleware que se destaca por permitir stream de dados podendo usar uma diversidade de estruturas (hash, list, bitmaps, etc.)

### Quem está usando?

<p align="left">
    <img src="../.github/redisPlayers.png" width="450px">
</p>

### Distributed caching

Além disso, ele usa um dataset em memória principal simulando as lógicas de gerenciamento de cache por motivos de performance. Por isso, todas as operações são feitas após os dados em questão estarem na memória principal do database e assim obter o “cache hit”.

<p align="left">
    <img src="../.github/distributedCaching.gif" width="450px">
</p>

### Componente Pub/Sub

O Redis possui um componente de publisher-subscriber, onde não é necessário programar para quem o dado será enviado, apenas é especificado um canal, no qual um ou mais subscribers podem se inscrever e receber os dados via broadcast, que por sua vez foram publicados pelo publisher no canal em questão

<p align="left">
    <img src="../.github/redisPubSub.gif" width="600px">
</p>

1. Todos os clientes estão desconectados.
1. O Subscriber 1 se inscreve em um canal X, e se esse canal não existir, ele é criado
1. Subscriber 3 se inscreve no canal Y, como ele não existe, é criado também
1. É possível que vários subscribers diferentes se inscreverem no mesmo canal. No caso os Subscribers 2 e 3 se inscrevem no canal Y
1. Publisher 1 publica no canal X a string “abc”, e portanto Subscriber 1 que está inscrito nesse canal recebe a mensagem “abc”
1. Publisher 2 publica a mensagem “def” no canal Y e os Subscribers 2 e 3 recebem ela, pois estão escutando Y


<p align="left">
    <img src="../.github/exampleTP1.png" width="600px">
</p>

> Vale ressaltar também que o publisher também pode publicar em dois canais diferentes. No caso abaixo, publisher 1 publica “abc” no canal Z, que por sua vez tem o Subscriber 2 como inscrito



## Aplicação

A aplicação feita é feita em nodejs e busca contar a ocorrência de todas as palavras da bíblia utilizando conceitos de paralelismo (pipeline) utilizando Sistemas Distribuídos via Redis pub/sub e Docker Swarm.

<p align="left">
    <img src="../.github/technologiesTP1.png" width="450px">
</p>

### Pipeline

Apesar de relativamente simples, é possível aplicar diversas camadas de paralelismo na aplicação, a subdividindo em vários steps (ou pipes) que são executados de maneira assíncrona.

Uma vez esquematizado o pipeline, fica fácil enxergar o paralelismo na aplicação. A ideia é usar o Redis pub/sub como intermédio entre esses pipes, onde serão tratados como micro-serviços dentro de cada máquina.

<p align="left">
    <img src="../.github/app1TP1.png" width="600px">
</p>

Vários pipelines podem ser executados simultaneamente utilizando indexação roundRobin. Isso permite que haja paralelismo entre os passos, uma vez que são assíncronos. 

<p align="left">
    <img src="../.github/app2TP1.png" width="400px">
</p>

> Exemplo do nome de um canal: term_clean_1

A princípio, devido ao pub/sub e sua natureza de broadcasting, a escalabilidade para aplicações de pipeline acaba sendo prejudicada devido ao fato de haver processamento de dados repetido, caso mais de uma máquina esteja acessando o canal.

<p align="left">
    <img src="../.github/app3TP1.png" width="250px">
</p>

Uma possível solução que pensamos para permitir mais de uma máquina por pipe, é indexar um canal para cada máquina no próximo step, passando a responsabilidade do gerenciamento de envio para o producer, e cabe a ele garantir que não serão publicados dados repetidos para máquinas diferentes.

<p align="left">
    <img src="../.github/app4TP1.png" width="250px">
</p>
