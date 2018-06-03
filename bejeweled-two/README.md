# Bejeweled 2
*__Author__: Alex Xie*  
*__Created__: sometime long ago in grade 11 CS*  
*__Archived__: June 3, 2018*

### About
A polished (pun intended?) recreation of the gem-matching game, with a flat minimal theme, score tracking, help for beginners.
![image](https://user-images.githubusercontent.com/17508679/34344560-fcfd060e-e9b4-11e7-8682-74d9cefaf46b.png)

![image](https://user-images.githubusercontent.com/17508679/34344567-114679e2-e9b5-11e7-9ac0-e4364b541c62.png)

### Retrospective
I made this game so that I could learn a little about Python before attempting to take CS in high school. It was challenging, but since I already had my other project (bejeweled-orig) and the original game to base it off of, it wasn't as bad.

By far, the hardest part of this entire project was getting the gems to fall correctly and then matching gems across rows and columns to determine which ones to delete - admittedly, this is pretty much describing most of the game's functionality, but still. I originally implemented a detection system that would check the surroundings of the gems that swapped places, but I ran into a problem of gems not being scored if they were matched as a result of falling into the empty places that the user swapped into matches.
