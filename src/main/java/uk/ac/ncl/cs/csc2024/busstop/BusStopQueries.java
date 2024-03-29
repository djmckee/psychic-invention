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
package uk.ac.ncl.cs.csc2024.busstop;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import uk.ac.ncl.cs.csc2024.query.ExampleQuery;

import java.util.Map;


/**
 * Collection of Queries relating to BusStop entities
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
public class BusStopQueries {

    public static Session insert(Map<String, String> row, Session session) {

        // Create our new bus stop instance
        BusStop busStop = new BusStop();

        // Parse the ID from a String in the map to an integer...
        String idString = row.get("id");
        int busStopId = Integer.parseInt(idString);
        busStop.setId(busStopId);

        // Set description (description is already a String type so no parsing/typecasting is necessary)
        String description = row.get("description");
        busStop.setDescription(description);

        // Persist Bus Stop to database
        session.save(busStop);

        return session;
    }

    public static ExampleQuery selectAll() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery(BusStop.SELECT_ALL_HQL_QUERY);
            }

            @Override
            public String getNamedQueryName() {
                return BusStop.SELECT_ALL;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(BusStop.class, "b");
                Order ascendingOrder = Order.asc("b.id");
                criteria.addOrder(ascendingOrder);
                return criteria;
            }
        };
    }

    public static ExampleQuery selectMaxId() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery(BusStop.SELECT_MAX_ID_HQL_QUERY);
            }

            @Override
            public String getNamedQueryName() {
                return BusStop.SELECT_MAX_ID;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(BusStop.class, "b");

                // Selecting the maximum by sorting the results in descending order by ID number,
                // and then limiting the results set size to 1 - meaning that the results set contains
                // only 1 bus stop - the one with the maximum ID.
                Order descendingOrder = Order.desc("id");
                criteria.addOrder(descendingOrder);

                criteria.setMaxResults(1);

                return criteria;
            }
        };
    }


}
