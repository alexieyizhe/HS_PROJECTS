fillpoints = []
if tool == "bucket":
    currentcol = screen.get_at((e.pos))
    fillpoints.append(e.pos)
    for f in fillpoints:
        if screen.get_at(f) == currentcol:
            screen.set_at(f,color)
        else:
            break
        fillpoints.append((f[0]+1,f[1]))
        fillpoints.append((f[0],f[1]+1))
        fillpoints.append((f[0]-1,f[1]))
        fillpoints.append((f[0],f[1]-1))
        del fillpoints[fillpoints.index(f)]
        
        
