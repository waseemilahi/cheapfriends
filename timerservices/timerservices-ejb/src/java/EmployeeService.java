/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David
 */

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;

@Stateless(mappedName="ejb.employee")
public class EmployeeService implements EmployeeServiceRemote {
  @Resource
  javax.ejb.TimerService timerService;

  public EmployeeService() {
  }

  public void doAction() {
    String item = "item 1";

    for (Object obj : timerService.getTimers()) {
      javax.ejb.Timer timer = (javax.ejb.Timer) obj;
      String scheduled = (String) timer.getInfo();
      if (scheduled.equals(item)) {
        timer.cancel();
      }
    }

    timerService.createTimer(1, item);

  }


  @Timeout
  public void maintenance(javax.ejb.Timer timer) {
    System.out.println("TIMEOUT METHOD CALLED");
    String scheduled = (String) timer.getInfo();
    System.out.println(scheduled);
  }
}

