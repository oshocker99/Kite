FROM python:3.7-alpine
LABEL author="Stephen Onnen"

WORKDIR /opt

ADD Chat/ /opt

RUN \
    apk add --no-cache --virtual .build-deps libffi-dev gcc musl-dev make && \
    python3 -m pip install -r requirements.txt --no-cache-dir && \
    apk --purge del .build-deps

EXPOSE 5500 

CMD ["python", "chat.py"]