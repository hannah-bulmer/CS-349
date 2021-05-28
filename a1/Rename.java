import java.io.File;
import java.lang.Runtime;
import java.text.DecimalFormat;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

public class Rename {

    public static HashMap<String,ArrayList<String>> map=new HashMap<String,ArrayList<String>>();
    public static HashSet<String> set = new HashSet<>(Arrays.asList(
            "-h", "-help", "-f", "-file", "-p", "-prefix", "-s", "-suffix", "-r", "-replace"));

    public static void main(String[] args) {
        Rename runner = new Rename();

        runner.processArgs(args);

        System.out.println(Arrays.asList(map)); // method 1
    }

    public static void returnError(String err) {
        System.out.println(err);
        System.exit(0);
    }

    public static void printHelp() {
        System.out.println("I am the help menu!");
        System.exit(0);
    }

    Rename() {}

    public void processArgs(String[] args) {
        int len = args.length;
        if (len == 0) returnError("Error, please add at least one flag");

        String curFlag = "";
        for (int i = 0; i < len; i ++) {
            String arg = args[i];
            if (arg.charAt(0) == '-') {
                if (set.contains(arg)) {
                    String key = arg.substring(0,2);
                    System.out.println("Adding " + key);
                    curFlag = key;
                    if (map.containsKey(key)) returnError("Error, flag " + key + " given more than once");
                    map.put(key, new ArrayList<>());
                }
            } else {
                // argument
                if (curFlag.equals("")) returnError("Error, please add at least one flag");
                if (curFlag.equals("-h")) returnError("Error, -help does not take any arguments");
                ArrayList<String> arguments = map.get(curFlag);
                if (curFlag.equals("-r") && arguments.size() == 2)
                    returnError("Error, -replace only takes 2 arguments");

                // convert @date and @time into the date/time
                arguments.add(arg);
            }
        }

        // print help menu
        if (map.containsKey("-h")) printHelp();

        // error checking

        // need at least 1 file provided
        if (!map.containsKey("-f")) returnError("Error, please use -f option to add a file");
        if (map.get("-f").size() == 0) returnError("Error, please specify at least one file");
        // need at least 1 of replace, suffix, prefix
        if (!map.containsKey("-r") && !map.containsKey("-p") && !map.containsKey("-s"))
            returnError("Error, please add some options for how you would like to rename your files");

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

