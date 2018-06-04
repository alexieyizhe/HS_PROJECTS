#POKEPAINT
#PokéPaint is a paint program made by Alex Xie that allows users to work in a Pokémon themed environment, featuring Pokémon stamps and backgrounds, as well as many other tools. Loading and saving, as well as rectifying mistakes
#can be done using the save,load, undo, and redo tools. The user can also pick their own primary and secondary color, draw shapes, lines, and polygons in a variety of sizes, and write text on top of their creations.
#Using PokéPaint is quick and easy, with intuitive keyboard shortcuts as well as the ability to see which tool, size, and color the user has selected.
from Tkinter import *
import tkFileDialog
from pygame import *
from random import *
from math import *
import os,sys

init()

root = Tk() 
root.withdraw()

#---------OPTIONS------------------#
os.environ['SDL_VIDEO_WINDOW_POS'] = '10,30' #window opens at (10,30)
screen = display.set_mode((1250,938))
display.set_caption("PokéPaint by Alex Xie")
splash = True
main = True
tool = "pencil"
size = 10
black = (0,0,0)
white = (255,255,255)
pokeyellow = (255,203,5)
pokeblue = (57,93,168)
color = (0,0,0)
color2 = (255,255,255)
toolmenu = False #is tool menu open
bgmenu = False #is background menu open
pokemenu = False #is stamp menu open
shapemenu = False #is shape menu open
menuclose = True #allow drawing if true
drawing = False #checks if stuff is being drawn
undo = []
redo = []
fillpoints = []
polygonlist = []
alphabet = ["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"," ","1","2","3","4","5","6","7","8","9","0","!","@","#","$","%","^","&","*","(",")","-",
            "_","+","=",",",".","<",">",":",";",'"',"'","{","[","}","]","\\","|","`","~","?","/"]
startx,starty = 0,0
words = ""
timesnewroman = font.SysFont("Times New Roman",size*2)
mousepos = font.SysFont("Times New Roman",15)
arial = font.SysFont("Arial",20)

def addundo():
    canvascopy = screen.subsurface(canvas).copy()
    undo.append(canvascopy)
    del redo[:]
    
def undolast():
    screen.blit(undo[-2],canvas)
    redo.append(undo.pop())
    del polygonlist [:]
    
def redolast():
    screen.blit(redo[-1],canvas)
    undo.append(redo.pop())
    
def savecanvas():
    filename = tkFileDialog.asksaveasfilename(parent=root,title="Save the image as...")
    if filename != "":
        if filename[-4:] not in [".png",".jpg",".gif",".bmp"]:
            filename += ".png"
        image.save(canvascopy,filename)
        menuclose = True
        
def loadcanvas():
    filename = tkFileDialog.askopenfilename(parent=root,title="Open: ")
    if filename != "" and filename[-4:] in [".png",".jpg",".gif",".bmp"]:
        global loaded,tool
        loaded = image.load(filename)
        tool = "loadpic"
        menuclose = True
        
#----------------STARTUP SCREEN--------------#
screen.blit(image.load("Other Pictures/pokemonsplashscreen.png").convert(),(0,0))
display.flip()

#music
mixer.music.load("Music/pokemusic.wav")
mixer.music.play()

#-----------LAYOUT,MUSIC AND CANVAS-----------#
colors = [(99,172,70),(106,167,195),(221,139,89)]
trios = ["Other Pictures/triofire.png","Other Pictures/triowater.png","Other Pictures/triograss.png"]

bgcolor = choice(colors)
screen.fill(bgcolor)
screen.blit(image.load(choice(trios)),(950,29))

canvas = Rect(47,170,1154,721) #drawing surface
frame = Rect(15,138,1220,785) #frame of canvas

draw.line(screen,pokeblue,(0,19),(1250,19),2) #pokepaint logo
screen.blit(image.load("Other Pictures/pokelogosmall.png"),(570,0))

#----------BUTTONS AND IMAGES--------------------#
#MAIN TOOLS
toolbut = screen.blit(image.load("Menu Pictures/toolpic.png"),(15,35))
menubut = screen.blit(image.load("Menu Pictures/framepic.png"),(70,35))
stampbut = screen.blit(image.load("Menu Pictures/stamppic.png"),(125,35))
shapebut = screen.blit(image.load("Menu Pictures/shapepic.png"),(180,35))

#MENU BACKGROUND BARS
menubar = image.load("Menu Pictures/pokeboxball.png")
bgbar = image.load("Menu Pictures/pokeboxpurple.png")
shapebar = image.load("Menu Pictures/pokebox4ball.png")
stampbar = image.load("Menu Pictures/pokeboxmetal.png")

#UTILITY BUTTONS
savepic = image.load("Tools/savepic.png")
loadpic = image.load("Tools/loadpic.png")
undopic = image.load("Tools/undopic.png")
redopic = image.load("Tools/redopic.png")

savebut = screen.blit(savepic,(10,90))
loadbut = screen.blit(loadpic,(60,90))
undobut = screen.blit(undopic,(120,100))
redobut = screen.blit(redopic,(180,100))

#TOOLs
pencil = image.load("Tools/pokepencil.png")
eraser = image.load("Tools/pokeeraser.png")
brush = image.load("Tools/pokebrush.png")
spray = image.load("Tools/pokespray.png")
picker = image.load("Tools/pokepicker.png")
bucket = image.load("Tools/pokebucket.png")
text = image.load("Tools/poketext.png")

pencilbut = Rect(30,97,50,50)
eraserbut = Rect(90,97,50,50)
brushbut = Rect(150,97,50,50)
spraybut = Rect(210,97,50,50)
pickerbut = Rect(270,97,50,50)
bucketbut = Rect(330,97,50,50)
textbut = Rect(390,97,50,50)

#SHAPES
loadimage = image.load("Tools/loadimage.png")
polygon = image.load("Tools/polygonpic.png")
rect = image.load("Tools/rectpic.png")
line = image.load("Tools/linepic.png")
circle = image.load("Tools/circlepic.png")

polybut = Rect(185,90,50,50)
rectbut = Rect(240,90,50,50)
linebut = Rect(295,90,50,50)
circlebut = Rect(350,90,50,50)

#STAMPS
charizard = image.load("Stamps/charizard.png")
charmander = image.load("Stamps/charmander.png")
pikachu = image.load("Stamps/pikachu.png")
squirtle = image.load("Stamps/squirtle.png")
bulbasaur = image.load("Stamps/bulbasaur.png")
ash = image.load("Stamps/ash.png")
snorlax = image.load("Stamps/snorlax.png")

zardbut = Rect(135,90,50,50)
charmbut = Rect(185,90,50,50)
pikabut = Rect(235,90,50,50)
sqrtlebut = Rect(285,90,50,50)
bulbbut = Rect(335,90,50,50)
ashbut = Rect(385,90,50,50)
snorebut = Rect(435,90,50,50)

#COLOR PICKER SPECTRUM
colorspectrum = image.load("Other Pictures/colorPicker.png").convert()
spectrumbut = Rect(500,40,270,68)
colorbut = Rect(482,40,15,68)
color2but = Rect(773,40,15,68)

draw.rect(screen,color,colorbut) 
draw.rect(screen,color2,color2but)
draw.rect(screen,pokeblue,(480,38,310,72),3) #box around it
draw.line(screen,pokeblue,(498,40),(498,110),3)
draw.line(screen,pokeblue,(771,40),(771,110),2)

currentTool = draw.rect(screen,pokeblue,(410,39,60,60),3) #box around current tool
currentSize = draw.rect(screen,pokeblue,(800,39,60,60),3) #box around current size
currentPos = draw.rect(screen,pokeblue,(585,115,105,18),3) #box around mouse position on canvas

#BACKGROUNDS
pokedefaultsmall = image.load("Backgrounds/pokedefaultsmall.png")    
pokebg1small = image.load("Backgrounds/pokebgpicframesmall.png")
pokebg2small = image.load("Backgrounds/pokebggrassoceansmall.png")
pokebg3small = image.load("Backgrounds/pokebgbattlesmall.png")
pokebg4small = image.load("Backgrounds/pokebgtreessmall.png")
pokebg4small = image.load("Backgrounds/pokebgtreessmall.png")

pokedefault = image.load("Backgrounds/pokedefault.png").convert()
pokebg1 = image.load("Backgrounds/pokebgpicframe.jpg").convert()
pokebg2 = image.load("Backgrounds/pokebggrassocean.jpg").convert()
pokebg3 = image.load("Backgrounds/pokebgbattle.jpg").convert()
pokebg4 = image.load("Backgrounds/pokebgtrees.jpg").convert()

bgbutdefault = Rect(105,100,70,50)
bgbutone = Rect(180,100,70,50)
bgbuttwo = Rect(255,100,70,50)
bgbutthree = Rect(330,100,70,50)
bgbutfour = Rect(405,100,70,50)

#START/BUTTONS
screen.blit(pokedefault,frame)
screen.blit(colorspectrum,spectrumbut)
draw.circle(screen,black,(830,70),size)
nomenu = screen.copy()
canvascopy = screen.subsurface(canvas).copy()
preview = canvascopy
undo.append(canvascopy)
mx,my = mouse.get_pos()
textx,texty = mx,my

#STARTUP SCREEN
while splash:
    for e in event.get():
        if e.type == QUIT:
            splash = False
            main = False
        elif e.type == MOUSEBUTTONUP:
            splash = False #quits title screen and enters main program

#MAIN PROGRAM            
while main:
    for e in event.get():
        if e.type == QUIT: main = False

        elif e.type == MOUSEBUTTONDOWN:
            startx,starty = e.pos
            copy = screen.copy() #for typing, stamps, etc
            if e.button == 1:
                if canvas.collidepoint(mx,my) and menuclose:
                    if tool == "bucket": 
                        drawing = True
                        oldcol = screen.get_at((mx,my)) #color to be filled
                        fillpoints.append((mx,my))
                        if oldcol != color:
                            while len(fillpoints): 
                                f = fillpoints[0]
                                if screen.get_at(f) == oldcol: #only adds new points if point is same as original color (part of same shape)
                                    screen.set_at(f,color)
                                    fillpoints.append((f[0]+1,f[1]))
                                    fillpoints.append((f[0],f[1]+1))
                                    fillpoints.append((f[0]-1,f[1]))
                                    fillpoints.append((f[0],f[1]-1))
                                del fillpoints[0]
                            

                    elif tool == "polygon":
                        draw.circle(screen,color,(mx,my),2) #draw circle to show where vertices of polygon are
                        polygonlist.append((mx,my))

                #MENUS
                if toolbut.collidepoint(mx,my): #tool menu
                    if toolmenu:
                        toolmenu = False
                        menuclose = True
                        screen.blit(nomenu,(0,0)) #blits screen with no menu that was previosly copied
                    else:
                        toolmenu = True
                        bgmenu = False
                        pokemenu = False
                        menuclose = False
                        shapemenu = False
                        screen.blit(nomenu,(0,0))
                        nomenu = screen.copy() #copies screen before opening menu
                        screen.blit(menubar,(10,85))
                        screen.blit(pencil,pencilbut)
                        screen.blit(eraser,eraserbut)
                        screen.blit(brush,brushbut)
                        screen.blit(spray,spraybut)
                        screen.blit(picker,pickerbut)
                        screen.blit(bucket,bucketbut)
                        screen.blit(text,textbut)
                        
                elif menubut.collidepoint(mx,my): #background menu
                    if bgmenu:
                        bgmenu = False
                        menuclose = True
                        screen.blit(nomenu,(0,0))
                    else:
                        bgmenu = True
                        toolmenu = False
                        pokemenu = False
                        menuclose = False
                        shapemenu = False
                        screen.blit(nomenu,(0,0))
                        nomenu = screen.copy()
                        screen.blit(bgbar,(75,85))
                        screen.blit(pokedefaultsmall,bgbutdefault)
                        screen.blit(pokebg1small,bgbutone)
                        screen.blit(pokebg2small,bgbuttwo)
                        screen.blit(pokebg3small,bgbutthree)
                        screen.blit(pokebg4small,bgbutfour)

                elif stampbut.collidepoint(mx,my): #stamp menu
                    if pokemenu:
                        pokemenu = False
                        menuclose = True
                        screen.blit(nomenu,(0,0))
                    else:
                        pokemenu = True
                        toolmenu = False
                        bgmenu = False
                        menuclose = False
                        shapemenu = False
                        screen.blit(nomenu,(0,0))
                        nomenu = screen.copy()
                        screen.blit(stampbar,(125,85))
                        screen.blit(transform.scale(charizard,(50,50)),zardbut)
                        screen.blit(transform.scale(charmander,(50,50)),charmbut)
                        screen.blit(transform.scale(pikachu,(50,50)),pikabut)
                        screen.blit(transform.scale(squirtle,(50,50)),sqrtlebut)
                        screen.blit(transform.scale(bulbasaur,(50,50)),bulbbut)
                        screen.blit(transform.scale(ash,(50,50)),ashbut)
                        screen.blit(transform.scale(snorlax,(50,50)),snorebut)
                        
                elif shapebut.collidepoint(mx,my): #shape menu
                    if shapemenu:
                        shapemenu = False
                        menuclose = True
                        screen.blit(nomenu,(0,0))
                    else:
                        shapemenu = True
                        bgmenu = False
                        toolmenu = False
                        pokemenu = False
                        menuclose = False
                        screen.blit(nomenu,(0,0))
                        nomenu = screen.copy()
                        screen.blit(shapebar,(170,85))
                        screen.blit(polygon,polybut)
                        screen.blit(rect,rectbut)
                        screen.blit(line,linebut)
                        screen.blit(circle,circlebut)
                        
                elif colorbut.collidepoint(mx,my) or color2but.collidepoint(mx,my):
                    color,color2 = color2, color #switches primary and secondary drawing color
                    draw.rect(screen,color,colorbut)
                    draw.rect(screen,color2,color2but)
                
                if menuclose:
                    if undobut.collidepoint(mx,my): #undo
                        if len(undo) > 1: undolast()
                        
                        nomenu = screen.copy()
                        preview = screen.subsurface(canvas).copy() #preview used to remove dots after drawing polygon
                    
                    elif redobut.collidepoint(mx,my): #redo
                        if len(redo): redolast()
                        
                        nomenu = screen.copy()
                        preview = screen.subsurface(canvas).copy()
                            
                    elif savebut.collidepoint(mx,my): savecanvas() #saving
                    elif loadbut.collidepoint(mx,my): loadcanvas() #loading
                        
            if menuclose: 
                if e.button == 2:
                    if lastshape.collidepoint(mx,my): #fills last drawn shape
                        screen.set_clip(canvas)
                        if tool == "rect":
                            screen.fill(color,lastshape) 
                            addundo()
                        elif tool == "circle":
                            draw.ellipse(screen,color,lastshape)
                            addundo()
                        screen.set_clip(None)
                        
                elif e.button == 4 and size < 25 and drawing == False:
                    textx += len(words)*size + 1
                    words = ""
                    copy = screen.copy()
                    size += 2 #scroll wheel increases
                    timesnewroman = font.SysFont("Times New Roman",size*2)
                    arial = font.SysFont("Arial",size*2)

                    
                elif e.button == 5 and size > 5 and drawing == False:
                    textx += len(words)*size + 1
                    words = ""
                    copy = screen.copy()
                    size -= 2  #and decreases size
                    timesnewroman = font.SysFont("Times New Roman",size*2)
                    arial = font.SysFont("Arial",size*2)

                    
                nomenu = screen.copy()
                   
        elif e.type == MOUSEBUTTONUP:
            if e.button != 5 and e.button != 4:
                if drawing and tool != "picker": #adding to undo list
                    addundo()
                    drawing = False

                if tool == "text": #resets text tool to current pos and clears
                    textx,texty = e.pos
                    words = ""
                    
            mouse.set_cursor(*cursors.arrow)
            mouse.set_visible(True)
            
        elif e.type == KEYDOWN:
            if key.get_pressed()[K_LCTRL] and menuclose:
                print("YES")#undo redo with ctrl z + ctrl shift z
                if key.get_pressed()[K_z]:
                    
                    if key.get_pressed()[K_LSHIFT]:
                        print("re")
                        if len(redo): redolast()      
                    else:
                        print("undo")
                        if len(undo) > 1: undolast()
                        
                    nomenu = screen.copy()
                    preview = screen.subsurface(canvas).copy()
                    
                elif key.get_pressed()[K_s]: savecanvas() #save load with ctrl o + ctrl s
                elif key.get_pressed()[K_o]: loadcanvas()
                
            elif key.get_pressed()[K_RETURN] and menuclose: #pressing enter completes the polygon
                if tool == "polygon":
                    if len(polygonlist) > 2:
                        screen.blit(preview,canvas)
                        draw.polygon(screen,color,polygonlist)
                        del polygonlist[:]
                        preview = screen.subsurface(canvas).copy()
            else:           
                if tool == "text":
                    if e.unicode.lower() in alphabet:
                        words += e.unicode
                    elif e.unicode == "\t": #if tab is pressed, add 4 spaces (tab) to words
                        words += "    "
                    elif e.unicode == "\x08": words = words[:-1] #if backspace is pressed, delete last letter
                    elif e.unicode == "\r": #if enter is pressed, blit text further down
                        texty += size*2
                        words = ""
                        copy = screen.copy()
                        addundo()

                    if menuclose: #blits text to canvas everytime you type
                        fontname = eval(typefont)
                        screen.set_clip(canvas)
                        screen.blit(copy,(0,0))
                        screen.blit(fontname.render(words,True,color),(textx,texty))
                        screen.set_clip(None)
                        addundo()

    mx,my = mouse.get_pos() #updates mouse location
    mb = mouse.get_pressed() #updates status of mouse buttons
    
    if mb[0] == 1:
        #MENU SELECTION
        if toolmenu:
            if pencilbut.collidepoint(mx,my):
                tool = "pencil"
                screen.blit(nomenu,(0,0))
                toolmenu = False
                menuclose = True
                    
            elif eraserbut.collidepoint(mx,my):
                tool = "eraser"
                screen.blit(nomenu,(0,0))
                toolmenu = False
                menuclose = True
                    
            elif brushbut.collidepoint(mx,my):
                tool = "brush"
                screen.blit(nomenu,(0,0))
                toolmenu = False
                menuclose = True
                    
            elif spraybut.collidepoint(mx,my):
                tool = "spray"
                screen.blit(nomenu,(0,0))
                toolmenu = False
                menuclose = True
                
            elif pickerbut.collidepoint(mx,my):
                tool = "picker"
                screen.blit(nomenu,(0,0))
                toolmenu = False
                menuclose = True
                    
            elif bucketbut.collidepoint(mx,my):
                tool = "bucket"
                screen.blit(nomenu,(0,0))
                toolmenu = False
                menuclose = True
                
            elif textbut.collidepoint(mx,my):
                tool = "text"
                typefont = "timesnewroman"
                screen.blit(nomenu,(0,0))
                toolmenu = False
                menuclose = True
                    
        elif bgmenu:
            if bgbutone.collidepoint(mx,my):
                screen.blit(nomenu,(0,0))
                screen.blit(pokebg1,canvas)
                nomenu = screen.copy()
                addundo()
                bgmenu = False
                menuclose = True
            elif bgbuttwo.collidepoint(mx,my):
                screen.blit(nomenu,(0,0))
                screen.blit(pokebg2,canvas)
                nomenu = screen.copy()
                addundo()
                bgmenu = False
                menuclose = True
            elif bgbutthree.collidepoint(mx,my):
                screen.blit(nomenu,(0,0))
                screen.blit(pokebg3,canvas)
                nomenu = screen.copy()
                addundo()
                bgmenu = False
                menuclose = True
            elif bgbutfour.collidepoint(mx,my):  
                screen.blit(nomenu,(0,0))
                screen.blit(pokebg4,canvas)
                nomenu = screen.copy()
                addundo()
                bgmenu = False
                menuclose = True
            elif bgbutdefault.collidepoint(mx,my):
                screen.blit(nomenu,(0,0))
                screen.blit(pokedefault,frame)
                nomenu = screen.copy()
                addundo()
                bgmenu = False
                menuclose = True
            
        elif pokemenu:
            if zardbut.collidepoint(mx,my):
                tool = "charizard"
                screen.blit(nomenu,(0,0))
                pokemenu = False
                menuclose = True
            elif charmbut.collidepoint(mx,my):
                tool = "charmander"
                screen.blit(nomenu,(0,0))
                pokemenu = False
                menuclose = True
            elif pikabut.collidepoint(mx,my):
                tool = "pikachu"
                screen.blit(nomenu,(0,0))
                pokemenu = False
                menuclose = True
            elif sqrtlebut.collidepoint(mx,my):
                tool = "squirtle"
                screen.blit(nomenu,(0,0))
                pokemenu = False
                menuclose = True
            elif bulbbut.collidepoint(mx,my):
                tool = "bulbasaur"
                screen.blit(nomenu,(0,0))
                pokemenu = False
                menuclose = True     
            elif ashbut.collidepoint(mx,my):
                tool = "ash"
                screen.blit(nomenu,(0,0))
                pokemenu = False
                menuclose = True
            elif snorebut.collidepoint(mx,my):
                tool = "snorlax"
                screen.blit(nomenu,(0,0))
                pokemenu = False
                menuclose = True
                
        elif shapemenu:
            if polybut.collidepoint(mx,my):
                    tool = "polygon"
                    screen.blit(nomenu,(0,0))
                    shapemenu = False
                    menuclose = True
                    preview = screen.subsurface(canvas).copy()
            elif rectbut.collidepoint(mx,my):
                    tool = "rect"
                    screen.blit(nomenu,(0,0))
                    shapemenu = False
                    menuclose = True
            elif linebut.collidepoint(mx,my):
                    tool = "line"
                    screen.blit(nomenu,(0,0))
                    shapemenu = False
                    menuclose = True
            elif circlebut.collidepoint(mx,my):
                    tool = "circle"
                    screen.blit(nomenu,(0,0))
                    shapemenu = False
                    menuclose = True

        #BUTTON PRESS           
        if spectrumbut.collidepoint(mx,my) and menuclose: #changing color using color spectrum
            color = screen.get_at((mx,my))
            draw.rect(screen,color,colorbut)
            
        #DRAWING
        if canvas.collidepoint(mx,my) and menuclose:
            
            if tool != "polygon" and tool != "text":    #so it doesn't add to undo list when clicking for text or drawing vertices of polygon
                drawing = True
                if polygonlist: #gets rid of preview circles if not used before picking different tool
                    screen.blit(preview,canvas)
                    del polygonlist[:]
                
            screen.set_clip(canvas)
            
            if tool == "pencil":
                draw.line(screen,color,(oldx,oldy),(mx,my))
                
            elif tool in ["brush","spray","eraser"]: #smooth drawing
                distance = sqrt((mx - oldx)**2 + (my - oldy)**2) #distance from start point to end point of mouse
                if distance == 0:
                    distance = 1
                x = oldx
                y = oldy
                sx = (mx - oldx)/distance
                sy = (my - oldy)/distance
                for i in range(int(distance)):
                    if tool == "brush":
                        draw.circle(screen,color,(int(x),int(y)),size) #draws a circle every predetermined distance(sx and sy) to fill gaps
                        draw.circle(screen,color,(mx,my),size)
                    elif tool == "spray":
                        for i in range(size//2):  #draws amount relative to size of circle (larger the circle, more dots it draws)
                            x = randint(size*-1,size) #picks random point in square
                            y = randint(size*-1,size)
                            if sqrt(x**2 + y**2) <= size: #distance formula so it draws in circle (checks if point is in circle; if so, draw it)
                                screen.set_at((mx + x,my + y),color)
                    elif tool == "eraser":
                        mouse.set_cursor(*cursors.diamond)
                        draw.rect(screen,white,(int(x) - size//2,int(y) - size//2,size,size))
                        draw.rect(screen,white,(mx  - size//2,my  - size//2,size,size))
                    x += sx #adds in increments to draw next circle
                    y += sy

            elif tool in ["charizard","charmander","pikachu","squirtle","bulbasaur","ash","snorlax"]:
                blitstamp = eval(tool) #tool is same as picture name
                mouse.set_visible(False)
                screen.blit(copy,(0,0))
                screen.blit(blitstamp,(mx - (blitstamp.get_width()/2),my - (blitstamp.get_height()/2))) #offset to place mouse at middle

            elif tool == "loadpic":
                mouse.set_visible(False)
                screen.blit(copy,(0,0))
                screen.blit(loaded,(mx - (loaded.get_width()/2),my - (loaded.get_height()/2)))
                
            elif tool == "rect":
                mouse.set_visible(False)
                screen.blit(copy,(0,0))
                draw.rect(screen,color,(startx,starty,mx - startx,my - starty),size)
                draw.rect(screen,color,(startx - (size/2) + 1,starty - (size/2) + 1,size,size)) #draws 4 rectangles to smooth edges
                draw.rect(screen,color,(startx - (size/2) + 1,my - size/2,size,size))
                draw.rect(screen,color,(mx - size/2,starty - (size/2) + 1,size,size))
                draw.rect(screen,color,(mx - size/2,my - size/2,size,size))
                lastshape = Rect(startx,starty,mx - startx,my - starty)

            elif tool == "line":
                mouse.set_visible(False)
                screen.blit(copy,(0,0))
                if size < 11: draw.line(screen,color,(startx,starty),(mx,my))
                else:  draw.line(screen,color,(startx,starty),(mx,my),size//5)
                    
            elif tool == "circle":
                mouse.set_visible(False)
                screen.blit(copy,(0,0))
                lastshape = Rect(startx,starty,mx - startx,my - starty)
                lastshape.normalize() #prevent negative values
                if lastshape[2] > size*2 and lastshape[3] > size*2: #prevent crashing from radius being bigger than width of ellipse
                    draw.ellipse(screen,color,lastshape,size)
                
            screen.set_clip(None)
                                
            if tool == "picker": #goes outside canvas clip so it can draw new color rect beside spectrum
                color = screen.get_at((mx,my))
                draw.rect(screen,color,colorbut) 
                nomenu = screen.copy()
                
            canvascopy = screen.subsurface(canvas).copy()
            nomenu = screen.copy()
                
    elif mb[2] == 1:
        #MENU SELECTION
        if toolmenu:
            if textbut.collidepoint(mx,my):
                tool = "text"
                typefont = "arial"
                screen.blit(nomenu,(0,0))
                toolmenu = False
                menuclose = True
                
        #BUTTON PRESS
        if spectrumbut.collidepoint(mx,my) and menuclose:
            color2 = screen.get_at((mx,my))
            draw.rect(screen,color2,color2but)
            nomenu = screen.copy()
            
        elif tool in ["brush","spray","eraser"]:
            distance = sqrt((mx - oldx)**2 + (my - oldy)**2)
            if distance == 0:
                distance = 1
            x = oldx
            y = oldy
            sx = (mx - oldx)/distance
            sy = (my - oldy)/distance
            for i in range(int(distance)):
                if tool == "brush":
                    draw.circle(screen,color2,(int(x),int(y)),size)
                    draw.circle(screen,color2,(mx,my),size)
                elif tool == "spray":
                    for i in range(size//2):
                        x = randint(size*-1,size)
                        y = randint(size*-1,size)
                        if sqrt(x**2 + y**2) <= size:
                            screen.set_at((mx + x,my + y),color2)
                x += sx
                y += sy

        #DRAWING
        if canvas.collidepoint(mx,my) and menuclose:
            drawing = True
            screen.set_clip(canvas)
            
            if tool == "pencil":
                draw.line(screen,color2,(oldx,oldy),(mx,my))
                
            elif tool == "polygon" and menuclose:
                if len(polygonlist) > 2:
                    screen.blit(preview,canvas)
                    draw.polygon(screen,color,polygonlist)
                    del polygonlist[:]
                    preview = screen.subsurface(canvas).copy()

            screen.set_clip(None)
            
            if tool == "picker":
                color2 = screen.get_at((mx,my))
                draw.rect(screen,color2,color2but)
                
            canvascopy = screen.subsurface(canvas).copy()
            nomenu = screen.copy()
            
    if menuclose:
        screen.fill(bgcolor,currentTool)
        screen.fill(bgcolor,currentSize)
        draw.rect(screen,pokeblue,currentTool,3)
        draw.rect(screen,pokeblue,currentSize,3)
        draw.circle(screen,black,(830,70),size) #shows currrent size in box
        if tool == "loadpic": screen.blit(loadimage,(currentTool[0] + 5, currentTool[1] + 5))
        else : screen.blit(transform.scale(eval(tool),(50,50)),(currentTool[0] + 5, currentTool[1] + 5)) #shows current tool as image in box
        nomenu = screen.copy()

    #MOUSE POSITION ON SCREEN
    if canvas.collidepoint(mx,my):
        screen.fill(bgcolor,currentPos)
        draw.rect(screen,pokeblue,currentPos,2)
        screen.blit(mousepos.render("("+str(mx - 47)+","+str(my - 170)+")",True,black),(608,116)) #subtract distance from (0,0) to get coords inside canvas
    else:
        screen.fill(bgcolor,currentPos)
        draw.rect(screen,pokeblue,currentPos,2)
        screen.blit(mousepos.render("OFF CANVAS",True,black),(593,116)) #blits 'OFF CANVAS' if not on canvas
    
    oldx,oldy = mx,my
    display.flip()
quit()


