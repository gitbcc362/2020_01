import mongoose from 'mongoose';

const Project = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  tasks: {
    type: Array,
    required: true,
  },
  members: {
    type: Array,
    required: true,
  },
});

export default mongoose.model('Project', Project);
