import csv

JOTFORM_FILENAME = "jotform.csv"
FORUM_FILENAME = "Google forum.csv"
WP_FILENAME = "Wordpress.csv"

'''
Reconcile OFFC membership lists

Jotform - source list which has the correct members
Google Forums - old list
Wordpress - old list
'''
def main():

    # read master Jotforms.csv
    master_dict = read_file(JOTFORM_FILENAME)
    # print_dict(master_dict)

    # read copy Google Forum.csv
    google_dict = read_file(FORUM_FILENAME)
    # print_dict(google_dict)

    # read copy from Wordpress.csv
    wp_dict = read_file(WP_FILENAME)
    # print_dict(wp_dict)

    # Jotforms - Google forums = new members
    google_adds = remove_dict(master_dict, google_dict)
    print("These are to be added to the forum")
    print_dict(google_adds)
    # Google forums - Jotforms = deleted members
    google_removes = remove_dict(google_dict, master_dict)
    print("These are to be removed from the forum")
    print_dict(google_removes)

    # Jotforms - WP = new adds for Wordpress
    wp_adds = remove_dict(master_dict, wp_dict)
    print("These are to be added to WordPress")
    print_dict(google_adds)
    wp_removes = remove_dict(wp_dict, master_dict)
    print("These are to be removed from wordpress")
    print_dict(wp_removes)


'''
Remove the removes dictionary from the source dictionary if the keys are the same
'''
def remove_dict(source, removes):
    result = {}
    for name in source:
        if name not in removes:
            result[name] = source[name]
    return result

'''
pretty print a dictionary of names
'''
def print_dict(dict):
    count = 0
    for name in dict:
        print(name + " / " + dict[name])
        count += 1
    print(f"printed {count} entries")
    print()

'''
Read an input CSV file.  Skip header line and read fields depending upon which file
Jotform: FIRST     LAST    EMAIL
Google Forum: EMAIL     NICKNAME
Wordpress: USER_LOGIN    USER_EMAIL
'''
def read_file(filename):
    result_dict = {}
    with open(filename, newline='') as csvfile:
        reader = csv.DictReader(l.upper() for l in csvfile)
        for row in reader:
            if filename == JOTFORM_FILENAME:
                result_dict[row['E-MAIL']] = row['FIRST NAME'] + row['LAST NAME']
            elif filename == FORUM_FILENAME:
                result_dict[row['EMAIL ADDRESS']] = row['NICKNAME']
            else: # Wordpress
                result_dict[row['USER_EMAIL']] = row['USER_LOGIN']

    print(f"read {len(result_dict)} names in {filename}\n")
    return result_dict

if __name__ == '__main__':
    main()

