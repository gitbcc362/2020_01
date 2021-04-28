# Trabalho Prático 1

Este trabalho teve como objetivo apresentar a tecnologia mongodb. Para tanto, foram explorados 3 conceitos:
`Replica Sets, Sharding e micro-serviços`. O arquivo `mongo.pdf` contém a ideia geral desses três conceitos.
Para aplicar o conceito de Replica Sets e de micro-serviços, foi desenvolvida uma aplicação de gerenciamento 
de tarefas/projetos que permite que os usuários realizem um cadastro e se inscrevam ou compartilhem tarefas/projetos de
comum interesse. Para tanto, foram desenvolvidas duas apis independentes para simular a ideia de micro-serviços. 
A primeira api oferece o serviço de autenticação de usuários. Já a segunda, fornece o serviço para o gerenciamento
de tarefas/projetos desses usuários.   

### Dependências do projeto
Para compilar o código a primeira vez, é necessário instalar algumas dependências:

1. Portanto, é necessário executar o comando abaixo na raíz de cada uma das apis:  
  `docker-compose up`

### Executando
Uma vez que todas as dependências estejam instaladas corretamente é possível startar a aplicação.

2. Para isso, basta executar o comando abaixo na raíz de cada uma das apis:
  `yarn dev`

