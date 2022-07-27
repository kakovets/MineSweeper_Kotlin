# MineSweeper_Kotlin
Here is classic game MineSweeper created with Kotlin language. I didnt use any objects or OOP paradigms, cause i was practising in realization with functions. Enjoy! 
By the start of the game you will be asked how many rows and columns will be on your playing field. I recommend you to choose less than 9, cause I didnt optimize the game for 2-digit amount of rows or columns. After choosing it, you will be asked to set the quantity of mines. Of course, uou cant set more mines, than field has cells. Actually, formula is : ( maxMines = rows * cols - 9 ). 
After that game is waiting for your first choice of the cell. In order to make your first choice safe, you will hit "free" zone 100% percent. In that moment mines are setted randomly. 
Then classic game starts. You need to give coordinates and mark cell as "free" or "mine". Also you can unmark the cell. 
When the player hit "free" cell, i used flood fill method (recursion) to open all "free" cells and nearest "number" cells. 
The game will end when all free cells will be opened! 
