import java.io.File;
import java.lang.Runtime;
import java.text.DecimalFormat;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Rename {

    public static String date;
    public static String time;
    public static Map<String,ArrayList<String>> map=new LinkedHashMap<String,ArrayList<String>>();
    public static HashSet<String> set = new HashSet<>(Arrays.asList(
            "-h", "-help", "-f", "-file", "-p", "-prefix", "-s", "-suffix", "-r", "-replace"));

    public static void main(String[] args) {
        Rename runner = new Rename();
        runner.processArgs(args);

        ArrayList<String> oldFilenames = new ArrayList<>(map.get("-f"));
        ArrayList<String> files = map.get("-f");

        for (Map.Entry<String,ArrayList<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> values = entry.getValue();

            if (key.equals("-f")) continue;
            if (key.equals("-r")) runner.runReplace(files, values.get(0), values.get(1));
            if (key.equals("-p")) runner.runPrefix(files,values);
            if (key.equals("-s")) runner.runSuffix(files,values);

        }

        System.out.println(Arrays.asList(files));
        System.out.println(Arrays.asList(oldFilenames));

        if (files.size() != oldFilenames.size())
            returnError("Something bad occured");

        // rename files
        for (int i = 0; i < files.size(); i ++) {
            File file1 = new File(oldFilenames.get(i));
            File file2 = new File(files.get(i));

            boolean success = file1.renameTo(file2);
            if (success) {
                System.out.println("Successfully renamed " + file1 + " to " + file2);
            } else {
                System.out.println("Error renaming " + file1 + " to " + " " + file2 + ". " +
                        "Check for duplicate filenames in same path. Contiuing...");
            }
        }
    }

    public static void returnError(String err) {
        System.out.println(err);
        System.exit(0);
    }

    public static void printHelp() {
        System.out.println("(c) 2021 Hannah Bulmer. Revised: May 28, 2021.");
        System.out.println("Usage: rename [-option argument1 argument2 ...]");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("-f|file [filename]          :: file(s) to change.");
        System.out.println("-p|prefix [string]          :: rename [filename] so that it starts with [string].");
        System.out.println("-s|suffix [string]          :: rename [filename] so that it ends with [string].");
        System.out.println("-r|replace [str1] [str2]    :: rename [filename] by replacing all instances of [str1] with [str2].");
        System.out.println("-h|help                     :: print out this help and exit the program.");
        System.exit(0);
    }

    Rename() {}

    public void runReplace(ArrayList<String> files, String val1, String val2) {
        for (int i = 0; i < files.size(); i ++) {
//            System.out.println("Replace " + val1 + " with " + val2 + " on file "  + files.get(i));
            if (files.get(i).equals(files.get(i).replace(val1, val2))) {
                System.out.println("Running -replace on " + files.get(i) + " made no changes");
            } else if (files.contains(files.get(i).replace(val1, val2)))
                returnError("Error: running -replace causes duplicate filenames to occur");
            files.set(i, files.get(i).replace(val1, val2));
        }
    }

    public void runPrefix(ArrayList<String> files, ArrayList<String> values) {
        for (String val: values) {
            for (int i = 0; i < files.size(); i ++) {
                if (files.contains(val+files.get(i)))
                    returnError("Error: running -prefix causes duplicate filenames to occur");
//                System.out.println("Adding prefix to file " + files.get(i));
                files.set(i, val+files.get(i));
            }
        }
    }

    public void runSuffix(ArrayList<String> files, ArrayList<String> values) {
        for (String val: values) {
            for (int i = 0; i < files.size(); i ++) {
                if (files.contains(files.get(i)+val))
                    returnError("Error: running -suffix causes duplicate filenames to occur");
//                System.out.println("Adding suffix to file " + files.get(i));
                files.set(i, files.get(i)+val);
            }
        }
    }

    public void getDateAndTime() {
        Date today = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        date = dateFormatter.format(today);
        time = timeFormatter.format(today);
    }

    public void findFile(String file) {
        // makes directories valid
        File f = new File(file);
        if (!f.exists()) {
            String errMsg = "Error: file " + f.toString() + " does not exist.";
            returnError(errMsg);
        }
//        try {
//            FileInputStream fis=new FileInputStream(file);
//        } catch (Exception e) {
//            String errMsg = "Error: file " + file + " had some problems. " +
//                    "The message from the client is: \n\t-> " + e.getMessage();
//            returnError(errMsg);
//        }
    }

    public void processArgs(String[] args) {
        int len = args.length;
        if (len == 0) returnError("Error: please add at least one flag");

        // get date and time
        this.getDateAndTime();

        String curFlag = "";
        for (int i = 0; i < len; i ++) {
            String arg = args[i];
            if (arg.charAt(0) == '-') {
                // flag
                if (set.contains(arg)) {
                    String key = arg.substring(0, 2);
                    curFlag = key;
                    if (map.containsKey(key)) returnError("Error: flag " + key + " given more than once");
                    map.put(key, new ArrayList<>());
                } else {
                    returnError("Error: flag " + arg + " not recognized");
                }
            } else {
                // argument
                if (curFlag.equals("")) returnError("Error: please add at least one flag");
                if (curFlag.equals("-h")) returnError("Error: -help does not take any arguments");
                ArrayList<String> arguments = map.get(curFlag);

                // convert @date and @time into the date/time
                if (arg.equals("@date")) arg = date;
                if (arg.equals("@time")) arg = time;
                arguments.add(arg);
            }
        }

        // print help menu
        if (map.containsKey("-h")) printHelp();

        // check for errors
        // need at least 1 file provided
        if (!map.containsKey("-f")) returnError("Error: please use -f option to add a file");
        if (map.get("-f").size() == 0) returnError("Error: please specify at least one file");

        // make sure all files are valid
        ArrayList<String> files = map.get("-f");
        for (String file: files) {
            this.findFile(file);
        }

        // if file is listed more than once, error
        HashSet<String> s = new HashSet<String>();
        for(String f : files) {
            if(!s.add(f)) {
                String err = "Error: File '" + f + "' was provided twice. Please only provide distinct filenames" +
                        " to avoid putting your files into invalid state";
                returnError(err);
            }
        }

        // need at least 1 of replace, suffix, prefix
        if (!map.containsKey("-r") && !map.containsKey("-p") && !map.containsKey("-s"))
            returnError("Error: please add some options for how you would like to rename your files");

        // if replace, need exactly 2 args
        if (map.containsKey("-r") && map.get("-r").size() != 2)
            returnError("Error using -r, please specify exactly two values");

        // if p or s, need at least 1 arg
        if (map.containsKey("-p") && map.get("-p").size() == 0)
            returnError("Error using -p, please specify at least one prefix");
        if (map.containsKey("-s") && map.get("-s").size() == 0)
            returnError("Error using -s, please specify at least one suffix");
    }
}

