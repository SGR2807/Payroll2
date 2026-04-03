#!/bin/bash

# Rebuild specific services: Jib docker build + force-recreate container
# Edit the SERVICES array below to choose which services to rebuild.
# Use the directory name (case-sensitive): configserver, eurekaserver, gatewayserver, Employee, leave, payroll

set -e

SERVICES=("payroll")

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
COMPOSE_DIR="$BASE_DIR/docker-compose"

# Map directory name to docker-compose service name
to_compose_name() {
  case "$1" in
    Employee) echo "employee" ;;
    *)        echo "$1" ;;
  esac
}

echo "============================================"
echo "  Rebuilding: ${SERVICES[*]}"
echo "============================================"

COMPOSE_NAMES=()

for service in "${SERVICES[@]}"; do
  echo ""
  echo "--------------------------------------------"
  echo "  Building: $service"
  echo "--------------------------------------------"
  cd "$BASE_DIR/$service"
  ./mvnw compile jib:dockerBuild -DskipTests -q
  echo "  ✓ $service image built"

  COMPOSE_NAMES+=("$(to_compose_name "$service")")
done

if [ ${#COMPOSE_NAMES[@]} -gt 0 ]; then
  echo ""
  echo "============================================"
  echo "  Recreating containers: ${COMPOSE_NAMES[*]}"
  echo "============================================"
  docker compose -f "$COMPOSE_DIR/docker-compose.yml" up -d --force-recreate "${COMPOSE_NAMES[@]}"
  echo ""
  echo "  ✓ Done. Follow logs with:"
  echo "    docker compose -f $COMPOSE_DIR/docker-compose.yml logs -f ${COMPOSE_NAMES[*]}"
fi
