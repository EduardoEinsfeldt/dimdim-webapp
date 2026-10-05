#!/usr/bin/env bash
# Deploy automatizado com Azure CLI (az webapp deploy).
# Preencha as variaveis. Nao coloque senha neste arquivo.
set -euo pipefail

RESOURCE_GROUP="${RESOURCE_GROUP:-rg-dimdimwebapp}"
WEBAPP_NAME="${WEBAPP_NAME:-app-dimdim-556460}"
JAR_PATH="${JAR_PATH:-target/dimdim.jar}"

az account show >/dev/null

echo "Build do JAR"
mvn -q clean package -DskipTests

echo "Deploy do JAR no App Service"
az webapp deploy \
  --resource-group "$RESOURCE_GROUP" \
  --name "$WEBAPP_NAME" \
  --src-path "$JAR_PATH" \
  --type jar \
  --async false

echo "URL:"
az webapp show --resource-group "$RESOURCE_GROUP" --name "$WEBAPP_NAME" --query defaultHostName -o tsv
