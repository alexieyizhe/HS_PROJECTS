# -*- coding: utf-8 -*-
#Bejeweled
#non-complete 1.0 version

from pygame import *
from random import *
from math import *
import glob
import copy
from pprint import pprint
#init()   #need init to initialize the mixer
#
screen = display.set_mode((1280,720))
mb = mouse.get_pressed
mx,my = mouse.get_pos()


####VARIABLES AND LISTS####

base = Rect(85,85,500,500)
boardinfo = [85, 85, 500, 500] #size and position of board
clearing = False
firstgem = []
secondgem = []
deleteGems = []
spots = []
scoreNum = 0 #users score that will accumulate
target = 0 #target changes for each difficulty
board = [[]]
colrow = []
pics={}

####COLOURS####
white = 255,255,255
black = 0,0,0
red = 225,0,0
###############

bjlogowindow = image.load("images/logo.png")

levelpics = {"easy":image.load("images/backgroundHard.jpg"),"medium":image.load("images/backgroundMedium.jpg"),"hard":image.load("images/backgroundHard.jpg")}
display.set_icon(bjlogowindow)  #displays the window icon



'''
FUNCTIONS TO CHECK MATCHES

1. check for horizontal match to the left of the gem
2. check horizontal match in the middle (gem is moved to be in middle of match)
3. check for horizontal match to the right of the gem
4. check for vertical match going up form the gem (have to check all possibilities of it going up and down)
    -match first gem up/down
    -match second gem up/down
    -match third gem up/down


'''
################################ MENU ####################################

def loadScreen():
    global board
    global colrow
    global pics
    gem = glob.glob("images/gems/*.png")
    
    board = [[randint(1,6) for i in range(12)] for i in range(10)]
    
    for row in range(10):
       for column in range(12):
           rvalue = board[row][column]
           #this is so that if three gems in a row match when the board is set up it changes the gem to be different
           while ((column >= 2 and board[row][column-1] == rvalue and board[row][column-2] == rvalue) or(row >= 2 and board[row-1][column] == rvalue and board[row-2][column] == rvalue)):
               rvalue = randint(1,6)
               board[row][column] = rvalue
    pics = {1:image.load(gem[0]),2:image.load(gem[1]),3:image.load(gem[2]),4:image.load(gem[3]),5:image.load(gem[4]),6:image.load(gem[5]),7:image.load("images/black.png")}
    

    
def drawScreen():
    global board
    screen.blit(levelpics[level], (0, 0))
    draw.rect(screen,black,base)
    
    for row in range(10):
        #for column in range(2, 12):
        for column in range(0, 12): #testing purposes only, shows the top 2 rows
            #screen.blit(pics[board[row][column]],(row*50+85,(column - 2)*50+85))  #gets the position of the gem and draws the corresponding gem from the list on the board
            screen.blit(pics[board[row][column]],(row*50+85,column * 50 - 15)) #testing purposes only, shows top 2 rows as well
            spot = Rect(row,column,50,50)
            spots.append(spot)    #a list of rectangles created for the highlight function
    


def checkSwap():
    global board
    global firstgem
    global secondgem
    global clearing 
    mb = mouse.get_pressed()
    mx,my = mouse.get_pos()
    filled = False
    tmpboard = copy.deepcopy(board)
    
    if mx >= boardinfo[0] and mx < boardinfo[0] + boardinfo[2]  and my >= boardinfo[1] and my < boardinfo[1] + boardinfo[3]:
        row = floor((mx - 85) / 50)
        column = floor((my - 85) / 50) + 2 #adds 2 to compensate for the grid starting 2 spaces up where you cant see
        if firstgem == []:
            firstgem = [board[row][column], row, column]
        elif secondgem  == []: #second gem is now filled, check for swaps and clears
            secondgem = [board[row][column], row, column]
            filled = True

    if filled:
        distance = [abs(firstgem[1] - secondgem[1]), abs(firstgem[2] - secondgem[2])]
        if (1 in distance and 0 in distance): #checks if they're 1 away, therefore can swap, but not both 1 away (diagonal can't swap)
            print(firstgem, secondgem)
            tmpboard[secondgem[1]][secondgem[2]] = firstgem[0]
            tmpboard[firstgem[1]][firstgem[2]] = secondgem[0]

            pprint(tmpboard)
            foundMatch(tmpboard)
            if clearing:
                #pprint(deleteGems)
                board = tmpboard
                clearing = False
                
        firstgem = []
        secondgem = []

    
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
    
    if x1 < 0 or x1 > 9 or y1 <0 or y1 > 11 or x2 < 0 or x2 > 9 or y2 < 0 or y2 > 11:
        #print("out")
        pass
    
    elif newboard[x1][y1] == newboard[x2][y2]:
        length += 1
        canClear(x2, y2, x2 + directionx, y2 + directiony, length, newboard, tobecleared + [[x2, y2]])

    elif length >= 3:
        clearing = True;
        print(tobecleared)
        deleteGems = tobecleared
        updateGame()
        #print(directionx, directiony)
        #pprint(deleteGems)

    
def updateGame(): #draw highlights and shit
    global deleteGems
    
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
    home = image.load("images/loadscreen.jpg")
    homeS = transform.scale(home,(1280,720))
    play = image.load("images/play.png")
    helppic = image.load("images/help.png")             #images
    helppic = transform.scale(helppic, (225,100))
    play = transform.scale(play,(225,100))
    screen.blit(homeS,(0,0))
    levelRect = Rect(700,630,225,100)
    helpRect = Rect(330,630,225,100)
#    mixer.music.load("") MAKE IT .WAV (DONT FORGET TO CONVERTTT)
#    mixer.music.play(-1,2) 
    while running:
        for evnt in event.get():          
            if evnt.type == QUIT:
                running = False
            if evnt.type == MOUSEBUTTONUP:
                if levelRect.collidepoint((mx,my)):
                    #if the user presses play, have them select a level
                    page = "levels"
                elif helpRect.collidepoint((mx,my)):
                    #if the user presses help, show the instructions
                    page = "instructions"
                
        if page == "game":
            runGame()
            running = False
        if page == "instructions":
            instructions()         #changes pages depending on which button is clicked
            running = False
        if page == "levels":
            levels()
            running = False
            

        screen.blit(play,(700,630))
        screen.blit(helppic,(330,630))

        mx,my = mouse.get_pos()
        mb = mouse.get_pressed()
        display.flip()
    quit()    

def instructions():
    global page
    howto = image.load("images/instructions.png")
    howto = transform.scale(howto,(1280,720))
    screen.blit(howto,(0,0))
    back = image.load("images/back.png")        
    back = transform.scale(back,(200,100))
    backRect = Rect(1050,45,200,100)
    
    running = True
    while running:
        for e in event.get():
            if e.type == QUIT:
                page = "mainMenu"   #return to the homescreen if the user clicks exit on this page
            if e.type == MOUSEBUTTONUP:
                if backRect.collidepoint((mx,my)):   #instructions on how to play the game
                    page = "mainMenu"
        if page == "mainMenu":
            mainMenu()
            running = False
        mb = mouse.get_pressed()
        mx,my = mouse.get_pos()

        screen.blit(back,(1050,45))
        display.flip()
        
def levels():
    #screen that displays the different levels the user can select (easy, medium or hard)
    global page
    global moveNum
    global target
    global scoreNum
    global level
    level = "easy"
    background = image.load("images/levels.png")
    background = transform.scale(background,(1280,720))
    screen.blit(background,(0,0))
    back = image.load("images/back.png")
    back = transform.scale(back,(200,100))
    backRect = Rect(1050,45,200,100)
    easy = image.load("images/easy.png")
    medium = image.load("images/medium.png")
    hard = image.load("images/hard.png")
    easy = transform.scale(easy,(225,100))
    medium = transform.scale(medium,(225,100))
    hard = transform.scale(hard,(225,100))
    easyRect = Rect(100,550,225,100)
    mediumRect = Rect(527,550,225,100)
    hardRect = Rect(954,550,225,100)
    running = True
    while running:
        for e in event.get():
            if e.type == QUIT:
                page = "mainMenu"       #return to homescreen if the user clicks exit on this page
            if e.type == MOUSEBUTTONUP:
                if backRect.collidepoint((mx,my)):
                    page = "mainMenu"  
                elif easyRect.collidepoint((mx,my)):
                    page = "game"
                    level = "easy"
                    moveNum = 60
                    target = 25000
                elif mediumRect.collidepoint((mx,my)):
                    page = "game"
                    level = "medium"
                    moveNum = 40                                 #number of moves and target score is different for each level
                    target = 30000
                elif hardRect.collidepoint((mx,my)):
                    page = "game"
                    level = "hard"
                    moveNum = 25
                    target = 45000
                    
        if page == "mainMenu":
            mainMenu()
        if page == "game":
            runGame()
        mb = mouse.get_pressed()
        mx,my = mouse.get_pos()

        screen.blit(back,(1050,45))
        screen.blit(easy,(100,550))
        screen.blit(medium,(527,550))
        screen.blit(hard,(954,550))
        display.flip()

def runGame ():
    global page
    global moveNum
    currentscreen = True
    loadScreen()
    while currentscreen:
        for e in event.get():
            if e.type == QUIT:
                page = "mainMenu"       #return to homescreen if the user clicks exit on this page
                currentscreen = False
            if e.type == MOUSEBUTTONUP:
                checkSwap()
        drawScreen()
        foundMatch(board)
        updateGame()
        
        display.flip()
        
        
    
    

#def endGame (): #main function for the ending - page telling player they won/lost




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
 #   if page == "end":
#        endGame()
        
    
quit()




    
