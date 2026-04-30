const { Database } = require('../Database');
const { v4: uuidv4 } = require('uuid');

const usersDB = Database.getInstance('users');

exports.signup = (req, res) => {
  try {
    const { fullName, email, password } = req.body;

    if (!fullName || !email || !password) {
      return res.status(400).json({ message: 'Missing fields' });
    }

    if (!email.endsWith('@student.sdu.dk')) {
      return res.status(400).json({
        message: 'Only SDU students can create an account'
      });
    }

    if (usersDB.select({ email }).length > 0) {
      return res.status(400).json({ message: 'User already exists' });
    }

    const newUser = {
      id: uuidv4(),
      fullName,
      email,
      password,
      profile_picture: null
    };

    usersDB.insert(newUser);

    return res.status(201).json({
      message: 'User created successfully',
      user: newUser
    });
  } catch (err) {
    return res.status(500).json({ message: 'Server error' });
  }
};

exports.login = (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ message: 'Missing credentials' });
  }

  const user = usersDB.select({ email, password })[0];

  if (!user) {
    return res.status(401).json({ message: 'Invalid credentials' });
  }

  return res.json(user);
};