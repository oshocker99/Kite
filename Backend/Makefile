dbup:
	docker run --rm -d \
		--name test-db \
		-p 5432:5432 \
		-e "POSTGRES_USER=admin" \
		-e "POSTGRES_PASSWORD=pass" \
		-e "POSTGRES_DB=forum_db" \
		postgres:9.6-alpine

dbdown:
	docker kill test-db

up:
	docker-compose up -d

exec:
	docker-compose exec api bash

down:
	docker-compose down

run:
	flask run --host=0.0.0.0

clean:
	find . -name '*.pyc' -exec rm '{}' ';'
	find . -name '__pycache__' -type d -prune -exec rm -rf '{}' '+'
	find . -name '.pytest_cache' -type d -prune -exec rm -rf '{}' '+'

scrub: clean
	find . -name '*.egg-info' -type d -prune -exec rm -rf '{}' '+'
	rm -rf htmlcov
	rm -f .coverage

format:
	black kite tests
	isort -rc kite tests