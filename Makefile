.PHONY: up down db wait-db run stop build clean

UMS_DIR := ums
PORT := 9000

up: db wait-db run

db:
	docker compose up -d

wait-db:
	@until docker compose exec -T postgres pg_isready -U postgres -d postgres >/dev/null 2>&1; do sleep 1; done
	@echo "Postgres is ready"

run:
	cd $(UMS_DIR) && mvn spring-boot:run

build:
	cd $(UMS_DIR) && mvn -DskipTests package

stop:
	-@lsof -ti tcp:$(PORT) | xargs kill 2>/dev/null || true

down: stop
	docker compose down

clean: down
	cd $(UMS_DIR) && mvn -q clean
