const fs = require('fs');
const path = require('path');

const groupsFilePath = path.join(__dirname, '..', 'data', 'groups.json');

function ensureGroupsFile() {
  if (!fs.existsSync(groupsFilePath)) {
    fs.mkdirSync(path.dirname(groupsFilePath), { recursive: true });
    fs.writeFileSync(groupsFilePath, JSON.stringify({ groups: [] }, null, 2), 'utf8');
  }
}

function readGroupsFile() {
  ensureGroupsFile();
  const raw = fs.readFileSync(groupsFilePath, 'utf8');
  const data = JSON.parse(raw);
  if (!Array.isArray(data.groups)) data.groups = [];
  return data;
}

function writeGroupsFile(data) {
  ensureGroupsFile();
  fs.writeFileSync(groupsFilePath, JSON.stringify(data, null, 2), 'utf8');
}

module.exports = {
  readGroupsFile,
  writeGroupsFile
};