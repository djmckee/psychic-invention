package uk.ac.ncl.cs.csc2024.query;

import org.hibernate.Query;
import org.hibernate.Session;
import uk.ac.ncl.cs.csc2024.busstop.BusStop;
import uk.ac.ncl.cs.csc2024.operator.Operator;

import java.util.List;

/**
 * Convenience methods to find operator by name and find a bus stop by ID number.
 * I created this class to reduce code and logic duplication, and ensure that the
 * 'Queries' classes to not become cluttered with these accessor methods.
 *
 * Created by Dylan McKee on 16/04/2016.
 * @author Dylan McKee
 */
public class QueryUtilities {

    /**
     * The HQL Query to find a bus stop by name, to be used in and have the 'name' variable placeholder populated
     * by the findOperatorByName method.
     */
    private static final String FIND_OPERATOR_BY_NAME_HQL_QUERY = "select o from Operator o where o.name=:name";

    /**
     * The HQL Query to find a bus stop by name, to be used in and have the 'id' variable placeholder populated
     * by the findBusStopWithID method.
     */
    private static final String FIND_BUS_STOP_BY_ID_HQL_QUERY = "select s from BusStop s where s.id=:id";

    /**
     * Finds the Operator instance from the operators entirety table that matches the name passed into this method.
     *
     * @param session the Hibernate session to run the query within.
     * @param name the name of the Operator instance to search for.
     * @return the Operator instance that matches the name passed to this method; or null if no matching operator
     * could be found.
     */
    public static Operator findOperatorByName(Session session, String name) {
        // Look for the operator with the name we've been passed...
        Query operatorQuery = session.createQuery(FIND_OPERATOR_BY_NAME_HQL_QUERY);
        operatorQuery.setString("name", name);

        List<Operator> operatorResults = (List<Operator>) operatorQuery.list();

        // There should only be 1 match with that name...
        for (Operator operator : operatorResults) {
            // Sanity check
            if (operator.getName().equals(name)) {
                // It's definitely the desired operator because the name matches - return it.
                return operator;

            }
        }

        // Something went wrong. Null pointer exception waiting to happen...
        return null;

    }

    /**
     * Finds and returns the BusStop instance with the id that matches the id number passed into this method.
     *
     * @param session the Hibernate session to run this query within.
     * @param id the id of the Bus Stop instance to search for and return (iff it exists)
     * @return the Bus Stop instance that matches the ID being searched for, if one exists. Otherwise null.
     */
    public static BusStop findBusStopWithID(Session session, int id) {
        Query sessionQuery = session.createQuery(FIND_BUS_STOP_BY_ID_HQL_QUERY);
        sessionQuery.setInteger("id", id);

        List<BusStop> stops= (List<BusStop>) sessionQuery.list();

        // There should only be 1 match with that ID...
        for (BusStop stop : stops) {
            // Sanity check
            if (stop.getId() == id) {
                // It's definitely the desired stop because the IDs match...
                // We've found a match; return it and terminate the loop.
                return stop;
            }
        }

        // Something went wrong. Null pointer exception waiting to happen...
        return null;
    }

}
