const fs = require('fs');
const path = require('path');

const usersFile = path.join(__dirname, '../data/users.json');

class User {
    constructor(fullName, email, password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    static getAllUsers() {
        if (!fs.existsSync(usersFile)) return [];
        const data = fs.readFileSync(usersFile);
        return JSON.parse(data);
    }

    static saveAllUsers(users) {
        fs.writeFileSync(usersFile, JSON.stringify(users, null, 2));
    }

    save() {
        const users = User.getAllUsers();
        users.push(this);
        User.saveAllUsers(users);
    }

    static findByEmail(email) {
        const users = User.getAllUsers();
        return users.find(user => user.email === email);
    }
}

module.exports = User;