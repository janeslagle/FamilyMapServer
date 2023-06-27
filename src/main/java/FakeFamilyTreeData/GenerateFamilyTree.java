package FakeFamilyTreeData;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import com.google.gson.*;
import java.sql.Connection;
import java.util.Random;
import java.util.UUID;

import DAO.*;
import Model.*;

public class GenerateFamilyTree {
    private FemaleNames fNames;     // Will store all female names from fnames file
    private LocationData locData;   // Will store entire data list from locations file
    private MaleNames mNames;       // Will store all male names from mnames file
    private LastNames sNames;       // Will store all last names from snames file
    private int numPeople;          // Will keep track of number people create in family tree
    private int numEvents;          // Will keep track of number events create in family tree

    // Read in, get all data out of json files
    // Put in try catch block since working with file reader
    public void ReadInData() {
        try {
            // When read the data in, are starting out with no people or events in the family tree
            numPeople = 0;
            numEvents = 0;

            Gson gson = new Gson();

            // Female names file
            Reader reader = new FileReader("json/fnames.json");
            fNames = (FemaleNames) gson.fromJson(reader, FemaleNames.class);

            // Locations file
            reader = new FileReader("json/locations.json");
            locData = (LocationData) gson.fromJson(reader, LocationData.class);

            // Male names file
            reader = new FileReader("json/mnames.json");
            mNames = (MaleNames) gson.fromJson(reader, MaleNames.class);

            // Last names file
            reader = new FileReader("json/snames.json");
            sNames = (LastNames) gson.fromJson(reader, LastNames.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Now write all methods will need for generating the family tree

    // Method that is called by RegisterService + FillService, so starts everything out for generating ancestors of given user
    public void generateAncestorData(Connection connection, User user, int generations) throws DataAccessException {
        // Set user's / person/s properties
        // Creating ancestors for user so first create Person class obj for them
        // Use random, unique strings for IDs to create the person
        Person theCurrentPerson = new Person(user.getPersonID(), user.getUsername(), user.getFirstName(), user.getLastName(),
                user.getGender(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());

        personDAO thePersonDAO = new personDAO(connection);
        thePersonDAO.createNewPerson(theCurrentPerson);    // Save the person in the DB
        numPeople++;                                       // Just made a person so increment counter variable to reflec that!

        // Generate all events for the person are currently on (only need birth event for the user that are generating the ancestors for)
        // Event locations can be random so get random location out of LocationData class to use for the birth event
        Locations location = getRandomLocation();

        // Set the event type and event ID for the user's birth
        String eventType = "birth";
        String eventID = UUID.randomUUID().toString();

        // Create the event class obj, can use whatever year I want for it
        Event userBirthEvent = new Event(eventID, user.getUsername(), user.getPersonID(), location.getLatitude(),
                location.getLongitude(), location.getCountry(), location.getCity(), eventType, 2000);

        // Create eventDAO class so that can actually create (insert) the event into the DB / family tree
        eventDAO userBirthEventDAO = new eventDAO(connection);
        userBirthEventDAO.insert(userBirthEvent);
        numEvents++;                                       // Just made an event so increment counter variable to reflect that!

        // Have all of that before check if generations is greater than 0 bc if generations = 0, only want create person and their birth event
        // Only want to create parents for the person if generations is greater than or equal to 1

        // So do that next - make all of the ancestors (mom's + dad's) for the person!
        if (generations > 0) {
            // Call generatePerson func that went over in implementation video
            // This will generate parents for the person currently at so plug them in
            generatePerson(connection, theCurrentPerson, generations);
        }
    }

    // Follows structure of implementation video example, will generate the parents for the person inserted into the func
    // Will call recursively within itself and create parents, grandparents, great grandparents, etc. for the number of generations inputted
    // So creates all of the ancestors recursively at once
    public void generatePerson(Connection connection, Person theChild, int generations) throws DataAccessException {
        // Will user random names from fNames, mNames and sNames files when create the ancestors so will need random obj to do so
        Random rand = new Random();

        // Create mother, father person class objects
        Person mom;
        Person dad;

        // Use the motherID to get the correct personID when make the mom
        // Grab random last name for the mom bc the mom will have her maiden name technically I think
        // Use the child's fatherID to get the correct spouseID for the mom (the child's mom will be married to the child's dad)
        // Use random, unique strings for the mom's own parent IDs and then those will be recycled through correctly for each generation since the mom will be the child for the next generation round
        mom = new Person(theChild.getMotherID(), theChild.getAssociatedUsername(), fNames.getData()[rand.nextInt(fNames.getData().length)],
                sNames.getData()[rand.nextInt(sNames.getData().length)], "f", UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                theChild.getFatherID());

        // The father and child will have the same last name as each other
        dad = new Person(theChild.getFatherID(), theChild.getAssociatedUsername(), mNames.getData()[rand.nextInt(mNames.getData().length)],
                theChild.getLastName(), "m", UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                theChild.getMotherID());

        // When get to last generation round, don't want the last set of ancestors to have father and mother IDs (bc they are the last mom + dad)
        // So will need to set those IDs for them to be null bc just set them as random strings above
        // Have found through trial and error that the last round for generations want to make will be when generations equals 1
        if (generations == 1) {
            mom.setFatherID(null);
            mom.setMotherID(null);
            dad.setFatherID(null);
            dad.setMotherID(null);
        }

        // Now actually create the mom + dad person objects using personDAO class
        personDAO thePersonDAO = new personDAO(connection);
        thePersonDAO.createNewPerson(mom);
        numPeople++;
        thePersonDAO.createNewPerson(dad);
        numPeople++;

        // Generate all events for both parents (need birth, death, and marriage events for them)
        generateAncestorBirthEvent(connection, mom, theChild);
        generateAncestorBirthEvent(connection, dad, theChild);

        // Need generate their death events before make their marriage event bc will need info about their death (like their ages) when make their marriage event
        generateAncestorDeathEvent(connection, mom, theChild);
        generateAncestorDeathEvent(connection, dad, theChild);

        // Now generate the marriage event between the 2 parents just created! (will be based on their birth and death events just generated)
        // Need create eventDAO obj so that can insert the marriage events into DB after create them
        eventDAO theEventDAO = new eventDAO(connection);

        // Need get the birth, death events for both people in the marriage bc they can both only be married in between those 2 events
        // Cannot be married before both of them are born or after either of them are dead
        Event motherBirth = theEventDAO.findPersonBirthEvent(mom.getPersonID());
        Event motherDeath = theEventDAO.findPersonDeathEvent(mom.getPersonID());
        Event fatherBirth = theEventDAO.findPersonBirthEvent(dad.getPersonID());
        Event fatherDeath = theEventDAO.findPersonDeathEvent(dad.getPersonID());

        // Find the valid range of years where the 2 inputted people can be married to each other
        int lowerMarriageYear;
        int upperMarriageYear;

        // Go through all different possible cases could have with the 2 people and their ages

        // First get the very first year the 2 people could get married
        // 1st case: the first person inputted is older than the second person
        if (motherBirth.getYear() < fatherBirth.getYear()) {
            // Then the 2 can only get married 13 yrs AFTER the second person is born (13 yrs after the second person is born is the very first year they could get married in)
            lowerMarriageYear = fatherBirth.getYear() + 13;
        }
        // 2nd case: the second person inputted is older than the first person (so same as 1st case but the 2 people are swapped now)
        else {
            lowerMarriageYear = motherBirth.getYear() + 13;
        }

        // Now get the very last year the 2 people could get married
        // 1st case: the firstPerson dies before the 2nd (they can't get married if the one person is dead, so this is the last year they could get married)
        if (motherDeath.getYear() < fatherDeath.getYear()) {   // Dying in a year that is less means they die first
            upperMarriageYear = motherDeath.getYear();
        }
        // 2nd case: the second person dies before the 1st (same case but swap the 2 people)
        else {
            upperMarriageYear = fatherDeath.getYear();
        }

        // They can be married in any year in this range so find any random number in that range
        int yearMarried = rand.ints(lowerMarriageYear, upperMarriageYear).findAny().getAsInt();

        // Now actually create the event object
        String eventType = "Marriage";

        // Each event will have a unique eventID, but both marriage events must have matching years and locations
        // so BOTH people need a marriage eventID associated with them so make 2
        String firstPersonEventID = UUID.randomUUID().toString();
        String otherPersonEventID = UUID.randomUUID().toString();

        // Event location can be random
        // Get all of the location info that will use for event now since both people will need that same info
        Locations location = getRandomLocation();
        Float latitude = location.getLatitude();
        Float longitude = location.getLongitude();
        String country = location.getCountry();
        String city = location.getCity();

        // Now actually create the marriage events for both people in the marriage (both people need an event)
        Event motherMarriage = new Event(firstPersonEventID, mom.getAssociatedUsername(), mom.getPersonID(),
                latitude, longitude, country, city, eventType, yearMarried);
        Event fatherMarriage = new Event(otherPersonEventID, dad.getAssociatedUsername(), dad.getPersonID(),
                latitude, longitude, country, city, eventType, yearMarried);

        theEventDAO.insert(motherMarriage);
        numEvents++;
        theEventDAO.insert(fatherMarriage);
        numEvents++;

        // Loop through, create mom + dad (ancestors) for all number of generations want, recursive call to do so
        // Again, found that when get to generations = 1, are on the last generations want make
        if (generations > 1) {
            // Decrement generations each time loop through to reflect how once get here, just made 1 generation
            generatePerson(connection, mom, generations - 1);
            generatePerson(connection, dad, generations - 1);
        }
    }

    // Get a random location from LocationData class to use for generating any events will need
    // Do by getting random index that will use to access location data list have to get a random location out of it - woohoo
    public Locations getRandomLocation() {
        Random rand = new Random();

        return locData.getData()[rand.nextInt(locData.getData().length)];
    }

    // Generate the ancestor's birth event (the person currently on when create the ancestors)
    // Need the person's child inputted because will get the birth event of the child so that can figure out the age that the person needs be when they are born
    // All relevant info about birth events:
        // Parents must be 13 yrs older than their children
        // Parent can't be dead
        // Women must be less than 50 yrs old
        // Event location can be random
    public void generateAncestorBirthEvent(Connection connection, Person currentPerson, Person theirChild) throws DataAccessException {
        // Generating birth event so will need an eventDAO class obj
        eventDAO theEventDAO = new eventDAO(connection);

        // Need get the birth event for the person's child so that can build the person's event based on their child's age
        // So use the eventDAO method wrote for getting the birth event for the inputted personID
        Event theirChildsBirth = theEventDAO.findPersonBirthEvent(theirChild.getPersonID());

        // Figure out the possible range of years that the person could have been born based on their child's age
        int upperBornYear = theirChildsBirth.getYear() - 13;   // Parent HAS be at least 13 yrs older than their child, subtract 13 instead of adding since working with years here
        int lowerBornYear = theirChildsBirth.getYear() - 50;   // Mother HAS be less than 50 yrs old when their child is born so this is the oldest they can be, just use this same restriction for fathers too

        // The year they can be born in will be any random number btw the upper, lower bounds just found so create a random int that is within that range of lowerBornYear - upperBornYear
        Random rand = new Random();                            // Will need random obj to be able create int within range
        int yearBorn = rand.ints(lowerBornYear, upperBornYear).findAny().getAsInt();

        // Now actually create the birth event for the person
        Locations location = getRandomLocation();
        String eventType = "birth";
        String eventID = UUID.randomUUID().toString();

        Event birthEvent = new Event(eventID, currentPerson.getAssociatedUsername(), currentPerson.getPersonID(),
                location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                eventType, yearBorn);

        theEventDAO.insert(birthEvent);
        numEvents++;
    }

    // Generate the ancestor's death event (the person currently on when create the ancestors)
    // Need the person's child inputted because will get the birth event of the child so that can figure out the age that the person needs be when they are dead
    // All relevant info bout death events:
        // Must be ailve before child born
        // Must be less than 120 yrs old (cant be older than that)
        // Event location can be random
    public void generateAncestorDeathEvent(Connection connection, Person currentPerson, Person theirChild) throws DataAccessException {
        eventDAO theEventDAO = new eventDAO(connection);

        // Need the birth events for BOTH the current person + their child so that can figure out if they are able to die yet, etc.
        Event theirBirth = theEventDAO.findPersonBirthEvent(currentPerson.getPersonID());
        Event theirChildsBirth = theEventDAO.findPersonBirthEvent(theirChild.getPersonID());

        // Figure out what year they can die
        int lowerDeathYear = theirChildsBirth.getYear();  // Person can't be dead before their child is born, so at minimum they can die the same year their child was born
        int upperDeahYear = theirBirth.getYear() + 120;   // No person can be older than 120 yrs old, so they MUST die 120 years after their birth

        // The year they are able to die will be any random number between the lowerDeathYear and upperDeathYear range so find such a random number
        Random rand = new Random();
        int yearDied = rand.ints(lowerDeathYear, upperDeahYear).findAny().getAsInt();

        // Now actually create the death event
        Locations location = getRandomLocation();
        String eventType = "death";
        String eventID = UUID.randomUUID().toString();

        Event deathEvent = new Event(eventID, currentPerson.getAssociatedUsername(), currentPerson.getPersonID(),
                location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                eventType, yearDied);

        theEventDAO.insert(deathEvent);
        numEvents++;
    }

    // Use to be able to access the number of people added when generated the family tree
    public int getNumPeople() {
        return numPeople;
    }

    // Use to be able to access the number of events added when generated the family tree
    public int getNumEvents() {
        return numEvents;
    }
}


