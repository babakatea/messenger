from math import log2
import random as r

class SFE:
    def __init__(self):
        self.byte_size = 8

    # converts float to binary
    def float2bin(self, x, bit='0.'):
        LIM = 16
        if len(bit) == LIM:
            return float(bit)

        if 2 * x > 1:
            bit += '1'
            return self.float2bin(2 * x - 1, bit)

        elif 2 * x < 1:
            bit += '0'
            return self.float2bin(2 * x, bit)

        else:
            bit += '1'
            return float(bit)

    # void input –> array of byte converted to string
    def getbytes(self, input):
        bytes = []
        # filling list 'bytes'
        for i in range(0, len(input), self.byte_size):
            byte = ''
            for j in range(i, i + self.byte_size):
                byte += str(input[j])
            bytes.append(byte)

        return bytes

    # returns a list of lists [[bytes], [probabilities]]
    def gettable(self, input):
        # activating previous step
        bytes = self.getbytes(input)
        # # of bytes
        total = len(input) / self.byte_size
        # creating dictionary out of 'bytes'
        reference = dict()
        for byte in bytes:
            reference[byte] = reference.get(byte, 0) + 1

        # Since dictionaries cannot be sorted, we are creating a sorted list representation instead
        # sorting array by decreasing frequency
        sort = sorted(reference, key=reference.get, reverse=True)
        # in 2nd array values are replaced by byte probability (value / total)
        return [[k for k in sort], [float(reference[k] / total) for k in sort]]

    # returns a dictionary {byte(str) : code(str)}
    def getdictionary(self, input):
        # activating previous step
        table = self.gettable(input)

        codes = []

        for i in range(len(table[1])):
            # sum of previous probabilities
            prev_prob = sum(table[1][:i])
            # finding # of most significant bits to pick from binary float
            sig_bits = int(log2(1 / table[1][i])) + 1
            # converting function value to string of binaries
            binary = '{:.16f}'.format(self.float2bin(prev_prob + table[1][i] * 0.5)).split('.')[1]
            # append code to list
            codes.append(binary[:sig_bits])

        return dict(zip(table[0], codes))

    # returns input compressed
    def encode(self, input):
        coded = []
        bytes = self.getbytes(input)
        dictionary = self.getdictionary(input)

        for i in range(len(bytes)):
            code = list(dictionary.get(bytes[i]))
            for i in range(len(code)):
                coded.append(int(code[i]))

        return coded

    def decode(self, input, dictionary):
        decoded = []
        code = ''

        for i in range(len(input)):
            code += str(input[i])
            for key, value in dictionary.items():
                if value == code:
                    code = list(key)
                    for i in range(len(code)):
                        decoded.append(int(code[i]))
                    code = ''

        return decoded
