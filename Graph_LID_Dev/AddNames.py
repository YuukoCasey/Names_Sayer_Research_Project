import sqlite3

conn = sqlite3.connect('NamesDB.db')
c = conn.cursor()

c.execute("CREATE TABLE IF NOT EXISTS names(name TEXT, language TEXT, source TEXT);")
conn.commit()

while (True):
	input_name = input("What name would you like to enter? ")
#	input_name = input_name.title()

#	if (len(input_name) > 0):
#		if (input_name[0].islower()):
#			input_name = input_name[0].upper() + input_name[1:].lower()

	should_giveup = False
	for i in range(0, len(input_name)):
		if (ord(input_name[i]) == 27): #If escape key was pressed
			should_giveup = True
			break
	if (should_giveup):
		print("\n\n\n\n\n")
		continue


	input_name = input_name[0].upper() + input_name[1:].lower()
	if (len(input_name) > 0):
		for i in range(1, len(input_name)):
			if (input_name[i-1] == " "):
#				input_name[i] = input_name[i].upper()				input_name = input_name[0:i] + input_name[i].upper()
				input_name = input_name[0:i] + input_name[i].upper() + input_name[i+1:]

	question_string = "What language is the name '" + input_name + "' from? "
	input_language = input(question_string)
#	input_language = input_language.title()

	should_giveup = False
	for i in range(0, len(input_language)):
		if (ord(input_language[i]) == 27):
			should_giveup = True
			break
	if (should_giveup):
		print("\n\n\n\n\n")
		continue


	input_language = input_language[0].upper() + input_language[1:].lower()

	if (len(input_language) > 0):
		for i in range(1, len(input_language)):
			if (input_language[i-1] == " "):
#				input_language[i] = input_language[i].upper()
				input_languge = input_language[0:i] + input_language[i].upper() + input_language[i+1:]

	input_source = ""
	if (input_language == "Maori"):
		input_source = "https://www.taiuru.maori.nz/maori-baby-names-list/"

	c.execute("SELECT name FROM names WHERE name = ? AND language = ?;", (input_name, input_language))
	query_result = c.fetchall()

	if (len(query_result) == 0):
		c.execute("INSERT INTO names(name, language, source) VALUES (?,?,?);", (input_name, input_language, input_source))
		conn.commit()

	else:
		print("This name is already in this database under this language. Please select another name or language")

#	user_answer = input("Would you like to add more names? ")
#	if (user_answer.lower() == "n" or user_answer.lower() == "no"):
#		break
	print("\n\n\n\n\n")

c.close()
conn.close()
