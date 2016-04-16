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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
        @NamedQuery(name = Operator.SELECT_ALL, query = "select o from Operator o order by o.name asc"),
        @NamedQuery(name = Operator.SELECT_ALL_DIAMOND_BUSES_ROUTES, query = "select o from Operator o order by o.name asc"),
        @NamedQuery(name = Operator.SELECT_ALL_PARK_GATES_OPERATORS, query =  "select o from Operator o order by o.name asc")

})

@Table(name = "operator")
public class Operator {

    public static final String SELECT_ALL =  "Operator.selectAll";
    public static final String SELECT_ALL_DIAMOND_BUSES_ROUTES =  "Operator.selectAllRoutesByDiamondBuses";
    public static final String SELECT_ALL_PARK_GATES_OPERATORS =  "Operator.selectAllForParkGates";

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "street")
    private String street;

    @Column(name = "town")
    private String town;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "email")

    private String email;


    @Column(name = "phone")
    private String phone;

    @ManyToMany(
            targetEntity=Route.class,
            cascade={CascadeType.PERSIST, CascadeType.PERSIST}
    )
    @JoinTable(
            name="operator_route",
            joinColumns=@JoinColumn(name="operator_id"),
            inverseJoinColumns=@JoinColumn(name="route_id")
    )
    private Set<Route> routes = new HashSet<Route>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operator operator = (Operator) o;

        return getId() == operator.getId();

    }

    @Override
    public int hashCode() {
        return getId();
    }


}