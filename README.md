# Docker commands

Bring up instance of api with `docker-compose up -d`


To bring down the instance run `docker-compose down`

Since the database will use volumes, the database data will persist between restarts of the containers. In order to delete the database run `docker-compose down -v`

`make dbup` then `./run-tests.sh` to run tests
`make dbdown` when done