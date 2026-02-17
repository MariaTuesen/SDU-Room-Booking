const fs = require('fs');
const path = require('path');

class Database {
    static databaseInstances = {};

    static getInstance(name) {
        if (!Database.databaseInstances[name]) {
            Database.databaseInstances[name] = new Database(name);
        }
        return Database.databaseInstances[name];
    }

    constructor(name) {
        this.filePath = path.join(__dirname, 'data', `${name}.json`);
        this.data = this.readFromFile();
    }

    all() {
        return this.data;
    }

    select(query) {
        const keys = Object.keys(query);
        return this.data.filter(item =>
            keys.every(key => item[key] === query[key])
        );
    }

    selectLike(query) {
        const keys = Object.keys(query);
        return this.data.filter(item =>
            keys.every(key => item[key] && item[key].toLowerCase().includes(query[key].toLowerCase()))
        );
    }

    insert(obj) {
        this.data.push(obj);
        this.writeToFile();
    }

    update(id, updatedObj) {
        const index = this.data.findIndex(item => item.id === id);
        if (index !== -1) {
            this.data[index] = { ...this.data[index], ...updatedObj };
            this.writeToFile();
        }
    }

    delete(id) {
        this.data = this.data.filter(item => item.id !== id);
        this.writeToFile();
    }

    readFromFile() {
        if (!fs.existsSync(this.filePath)) return [];
        const content = fs.readFileSync(this.filePath, 'utf8');
        return JSON.parse(content);
    }

    writeToFile() {
        fs.writeFileSync(this.filePath, JSON.stringify(this.data, null, 2));
    }
}

module.exports = { Database };