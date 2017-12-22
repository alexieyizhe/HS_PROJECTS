#Bejeweled

#bugs
#  quit not working properly
#  highlighting selected gems is not properly centered
#  falling gems animation missing

from pygame import *
from random import *
from math import *
import glob
import copy
from pprint import pprint

screen = display.set_mode((1280,720))
mouse.set_visible(False)
display.set_caption("Bejeweled")
mb = mouse.get_pressed
mx,my = mouse.get_pos()
init()

####VARIABLES AND LISTS####

base = Rect(260,125,500,500)
boardinfo = [260, 125, 500, 500] #size and position of board
clearing = False
firstgem = []
secondgem = []
deleteGems = []
fallingGems = []
spots = []
scoreNum = 0 #users score that will accumulate
target = 0 #target changes for each difficulty
board = [[]]
colrow = []
pics={}
level = ""
currentScore = 0
moveNum = 0
target = 0


rtext = font.Font("Manteka.ttf",25)
gtext = font.Font("landmark.otf", 40)
mixer.music.load("music.mp3")
soundeffect = mixer.Sound("soundeffect.wav")
winsound = mixer.Sound("winsound.wav")
losesound = mixer.Sound("losesound.wav")


####COLOURS####
white = 255,255,255
black = 0,0,0
red = 225,0,0
###############


####IMAGES####
logowindow = image.load("images/misc/logo.png")
display.set_icon(logowindow)  #displays the window icon

cursor = image.load("images/misc/cursor.png")

titlelogo = image.load("images/misc/titlelogo.png")
levellogo = image.load("images/misc/levellogo.png")
howto = image.load("images/misc/help.png")
boardpic = image.load("images/misc/board.png")
highlight = image.load("images/misc/highlight.png")
track = image.load("images/misc/trackscore.png")
winpic = image.load("images/misc/win.png")
losepic = image.load("images/misc/lose.png")

#backgrounds
levelPics = {"easy":image.load("images/backgrounds/game/backgroundEasy.png"),"medium":image.load("images/backgrounds/game/backgroundMedium.png"),"hard":image.load("images/backgrounds/game/backgroundHard.png")}
bgPics = {"red":image.load("images/backgrounds/main menu/red.png"),"blue":image.load("images/backgrounds/main menu/blue.png"),"green":image.load("images/backgrounds/main menu/green.png"),"purple":image.load("images/backgrounds/main menu/purple.png")}

#gems
loadGems = glob.glob("images/gems/*.png")
gemPics = {1:image.load(loadGems[0]),2:image.load(loadGems[1]),3:image.load(loadGems[2]),4:image.load(loadGems[3]),5:image.load(loadGems[4]),6:image.load(loadGems[5])}

#buttons
playbutton = image.load("images/buttons/playbutton.png")
helpbutton = image.load("images/buttons/helpbutton.png")             #images
backbutton = image.load("images/buttons/backbutton.png")
replaybutton = image.load("images/buttons/replaybutton.png")
quitbutton = image.load("images/buttons/quitbutton.png")

easybutton = image.load("images/buttons/easybutton.png")
mediumbutton = image.load("images/buttons/mediumbutton.png")
hardbutton = image.load("images/buttons/hardbutton.png")



####RECTS####
playRect = Rect(350,400,200,200)
helpRect = Rect(730,400,200,200)
backRect = Rect(100, 65, 100, 88)

quitRect = playRect
replayRect = helpRect

easyRect = Rect(135,400,200,200)
mediumRect = Rect(527,400,200,200)
hardRect = Rect(915,400,200,200)
##############



def loadScreen():
    global board
    global colrow
    global pics
    
    board = [[randint(1,6) for i in range(12)] for i in range(10)]
    
    for row in range(10):
       for column in range(12):
           rvalue = board[row][column]
           #this is so that if three gems in a row match when the board is set up it changes the gem to be different
           while ((column >= 2 and board[row][column-1] == rvalue and board[row][column-2] == rvalue) or(row >= 2 and board[row-1][column] == rvalue and board[row-2][column] == rvalue)):
               rvalue = randint(1,6)
               board[row][column] = rvalue
    
    

    
def drawScreen():
    global board
    backRect = Rect(50,65,100,88)
    
    screen.blit(levelPics[level],(0,0))
    screen.blit(boardpic,(base.x-5,base.y-5))
    
    screen.blit(transform.scale(titlelogo,(250,59)),(515,25))
    screen.blit(backbutton,backRect)

    screen.blit(track,(815, 150))
    screen.blit(gtext.render(str(target), True, white), (1050, 185))
    screen.blit(gtext.render(str(currentScore), True, white), (1050, 290))
    screen.blit(gtext.render(str(moveNum), True, white), (1050, 400))
    
    for row in range(10):
        for column in range(2, 12):
        #for column in range(0, 12): #testing purposes only, shows the top 2 rows
            screen.blit(gemPics[board[row][column]],(row*50+boardinfo[0],column*50+boardinfo[1]-100))  #gets the position of the gem and draws the corresponding gem from the list on the board
            #screen.blit(gemPics[board[row][column]],(row*50+boardinfo[0],column*50+boardinfo[1]-100)) #testing purposes only, shows top 2 rows as well
            spot = Rect(row,column,50,50)
            spots.append(spot)    #a list of rectangles created for the highlight function
    if len(firstgem) != 0:
        screen.blit(highlight,(firstgem[1]*50+boardinfo[0]-5,(firstgem[2]-2)*50+boardinfo[1]-1)) #compensate for 5 pixel outline and top 2 rows of gems
    if len(secondgem) != 0:
        screen.blit(highlight,(secondgem[1]*50+boardinfo[0]-5,(secondgem[2]-2)*50+boardinfo[1]-1))
                
    
def checkSwap():
    global board
    global firstgem
    global secondgem
    global clearing
    global moveNum

    mb = mouse.get_pressed()
    mx,my = mouse.get_pos()
    filled = False
    tmpboard = copy.deepcopy(board)
    
    if mx >= boardinfo[0] and mx < boardinfo[0] + boardinfo[2]  and my >= boardinfo[1] and my < boardinfo[1] + boardinfo[3]:
        row = int(floor((mx-boardinfo[0])/50))
        column = int(floor((my-boardinfo[1])/50)+2) #adds 2 to compensate for the grid starting 2 spaces up where you cant see
        print(row)
        print(column)
        if firstgem == []:
            firstgem = [board[row][column],row,column]
        elif secondgem == []: #second gem is now filled, check for swaps and clears
            secondgem = [board[row][column],row,column]
            filled = True

    if filled:
        distance = [abs(firstgem[1] - secondgem[1]), abs(firstgem[2] - secondgem[2])]
        if (1 in distance and 0 in distance): #checks if they're 1 away, therefore can swap, but not both 1 away (diagonal can't swap)
            tmpboard[secondgem[1]][secondgem[2]] = firstgem[0]
            tmpboard[firstgem[1]][firstgem[2]] = secondgem[0]

            foundMatch(tmpboard)
            if clearing:
                soundeffect.play()
                moveNum -= 1
                board = tmpboard
                clearing = False
                
        firstgem = []
        secondgem = []
    clearing = False

    
def foundMatch(tmpboard):
    for row in range(10):
        for column in range(12):
            canClear(row, column, row - 1, column, 1, tmpboard, [[row, column]])
            canClear(row, column, row + 1, column, 1, tmpboard, [[row, column]])
            canClear(row, column, row, column - 1, 1, tmpboard, [[row, column]])
            canClear(row, column, row, column + 1, 1, tmpboard, [[row, column]])
        
def canClear(x1, y1, x2, y2, length, newboard, tobecleared):
    global clearing
    global deleteGems
    directionx = x2 - x1
    directiony = y2 - y1
    
    if x1<0 or x1>9 or y1<0 or y1>11 or x2<0 or x2>9 or y2<0 or y2>11:
        pass
    
    elif newboard[x1][y1] == newboard[x2][y2]:
        length += 1
        canClear(x2, y2, x2 + directionx, y2 + directiony, length, newboard, tobecleared + [[x2, y2]])

    elif length >= 3:
        clearing = True;
        print(tobecleared)
        deleteGems = tobecleared
        updateGame()

    
def updateGame(): #draw highlights and shit
    global deleteGems
    global currentScore
    
    currentScore += randint(100, 250)*len(deleteGems) #gives more points for longer chains at once
    for i in range(len(deleteGems)):
        print("CLEARINGGGGG")
        #print(deleteGems[i][0])
        newgemtmp = randint(1, 6)
        del board[deleteGems[i][0]][deleteGems[i][1]]
        #pprint(board)
        board[deleteGems[i][0]].insert(0, newgemtmp)
        #screen.sleep(20)

    deleteGems = []


def mainMenu (): #the main menu screen
    global page
    running = True
    randbg = bgPics[choice(["red","blue","purple","green"])]
    mixer.music.play(-1)

    while running:
        for evnt in event.get():          
            if evnt.type == QUIT:
                page = "exit"
                running = False
            if evnt.type == MOUSEBUTTONUP:
                if playRect.collidepoint((mx,my)):
                    #if the user presses play, have them select a level
                    page = "levels"
                    running = False
                elif helpRect.collidepoint((mx,my)):
                    #if the user presses help, show the instructions
                    page = "instructions"
                    running = False
                    
        mx,my = mouse.get_pos()
        mb = mouse.get_pressed()
        
        screen.blit(randbg, (0, 0))
        while len(fallingGems) < 20:
            newgem = [gemPics[randint(1, 6)], randint(0, 1280), randint(-1000, 0)]
            fallingGems.append(newgem)
        
        for gem in fallingGems:
            screen.blit(gem[0],(gem[1],gem[2]))

            if gem[2] > 720:
                del fallingGems[fallingGems.index(gem)]

            gem[2] += 3

        screen.blit(titlelogo,(314,100))
        screen.blit(playbutton, playRect)
        screen.blit(helpbutton, helpRect)

        screen.blit(cursor, (mx, my))
        
        display.flip()    

def instructions():
    global page
    running = True
    randbg = bgPics[choice(["red","blue","purple","green"])]
    
    while running:
        for e in event.get():
            if e.type == QUIT:
                page = "exit"
                running = False
            if e.type == MOUSEBUTTONUP and backRect.collidepoint((mx,my)):
                page = "mainMenu"   #return to the homescreen if the user clicks back on this page or clicks the x
                running = False
                                  
        mb = mouse.get_pressed()
        mx,my = mouse.get_pos()
        
        screen.blit(randbg, (0,0))
        screen.blit(howto,(0,0))
        screen.blit(backbutton,backRect)

        screen.blit(cursor, (mx, my))
        
        display.flip()
        
def levels():
    #screen that displays the different levels the user can select (easy, medium or hard)
    global page
    global moveNum
    global target
    global scoreNum
    global level
    global currentScore
    
    running = True
    randbg = bgPics[choice(["red","blue","purple","green"])]
    
    while running:
        for e in event.get():
            if e.type == QUIT:
                page = "exit"
                running = False
            if e.type == MOUSEBUTTONUP:
                if backRect.collidepoint((mx,my)):
                    page = "mainMenu"
                    running = False
                elif easyRect.collidepoint((mx,my)):
                    page = "game"
                    level = "easy"
                    moveNum = 60
                    target = 25000
                    currentScore = 0
                    running = False
                    mixer.music.fadeout(2000)
                elif mediumRect.collidepoint((mx,my)):
                    page = "game"
                    level = "medium"
                    moveNum = 40                                 #number of moves and target score is different for each level
                    target = 30000
                    currentScore = 0
                    running = False
                    mixer.music.fadeout(2000)
                elif hardRect.collidepoint((mx,my)):
                    page = "game"
                    level = "hard"
                    moveNum = 25
                    target = 45000
                    currentScore = 0
                    running = False
                    mixer.music.fadeout(2000)

        mb = mouse.get_pressed()
        mx,my = mouse.get_pos()
        
        screen.blit(randbg,(0,0))
        screen.blit(backbutton,backRect)
        screen.blit(levellogo, (272, 100))
        
        screen.blit(easybutton,easyRect)
        screen.blit(rtext.render("Moves: 60", True, white), (165, 330))
        screen.blit(rtext.render("Goal: 25000", True, white), (150, 360))
        
        screen.blit(mediumbutton,mediumRect)
        screen.blit(rtext.render("Moves: 40", True, white), (555, 330))
        screen.blit(rtext.render("Goal: 30000", True, white), (540, 360))
        
        screen.blit(hardbutton,hardRect)
        screen.blit(rtext.render("Moves: 25", True, white), (950, 330))
        screen.blit(rtext.render("Goal: 45000", True, white), (935, 360))

        screen.blit(cursor, (mx, my))
        
        display.flip()

def runGame ():
    global page
    global moveNum
    running = True
    
    loadScreen()
    stuckRect = Rect(815,473,400,111)
    
    while running:
        for e in event.get():
            if e.type == QUIT:
                page = "mainMenu"       #return to homescreen if the user clicks exit on this page
                running = False
            if e.type == MOUSEBUTTONUP and base.collidepoint(mx,my):
                checkSwap()
            if e.type == MOUSEBUTTONUP and backRect.collidepoint(mx,my):
                page = "mainMenu"       #return to homescreen if the user clicks the back button
                running = False
            if e.type == MOUSEBUTTONUP and stuckRect.collidepoint(mx,my):
                loadScreen()
                moveNum -= 5
        drawScreen()
        foundMatch(board)
        updateGame()

        mb = mouse.get_pressed()
        mx,my = mouse.get_pos()
        screen.blit(cursor,(mx,my))

        if currentScore >= target: 
            running = False
            endGame(True)
            
        if moveNum <= 0:
            running = False
            endGame(False)
        
        display.flip()
        
        
    
    

def endGame(win): #main function for the ending - page telling player they won/lost
    global page
    running = True
    randbg = bgPics[choice(["red","blue","purple","green"])]
    
    if win:
        winsound.play()
            
    else:
        losesound.play()
            
    while running:
        for e in event.get():
            if e.type == QUIT:
                page = "exit"
                running = False  
            if e.type == MOUSEBUTTONUP and replayRect.collidepoint(mx,my):
                page = "mainMenu"       #return to homescreen if the user wants to play again
                running = False
            if e.type == MOUSEBUTTONUP and quitRect.collidepoint(mx,my):
                page = "exit"       #exit game if user is done playing
                running = False

        screen.blit(randbg,(0,0))
        if win:
            screen.blit(winpic,(0,0))
            
        else:
            screen.blit(losepic,(0,0))

        screen.blit(gtext.render("You scored "+str(currentScore)+" on "+str(level)+" in "+str(moveNum)+" moves.", True, white), (260, 335))
        screen.blit(quitbutton,(quitRect))
        screen.blit(replaybutton,(replayRect))

        mb = mouse.get_pressed()
        mx,my = mouse.get_pos()
        screen.blit(cursor,(mx,my))
        
        display.flip()


######## MENU ##########
#copied from example in class
        
running = True
page = "mainMenu"
while page!= "exit":
    if page == "mainMenu":
        mainMenu()
    if page == "game":
        runGame()
    if page == "instructions":
        instructions()
    if page == "levels":
        levels()
quit()




    
