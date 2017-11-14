class Repetition:
    def __init__(self):
        self.repetition = 3

    # returns encode input
    def encode(self, input):
        coded = []
        for i in range(len(input)):
            # repeat each bit 'repetition' # of times and add to array
            for j in range(self.repetition):
                coded.append(input[i])

        return coded

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
    def decode(self, input):
        decoded = []
        # coded array is divided into sub-arrays of size 'repetition'
        for i in range(0, len(input), self.repetition):
            # sub-arrays are sent to function 'most_common', the returned bit is appended to 'decoded' array
            decoded.append(self.most_common(input[i:i + self.repetition]))
