# Deploy na AWS com cloudformation

___

## Configurando AWS CLI

1. Download AWS CLI: https://aws.amazon.com/cli/
2. Configure com as credenciais e região ```aws configure```

## Stacks

### Bootstrap Stack

```shell
    aws cloudformation create-stack \
--stack-name orders-bootstrap-stack \
--template-body file://cloudformation/template/bootstrap.yml \
--parameters file://cloudformation/parameters/bootstrap.json \
--capabilities CAPABILITY_NAMED_IAM
```

### VPC Stack

```shell
    aws cloudformation create-stack \
--stack-name orders-vpc-stack \
--template-body file://cloudformation/template/vpc.yml \
--parameters file://cloudformation/parameters/vpc.json \
```

### Database Stack

```shell
    aws cloudformation create-stack \
--stack-name orders-database-stack \
--template-body file://cloudformation/template/database.yml \
--parameters file://cloudformation/parameters/database.json \
```

### App Stack

```shell
    aws cloudformation create-stack \
--stack-name orders-app-stack \
--template-body file://cloudformation/template/app.yml \
--parameters file://cloudformation/parameters/app.json \
```

### Api Gateway Stack

```shell
    aws cloudformation create-stack \
--stack-name orders-api-gateway-stack \
--template-body file://cloudformation/template/api-gateway.yml \
--parameters file://cloudformation/parameters/api-gateway.json \
```

### Testando deploy:

Ao finalizar a criação das stacks, obtenha o id do api gateway:

```shell
  aws apigateway get-rest-apis | jq -r '.items[0].id'
```

- substitua o api_gw_id com o valor do output do comando acima.
    - ```https://${api_gw_id}.execute-api.us-east-1.amazonaws.com/default/swagger-ui/index.html```

___