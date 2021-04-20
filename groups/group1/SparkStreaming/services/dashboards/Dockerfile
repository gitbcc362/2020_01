FROM python:3.8

WORKDIR /usr/src/flask
COPY . /usr/src/flask

RUN pip install flask==1.1.2

ENV SAMPLE_HOST_NAME "twitter_client"
ENV SAMPLE_HOST_PORT 5009
ENV SAMPLE_HOST_IP "0.0.0.0"
CMD ["python", "app.py"]