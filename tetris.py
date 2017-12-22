from pygame import *
from time import time
from random import *
from pprint import pprint
from glob import glob
from copy import deepcopy

'''
TODO

if powerup spawns for one player, spawn it for the other player as well to make it fair
only show score in single player and only show power bar in two player
add game over screen
add graphics
get powerups to stack

'''
def validPos(grid, piece, movex, movey, rotate = 0):
    for x in range(4): #size of the pieces grid
        for y in range(4):
            if piece[0][(piece[1] + rotate) % len(piece[0])][y][x] != "0": #skips over the 0's in the piece
                if not piece[2] + x + movex >= 0 or not piece[2] + x + movex < 10 or not piece[3] + y + movey < 20: return False #checks if x and y are inside grid; if not, return False automatically
                if grid[piece[2] + x + movex][piece[3] + y + movey] != "0": return False #checks if the block that the piece wants to move to is not occupied
    return True #if nothing is conflicting return true (position is valid)

def validDrop(piece, grid): #returns the lowest possible valid y-value for a piece
    y = 1
    while True:
        if validPos(grid, piece, 0, y):
            y += 1
        else:
            break
    return y - 1

def newPiece():
    shape = randint(0,6)
            #0list of shape       1rotation of shape        2xpos 3ypos 
    newshape = [deepcopy(pieces[shape]), randint(0,len(pieces[shape]) - 1), 3, 0]
    if randint(1, 2) == 1:  
        newshape[0] = [['0000',
                        '0000',
                        '00' + choice(powerupList)[0] + '0',
                        '0000']]
        newshape[1] = 0
        
    return newshape

def getPiece(player,playerpieces, playergrid):
    if playerpieces["currentpiece"] == None:
        playerpieces["currentpiece"] = playerpieces["nextpiece"]
        playerpieces["nextpiece"] = newPiece()
        playerpieces["switched"] = False
        if not validPos(playergrid,playerpieces["currentpiece"],0,0):
            page = "gameover"
            loser = player

    return playerpieces

def changePiece(playerpiece, playergrid, movex, movey, rotate = False):
    if validPos(playergrid, playerpiece, movex, movey):
        if rotate and validPos(playergrid, playerpiece, movex, movey, 1):
            playerpiece[1] += 1
            #if playerpieces["currentpiece"][1] == len(playerpieces["currentpiece"][0]):
            playerpiece[1] = playerpiece[1] % len(playerpiece[0]) #resets rotation if it exceeds the max # of possible rotations
        playerpiece[2] += movex
        playerpiece[3] += movey

    return playerpiece

def storePiece(playerpieces):
    if not playerpieces["switched"]:    
        if playerpieces["storedpiece"] == None:
            playerpieces["storedpiece"] = playerpieces["currentpiece"]
            playerpieces["currentpiece"] = playerpieces["nextpiece"]
            playerpieces["nextpiece"] = newPiece()
        else:
            playerpieces["storedpiece"], playerpieces["currentpiece"] = playerpieces["currentpiece"], playerpieces["storedpiece"]
        playerpieces["storedpiece"][2], playerpieces["storedpiece"][3] = 3, 0 #resets piece to start at top of grid
        playerpieces["switched"] = True

    return playerpieces

def movePiece(playerpieces, playermovement, playergrid, otherplayerpowerups):
    reverse = 1
    for p in otherplayerpowerups:
        if p[0][0] == "r":
            reverse = -1
    if time() - playermovement["movetime"] > 0.1:
        if playermovement["left"]: playerpieces["currentpiece"] = changePiece(playerpieces["currentpiece"], playergrid, -1 * reverse, 0)
        elif playermovement["right"]: playerpieces["currentpiece"] = changePiece(playerpieces["currentpiece"], playergrid, 1 * reverse, 0)
        elif playermovement["down"]: playerpieces["currentpiece"] = changePiece(playerpieces["currentpiece"], playergrid, 0, 1)
        playermovement["movetime"] = time()

    return playerpieces, playermovement

def dropPiece(playerpieces, playermovement, playergrid, playerelements, otherplayergrid):
    if time() - playermovement["droptime"] > playerelements["speed"]:
            if validPos(playergrid, playerpieces["currentpiece"], 0, 1): #checks if piece can still fall
                playerpieces["currentpiece"] = changePiece(playerpieces["currentpiece"], playergrid,0,1)
            else: #otherwise, add existing piece to board and spawn new falling piece
                for x in range(4):
                    for y in range(4):
                        if playerpieces["currentpiece"][0][playerpieces["currentpiece"][1]][y][x] != "0": #skips over the 0s in the pieces
                            playergrid[playerpieces["currentpiece"][2] + x][playerpieces["currentpiece"][3] + y] = playerpieces["currentpiece"][0][playerpieces["currentpiece"][1]][y][x] #changes piece position on grid to number corresponding to color of piece
                            #playerelements["score"] += 13 * randint(1, 2) #adds some amount of points every time a piece lands
                playerpieces["currentpiece"] = None #allows new piece to spawn
                playerelements["dropped"] += 1
            playermovement["droptime"] = time()
            
    return playerpieces, playermovement, otherplayergrid

def changeGame(otherplayerpiece, playergrid, playerelements, otherplayergrid, otherplayerelements):
    multiplier = 0
    linescleared = 0
    for y in range(20):
        ylines = []
        for x in playergrid:
            ylines.append(x[y])

        if "0" not in ylines and "8" not in ylines: #only runs when line is full
            lineclear.play()
            playerelements, otherplayerelements = addtoPoweruplist(playerelements, otherplayerelements, ylines)

            multiplier += 1
            linescleared += 1
            playerelements["score"] += (100 * multiplier)  #adds higher score for more rows cleared at once
            for x in range(len(playergrid)):
                playergrid[x].insert(0, "0")
                del playergrid[x][y + 1]

                otherplayergrid[x].append("8")
                del otherplayergrid[x][0]
                
            changePiece(otherplayerpiece, otherplayergrid, 0, -1) #stops current piece from clipping with added rows at bottom

        if "8" in ylines and linescleared > 0:
            for x in range(len(playergrid)):
                del playergrid[x][y]
                playergrid[x].insert(0, "0")
            linescleared -= 1

    if playerelements["dropped"] == 25:
        playerelements["dropped"] = 0
        if playerelements["speed"] > 0.02:
            playerelements["speed"] -= 0.1

    expired = []
    for l in playerelements["powerups"]:
        if time() - l[1] >= 15:
            if l[0][0] == "f":
                playerelements["speed"] /= 2
            elif l[0][0] == "s":
                otherplayerelements["speed"] *= 2
            expired.append(l)
    for l in expired:
        del playerelements["powerups"][playerelements["powerups"].index(l) - 1]
        
    return otherplayerpiece, playergrid, playerelements, otherplayergrid, otherplayerelements


def drawGame(playername, playerpieces, playergrid, playerelements):
    screen.blit(gameframe, (widthmargin - 20 + (playername - 1) * 650, heightmargin - 20))
    draw.rect(screen,(255,255,255),((playername - 1) * 650 + widthmargin, 0 + heightmargin, 300, 600)) 
    for x in range(4):
        for y in range(4): 
            if playerpieces["currentpiece"][0][playerpieces["currentpiece"][1]][y][x] != "0": #skips over the 0s in the pieces
                draw.rect(screen, (200, 200, 200), ((playerpieces["currentpiece"][2] + x) * 30 + (playername - 1) * 650 + widthmargin, (playerpieces["currentpiece"][3] + y + validDrop(playerpieces["currentpiece"], playergrid)) * 30 + heightmargin, 29, 29))
                if playerpieces["theme"] == "minimalistic": draw.rect(screen, (pieceColor[playerpieces["currentpiece"][0][playerpieces["currentpiece"][1]][y][x]]), ((playerpieces["currentpiece"][2] + x) * 30 + (playername - 1) * 650 + widthmargin, (playerpieces["currentpiece"][3] + y) * 30 + heightmargin, 29, 29))
                elif playerpieces["theme"] == "classic": screen.blit(piecePics[playerpieces["currentpiece"][0][playerpieces["currentpiece"][1]][y][x]], ((playerpieces["currentpiece"][2] + x) * 30 + (playername - 1) * 650 + widthmargin, (playerpieces["currentpiece"][3] + y) * 30 + heightmargin))
                if not playerpieces["currentpiece"][0][playerpieces["currentpiece"][1]][y][x].isdigit(): screen.blit(blockfont.render(playerpieces["currentpiece"][0][playerpieces["currentpiece"][1]][y][x], True, (0, 0, 0)), ((playerpieces["currentpiece"][2] + x) * 30 + (playername - 1) * 650 + widthmargin + 5, (playerpieces["currentpiece"][3] + y) * 30 + heightmargin + 5))

            drawOther(x, y, playername, playerpieces, playerelements)

    for x in range(len(playergrid)):
        for y in range(len(playergrid[x])):
            draw.rect(screen, (200,200,200), (x * 30 + (playername - 1) * 650 + widthmargin, y * 30 + heightmargin, 30, 30), 1)
            if str(playergrid[x][y]) != "0":
                if playerpieces["theme"] == "minimalistic": draw.rect(screen, (pieceColor[str(playergrid[x][y])]), (x * 30  + (playername - 1) * 650 + widthmargin, y * 30 + heightmargin, 29, 29))
                elif playerpieces["theme"] == "classic": screen.blit(piecePics[str(playergrid[x][y])], (x * 30  + (playername - 1) * 650 + widthmargin, y * 30 + heightmargin))
                if not playergrid[x][y].isdigit(): screen.blit(blockfont.render(playergrid[x][y], True, (0, 0, 0)), (x * 30  + (playername - 1) * 650 + widthmargin + 5, y * 30 + heightmargin + 5))
                
def drawOther(x, y, playername, playerpieces, playerelements):
    if playerpieces["nextpiece"][0][playerpieces["nextpiece"][1]][y][x] != "0":
        if playerpieces["theme"] == "minimalistic": draw.rect(screen, (pieceColor[playerpieces["nextpiece"][0][playerpieces["nextpiece"][1]][y][x]]), (x * 30 + (305 + 200 * (playername - 1)) + widthmargin, y * 30 + 5 + heightmargin, 29, 29))
        elif playerpieces["theme"] == "classic": screen.blit(piecePics[playerpieces["nextpiece"][0][playerpieces["nextpiece"][1]][y][x]], (x * 30 + (305 + 200 * (playername - 1)) + widthmargin, y * 30 + 5 + heightmargin))
        if not playerpieces["nextpiece"][0][playerpieces["nextpiece"][1]][y][x].isdigit(): screen.blit(blockfont.render(playerpieces["nextpiece"][0][playerpieces["nextpiece"][1]][y][x], True, (0, 0, 0)), (x * 30 + (305 + 200 * (playername - 1)) + widthmargin + 5, y * 30 + 5 + heightmargin + 5))
        
    if playerpieces["storedpiece"] != None:
        if playerpieces["storedpiece"][0][playerpieces["storedpiece"][1]][y][x] != "0":
            if playerpieces["theme"] == "minimalistic": draw.rect(screen, (pieceColor[playerpieces["storedpiece"][0][playerpieces["storedpiece"][1]][y][x]]), (x * 30 + (305 + 200 * (playername - 1)) + widthmargin, y * 30 + 150 + heightmargin, 29, 29))
            elif playerpieces["theme"] == "classic": screen.blit(piecePics[playerpieces["storedpiece"][0][playerpieces["storedpiece"][1]][y][x]], (x * 30 + (305 + 200 * (playername - 1)) + widthmargin, y * 30 + 150 + heightmargin))
            if not playerpieces["storedpiece"][0][playerpieces["storedpiece"][1]][y][x].isdigit(): screen.blit(blockfont.render(playerpieces["storedpiece"][0][playerpieces["storedpiece"][1]][y][x], True, (0, 0, 0)), (x * 30 + (305 + 200 * (playername - 1)) + widthmargin + 5, y * 30 + 150 + heightmargin + 5))
            
    for l in playerelements["powerups"]:
        screen.blit(tetrisfont.render(l[0][0], True, (255, 200, 0)), (370 + (playername - 1) * 200 + widthmargin, l[0][1] + heightmargin))

    
    scoretext = tetrisfont.render(str(playerelements["score"]), True, (255, 200, 0))
    scorepos = scoretext.get_rect()
    scorepos.centerx, scorepos.y = 370 + (playername - 1) * 200 + widthmargin, 500 + heightmargin
    screen.blit(scoretext, scorepos)

    
def addtoPoweruplist(playerelements, otherplayerelements, line):
    for powerup in powerupList:
        if powerup[0] in line:
            if powerup[0] == "f":
                playerelements["speed"] *= 2
            elif powerup[0] == "s":
                otherplayerelements["speed"] /= 2
                
            for x in playerelements["powerups"]:
                print(x[0][0], powerup[0])
                if x[0][0] == powerup[0]:
                    print("dupe")
                    playerelements["powerups"][playerelements["powerups"].index(x) - 1][1] = time()
                    return playerelements, otherplayerelements
            playerelements["powerups"].append([powerup, time()])
    return playerelements, otherplayerelements

def pause():
    running = True
    screen.fill((255, 255, 255))
    while running:
        print("pause")
        for e in event.get():
            if e.type == MOUSEMOTION:
                running = False
        display.flip()
    

def menuBackground(piecelist):
    offscreen = []
    
    if randint(0, 35) == 1:
        piecelist.append([choice(menuPics),[randint(-5, 1095), -20]])
        
    for p in range(len(piecelist)):
        if piecelist[p][1][1] <= 800:
            piecelist[p][1][1] += 2
        else:
            offscreen.append(p)

    for i in offscreen:
        del piecelist[i]
        
    return piecelist
        
def mainmenu():
    running = True
    bgpiecelist = []
    totallynotsecretlist = []
    menutime = time()
    mixer.music.play(-1)
    while running:
        for e in event.get():
            if e.type == QUIT:
                return "exit"
            if e.type == MOUSEBUTTONUP:
                for x in menubuttons:
                    if x[0].collidepoint((mx, my)):
                        mixer.music.fadeout(2000)
                        return x[1]
            if e.type == KEYDOWN:
                totallynotsecretlist.append(e.key)
                if totallynotsecretlist == [273, 273, 274, 274, 276, 275, 276, 275, 98, 97]:
                    return "credits"
                    
        mx, my = mouse.get_pos()
        mb = mouse.get_pressed()
        
        screen.fill((0, 0, 0))
        if time() - menutime > 0.02:
            bgpiecelist = menuBackground(bgpiecelist)
            menutime = time()
        for p in range(len(bgpiecelist)):
            screen.blit(bgpiecelist[p][0], (bgpiecelist[p][1][0],bgpiecelist[p][1][1]))
        screen.blit(logo, (150, 100))
        for x in menubuttons:
            draw.rect(screen, (255, 0, 0), x[0])
        screen.blit(cursor, (mx - 15,my - 10))
        
        display.flip()
    
def game():
    global P1pieces, P1movement, P1elements, P1grid, P2pieces, P2movement, P2elements, P2grid
    running = True
    while running:
        #gets a new piece if old piece has been placed onto the board
        getPiece(P1elements["player"], P1pieces, P1grid)
        getPiece(P2elements["player"], P2pieces, P2grid)

        for e in event.get():
            if e.type == QUIT: return "exit"
            if e.type == KEYDOWN: #movement and rotation of current falling piece
                if e.key == K_a: P1movement["left"] = True
                elif e.key == K_d: P1movement["right"] = True
                elif e.key == K_s: P1movement["down"] = True
                elif e.key == K_w: P1pieces["currentpiece"] = changePiece(P1pieces["currentpiece"], P1grid, 0,0,True)
                elif e.key == K_z: 
                    P1movement["droptime"] = 0 
                    P1pieces["currentpiece"][3] += validDrop(P1pieces["currentpiece"], P1grid)
                elif e.key == K_x: P1pieces = storePiece(P1pieces)

                if e.key == K_j: P2movement["left"] = True
                elif e.key == K_l: P2movement["right"] = True
                elif e.key == K_k: P2movement["down"] = True
                elif e.key == K_i: P2pieces["currentpiece"] = changePiece(P2pieces["currentpiece"], P2grid, 0,0,True)
                elif e.key == K_n: 
                    P2movement["droptime"] = 0
                    P2pieces["currentpiece"][3] += validDrop(P2pieces["currentpiece"], P2grid)
                elif e.key == K_m: P2pieces = storePiece(P2pieces)

            if e.type == KEYUP:
                if e.key == K_ESCAPE or e.key == K_BACKSPACE: pause()

                if e.key == K_a: P1movement["left"] = False
                elif e.key == K_d: P1movement["right"] = False
                elif e.key == K_s: P1movement["down"] = False
                
                if e.key == K_j: P2movement["left"] = False
                elif e.key == K_l: P2movement["right"] = False
                elif e.key == K_k: P2movement["down"] = False


        screen.fill((0,0,0))

        #draws the falling piece, upcoming piece, stored piece, and pieces existing on grid
        drawGame(P1elements["player"], P1pieces, P1grid, P1elements)
        drawGame(P2elements["player"], P2pieces, P2grid, P2elements)

        #checks if keys are bring pressed, thus moving the pieces
        P1pieces, P1movement = movePiece(P1pieces, P1movement, P1grid, P2elements["powerups"])
        P2pieces, P2movement = movePiece(P2pieces, P2movement, P2grid, P1elements["powerups"])

        #playergrid, playerelements, otherplayergrid = changeGame(playerpieces["currentpiece"], playergrid, playerelements, otherplayergrid)
        P2pieces["currentpiece"], P1grid, P1elements, P2grid, P2elements = changeGame(P2pieces["currentpiece"], P1grid, P1elements, P2grid, P2elements)
        P1pieces["currentpiece"], P2grid, P2elements, P1grid, P1elements = changeGame(P1pieces["currentpiece"], P2grid, P2elements, P1grid, P1elements)

        #naturally drops the piece after a certain amount of time has passed, also checks if block has hit the ground and removes/adds lines
        P1pieces, P1movement, P2grid = dropPiece(P1pieces, P1movement, P1grid, P1elements, P2grid)
        P2pieces, P2movement, P1grid = dropPiece(P2pieces, P2movement, P2grid, P2elements, P1grid)

        display.flip()
    return "menu"

def instructions():
    running = True
    screen.fill((0, 0, 0))
    draw.rect(screen, (255, 0, 0), (50, 50, 1000, 700))
    while running:
        for e in event.get():          
            if e.type == QUIT: return "menu"
        if key.get_pressed()[27]: return "menu"

        display.flip()
    return "menu"

def highscores():
    running = True
    while running:
        for e in event.get():
            if e.type == QUIT: return "menu"
            
        mx, my = mouse.get_pos()
        screen.fill((0, 0, 0))
        screen.blit(tetrisfont.render("high scores", True, (255, 200, 0)), (500, 400))
        screen.blit(cursor, (mx - 15,my - 10))
        
        display.flip()
        
    return "menu"

def options():
   pass 
def credit():
    running = True
    while running:
        for e in event.get():
            if e.type == QUIT: return "menu"

def gameover():
    pass
        
        
sPiece = [['0000',
           '0011',
           '0110',
           '0000'],

          ['0000',
           '0100',
           '0110',
           '0010']]

zPiece = [['0000',
           '0220',
           '0022',
           '0000'],

          ['0000',
           '0020',
           '0220',
           '0200']]

iPiece = [['0030',
           '0030',
           '0030',
           '0030'],

          ['0000',
           '0000',
           '3333',
           '0000']]

oPiece = [['0000',
           '0440',
           '0440',
           '0000']]

jPiece = [['0500',
           '0555',
           '0000',
           '0000'],

          ['0000',
           '0550',
           '0500',
           '0500'],

          ['0000',
           '5550',
           '0050',
           '0000'],

          ['0050',
           '0050',
           '0550',
           '0000']]

lPiece = [['0000',
           '0006',
           '0666',
           '0000'],

          ['0600',
           '0600',
           '0660',
           '0000'],

          ['0000',
           '0666',
           '0600',
           '0000'],

          ['0000',
           '0660',
           '0060',
           '0060']]

tPiece = [['0000',
           '0070',
           '0777',
           '0000'],

          ['0000',
           '0070',
           '0077',
           '0070'],

          ['0000',
           '0000',
           '0777',
           '0070'],

          ['0000',
           '0070',
           '0770',
           '0070']]

init()

mixer.music.load("tetris.mp3")
lineclear = mixer.Sound("lineclear.wav")
cursor = image.load("tetriscursor.png")
logo = image.load("twotrislogo.png")
gameframe = image.load("twotrisframe.png")
mouse.set_visible(False)

pieces = [tPiece,lPiece,jPiece,oPiece,iPiece,zPiece,sPiece] #easier to read list of piece templates 
piecePics = {}
powerupList = [("s", 450), ("f", 420), ("r", 390)]
filenames = sorted(glob("BlockPieces/*.png"))
for i in range(8):
    piecePics[str(i + 1)] = transform.scale(image.load(filenames[i]), (30, 30)) #adds to dictionary of piece shape images
for i in range(len(powerupList)):
    piecePics[powerupList[i][0]] = transform.scale(image.load(filenames[i + 8]), (30, 30))

menuPics = []
menublocks = glob("MainMenuBlocks/*.png")
for i in range(len(menublocks)):
    menuPics.append(image.load(menublocks[i]))
pieceColor = {"1":(30,144,255),"2":(250,0,250),"3":(138,43,226),"4":(0, 255, 0),"5":(255, 0, 0),"6":(0, 191, 255),"7":(240, 240, 0),"8":(230, 230, 230), "f":(30,144,255), "s":(255, 0, 0), "r":(0, 255, 0)}
menubuttons = [(Rect(400, 350, 300, 70), "playgame"), (Rect(400, 445, 300, 70), "highscores"), (Rect(400, 540, 300, 70), "help") , (Rect(400, 635, 300, 70), "options")]
screenwidth = 1100
screenheight = 800
widthmargin = 75
heightmargin = 75
screen = display.set_mode((screenwidth,screenheight))
display.set_caption("Twotris")
tetrisfont = font.Font("Tetris.ttf",30)
blockfont = font.Font("Tetris.ttf",20)
loser = None
page = "menu"

#Player One Variables
P1movement = {"left":False, "right":False, "down":False, "droptime":time(), "movetime":time()}
P1elements = {"player": 1, "dropped": 0, "score": 0, "speed": 0.8, "powerups":[]}
P1pieces = {"currentpiece": None, "nextpiece": newPiece(), "storedpiece": None, "theme": "minimalistic", "switched": False}
P1grid = [["0" for i in range(20)] for i in range(10)]

#Player Two Variables 
P2movement = {"left":False, "right":False, "down":False, "droptime":time(), "movetime":time()}
P2elements = {"player": 2, "dropped": 0, "score": 0, "speed": 0.8, "powerups":[]}
P2pieces = {"currentpiece": None, "nextpiece": newPiece(), "storedpiece": None, "theme": "classic", "switched": False}
P2grid = [["0" for i in range(20)] for i in range(10)]


while page != "exit":
    if page == "menu":
        page = mainmenu()
    if page == "playgame":
        page = game()
    if page == "highscores":
        page = highscores()
    if page == "help":
        page = highscores()
    if page == "options":
        page = options()
    if page == "credits":
        page = credit()
    if page == "gameover":
        page = gameover()
quit()
