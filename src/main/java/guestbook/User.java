package guestbook;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * We have to define an Object to store in our database. 
 * the fields of this object are then automaticly parsed into rows and columns.
 * 
 * For this to happen, we need to anotate it as a @Entity, aka, a something that is 
 * changeable and !should be stored!
 * 
 * Really do read this: https://spring.io/guides/gs/accessing-data-jpa
 * 
 * we could define in which table to store entries wie @Table, but the default is to store
 * in the <Classname> table, and I think thats a great idea!
 */
@Entity
public class User {
    
    /**
     * the id is the field over which the database differentiates our objects.
     * anotate it as such.
     * MUST BE NON-REPEATING
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * fields parsed by default since we defined this as a @Entity
     */
    private String username;
    private String password;
    private String role;

    /**
     * exists for jpa (java persistence api), not to be used
     */
    protected User(){}

    public User(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
