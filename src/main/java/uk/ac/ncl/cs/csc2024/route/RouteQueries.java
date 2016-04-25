/**
 * csc2024-hibernate-assignment
 * <p>
 * Copyright (c) 2015 Newcastle University
 * Email: <h.firth@ncl.ac.uk/>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import java.util.HashSet;
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
 * @author modified by Dylan McKee
 *
 */
public class RouteQueries {


    /**
     * The ID number for Railway Station Bus Stop 1.
     */
    public static final int RAILWAY_STATION_STOP_ID_1 = 9015;

    /**
     * The ID number for Railway Station Bus Stop 2.
     */
    public static final int RAILWAY_STATION_STOP_ID_2 = 9016;

    /**
     * The name of OK Travel for use in the query.
     */
    public static final String OK_TRAVEL_OPERATOR_NAME = "OK Travel";

    public static Session insert(Map<String, String> row, Session session) {
        Route route = new Route();

        // Weirdly enough, route 'number' is actually a string :-/, so don't bother parsing it.
        String routeNumber = row.get("number");
        route.setNumber(routeNumber);

        String encodedOperatorStrings = row.get("operators");

        Set<Operator> operators = parseOperatorsFromEncodedString(session, encodedOperatorStrings);

        route.setOperators(operators);

        // Parse integers from the Map...
        String frequencyString = row.get("frequency");
        int frequency = Integer.parseInt(frequencyString);
        String startStopIdString = row.get("start");
        int startStopId = Integer.parseInt(startStopIdString);
        String destinationStopIdString = row.get("destination");
        int destinationStopId = Integer.parseInt(destinationStopIdString);

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

    /**
     * A convenience method I added to parse the '|' separated String containing the names of the bus operators into a
     * Set containing the relevant Operator instances.
     * @author Dylan McKee
     * @param session the Hibernate session to run the Operator search queries on.
     * @param encodedOperatorStrings the '|' separated string of operator names to parse.
     * @return a Set of Operator instances that were encoded within the string passed to this method.
     */
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
                return session.createQuery(Route.SELECT_ALL_ROUTES_SQL_QUERY);
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
                return session.createQuery(Route.SELECT_RAILWAY_STATION_SQL_QUERY);

            }

            @Override
            public String getNamedQueryName() {
                return Route.SELECT_ALL_FOR_RAILWAY_STATION;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(Route.class, "r");
                criteria.addOrder(Order.asc("r.number"));

                // Using a Disjunction to perform a logical OR
                // I looked up the disjunction Restriction to perform a logical OR at https://stackoverflow.com/questions/57484/how-do-you-or-criteria-together-when-using-a-criteria-query-with-hibernate
                Disjunction logicalOr = Restrictions.disjunction();

                // The start OR destination stop of the route must equal 9015 OR 9016 to be returned by this query
                Criterion stopIdEqualsStop1 = Property.forName("r.startStop.id").eq(RAILWAY_STATION_STOP_ID_1);
                Criterion stopIdEqualsStop2 = Property.forName("r.startStop.id").eq(RAILWAY_STATION_STOP_ID_2);
                Criterion destinationIdEqualsStop1 = Property.forName("r.destinationStop.id").eq(RAILWAY_STATION_STOP_ID_1);
                Criterion destinationIdEqualsStop2 = Property.forName("r.destinationStop.id").eq(RAILWAY_STATION_STOP_ID_2);

                logicalOr.add(stopIdEqualsStop1);
                logicalOr.add(stopIdEqualsStop2);
                logicalOr.add(destinationIdEqualsStop1);
                logicalOr.add(destinationIdEqualsStop2);

                criteria.add(logicalOr);

                return criteria;
            }
        };
    }

    public static ExampleQuery cumulativeFrequencyByOkTravel() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery(Route.CUMULATIVE_FREQUENCY_SQL_QUERY);

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

                // The route operator must equal 'OK Travel' to be included in this query.
                SimpleExpression operatorNameEquals = Restrictions.eq("o.name", OK_TRAVEL_OPERATOR_NAME);
                criteria.add(operatorNameEquals);

                // This query counts up the frequency per operator for each route that is operated by OK Travel.
                AggregateProjection sumOfOperatorFrequency = Projections.sum("frequencyPerOperator");
                criteria.setProjection(sumOfOperatorFrequency);

                return criteria;
            }
        };
    }


}
