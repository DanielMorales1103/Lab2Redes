from ReceptorPresentacion import ReceptorPresentacion
import matplotlib.pyplot as plt
class CR32(object):
    def __init__(self):
        self.Errores = 0
        self.Correctas = 0
        self.word_length={}
        
    def push_diccionario(self,word):
        word_len = len(word)
        if word_len not in self.word_length:
            self.word_length[word_len] = 1
        else:
            self.word_length[word_len] += 1

    def XOR(self,a, b):
        if a == b:
            return 0
        if a != b:
            return 1

    def from_array_to_string(self,array):
        acu =""
        for i in range(len(array)):
            acu += str(array[i])
            
        return acu
    
    def decode_CRC32(self,msg, CRC_32, tam):
        pos = tam
        dividend = msg[0:pos]
        while len(dividend) >= tam:
            # print("Dividendo: ", from_array_to_string(dividend))
            for i in range(tam):
                dividend[i] = self.XOR(dividend[i], CRC_32[i])
            
            # print("Dividendo2: ", from_array_to_string(dividend))
            
            while dividend[0] == 0:
                dividend = dividend[1:]
                if len(dividend) == 0:
                    return (msg[0:len(msg)-tam+1],1)
            
            
            if len(dividend) < tam and pos < len(msg):
                while len(dividend) < tam and pos < len(msg):
                    dividend.append(msg[pos])
                    pos += 1
            else:
                return (msg[0:len(msg)-tam+1],-1)       
            # print("Dividendo3: ", from_array_to_string(dividend))
            # print()
            # print()
            
            
        if len(dividend) != 0:
            return (msg[0:len(msg)-tam+1],-1)
        
        return (msg[0:len(msg)-tam+1],1)
                
    def detect_error(self,bin):
        presentacion = ReceptorPresentacion()
        CRC_32 =[1,0,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,1,1,1,0,1,1,0,1,1,0,1,1,1]
        msg = [int(i) for i in bin]
        tam = len(CRC_32)
        payload, verif = self.decode_CRC32(msg, CRC_32, tam)
        if verif == 1:
            self.Correctas += 1
            presentacion.decode_message(self.from_array_to_string(payload), 1)

        elif verif == -1:
            self.Errores += 1
            new_string = self.from_array_to_string(payload)
            error_word = presentacion.decode_message(new_string, 1, "Se detectaron errores, por lo tanto la trama se descarta")
            self.push_diccionario(error_word)

    def get_estadisticas(self):
        print("Accuracy: ", self.Correctas/(self.Correctas+self.Errores))
        
        x = self.word_length.keys()
        y = self.word_length.values()
        
        print(x)
        
        plt.bar(x, y)
        plt.xticks(range(min(x), max(x)+1, 1))
        plt.xlabel('Longitud de palabra')
        plt.ylabel('Cantidad de errores encontrados')
        plt.title('Histograma de errores')
        plt.show()
        
        


