# DimDim

Web app de contas e transacoes para o 2o Checkpoint (DevOps Tools e Cloud Computing).

Nao e o projeto da Sprint 3. Frontend em Thymeleaf, nao e API pura.

## O que faz

- Conta: criar, listar, atualizar e excluir.
- Transacao: criar, listar, atualizar e excluir, ligada a uma conta (1:N).
- Persistencia em Azure SQL Database (PaaS, nao container).
- Deploy por Azure CLI (`az webapp deploy`).
- Monitoracao por Application Insights.

## Arquitetura

Usuario no navegador -> Azure App Service (Java 17, Spring Boot) -> Azure SQL (`conta` 1:N `transacao`). Application Insights coleta as requisicoes do App Service. Desenho em `docs/arquitetura.svg`.

Segredos (usuario, senha, connection string) ficam apenas em App Settings, nao no codigo.
