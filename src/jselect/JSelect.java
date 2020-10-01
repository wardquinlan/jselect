package jselect;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JSelect {
  private static Log log = LogFactory.getFactory().getInstance(JSelect.class);
  private static String version = "0.10";
  
  public JSelect(String[] args) throws Exception {
    loadProperties();
    Options options = new Options();
    Option opt = new Option("u", "url", true, "source URL");
    opt.setArgName("url");
    opt.setRequired(true);
    options.addOption(opt);
    usage(options);
  }

  private void usage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("jselect version " + version, options);
  }
  
  private void loadProperties() {
    ClassLoader cl = ClassLoader.getSystemClassLoader();  
    try {
      InputStream is = cl.getResourceAsStream("jselect.properties");
      if (is == null) {
        log.error("cannot load properties");
        System.exit(1);
      }
      System.getProperties().load(is);    
    } catch(IOException e) {
      log.error("cannot load properties", e);
      System.exit(1);
    }
  }
  
  public static void main(String[] args) {
    try {
      new JSelect(args);
    } catch(Exception e) {
      log.error(e);
    }
  }
}
