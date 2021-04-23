import {Router} from "express";
import Member from "./controllers/Member";
import Project from './controllers/Project';
import Task from "./controllers/Task";
import authMiddleware from './middlewares/auth';
const router = Router();

router.get("/", (req,res)=>{
  console.log('Project: connected server')
});

router.get("/projects", Project.index);
router.get("/projects/:id", Project.show);

router.use(authMiddleware);
router.post("/projects", Project.store);
router.delete("/projects/:id", Project.destroy);

router.post("/project/:id/members", Member.store);
router.post("/project/:id/tasks", Task.store);

export default router;
