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
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import uk.ac.ncl.cs.csc2024.busstop.BusStop;
import uk.ac.ncl.cs.csc2024.operator.Operator;
import uk.ac.ncl.cs.csc2024.query.ExampleQuery;
import uk.ac.ncl.cs.csc2024.query.QueryUtilities;

import java.sql.PreparedStatement;
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
        String routeNumberString = row.get("number");
        route.setNumber(routeNumberString);

        String encodedOperatorStrings = row.get("operators");

        Set<Operator> operators = parseOperatorsFromEncodedString(session, encodedOperatorStrings);

        route.setOperators(operators);

        // Parse integers from the Map...
        String frequencyString = row.get("frequency");
        int frequency = Integer.parseInt(frequencyString);

        String startStopIdNumberString = row.get("start");
        int startStopId = Integer.parseInt(startStopIdNumberString);


        String destinationStopIdNumberString = row.get("destination");
        int destinationStopId = Integer.parseInt(destinationStopIdNumberString);

        route.setFrequency(frequency);


        // Now instantiate stops from stop ID numbers and attach them to the route...

        // Look for the start stop...
        BusStop startStop = QueryUtilities.findBusStopWithID(session, startStopId);
        route.setStartStop(startStop);

        // Find the destination stop...
        BusStop destinationStop = QueryUtilities.findBusStopWithID(session, destinationStopId);
        route.setDestinationStop(destinationStop);

        session.save(route);

        return session;

    }

    private static Set<Operator> parseOperatorsFromEncodedString(Session session, String encodedOperatorStrings) {
        // A placeholder array to hold the route's potentially multiple operators in...
        Set<Operator> operators = new HashSet<Operator>();

        // Parse the '|' separated string of operators, if there's more than one...
        if (encodedOperatorStrings.contains("|")) {
            // Continue with parse by splitting on | char
            String[] encodedNamesSplit = encodedOperatorStrings.split("\\|");

            // Add them all to the list...
            for (String operatorName : encodedNamesSplit) {
                // Instantiate Operator from name; add to operators array
                Operator operator = QueryUtilities.findOperatorByName(session, operatorName);
                operators.add(operator);
            }
        } else {
            // Single operator; no parse necessary - just add the 1 operator name to the array and continue
            // Instantiate Operator from name; add to operators array
            Operator operator = QueryUtilities.findOperatorByName(session, encodedOperatorStrings);
            operators.add(operator);
        }

        return operators;
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
                Order ascendingOrder = Order.asc("r.number");
                criteria.addOrder(ascendingOrder);
                return criteria;
            }
        };
    }


    public static ExampleQuery selectAllForRailwayStation() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery("select distinct r from Route r where r.startStop.id =  9015 OR r.startStop.id = 9016 OR r.destinationStop.id = 9015 OR r.destinationStop.id = 9016");

            }

            @Override
            public String getNamedQueryName() {
                return Route.SELECT_ALL_FOR_RAILWAY_STATION;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(Route.class, "r");
                Order ascendingOrder = Order.asc("r.number");
                criteria.addOrder(ascendingOrder);

                Disjunction logicalOr = Restrictions.disjunction();
                Criterion startStopId1 = Property.forName("r.startStop.id").eq(9015);
                Criterion startStopId2 = Property.forName("r.startStop.id").eq(9016);
                Criterion destinationStopId1 = Property.forName("r.destinationStop.id").eq(9015);
                Criterion destinationStopId2 = Property.forName("r.destinationStop.id").eq(9016);

                logicalOr.add(startStopId1);
                logicalOr.add(startStopId2);
                logicalOr.add(destinationStopId1);
                logicalOr.add(destinationStopId2);

                criteria.add(logicalOr);

                return criteria;
            }
        };
    }

    public static ExampleQuery cumulativeFrequencyByOkTravel() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery("select sum(r.frequency * 0.75) from Route r join r.operators o where o.name = 'OK Travel'");

            }

            @Override
            public String getNamedQueryName() {
                return Route.CUMULATIVE_FREQUENCY_BY_OK_TRAVEL;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(Route.class, "r");

                // I looked up the use of createAlias at https://stackoverflow.com/questions/6744941/hibernate-criteria-with-many-to-many-join-table
                criteria.createAlias("r.operators", "o");
                criteria.add(Restrictions.eq("o.name", "OK Travel"));


                criteria.setProjection(Projections.sum("frequencyPerOperator"));

                return criteria;
            }
        };
    }


}
