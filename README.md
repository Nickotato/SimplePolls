# Simple Polls
*Simple Polls is a lightweight Minecraft plugin that allows server admins to create interactive polls for players. Players can vote on polls directly in-game through an intuitive GUI, and poll results are tracked in real-time. Expired polls are automatically managed and stored separately.*

*This is a plugin designed for small SMPs to make sure that only people who actively play vote on important polls, use in large servers(200+ players) in not recommended*

![License: MPL 2.0](https://img.shields.io/badge/License-MPL_2.0-brightgreen.svg)

## Features
- Create polls with a question and multiple options (up to 9 options per poll)
- Manage polls through a GUI
- Track votes per player
- Automatically expire polls and archive them
- View active and expired polls
- Player-friendly GUI for voting
- Chat-based input for creating and editing polls
- Persistent storage using YAML files

## Installation
1. Download the latest build of **Simple Polls**.
2. Place the JAR file in your server's `plugins` folder.
3. Start or restart the server to generate the config and data folders.
4. Ensure you have the correct Minecraft version (`1.21.4+` recommended).

## Commands
| Command      | Description                   
|-------------|-------------------------------
| `/poll`      | Open the Simple Polls GUI       
| `/polls`     | Alias for `/poll`               

## Permissions
- `polls.manage` – Allows a player to create and manage polls. Default: OP.

## Usage
1. Open the polls GUI with `/poll` or `/polls`.
2. Click on a poll to vote.
3. Players who join the server are notified about active polls they haven’t voted on yet.
4. Expired polls can be viewed separately.

## Data Storage
- Poll data is saved in the `plugins/SimplePolls/polldata` folder.
- Expired polls are moved to `plugins/SimplePolls/expiredpolldata`.
- Each poll is stored as a separate `.yml` file.

## Planned Features
* Actually limiting the amount of options ;-;
* Quality of life buttons for going back and cancelling
* See specifically who voted for what
* Turn off voting notifications on join

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
This project is licensed under the Mozilla Public License 2.0 - see the [LICENSE](LICENSE) file for details.
