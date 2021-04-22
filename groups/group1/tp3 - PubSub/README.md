# Distributed PubSub

## Arquivos .JAR gerados:

1. broker.jar: corresponde a uma unidade de broker; cada broker pode possuir uma cópia.

```shell
    java -jar broker.jar <porta_broker>
```

2. app1.jar: exemplo simples de aplicação onde um cliente pede o acesso a um recurso, o utiliza, e libera o recurso.

```shell
    java -jar app1.jar <ip_broker> <porta_broker> <ip> <porta>
```

2. app2.jar: exemplo mais complicado da aplicação onde um cliente pede acesso a diversos recursos, escolhidos de forma aleatória, os utiliza e os libera.


```shell
    java -jar app2.jar <ip_broker> <porta_broker> <ip> <porta>
```