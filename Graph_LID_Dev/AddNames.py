import sqlite3

conn = sqlite3.connect('NamesDB.db')
c = conn.cursor()

c.execute("CREATE TABLE IF NOT EXISTS names(name TEXT, language TEXT);")

while (True):
	input_name = input("What name would you like to enter? ")
	input_name = input_name.title()

	question_string = "What language is the name '" + input_name + "' from? "
	input_language = input(question_string)
	input_language = input_language.title()

	c.execute("SELECT name FROM names WHERE name = ? AND language = ?;", (input_name, input_language))
	query_result = c.fetchall()

	if (len(query_result) == 0):
		c.execute("INSERT INTO names(name, language) VALUES (?,?);", (input_name, input_language))
		conn.commit()

	else:
		print("This name is already in this database under this language. Please select another name or language")

#	user_answer = input("Would you like to add more names? ")
#	if (user_answer.lower() == "n" or user_answer.lower() == "no"):
#		break
	print("\n\n\n\n\n")

c.close()
conn.close()
