FROM node
RUN mkdir -p /usr/src/api-project/
WORKDIR /usr/src/api-project/

COPY package*.json .

RUN yarn install

COPY . .

EXPOSE 3434

CMD ['yarn', 'dev']
