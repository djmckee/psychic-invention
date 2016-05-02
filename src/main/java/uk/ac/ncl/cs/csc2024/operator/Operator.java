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

import uk.ac.ncl.cs.csc2024.route.Route;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Hibernate Operator Entity
 *
 * Task: Create fields, methods and annotations which implicitly define an appropriate database table schema for
 * Operator records.
 *
 * @author hugofirth
 * @author Modified by Dylan McKee
 *
 */

@Entity
@NamedQueries({
        @NamedQuery(name = Operator.SELECT_ALL, query = Operator.SELECT_ALL_HQL_QUERY),
        @NamedQuery(name = Operator.SELECT_ALL_DIAMOND_BUSES_ROUTES, query = Operator.SELECT_DIAMOND_BUSES_ROUTES_HQL_QUERY),
        @NamedQuery(name = Operator.SELECT_ALL_PARK_GATES_OPERATORS, query = Operator.SELECT_ALL_PARK_GATE_OPERATORS_HQL_QUERY)

})

@Table(name = "operator")
public class Operator {

    /**
     * The name of the select all operators query.
     */
    public static final String SELECT_ALL =  "Operator.selectAll";

    /**
     * The name of the 'select all routes operated by diamond buses' query.
     */
    public static final String SELECT_ALL_DIAMOND_BUSES_ROUTES =  "Operator.selectAllRoutesByDiamondBuses";

    /**
     * The name of the 'select all routes that start/end at park gates' query.
     */
    public static final String SELECT_ALL_PARK_GATES_OPERATORS =  "Operator.selectAllForParkGates";

    /**
     * The HQL Query for the 'select all routes that start/end at park gates' query.
     */
    public static final String SELECT_ALL_HQL_QUERY = "select o from Operator o order by o.name asc";

    /**
     * The HQL Query for the 'select all routes operated by diamond buses' query.
     */
    public static final String SELECT_DIAMOND_BUSES_ROUTES_HQL_QUERY = "select r from Route r join r.operators o where o.name='Diamond Buses'";

    /**
     * The HQL Query for the select all operators query.
     */
    public static final String SELECT_ALL_PARK_GATE_OPERATORS_HQL_QUERY = "select o from Operator o join o.routes r where r.startStop.description='Park Gates' or r.destinationStop.description='Park Gates'";

    /**
     * The primary key for the Operator entirety.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    /**
     * The operator name.
     */
    @Column(name = "name")
    private String name;

    /**
     * The operator's street address.
     */
    @Column(name = "street")
    private String street;

    /**
     * The operator's town.
     */
    @Column(name = "town")
    private String town;

    /**
     * The operator's postcode.
     */
    @Column(name = "postcode")
    private String postcode;

    /**
     * The operator's email address.
     */
    @Column(name = "email")
    private String email;

    /**
     * The operator's phone number.
     */
    @Column(name = "phone")
    private String phone;

    /**
     * A list of Route objects operated the operator.
     */
    @ManyToMany(
            targetEntity = Route.class,
            cascade = {CascadeType.PERSIST, CascadeType.PERSIST}
    )
    @JoinTable(
            name = "operator_route",
            joinColumns = @JoinColumn(name = "operator_id"),
            inverseJoinColumns = @JoinColumn(name = "route_id")
    )
    private Set<Route> routes = new HashSet<Route>();

    /**
     * Returns the name of this operator.
     * @return the name of the operator.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this operator.
     * @param name the new name for this operator.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the street address of this operator.
     * @return the street address of the operator.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street address for this operator.
     * @param street the new street address for this operator.
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Returns the town of this operator.
     * @return the town of the operator.
     */
    public String getTown() {
        return town;
    }

    /**
     * Sets the town for this operator.
     * @param town the new town for this operator.
     */
    public void setTown(String town) {
        this.town = town;
    }

    /**
     * Returns the postcode of this operator.
     * @return the name of the operator.
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * Sets the postcode for this operator.
     * @param postcode the postcode for this operator.
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * Returns the email address of this operator.
     * @return the email address of the operator.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address for this operator.
     * @param email the new email address for this operator.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the phone number of this operator.
     * @return the phone number of the operator.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets this Operator's phone number.
     * @param phone the phone number for this operator.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the Set of Routes operated by this operator.
     * @return a set of route instances operated by this operator.
     */
    public Set<Route> getRoutes() {
        return routes;
    }

    /**
     * Sets the Set of routes operated by this operator.
     */
    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

    /**
     * The primary key for the Operator entirety.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns true of object O is equal to this Operator instance.
     * @param o the object to compare this Operator to.
     * @return true iff 'o' is logically equal to this Operator instance.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Operator operator = (Operator) o;

        return getId() == operator.getId();

    }

    /**
     * A hash value representing the unique fields of this Operator instance.
     * @return in integer representing this Operator uniquely.
     */
    @Override
    public int hashCode() {
        return getId();
    }

    /**
     * A human readable representation of the Operator class.
     * @return a String describing this Operator instance.
     */
    @Override
    public String toString() {
        return "Operator{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", town='" + town + '\'' +
                ", postcode='" + postcode + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", routes=" + routes +
                '}';
    }
}
