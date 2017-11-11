#SFE compression algo

from math import log2
import random as r

class SFE:
    def __init__(self, input):
        self.input = input
        self.byte_size = 8
        self.bytes = []
        self.table = None
        self.dictionary = None
        self.coded = []
        self.decoded = []


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
    def getbytes(self):
        # filling list 'bytes'
        for i in range(0, len(self.input), self.byte_size):
            byte = ''
            for j in range(i, i + self.byte_size):
                byte += str(self.input[j])
            self.bytes.append(byte)

    # returns a list of lists [[bytes], [probabilities]]
    def gettable(self):
        # activating previous step
        self.getbytes()
        # # of bytes
        total = len(self.input) / self.byte_size
        # creating dictionary out of 'bytes'
        reference = dict()
        for byte in self.bytes:
            reference[byte] = reference.get(byte, 0) + 1

        # Since dictionaries cannot be sorted, we are creating a sorted list representation instead
        # sorting array by decreasing frequency
        sort = sorted(reference, key=reference.get, reverse=True)
        # in 2nd array values are replaced by byte probability (value / total)
        self.table = [[k for k in sort], [float(reference[k] / total) for k in sort]]

    # returns a list of lists [[byte:str], [code:str]]
    def getcode(self):
        # activating previous step
        self.gettable()

        codes = []

        for i in range(len(self.table[1])):
            # sum of previous probabilities
            prev_prob = sum(self.table[1][:i])
            # finding # of most significant bits to pick from binary float
            sig_bits = int(log2(1 / self.table[1][i])) + 1
            # converting function value to string of binaries
            binary = '{:.16f}'.format(self.float2bin(prev_prob + self.table[1][i] * 0.5)).split('.')[1]
            # append code to list
            codes.append(binary[:sig_bits])

        self.dictionary = dict(zip(self.table[0], codes))

    # returns input compressed
    def encode(self):
        # activating previous step
        self.getcode()

        for i in range(len(self.bytes)):
            code = list(self.dictionary.get(self.bytes[i]))
            for i in range(len(code)):
                self.coded.append(int(code[i]))

        return self.coded

    def decode(self):
        code = ''

        for i in range(len(self.coded)):
            code += str(self.coded[i])
            for key, value in self.dictionary.items():
                if value == code:
                    code = list(key)
                    for i in range(len(code)):
                        self.decoded.append(int(code[i]))
                    code = ''

        return self.decoded




a = [r.randint(0, 1) for i in range(256)]

b = SFE(a)
b.encode()
b.decode()
print(b.coded)
print(b.decoded)

