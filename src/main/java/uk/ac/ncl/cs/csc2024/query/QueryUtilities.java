package uk.ac.ncl.cs.csc2024.query;

import org.hibernate.Query;
import org.hibernate.Session;
import uk.ac.ncl.cs.csc2024.busstop.BusStop;
import uk.ac.ncl.cs.csc2024.operator.Operator;

import java.util.HashSet;
import java.util.Set;

/**
 * Convenience methods to find operator by name and find a bus stop by ID number, and parse the | separated string of
 * bus operator names into a Set of Operators.
 *
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

        return (Operator) operatorQuery.uniqueResult();

    }

    /**
     * Finds and returns the BusStop instance with the id that matches the id number passed into this method.
     *
     * @param session the Hibernate session to run this query within.
     * @param id the id of the Bus Stop instance to search for and return (iff it exists)
     * @return the Bus Stop instance that matches the ID being searched for, if one exists. Otherwise null.
     */
    public static BusStop findBusStopWithID(Session session, int id) {
        Query busStopIdQuery = session.createQuery(FIND_BUS_STOP_BY_ID_HQL_QUERY);
        busStopIdQuery.setInteger("id", id);

        return (BusStop) busStopIdQuery.uniqueResult();
    }

    /**
     * A convenience method to parse a String of Operator names, separated by the '|' char., into a Set of the
     * Operator instances that match those names.
     *
     * @param session the Hibernate session in which to conduct the relevant queries.
     * @param encodedOperatorStrings the String to parse the Operator names from.
     * @return a Set of Operator instances.
     */
    public static Set<Operator> parseOperatorsFromEncodedString(Session session, String encodedOperatorStrings) {
        // A placeholder array to hold the route's potentially multiple operators in...
        Set<Operator> operators = new HashSet<Operator>();

        // Parse the '|' separated string of operators, if there's more than one...
        if (encodedOperatorStrings.contains("|")) {
            // Continue with parse by splitting on | char
            String[] encodedNamesSplit = encodedOperatorStrings.split("\\|");

            // Add them all to the list...
            for (String operatorName : encodedNamesSplit) {
                // Instantiate Operator from name; add to operators array
                Operator operator = findOperatorByName(session, operatorName);
                operators.add(operator);
            }
        } else {
            // Single operator; no parse necessary - just add the 1 operator name to the array and continue
            // Instantiate Operator from name; add to operators array
            Operator operator = findOperatorByName(session, encodedOperatorStrings);
            operators.add(operator);
        }

        return operators;
    }


}
