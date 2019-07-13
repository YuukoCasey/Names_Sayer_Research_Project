import sqlite3

conn = sqlite3.connect("NamesDB.db")
c = conn.cursor()

c.execute("DELETE FROM names WHERE source=?", ("",))
conn.commit()

print("Mistakes deleted")

c.close()
conn.close()

