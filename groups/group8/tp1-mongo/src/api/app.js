import express, { urlencoded } from "express";
import cors from 'cors';
import router from "./routes";
import mongoose from "mongoose";
import User from "./schemas/User";
import Mail from "./lib/Mail";

mongoose.connect('mongodb://mongo-rs0-1,mongo-rs0-2,mongo-rs0-3/test', {
  useNewUrlParser: true,
  useFindAndModify: true,
  useUnifiedTopology: true,
}).then(res => {
  console.log('MongoDB Conectado!!')
}).catch(error => {
  console.log(error);
});

const app = express();
app.use(cors());
app.use(express.json());
app.use(urlencoded({extended:true}))
app.use(router);
app.use(async (err, req, res, next) => {
  console.warn(err)
  return res.status(500).json(err);
});

const changeStreamUser = User.watch();
changeStreamUser.on('change', async change => {
  if(change.operationType === 'insert'){
    const {name,email} = change.fullDocument;
    console.log(`
        Caro ${name},
        Seu cadastro foi realizado com sucesso em nossa plataforma!.
    `)
    try {
      await Mail.sendMail({
        to: email,
        subject:'Bem vindo a Plataforma!',
        text:`
          Caro ${name},
          Seu cadastro foi realizado com sucesso em nossa plataforma!.
          ` 
      })
    } catch (error) {
      console.warn(error)
    }
  }
});

// change
// api-auth       | {
// api-auth       |   _id: {
// api-auth       |     _data: Binary {
// api-auth       |       _bsontype: 'Binary',
// api-auth       |       sub_type: 0,
// api-auth       |       position: 49,
// api-auth       |       buffer: <Buffer 82 60 26 e9 9b 00 00 00 02 46 64 5f 69 64 00 64 60 26 e9 9b 28 fa 10 00 56 de 1b a2 00 5a 10 04 66 94 d1 1e 38 76 4c 3f bf a2 1d c8 e8 9f 22 8f 04>
// api-auth       |     }
// api-auth       |   },
// api-auth       |   operationType: 'insert',
// api-auth       |   fullDocument: {
// api-auth       |     _id: 6026e99b28fa100056de1ba2,
// api-auth       |     email: 'ribeiro@email.com',
// api-auth       |     password: '$2b$08$8ZazPH8o1SWwhRFyvrYW7O42esSJls6L3TjCXS7pB642d7kzCS8cC',
// api-auth       |     name: 'rogerd',
// api-auth       |     __v: 0
// api-auth       |   },
// api-auth       |   ns: { db: 'test', coll: 'users' },
// api-auth       |   documentKey: { _id: 6026e99b28fa100056de1ba2 }
// api-auth       | }

export default app;