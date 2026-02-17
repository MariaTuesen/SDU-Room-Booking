const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { Database } = require('./Database');
const { v4: uuidv4 } = require('uuid');

const app = express();
app.use(cors());
app.use(bodyParser.json());

const usersDB = Database.getInstance('users');

//Create account
app.post('/auth/signup', (req, res) => {
    try {
        const { fullName, email, password } = req.body;

        if (!fullName || !email || !password) {
            return res.status(400).json({ message: 'Missing fields' });
        }

        if (!email.endsWith("@student.sdu.dk")) {
            console.log("Rejected signup: Not SDU email", email);
            return res.status(400).json({
                message: "Only SDU students can create an account"
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

        console.log("Signup successful:", newUser.email);
        return res.status(201).json({
            message: 'User created successfully',
            user: newUser
        });

    } catch (err) {
        console.error(err);
        res.status(500).json({ message: "Server error" });
    }
});

//Login
app.post('/auth/login', (req, res) => {
    const { email, password } = req.body;
    if (!email || !password) return res.status(400).json({ message: 'Missing credentials' });

    const user = usersDB.select({ email, password })[0];
    if (!user) return res.status(401).json({ message: 'Invalid credentials' });

    res.json(user);
});

app.listen(3000, '0.0.0.0', () => console.log('Server running on http://localhost:3000'));