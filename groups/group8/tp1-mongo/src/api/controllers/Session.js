import User from '../schemas/User';


class SessionController {
  async store(req, res) {
    const user = await User.findOne({ email: req.body.email });
    if (!user) return res.status(400).json({ message: 'Usuário não existe' });

    if (!await user.checkPassword(req.body.password)) {
      return res.status(401).json({ message: 'Senha incorreta' });
    }
    return res.json({
      user,
      token: User.generateToken(user),
    });
  }
}

export default new SessionController();
