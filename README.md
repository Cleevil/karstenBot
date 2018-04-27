# karstenBot
A simple Discord bot made with [JDA](https://github.com/DV8FromTheWorld/JDA).


## Motivation
I wanted to practice some Java. A Discord bot seemed like a neat little hobby project, so here i go!

## Running the bot
Two tokens are required to run the bot; a Discord bot token and a Google API token with access to Youtube. Currently the tokens are retrieved from static methods inside a class called `Secrets` 

You can also insert the tokens manually; the discord token within `Program` and the Google API token within `Jukebox.searchYoutube()`.