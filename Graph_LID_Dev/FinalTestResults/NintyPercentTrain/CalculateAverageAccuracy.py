
import sqlite3

conn = sqlite3.connect("TestingResults.db")
c = conn.cursor()

for i in range(0, 99):
	if (i%10 == 0 and i > 0):
		c.execute("SELECT AVG(Accuracy) FROM TestResults WHERE language = ? AND trainingPercent=?", ("English",float(i)))
		res = c.fetchall()
		res = res[0][0]
		print ("Average accuracy for ID'ing English names is " + str(res) + " when accuracy is " + str(i) + "%")


		c.execute("SELECT AVG(Accuracy) FROM TestResults WHERE language=? AND trainingPercent=?", ("Maori",float(i)))
		res = c.fetchall()
		res = res[0][0]
		print ("Average accuracy for ID'ing Maori names is " + str(res) + " when accuracy is " + str(i) + "%")


		c.execute("SELECT AVG(Accuracy) FROM TestResults WHERE language=? AND trainingPercent=?", ("Samoan",float(i)))
		res = c.fetchall()
		res = res[0][0]
		print ("Average accuracy for ID'ing Samoan names is " + str(res) + " when accuracy is " + str(i) + "%\n\n\n")

c.close()
conn.close()
