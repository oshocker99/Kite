FROM  python:3.6

EXPOSE 5000

WORKDIR /opt

RUN apt-get update && apt-get upgrade -y

ADD requirements.txt /tmp/

RUN pip install -r /tmp/requirements.txt

# CMD ["pip", "install", "-e", "/opt"]