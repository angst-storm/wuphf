.PHONY: up down db wait-db init-db install-common run run-ums run-messages stop build test clean

UMS_PORT := 9000
MESSAGES_PORT := 9001

up: db wait-db init-db run

db:
	docker compose up -d

wait-db:
	@until docker compose exec -T postgres pg_isready -U postgres -d postgres >/dev/null 2>&1; do sleep 1; done
	@echo "Postgres is ready"

init-db:
	docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d postgres < db/init/00_databases.sql
	docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d postgres < db/init/01_ums.sql
	docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U postgres -d postgres < db/init/02_messages.sql

install-common:
	mvn -q -pl common -am -DskipTests install

run-ums: install-common
	mvn -pl ums spring-boot:run

run-messages: install-common
	mvn -pl messages spring-boot:run

run: install-common
	@trap 'kill 0' EXIT INT TERM; \
		mvn -pl ums spring-boot:run & \
		mvn -pl messages spring-boot:run & \
		wait

build:
	mvn -DskipTests package

test:
	mvn test

stop:
	-@lsof -ti tcp:$(UMS_PORT) | xargs kill 2>/dev/null || true
	-@lsof -ti tcp:$(MESSAGES_PORT) | xargs kill 2>/dev/null || true

down: stop
	docker compose down

clean: down
	mvn -q clean
