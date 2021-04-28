const { promisify } = require('util');
const jwt = require('jsonwebtoken');
const authConfig = require('../config/auth');

module.exports = async (req, res, next) => {
  const authHeader = req.headers.authorization;

  if (!authHeader) {
    return res.status(401).json({ error: 'Token not provided' });
  }

  const [, token] = authHeader.split(' ');
  try {
    console.log(authHeader)
    console.log(token)
    const decoded = await promisify(jwt.verify)(token, 'adlsldfkjsdfkljsldkjfklsdfj');
    console.log(decoded)
    req.email = decoded.email;
    return next();
  } catch (err) {
    console.log(err)
    return res.status(401).json({ error: 'Token invalid' });
  }
};
