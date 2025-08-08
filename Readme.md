# Sumário

- [Introdução & Arquitetura](#introducao-arquitetura)
- [Tecnologias](#tecnologias)
- [Plano de trabalho realizado vs previsto](#plano-de-trabalho-realizado-vs-previsto)
- [Diagramas](#diagramas)
- [Rodando local com docker](#rodando-local-com-docker)
- [Docker hub](#docker-hub)


# Introdução & Arquitetura

---
Este projeto utiliza **Java** com **Spring** (Boot, Data JPA, REST) e **PostgreSQL**, escolhas alinhadas com a descrição
do dia a dia do time.

A arquitetura segue **DDD** de forma simplificada. Em ambos os módulos (api e service), existe um package `domain`em
cada aplicação que é organizado por produto. Para cada produto, há um package próprio que reúne as
implementações relacionadas: controllers, entidades, DTOs, services, etc.

No package `infrastructure`, ficam os elementos que não pertencem a um produto ou regra de negócio, como configurações,
banco de dados e mensageria/filas.

O package de `application` não foi utilizado, com o objetivo de simplificar o projeto e acelerar o desenvolvimento.

**Obs.**: O módulo de domain existe para reutilização: concentra entidades, repositórios e migrações de banco de dados
usados pelo serviço e api.

Quanto aos testes, optei exclusivamente por cenários end-to-end (E2E) com Testcontainers para validar o funcionamento de
ponta a ponta das aplicações. A ideia foi cobrir tudo de forma concreta e rápida, com um conjunto pequeno de testes
executados em ambiente isolado e próximo do real, priorizando agilidade e qualidade.


---

# Tecnologias

___

- Java 21
- Spring Boot 3.5.4
- Spring Data JPA
- Spring Web com Undertow (Tomcat excluído)
- Spring Validation
- Spring Actuator
- Spring AMQP (RabbitMQ)
- Liquibase
- PostgreSQL
- springdoc-openapi (Swagger UI) – 2.7.0
- Testes:
    - JUnit 5
    - Spring Boot Test
    - Testcontainers (PostgreSQL, RabbitMQ, JUnit Jupiter)
    - Spring AMQP Test
    - Awaitility 4.2.0
- Hibernate ORM e Jakarta Persistence API
- Gradle com Spring Dependency Management (1.1.7)
- Docker e Docker Compose
- IDE IntelliJ IDEA Ultimate
- Imagens Docker para build e runtime:
    - eclipse-temurin:21-jdk (builder)
    - gcr.io/distroless/java21-debian12 (runtime)

___

# Plano de trabalho realizado vs previsto

___

O prazo previsto para concluir o desafio era de 13,5 horas; a execução foi finalizada em 11,3 horas.

Isso ocorreu porque o tempo previsto estava superestimado para as etapas abaixo, o que permitiu antecipar:

- Estrutura do projeto
- Docker + Liquibase
- Consumidor RabbitMQ
- API REST
- Diagramas
- Entrega final

Por outro lado, os Testes E2E demandaram 0,3 h a mais, devido à necessidade de ajustes de configuração, refatoração dos
e validações adicionais para garantir o funcionamento correto do fluxo ponta a ponta.

___

# Diagramas

___

[AWS](https://github.com/lczago/microservices-order-challenge/blob/main/aws_diagram.png)

[Relacionamento Postgres](https://github.com/lczago/microservices-order-challenge/blob/main/database_diagram.png)

___

# Rodando local com docker

___

1. certifique-se de que o arquivo .env esteja preenchido corretamente.
2. Após isso, rodar o comando abaixo.
    ```shell
    docker compose up -d --build
    ```
3. Acesse: [RabbitMq management](http://localhost:15672/)
    1. Faça o login com usuário e password, ambos sendo o valor: guest
4. Acesse a aba "Queues and Streams" e clique em order_queue na coluna de "Name"
5. Acesse publish message e envie uma mensagem, conforme exemplo abaixo.
    ```json
    {
      "codigoPedido": 1001,
      "codigoCliente": 1,
      "itens": [
        {
          "produto": "lápis",
          "quantidade": 100,
          "preco": 1.10
        },
        {
          "produto": "caderno",
          "quantidade": 10,
          "preco": 1.00
        }    
      ]
    }
    ```
6. Acesse o swagger e realize as requisições que desejar: [Swagger Api](http://localhost:8080/swagger-ui/index.html)

___

# Docker hub

___

[api image](https://hub.docker.com/r/lczago/orders-api)

[service image](https://hub.docker.com/r/lczago/orders-service)

___