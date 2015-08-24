package com.ab.health.model;

public class User
{
  private int age;
  private float height;
  private Boolean sex;
  private float weight;

  public User(float paramFloat1, float paramFloat2)
  {
    this.weight = paramFloat1;
    this.height = paramFloat2;
  }
  
  public User()
  {
	  
  }

  public User(Boolean sex, int age, float weight, float height)
  {
    this.sex = sex;
    this.age = age;
    this.weight = weight;
    this.height = height;
  }

  public int getAge()
  {
    return this.age;
  }

  public float getHeight()
  {
    return this.height;
  }

  public Boolean getSex()
  {
    return this.sex;
  }

  public float getWeight()
  {
    return this.weight;
  }

  public void setAge(int paramInt)
  {
    this.age = paramInt;
  }

  public void setHeight(float paramFloat)
  {
    this.height = paramFloat;
  }

  public void setSex(Boolean paramBoolean)
  {
    this.sex = paramBoolean;
  }

  public void setWeight(float paramFloat)
  {
    this.weight = paramFloat;
  }
}