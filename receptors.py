import socket

def XOR(a, b):
    return 0 if a == b else 1

def from_array_to_string(array):
    return ''.join(map(str, array))

def decode_CRC32(msg, CRC_32, tam):
    pos = tam
    dividend = msg[0:pos]
    while len(dividend) >= tam:
        for i in range(tam):
            dividend[i] = XOR(dividend[i], CRC_32[i])

        while dividend[0] == 0:
            dividend = dividend[1:]
            if len(dividend) == 0:
                return msg[0:len(msg)-tam+1], 1
        
        if len(dividend) < tam and pos < len(msg):
            while len(dividend) < tam and pos < len(msg):
                dividend.append(msg[pos])
                pos += 1
        else:
            return -1, -1
        
    if len(dividend) != 0:
        return -1, -1
    
    return msg[0:len(msg)-tam+1], 1

def hamming_decode(encoded):
    n = len(encoded)
    r = 0

    while (2**r) < n + 1:
        r += 1

    encoded = list(encoded)
    error_position = 0
    for i in range(r):
        parity_position = 2**i
        parity = 0
        for j in range(parity_position - 1, n, 2 * parity_position):
            for k in range(j, j + parity_position):
                if k < n and encoded[k] == '1':
                    parity ^= 1
        if parity != 0:
            error_position += parity_position

    if error_position != 0:
        print(f"Error detectado en la posición: {error_position}")
        error_position -= 1
        encoded[error_position] = '0' if encoded[error_position] == '1' else '1'
        print("Error corregido.")
    else:
        print("No se detectaron errores.")

    original_message = []
    for i in range(n):
        if not is_power_of_two(i + 1):
            original_message.append(encoded[i])

    original_message = ''.join(original_message)
    return original_message, error_position != 0

def is_power_of_two(num):
    return (num & (num - 1)) == 0 and num != 0

def start_server():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('localhost', 12345))
    server_socket.listen(1)

    print("Esperando conexión del emisor...")
    conn, addr = server_socket.accept()
    print(f"Conectado con {addr}")

    algorithm = conn.recv(1024).decode().strip()
    noisy_message = conn.recv(1024).decode().strip()

    print("Mensaje recibido: " + noisy_message)
    print("Algoritmo a utilizar: " + algorithm)
    if algorithm == "CRC32":
        CRC_32 =[1,0,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,1,1,1,0,1,1,0,1,1,0,1,1,1]
        msg = [int(i) for i in noisy_message]
        tam = len(CRC_32)
        payload, verif = decode_CRC32(msg, CRC_32, tam)
        if verif == 1:
            print("No se detectaron errores, el payload final es: ", from_array_to_string(payload))
        elif verif == -1:
            print("Se detectaron errores, el mensaje final se descarta")

    elif algorithm == "Hamming":
        original_message, had_error = hamming_decode(noisy_message)
        if had_error:
            print(f"Mensaje original: {original_message} con corrección de errores")
        else:
            print(f"Mensaje original: {original_message} sin errores detectados")

    conn.close()

if __name__ == "__main__":
    start_server()