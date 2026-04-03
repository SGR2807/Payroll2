#!/bin/bash

# Build all microservice Docker images locally using Jib, then start with docker compose

set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
COMPOSE_DIR="$BASE_DIR/docker-compose"

# Services that need to be built with Jib (order doesn't matter for build)
SERVICES=("configserver" "eurekaserver" "gatewayserver" "Employee" "leave" "payroll")

echo "============================================"
echo "  Building & Running Payroll Microservices"
echo "============================================"

# Step 1: Build all service images with Jib
for service in "${SERVICES[@]}"; do
    echo ""
    echo "--------------------------------------------"
    echo "  Building: $service"
    echo "--------------------------------------------"
    cd "$BASE_DIR/$service"
    ./mvnw compile jib:dockerBuild -DskipTests -q
    echo "  ✓ $service built successfully"
done

echo ""
echo "============================================"
echo "  All images built. Starting containers..."
echo "============================================"

# Step 2: Stop existing containers and start fresh
cd "$COMPOSE_DIR"
docker compose down
docker compose up -d

echo ""
echo "============================================"
echo "  All containers started!"
echo "  Run 'docker compose -f $COMPOSE_DIR/docker-compose.yml logs -f' to follow logs"
echo "============================================"
