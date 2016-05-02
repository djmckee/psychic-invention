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

import org.hibernate.annotations.Formula;
import uk.ac.ncl.cs.csc2024.busstop.BusStop;
import uk.ac.ncl.cs.csc2024.operator.Operator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Hibernate Route Entity
 *
 * Task: Create fields, methods and annotations which implicitly define an appropriate database table schema for
 * Route records.
 *
 * @author hugofirth
 * @author Modified by Dylan McKee
 *
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Route.SELECT_ALL, query = Route.SELECT_ALL_HQL_QUERY),
        @NamedQuery(name = Route.SELECT_ALL_FOR_RAILWAY_STATION, query = Route.SELECT_ALL_RAILWAY_STATION_ROUTES_HQL_QUERY),
        @NamedQuery(name = Route.CUMULATIVE_FREQUENCY_BY_OK_TRAVEL, query = Route.SELECT_CUMULATIVE_FREQUENCY_FOR_OK_TRAVEL_HQL_QUERY)
})

@Table(name = "route")
public class Route {

    /**
     * The name of the 'select all routes' query.
     */
    public static final String SELECT_ALL = "Route.selectAll";

    /**
     * The name of the select all routes that go via the Railway Station query.
     */
    public static final String SELECT_ALL_FOR_RAILWAY_STATION = "Route.selectAllForRailwayStation";

    /**
     * The name of the select cumulative frequency by OK Travel query.
     */
    public static final String CUMULATIVE_FREQUENCY_BY_OK_TRAVEL = "Route.cumulativeFrequencyByOkTravel";

    /**
     * The HQL Query the 'select all routes' query.
     */
    public static final String SELECT_ALL_HQL_QUERY = "select r from Route r order by r.number asc";

    /**
     * The HQL Query for the select all routes that go via the Railway Station query.
     */
    public static final String SELECT_ALL_RAILWAY_STATION_ROUTES_HQL_QUERY = "select distinct r from Route r where r.startStop.id =  9015 OR r.startStop.id = 9016 OR r.destinationStop.id = 9015 OR r.destinationStop.id = 9016";

    /**
     * The HQL Query for the select cumulative frequency by OK Travel query.
     */
    public static final String SELECT_CUMULATIVE_FREQUENCY_FOR_OK_TRAVEL_HQL_QUERY = "select sum(r.frequency * 0.75) from Route r join r.operators o where o.name = 'OK Travel'";

    /**
     * The primary key for the Route entirety.
     *
     * Using a String type for a variable called 'number' seems extremely counter-intuitive, but the fact that
     * '16A' is in the sample data set as a 'route number' leaves me with no choice.
     *
     */
    @Id
    @Column(name = "number")
    // Using a String type for a variable called 'number' seems extremely counter-intuitive, but the fact that
    // '16A' is in the sample data set as a 'route number' leaves me with no choice. Ugh. :-(
    private String number;

    /**
     * The frequency per hour for the given route.
     */
    @Column(name = "frequency")
    private int frequency;


    /**
     * The BusStop that this Route starts at.
     */
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "start_stop_id")
    private BusStop startStop;

    /**
     * The destination BusStop for this Route.
     */
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "destination_stop_id")
    private BusStop destinationStop;

    /**
     * A Set of Operators for the current Route.
     */
    @ManyToMany(
            targetEntity = Operator.class,
            cascade = {CascadeType.REMOVE, CascadeType.REMOVE}
    )
    @JoinTable(
            name = "operator_route",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "operator_id")
    )
    private Set<Operator> operators = new HashSet<Operator>();

    /**
     * A computed property that contains the frequency per hour to be operated by each operator for the current route,
     * based off of the frequency of the route and the number of operators.
     */
    // TODO: fix
    @Formula("frequency * 0.75")
    private double frequencyPerOperator;

    /**
     * Returns the route number for the current Route instance.
     * @return the number of the current Route.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the number for the current route instance.
     * @param routeNumber a String containing the route number.
     */
    public void setNumber(String routeNumber) {
        number = routeNumber;
    }

    /**
     * Returns the frequency for the current route.
     * @return the frequency of the current route.
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency (i.e. the number of times operated per hour) for this Route.
     * @param frequency the frequency for this Route.
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Returns the starting BusStop for this Route.
     * @return the BusStop that this route starts at.
     */
    public BusStop getStartStop() {
        return startStop;
    }

    /**
     * Sets the BusStop that this Route ends at.
     * @param startStop the BusStop that this route finishes at.
     */
    public void setStartStop(BusStop startStop) {
        this.startStop = startStop;
    }

    /**
     * Returns the destination BusStop for this Route.
     * @return the BusStop that this route ends at.
     */
    public BusStop getDestinationStop() {
        return destinationStop;
    }

    /**
     * Sets the BusStop that this Route ends at.
     * @param destinationStop the BusStop that this route finishes at.
     */
    public void setDestinationStop(BusStop destinationStop) {
        this.destinationStop = destinationStop;
    }

    /**
     * Returns a Set containing the Operators that operate this Route.
     * @return a Set containing the Operators that operate this Route.
     */
    public Set<Operator> getOperators() {
        return operators;
    }

    /**
     * Sets the operators that operate this route.
     * @param operators a set containing the operators that operate this route.
     */
    public void setOperators(Set<Operator> operators) {
        this.operators = operators;
    }

    /**
     * Returns the frequency per operator for this route.
     * @return the frequency per operator for this route.
     */
    public double getFrequencyPerOperator() {
        return frequencyPerOperator;
    }

    /**
     * A human readable representation of the Route class.
     * @return a String describing this Route instance.
     */
    @Override
    public String toString() {
        return "Route{" +
                "number='" + number + '\'' +
                ", frequency=" + frequency +
                ", startStop=" + startStop +
                ", destinationStop=" + destinationStop +
                ", operators=" + operators +
                ", frequencyPerOperator=" + frequencyPerOperator +
                '}';
    }

}
