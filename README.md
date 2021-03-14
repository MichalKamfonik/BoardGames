# BoardGames

Graphic desktop application for playing Boardgames.

The app has implemented Draughts and Min-Max algorithm for the computer 
but is ready for adding more games, player types and algorithms.

## What for?
BoardGames provides Swing graphic interface for creating and playing 
Boardgames.

Without any additions you can play Draughts game against your friends on the same 
desktop or against the computer with 1 of 10 available difficulty levels.

If you want you can write and add your own games (e.g. chess), players (e.g. online) and 
AI algorithms (e.g. A*) using provided abstract classes:
* Board
* Figure
* Game
* Player

## How does it work?
1. Clone repository and build the app or download 
   [.jar](https://github.com/MichalKamfonik/BoardGames/raw/master/board-games-1.0-SNAPSHOT-jar-with-dependencies.jar):
   
1. Run the app from console or IDE:

   ![run_console][run_console]

   or   

   ![run_ide][run_ide]
1. Choose a game:

   ![choose_game][choose_game]
1. Choose players

   ![choose_players][choose_players]

1. Choose difficulty level for AI

   ![choose_difficulty][choose_difficulty]

1. Hit start and enjoy the game!

   ![start][start]
   ![enjoy1][enjoy1]
   ![enjoy2][enjoy2]


[run_console]: images/run_console.png "Start app fomr console"
[run_ide]: images/run_ide.png "Start app from IntelliJ"
[choose_game]: images/choose_game.png "Choose game"
[choose_players]: images/choose_players.png "Choose player"
[choose_difficulty]: images/choose_difficulty.png "Choose difficulty"
[start]: images/start.png "Hit start"
[enjoy1]: images/enjoy1.png "Enjoy!"
[enjoy2]: images/enjoy2.png "Enjoy!"

## What technologies were used?
* Swing
* Maven
* Log4j