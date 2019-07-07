import sqlite3

def personal_title_transformer(input_string):
	
	input_string = input_string[0].upper() + input_string[1:].lower()
	if (len(input_string) > 0):
		for i in range(1, len(input_string)):
			if (input_string[i-1] == " "):
				input_string = input_string[0:i] + input_string[i].upper() + input_string[i+1:]

	return input_string
	
	
	
conn = sqlite3.connect("NamesDB.db")
c = conn.cursor()

while(True):

	input_string = input("Enter a name you wish to search for: ")
	
	should_break = False
	for i in range(0, len(input_string)):
		if (ord(input_string[i]) == 27):
			should_break = True
	if (should_break):
		break

	input_string = personal_title_transformer(input_string)

	c.execute("SELECT language FROM names WHERE name = ?", (input_string,))
	list_of_languages = c.fetchall()
	
	if (len(list_of_languages) <= 0):
		print("This name is not in this database for any language")
		print("\n\n")
		continue
	else: 
		print("This name has been found in the database under the following languages: ")
		for i in range(0, len(list_of_languages)):
			print("\t" + list_of_languages[i][0])
		print("\n\n")

c.close()
conn.close()
