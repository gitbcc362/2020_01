import express, { urlencoded } from "express";
import cors from 'cors';
import router from "./routes";
import mongoose from "mongoose";
import Project from "./schemas/Project";
import Mail from './lib/Mail';

mongoose.connect('mongodb://mongo-rs0-1,mongo-rs0-2,mongo-rs0-3/test', {
  useNewUrlParser: true,
  useFindAndModify: true,
  useUnifiedTopology: true,
}).then(res => {
  console.log('MongoDB - Task Conectado!!')
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

const changeStreamUser = Project.watch();
changeStreamUser.on('change', async change => {
  console.log(change)
  if(change.operationType === 'update' && change.updateDescription.updatedFields.members){
    const email = change.updateDescription.updatedFields.members.pop()
    console.log(`
    Você foi adicionado em um novo projeto!
    Clique no link para ver os detalhes desse projeto: http://34.67.246.251:3434/projects/${change.documentKey._id}
    `)
    try {
      await Mail.sendMail({
        to: email,
        subject:'Você foi adicionado um novo projeto',
        text:`
          Você foi adicionado em um novo projeto!
          Clique no link para ver os detalhes desse projeto: http://34.67.246.251:3434/projects/${change.documentKey._id}
          `
      })
    } catch (error) {
      console.warn(error)
    }
  }
});

export default app;
