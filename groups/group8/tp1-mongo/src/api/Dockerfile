FROM node
RUN mkdir -p /usr/src/api/
WORKDIR /usr/src/api/

COPY package*.json .

RUN yarn install

COPY . .

EXPOSE 3333

CMD ['yarn', 'dev']
