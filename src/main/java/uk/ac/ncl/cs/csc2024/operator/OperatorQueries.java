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
package uk.ac.ncl.cs.csc2024.operator;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.Query;

import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import uk.ac.ncl.cs.csc2024.busstop.BusStop;
import uk.ac.ncl.cs.csc2024.query.ExampleQuery;
import uk.ac.ncl.cs.csc2024.query.QueryUtilities;
import uk.ac.ncl.cs.csc2024.route.Route;

import java.util.Map;

/**
 * Collection of Queries relating to Operator entities
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
public class OperatorQueries {

    public static Session insert(final Map<String, String> row, Session session) {
        Operator operator = new Operator();

        // Operator fields are all String types so no need to perform any parsing.
        operator.setName(row.get("name"));
        operator.setStreet(row.get("street"));
        operator.setTown(row.get("town"));
        operator.setPostcode(row.get("postcode"));
        operator.setEmail(row.get("email"));
        operator.setPhone(row.get("phone"));

        session.save(operator);

        return session;
    }

    public static ExampleQuery selectAll() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery("select o from Operator o order by o.name asc");
            }

            @Override
            public String getNamedQueryName() {
                return Operator.SELECT_ALL;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(Operator.class, "o");
                criteria.addOrder(Order.asc("o.name"));
                return criteria;
            }
        };
    }

    public static ExampleQuery selectAllRoutesByDiamondBuses() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                // I looked up the use of Hibernate joins at https://stackoverflow.com/questions/3475171/hql-hibernate-query-with-manytomany
                return session.createQuery("select r from Route r join r.operators o where o.name='Diamond Buses'");
            }

            @Override
            public String getNamedQueryName() {
                return Operator.SELECT_ALL_DIAMOND_BUSES_ROUTES;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(Route.class, "r");

                // I looked up the use of createAlias at https://stackoverflow.com/questions/6744941/hibernate-criteria-with-many-to-many-join-table
                criteria.createAlias("r.operators", "o");
                criteria.add(Restrictions.eq("o.name", "Diamond Buses"));


                return criteria;

            }
        };
    }

    public static ExampleQuery selectAllForParkGates() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery("select o from Operator o join o.routes r where r.startStop.description='Park Gates' or r.destinationStop.description='Park Gates'");
            }

            @Override
            public String getNamedQueryName() {
                return Operator.SELECT_ALL_PARK_GATES_OPERATORS;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(Operator.class, "o");

                // I looked up the use of createAlias at https://stackoverflow.com/questions/6744941/hibernate-criteria-with-many-to-many-join-table
                criteria.createAlias("o.routes", "r");

                criteria.createAlias("r.startStop", "startStop");
                criteria.createAlias("r.destinationStop", "destinationStop");

                // I looked up the disjunction Restriction to perform a logical OR at https://stackoverflow.com/questions/57484/how-do-you-or-criteria-together-when-using-a-criteria-query-with-hibernate
                criteria.add(Restrictions.disjunction().add(
                        Restrictions.eq("startStop.description", "Park Gates")
                ).add(
                        Restrictions.eq("destinationStop.description", "Park Gates")
                ));


                return criteria;

            }
        };
    }


}
