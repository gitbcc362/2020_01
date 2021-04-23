import Project from '../schemas/Project';

class MembersController {
  async index(req, res) {
    const members = await Project.find();
    return res.json(members);
  }

  async store(req, res) {
    try {
      const { email } = req.body;
      if (!req.email) return res.status(400).json({ message: 'Usuário não cadastrado' });
      const project = await Project.findById(req.params.id);
      if(!project) return res.status(400).json({ message: 'O Projeto informado não está cadastrado' });
      project.members = [...project.members , email];
      await project.save();
      return res.json(project);
    } catch (error) {
      
    }
  }
}

export default new MembersController();
