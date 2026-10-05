#!/usr/bin/env bash
# Criacao dos recursos via Azure CLI. Alternativa ao portal.
# A senha do SQL entra so como variavel de ambiente, nunca commitada.
set -euo pipefail

LOCATION="${LOCATION:-eastus2}"
RESOURCE_GROUP="${RESOURCE_GROUP:-rg-dimdimwebapp}"
SQL_SERVER="${SQL_SERVER:-sql-dimdim-556460}"
SQL_DB="${SQL_DB:-dimdim}"
SQL_ADMIN="${SQL_ADMIN:-dimdimadmin}"
SQL_PASSWORD="${SQL_PASSWORD:?defina SQL_PASSWORD}"
PLAN_NAME="${PLAN_NAME:-plan-dimdim}"
WEBAPP_NAME="${WEBAPP_NAME:-app-dimdim-556460}"
INSIGHTS_NAME="${INSIGHTS_NAME:-ai-dimdim}"

az group create --name "$RESOURCE_GROUP" --location "$LOCATION"

az sql server create \
  --name "$SQL_SERVER" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --admin-user "$SQL_ADMIN" \
  --admin-password "$SQL_PASSWORD"

az sql server firewall-rule create \
  --resource-group "$RESOURCE_GROUP" \
  --server "$SQL_SERVER" \
  --name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0

az sql db create \
  --resource-group "$RESOURCE_GROUP" \
  --server "$SQL_SERVER" \
  --name "$SQL_DB" \
  --service-objective Basic \
  --backup-storage-redundancy Local

az appservice plan create \
  --name "$PLAN_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --sku B1 \
  --is-linux

az webapp create \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --plan "$PLAN_NAME" \
  --runtime "JAVA:17-java17"

az monitor app-insights component create \
  --app "$INSIGHTS_NAME" \
  --location "$LOCATION" \
  --resource-group "$RESOURCE_GROUP" \
  --application-type web

CONN=$(az monitor app-insights component show \
  --app "$INSIGHTS_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --query connectionString -o tsv)

DB_URL="jdbc:sqlserver://${SQL_SERVER}.database.windows.net:1433;database=${SQL_DB};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

az webapp config appsettings set \
  --resource-group "$RESOURCE_GROUP" \
  --name "$WEBAPP_NAME" \
  --settings \
    DB_URL="$DB_URL" \
    DB_USER="${SQL_ADMIN}" \
    DB_PASSWORD="$SQL_PASSWORD" \
    APPLICATIONINSIGHTS_CONNECTION_STRING="$CONN" \
    WEBSITES_PORT=8080
