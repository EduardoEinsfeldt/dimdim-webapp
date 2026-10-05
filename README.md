# DimDim

Web app de contas e transacoes do 2o Checkpoint, disciplina DevOps Tools & Cloud Computing (FIAP). Nao e o projeto da Sprint 3.

Frontend em Thymeleaf (nao e API pura), persistencia em Azure SQL Database (PaaS, nao containerizado), deploy automatizado com Azure CLI (`az webapp deploy`) e monitoracao com Application Insights.

Integrante: Eduardo Augusto Pelegrino Einsfeldt - RM 556460.


## Descricao da solucao

O DimDim controla contas e lancamentos. O usuario cria, lista, altera e exclui contas. Cada transacao pertence a uma conta. O navegador fala com o App Service. O App Service grava no Azure SQL. O Application Insights coleta as requisicoes.

Tabelas, relacionamento 1:N:

- `conta`: id, nome, tipo, saldo
- `transacao`: id, conta_id (FK para conta), descricao, valor, tipo, data_transacao

CRUD das duas telas:

- Conta: criar, listar, atualizar, excluir
- Transacao: criar, listar, atualizar, excluir

Segredos (usuario, senha, connection string) ficam apenas em App Settings. Nao ha senha no codigo fonte.

## Arquitetura

Desenho macro: [docs/arquitetura.svg](docs/arquitetura.svg)

Usuario (navegador) -> Azure App Service Linux, Java 17 (Spring Boot + Thymeleaf) -> Azure SQL Database (conta 1:N transacao). Application Insights no App Service. Deploy por `az webapp deploy`. DDL em `scripts/ddl.sql`.

## Onde esta cada item pedido

| Item | Onde |
| --- | --- |
| Descricao da solucao | este README |
| Desenho macro da arquitetura | `docs/arquitetura.svg` |
| DDL das tabelas | `scripts/ddl.sql` |
| Scripts do CLI | `scripts/criar-recursos-cli.sh` e `scripts/deploy-cli.sh` |
| Codigo fonte | `src/` e `pom.xml` |
| How to de implantacao | este README |
| JSON das operacoes GET, POST, PUT e DELETE | `docs/operacoes.json` |
| Link do video | no topo deste README e no PDF |

## Recursos usados

Regiao de todos os recursos: `eastus2` .

| Recurso | Nome                                          |
| --- |-----------------------------------------------|
| Resource group | `rg-dimdimwebapp`                             |
| Azure SQL Server | `sql-dimdim-556460`                           |
| Azure SQL Database | `dimdim`                                      |
| Login SQL | `dimdimadmin`                                 |
| Plano App Service | `plan-dimdim` (Linux, B1)                     |
| Web App | `app-dimdim-556460` (Java 17, Java SE)        |
| Application Insights | `ai-dimdim`                                   |
| URL | `https://app-dimdim-556460.azurewebsites.net` |

## How to: implantacao na nuvem

Pode ser feito no Cloud Shell do portal (icone `>_`) ou num PC com Azure CLI, JDK 17 e Maven. O deploy tem que ser o comando `az webapp deploy`. Nao use o botao de upload do portal.

### 1. Conferir a conta antes de criar

az account show --query "{nome:name, id:id, estado:state}" -o table
java -version
mvn -version
az webapp list-runtimes --os linux | grep JAVA:17

A assinatura precisa estar Enabled. Se houver mais de uma, selecione a de estudante com `az account set --subscription "NOME-DA-ASSINATURA"`. A policy Allowed resource deployment regions precisa incluir `eastus2`.

### 2. Subir o projeto

No Cloud Shell, envie o zip pelo icone de upload e descompacte. No PC: clone o repositorio [URL-DO-REPOSITORIO] e entre na pasta `dimdim-webapp`.

### 3. Definir a senha fora do arquivo

Nao edite o script para colocar a senha. A senha precisa ter 8 ou mais caracteres, maiuscula, minuscula, numero e simbolo, e nao pode conter `dimdimadmin`.

export SQL_PASSWORD='TROQUE-POR-UMA-SENHA-FORTE'
export LOCATION='eastus2'
export RESOURCE_GROUP='rg-dimdimwebapp'
export SQL_SERVER='sql-dimdim-556460'
export WEBAPP_NAME='app-dimdim-556460'

### 4. Criar os recursos com o CLI

bash scripts/criar-recursos-cli.sh

O script cria, nesta ordem: resource group, Azure SQL Server, regra AllowAzureServices, database `dimdim` (Basic, PaaS), plano Linux B1, Web App Java 17, Application Insights e App Settings `DB_URL`, `DB_USER`, `DB_PASSWORD`, `APPLICATIONINSIGHTS_CONNECTION_STRING`, `WEBSITES_PORT`. Nao rode de novo se o resource group ja existir.

### 5. Criar as tabelas

No portal: SQL database `dimdim`, Query editor, login `dimdimadmin`. Cole e execute `scripts/ddl.sql`. Confira com `SELECT * FROM dbo.conta;` e `SELECT * FROM dbo.transacao;`. A coluna `transacao.conta_id` referencia `conta.id`.

### 6. Deploy automatizado

bash scripts/deploy-cli.sh

O script roda `mvn clean package -DskipTests` e publica `target/dimdim.jar` com `az webapp deploy --resource-group rg-dimdimwebapp --name app-dimdim-556460 --src-path target/dimdim.jar --type jar`. A primeira subida do Java leva 1 a 2 minutos.

Se abrir a pagina padrao da Azure, no App Service, Configuration, General settings, Startup Command: `java -jar /home/site/wwwroot/dimdim.jar`. Salve e reinicie.

### 7. Teste com persistencia

Abra `https://app-dimdim-556460.azurewebsites.net`.

Conta: criar, rodar `SELECT * FROM dbo.conta;`, alterar e rodar o SELECT de novo, excluir por ultimo. Transacao: abrir Transacoes, criar, rodar `SELECT * FROM dbo.transacao;` (com `conta_id` preenchido), alterar, SELECT de novo, excluir, SELECT de novo.

### 8. Monitoracao

Application Insights `ai-dimdim`, Transaction search ou Live metrics. Navegue de novo no site. As requisicoes precisam aparecer. Se a lista continuar vazia, no App Service ligue Application Insights, confirme a connection string, reinicie e gere novo trafego.

## Operacoes HTTP

Arquivo: `docs/operacoes.json`. O frontend usa POST nos formularios. O JSON registra o equivalente GET, POST, PUT e DELETE.

Conta: GET `/`, POST `/contas`, PUT `/contas/{id}`, DELETE `/contas/{id}/excluir`.
Transacao: GET `/contas/{contaId}/transacoes`, POST `/transacoes`, PUT `/transacoes/{id}`, DELETE `/transacoes/{id}/excluir`.

