package Request;

import Model.User;
import Model.Person;
import Model.Event;

/**
 * This implements the Service Load class
 * This is a request to load User, Person, Event data into the DB
 * So need all of the info from the User, Person and Event classes to make this request
 */
public class LoadRequest {
    /**
     * All of the users that will get from User class to load into the DB
     */
    private User[] users;
    /**
     * All of the people that will get from Person class to load into the DB
     */
    private Person[] persons;
    /**
     * All of the events that will get from Event class to load into the DB
     */
    private Event[] events;

    /**
     * This creates a Request to load all of the data from the User, Person and Event classes into the DB
     * @param users A list of all of the users that get from the User class
     * @param persons A list of all of the people that get from the Person class
     * @param events A list of all of the events that get from the Event class
     */
    public LoadRequest(User[] users, Person[] persons, Event[] events) {
        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPeople() {
        return persons;
    }

    public void setPeople(Person[] persons) {
        this.persons= persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents() {
        this.events = events;
    }
}
