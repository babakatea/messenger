from math import log2

class SFE:
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

    # returns a list of lists [[bytes], [probabilities]]
    def gettable(self, input):
        # creating dictionary out of 'bytes'
        reference = dict()
        for byte in input:
            reference[byte] = reference.get(byte, 0) + 1

        # Since dictionaries cannot be sorted, we are creating a sorted list representation instead
        # sorting array by decreasing frequency
        sort = sorted(reference, key=reference.get, reverse=True)
        # in 2nd array values are replaced by byte probability (value / total)
        return [[k for k in sort], [float(reference[k] / len(input)) for k in sort]]

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
        dictionary = self.getdictionary(input)

        for i in range(len(input)):
            coded.append(dictionary.get(input[i]))

        return coded

    def decode(self, input, dictionary):
        decoded = []
        code = ''

        for i in range(len(input)):
            code += str(input[i])
            for key, value in dictionary.items():
                if value == code:
                    decoded.append(key)
                    code = ''

        return decoded

a = [11111111, 00000000, 00000000]
print(a)

b = SFE()
print(b.encode(a))
print(b.getdictionary(a))
print(b.decode(b.encode(a), b.getdictionary(a)))