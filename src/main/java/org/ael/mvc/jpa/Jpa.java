package org.ael.mvc.jpa;

public abstract class Jpa<T, ID> {

     abstract T findById(ID id);
     abstract T deleteById(ID id);

}
