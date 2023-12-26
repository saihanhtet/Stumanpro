package com.hanhtet.stumanpro.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetConnectionChecker {

  public static boolean isInternetAvailable() {
    try {
      URL url = new URL("https://www.google.com");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();

      int responseCode = connection.getResponseCode();
      return responseCode == HttpURLConnection.HTTP_OK;
    } catch (IOException e) {
      return false;
    }
  }

  public static void main(String[] args) {
    boolean isConnected = isInternetAvailable();
    System.out.println("Internet connection available: " + isConnected);
  }
}
