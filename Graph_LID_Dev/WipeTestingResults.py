
import sqlite3

conn = sqlite3.connect("TestingResults.db")
c = conn.cursor()
c.execute("DELETE FROM TestResults")
conn.commit()

c.close()
conn.close()
