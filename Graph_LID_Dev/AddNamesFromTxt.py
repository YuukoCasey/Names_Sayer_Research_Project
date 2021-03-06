
import sqlite3
import os

def nameExistsInDB(search_name, c):
	c.execute("SELECT name FROM names WHERE name=? AND Language=?", (search_name, "English"))
	res_list = c.fetchall()
	for i in range(0, len(res_list)):
		res_list[i] = res_list[i][0]
		if res_list[i].lower() == search_name.lower():
			return True
	return False

def separateWords(arrayOfLines):
	returnList = [""]
	for i in range(0, len(arrayOfLines)):
		temp_var = arrayOfLines[i].split()
		for j in range(0, len(temp_var)):
			returnList.append(temp_var[j])
	return returnList

conn = sqlite3.connect("NamesDB.db")
c = conn.cursor()

path = os.getcwd() + "/" + "EnglishPlaceNames.txt"
readingFile = open(path, 'r')
arrayOfWords = readingFile.readlines()
arrayOfWords = separateWords(arrayOfWords)

for i in range(0, len(arrayOfWords)):
	this_word = arrayOfWords[i]
	this_word = this_word.lower()
	if (not(this_word == "")):
		this_language = "English"
		this_source = "https://www.oxfordreference.com/view/10.1093/acref/9780199609086.001.0001/acref-9780199609086?btog=chap&hide=true&pageSize=100&skipEditions=true&sort=titlesort&source=%2F10.1093%2Facref%2F9780199609086.001.0001%2Facref-9780199609086"
		if(not(nameExistsInDB(this_word, c))):
			c.execute("INSERT INTO names VALUES(?,?,?)", (this_word, this_language, this_source))
			conn.commit()
			print("Added the name " + this_word)
c.close()
conn.close()
print("FINISHED ADDING NAMES")
