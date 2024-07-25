def XOR(a, b):
    if a == b:
        return 0
    if a != b:
        return 1

def from_array_to_string(array):
    acu =""
    for i in range(len(array)):
        acu += str(array[i])
        
    return acu
    
def decode_CRC32(msg, CRC_32, tam):
    pos = tam
    dividend = msg[0:pos]
    while len(dividend) >= tam:
        print("Dividendo: ", from_array_to_string(dividend))
        for i in range(tam):
            dividend[i] = XOR(dividend[i], CRC_32[i])
        
        print("Dividendo2: ", from_array_to_string(dividend))
        
        while dividend[0] == 0:
            dividend = dividend[1:]
            if len(dividend) == 0:
                return (msg[0:len(msg)-tam+1],1)
        
        
        if len(dividend) < tam and pos < len(msg):
            while len(dividend) < tam and pos < len(msg):
                dividend.append(msg[pos])
                pos += 1
        else:
            return (-1,-1)       
        print("Dividendo3: ", from_array_to_string(dividend))
        print()
        print()
        
        
    if len(dividend) != 0:
        return (-1,-1)
    
    return (msg[0:len(msg)-tam+1],1)
            


valid = False
while not valid:
    try: 
        bin = input("Ingrese el mensaje en binario: ").strip()
        bin = bin.replace(" ", "")
        valid = True
    except:
        print("Ingrese un valor valido")
        
CRC_32 =[1,0,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,1,1,1,0,1,1,0,1,1,0,1,1,1]
#CRC_32 =[1,0,0,1]

msg = [int(i) for i in bin]
tam = len(CRC_32)
payload, verif = decode_CRC32(msg, CRC_32, tam)
print(len(msg))
if verif == 1:
    print("No se detectaron errores, el payload final es: ", from_array_to_string(payload))
elif verif == -1:
    print("Se detectaron errores, el mensaje final se descarta")
    





