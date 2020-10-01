package jselect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    opt = new Option("s", "selector", true, "CSS-style selector");
    opt.setArgName("selector");
    opt.setRequired(true);
    options.addOption(opt);
    opt = new Option("a", "attribute", true, "attribute name");
    opt.setArgName("name");
    options.addOption(opt);
    opt = new Option("e", "echo-content", false, "echo HTML content to stdout");
    opt.setArgName("name");
    options.addOption(opt);
    CommandLine cmd = null;
    try {
      CommandLineParser parser = new DefaultParser();
      cmd = parser.parse(options, args);
    } catch(ParseException e) {
      usage(options);
      System.exit(1);
    }
    
    String url = cmd.getOptionValue("url");
    String selector = cmd.getOptionValue("selector");
    String content = readContent(url, cmd.hasOption("echo-content"));
    Document doc = Jsoup.parse(content);
    Elements elements = doc.select(selector);
    if (elements.size() == 0) {
      log.error("element not found for selector " + selector);
      System.exit(1);
    }
    if (elements.size() > 1) {
      log.error("multiple elements found for selector " + selector);
      System.exit(1);
    }
    Element element = elements.get(0);
    System.out.println("*");
    //usage(options);
  }

  private String readContent(String url, boolean echo) {
    BufferedReader reader = null;
    try {
      URL myurl = new URL(url);
      HttpsURLConnection connection = (HttpsURLConnection) myurl.openConnection();
      InputStream stream = connection.getInputStream();
      reader = new BufferedReader(new InputStreamReader(stream));
      String line;
      StringBuffer sb = new StringBuffer();
      while ((line = reader.readLine()) != null) {
        if (echo) {
          System.out.println(line);
        }
        sb.append(line);
      }
      return sb.toString();
    } catch(Exception e) {
      log.error("unable to read content", e);
      return null;
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch(Exception e) {
        }
      }
    }
  }
  
  private void usage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    System.out.println("jselect version " + version);
    formatter.printHelp("jselect", options);
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
