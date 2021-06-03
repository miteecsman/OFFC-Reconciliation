OFFC Reconciliation

The Overlake Fly Fishing Club maintains its membership list online but also uses other online tools.  This program synchronizes the main online membership with other lists.  You must export or manually scrape the lists from each website and put it into a CSV format.

The tool was originally written in Java, but the Python version is much simpler and should be used going forward.  The Python csv library allows one to very simply refer to fields in files by their headers.

Jotform is the main membership list which is updated online directly by members each year.  The process to get the membership list is a little complicated because there are so many report options, and then extra columns are removed.

Wordpress is a secondary list which must be sychronized to the main list.  We have individual wordpress accounts in order to support member blog posts.  This was added after the Java program's creation and is not supported by it.

Google forums is a secondary list which must be synchronized to the main list.  The export process is very simple and is just a single button click on the Google Forum user interface.

Evite is used for in-person dinner invites.  Due to the pandemic, support for this was temporarily dropped and is not included in the Python version.  The process to get the contacts was very complicated and manual.  In the future, Python's Beautiful Soup library might be used to automate the scraping of the contacts.