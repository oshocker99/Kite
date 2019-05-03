# Backend Things

In order to run the backend API/database for testing, ensure you have Docker and docker compose installed on your machine.

Then in a terminal change to the `Backend` directory *where the docker-compose.yml* file is located.

Spin up an instance of the db and API server with:

`docker-compose up -d`

This will create a running API and database container, but will not start the API server itself. This is so that we can
view Stdout and see the debug output from the API server.

In order to run the API server, shell into the running API container with:

`docker-compose exec api bash`

Inside the api container install the required python libraries from the requirements.txt file with:

`pip install -r requirements.txt`

Now install the kite as a python module to properly register all the modules with:

`pip install -e .`

Now, inside the API's command line, run the following command to start the API server:

`flask run --host=0.0.0.0`

After you are finished bring down the instance with:

`docker-compose down`

Since we are not using volumes in the DB container for testing, all database changes will be lost when the containers are
stopped. This is useful when testing to get a clean database.

The API container is using a volume to mount the Backend directory into the container. This allows any changes to the API's modules to immediatly reflect in the API. Usually a change to a .py file will automatically restart the Flask applicaiton.

Tear everything down with:

`docker-compose down`

### Optimize Imports


`isort -rc .`
