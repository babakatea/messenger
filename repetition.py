# Repetition encoding/decoding algo


class Repetition:
    def __init__(self, input):
        self.input = input
        self.repetition = 3
        self.coded = []
        self.decoded = []

    # returns encode input
    def encode(self):

        for i in range(len(self.input)):
            # repeat each bit 'repetition' # of times and add to array
            for j in range(self.repetition):
                self.coded.append(self.input[i])

        return self.coded

    # returns most common bit in array
    def most_common(self, array):
        zero = 0
        one = 0
        for i in range(len(array)):
            if array[i] == 0:
                zero += 1
            else:
                one += 1

        return 0 if zero > one else 1

    # returns decoded array
    def decode(self):
        # coded array is divided into sub-arrays of size 'repetition'
        for i in range(0, len(self.coded), self.repetition):
            # sub-arrays are sent to function 'most_common', the returned bit is appended to 'decoded' array
            self.decoded.append(self.most_common(self.coded[i:i + self.repetition]))

        return self.decoded




a = [1, 0, 1, 1, 1, 1]

b = Repetition(a)
b.encode()
b.decode()
print(b.coded)
print(b.decoded)
