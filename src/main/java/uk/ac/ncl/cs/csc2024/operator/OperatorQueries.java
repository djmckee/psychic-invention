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
 * @author Modified by Dylan McKee
 */
public class OperatorQueries {

    private static final String PARK_GATES_DESCRIPTION = "Park Gates";
    private static final String DIAMOND_BUSES_NAME = "Diamond Buses";

    public static Session insert(final Map<String, String> row, Session session) {
        Operator operator = new Operator();

        // Operator fields are all String types so no need to perform any parsing.
        String name = row.get("name");
        operator.setName(name);

        String street = row.get("street");
        operator.setStreet(street);

        String town = row.get("town");
        operator.setTown(town);

        String postcode = row.get("postcode");
        operator.setPostcode(postcode);

        String email = row.get("email");
        operator.setEmail(email);

        String phone = row.get("phone");
        operator.setPhone(phone);

        session.save(operator);

        return session;

    }

    public static ExampleQuery selectAll() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery(Operator.SELECT_ALL_HQL_QUERY);
            }

            @Override
            public String getNamedQueryName() {
                return Operator.SELECT_ALL;
            }

            @Override
            public Criteria getCriteria(Session session) {
                Criteria criteria = session.createCriteria(Operator.class, "o");
                Order ascendingOrder = Order.asc("o.name");
                criteria.addOrder(ascendingOrder);
                return criteria;
            }
        };
    }

    public static ExampleQuery selectAllRoutesByDiamondBuses() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                // I looked up the use of Hibernate joins at https://stackoverflow.com/questions/3475171/hql-hibernate-query-with-manytomany
                return session.createQuery(Operator.SELECT_DIAMOND_BUSES_ROUTES_HQL_QUERY);
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

                // Name of one of the route operators must equal 'Diamond Buses'
                SimpleExpression nameEqualsConstraint = Restrictions.eq("o.name", DIAMOND_BUSES_NAME);
                criteria.add(nameEqualsConstraint);


                return criteria;

            }
        };
    }

    public static ExampleQuery selectAllForParkGates() {
        return new ExampleQuery() {
            @Override
            public Query getQuery(Session session) {
                return session.createQuery(Operator.SELECT_ALL_PARK_GATE_OPERATORS_HQL_QUERY);
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
                Disjunction logicalOrOperation = Restrictions.disjunction();

                // Either the start stop or destination stop's name must equal 'Park Gates'
                SimpleExpression startStopNameConstraint = Restrictions.eq("startStop.description", PARK_GATES_DESCRIPTION);
                SimpleExpression destinationStopNameConstraint = Restrictions.eq("destinationStop.description", PARK_GATES_DESCRIPTION);

                logicalOrOperation.add(startStopNameConstraint);
                logicalOrOperation.add(destinationStopNameConstraint);

                criteria.add(logicalOrOperation);


                return criteria;

            }
        };
    }


}
