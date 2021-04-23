import {Router} from "express";
import User from './controllers/User'
import Session from './controllers/Session'
const router = Router();

router.get("/", (req,res)=>{
  console.log('connected server')
});

router.get("/users", User.index);
router.post("/users", User.store);
router.put("/users/:id", User.update);
router.delete("/users/:id", User.destroy);

router.post("/session", Session.store);

export default router;
