/**
 * csc2024-hibernate-assignment
 *
 * Copyright (c) 2015 Newcastle University
 * Email: <h.firth@ncl.ac.uk/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ncl.cs.csc2024.route;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import uk.ac.ncl.cs.csc2024.busstop.BusStop;
import uk.ac.ncl.cs.csc2024.operator.Operator;
import uk.ac.ncl.cs.csc2024.query.ExampleQuery;

import java.util.*;

/**
 * Collection of Queries relating to Route entities
 *
 * Task: fill out method bodies to create "Queries" which satisfy the requirements laid out in the coursework spec.
 * Other than the `insert(...)` method, where you should return the session to which an Entity object has been persisted,
 * you should return an ExampleQuery object.
 *
 * The methods of the ExampleQuery objects should return a Query, a Named Query's identifier and a Criteria Query for
 * each task laid out in the coursework spec. You should return every query type for each task.
 *
 * An example of how this should look is provided in the `selectAll(...)` query.
 *
 * @author hugofirth
 */
public class RouteQueries {

    public static Session insert(final Map<String, String> row, final Session session) {
        Route route = new Route();

        // Weirdly enough, route 'number' is actually a string :-/, so don't bother parsing it.
        route.setNumber(row.get("number"));


        // Parse integers from the Map...
        int frequency = Integer.parseInt(row.get("frequency"));
        int startStopId = Integer.parseInt(row.get("start_stop_id"));
        int destinationStopId = Integer.parseInt(row.get("destination_stop_id"));

        route.setFrequency(frequency);


        // Now instantiate stops from stop ID numbers and attach them to the route...

        // Look for the start stop...
        BusStop startStop = findBusStopWithID(session, startStopId);
        route.setStartStop(startStop);

        // Find the destination stop...
        BusStop destinationStop = findBusStopWithID(session, destinationStopId);
        route.setDestinationStop(destinationStop);

        String encodedOperatorStrings = row.get("operator_names");

        // A placeholder array to hold the route's potentially multiple operators in...
        Set<Operator> operators = new HashSet<Operator>();

        // Parse the '|' separated string of operators, if there's more than one...
        if (encodedOperatorStrings.contains("|")) {
            // Continue with parse by splitting on | char
            String[] encodedNamesSplit = encodedOperatorStrings.split("|");

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

        route.setOperators(operators);

        session.save(route);

        return session;

    }

    private static Operator findOperatorByName(Session session, String name) {
        // Look for the operator with the name we've been passed...
        Query operatorQuery = session.createQuery("select o from Operator o where o.name='" + name + "'");
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

    private static BusStop findBusStopWithID(Session session, int id) {
        Query sessionQuery = session.createQuery("select s from BusStop s where s.id=" + id);
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

    public static ExampleQuery selectAll() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery("select r from Route r order by r.number asc");
            }

            @Override
            public String getNamedQueryName() {
                return Route.SELECT_ALL;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(Route.class, "r");
                criteria.addOrder(Order.asc("r.number"));
                return criteria;
            }
        };
    }


    public static ExampleQuery selectAllForRailwayStation() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return null;
            }

            @Override
            public String getNamedQueryName() {
                return null;
            }

            @Override
            public Criteria getCriteria(Session session) {
                return null;
            }
        };
    }

    public static ExampleQuery cumulativeFrequencyByOkTravel() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return null;
            }

            @Override
            public String getNamedQueryName() {
                return null;
            }

            @Override
            public Criteria getCriteria(Session session) {
            	return null;
            }
        };
    }


}
