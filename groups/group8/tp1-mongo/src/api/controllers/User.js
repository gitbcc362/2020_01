import User from '../schemas/User';

class UserController {
  async index(req, res) {
    const users = await User.find();
    return res.json(users);
  }

  async store(req, res) {
    const { email } = req.body;
    const userExists = await User.findOne({ email });
    if (userExists) return res.status(400).json({ message: 'Usuário já cadastrado' });
    const user = await User.create(req.body);
    return res.json(user);
  }

  async update(req, res) {
    const { id } = req.params;
    const user = await User.findByIdAndUpdate(id,req.body)
    if (!user) return res.status(400).json({ message: 'Usuário não cadastrado' });
    return res.json(Object.assign(user,req.body));
  }

  async destroy(req, res) {
    const { id } = req.params;
    const user = await User.findByIdAndRemove(id)
    if (!user) return res.status(400).json({ message: 'Usuário não cadastrado' });
    return res.json(Object.assign(user,req.body));
  }
}

export default new UserController();
