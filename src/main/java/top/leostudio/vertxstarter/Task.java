/*
    Responsible: Leo Feng
*/
package top.leostudio.vertxstarter;

import java.util.UUID;

/**
 * Created by Leo on 2021/12/15.
 */
public class Task
{
  public String id;
  public String description;
  public boolean completed;

  public Task(String description)
  {
    id = UUID.randomUUID().toString();
    this.description = description;
  }

}
