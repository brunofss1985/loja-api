#!/bin/bash

# Detecta a branch atual
BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "📦 Branch atual: $BRANCH"

# Define profile com base na branch
if [ "$BRANCH" == "dev" ]; then
  PROFILE="dev"
elif [ "$BRANCH" == "test" ]; then
  PROFILE="test"
elif [ "$BRANCH" == "prod" ]; then
  PROFILE="prod"
  read -p "⚠️ Você está prestes a iniciar o ambiente de PRODUÇÃO. Continuar? (s/n): " confirm
  if [ "$confirm" != "s" ]; then
    echo "❌ Execução cancelada."
    exit 0
  fi
else
  echo "❌ Branch inválida. Use dev, test ou prod."
  exit 1
fi

# Compila se necessário
echo "🔧 Compilando aplicação com perfil: $PROFILE"
./mvnw clean package -DskipTests

# Encontra o JAR gerado
JAR_FILE=$(find target -name "*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
  echo "❌ JAR não encontrado! Compile com mvn package."
  exit 1
fi

# Inicia a aplicação
echo "🚀 Iniciando com profile: $PROFILE"
java -Dspring.profiles.active=$PROFILE -jar $JAR_FILE
