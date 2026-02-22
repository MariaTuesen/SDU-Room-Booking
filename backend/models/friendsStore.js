const fs = require("fs/promises");
const path = require("path");

const FRIENDS_PATH = path.join(__dirname, "../data/friends.json");

async function readFriendsFile() {
  try {
    const raw = await fs.readFile(FRIENDS_PATH, "utf-8");
    const parsed = JSON.parse(raw);

    if (!parsed.byUserId) parsed.byUserId = {};
    return parsed;
  } catch (e) {
    if (e.code === "ENOENT") {
      return { byUserId: {} };
    }
    throw e;
  }
}

async function writeFriendsFile(data) {
  await fs.mkdir(path.dirname(FRIENDS_PATH), { recursive: true });
  await fs.writeFile(FRIENDS_PATH, JSON.stringify(data, null, 2), "utf-8");
}

function uniq(arr) {
  return Array.from(new Set(arr));
}

module.exports = { readFriendsFile, writeFriendsFile, uniq };