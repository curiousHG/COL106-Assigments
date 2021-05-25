import os
import difflib

outF = open("difference.txt", "w")

os.system("javac -d classes/ -cp classes/ IllegalNumberException.java")
os.system("javac -d classes/ -cp classes/ MMBurgersInterface.java")
os.system("javac -d classes/ -cp classes/ MMBurgers.java")
for i in range(1, 11):
    os.system(f'javac -d classes/ -cp classes/ TestCase{i}.java')
    os.system(f'java -cp classes/ TestCase{i}')
    outF.write(f"TestCase Num{i}")
    with open(f"Answer{i}.txt") as file_1:
        file_1_text = file_1.readlines()

    with open(f'StudentAnswer{i}.txt') as file_2:
        file_2_text = file_2.readlines()

    # Find and print the diff:
    for line in difflib.unified_diff(file_1_text, file_2_text, fromfile=f"Answer{i}.txt", tofile=f'StudentAnswer{i}.txt', lineterm=''):
        outF.write(line)
        outF.write("\n")

outF.close()
print("Done")
