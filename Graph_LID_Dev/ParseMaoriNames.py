
import sqlite3

def name_exists_in_db(name):
    conn = sqlite3.connect("NamesDB.db")
    c = conn.cursor()
    
    c.execute("SELECT COUNT(*) FROM names WHERE name=? AND language=?", (name, "Maori"))
    result_set = c.fetchall()
    result_set = result_set[0][0]
    retBool = False
    if (result_set >= 1):
        retBool = True
    c.close()
    conn.close()
    return retBool

def add_name_to_db(name):

    conn = sqlite3.connect("NamesDB.db")
    c = conn.cursor()
    this_language = "Maori"
    this_source = "https://www.waikato.ac.nz/library/resources/digital-collections/index-of-maori-names/browse-the-fletcher-index/?alphabet=W"
    c.execute("INSERT INTO names VALUES(?,?,?)", (name, this_language, this_source))
    conn.commit()
    c.close()
    conn.close()


names_file = open("MaoriNames.txt", "r")
array_of_names = names_file.readlines()
names_file.close()

for i in range(0, len(array_of_names)):
    this_name = array_of_names[i]
    while(this_name[0] == " "):
        this_name = this_name[1:]
    this_name = this_name.replace("\n", "")
    this_name = this_name.lower()
    if not(name_exists_in_db(this_name)):
        add_name_to_db(this_name)    
        print("Added " + this_name + " to the db")
        
print ("FINISHED!!!")    

