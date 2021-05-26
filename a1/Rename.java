import java.io.File;
import java.lang.Runtime;
import java.text.DecimalFormat;
import java.net.InetAddress;

public class Rename {

  static DecimalFormat formatter = new DecimalFormat("###,###,###,###");

  static final String ANSI_RESET = "\u001B[0m";
  static final String ANSI_BLACK = "\u001B[30m";
  static final String ANSI_RED = "\u001B[31m";
  static final String ANSI_GREEN = "\u001B[32m";
  static final String ANSI_YELLOW = "\u001B[33m";
  static final String ANSI_BLUE = "\u001B[34m";
  static final String ANSI_PURPLE = "\u001B[35m";
  static final String ANSI_CYAN = "\u001B[36m";
  static final String ANSI_WHITE = "\u001B[37m";

  public static void main(String[] args) {
    new Rename();
  }

  Rename() {
    try {

      System.out.println();
      System.out.println("  ;)(;  " + " Vendor:  " + System.getProperty("java.vendor"));
      System.out.println(" :----: " + " JDK:     " + System.getProperty("java.vm.name"));
      System.out.println("C|====| " + " Version: " + System.getProperty("java.version"));
      System.out.println(" |    | " + " Memory:  " + Runtime.getRuntime().totalMemory()/(1024*1024) + " MB");
      System.out.println(" `----' " + " Free:    " + Runtime.getRuntime().freeMemory()/(1024*1024) + " MB");
      System.out.println();

    } catch (Exception ex) {
      System.out.println(ex.toString());
    }
  }
}

