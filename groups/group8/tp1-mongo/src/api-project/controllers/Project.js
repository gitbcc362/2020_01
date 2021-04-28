import Project from '../schemas/Project';

class ProjectController {
  async index(req, res) {
    const projects = await Project.find();
    return res.json(projects);
  }

  async store(req, res) {
    try {
      if (!req.email) return res.status(400).json({ message: 'Usuário não cadastrado' });
      const project = await Project.create({...req.body, members:[req.email]});
      return res.json(project);
    } catch (error) {
      return res.status(500).json(error);
    }
  }

  async show(req, res) {
    const id = req.params.id;
    if (!id) return res.status(400).json({ message: 'Informe o id do projeto' });
    const project = await Project.findById(id);
    return res.json(project);
  }
  async destroy(req, res) {
    const id = req.params.id;
    if (!id) return res.status(400).json({ message: 'Informe o id do projeto' });
    const project = await Project.findByIdAndRemove(id);
    return res.json(project);
  }
}

export default new ProjectController();
