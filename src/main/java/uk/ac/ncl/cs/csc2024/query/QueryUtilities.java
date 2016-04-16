package uk.ac.ncl.cs.csc2024.query;

import org.hibernate.Query;
import org.hibernate.Session;
import uk.ac.ncl.cs.csc2024.busstop.BusStop;
import uk.ac.ncl.cs.csc2024.operator.Operator;

import java.util.List;

/**
 * Created by djmckee on 16/04/2016.
 */
public class QueryUtilities {

    public static Operator findOperatorByName(Session session, String name) {
        // Look for the operator with the name we've been passed...
        Query operatorQuery = session.createQuery("select o from Operator o where o.name=:name");
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

    public static BusStop findBusStopWithID(Session session, int id) {
        Query sessionQuery = session.createQuery("select s from BusStop s where s.id=:id");
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
