
import sqlite3

conn = sqlite3.connect("TestingResults.db")
c = conn.cursor()

c.execute("SELECT AVG(Accuracy) FROM TestResults WHERE language = ?", ("English",))
res = c.fetchall()
res = res[0][0]

print ("Average accuracy for ID'ing English names is " + str(res))

c.execute("SELECT AVG(Accuracy) FROM TestResults WHERE language=?", ("Maori",))
res = c.fetchall()
res = res[0][0]
print ("Average accuracy for ID'ing Maori names is " + str(res))

c.execute("SELECT AVG(Accuracy) FROM TestResults WHERE language=?", ("Samoan",))
res = c.fetchall()
res = res[0][0]
print ("Average accuracy for ID'ing Samoan names is " + str(res))

c.close()
conn.close()
