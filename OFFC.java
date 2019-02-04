import javax.swing.JFileChooser;
import java.io.File;    
import java.util.*;
import java.io.*;

public class OFFC {


    /*
     * 10/22/18 Version 1.1 - added comments
     *
     * See OFFC Reconciliation Help.txt for usage on these 3 source files:
     * 
     * OFFC Emails.csv -
     * OFFC Evite Emails.csv -
     * OFFC Forum Emails.csv -
     * 
     * set the ifDebug flag to true to print out entries as they are scanned
     */

    public static final boolean ifDebug = false;
    public static void main(String[] args) throws FileNotFoundException {

        // Display help
        displayHelp();

        // read Membership
        Scanner scMembership = prepareFileForScanning("OFFC Emails.csv");
        Map<String, String> mapMembership = readMembership(scMembership);

        // read forum
        Scanner scForum = prepareFileForScanning("OFFC Forum Emails.csv");
        Map<String, String> mapForum = readEmailList(scForum);

        // read evite
        Scanner scEvite = prepareFileForScanning("OFFC Evite Emails.csv");
        Map<String, String> mapEvite = readEviteList(scEvite);


        // Evite

        // Find spares that need to be removed
        Map<String, String> mapEviteCopy = copyMap(mapEvite);
        mapEviteCopy.keySet().removeAll(mapMembership.keySet()); 
        //        System.out.println("\nThese need to be deleted from Evite\n");
        //        printMap(mapEviteCopy, " -Evite");

        // Find new names to be added
        Map<String, String> mapMembershipEviteCopy = copyMap(mapMembership);
        mapMembershipEviteCopy.keySet().removeAll(mapEvite.keySet()); 
        //        System.out.println("\nThese need to be added to Evite\n");
        //        printMap(mapMembershipEviteCopy, " +Evite");

        // Forum

        // Find spares that need to be removed
        Map<String, String> mapForumCopy = copyMap(mapForum);
        mapForumCopy.keySet().removeAll(mapMembership.keySet()); 
        //        System.out.println("\nThese need to be deleted from Forum\n");
        //        printMap(mapForumCopy, " -Forum");

        // Find new names to be added
        Map<String, String> mapMembershipForumCopy = copyMap(mapMembership);
        mapMembershipForumCopy.keySet().removeAll(mapForum.keySet()); 
        //        System.out.println("\nThese need to be added to Forum\n");
        //        printMap(mapMembershipForumCopy, " +Forum");

        // Build a combined add/subtract list

        // Remove from Evite
        Map<String, String> mapCombined = new TreeMap<>();
        for (Map.Entry<String, String> entry : mapEviteCopy.entrySet())
            mapCombined.put(entry.getKey()+"-E", entry.getValue());

        // Add to Evite
        for (Map.Entry<String, String> entry : mapMembershipEviteCopy.entrySet())
            mapCombined.put(entry.getKey()+"+E", entry.getValue());

        // Remove from Forum
        for (Map.Entry<String, String> entry : mapForumCopy.entrySet()) {
            // if it's already removed from the other, replace it with "B" for both
            if (mapCombined.containsKey(entry.getKey()+"-E")) {
                mapCombined.remove(entry.getKey()+"-E");
                mapCombined.put(entry.getKey()+"-B", entry.getValue());
            }
            else
                mapCombined.put(entry.getKey()+"-F", entry.getValue());
        }

        // Add to Forum
        for (Map.Entry<String, String> entry : mapMembershipForumCopy.entrySet()) {
            // if it's already removed from the other, replace it with "B" for both
            if (mapCombined.containsKey(entry.getKey()+"+E")) {
                mapCombined.remove(entry.getKey()+"+E");
                mapCombined.put(entry.getKey()+"+B", entry.getValue());
            }
            else
                mapCombined.put(entry.getKey()+"+F", entry.getValue());
        }

        // Print results
        printResults(mapCombined);
    }

    /**
     * Displays the help page on how to use this
     */
    public static void displayHelp() throws FileNotFoundException {
        File f = new File("OFFC Reconciliation Help.txt");
        if (f.canRead()) {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
            System.out.println();
        } else
            System.out.println("Can't find " + "OFFC Reconciliation Help.txt");


    }

    /**
     * Print out the results from a map of the combined actions
     * 
     * @param mapCombined
     */
    public static void printResults(Map<String, String> mapCombined) {
        System.out.println("\nThis is the combined list of all actions sorted by emails\n");     
        printMap(mapCombined, " +/-");

        Map<String, String> mapDup = copyMap(mapCombined);

        System.out.println("\nThese are the lists by action sorted by emails\n");     
        printAndRemoveMap(mapCombined, " remove Both ", "-B");
        System.out.println();
        printAndRemoveMap(mapCombined, " add Both", "+B");
        System.out.println();
        printAndRemoveMap(mapCombined, " add Evite", "+E");
        System.out.println();
        printAndRemoveMap(mapCombined, " remove Evite", "-E");
        System.out.println();
        printAndRemoveMap(mapCombined, " add Forum", "+F");
        System.out.println();
        printAndRemoveMap(mapCombined, " remove Forum", "-F");
        System.out.println();

        System.out.println("\n These are the lists by destination action\n");

        printEmailsOnly(mapDup, " add Evite", "+E");
        System.out.println();
        printEmailsOnly(mapDup, " add Evite Both", "+B");
        System.out.println();
        printEmailsOnly(mapDup, " remove Evite", "-E");
        System.out.println();
        printEmailsOnly(mapDup, " remove Evite Both", "-B");
        System.out.println();
        printEmailsOnly(mapDup, " add Forum", "+F");
        System.out.println();
        printEmailsOnly(mapDup, " add Forum Both", "+B");
        System.out.println();
        printEmailsOnly(mapDup, " remove Forum", "-F");
        System.out.println();
        printEmailsOnly(mapDup, " remove Forum Both", "-B");
        System.out.println();





    }

    public static HashMap<String, String> copyMap(Map<String, String>original)
    {
        HashMap<String, String> copy = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : original.entrySet())
        {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    /**
     * Just prints the maps
     * 
     * @param mp
     * @param sPrefix
     */
    public static void printMap (Map<String, String> mp, String sPrefix) {
        int count = 1;
        for (String s : mp.keySet()) {
            System.out.println(count++ + sPrefix + ": " + s + "=>" + mp.get(s));
        }
    }

    /**
     * Prints and removes all strings with the suffix 
     * 
     * @param mp
     * @param sPrefix
     * @param sSuffix
     */
    public static void printAndRemoveMap (Map<String, String> mp, String sPrefix, String sSuffix) {
        int count = 1;
        Iterator<String> it = mp.keySet().iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (s.contains(sSuffix)) {
                System.out.println(count++ + sPrefix + ": " + s + "=>" + mp.get(s));
                it.remove();
            }
        }
    }

    /**
     * Same as above, but doesn't remove
     * 
     * @param mp
     * @param sPrefix
     * @param sSuffix
     * @param ifRemove
     */
    public static void printEmailsOnly (Map<String, String> mp, String sPrefix, String sSuffix) {
        System.out.println("\nPrinting items to: " + sPrefix + "\n");
        int count = 0;
        Iterator<String> it = mp.keySet().iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (s.contains(sSuffix)) {
                count++;
                System.out.println(mp.get(s)+ "," + s.substring(0,  s.length()-2));
            }
        }
        System.out.printf("\nA total of %d items were output", count);
    }

    /**
     * Opens filename and creates scanner.
     *   Will prompt for the file if it doesn't exist as expected
     * 
     * @param filename
     * @return Scanner to open file
     */
    public static Scanner prepareFileForScanning(String filename) {
        Scanner sc = null;
        File f = null;
        try {
            f = new File(filename);
            if (!f.canRead()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fileChooser.setDialogTitle(String.format("Locate the %s file", filename)); 
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    f = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + f.getAbsolutePath());
                }

                if (!f.canRead())
                    System.out.println("can't read the file: " + filename);
                else
                    System.out.println("\nreading file: " + filename);
            }
            sc =  new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("Can't find the file " + filename);
        }
        sc.useDelimiter("\\,|\\r|\\n");
        return sc;
    }

    /**
     * Reads the OFFC Membership Email list with Last, First, Pri, Alt columns
     * 
     * @param sc
     * @return
     */
    public static Map<String, String> readMembership(Scanner sc) {
        // discard Header line
        sc.nextLine();

        Map<String, String> mapMembership = new HashMap<>();

        Scanner scLine = null;
        while (sc.hasNextLine()) {
            String line = sc.nextLine().replaceAll("\"", "");
            scLine = new Scanner(line);
            scLine.useDelimiter("\\,|\\r|\\n");
            // could be 3 or 4 items ending in a comma (3 items) or return (4 items) so skip blanks

            // Combine First name + Last Name
            String last = scLine.next();
            String first = scLine.next();
            String name = first + " " + last;

            // Primary email always exists
            String email = nextEmail(scLine);
            if (email == null)
                System.out.printf("Bad email for %s %s\n", first, last);

            // Make map entry for johndoe@foo.com -> "John Doe"
            mapMembership.put(email, name);
            //            System.out.printf("pri: %s -> %s\n", email, name);
            if (ifDebug)
                System.out.printf("%s\n", email);

            // Alternate email is optional, could be blank
            if (scLine.hasNext()) {
                String alt = nextEmail(scLine);
                if (alt == null)
                    System.out.printf("Bad alternate email for %s %s\n", first, last);
                else 
                {
                    mapMembership.put(alt,  name);
                    //                    System.out.printf("alt: %s -> %s\n", email, name);
                    if (ifDebug)
                        System.out.printf("alternate: %s\n", alt);
                }
            }
        }
        System.out.printf("Read %d entries\n", mapMembership.size());
        return mapMembership;
    }

    /**
     * Reads the Google Forum list with name, email columns
     * 
     * @param sc
     * @return
     */
    public static Map<String, String> readEmailList(Scanner sc) {
        // discard Header line
        sc.nextLine();

        Map<String, String> mapEmail = new HashMap<>();

        Scanner scLine = null;
        while (sc.hasNextLine()) {
            // eliminate quotes
            String line = sc.nextLine().replaceAll("\"", "");
            scLine = new Scanner(line);

            // Use comma separated
            scLine.useDelimiter("\\,|\\r|\\n");
            // could be 3 or 4 items ending in a comma (3 items) or return (4 items) so skip blanks

            // read username
            String name = scLine.next();

            // Primary email always exists
            String email = nextEmail(scLine);
            if (email == null || email.length() == 0)
                System.out.printf("Bad email for username %s\n", name);

            // Make map entry for johndoe@foo.com -> "John Doe"
            mapEmail.put(email, name);
            //            System.out.printf("%s -> %s\n", email, name);
            if (ifDebug)
                System.out.printf("%s\n", email);

        }
        System.out.printf("Read %d entries\n", mapEmail.size());

        return mapEmail;
    }

    /**
     * Reads the Evite list with name on one line, email on next
     * 
     * @param sc
     * @return
     */
    public static Map<String, String> readEviteList(Scanner sc) {
        Map<String, String> mapEmail = new HashMap<>();

        sc.reset();
        while (sc.hasNextLine()) {
            // eliminate quotes
            String name = sc.nextLine().replaceAll("\"", "");

            // Primary email always exists
            String email = sc.nextLine();
            if (email == null || email.length() == 0)
                System.out.printf("Bad email for username %s\n", name);

            // Make map entry for johndoe@foo.com -> "John Doe"
            mapEmail.put(email, name);
            //            System.out.printf("%s -> %s\n", email, name);
            if (ifDebug)
                System.out.printf("%s, %s\n", name, email);

        }
        System.out.printf("Read %d entries\n", mapEmail.size());

        return mapEmail;
    }

    /**
     * Scans in an email address  case insensitive & validates that it's correct
     * returns null if invalid
     * 
     * @param sc
     * @return
     */
    public static String nextEmail(Scanner sc) {
        String email = sc.next().toLowerCase().replaceAll(" ",  "");
        if (email == null || email.length() == 0 || email.indexOf(".") == -1) {
            System.out.println("Found bad email address: " + email);
            return null;
        }
        return email;
    }
}
