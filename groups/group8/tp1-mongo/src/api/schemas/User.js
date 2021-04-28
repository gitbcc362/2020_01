import mongoose from 'mongoose';
import jwt from 'jsonwebtoken';
import bcrypt from 'bcrypt';
import authConfig from '../config/auth';

const User = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  email: {
    type: String,
    default: 0,
    required: true,
  },
  password: {
    type: String,
    default: 0,
    required: true,
  },
});

User.pre('save', async function (next) {
  if (!this.isModified('password')) {
    return next()
  }
  this.password = await bcrypt.hash(this.password, 8)
})

User.methods = {
  checkPassword(password) {
    return bcrypt.compare(password, this.password)
  }
}
  
User.statics = {
  generateToken ({ email }) {
    return jwt.sign({ email }, authConfig.secret, {
      expiresIn: authConfig.ttl
    })
  }
}

export default mongoose.model('User', User);
