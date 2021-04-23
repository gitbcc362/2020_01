import Project from '../schemas/Project';

class TaskController {
  async index(req, res) {
    const projects = await Project.find();
    return res.json(projects);
  }

  async store(req, res) {
    try {
      const { name } = req.body;
      const id = req.params.id;
      if (!req.email) return res.status(400).json({ message: 'Usuário não cadastrado' });
      const project = await Project.findById(id);
      if(!project) return res.status(400).json({ message: 'O Projeto informado não está cadastrado' });
      project.tasks = [...project.tasks , name];
      await project.save();
      return res.json(project);
    } catch (error) {
      return res.status(500).json(error);
    }
  }
}

export default new TaskController();
