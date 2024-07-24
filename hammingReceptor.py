def hamming_decode(encoded):
    n = len(encoded)
    r = 0

    # Encontrar el número de bits de paridad
    while (2**r) < n + 1:
        r += 1

    # Convertir la cadena a una lista para modificarla
    encoded = list(encoded)

    # Verificar los bits de paridad
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

    # Determinar si hay un error y corregirlo
    if error_position != 0:
        print(f"Error detectado en la posición: {error_position}")
        error_position -= 1  # Ajustar a índice de lista
        encoded[error_position] = '0' if encoded[error_position] == '1' else '1'
        print("Error corregido.")
    else:
        print("No se detectaron errores.")

    # Extraer los bits de datos
    original_message = []
    for i in range(n):
        if not is_power_of_two(i + 1):
            original_message.append(encoded[i])

    original_message = ''.join(original_message)
    return original_message, error_position != 0

def is_power_of_two(num):
    return (num & (num - 1)) == 0 and num != 0

encoded_message = input("Ingrese el mensaje codificado: ")
original_message, had_error = hamming_decode(encoded_message)
print(f"Mensaje original: {original_message}")
