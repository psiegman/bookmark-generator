FROM python:3

WORKDIR /tmp/test

COPY . .

RUN pip3 install -r requirements.txt

CMD python3 bookmark_generator.py example.yml
