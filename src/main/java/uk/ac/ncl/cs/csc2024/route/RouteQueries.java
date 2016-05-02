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

    private static final int RAILWAY_STATION_STOP_ID_1 = 9015;
    private static final int RAILWAY_STATION_STOP_ID_2 = 9016;
    private static final String OK_TRAVEL_NAME = "OK Travel";

    public static Session insert(Map<String, String> row, Session session) {
        Route route = new Route();

        // Weirdly enough, route 'number' is actually a string :-/, so don't bother parsing it.
        String routeNumberString = row.get("number");
        route.setNumber(routeNumberString);

        String encodedOperatorStrings = row.get("operators");

        Set<Operator> operators = QueryUtilities.parseOperatorsFromEncodedString(session, encodedOperatorStrings);

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

                String startStopIdProperty = "r.startStop.id";
                String destinationStopIdProperty = "r.destinationStop.id";

                // The start stop OR destination stop ID must equal one of the two Railway Station stop IDs to be selected by this query.
                Criterion startStopId1 = Property.forName(startStopIdProperty).eq(RAILWAY_STATION_STOP_ID_1);
                Criterion startStopId2 = Property.forName(startStopIdProperty).eq(RAILWAY_STATION_STOP_ID_2);
                Criterion destinationStopId1 = Property.forName(destinationStopIdProperty).eq(RAILWAY_STATION_STOP_ID_1);
                Criterion destinationStopId2 = Property.forName(destinationStopIdProperty).eq(RAILWAY_STATION_STOP_ID_2);

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

                // Add up the frequency of each OK Travel route to get cumulative frequency...
                criteria.setProjection(Projections.sum("frequencyPerOperator"));

                return criteria;
            }
        };
    }


}
