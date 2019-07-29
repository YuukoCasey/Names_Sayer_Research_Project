
import sqlite3
import os

def hasANumber(input_str):
	for i in range(0, len(input_str)):
		if (input_str[i] == "0" or input_str[i] == "1" or input_str[i] == "2" or input_str[i] == "3" or input_str[i] == "4" or input_str[i] == "4" or input_str[i] == "5" or input_str[i] == "6" or input_str[i] == "7" or input_str[i] == "8" or input_str[i] == "9"):
			return True
	return False

def separateWords(arrayOfLines):
	returnList = [""]
	for i in range(0, len(arrayOfLines)):
		temp_var = arrayOfLines[i].split()
		for j in range(0, len(temp_var)):
			returnList.append(temp_var[j])
	return returnList

def getTextFromFile(filename):
	path = os.getcwd() + "/" + filename
	reading_file = open(path, 'r')
	return reading_file.readlines()

conn = sqlite3.connect("TextDB.db")
c = conn.cursor()

while(True):
	#input_text = input("\nCopy in the text you wish to add to the database:\n\t")
	#input_text = input_text.replace('"', "")
	
	#input_text = input_text.title()
	#buffer_input = input("")
	input_text = input("\nEnter the name of the text file you wish to use:\n\t")
	arrayOfWords = getTextFromFile(input_text)
	
	arrayOfWords = separateWords(arrayOfWords)

	for i in range(0, len(arrayOfWords)):
		#arrayOfWords[i] = arrayOfWords[i].replace('\"', "")
		this_word = arrayOfWords[i]
		
		print("Original word length is " + str(len(this_word)))
		print("Original string is '" + this_word + "'")
		for j in range(0, len(this_word)):

			if (j >= len(this_word)):
				break

			print("j is " + str(j))
			examine_var = this_word[j]

			if(ord(examine_var) == 8220 or ord(examine_var) == 8221 or ord(examine_var) == 34):
				print("Removing the character " + examine_var)
				this_word = this_word.replace(examine_var, "")

				print("this_word is now '" + this_word + "' with a length of " + str(len(this_word)))
				j = j - 1


		if (this_word.upper() == this_word):
			this_word = this_word.title()
		arrayOfWords[i] = this_word

	input_language = input("\nEnter the language of this text:\n\t")
	
	input_language = input_language.title()

	input_source = input("\nEnter the source of this text:\n\t")
	
	index_num = 0
	for i in range(0, len(arrayOfWords)):
		#if (hasANumber(arrayOfWords[i])):
		#	continue
		#elif (arrayOfWords[i] == ""):
		#	continue
		#c.execute("INSERT INTO text(Word, Language, source) VALUES (?,?,?);", (arrayOfWords[i], input_language, input_source))
		#conn.commit()
		#print("Added " + arrayOfWords[i] + "\n")
		if ((not hasANumber(arrayOfWords[i])) and (not(arrayOfWords[i] == "")) ):
			c.execute("INSERT INTO text(id, Word, Language, Source) VALUES (?,?,?,?);", (index_num, arrayOfWords[i], input_language, input_source))
			conn.commit()
			print("Added " + arrayOfWords[i] + "\n")
			index_num = index_num + 1
	print("\n\n\n\n\n")
	
c.close()
conn.close()
