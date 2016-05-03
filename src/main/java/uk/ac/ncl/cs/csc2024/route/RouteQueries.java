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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.type.DoubleType;
import org.hibernate.type.Type;
import uk.ac.ncl.cs.csc2024.busstop.BusStop;
import uk.ac.ncl.cs.csc2024.operator.Operator;
import uk.ac.ncl.cs.csc2024.query.ExampleQuery;
import uk.ac.ncl.cs.csc2024.query.QueryUtilities;

import java.util.Map;
import java.util.Set;

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
 * @author Modified by Dylan McKee
 */
public class RouteQueries {

    /**
     * The name of 'OK Travel' for use in the queries.
     */
    private static final String OK_TRAVEL_NAME = "OK Travel";

    /**
     * The description of the 'Railway Station' stops for use in the queries.
     */
    private static final String RAILWAY_STATION_STOP_DESCRIPTION = "Railway Station";

    /**
     * The SQL query to calculate the cumulative frequency per operator for a given route;
     * used in the 'cumulativeFrequencyByOkTravel' method.
     */
    private static final String CALCULATE_CUMULATIVE_OPERATOR_FREQUENCY_SQL_QUERY = "SUM(frequency / (select count(*) from operator_route where route_id=number)) as operator_frequency";

    public static Session insert(Map<String, String> row, Session session) {
        Route route = new Route();

        // Weirdly enough, route 'number' is actually a string :-/, so don't bother parsing it.
        String routeNumberString = row.get("number");
        route.setNumber(routeNumberString);

        String encodedOperatorStrings = row.get("operators");

        // Parse operators from the encoded string of operator names...
        Set<Operator> operators = QueryUtilities.parseOperatorsFromEncodedString(session, encodedOperatorStrings);

        route.setOperators(operators);

        // Parse integers from the Map...

        // Parse frequency from String to integer...
        String frequencyString = row.get("frequency");
        int frequency = Integer.parseInt(frequencyString);
        // Set frequency to the parsed integer...
        route.setFrequency(frequency);

        // Parse start stop ID number from string to integer to perform lookup.
        String startStopIdNumberString = row.get("start");
        int startStopId = Integer.parseInt(startStopIdNumberString);


        // Parse destination stop ID from String to integer to perform lookup.
        String destinationStopIdNumberString = row.get("destination");
        int destinationStopId = Integer.parseInt(destinationStopIdNumberString);



        // Now instantiate stops from stop ID numbers and attach them to the route...

        // Look for the start stop...
        BusStop startStop = QueryUtilities.findBusStopWithID(session, startStopId);
        route.setStartStop(startStop);

        // Find the destination stop...
        BusStop destinationStop = QueryUtilities.findBusStopWithID(session, destinationStopId);
        route.setDestinationStop(destinationStop);

        // Save the new route to insert it...
        session.save(route);

        return session;

    }

    public static ExampleQuery selectAll() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery(Route.SELECT_ALL_HQL_QUERY);
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
                return session.createQuery(Route.SELECT_ALL_RAILWAY_STATION_ROUTES_HQL_QUERY);

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

                // Using the disjunction operation to perform a logical OR...
                Disjunction logicalOr = Restrictions.disjunction();

                // Create aliases for the start stop and destination stop so that their descriptions can be queried
                criteria.createAlias("r.startStop", "sStop");
                criteria.createAlias("r.destinationStop", "dStop");

                // The start stop OR destination stop description must equal 'Railway Station' for this query.
                Criterion startStopDescription = Property.forName("sStop.description").eq(RAILWAY_STATION_STOP_DESCRIPTION);
                Criterion destinationStopDescription = Property.forName("dStop.description").eq(RAILWAY_STATION_STOP_DESCRIPTION);

                logicalOr.add(startStopDescription);
                logicalOr.add(destinationStopDescription);

                criteria.add(logicalOr);

                return criteria;
            }
        };
    }

    public static ExampleQuery cumulativeFrequencyByOkTravel() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery(Route.SELECT_CUMULATIVE_FREQUENCY_FOR_OK_TRAVEL_HQL_QUERY);

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

                // The name of one of the operators must equal OK Travel to be selected by this query...
                criteria.add(Restrictions.eq("o.name", OK_TRAVEL_NAME));

                // Cumulative frequency for a given route is the total frequency divided by the number of operators...
                criteria.setProjection(Projections.sqlProjection(CALCULATE_CUMULATIVE_OPERATOR_FREQUENCY_SQL_QUERY, new String[]{"operator_frequency"}, new Type[]{DoubleType.INSTANCE}));

                return criteria;
            }
        };
    }

}
