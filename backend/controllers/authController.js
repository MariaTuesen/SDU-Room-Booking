const User = require('../models/User');

exports.signup = (req, res) => {
    const { fullName, email, password } = req.body;

    if (User.findByEmail(email)) {
        return res.status(400).json({ message: 'User already exists' });
    }

    const user = new User(fullName, email, password);
    user.save();

    res.status(201).json({ message: 'Account created successfully' });
};

exports.login = (req, res) => {
    const { email, password } = req.body;

    const user = User.findByEmail(email);

    if (!user || user.password !== password) {
        return res.status(401).json({ message: 'Invalid credentials' });
    }

    res.json({ message: 'Login successful', user });
};