package it.dohyun.recipe_hub.listener;

import it.dohyun.recipe_hub.util.firebase.FirebaseUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class FirebaseInitListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      FirebaseUtil.initialize();
      System.out.println("[Firebase] Initialization successful.");
    } catch (Exception e) {
      System.err.println("[Firebase] Initialization failed: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    System.out.println("[Firebase] Context destroyed.");
  }
}
