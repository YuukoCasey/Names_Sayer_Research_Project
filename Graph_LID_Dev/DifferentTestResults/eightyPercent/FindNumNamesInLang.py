import sqlite3

conn = sqlite3.connect("NamesDB.db")
c = conn.cursor()

input_lang = input("Enter the language you wish to investigate: ")
c.execute("SELECT COUNT(*) FROM names WHERE language=?", (input_lang,))
names_num = c.fetchall()[0][0]
print("There are " + str(names_num)  + " names for the language " + input_lang)

c.close()
conn.close()

